package com.ipang.wansha.exception;

public class UserException extends Exception{

	private static final long serialVersionUID = 1424646102115588256L;
	
	public static final int UNKNOWN_ERROR = 0;
	public static final int WRONG_USERNAME_PASSWORD = 1;
	public static final int NETWORK_CONNECT_FAILED = 2;
	public static final int JSON_FORMAT_NOT_MATCH = 3;
	public static final int REGISTER_FAILED = 4;
	public static final int JSESSION_NOT_FOUND = 5;
	public static final int LOGIN_FAILED = 6;
	public static final int NOT_ALIVE = 7;
	public static final int CHANGE_PASSWORD_FAILED = 8;


	
	private int exceptionCause;
	
	public UserException(int exceptionCause) {
        super();
		this.exceptionCause = exceptionCause;
    }

	public int getExceptionCause() {
		return exceptionCause;
	}

}
