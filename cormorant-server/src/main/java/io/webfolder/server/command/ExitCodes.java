/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (support@webfolder.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
