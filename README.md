# WinLTOkt
A Kotlin library for operating LTO tape drives on Windows.

## Why?

Because Windows sucks.
While linux offers you tons of utilities to operate your tape drive,
Windows only offers a `\\.\TAPE0` and that's all you get.

This project aims to bring back the Linux experience back to, at least on JVM, Windows.
With [win32 tape API for backup](https://learn.microsoft.com/en-us/windows/win32/backup/tape-backup),
this project can offer something like `mt` command from Linux, that allows you
to move tape around (like rewind, seek filemarks, etc.), and something like
an input and output stream to `/dev/nst0`.

_Once I tried to implement the hardware encryption thing, like [LTOEnc](https://github.com/VulpesSARL/LTOEnc)
and [stenc](https://github.com/scsitape/stenc), but this is my first time writing C/C++ project.
I can hardly write C/C++ code without triggering segment fault and causing memory leaks
(that's why I choose JVM).
The SCSI thing is totally new to me, and I have no clue on where to start.
Not mention the SCSI Secure Protocol thing.
So just use LTOEnc on windows or stenc on Linux to manage hardware encryption._

## How?

The `TapeDrive` offers a OOP way to access the tape drive.
When initializing the object, it will request a HANDLE and save it.
All operations related to this tape drive will be transformed to the actual win32 api calls,
with project panama.

We also offers `BufferedTapeInputStream` and `BufferedTapeOutputStream`,
which works like a file input and output stream to Linux's `/dev/nst0`.
Note it's buffered since KVM's memory is not fixed, while C don't know that.
Thus, all data must be copied between C and JVM side, which actually acts like a buffer.
This also means that all data will be copied and hit the performance during read and write.
However, with my setup, I got 130MiB/s write and 163.84MiB/s read (sequential)
on HPE 6250 LTO-6 tape drive (of course without compression). 

I don't know if this will influence a later generation like LTO-9.
All I have is a second-hand LTO-6 drive, it's good enough for me.

## Usage?

Unfortunately, this package is not released to maven or even [jitpack](https://jitpack.io/#info.skyblond/WinLTOkt).
Since C/C++ code is involved.
Current solution is including the prebuilt dll file in the resource file,
during loading the `TapeDrive` class, it will automatically write to either
your home dir or tmp dir, then load it from there.
You may try JitPack to build jar file and use it.
But if you don't trust my prebuilt dll file, you may build yourself.
I set up the C project to build and copy the dll file to resource folder.
It's recommended that you don't trust some random guy offering you a prebuilt
binary on the internet, not even GitHub.

## Footnote

Currently this project is solely built on top of project Panama.
Currently I'm using JDK21, the Project Panama still under preview,
things can change.
The main reason I have this project is that: 1) Windows sucks, 2)
jextract can't handle win32 header.
I have to write a separate C/C++ project to isolate win32 stuff to
the native world, exposing only the good old C header to jextract
and JVM.

If one day jextract can process win32 header, then with someone
ambitious, you may be able to call any win32 apis including those
tape related ones.

## Maintenance

I wrote this project for fun. I don't know if I'm going to
use this project for my future project. So there is no guarantee
on if I'm going to keep maintaining this project.

One thing for sure: If you pay me enough, I'll do whatever you want.

## License

As usual, all my projects for fun are AGPLv3.
Feel free to ask for a special license.
