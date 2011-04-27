package bbth.network;

public enum State {
	DISCONNECTED, ENABLE_BLUETOOTH, LIST_DEVICES, CONNECT_TO_EACH_DEVICE, LISTEN_FOR_CONNECTIONS, CONNECTED;

	@Override
	public String toString() {
		switch (this) {
		case DISCONNECTED:
			return "Disconnected";

		case ENABLE_BLUETOOTH:
			return "Enabling bluetooth...";

		case LIST_DEVICES:
			return "Finding all devices...";

		case CONNECT_TO_EACH_DEVICE:
			return "Checking each device...";

		case LISTEN_FOR_CONNECTIONS:
			return "Listening for connections...";

		case CONNECTED:
			return "Connected";

		default:
			return "???";
		}
	}
}
