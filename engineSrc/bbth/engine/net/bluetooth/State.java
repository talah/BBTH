package bbth.engine.net.bluetooth;

public enum State {
	DISCONNECTED("Disconnected"),
	ENABLE_BLUETOOTH("Enabling bluetooth..."),
	CHECK_PREVIOUS_CONNECTION("Checking previous connection..."),
	GET_NEARBY_DEVICES("Finding all nearby devices..."),
	CONNECT_TO_DEVICE("Connecting..."),
	LISTEN_FOR_CONNECTIONS("Listening for connections..."),
	CONNECTED("Connected");

	private final String message;

	private State(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}
