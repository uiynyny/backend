package com.ecom.orderProcessService.exceptions;

public enum ErrorCode {
	BUSINESS_VALIDATION_ERROR(0, "A error has occured during processing.."),
	UNABLE_TO_START_SERVICE(1, "Unable to start service due to config error"),
	VALIDATION_ERROR(2, "input validation error"),
	ORDER_AUTH_ERROR(3, "error while authorizing the order"),
	CONFIRM_ORDER(4, "Error occured while confirming order"),
	CREATE_ACCOUNT(5,"create account error"),
	CHECK_ACCOUNT(6,"Check account error"),
	CHECK_COUNT(7,"Error while counting rows");

	private final int errorCode;
	private final String errorDescription;

	private ErrorCode(int code, String description) {
		this.errorCode = code;
		this.errorDescription = description;
	}

	public String getDescription() {
		return errorDescription;
	}

	public int getCode() {
		return errorCode;
	}
}