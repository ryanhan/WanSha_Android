package com.ipang.wansha.exception;

public class ProductException extends Exception{

	private static final long serialVersionUID = 2510985004545813280L;
	
	public static final int UNKNOWN_ERROR = 0;
	public static final int NETWORK_CONNECT_FAILED = 1;
	public static final int JSON_FORMAT_NOT_MATCH = 2;

	private int exceptionCause;
	
	public ProductException(int exceptionCause) {
        super();
		this.exceptionCause = exceptionCause;
    }

	public int getExceptionCause() {
		return exceptionCause;
	}

}
