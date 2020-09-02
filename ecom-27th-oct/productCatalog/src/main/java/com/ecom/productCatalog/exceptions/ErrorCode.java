package com.ecom.productCatalog.exceptions;

public enum ErrorCode {
	BUSINESS_VALIDATION_ERROR(0, "A error has occured during processing.."),
	UNABLE_TO_START_SERVICE(1, "No config File found"),
	QUERY_PROCESSING_ERROR(2, "Error occured while executing query");

	private final int errorCode;
	private final String errorDescription;

	/**
	 * @param code
	 * @param description
	 */
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