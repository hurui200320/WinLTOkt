// For testing. Contains no actual code.
// Created by hurui on 2023/12/26.
//

#include <iostream>
#include <cassert>
#include <windows.h>
#include "win32tape.h"


void printPos(Device handle) {
    unsigned long long pos = 0;
    assert(getTapeAbsolutePosition(handle, &pos) == NO_ERROR);
    std::cout << "pos: " << pos << std::endl;
}

void readTest(Device handle, unsigned long bufferSize) {
    Ret ret;
    auto buffer = malloc(bufferSize);
    for (int i = 0; i < 3; ++i) {
        unsigned long readCount = 0;
        unsigned long long totalReadCounter = 0;
        std::cout << "----------" << std::endl;
        while (true) {
            memset(buffer, 0, bufferSize);
            ret = readTape(handle, buffer, bufferSize, &readCount);
            if (ret != NO_ERROR) {
                // File mark -> stop current reading
                if (ret == ERROR_FILEMARK_DETECTED) break;
                    // EOM -> stop reading
                else if (ret == ERROR_NO_DATA_DETECTED) goto exitReadingLoop;
                else {
                    std::cerr << "ret: " << ret << std::endl;
                    assert(ret == NO_ERROR);
                }
            }
            totalReadCounter += readCount;
//            std::cout << buffer;
            std::cout << readCount << std::endl;
        }
        std::cout << std::endl << "----------" << std::endl;
        std::cout << "Total read count: " << totalReadCounter << std::endl;

        printPos(handle);
    }
    exitReadingLoop:
    free(buffer);
}

void rewindTape(Device handle) {
    std::cout << "\nrewind" << std::endl;
    assert(rewind(handle) == NO_ERROR);
    printPos(handle);
}


int main() {
    Ret ret = 0;
    auto handle = openDevice(R"(\\.\TAPE0)", &ret);
    setTapeDriveCompression(handle, false);
//    rewindTape(handle);

    std::cout << "\nget tape info" << std::endl;
    auto tapeInfo = (TapeInfo *) malloc(sizeof(TapeInfo));
    ret = getTapeInfo(handle, tapeInfo);
    assert(ret == NO_ERROR);
    std::cout << "block size: " << tapeInfo->mediaBlockSize << std::endl;
    // 256MB buffer
    auto bufferSize = max(tapeInfo->mediaBlockSize, 256 * 1024 * 1024);
    std::cout << "bufferSize: " << bufferSize << std::endl;

    ret = seekTapeAbsolutePosition(handle, 100000000L);
    assert(ret == NO_ERROR);
    printPos(handle);
    auto buffer = malloc(bufferSize);
    unsigned long writeCount = 0;
    unsigned long long totalWriteCounter = 0;

    for (int i = 0; i < 5; ++i) {
        ret = writeTape(handle, buffer, bufferSize, &writeCount);
        if (ret != NO_ERROR) {
            // File mark -> stop current reading
            if (ret == ERROR_FILEMARK_DETECTED) break;
                // EOM -> stop reading
//            else if (ret == ERROR_NO_DATA_DETECTED) goto exitReadingLoop;
            else {
                std::cerr << "ret: " << ret << std::endl;
                assert(ret == NO_ERROR);
            }
        }
        totalWriteCounter += writeCount;
        std::cout << "write: " <<  writeCount << std::endl;
    }
    writeFileMarks(handle, 1);
    std::cout << "total write: " <<  totalWriteCounter << std::endl;
    printPos(handle);

    rewindTape(handle);
    seekTapeAbsolutePosition(handle, 1);
    printPos(handle);
    std::cout << "\nread" << std::endl;
    readTest(handle, bufferSize);

    free(tapeInfo);
    closeDevice(handle);
    return 0;
}