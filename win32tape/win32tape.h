#ifndef WIN32TAPE_H
#define WIN32TAPE_H

// if not in cpp, include stdbool,
// so we can use bool and jextract know it.
#ifdef __cplusplus
// let cpp compiler knows this is C header, otherwise the JVM can't use dll
// CPP by default will change symbol name from `openDevice` to `?openDevice@@YAPEAXPEBDPEAK@Z`
// this will disable the renaming
extern "C" {
#else
#include <stdbool.h>
#endif

typedef unsigned long Ret, *pRet;
typedef void *Device;

typedef struct {
    // TAPE_GET_DRIVE_PARAMETERS
    bool driveECC;
    bool driveCompression;
    bool driveDataPadding;
    bool driveReportSetmarks;
    unsigned long driveDefaultBlockSize;
    unsigned long driveMaximumBlockSize;
    unsigned long driveMinimumBlockSize;
    unsigned long driveMaximumPartitionCount;
    unsigned long driveFeaturesLow;
    unsigned long driveFeaturesHigh;
    unsigned long driveEOTWarningZoneSize;
    // TAPE_GET_MEDIA_PARAMETERS
    long long mediaCapacity;
    long long mediaRemaining;
    unsigned long mediaBlockSize;
    unsigned long mediaPartitionCount;
    bool mediaWriteProtected;
} TapeInfo;

/**
 * Open a device using the given deviceName.
 *
 * @param deviceName Normally it's `\\\\.\\TAPEn`, where `n` start from 0. See https://learn.microsoft.com/en-us/windows/win32/api/fileapi/nf-fileapi-createfilea
 * @param err The pointer to a DWORD, representing the error code when calling CreateFileA. 0 means ok.
 * @returns the handle of the device. Return NULL if error.
 * */
__declspec(dllexport) Device openDevice(const char *deviceName, pRet err);

/**
 * Close the device and release resources.
 * All opened devices must be closed by calling this function.
 * */
__declspec(dllexport) void closeDevice(Device handle);

/**
 * Get the status of tape drive.
 * See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-gettapestatus
 *
 * @returns 0 if drive is ok.
 * */
__declspec(dllexport) Ret getDeviceStatus(Device handle);

/**
 * Get the info/parameters of both tape drive and media.
 * Calls GetTapeParameters two times. First get drive params,
 * then get media. If there is no media, the media part is leave untouched.
 *
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret getTapeInfo(Device handle, TapeInfo *result);

/**
 * Load a tape. Return once the tape is loaded. See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-preparetape
 * */
__declspec(dllexport) Ret loadTape(Device handle);

/**
 * Unload a tape. Return once the tape is unloaded. See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-preparetape
 * */
__declspec(dllexport) Ret unloadTape(Device handle);

/**
 * Lock a tape. Return once the tape is lock. See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-preparetape
 * */
__declspec(dllexport) Ret lockTape(Device handle);

/**
 * Unload a tape. Return once the tape is unlock. See https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-preparetape
 * */
__declspec(dllexport) Ret unlockTape(Device handle);

/**
 * Set the parameters of tape drive.
 * Use getTapeParameters to get current values and override the option
 * you want to change.
 * <br/>
 * See https://learn.microsoft.com/en-us/windows/win32/api/winnt/ns-winnt-tape_set_drive_parameters
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret setTapeDriveParameters(
        Device handle,
        bool ecc,
        bool compression,
        bool dataPadding,
        bool reportSetmarks,
        unsigned long eotWarningZoneSize
);

/**
 * Set the parameters of tape media.
 * Use getTapeParameters to get current values and override the option
 * you want to change.
 * <br/>
 * See https://learn.microsoft.com/en-us/windows/win32/api/winnt/ns-winnt-tape_set_media_parameters
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret setTapeMediaParameters(
        Device handle,
        unsigned long blockSize
);

/**
 * A convenience way to set compression.
 * Fetch tape drive info and only alter the compression setting.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret setTapeDriveCompression(
        Device handle,
        bool compression
);

/**
 * Get the device-specific block address.
 *
 * @param offsetLow low 32bits of offset.
 * @param offsetHigh high 32bits of offset, can be null if don't need.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret getTapeAbsolutePosition(
        Device handle,
        unsigned long long *offset
);

/**
 * Seek to a given device-specific block address.
 *
 * @param offset 64bit offset, must not be negative.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret seekTapeAbsolutePosition(
        Device handle,
        unsigned long long offset
);

/**
 * Get the logical block address. The offset is relative to the partition.
 *
 * @param partition returns the current partition, start from 1.
 * @param offsetLow low 32bits of offset.
 * @param offsetHigh high 32bits of offset, can be null if don't need.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret getTapeLogicalPosition(
        Device handle,
        unsigned long *partition,
        unsigned long long *offset
);

/**
 * Seek to a given logical block address. The offset is relative to the partition.
 *
 * @param partition returns the current partition, start from 1.
 * @param offsetLow low 32bits of offset.
 * @param offsetHigh high 32bits of offset.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret seekTapeLogicalPosition(
        Device handle,
        unsigned long partition,
        unsigned long long offset
);

/**
 * Rewind to the beginning of the tape.
 *
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret rewindTape(Device handle);

/**
 * Move the tape to the End Of Data of the given partition.
 *
 * @param partition start from 1.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret seekToEOD(Device handle, unsigned long partition);

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
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret seekFileMarks(Device handle, long long count);

/**
 * Move the tape forward (or backward) the N set marks from current position.
 * <br/>
 * Forward: stop at the end of the last set marks.
 * <br/>
 * Backward: stop at the beginning of the last set mark, you need to skip it
 * before you can write.
 *
 * @param count positive number to forward, negative number to backward.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret seekSetMarks(Device handle, long long count);

/**
 * Write N normal file marks at current position and move to next block.
 * This is the most general marks used on tapes.
 *
 * @param count the number of file marks to write.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret writeFileMarks(Device handle, unsigned long count);

/**
 * Write N short file marks at current position and move to next block.
 * <br/>
 * A short file marks cannot be overwritten unless the write operation is
 * performed from the beginning of the partition or from an earlier long
 * file mark. The short means this file mark has a short erase gap.
 *
 * @param count the number of set marks to write.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret writeShortFileMarks(Device handle, unsigned long count);

/**
 * Write N long file marks at current position and move to next block.
 * <br/>
 * A long file marks contains a long erase gap that allows an application
 * to position the tape at the beginning of the file mark and to overwrite
 * the file mark and the erase gap.
 *
 * @param count the number of file marks to write.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret writeLongFileMarks(Device handle, unsigned long count);

/**
 * Write N set marks at current position and move to next block.
 * <br/>
 * Set marks provide a hierarchy not available with file marks.
 *
 * @param count the number of set marks to write.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret writeSetMarks(Device handle, unsigned long count);

/**
 * Erase all or part of the tape.
 * <br/>
 * Fast erase write a erase gap or End Of Data marker at current position.
 * This will logically make the data accessible (unless using write anywhere mode).
 * <br/>
 * Full erase will literally erase the tape from current location to the
 * end of current partition.
 *
 * @param fast ture for fast erase; false for slower but full erase.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret eraseTape(Device handle, bool fast);

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
 * @param nBytesToRead how many bytes should be read, normally the size of buffer.
 * @param readBytesCount the actual bytes read from tape. Can be null if not needed.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret readTape(
        Device handle,
        void *buffer,
        unsigned long nBytesToRead,
        unsigned long *readBytesCount
);

/**
 * Write to tape.
 * <br/>
 * If writeTape encounters a non-overwrite-able file mark, the function fails
 * with an returned error code indicating the type of file mark that was
 * encountered (ERROR_FILEMARK_DETECTED 1101 or ERROR_SETMARK_DETECTED 1103).
 * <br/>
 * You have to manually write a file mark to properly finish the data/file.
 * <br/>
 * TODO: What error will be return when exceed end of the tape? ERROR_END_OF_MEDIA 1100?
 *
 * @param buffer the bytes to be written
 * @param nBytesToWrite how many bytes should be write.
 * @param readBytesCount the actual bytes write to tape. Can be null if not needed.
 * @returns 0 if success.
 * */
__declspec(dllexport) Ret writeTape(
        Device handle,
        void *buffer,
        unsigned long nBytesToWrite,
        unsigned long *writeBytesCount
);

#ifdef __cplusplus
}
#endif

#endif //WIN32TAPE_H
