package btx;

public class Texture {
	private int offset, palID, format, height, width;
	private int repeatX, repeatY, flipX, flipY, color0, coord_transf,
			parameter;
	private int depth, compDataStart;
	private byte[] image, spData;
	private int Width, Height, Unknown1, Unknown2;

	public void setWidth2(int w) {
		Width = w;
	}

	public int getWidth2() {
		return Width;
	}

	public void setHeight2(int h) {
		Height = h;
	}

	public int getHeight2() {
		return Height;
	}

	public byte[] getSpData() {
		return spData;
	}

	public void setSpData(byte[] spData) {
		this.spData = spData;
	}

	public int getUnknown1() {
		return Unknown1;
	}

	public void setUnknown1(int unknown1) {
		Unknown1 = unknown1;
	}

	public int getUnknown2() {
		return Unknown2;
	}

	public void setUnknown2(int unknown2) {
		Unknown2 = unknown2;
	}

	public int getRepeatX() {
		return repeatX;
	}

	public void setRepeatX(int repeatX) {
		this.repeatX = repeatX;
	}

	public int getRepeatY() {
		return repeatY;
	}

	public void setRepeatY(int repeatY) {
		this.repeatY = repeatY;
	}

	public int getFlipX() {
		return flipX;
	}

	public void setFlipX(int flipX) {
		this.flipX = flipX;
	}

	public int getFlipY() {
		return flipY;
	}

	public void setFlipY(int flipY) {
		this.flipY = flipY;
	}

	public int getColor0() {
		return color0;
	}

	public void setColor0(int color0) {
		this.color0 = color0;
	}

	public int getCoord_transf() {
		return coord_transf;
	}

	public void setCoord_transf(int coord_transf) {
		this.coord_transf = coord_transf;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getCompDataStart() {
		return compDataStart;
	}

	public void setCompDataStart(int compDataStart) {
		this.compDataStart = compDataStart;
	}

	private int palOffset;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getPalID() {
		return palID;
	}

	public void setPalID(int palID) {
		this.palID = palID;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getPalOffset() {
		return palOffset;
	}

	public void setPalOffset(int palOffset) {
		this.palOffset = palOffset;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public int getParameter() {
		return parameter;
	}

	public void setParameter(int parameter) {
		this.parameter = parameter;
	}
}
