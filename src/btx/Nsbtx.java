package btx;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import narc.FimgEntry;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class Nsbtx {
	private int size, texSize;
	private int texDataSize, texDataOffset;
	private int compTexDataSize, compTexDataOffset, compTexInfoDataOffset;
	private int palDataSize, palInfoOffset, palDataOffset;

	private int numObjects_3d, size_3d, infoDataSize_3d;
	private int numObjects_pal, size_pal, infoDataSize_pal;

	private byte[] unknownBlock1, unknowBlock2;

	private List<Texture> textures = new ArrayList<Texture>();
	private List<Palette> palettes = new ArrayList<Palette>();
	private int[] bpp = { 0, 8, 2, 4, 8, 2, 8, 16 };
	private String[] tex_names, pal_names;
	public ImageData img[];

	public Nsbtx(FimgEntry fs) throws IOException {
		try {
			EndianBinaryReader inp = new EndianBinaryReader(fs.getEntryData());
			if (inp.readInt32() != 811095106) {
				inp.close();
				throw new IOException();
			}
			inp.skip(4);
			this.size = inp.readInt32();
			inp.skip(12);
			this.texSize = inp.readInt32();
			inp.skip(4);
			this.texDataSize = inp.readInt16() << 3;
			inp.skip(6);
			this.texDataOffset = inp.readInt32();
			inp.skip(4);
			this.compTexDataSize = inp.readInt16() << 3;
			inp.skip(6);
			this.compTexDataOffset = inp.readInt32();
			this.compTexInfoDataOffset = inp.readInt32();
			inp.skip(4);
			this.palDataSize = inp.readInt32() << 3;
			this.palInfoOffset = inp.readInt32();
			this.palDataOffset = inp.readInt32();
			inp.skip(1);
			this.numObjects_3d = inp.read();
			this.img = new ImageData[this.numObjects_3d];
			this.size_3d = inp.readInt16();
			this.unknownBlock1 = inp.readBytes(10 + 4 * this.numObjects_3d);
			this.infoDataSize_3d = inp.readInt16();
			for (int i = 0; i < this.numObjects_3d; i++) {
				Texture tmp = new Texture();
				tmp.setOffset(inp.readInt16() << 3);
				tmp.setParameter(inp.readInt16());
				tmp.setWidth2(inp.read());
				tmp.setUnknown1(inp.read());
				tmp.setHeight2(inp.read());
				tmp.setUnknown2(inp.read());
				tmp.setCoord_transf(tmp.getParameter() & 14);
				tmp.setColor0((tmp.getParameter() >> 13) & 1);
				tmp.setFormat((tmp.getParameter() >> 10) & 7);
				tmp.setHeight(8 << ((tmp.getParameter() >> 7) & 7));
				tmp.setWidth(8 << ((tmp.getParameter() >> 4) & 7));
				tmp.setFlipY((tmp.getParameter() >> 3) & 1);
				tmp.setFlipX((tmp.getParameter() >> 2) & 1);
				tmp.setRepeatY((tmp.getParameter() >> 1) & 1);
				tmp.setRepeatX(tmp.getParameter() & 1);
				tmp.setDepth(bpp[tmp.getFormat()]);

				if (tmp.getWidth() == 0x00)
					switch (tmp.getUnknown1() & 0x3) {
					case 2:
						tmp.setWidth(0x200);
						break;
					default:
						tmp.setWidth(0x100);
					}
				if (tmp.getHeight() == 0x00)
					switch ((tmp.getHeight2() >> 3) & 0x3) {
					case 2:
						tmp.setHeight(0x200);
						break;
					default:
						tmp.setHeight(0x100);
						break;
					}
				int imgsize = (tmp.getWidth() * tmp.getHeight() * tmp
						.getDepth()) / 8;
				int curpos = inp.getPosition();
				if (tmp.getFormat() != 5)
					inp.seek(tmp.getOffset() + 20 + this.texDataOffset);
				else
					inp.seek(20 + this.compTexDataOffset + tmp.getOffset());
				tmp.setImage(inp.readBytes(imgsize));
				inp.seek(curpos);

				if (tmp.getFormat() == 5) {
					curpos = inp.getPosition();
					inp.seek(20 + this.compTexInfoDataOffset + tmp.getOffset()
							/ 2);
					tmp.setSpData(inp.readBytes(imgsize / 2));
					inp.seek(curpos);
				}

				textures.add(tmp);
			}

			this.tex_names = new String[this.numObjects_3d];
			for (int i1 = 0; i1 < this.numObjects_3d; i1++)
				this.tex_names[i1] = inp.readString(16).replace("\0", "");

			inp.skip(1);
			this.numObjects_pal = inp.read();
			this.size_pal = inp.readInt16();
			this.unknowBlock2 = inp.readBytes(10 + 4 * this.numObjects_pal);
			this.infoDataSize_pal = inp.readInt16();
			for (int i = 0; i < this.numObjects_pal; i++) {
				Palette tmp = new Palette();
				tmp.setOffset(inp.readInt16() << 3);
				tmp.setColor0(inp.readInt16());
				int curpos = inp.getPosition();
				inp.seek(20 + this.palDataOffset + tmp.getOffset());
				tmp.setPal(Palette.toBGR555Array(inp.readBytes(this.palDataSize
						- tmp.getOffset())));
				palettes.add(tmp);
				inp.seek(curpos);
			}

			this.pal_names = new String[this.numObjects_pal];
			for (int i = 0; i < this.numObjects_pal; i++)
				this.pal_names[i] = inp.readString(16).replace("\0", "");

			for (int i = 0; i < this.numObjects_3d; i++)
				this.img[i] = getImage(i, 0).getImageData();

			inp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Image getImage(int index, int palnum) {
		img[index] = new ImageData(textures.get(index).getWidth(), textures
				.get(index).getHeight(), textures.get(index).getDepth(),
				new PaletteData(palettes.get(palnum).getPalRGBs()));
		int pixelnum = textures.get(index).getWidth()
				* textures.get(index).getHeight();
		switch (textures.get(index).getFormat()) {
		case 1:
			for (int j = 0; j < pixelnum; j++) {
				int index2 = textures.get(index).getImage()[j] & 0x1f;
				int alpha = textures.get(index).getImage()[j] >> 5;
				alpha = ((alpha * 4) + (alpha / 2)) * 8;
				img[index]
						.setPixel(
								j
										- ((j / (textures.get(index).getWidth())) * (textures
												.get(index).getWidth())), j
										/ (textures.get(index).getWidth()),
								img[index].palette.getPixel(img[index].palette
										.getRGB(index2)));
				img[index]
						.setAlpha(
								j
										- ((j / (textures.get(index).getWidth())) * (textures
												.get(index).getWidth())), j
										/ (textures.get(index).getWidth()),
								alpha);
			}
			break;
		case 2:
			for (int j = 0; j < pixelnum; j++) {
				int index2 = textures.get(index).getImage()[j / 4];
				index2 = (index2 >> ((j % 4) << 1)) & 3;
				if (index2 == 0 && textures.get(index).getColor0() == 1)
					img[index]
							.setPixel(
									j
											- ((j / (textures.get(index)
													.getWidth())) * (textures
													.get(index).getWidth())), j
											/ (textures.get(index).getWidth()),
									img[index].palette
											.getPixel(img[index].palette
													.getRGB(index2)));
			}
			break;
		case 3:
			for (int j = 0; j < pixelnum; j++) {
				int index2 = textures.get(index).getImage()[j / 2];
				index2 = (index2 >> ((j % 2) << 2)) & 0x0f;
				img[index]
						.setPixel(
								j
										- ((j / (textures.get(index).getWidth())) * (textures
												.get(index).getWidth())), j
										/ (textures.get(index).getWidth()),
								img[index].palette.getPixel(img[index].palette
										.getRGB(index2)));
			}
			break;
		case 4:
			for (int j = 0; j < pixelnum; j++) {
				int index2 = textures.get(index).getImage()[j];
				img[index]
						.setPixel(
								j
										- ((j / (textures.get(index).getWidth())) * (textures
												.get(index).getWidth())), j
										/ (textures.get(index).getWidth()),
								img[index].palette.getPixel(img[index].palette
										.getRGB(index2)));
			}
			break;
		case 5:
			// I'm too lazy to implement this (which is not used in pokemon
			// games
			break;
		case 6:
			for (int j = 0; j < pixelnum; j++) {
				int index2 = textures.get(index).getImage()[j] & 0x7;
				int alpha = textures.get(index).getImage()[j] >> 3;
				alpha *= 8;
				img[index]
						.setPixel(
								j
										- ((j / (textures.get(index).getWidth())) * (textures
												.get(index).getWidth())), j
										/ (textures.get(index).getWidth()),
								img[index].palette.getPixel(img[index].palette
										.getRGB(index2)));
				img[index]
						.setAlpha(
								j
										- ((j / (textures.get(index).getWidth())) * (textures
												.get(index).getWidth())), j
										/ (textures.get(index).getWidth()),
								alpha);
			}
			break;
		case 7:
			for (int j = 0; j < pixelnum; j++) {
				int index2 = textures.get(index).getImage()[j * 2]
						+ (textures.get(index).getImage()[j * 2 + 1] << 8);
				img[index]
						.setPixel(
								j
										- ((j / (textures.get(index).getWidth())) * (textures
												.get(index).getWidth())), j
										/ (textures.get(index).getWidth()),
								img[index].palette.getPixel(new RGB(
										(((index2 >> 0) & 0x1f) << 3),
										(((index2 >> 5) & 0x1f) << 3),
										(((index2 >> 10) & 0x1f) << 3))));
				img[index]
						.setAlpha(
								j
										- ((j / (textures.get(index).getWidth())) * (textures
												.get(index).getWidth())), j
										/ (textures.get(index).getWidth()),
								(((index2 & 0x8000) != 0) ? 0xff : 0));
			}
			break;
		}
		return new Image(Display.getCurrent(), img[index]);
	}

	public void save(FimgEntry fs) {
		try {
			for (int i = 0; i < this.numObjects_3d; i++) {
				List<Byte> pic = new ArrayList<Byte>();
				for (int t = 0; t < this.img[i].data.length; t++)
					pic.add((byte) 0);
				for (int x = 0; x < this.textures.get(i).getWidth(); x++) {
					for (int y = 0; y < this.textures.get(i).getHeight(); y++) {
						pic = setPixelVal(x, y, this.img[i].getPixel(x, y),
								this.textures.get(i).getWidth(),
								bpp[this.textures.get(i).getFormat()], pic);
					}
				}
				byte[] dat = new byte[pic.size()];
				for (int it = 0; it < pic.size(); it++)
					dat[it] = pic.get(it).byteValue();
				this.textures.get(i).setImage(dat);
			}
			ByteBuffer out = ByteBuffer.allocate(size).order(
					ByteOrder.LITTLE_ENDIAN);
			;
			out.put(fs.getEntryData());
			out.position(8);
			out.putInt(this.size);
			out.position(out.position() + 12);
			out.putInt(this.texSize);
			out.position(out.position() + 4);
			out.putShort((short) (this.texDataSize >> 3));
			out.position(out.position() + 6);
			out.putInt(this.texDataOffset);
			out.position(out.position() + 4);
			out.putShort((short) (this.compTexDataSize >> 3));
			out.position(out.position() + 6);
			out.putInt(this.compTexDataOffset);
			out.putInt(this.compTexInfoDataOffset);
			out.position(out.position() + 4);
			out.putInt(this.palDataSize >> 3);
			out.putInt(this.palInfoOffset);
			out.putInt(this.palDataOffset);
			out.position(out.position() + 1);
			out.put((byte) this.numObjects_3d);
			out.putShort((short) this.size_3d);
			out.put(this.unknownBlock1);
			out.putShort((short) this.infoDataSize_3d);
			for (int i = 0; i < this.numObjects_3d; i++) {
				out.putShort((short) (this.textures.get(i).getOffset() >> 3));
				out.putShort((short) this.textures.get(i).getParameter());
				out.put((byte) this.textures.get(i).getWidth2());
				out.put((byte) this.textures.get(i).getUnknown1());
				out.put((byte) this.textures.get(i).getHeight2());
				out.put((byte) this.textures.get(i).getUnknown2());

				int curpos = out.position();
				if (this.textures.get(i).getFormat() != 5)
					out.position(this.textures.get(i).getOffset() + 20
							+ this.texDataOffset);
				else
					out.position(20 + this.compTexDataOffset
							+ this.textures.get(i).getOffset());
				out.put(this.textures.get(i).getImage());
				out.position(curpos);

				if (this.textures.get(i).getFormat() == 5) {
					curpos = out.position();
					out.position(20 + this.compTexInfoDataOffset
							+ this.textures.get(i).getOffset() / 2);
					out.put(this.textures.get(i).getSpData());
					out.position(curpos);
				}

			}

			for (int i = 0; i < this.numObjects_3d; i++) {
				out.put(this.tex_names[i].getBytes());
				for (int j = 0; j < 16 - this.tex_names[i].length(); j++)
					out.put((byte) 0x0);
			}

			out.position(out.position() + 1);
			out.put((byte) this.numObjects_pal);
			out.putShort((short) this.size_pal);
			out.put(this.unknowBlock2);
			out.putShort((short) this.infoDataSize_pal);
			for (int i = 0; i < this.numObjects_pal; i++) {
				out.putShort((short) (this.palettes.get(i).getOffset() >> 3));
				out.putShort((short) this.palettes.get(i).getColor0());
				int curpos = out.position();
				out.position(20 + this.palDataOffset
						+ this.palettes.get(i).getOffset());
				out.put(Palette.fromBGR555(this.palettes.get(i).getPal()));
				out.position(curpos);
			}

			for (int i = 0; i < this.numObjects_pal; i++) {
				out.put(this.pal_names[i].getBytes());
				for (int j = 0; j < 16 - this.pal_names[i].length(); j++)
					out.put((byte) 0x0);
			}

			if (compareArrays(fs.getEntryData(), out.array()) == false)
				fs.setEntryData(out.array());
			else
				System.out.println("ERROR PORCA VACCA!!!");
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean compareArrays(byte[] array1, byte[] array2) {
		boolean b = true;
		if (array1 != null && array2 != null) {
			if (array1.length != array2.length)
				b = false;
			else
				for (int i = 0; i < array2.length; i++) {
					if (array2[i] != array1[i]) {
						b = false;
					}
				}
		} else {
			b = false;
		}
		return b;
	}

	private static List<Byte> setPixelVal(int x, int y, int v, int width,
			int bpp, List<Byte> pic) {
		List<Byte> pic2 = pic;
		int i = x + y * width;
		if (bpp == 8) {
			pic2.add((byte) v);
			return pic2;
		} else if (bpp == 4) {
			int res = pic2.get(i / 2);
			res &= ~(0xF << ((i % 2) * 4));
			res |= (v & 0xF) << ((i % 2) * 4);
			pic2.set(i / 2, (byte) res);
		}

		else if (bpp == 2) {
			int res = pic2.get(i / 4);
			res &= ~(0xF << ((i % 4) * 2));
			res |= (v & 0xF) << ((i % 4) * 2);
			pic2.set(i / 4, (byte) res);
		}
		return pic2;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getTexSize() {
		return texSize;
	}

	public void setTexSize(int texSize) {
		this.texSize = texSize;
	}

	public int getTexDataSize() {
		return texDataSize;
	}

	public void setTexDataSize(int texDataSize) {
		this.texDataSize = texDataSize;
	}

	public int getTexDataOffset() {
		return texDataOffset;
	}

	public void setTexDataOffset(int texDataOffset) {
		this.texDataOffset = texDataOffset;
	}

	public int getCompTexDataSize() {
		return compTexDataSize;
	}

	public void setCompTexDataSize(int compTexDataSize) {
		this.compTexDataSize = compTexDataSize;
	}

	public int getCompTexDataOffset() {
		return compTexDataOffset;
	}

	public void setCompTexDataOffset(int compTexDataOffset) {
		this.compTexDataOffset = compTexDataOffset;
	}

	public int getComTexInfoDataOffset() {
		return compTexInfoDataOffset;
	}

	public void setComTexInfoDataOffset(int comTexInfoDataOffset) {
		this.compTexInfoDataOffset = comTexInfoDataOffset;
	}

	public int getPalDataSize() {
		return palDataSize;
	}

	public void setPalDataSize(int palDataSize) {
		this.palDataSize = palDataSize;
	}

	public int getPalInfoOffset() {
		return palInfoOffset;
	}

	public void setPalInfoOffset(int palInfoOffset) {
		this.palInfoOffset = palInfoOffset;
	}

	public int getPalDataOffset() {
		return palDataOffset;
	}

	public void setPalDataOffset(int palDataOffset) {
		this.palDataOffset = palDataOffset;
	}

	public int getNumObjects_3d() {
		return numObjects_3d;
	}

	public void setNumObjects_3d(int numObjects_3d) {
		this.numObjects_3d = numObjects_3d;
	}

	public int getSize_3d() {
		return size_3d;
	}

	public void setSize_3d(int size_3d) {
		this.size_3d = size_3d;
	}

	public int getInfoDataSize_3d() {
		return infoDataSize_3d;
	}

	public void setInfoDataSize_3d(int infoDataSize_3d) {
		this.infoDataSize_3d = infoDataSize_3d;
	}

	public List<Texture> getTextures() {
		return textures;
	}

	public void setTextures(List<Texture> textures) {
		this.textures = textures;
	}

	public int[] getBpp() {
		return bpp;
	}

	public void setBpp(int[] bpp) {
		this.bpp = bpp;
	}

	public String[] getNames() {
		return tex_names;
	}

	public void setNames(String[] names) {
		this.tex_names = names;
	}

	public String[] getTex_names() {
		return tex_names;
	}

	public void setTex_names(String[] tex_names) {
		this.tex_names = tex_names;
	}

	public int getNumObjects_pal() {
		return numObjects_pal;
	}

	public void setNumObjects_pal(int numObjects_pal) {
		this.numObjects_pal = numObjects_pal;
	}

	public int getSize_pal() {
		return size_pal;
	}

	public void setSize_pal(int size_pal) {
		this.size_pal = size_pal;
	}

	public int getInfoDataSize_pal() {
		return infoDataSize_pal;
	}

	public void setInfoDataSize_pal(int infoDataSize_pal) {
		this.infoDataSize_pal = infoDataSize_pal;
	}

	public List<Palette> getPalettes() {
		return palettes;
	}

	public void setPalettes(List<Palette> palettes) {
		this.palettes = palettes;
	}

	public String[] getPal_names() {
		return pal_names;
	}

	public void setPal_names(String[] pal_names) {
		this.pal_names = pal_names;
	}
}
