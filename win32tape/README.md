# win32tape

This is a simple C/C++ wrapper of Windows' win32 tape API.
Because jextract has problem processing win32 headers, this
is a good-enough way to isolate the native win32 APIs.

_There was a plan for SCSI Secure Protocol support to control
the hardware encryption, but as a Java/Kotlin developer, SCSI
is a totally new world to me. Not even mention that you need
a lot of experience to do things right with security stuff.
I don't have a tape library, so I'd take the speed and performance
penalty and use software encryption._

This is not a full and dedicated wrapper for win32 tape API,
it just a good-enough wrapper for my own project, that's why
it's located in a sub folder of a Kotlin project. It solely
serves the Kotlin project.

Note: not all feature in this wrapper works, like the long
and short file marks. I found them very useful when reading
the documentation but then find out I can't get it working.
Later by `mt -f /dev/nst0 status 3` I learnt that my tape
drive can't do it.

This C wrapper is implemented as a general tape API wrapper,
but the parent project is focus on LTO tape drive. You know
the difference.
