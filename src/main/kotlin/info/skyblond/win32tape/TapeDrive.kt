package info.skyblond.win32tape

import jextract.win32tape.Win32Tape
import java.io.File
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

/**
 * A class representing a tape drive device.
 * */
class TapeDrive(
    private val device: MemorySegment
) : AutoCloseable {
    companion object {

        // write dll and load it
        init {
            val userHome = System.getProperty("user.home")
            val ddlFile = if (userHome != null) {
                val folder = File("${userHome}/.win32tape")
                folder.mkdirs()
                File(folder, "win32tape.dll")
            } else {
                File.createTempFile("win32tape", ".dll").also { it.deleteOnExit() }
            }
            this::class.java.getResourceAsStream("/win32tape.dll")!!.use { dll ->
                ddlFile.outputStream().use { local ->
                    dll.copyTo(local, 4096)
                }
            }
            System.load(ddlFile.canonicalPath)
        }

        /**
         * Open a device using the given deviceName.
         *
         * @param deviceName Normally it's `\\.\TAPEn`, where `n` start from 0.
         * See https://learn.microsoft.com/en-us/windows/win32/api/fileapi/nf-fileapi-createfilea
         * @returns the handle of the device.
         * @throws Win32RuntimeException if the operation failed.
         * */
        fun openDevice(deviceName: String): MemorySegment = Arena.ofConfined().use { arena ->
            val name = arena.allocateUtf8String(deviceName)
            val ret = arena.allocate(Win32Tape.Ret)
            val result = Win32Tape.openDevice(name, ret)
            val retCode = ret.get(Win32Tape.Ret, 0)
            if (retCode != 0) {
                throw Win32RuntimeException("openDevice", retCode)
            }
            return result
        }


        /**
         * Close the device and release resources.
         * All opened devices must be closed by calling this function.
         * */
        fun closeDevice(device: MemorySegment) = Win32Tape.closeDevice(device)
    }

    constructor(deviceName: String) : this(openDevice(deviceName))

    /**
     * Get the info/parameters of both tape drive and media.
     * Calls GetTapeParameters two times. First get drive params,
     * then get media. If there is no media, the media part is leave untouched.
     *
     * @returns [TapeInfo]
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun getTapeInfo(): TapeInfo = Arena.ofConfined().use { arena ->
        val tapeInfoNative = arena.allocate(jextract.win32tape.TapeInfo.`$LAYOUT`())
        Win32Tape.getTapeInfo(device, tapeInfoNative).let {
            // NO_ERROR or ERROR_NO_MEDIA_IN_DRIVE is fine
            if (it != 0 && it != 1112) throw Win32RuntimeException("getTapeInfo", it)
        }

        return TapeInfo.fromMemorySegment(tapeInfoNative)
    }

    /**
     * Get the status of tape drive.
     * See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-gettapestatus
     *
     * @returns [DeviceStatus]
     * @throws Win32RuntimeException if the unexpected status code returned.
     * */
    fun getDeviceStatus(): DeviceStatus =
        Win32Tape.getDeviceStatus(device).let { code ->
            DeviceStatus.entries.find { it.code == code }
                ?: throw Win32RuntimeException("getDeviceStatus", code)
        }

    /**
     * Load a tape. Return once the tape is loaded.
     * See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-preparetape
     *
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun loadTape() = Win32Tape.loadTape(device).let {
        if (it != 0) throw Win32RuntimeException("loadTape", it)
    }

    /**
     * Unload a tape. Return once the tape is unloaded.
     * See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-preparetape
     *
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun unloadTape() = Win32Tape.unloadTape(device).let {
        if (it != 0) throw Win32RuntimeException("unloadTape", it)
    }

    /**
     * Lock a tape. Return once the tape is locked.
     * See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-preparetape
     *
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun lockTape() = Win32Tape.lockTape(device).let {
        if (it != 0) throw Win32RuntimeException("lockTape", it)
    }

    /**
     * Unload a tape. Return once the tape is unlocked.
     * See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-preparetape
     *
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun unlockTape() = Win32Tape.unlockTape(device).let {
        if (it != 0) throw Win32RuntimeException("unlockTape", it)
    }

    /**
     * Rewind to the beginning of the tape.
     *
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun rewindTape() = Win32Tape.rewindTape(device).let {
        if (it != 0) throw Win32RuntimeException("rewindTape", it)
    }

    /**
     * Erase all or part of the tape.
     * <br/>
     * Fast erase write a erase gap or End Of Data marker at current position.
     * This will logically make the data accessible (unless using write anywhere mode).
     * <br/>
     * Full erase will literally erase the tape from the current location to the
     * end of the current partition.
     *
     * @param fast ture for fast erase; false for slower but full erase.
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun eraseTape(fast: Boolean) = Win32Tape.eraseTape(device, fast).let {
        if (it != 0) throw Win32RuntimeException("eraseTape", it)
    }

    /**
     * Set drive compression.
     *
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun setTapeDriveCompression(compression: Boolean) =
        Win32Tape.setTapeDriveCompression(device, compression).let {
            if (it != 0) throw Win32RuntimeException("setTapeDriveCompression", it)
        }

    /**
     * Get the device-specific block address.
     *
     * @returns offset/address/block number, whatever you call it.
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun getTapeAbsolutePosition(): Long = Arena.ofConfined().use { arena ->
        val offset = arena.allocate(ValueLayout.JAVA_LONG)
        Win32Tape.getTapeAbsolutePosition(device, offset).let {
            if (it != 0) throw Win32RuntimeException("getTapeAbsolutePosition", it)
        }
        return offset.get(ValueLayout.JAVA_LONG, 0)
    }

    /**
     * Seek to a given device-specific block address.
     *
     * @param offset the address/offset/block number, must not be negative.
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun seekTapeAbsolutePosition(offset: Long) =
        Win32Tape.seekTapeAbsolutePosition(device, offset).let {
            if (it != 0) throw Win32RuntimeException("seekTapeAbsolutePosition", it)
        }

    /**
     * Get the logical block address. The offset is relative to the partition.
     *
     * @returns Pair of the partition number (start from 1) and the relative offset (start from 0).
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun getTapeLogicalPosition(): Pair<Int, Long> = Arena.ofConfined().use { arena ->
        val partition = arena.allocate(ValueLayout.JAVA_INT)
        val offset = arena.allocate(ValueLayout.JAVA_LONG)
        Win32Tape.getTapeLogicalPosition(device, partition, offset).let {
            if (it != 0) throw Win32RuntimeException("getTapeLogicalPosition", it)
        }
        return partition.get(ValueLayout.JAVA_INT, 0) to offset.get(ValueLayout.JAVA_LONG, 0)
    }

    /**
     * Seek to a given logical block address. The offset is relative to the partition.
     *
     * @param partition the partition, start from 1.
     * @param offset the address/offset/block number, which must not be negative.
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun seekTapeLogicalPosition(partition: Int, offset: Long) =
        Win32Tape.seekTapeLogicalPosition(device, partition, offset).let {
            if (it != 0) throw Win32RuntimeException("seekTapeLogicalPosition", it)
        }

    /**
     * Move the tape forward (or backward) the N file marks from current position.
     * <br/>
     * Forward: stop at the end of the last file marks, you can write from there
     * if it's a long file mark.
     * <br/>
     * Backward: stop at the beginning of the last file mark, you need to skip it
     * before you can write, otherwise you will overwrite the mark.
     *
     * @param count positive number to forward, negative number to backward.
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun seekFileMarks(count: Long) =
        Win32Tape.seekFileMarks(device, count).let {
            if (it != 0) throw Win32RuntimeException("seekFileMarks", it)
        }

    /**
     * Move the tape to the End Of Data of the given partition.
     *
     * @param partition start from 1.
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun seekToEOD(partition: Int) =
        Win32Tape.seekToEOD(device, partition).let {
            if (it != 0) throw Win32RuntimeException("seekToEOD", it)
        }

    /**
     * Read from tape.
     * <br/>
     * The data is read in complete blocks. If the tape's block size is 512 bytes,
     * all read operations must use buffers that are simple integer multiples of
     * that block size: 512, 1024, 1536, 2048, and so on.
     * <br/>
     * If readTape encounters a file mark, the data up to the file mark is read
     * and the function fails with an returned error code indicating the type of
     * file mark that was encountered (ERROR_FILEMARK_DETECTED 1101 or ERROR_SETMARK_DETECTED 1103).
     * <br/>
     * The operating system moves the tape past the file mark, and an application
     * can call readTape again to continue reading next file.
     * <br/>
     * If no more data can be read, the return code will be ERROR_NO_DATA_DETECTED 1104.
     * <br/>
     * ERROR_MORE_DATA 234 means your buffer is too small.
     *
     * @param buffer the bytes from tape will be loaded to this buffer
     * @param size how many bytes should be read, normally the size of buffer.
     * @returns the actual bytes read.
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun readTape(buffer: MemorySegment, size: Int = buffer.byteSize().toInt()): Int =
        Arena.ofConfined().use { arena ->
            val readCount = arena.allocate(ValueLayout.JAVA_INT)
            Win32Tape.readTape(device, buffer, size, readCount).let {
                if (it != 0) throw Win32RuntimeException("readTape", it)
            }
            return readCount.get(ValueLayout.JAVA_INT, 0)
        }

    /**
     * Write to tape.
     * <br/>
     * If writeTape encounters a non-overwrite-able file mark, the function fails
     * with an returned error code indicating the type of file mark that was
     * encountered (ERROR_FILEMARK_DETECTED 1101 or ERROR_SETMARK_DETECTED 1103).
     * <br/>
     * You have to manually write a file mark to properly finish the data/file.
     *
     * @param buffer the bytes to be written
     * @param size how many bytes should be written.
     * @returns the actual bytes write.
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun writeTape(buffer: MemorySegment, size: Int = buffer.byteSize().toInt()): Int =
        Arena.ofConfined().use { arena ->
            val writeCount = arena.allocate(ValueLayout.JAVA_INT)
            Win32Tape.writeTape(device, buffer, size, writeCount).let {
                if (it != 0) throw Win32RuntimeException("writeTape", it)
            }
            return writeCount.get(ValueLayout.JAVA_INT, 0)
        }

    /**
     * Write N normal file marks at current position and move to next block.
     * This is the most general marks used on tapes.
     *
     * @param count the number of file marks to write.
     * @throws Win32RuntimeException if the operation failed.
     * */
    fun writeFileMarks(count: Int) =
        Win32Tape.writeFileMarks(device, count).let {
            if (it != 0) throw Win32RuntimeException("writeFileMarks", it)
        }

    override fun close() {
        closeDevice(device)
    }
}
