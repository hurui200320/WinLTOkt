// Generated by jextract

package jextract.win32tape;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
final class constants$0 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$0() {}
    static final StructLayout const$0 = MemoryLayout.structLayout(
        JAVA_BOOLEAN.withName("driveECC"),
        JAVA_BOOLEAN.withName("driveCompression"),
        JAVA_BOOLEAN.withName("driveDataPadding"),
        JAVA_BOOLEAN.withName("driveReportSetmarks"),
        JAVA_INT.withName("driveDefaultBlockSize"),
        JAVA_INT.withName("driveMaximumBlockSize"),
        JAVA_INT.withName("driveMinimumBlockSize"),
        JAVA_INT.withName("driveMaximumPartitionCount"),
        JAVA_INT.withName("driveFeaturesLow"),
        JAVA_INT.withName("driveFeaturesHigh"),
        JAVA_INT.withName("driveEOTWarningZoneSize"),
        JAVA_LONG.withName("mediaCapacity"),
        JAVA_LONG.withName("mediaRemaining"),
        JAVA_INT.withName("mediaBlockSize"),
        JAVA_INT.withName("mediaPartitionCount"),
        JAVA_BOOLEAN.withName("mediaWriteProtected"),
        MemoryLayout.paddingLayout(7)
    ).withName("");
    static final VarHandle const$1 = constants$0.const$0.varHandle(MemoryLayout.PathElement.groupElement("driveECC"));
    static final VarHandle const$2 = constants$0.const$0.varHandle(MemoryLayout.PathElement.groupElement("driveCompression"));
    static final VarHandle const$3 = constants$0.const$0.varHandle(MemoryLayout.PathElement.groupElement("driveDataPadding"));
    static final VarHandle const$4 = constants$0.const$0.varHandle(MemoryLayout.PathElement.groupElement("driveReportSetmarks"));
    static final VarHandle const$5 = constants$0.const$0.varHandle(MemoryLayout.PathElement.groupElement("driveDefaultBlockSize"));
}

