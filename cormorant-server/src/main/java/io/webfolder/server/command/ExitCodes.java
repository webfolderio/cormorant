package io.webfolder.server.command;

public interface ExitCodes {
    static final int INVALID_ARG                      = -1;
    static final int CAN_NOT_CREATE_DATA_FOLDER       = -1000 - 1;
    static final int CAN_NOT_CREATE_METADATA_FOLDER   = -1000 - 2;
    static final int CAN_NOT_CREATE_ACCESS_LOG_FOLDER = -1000 - 3;
}
