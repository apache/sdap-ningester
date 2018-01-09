/*
 *****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.jpl.nexus.ningester.writer;

public class DataStoreException extends RuntimeException {

    public DataStoreException() {
    }

    public DataStoreException(String message) {
        super(message);
    }

    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataStoreException(Throwable cause) {
        super(cause);
    }

    public DataStoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
