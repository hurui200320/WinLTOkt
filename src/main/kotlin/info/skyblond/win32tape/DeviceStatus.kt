package info.skyblond.info.skyblond.win32tape

enum class DeviceStatus(val code: Int) {
    /**
     * The Device is ready to process command
     * */
    NO_ERROR(0),

    /**
     * An attempt to access data before the beginning-of-medium marker failed.
     * */
    ERROR_BEGINNING_OF_MEDIA(1102),

    /**
     * A reset condition was detected on the bus.
     * */
    ERROR_BUSY(1111),

    /**
     * The partition information could not be found when a tape was being loaded.
     */
    ERROR_DEVICE_NOT_PARTITIONED(1107),

    /**
     * The tape drive is capable of reporting that it requires cleaning, and reports that it does require cleaning.
     */
    ERROR_DEVICE_REQUIRES_CLEANING(1165),

    /**
     * The end-of-tape marker was reached during an operation.
     */
    ERROR_END_OF_MEDIA(1100),

    /**
     * A filemark was reached during an operation.
     */
    ERROR_FILEMARK_DETECTED(1101),

    /**
     * The block size is incorrect on a new tape in a multivolume partition.
     */
    ERROR_INVALID_BLOCK_LENGTH(1106),

    /**
     * The tape that was in the drive has been replaced or removed.
     */
    ERROR_MEDIA_CHANGED(1110),

    /**
     * The end-of-data marker was reached during an operation.
     */
    ERROR_NO_DATA_DETECTED(1104),

    /**
     * There is no media in the drive.
     * */
    ERROR_NO_MEDIA_IN_DRIVE(1112),

    /**
     * The tape driver does not support a requested function.
     * */
    ERROR_NOT_SUPPORTED(50),

    /**
     * The tape could not be partitioned.
     * */
    ERROR_PARTITION_FAILURE(1105),

    /**
     * A setmark was reached during an operation.
     * */
    ERROR_SETMARK_DETECTED(1103),

    /**
     * An attempt to write data to a write-once device failed.
     */
    ERROR_UNABLE_TO_LOCK_MEDIA(1108),


    /**
     * An attempt to unload the tape failed.
     */
    ERROR_UNABLE_TO_UNLOAD_MEDIA(1109),

    /**
     * The media is write protected.
     * */
    ERROR_WRITE_PROTECT(19)
}
