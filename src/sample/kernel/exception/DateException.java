package sample.kernel.exception;

import sample.kernel.util.ThinkStr;

public class DateException extends Exception {
	private static final long serialVersionUID = -2338483876861721849L;

	public DateException() {
		super(ThinkStr.date_false.toString());
	}

	public DateException(String msg) {
		super(msg);
	}
}
