package com.ipang.wansha.exception;

public class HttpException extends Exception {

	private static final long serialVersionUID = 3224736811104280753L;

	public static final int UNKNOWN_ERROR = 0;
	public static final int HOST_CONNECT_FAILED = 1;
	public static final int HTTP_RESPONSE_ERROR = 2;
	public static final int REQUEST_CANCELLED = 3;

	private int exceptionCause;

	public HttpException(int exceptionCause) {
		super();
		this.exceptionCause = exceptionCause;
	}

	public int getExceptionCause() {
		return exceptionCause;
	}
}
