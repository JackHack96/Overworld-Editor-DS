package gui;

import java.io.File;
import java.io.IOException;

import narc.Narc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import btx.Nsbtx;

public class MainWindow {
	private static Narc nrc;
	private static Nsbtx tmp;

	private static int prev_x, prev_y, prev_zoom = 1, paint_zoom = 4;

	private static Color c;
	private static ImageData current_image;

	private Menu menu;
	private MenuItem mntmfile;
	private Menu menu_1;
	private MenuItem mntmopenNarc;
	private MenuItem mntmSaveNarc;
	private MenuItem mntmexit;
	private MenuItem mntmhelp;
	private Menu menu_2;
	private MenuItem mntmabout;
	private Canvas cnvSpritePreview;
	private Canvas cnvPaintArea;
	private Canvas cnvPalette;
	private Canvas cnvSelectedColor;
	private Group grpSpriteNavigator;
	private Label lblIndex;
	private Spinner spnSpriteIndex;
	private Label lblFrame;
	private Spinner spnSpriteFrame;
	private Label lblPalette_1;
	private Spinner spnSpritePalette;
	private Group grpSpriteInfo;
	private Label lblWidth;
	private Label lblHeight;
	private Label lblDepth;
	private Label lblSpriteWidth;
	private Label lblSpriteHeight;
	private Label lblSpriteDepth;
	private Label lblCurrentSpriteName;
	private Label lblSpriteName;
	private MenuItem mntmoptions;
	private Menu menu_3;
	private MenuItem mntmShowgrid;
	private Scale sclPrevZoom;
	private Group grpPreviewZoom;
	private Label lblPrevZoom;
	private Group grpSpritePreview;
	private Group grpPaintAreaZoom;
	private Scale sclPaintZoom;
	private Label lblPaintZoom;
	private Group grpPaintArea;
	private Group grpSelectedColor;
	private Group grpPalette;
	private Button btnSaveCurrentSprite;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		final Shell shlOverworldEditorDs = new Shell();
		shlOverworldEditorDs.setSize(697, 624);
		shlOverworldEditorDs.setText("Overworld Editor DS");
		shlOverworldEditorDs.setLayout(new GridLayout(3, false));

		menu = new Menu(shlOverworldEditorDs, SWT.BAR);
		shlOverworldEditorDs.setMenuBar(menu);

		mntmfile = new MenuItem(menu, SWT.CASCADE);
		mntmfile.setText("&File");

		menu_1 = new Menu(mntmfile);
		mntmfile.setMenu(menu_1);

