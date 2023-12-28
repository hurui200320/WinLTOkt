package info.skyblond.info.skyblond.win32tape

import java.lang.foreign.MemorySegment


data class TapeInfo(
    val driveECC: Boolean,
    val driveCompression: Boolean,
    val driveDataPadding: Boolean,
    val driveReportSetmarks: Boolean,
    val driveDefaultBlockSize: Int,
    val driveMaximumBlockSize: Int,
    val driveMinimumBlockSize: Int,
    val driveMaximumPartitionCount: Int,
    val driveFeaturesLow: Int,
    val driveFeaturesHigh: Int,
    val driveEOTWarningZoneSize: Int,
    val mediaCapacity: Long,
    val mediaRemaining: Long,
    val mediaBlockSize: Int,
    val mediaPartitionCount: Int,
    val mediaWriteProtected: Boolean
) {
    companion object {
        fun fromMemorySegment(tapeInfo: MemorySegment): TapeInfo = TapeInfo(
            driveECC = jextract.win32tape.TapeInfo.`driveECC$get`(tapeInfo),
            driveCompression = jextract.win32tape.TapeInfo.`driveCompression$get`(tapeInfo),
            driveDataPadding = jextract.win32tape.TapeInfo.`driveDataPadding$get`(tapeInfo),
            driveReportSetmarks = jextract.win32tape.TapeInfo.`driveReportSetmarks$get`(tapeInfo),
            driveDefaultBlockSize = jextract.win32tape.TapeInfo.`driveDefaultBlockSize$get`(tapeInfo),
            driveMaximumBlockSize = jextract.win32tape.TapeInfo.`driveMaximumBlockSize$get`(tapeInfo),
            driveMinimumBlockSize = jextract.win32tape.TapeInfo.`driveMinimumBlockSize$get`(tapeInfo),
            driveMaximumPartitionCount = jextract.win32tape.TapeInfo.`driveMaximumPartitionCount$get`(tapeInfo),
            driveFeaturesLow = jextract.win32tape.TapeInfo.`driveFeaturesLow$get`(tapeInfo),
            driveFeaturesHigh = jextract.win32tape.TapeInfo.`driveFeaturesHigh$get`(tapeInfo),
            driveEOTWarningZoneSize = jextract.win32tape.TapeInfo.`driveEOTWarningZoneSize$get`(tapeInfo),
            mediaCapacity = jextract.win32tape.TapeInfo.`mediaCapacity$get`(tapeInfo),
            mediaRemaining = jextract.win32tape.TapeInfo.`mediaRemaining$get`(tapeInfo),
            mediaBlockSize = jextract.win32tape.TapeInfo.`mediaBlockSize$get`(tapeInfo),
            mediaPartitionCount = jextract.win32tape.TapeInfo.`mediaPartitionCount$get`(tapeInfo),
            mediaWriteProtected = jextract.win32tape.TapeInfo.`mediaWriteProtected$get`(tapeInfo)
        )
    }
}
