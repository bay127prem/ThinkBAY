package sample.kernel.exception;

import sample.kernel.util.ThinkStr;

public class ConnectionException extends Exception {
	private static final long serialVersionUID = 1286183483983343865L;

		public ConnectionException() {
			super(ThinkStr.connection_failed.toString());
		}

		public ConnectionException(String msg) {
			super(msg);
		}

}