		mntmopenNarc = new MenuItem(menu_1, SWT.NONE);
		mntmopenNarc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog dlgOpen = new FileDialog(shlOverworldEditorDs
						.getShell(), SWT.OPEN);
				dlgOpen.setText("Select narc...");
				String[] filter_ext = { "*.narc" };
				String[] filter_name = { "NARC Archive (*.narc)" };
				dlgOpen.setFilterNames(filter_name);
				dlgOpen.setFilterExtensions(filter_ext);
				if (dlgOpen.open() != null) {
					try {
						nrc = new Narc(new File(dlgOpen.getFilterPath(),
								dlgOpen.getFileName()).getAbsolutePath());
						tmp = new Nsbtx(nrc.getFimgEntry().get(0));
						spnSpriteIndex.setMaximum((int) nrc.getnumEntries() - 1);
						spnSpriteFrame.setMaximum(tmp.getNumObjects_3d() - 1);
						spnSpritePalette.setMaximum(tmp.getNumObjects_pal() - 1);
						prev_x = 0;
						prev_y = 0;
						c = tmp.getPalettes()
								.get(spnSpritePalette.getSelection()).getPal()[0];

						cnvSpritePreview.redraw();
						cnvSpritePreview.update();
						cnvPaintArea.redraw();
						cnvPaintArea.update();
						cnvPalette.redraw();
						cnvPalette.update();
						cnvSelectedColor.redraw();
						cnvSelectedColor.update();

						lblSpriteWidth.setText(String.valueOf(tmp.getTextures()
								.get(0).getWidth()));
						lblSpriteHeight.setText(String.valueOf(tmp
								.getTextures().get(0).getHeight()));
						lblSpriteDepth.setText(String.valueOf(tmp.getTextures()
								.get(0).getDepth())
								+ " ("
								+ tmp.getPalettes().get(0).getPal().length
								+ " Colors)");
						lblSpriteName.setText(String.valueOf(tmp.getTex_names()[0]));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		mntmopenNarc.setText("&Open NARC...");
		mntmopenNarc.setAccelerator(SWT.CTRL + 'O');

		mntmSaveNarc = new MenuItem(menu_1, SWT.NONE);
		mntmSaveNarc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog dlgSave = new FileDialog(shlOverworldEditorDs
						.getShell(), SWT.SAVE);
				dlgSave.setText("Save narc...");
				String[] filter_ext = { "*.narc" };
				String[] filter_name = { "NARC Archive (*.narc)" };
				dlgSave.setFilterNames(filter_name);
				dlgSave.setFilterExtensions(filter_ext);
				if (dlgSave.open() != null)
					try {
						nrc.writeNarc(new File(dlgSave.getFilterPath(), dlgSave
								.getFileName()).getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		});
		mntmSaveNarc.setText("Save NARC...");

		new MenuItem(menu_1, SWT.SEPARATOR);

		mntmexit = new MenuItem(menu_1, SWT.NONE);
		mntmexit.setText("&Exit");

		mntmoptions = new MenuItem(menu, SWT.CASCADE);
		mntmoptions.setText("&Options");

		menu_3 = new Menu(mntmoptions);
		mntmoptions.setMenu(menu_3);

		mntmShowgrid = new MenuItem(menu_3, SWT.CHECK);
		mntmShowgrid.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				cnvPaintArea.redraw();
				cnvPaintArea.update();
				cnvPalette.redraw();
				cnvPalette.update();
			}
		});
		mntmShowgrid.setSelection(true);
		mntmShowgrid.setText("Show &Grid");
		mntmShowgrid.setAccelerator(SWT.CTRL + 'G');

		mntmhelp = new MenuItem(menu, SWT.CASCADE);
		mntmhelp.setText("&Help");

		menu_2 = new Menu(mntmhelp);
		mntmhelp.setMenu(menu_2);

		mntmabout = new MenuItem(menu_2, SWT.NONE);
		mntmabout.setText("&About");

		grpSpritePreview = new Group(shlOverworldEditorDs, SWT.NONE);
		grpSpritePreview.setLayout(new GridLayout(1, false));
		grpSpritePreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 2));
		grpSpritePreview.setText("Sprite Preview");

		cnvSpritePreview = new Canvas(grpSpritePreview, SWT.DOUBLE_BUFFERED);
		cnvSpritePreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		cnvSpritePreview.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				if (arg0.button == 1)
					if (nrc != null) {
						if (arg0.x > 0
								&& arg0.x < tmp.getTextures()
										.get(spnSpriteFrame.getSelection())
										.getWidth()
										* prev_zoom / 2) {
							if (arg0.y > 0
									&& arg0.y < tmp.getTextures()
											.get(spnSpriteFrame.getSelection())
											.getHeight()
											* prev_zoom / 2) {
								prev_x = 0;
								prev_y = 0;
							} else if (arg0.y > tmp.getTextures()
									.get(spnSpriteFrame.getSelection())
									.getHeight()
									* prev_zoom / 2
									&& arg0.y < tmp.getTextures()
											.get(spnSpriteFrame.getSelection())
											.getHeight()
											* prev_zoom) {
								prev_x = 0;
								prev_y = 1;
							}
						} else if (arg0.x > tmp.getTextures()
								.get(spnSpriteFrame.getSelection()).getWidth()
								* prev_zoom / 2
								&& arg0.x < tmp.getTextures()
										.get(spnSpriteFrame.getSelection())
										.getWidth()
										* prev_zoom) {
							if (arg0.y > 0
									&& arg0.y < tmp.getTextures()
											.get(spnSpriteFrame.getSelection())
											.getHeight()
											* prev_zoom / 2) {
								prev_x = 1;
								prev_y = 0;
							} else if (arg0.y > tmp.getTextures()
									.get(spnSpriteFrame.getSelection())
									.getHeight()
									* prev_zoom / 2
									&& arg0.y < tmp.getTextures()
											.get(spnSpriteFrame.getSelection())
											.getHeight()
											* prev_zoom) {
								prev_x = 1;
								prev_y = 1;
							}
						}
						cnvSpritePreview.redraw();
						cnvSpritePreview.update();
						cnvPaintArea.redraw();
						cnvPaintArea.update();
					}
			}
		});
		cnvSpritePreview.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setInterpolation(SWT.NONE);
				if (nrc != null) {
					drawSpritePreview(e.gc, spnSpriteFrame.getSelection(),
							spnSpritePalette.getSelection(), prev_x, prev_y,
							prev_zoom);
				}
			}
		});

		grpPaintArea = new Group(shlOverworldEditorDs, SWT.NONE);
		grpPaintArea.setLayout(new GridLayout(1, false));
		GridData gd_grpPaintArea = new GridData(SWT.FILL, SWT.FILL, true, true,
				2, 2);
		gd_grpPaintArea.heightHint = 224;
		grpPaintArea.setLayoutData(gd_grpPaintArea);
		grpPaintArea.setText("Paint Area");

		cnvPaintArea = new Canvas(grpPaintArea, SWT.NONE);
		cnvPaintArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		cnvPaintArea.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if (e.stateMask == (SWT.BUTTON_MASK & SWT.BUTTON1)) {
					if (prev_x == 0 && prev_y == 0) {
						current_image.setPixel((e.x / paint_zoom) / 2,
								(e.y / paint_zoom) / 2, tmp.img[spnSpriteFrame
										.getSelection()].palette.getPixel(c
										.getRGB()));
					} else if (prev_x == 0 && prev_y == 1) {
						current_image.setPixel(
								(e.x / paint_zoom) / 2,
								(e.y / paint_zoom)
										/ 2
										+ tmp.getTextures()
												.get(spnSpriteFrame
														.getSelection())
												.getHeight() / 2,
								tmp.img[spnSpriteFrame.getSelection()].palette
										.getPixel(c.getRGB()));
					} else if (prev_x == 1 && prev_y == 0) {
						current_image.setPixel(
								(e.x / paint_zoom)
										/ 2
										+ tmp.getTextures()
												.get(spnSpriteFrame
														.getSelection())
												.getWidth() / 2,
								(e.y / paint_zoom) / 2, tmp.img[spnSpriteFrame
										.getSelection()].palette.getPixel(c
										.getRGB()));
					} else {
						current_image.setPixel(
								(e.x / paint_zoom)
										/ 2
										+ tmp.getTextures()
												.get(spnSpriteFrame
														.getSelection())
												.getWidth() / 2,
								(e.y / paint_zoom)
										/ 2
										+ tmp.getTextures()
												.get(spnSpriteFrame
														.getSelection())
												.getHeight() / 2,
								tmp.img[spnSpriteFrame.getSelection()].palette
										.getPixel(c.getRGB()));
					}
					tmp.img[spnSpriteFrame.getSelection()] = current_image;
					for (int i = 0; i < tmp.img.length; i++) {
						for (int i2 = 0; i2 < tmp.img.length; i2++)
							if (tmp.getTextures().get(i).getOffset() == tmp
									.getTextures().get(i2).getOffset())
								tmp.img[i2] = tmp.img[i];
					}

					cnvPaintArea.redraw();
					cnvPaintArea.update();
					cnvSpritePreview.redraw();
					cnvSpritePreview.update();
				}
			}
		});
		cnvPaintArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					if (prev_x == 0 && prev_y == 0) {
						current_image.setPixel((e.x / paint_zoom) / 2,
								(e.y / paint_zoom) / 2, tmp.img[spnSpriteFrame
										.getSelection()].palette.getPixel(c
										.getRGB()));
					} else if (prev_x == 0 && prev_y == 1) {
						current_image.setPixel(
								(e.x / paint_zoom) / 2,
								(e.y / paint_zoom)
										/ 2
										+ tmp.getTextures()
												.get(spnSpriteFrame
														.getSelection())
												.getHeight() / 2,
								tmp.img[spnSpriteFrame.getSelection()].palette
										.getPixel(c.getRGB()));
					} else if (prev_x == 1 && prev_y == 0) {
						current_image.setPixel(
								(e.x / paint_zoom)
										/ 2
										+ tmp.getTextures()
												.get(spnSpriteFrame
														.getSelection())
												.getWidth() / 2,
								(e.y / paint_zoom) / 2, tmp.img[spnSpriteFrame
										.getSelection()].palette.getPixel(c
										.getRGB()));
					} else {
						current_image.setPixel(
								(e.x / paint_zoom)
										/ 2
										+ tmp.getTextures()
												.get(spnSpriteFrame
														.getSelection())
												.getWidth() / 2,
								(e.y / paint_zoom)
										/ 2
										+ tmp.getTextures()
												.get(spnSpriteFrame
														.getSelection())
												.getHeight() / 2,
								tmp.img[spnSpriteFrame.getSelection()].palette
										.getPixel(c.getRGB()));
					}
					tmp.img[spnSpriteFrame.getSelection()] = current_image;

					// A little workaround for same-texture-offset bugs
					for (int i = 0; i < tmp.img.length; i++) {
						for (int i2 = 0; i2 < tmp.img.length; i2++)
							if (tmp.getTextures().get(i).getOffset() == tmp
									.getTextures().get(i2).getOffset())
								tmp.img[i2] = tmp.img[i];
					}

					cnvPaintArea.redraw();
					cnvPaintArea.update();
					cnvSpritePreview.redraw();
					cnvSpritePreview.update();
				}
			}
		});
		cnvPaintArea.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setInterpolation(SWT.NONE);
				if (nrc != null) {
					if (mntmShowgrid.getSelection())
						drawPaintArea(e.gc, spnSpriteFrame.getSelection(),
								spnSpritePalette.getSelection(), true,
								paint_zoom);
					else
						drawPaintArea(e.gc, spnSpriteFrame.getSelection(),
								spnSpritePalette.getSelection(), false,
								paint_zoom);
				}
			}
		});

		grpPreviewZoom = new Group(shlOverworldEditorDs, SWT.NONE);
		grpPreviewZoom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		grpPreviewZoom.setText("Preview Zoom");
		grpPreviewZoom.setLayout(new GridLayout(2, false));

		sclPrevZoom = new Scale(grpPreviewZoom, SWT.NONE);
		sclPrevZoom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		sclPrevZoom.setPageIncrement(1);
		sclPrevZoom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				prev_zoom = sclPrevZoom.getSelection();
				lblPrevZoom.setText(prev_zoom + "x");
				cnvSpritePreview.redraw();
				cnvSpritePreview.update();
			}
		});
		sclPrevZoom.setMaximum(8);
		sclPrevZoom.setMinimum(1);
		sclPrevZoom.setSelection(1);

		lblPrevZoom = new Label(grpPreviewZoom, SWT.NONE);
		lblPrevZoom.setText("1x");

		grpPaintAreaZoom = new Group(shlOverworldEditorDs, SWT.NONE);
		grpPaintAreaZoom.setLayout(new GridLayout(2, false));
		grpPaintAreaZoom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 2, 1));
		grpPaintAreaZoom.setText("Paint Area Zoom");

		sclPaintZoom = new Scale(grpPaintAreaZoom, SWT.NONE);
		sclPaintZoom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				paint_zoom = sclPaintZoom.getSelection();
				lblPaintZoom.setText(paint_zoom + "x");
				cnvPaintArea.redraw();
				cnvPaintArea.update();
			}
		});
		sclPaintZoom.setPageIncrement(1);
		sclPaintZoom.setMaximum(16);
		sclPaintZoom.setMinimum(1);
		sclPaintZoom.setSelection(4);
		sclPaintZoom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblPaintZoom = new Label(grpPaintAreaZoom, SWT.NONE);
		GridData gd_lblPaintZoom = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_lblPaintZoom.heightHint = 21;
		gd_lblPaintZoom.widthHint = 18;
		lblPaintZoom.setLayoutData(gd_lblPaintZoom);
		lblPaintZoom.setText("4x");

		grpSpriteNavigator = new Group(shlOverworldEditorDs, SWT.NONE);
		grpSpriteNavigator.setLayout(new GridLayout(2, false));
		GridData gd_grpSpriteNavigator = new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1);
		gd_grpSpriteNavigator.widthHint = 301;
		grpSpriteNavigator.setLayoutData(gd_grpSpriteNavigator);
		grpSpriteNavigator.setText("Sprite Navigator");

		lblIndex = new Label(grpSpriteNavigator, SWT.NONE);
		lblIndex.setText("Index:");

		spnSpriteIndex = new Spinner(grpSpriteNavigator, SWT.BORDER);
		GridData gd_spnSpriteIndex = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_spnSpriteIndex.widthHint = 60;
		spnSpriteIndex.setLayoutData(gd_spnSpriteIndex);
		spnSpriteIndex.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					tmp = new Nsbtx(nrc.getFimgEntry().get(
							spnSpriteIndex.getSelection()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				spnSpriteFrame.setMaximum(tmp.getNumObjects_3d() - 1);
				spnSpritePalette.setMaximum(tmp.getNumObjects_pal() - 1);
				lblSpriteWidth.setText(String.valueOf(tmp.getTextures().get(0)
						.getWidth()));
				lblSpriteHeight.setText(String.valueOf(tmp.getTextures().get(0)
						.getHeight()));
				lblSpriteDepth.setText(String.valueOf(tmp.getTextures().get(0)
						.getDepth())
						+ " ("
						+ tmp.getPalettes().get(0).getPal().length
						+ " Colors)");
				lblSpriteName.setText(String.valueOf(tmp.getTex_names()[0]));
				tmp.img[0] = tmp.getImage(0, 0).getImageData();

				cnvSpritePreview.redraw();
				cnvSpritePreview.update();
				cnvPaintArea.redraw();
				cnvPaintArea.update();
				cnvPalette.redraw();
				cnvPalette.update();

				spnSpriteFrame.setSelection(0);
				spnSpritePalette.setSelection(0);
			}
		});

		lblFrame = new Label(grpSpriteNavigator, SWT.NONE);
		lblFrame.setText("Frame:");

		spnSpriteFrame = new Spinner(grpSpriteNavigator, SWT.BORDER);
		spnSpriteFrame.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		spnSpriteFrame.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				cnvSpritePreview.redraw();
				cnvSpritePreview.update();
				cnvPaintArea.redraw();
				cnvPaintArea.update();
				lblSpriteWidth.setText(String.valueOf(tmp.getTextures()
						.get(spnSpriteFrame.getSelection()).getWidth()));
				lblSpriteHeight.setText(String.valueOf(tmp.getTextures()
						.get(spnSpriteFrame.getSelection()).getHeight()));
				lblSpriteDepth.setText(String.valueOf(tmp.getTextures().get(0)
						.getDepth())
						+ " ("
						+ tmp.getPalettes()
								.get(spnSpritePalette.getSelection()).getPal().length
						+ " Colors)");
				lblSpriteName.setText(String.valueOf(tmp.getTex_names()[spnSpriteFrame
						.getSelection()]));
			}
		});

		lblPalette_1 = new Label(grpSpriteNavigator, SWT.NONE);
		lblPalette_1.setText("Palette:");

		spnSpritePalette = new Spinner(grpSpriteNavigator, SWT.BORDER);
		spnSpritePalette.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 1, 1));
		spnSpritePalette.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				cnvSpritePreview.redraw();
				cnvSpritePreview.update();
				cnvPaintArea.redraw();
				cnvPaintArea.update();
				cnvPalette.redraw();
				cnvPalette.update();
			}
		});

		grpSpriteInfo = new Group(shlOverworldEditorDs, SWT.NONE);
		grpSpriteInfo.setLayout(new GridLayout(2, false));
		grpSpriteInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false, 2, 1));
		grpSpriteInfo.setText("Sprite Info");

		lblWidth = new Label(grpSpriteInfo, SWT.NONE);
		lblWidth.setText("Width:");

		lblSpriteWidth = new Label(grpSpriteInfo, SWT.NONE);
		lblSpriteWidth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblHeight = new Label(grpSpriteInfo, SWT.NONE);
		lblHeight.setText("Height:");

		lblSpriteHeight = new Label(grpSpriteInfo, SWT.NONE);
		lblSpriteHeight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblDepth = new Label(grpSpriteInfo, SWT.NONE);
		lblDepth.setText("Depth:");

		lblSpriteDepth = new Label(grpSpriteInfo, SWT.NONE);
		lblSpriteDepth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		lblCurrentSpriteName = new Label(grpSpriteInfo, SWT.NONE);
		lblCurrentSpriteName.setText("Current Sprite Name:");

		lblSpriteName = new Label(grpSpriteInfo, SWT.NONE);
		lblSpriteName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		grpPalette = new Group(shlOverworldEditorDs, SWT.NONE);
		FillLayout fl_grpPalette = new FillLayout(SWT.HORIZONTAL);
		fl_grpPalette.marginWidth = 5;
		fl_grpPalette.marginHeight = 1;
		grpPalette.setLayout(fl_grpPalette);
		grpPalette.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				2, 2));
		grpPalette.setText("Palette");

		cnvPalette = new Canvas(grpPalette, SWT.NONE);
		cnvPalette.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {
					c = tmp.getPalettes().get(spnSpritePalette.getSelection())
							.getPal()[e.x / 16];
					cnvSelectedColor.redraw();
					cnvSelectedColor.update();
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				ColorDialog dlgColor = new ColorDialog(shlOverworldEditorDs);
				dlgColor.setRGB(tmp.getPalettes()
						.get(spnSpritePalette.getSelection()).getPalRGBs()[arg0.x / 16]);
				if (dlgColor.open() != null) {
					tmp.getPalettes().get(spnSpritePalette.getSelection())
							.getPal()[arg0.x / 16] = new Color(Display
							.getCurrent(), dlgColor.getRGB());
					cnvPalette.redraw();
					cnvPalette.update();
					cnvSpritePreview.redraw();
					cnvSpritePreview.update();
					cnvPaintArea.redraw();
					cnvPaintArea.update();
				}
			}
		});
		cnvPalette.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				if (nrc != null)
					drawPalette(arg0.gc, spnSpriteFrame.getSelection(),
							spnSpritePalette.getSelection(),
							mntmShowgrid.getSelection());
			}
		});

		grpSelectedColor = new Group(shlOverworldEditorDs, SWT.NONE);
		FillLayout fl_grpSelectedColor = new FillLayout(SWT.HORIZONTAL);
		fl_grpSelectedColor.marginHeight = 1;
		fl_grpSelectedColor.marginWidth = 5;
		grpSelectedColor.setLayout(fl_grpSelectedColor);
		GridData gd_grpSelectedColor = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 2);
		gd_grpSelectedColor.widthHint = 103;
		grpSelectedColor.setLayoutData(gd_grpSelectedColor);
		grpSelectedColor.setText("Selected Color");

		cnvSelectedColor = new Canvas(grpSelectedColor, SWT.NONE);

		btnSaveCurrentSprite = new Button(shlOverworldEditorDs, SWT.NONE);
		btnSaveCurrentSprite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				tmp.save(nrc.getFimgEntry().get(spnSpriteIndex.getSelection()));
			}
		});
		btnSaveCurrentSprite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				false, false, 3, 1));
		btnSaveCurrentSprite.setText("Save Current Sprite");
		cnvSelectedColor.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (nrc != null)
					drawSelectedColor(e.gc, c);
			}
		});

		shlOverworldEditorDs.open();
		shlOverworldEditorDs.layout();
		while (!shlOverworldEditorDs.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private static void drawSpritePreview(GC gc, int s_index, int s_pal, int x,
			int y, int zoom) {
		try {
			current_image = tmp.img[s_index];
			gc.drawImage(new Image(Display.getCurrent(), current_image), 0, 0,
					tmp.getTextures().get(s_index).getWidth(), tmp
							.getTextures().get(s_index).getHeight(), 0, 0, tmp
							.getTextures().get(s_index).getWidth()
							* zoom, tmp.getTextures().get(s_index).getHeight()
							* zoom);
			gc.setForeground(new Color(Display.getCurrent(), 255, 0, 0));
			gc.drawRectangle(tmp.getTextures().get(s_index).getWidth() * zoom
					/ 2 * x, tmp.getTextures().get(s_index).getHeight() * zoom
					/ 2 * y, tmp.getTextures().get(s_index).getWidth() * zoom
					/ 2 - 1, tmp.getTextures().get(s_index).getHeight() * zoom
					/ 2 - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void drawPaintArea(GC gc, int s_index, int s_pal,
			boolean grid, int zoom) {
		try {
			if (grid) {
				if (prev_x == 0 && prev_y == 0)
					gc.drawImage(
							new Image(Display.getCurrent(), current_image), 0,
							0, tmp.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, 0,
							0,
							tmp.getTextures().get(s_index).getWidth() * zoom,
							tmp.getTextures().get(s_index).getHeight() * zoom);
				else if (prev_x == 0 && prev_y == 1)
					gc.drawImage(
							new Image(Display.getCurrent(), current_image), 0,
							tmp.getTextures().get(s_index).getHeight() / 2, tmp
									.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, 0,
							0,
							tmp.getTextures().get(s_index).getWidth() * zoom,
							tmp.getTextures().get(s_index).getHeight() * zoom);
				else if (prev_x == 1 && prev_y == 0)
					gc.drawImage(
							new Image(Display.getCurrent(), current_image), tmp
									.getTextures().get(s_index).getWidth() / 2,
							0, tmp.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, 0,
							0,
							tmp.getTextures().get(s_index).getWidth() * zoom,
							tmp.getTextures().get(s_index).getHeight() * zoom);
				else
					gc.drawImage(
							new Image(Display.getCurrent(), current_image), tmp
									.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, tmp
									.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, 0,
							0,
							tmp.getTextures().get(s_index).getWidth() * zoom,
							tmp.getTextures().get(s_index).getHeight() * zoom);

				for (int y = 0; y <= tmp.getTextures().get(s_index).getHeight()
						* zoom; y += zoom * 2)
					gc.drawLine(0, y, tmp.getTextures().get(s_index)
							.getHeight()
							* zoom, y);
				for (int x = 0; x <= tmp.getTextures().get(s_index).getWidth()
						* zoom; x += zoom * 2)
					gc.drawLine(x, 0, x, tmp.getTextures().get(s_index)
							.getWidth()
							* zoom);
			} else {
				if (prev_x == 0 && prev_y == 0)
					gc.drawImage(
							new Image(Display.getCurrent(), current_image), 0,
							0, tmp.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, 0,
							0,
							tmp.getTextures().get(s_index).getWidth() * zoom,
							tmp.getTextures().get(s_index).getHeight() * zoom);
				else if (prev_x == 0 && prev_y == 1)
					gc.drawImage(
							new Image(Display.getCurrent(), current_image), 0,
							tmp.getTextures().get(s_index).getHeight() / 2, tmp
									.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, 0,
							0,
							tmp.getTextures().get(s_index).getWidth() * zoom,
							tmp.getTextures().get(s_index).getHeight() * zoom);
				else if (prev_x == 1 && prev_y == 0)
					gc.drawImage(
							new Image(Display.getCurrent(), current_image), tmp
									.getTextures().get(s_index).getWidth() / 2,
							0, tmp.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, 0,
							0,
							tmp.getTextures().get(s_index).getWidth() * zoom,
							tmp.getTextures().get(s_index).getHeight() * zoom);
				else
					gc.drawImage(
							new Image(Display.getCurrent(), current_image), tmp
									.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, tmp
									.getTextures().get(s_index).getWidth() / 2,
							tmp.getTextures().get(s_index).getHeight() / 2, 0,
							0,
							tmp.getTextures().get(s_index).getWidth() * zoom,
							tmp.getTextures().get(s_index).getHeight() * zoom);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void drawPalette(GC gc, int s_index, int s_pal, boolean grid) {
		try {
			if (grid) {
				ImageData img = new ImageData(tmp.getPalettes().get(s_pal)
						.getPal().length * 16, 16, tmp.getTextures()
						.get(s_index).getDepth(), new PaletteData(tmp
						.getPalettes().get(s_pal).getPalRGBs()));
				for (int index = 0; index < tmp.getPalettes().get(s_pal)
						.getPal().length; index++)
					for (int x = 0; x < 16; x++)
						for (int y = 0; y < 16; y++)
							img.setPixel(x + (index * 16), y, img.palette
									.getPixel(img.palette.getRGB(index)));
				gc.drawImage(new Image(Display.getCurrent(), img), 0, 0);
				for (int y = 0; y <= 16; y += 16)
					gc.drawLine(0, y,
							tmp.getPalettes().get(s_pal).getPal().length * 16,
							y);
				for (int x = 0; x <= tmp.getPalettes().get(s_pal).getPal().length * 16; x += 16)
					gc.drawLine(x, 0, x, 16);
			} else {
				ImageData img = new ImageData(tmp.getPalettes().get(s_pal)
						.getPal().length * 16, 16, tmp.getTextures()
						.get(s_index).getDepth(), new PaletteData(tmp
						.getPalettes().get(s_pal).getPalRGBs()));
				for (int index = 0; index < tmp.getPalettes().get(s_pal)
						.getPal().length; index++)
					for (int x = 0; x < 16; x++)
						for (int y = 0; y < 16; y++)
							img.setPixel(x + (index * 16), y, img.palette
									.getPixel(img.palette.getRGB(index)));
				gc.drawImage(new Image(Display.getCurrent(), img), 0, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void drawSelectedColor(GC gc, Color c) {
		try {
			gc.setBackground(c);
			gc.fillRectangle(0, 0, 90, 60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
