#include "win32tape.h"

#include <windows.h>
#include <iostream>

Device openDevice(const char *deviceName, pRet err) {
    auto tape = CreateFileA(
            deviceName,
            GENERIC_ALL,
            0, // we have exclusive usage with the device
            NULL,
            OPEN_EXISTING,
            FILE_ATTRIBUTE_NORMAL,
            NULL
    );
    if (tape == INVALID_HANDLE_VALUE) {
        *err = GetLastError();
        return NULL;
    }
    *err = 0;
    return tape;
}


void closeDevice(void *handle) {
    CloseHandle(handle);
}

DWORD getDeviceStatus(HANDLE handle) {
    return GetTapeStatus(handle);
}

DWORD getTapeDriveParameter(HANDLE device, TapeInfo *result) {
    TAPE_GET_DRIVE_PARAMETERS p;
    DWORD size = sizeof(TAPE_GET_DRIVE_PARAMETERS);
    DWORD ret = GetTapeParameters(
            device, GET_TAPE_DRIVE_INFORMATION,
            &size, &p
    );
    if (ret != NO_ERROR) return ret;

    result->driveECC = p.ECC;
    result->driveCompression = p.Compression;
    result->driveDataPadding = p.DataPadding;
    result->driveReportSetmarks = p.ReportSetmarks;
    result->driveDefaultBlockSize = p.DefaultBlockSize;
    result->driveMaximumBlockSize = p.MaximumBlockSize;
    result->driveMinimumBlockSize = p.MinimumBlockSize;
    result->driveMaximumPartitionCount = p.MaximumPartitionCount;
    result->driveFeaturesLow = p.FeaturesLow;
    result->driveFeaturesHigh = p.FeaturesHigh;
    result->driveEOTWarningZoneSize = p.EOTWarningZoneSize;

    return NO_ERROR;
}

DWORD getTapeMediaParameter(HANDLE device, TapeInfo *result) {
    TAPE_GET_MEDIA_PARAMETERS p;
    DWORD size = sizeof(TAPE_GET_MEDIA_PARAMETERS);
    DWORD ret = GetTapeParameters(
            device, GET_TAPE_MEDIA_INFORMATION,
            &size, &p
    );
    if (ret != NO_ERROR) return ret;

    result->mediaCapacity = p.Capacity.QuadPart;
    result->mediaRemaining = p.Remaining.QuadPart;
    result->mediaBlockSize = p.BlockSize;
    result->mediaPartitionCount = p.PartitionCount;
    result->mediaWriteProtected = p.WriteProtected;

    return NO_ERROR;
}

DWORD getTapeInfo(HANDLE handle, TapeInfo *result) {
    auto ret = getTapeDriveParameter(handle, result);
    if (ret != NO_ERROR) return ret;
    return getTapeMediaParameter(handle, result);
}

DWORD loadTape(HANDLE handle) {
    return PrepareTape(handle, TAPE_LOAD, FALSE);
}

DWORD unloadTape(HANDLE handle) {
    return PrepareTape(handle, TAPE_UNLOAD, FALSE);
}

DWORD lockTape(HANDLE handle) {
    return PrepareTape(handle, TAPE_LOCK, FALSE);
}

DWORD unlockTape(HANDLE handle) {
    return PrepareTape(handle, TAPE_UNLOCK, FALSE);
}

DWORD setTapeDriveParameters(
        HANDLE handle,
        bool ecc,
        bool compression,
        bool dataPadding,
        bool reportSetmarks,
        unsigned long eotWarningZoneSize
) {
    TAPE_SET_DRIVE_PARAMETERS p = {
            .ECC = ecc,
            .Compression = compression,
            .DataPadding = dataPadding,
            .ReportSetmarks = reportSetmarks,
            .EOTWarningZoneSize = eotWarningZoneSize
    };
    return SetTapeParameters(handle, SET_TAPE_DRIVE_INFORMATION, &p);
}

DWORD setTapeMediaParameters(
        HANDLE handle,
        unsigned long blockSize
) {
    TAPE_SET_MEDIA_PARAMETERS p = {
            .BlockSize = blockSize
    };
    return SetTapeParameters(handle, SET_TAPE_MEDIA_INFORMATION, &p);
}

DWORD setTapeDriveCompression(
        HANDLE handle,
        bool compression
) {
    TapeInfo tapeInfo;
    memset(&tapeInfo, 0, sizeof(TapeInfo));
    auto ret = getTapeInfo(handle, &tapeInfo);
    // has error and not media not found
    if (ret != NO_ERROR && ret != ERROR_NO_MEDIA_IN_DRIVE) {
        return ret;
    }
    return setTapeDriveParameters(
            handle,
            tapeInfo.driveECC,
            compression,
            tapeInfo.driveDataPadding,
            tapeInfo.driveReportSetmarks,
            tapeInfo.driveEOTWarningZoneSize
    );
}

