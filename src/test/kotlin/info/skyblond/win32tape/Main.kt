package info.skyblond.win32tape

import info.skyblond.info.skyblond.win32tape.BufferedTapeInputStream
import info.skyblond.info.skyblond.win32tape.BufferedTapeOutputStream
import info.skyblond.info.skyblond.win32tape.TapeDrive
import kotlin.random.Random
import kotlin.time.measureTime

// jextract -t jextract.win32tape --header-class-name Win32Tape --output ..\src\main\java\ --source '@includes.txt' .\win32tape.h
object Main {

    // 1GB * 8
    private const val DATA_SIZE = 1 * 1024 * 1024 * 1024
    private const val REPEAT_N = 8

    @JvmStatic
    fun main(args: Array<String>) {
        System.loadLibrary("win32tape")
        val device = TapeDrive("\\\\.\\TAPE0")
        device.setTapeDriveCompression(false)
        device.getTapeInfo().also { println(it) }
        device.getDeviceStatus().also { println(it) }

        println("Rewinding...")
        device.rewindTape()


        val data = Random.nextBytes(DATA_SIZE)
        device.seekTapeLogicalPosition(1, 0)
        device.getTapeLogicalPosition().also { println(it) }
        println("Start writing...")
        val writeTiming = measureTime {
            BufferedTapeOutputStream(device, startPos = 1 to 0)
                .use { s -> repeat(REPEAT_N) { s.write(data) } }
        }
        device.getTapeLogicalPosition().also { println(it) }

        println("Rewinding...")
        device.rewindTape()
        device.seekTapeLogicalPosition( 1, 0)
        val data2 = Random.nextBytes(DATA_SIZE)
        println("Start reading...")
        val readTiming = measureTime {
            BufferedTapeInputStream(device, startPos = 1 to 0).use { s ->
                repeat(REPEAT_N) { s.read(data2) }
                check(s.read() == -1)
            }
        }
        device.getTapeLogicalPosition().also { println(it) }
        println("Write time: ${DATA_SIZE.toLong() * REPEAT_N / writeTiming.inWholeSeconds}B/s")
        println("Read time: ${DATA_SIZE.toLong() * REPEAT_N / readTiming.inWholeSeconds}B/s")

        check(data.contentEquals(data2))

        device.close()
    }

}
