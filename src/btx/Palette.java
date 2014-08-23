package btx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class Palette {
	private int offset, color0;
	private Color[] pal;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getColor0() {
		return color0;
	}

	public void setColor0(int color0) {
		this.color0 = color0;
	}

	public Color[] getPal() {
		return pal;
	}

	public void setPal(Color[] pal) {
		this.pal = pal;
	}

	public static Color[] toBGR555Array(byte[] bytes) {
		Color[] palette = new Color[bytes.length / 2];
		for (int i = 0; i < bytes.length / 2; i++)
			palette[i] = toBGR555(bytes[i * 2], bytes[i * 2 + 1]);
		return palette;
	}

	public static byte[] fromBGR555(Color[] p) {
		ByteBuffer b = ByteBuffer.allocate(p.length * 2).order(
				ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < p.length; i++) {
			int num = 0;
			num += (p[i].getRed() / 0x08);
			num += ((p[i].getGreen() / 0x08) << 5);
			num += ((p[i].getBlue() / 0x08) << 10);
			b.putShort((short) num);
		}
		return b.array();
	}

	public static Color toBGR555(int byte1, int byte2) {
		ByteBuffer b = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
		b.put((byte) byte1);
		b.put((byte) byte2);
		short num = b.getShort(0);
		return new Color(Display.getCurrent(), (num & 0x001F) * 0x08,
				((num & 0x03E0) >> 5) * 0x08, ((num & 0x7C00) >> 10) * 0x08);
	}

	public RGB[] getPalRGBs() {
		RGB[] rgb = new RGB[pal.length];
		for (int i = 0; i < pal.length; i++)
			rgb[i] = pal[i].getRGB();
		return rgb;
	}
}