DWORD getTapeAbsolutePosition(
        HANDLE handle,
        unsigned long long *offset
) {
    DWORD partition = 0;
    DWORD offsetLow = 0;
    DWORD offsetHigh = 0;
    auto ret = GetTapePosition(
            handle, TAPE_ABSOLUTE_POSITION,
            &partition, &offsetLow, &offsetHigh
    );
    if (ret == NO_ERROR)
        *offset = ((long long) offsetHigh) << 32 | offsetLow;
    return ret;
}

DWORD seekTapeAbsolutePosition(
        HANDLE handle,
        unsigned long long offset
) {
    return SetTapePosition(
            handle, TAPE_ABSOLUTE_BLOCK,
            0,
            offset & 0xFFFFFFFF,
            (offset >> 32) & 0xFFFFFFFF,
            FALSE
    );
}

DWORD getTapeLogicalPosition(
        HANDLE handle,
        unsigned long *partition,
        unsigned long long *offset
) {
    DWORD offsetLow = 0;
    DWORD offsetHigh = 0;
    auto ret = GetTapePosition(
            handle, TAPE_LOGICAL_POSITION,
            partition, &offsetLow, &offsetHigh
    );
    if (ret == NO_ERROR)
        *offset = ((long long) offsetHigh) << 32 | offsetLow;
    return ret;
}

DWORD seekTapeLogicalPosition(
        HANDLE handle,
        unsigned long partition,
        unsigned long long offset
) {
    return SetTapePosition(
            handle, TAPE_LOGICAL_BLOCK,
            partition,
            offset & 0xFFFFFFFF,
            (offset >> 32) & 0xFFFFFFFF,
            FALSE
    );
}

DWORD rewindTape(HANDLE handle) {
    return SetTapePosition(
            handle, TAPE_REWIND,
            0, 0, 0,
            FALSE
    );
}

DWORD seekToEOD(HANDLE handle, unsigned long partition) {
    return SetTapePosition(
            handle, TAPE_SPACE_END_OF_DATA,
            partition, 0, 0,
            FALSE
    );
}

DWORD seekFileMarks(HANDLE handle, long long count) {
    return SetTapePosition(
            handle, TAPE_SPACE_FILEMARKS,
            0,
            count & 0xFFFFFFFF,
            (count >> 32) & 0xFFFFFFFF,
            FALSE
    );
}

DWORD seekSetMarks(HANDLE handle, long long count) {
    return SetTapePosition(
            handle, TAPE_SPACE_SETMARKS,
            0,
            count & 0xFFFFFFFF,
            (count >> 32) & 0xFFFFFFFF,
            FALSE
    );
}

DWORD writeFileMarks(HANDLE handle, unsigned long count) {
    return WriteTapemark(
            handle,
            TAPE_FILEMARKS,
            count,
            FALSE
    );
}

DWORD writeShortFileMarks(HANDLE handle, unsigned long count) {
    return WriteTapemark(
            handle,
            TAPE_SHORT_FILEMARKS,
            count,
            FALSE
    );
}

DWORD writeLongFileMarks(HANDLE handle, unsigned long count) {
    return WriteTapemark(
            handle,
            TAPE_LONG_FILEMARKS,
            count,
            FALSE
    );
}

DWORD writeSetMarks(HANDLE handle, unsigned long count) {
    return WriteTapemark(
            handle,
            TAPE_SETMARKS,
            count,
            FALSE
    );
}

DWORD eraseTape(HANDLE handle, bool fast) {
    return EraseTape(
            handle,
            fast ? TAPE_ERASE_SHORT : TAPE_ERASE_LONG,
            FALSE
    );
}

DWORD readTape(
        HANDLE handle,
        void *buffer,
        unsigned long nBytesToRead,
        unsigned long *readBytesCount
) {
    auto ret = ReadFile(handle, buffer, nBytesToRead, readBytesCount, NULL);
    if (ret) {
        return NO_ERROR;
    } else {
        return GetLastError();
    }
}

DWORD writeTape(
        HANDLE handle,
        void *buffer,
        unsigned long nBytesToWrite,
        unsigned long *writeBytesCount
) {
    auto ret = WriteFile(handle, buffer, nBytesToWrite, writeBytesCount, NULL);
    if (ret) {
        return NO_ERROR;
    } else {
        return GetLastError();
    }
}
