package info.skyblond.win32tape

class Win32RuntimeException(
    operation: String,
    val code: Int,
) : RuntimeException("$operation failed with err: $code")
