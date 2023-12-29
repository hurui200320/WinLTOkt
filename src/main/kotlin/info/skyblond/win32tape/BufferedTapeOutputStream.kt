package info.skyblond.win32tape

import java.io.OutputStream
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class BufferedTapeOutputStream(
    private val device: TapeDrive,
    bufferSize: Int = 128 * 1024 * 1024,
    startPos: Pair<Int, Long> = device.getTapeLogicalPosition()
) : OutputStream() {
    private val buffer = ByteArray(bufferSize)
    private val bufferHeap = MemorySegment.ofArray(buffer)
    private val bufferNative = Arena.ofAuto().allocate(bufferSize.toLong())
    private var bufferOffset = 0
    private var lastTapePartition = startPos.first
    private var lastTapePosition = startPos.second

    private fun pushDataToDrive() {
        if (bufferOffset <= 0) return // nothing to do
        // copy from heap to native, TODO: performance hit?
        bufferNative.copyFrom(bufferHeap)
        // move to last read block
        val (cPartition, cPosition) = device.getTapeLogicalPosition()
        if (cPartition != lastTapePartition || cPosition != lastTapePosition) {
            device.seekTapeLogicalPosition(lastTapePartition, lastTapePosition)
        }
        // write data
        device.writeTape(bufferNative, bufferOffset)
        // update pos
        device.getTapeLogicalPosition().let {
            lastTapePartition = it.first
            lastTapePosition = it.second
        }
        // reset offset
        bufferOffset = 0
    }

    override fun write(b: Int) {
        if (bufferOffset >= buffer.size) pushDataToDrive()
        buffer[bufferOffset++] = b.toByte()
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        var counter = 0

        while (counter < len) {
            // bytes need to copy VS space we left
            val canCopyCount = minOf(len - counter, buffer.size - bufferOffset)
            if (canCopyCount == 0) {
                // no space left, flush and retry on the next loop
                pushDataToDrive()
            } else {
                // copy as much as we can
                b.copyInto(buffer, bufferOffset, off + counter, off + counter + canCopyCount)
                bufferOffset += canCopyCount
                counter += canCopyCount
                // fetch new data on next loop
            }
        }
    }

    override fun flush() {
        pushDataToDrive()
    }

    override fun close() {
        flush() // flush remain data
        // finish the file
        device.writeFileMarks(1)
    }
}
