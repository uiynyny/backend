package com.ecom.productCatalog.exceptions;

public class ProductCatalogException extends Exception {

	private static final long serialVersionUID = 1L;

	private ErrorCode code;

	public ProductCatalogException() {
		super();
	}

	public ProductCatalogException(ErrorCode code) {
		// TODO Auto-generated constructor stub
		this.code = code;
	}

	public ErrorCode getErrorCode() {
		return this.code;
	}

}
