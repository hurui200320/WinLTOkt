// Generated by jextract

package jextract.win32tape;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
public class Win32Tape  {

    public static final OfByte C_CHAR = JAVA_BYTE;
    public static final OfShort C_SHORT = JAVA_SHORT;
    public static final OfInt C_INT = JAVA_INT;
    public static final OfInt C_LONG = JAVA_INT;
    public static final OfLong C_LONG_LONG = JAVA_LONG;
    public static final OfFloat C_FLOAT = JAVA_FLOAT;
    public static final OfDouble C_DOUBLE = JAVA_DOUBLE;
    public static final AddressLayout C_POINTER = RuntimeHelper.POINTER;
    /**
     * {@snippet :
     * typedef unsigned long Ret;
     * }
     */
    public static final OfInt Ret = JAVA_INT;
    /**
     * {@snippet :
     * typedef unsigned long* pRet;
     * }
     */
    public static final AddressLayout pRet = RuntimeHelper.POINTER;
    /**
     * {@snippet :
     * typedef void* Device;
     * }
     */
    public static final AddressLayout Device = RuntimeHelper.POINTER;
    public static MethodHandle openDevice$MH() {
        return RuntimeHelper.requireNonNull(constants$2.const$6,"openDevice");
    }
    /**
     * {@snippet :
     * Device openDevice(char* deviceName, pRet err);
     * }
     */
    public static MemorySegment openDevice(MemorySegment deviceName, MemorySegment err) {
        var mh$ = openDevice$MH();
        try {
            return (java.lang.foreign.MemorySegment)mh$.invokeExact(deviceName, err);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle closeDevice$MH() {
        return RuntimeHelper.requireNonNull(constants$3.const$1,"closeDevice");
    }
    /**
     * {@snippet :
     * void closeDevice(Device handle);
     * }
     */
    public static void closeDevice(MemorySegment handle) {
        var mh$ = closeDevice$MH();
        try {
            mh$.invokeExact(handle);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle getDeviceStatus$MH() {
        return RuntimeHelper.requireNonNull(constants$3.const$3,"getDeviceStatus");
    }
    /**
     * {@snippet :
     * Ret getDeviceStatus(Device handle);
     * }
     */
    public static int getDeviceStatus(MemorySegment handle) {
        var mh$ = getDeviceStatus$MH();
        try {
            return (int)mh$.invokeExact(handle);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle getTapeInfo$MH() {
        return RuntimeHelper.requireNonNull(constants$3.const$5,"getTapeInfo");
    }
    /**
     * {@snippet :
     * Ret getTapeInfo(Device handle, TapeInfo* result);
     * }
     */
    public static int getTapeInfo(MemorySegment handle, MemorySegment result) {
        var mh$ = getTapeInfo$MH();
        try {
            return (int)mh$.invokeExact(handle, result);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle loadTape$MH() {
        return RuntimeHelper.requireNonNull(constants$4.const$0,"loadTape");
    }
    /**
     * {@snippet :
     * Ret loadTape(Device handle);
     * }
     */
    public static int loadTape(MemorySegment handle) {
        var mh$ = loadTape$MH();
        try {
            return (int)mh$.invokeExact(handle);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle unloadTape$MH() {
        return RuntimeHelper.requireNonNull(constants$4.const$1,"unloadTape");
    }
    /**
     * {@snippet :
     * Ret unloadTape(Device handle);
     * }
     */
    public static int unloadTape(MemorySegment handle) {
        var mh$ = unloadTape$MH();
        try {
            return (int)mh$.invokeExact(handle);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle lockTape$MH() {
        return RuntimeHelper.requireNonNull(constants$4.const$2,"lockTape");
    }
    /**
     * {@snippet :
     * Ret lockTape(Device handle);
     * }
     */
    public static int lockTape(MemorySegment handle) {
        var mh$ = lockTape$MH();
        try {
            return (int)mh$.invokeExact(handle);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle unlockTape$MH() {
        return RuntimeHelper.requireNonNull(constants$4.const$3,"unlockTape");
    }
    /**
     * {@snippet :
     * Ret unlockTape(Device handle);
     * }
     */
    public static int unlockTape(MemorySegment handle) {
        var mh$ = unlockTape$MH();
        try {
            return (int)mh$.invokeExact(handle);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle setTapeDriveParameters$MH() {
        return RuntimeHelper.requireNonNull(constants$4.const$5,"setTapeDriveParameters");
    }
    /**
     * {@snippet :
     * Ret setTapeDriveParameters(Device handle, _Bool ecc, _Bool compression, _Bool dataPadding, _Bool reportSetmarks, unsigned long eotWarningZoneSize);
     * }
     */
    public static int setTapeDriveParameters(MemorySegment handle, boolean ecc, boolean compression, boolean dataPadding, boolean reportSetmarks, int eotWarningZoneSize) {
        var mh$ = setTapeDriveParameters$MH();
        try {
            return (int)mh$.invokeExact(handle, ecc, compression, dataPadding, reportSetmarks, eotWarningZoneSize);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle setTapeMediaParameters$MH() {
        return RuntimeHelper.requireNonNull(constants$5.const$1,"setTapeMediaParameters");
    }
    /**
     * {@snippet :
     * Ret setTapeMediaParameters(Device handle, unsigned long blockSize);
     * }
     */
    public static int setTapeMediaParameters(MemorySegment handle, int blockSize) {
        var mh$ = setTapeMediaParameters$MH();
        try {
            return (int)mh$.invokeExact(handle, blockSize);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle setTapeDriveCompression$MH() {
        return RuntimeHelper.requireNonNull(constants$5.const$3,"setTapeDriveCompression");
    }
    /**
     * {@snippet :
     * Ret setTapeDriveCompression(Device handle, _Bool compression);
     * }
     */
    public static int setTapeDriveCompression(MemorySegment handle, boolean compression) {
        var mh$ = setTapeDriveCompression$MH();
        try {
            return (int)mh$.invokeExact(handle, compression);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle getTapeAbsolutePosition$MH() {
        return RuntimeHelper.requireNonNull(constants$5.const$4,"getTapeAbsolutePosition");
    }
    /**
     * {@snippet :
     * Ret getTapeAbsolutePosition(Device handle, unsigned long long* offset);
     * }
     */
    public static int getTapeAbsolutePosition(MemorySegment handle, MemorySegment offset) {
        var mh$ = getTapeAbsolutePosition$MH();
        try {
            return (int)mh$.invokeExact(handle, offset);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle seekTapeAbsolutePosition$MH() {
        return RuntimeHelper.requireNonNull(constants$5.const$6,"seekTapeAbsolutePosition");
    }
    /**
     * {@snippet :
     * Ret seekTapeAbsolutePosition(Device handle, unsigned long long offset);
     * }
     */
    public static int seekTapeAbsolutePosition(MemorySegment handle, long offset) {
        var mh$ = seekTapeAbsolutePosition$MH();
        try {
            return (int)mh$.invokeExact(handle, offset);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle getTapeLogicalPosition$MH() {
        return RuntimeHelper.requireNonNull(constants$6.const$1,"getTapeLogicalPosition");
    }
    /**
     * {@snippet :
     * Ret getTapeLogicalPosition(Device handle, unsigned long* partition, unsigned long long* offset);
     * }
     */
    public static int getTapeLogicalPosition(MemorySegment handle, MemorySegment partition, MemorySegment offset) {
        var mh$ = getTapeLogicalPosition$MH();
        try {
            return (int)mh$.invokeExact(handle, partition, offset);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle seekTapeLogicalPosition$MH() {
        return RuntimeHelper.requireNonNull(constants$6.const$3,"seekTapeLogicalPosition");
    }
    /**
     * {@snippet :
     * Ret seekTapeLogicalPosition(Device handle, unsigned long partition, unsigned long long offset);
     * }
     */
    public static int seekTapeLogicalPosition(MemorySegment handle, int partition, long offset) {
        var mh$ = seekTapeLogicalPosition$MH();
        try {
            return (int)mh$.invokeExact(handle, partition, offset);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle rewindTape$MH() {
        return RuntimeHelper.requireNonNull(constants$6.const$4,"rewindTape");
    }
    /**
     * {@snippet :
     * Ret rewindTape(Device handle);
     * }
     */
    public static int rewindTape(MemorySegment handle) {
        var mh$ = rewindTape$MH();
        try {
            return (int)mh$.invokeExact(handle);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle seekToEOD$MH() {
        return RuntimeHelper.requireNonNull(constants$6.const$5,"seekToEOD");
    }
    /**
     * {@snippet :
     * Ret seekToEOD(Device handle, unsigned long partition);
     * }
     */
    public static int seekToEOD(MemorySegment handle, int partition) {
        var mh$ = seekToEOD$MH();
        try {
            return (int)mh$.invokeExact(handle, partition);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle seekFileMarks$MH() {
        return RuntimeHelper.requireNonNull(constants$7.const$0,"seekFileMarks");
    }
    /**
     * {@snippet :
     * Ret seekFileMarks(Device handle, long long count);
     * }
     */
    public static int seekFileMarks(MemorySegment handle, long count) {
        var mh$ = seekFileMarks$MH();
        try {
            return (int)mh$.invokeExact(handle, count);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle writeFileMarks$MH() {
        return RuntimeHelper.requireNonNull(constants$7.const$1,"writeFileMarks");
    }
    /**
     * {@snippet :
     * Ret writeFileMarks(Device handle, unsigned long count);
     * }
     */
    public static int writeFileMarks(MemorySegment handle, int count) {
        var mh$ = writeFileMarks$MH();
        try {
            return (int)mh$.invokeExact(handle, count);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle eraseTape$MH() {
        return RuntimeHelper.requireNonNull(constants$7.const$2,"eraseTape");
    }
    /**
     * {@snippet :
     * Ret eraseTape(Device handle, _Bool fast);
     * }
     */
    public static int eraseTape(MemorySegment handle, boolean fast) {
        var mh$ = eraseTape$MH();
        try {
            return (int)mh$.invokeExact(handle, fast);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle readTape$MH() {
        return RuntimeHelper.requireNonNull(constants$7.const$4,"readTape");
    }
    /**
     * {@snippet :
     * Ret readTape(Device handle, void* buffer, unsigned long nBytesToRead, unsigned long* readBytesCount);
     * }
     */
    public static int readTape(MemorySegment handle, MemorySegment buffer, int nBytesToRead, MemorySegment readBytesCount) {
        var mh$ = readTape$MH();
        try {
            return (int)mh$.invokeExact(handle, buffer, nBytesToRead, readBytesCount);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
    public static MethodHandle writeTape$MH() {
        return RuntimeHelper.requireNonNull(constants$7.const$5,"writeTape");
    }
    /**
     * {@snippet :
     * Ret writeTape(Device handle, void* buffer, unsigned long nBytesToWrite, unsigned long* writeBytesCount);
     * }
     */
    public static int writeTape(MemorySegment handle, MemorySegment buffer, int nBytesToWrite, MemorySegment writeBytesCount) {
        var mh$ = writeTape$MH();
        try {
            return (int)mh$.invokeExact(handle, buffer, nBytesToWrite, writeBytesCount);
        } catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }
}

