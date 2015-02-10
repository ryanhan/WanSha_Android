package com.ipang.wansha.exception;

public class OfflineException extends Exception{

	private static final long serialVersionUID = 2510985004545813280L;
	
	public static final int UNKNOWN_ERROR = 0;
	public static final int NETWORK_CONNECT_FAILED = 1;

	private int exceptionCause;
	
	public OfflineException(int exceptionCause) {
        super();
		this.exceptionCause = exceptionCause;
    }

	public int getExceptionCause() {
		return exceptionCause;
	}

}
