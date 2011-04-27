package bbth.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Packet {

	public abstract void read(DataInputStream stream) throws IOException;
	public abstract void write(DataOutputStream stream) throws IOException;
}
