/**
 * The MIT License
 * Copyright © 2017 WebFolder OÜ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.webfolder.server.command;

public interface ExitCodes {

    static final int INVALID_ARG                      = -1;
    static final int CAN_NOT_CREATE_DATA_FOLDER       = -1000 - 1;
    static final int CAN_NOT_CREATE_METADATA_FOLDER   = -1000 - 2;
    static final int CAN_NOT_CREATE_ACCESS_LOG_FOLDER = -1000 - 3;

    static final int INVALID_PATH                     = -2000 - 1;
    static final int PID_FILE_NOT_FOUND               = -2000 - 2;
    static final int IO_ERROR                         = -2000 - 3;
    static final int UNABLE_TO_READ_PID_FILE          = -2000 - 4;
    static final int UNABLE_TO_WRITE_PID_FILE         = -2000 - 5;
    static final int INTERRUPTED                      = -2000 - 6;
    static final int INVALID_PID_FILE                 = -2000 - 7;
    static final int PROCESS_NOT_FOUND                = -2000 - 8;    
}
