package info.skyblond.win32tape

import java.io.InputStream
import java.lang.foreign.Arena
import java.lang.foreign.ValueLayout
import kotlin.math.max

/**
 * Input stream from tape. Will produce EOF once met FILEMARK on tape.
 *
 * Start reading from the current position.
 *
 * Not thread safe.
 * */
class BufferedTapeInputStream(
    private val device: TapeDrive,
    bufferSize: Int = 128 * 1024 * 1024,
    startPos: Pair<Int, Long> = device.getTapeLogicalPosition()
) : InputStream() {
    private var buffer = ByteArray(bufferSize)
    private val bufferNative = Arena.ofAuto().allocate(bufferSize.toLong())
    private var bufferOffset = 0
    private var bufferLimit = 0
    private var lastTapePartition = startPos.first
    private var lastTapePosition = startPos.second

    private fun reload() {
        if (bufferLimit < 0) return // already EOF
        // move to last read block
        val (cPartition, cPosition) = device.getTapeLogicalPosition()
        if (cPartition != lastTapePartition || cPosition != lastTapePosition) {
            device.seekTapeLogicalPosition(lastTapePartition, lastTapePosition)
        }
        bufferLimit = try { // fetch data
            device.readTape(bufferNative)
        } catch (e: Win32RuntimeException) {
            if (e.code == 1101) {
                -1 // if met file mark, EOF
            } else throw e
        }
        device.getTapeLogicalPosition().let {
            lastTapePartition = it.first
            lastTapePosition = it.second
        }
        // TODO: here involves a mem copy, performance hit?
        buffer = bufferNative.toArray(ValueLayout.JAVA_BYTE)
        bufferOffset = 0
    }

    override fun read(): Int {
        if (bufferOffset >= bufferLimit) reload() // try reload if needed
        if (bufferLimit < 0) return -1 // already EOF

        return buffer[bufferOffset++].toInt() and 0xFF // convert to unsigned int
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        if (bufferOffset >= bufferLimit) reload() // try reload if needed
        if (bufferLimit < 0) return -1 // already EOF
        var writeCounter = 0

        while (writeCounter < len) {
            // bytes need to copy VS bytes we have
            val canCopyCount = minOf(len - writeCounter, bufferLimit - bufferOffset)
            if (canCopyCount == 0) {
                // nothing left, reload and try on next loop
                reload()
                if (bufferLimit < 0) return writeCounter // EOF
            } else {
                // copy as much as we can
                buffer.copyInto(b, off + writeCounter, bufferOffset, bufferOffset + canCopyCount)
                bufferOffset += canCopyCount
                writeCounter += canCopyCount
                // fetch new data on next loop
            }
        }
        return writeCounter
    }

    override fun available(): Int {
        if (bufferLimit < 0) return 0; // already EOF
        // clap the remain to 0
        return max(0, bufferLimit - bufferOffset)
    }
}
