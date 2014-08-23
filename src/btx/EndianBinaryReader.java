package btx;

import java.io.ByteArrayInputStream;

public class EndianBinaryReader extends ByteArrayInputStream {
	public int size;

	public EndianBinaryReader(byte[] data) {
		super(data);
		size = data.length;
	}

	public void seek(int pos) {
		super.reset();
		super.skip(pos);
	}

	public byte[] readBytes(int num) {
		byte[] tmp = new byte[num];
		super.read(tmp, 0, num);
		return tmp;
	}

	public String readString(int num) {
		byte[] tmp = new byte[num];
		super.read(tmp, 0, num);
		return new String(tmp);
	}

	public int readInt32() {
		final long low = (((super.read() & 0xff) << 0)
				+ ((super.read() & 0xff) << 8) + ((super.read() & 0xff) << 16));

		final long high = super.read() & 0xff;

		return (int) ((high << 24) + (0xffffffffL & low));
	}

	public int readInt16() {
		return (((super.read() & 0xff) << 0) + ((super.read() & 0xff) << 8));
	}

	public int getPosition() {
		return super.pos;
	}
}
