package com.imooc.o2o.exception;

public class ProductOperationException extends RuntimeException {

	/**
	 * add generated serial verisonID
	 */
	private static final long serialVersionUID = -552463239137877992L;

	public ProductOperationException(String msg) {
		super(msg);
	}
}
