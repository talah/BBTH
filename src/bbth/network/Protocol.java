package bbth.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public interface Protocol {

	public UUID getUUID();

	public Packet readPacket(DataInputStream stream) throws IOException;
}
