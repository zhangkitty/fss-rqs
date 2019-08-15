package com.znv.fssrqs.util.hbase;

/**
 * Created by dongzelong on  2019/8/15 18:47.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class ConnectionException extends RuntimeException{
    private static final long serialVersionUID = -6503525110247209484L;

    public ConnectionException() {
        super();
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(Throwable e) {
        super(e);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
