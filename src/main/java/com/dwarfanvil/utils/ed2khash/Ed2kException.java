package com.dwarfanvil.utils.ed2khash;

/**
 * @author Juan Villablanca
 *
 */
public class Ed2kException extends Exception {

	private static final long serialVersionUID = -6385357918705177624L;

	public Ed2kException() {
	}

	public Ed2kException(String message) {
		super(message);
	}

	public Ed2kException(Throwable cause) {
		super(cause);
	}

	public Ed2kException(String message, Throwable cause) {
		super(message, cause);
	}

	public Ed2kException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
