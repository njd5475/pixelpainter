package com.pixel.painter.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.pixel.painter.animation.PreviewAnimator;
import com.pixel.painter.brushes.Brush;
import com.pixel.painter.brushes.BrushChangeListener;
import com.pixel.painter.brushes.EraseBrush;
import com.pixel.painter.brushes.FillMode;
import com.pixel.painter.controller.ImageController;
import com.pixel.painter.controller.SpriteController;
import com.pixel.painter.model.ApplicationSettings;
import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.palettes.PaletteListener;
import com.pixel.painter.palettes.PaletteManager;
import com.pixel.painter.settings.Json;
import com.pixel.painter.settings.Settings;
import com.pixel.painter.ui.dialog.NewFilePanel;
import com.pixel.painter.ui.dialog.NewImagePanel;
import com.pixel.painter.ui.overlays.ColorBarOverlay;
import com.pixel.painter.ui.overlays.ColorHoverOverlay;
import com.pixel.painter.ui.overlays.Overlay;
import com.pixel.painter.ui.overlays.SpriteFrameBarOverlay;

/**
 * @author pbywebapp
 * 
 */
public class PixelPainter extends JPanel
		implements PaletteListener, BrushChangeListener {

	private static Font fntAwesome;

	static {
		try {
			// JFrame.setDefaultLookAndFeelDecorated(true);
			// UIManager
			// .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			// UIManager
			// .setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");

			LookAndFeelInfo[] installedLookAndFeels = UIManager
					.getInstalledLookAndFeels();
			for (LookAndFeelInfo laf : installedLookAndFeels) {
				System.out.println(laf.getClassName());
			}
		} catch (Exception e) {
		}
		(new Thread() {
			public void run() {
				// windows file chooser takes forever on first load.
				JFileChooser jfc = new JFileChooser();
				getFontAwesome();
				Settings.getInstance();
			}
		}).start();
	}

	public enum FILE_CHOOSER {
		OPEN_IMAGE, SAVE_IMAGE
	};

	/**
	 * 
	 */
	private static final long							serialVersionUID	= -1341074200545654219L;

	private static final Color						GRID_COLOR				= Color.black;

	private static final String						version						= "v0.2";

	private static boolean								gridOn						= true;
	private static boolean								preview						= true;
	private static boolean								animation;
	private static JFrame									frame;
	private static JToolBar								tools;
	private static JMenu									paletteMenu;
	private static JMenu									imageMenu;
	private static Map<Image, Rectangle>	sketchLocations;
	private static List<Image>						sketchImages;
	private static PaletteManager					paletteManager;

	static {
		sketchLocations = new HashMap<Image, Rectangle>();
		sketchImages = new LinkedList<Image>();
		paletteManager = new PaletteManager();
	}

	private ImageController							ctrl;

	private BufferedImage								backdrop;
	private double											scale				= 1.0;
	private final AffineTransform				transform;
	private Dimension										size;
	private ApplicationSettings					settings;
	private File												file;
	private Map<File, ImageController>	controllers;

	private SpriteController						spriteController;

	private Image												previewImage;

	private BufferedImage								backgroundImage;

	private Set<ModifyListener>					modifyListeners;

	private static Set<Overlay>					overlays;

	private static PreviewAnimator			animator;

	private static int									imageHeight;

	private static int									imageWidth;

	private static File									lastSaveDir	= new File(".");

	public static Dimension							toolButtonSize;

	public PixelPainter(ImageController ctrl, File file) {
		this.file = file;
		modifyListeners = new HashSet<ModifyListener>();
		ctrl.addBrushChangeListener(this);
		this.loadSavedPalettes(paletteManager);
		paletteManager.addPaletteListener(this);
		if (paletteManager.get("default") == null) {
			paletteManager.addPalette("default", ColorPalette.createFrom(//
					Color.green, Color.red, Color.blue, //
					Color.gray, Color.orange, Color.yellow, //
					Color.cyan, Color.pink, //
					// colors.add(new Color(139, 69, 19)); // redish brown
					new Color(107, 66, 38), // Semi-Sweet Chocolate
					new Color(222, 184, 135))); // Burly wood));
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Map<String, Object> paletteObjects = new HashMap<>();
				for (Map.Entry<String, ColorPalette> cp : paletteManager
						.getPalettes()) {
					Map<String, Object> colorsObj = new HashMap<>();
					Color clr[] = cp.getValue().getColors();
					String[] cToStr = new String[clr.length];
					for (int i = 0; i < clr.length; ++i) {
						cToStr[i] = String.format("#%02x%02x%02x", clr[i].getRed(),
								clr[i].getGreen(), clr[i].getBlue());
					}
					colorsObj.put("colors", cToStr);
					paletteObjects.put(cp.getKey(), colorsObj);
				}
				File paletteFile = new File(Settings.getInstance().settingsDir(),
						"palettes.json");
				if (!paletteFile.exists()) {
					try {
						paletteFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				String pStr = Json.toJson(paletteObjects);
				try {
					Files.write(paletteFile.toPath(), pStr.getBytes(),
							StandardOpenOption.TRUNCATE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		controllers = new HashMap<File, ImageController>();
		settings = ApplicationSettings.getInstance();
		setPreferredSize(new Dimension(settings.getWidth(), settings.getHeight()));
		setOpaque(true);
		setBackground(Color.darkGray);
		transform = new AffineTransform();
		this.ctrl = ctrl;
		scaleToFit();

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				for (Overlay o : overlays) {
					o.keyPressed(e);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				for (Overlay o : overlays) {
					o.keyReleased(e);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				for (Overlay o : overlays) {
					o.keyTyped(e);
				}
			}
		});
		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				// if(overlay.isInside(e.getPoint())) {
				for (Overlay o : overlays) {
					o.mousePressed(e);
				}
				// }else{
				if (!e.isConsumed()) {
					handleMouseEvent(e);
				}
				// }

				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// if (overlay.isInside(e.getPoint())) {
				for (Overlay o : overlays) {
					o.mouseReleased(e);
				}
				// } else {
				if (!e.isConsumed()) {
					handleMouseEvent(e);
				}
				// }

				repaint();
			}

		});

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				handleMouseEvent(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				for (Overlay o : overlays) {
					o.mouseMoved(e);
				}
				repaint();
			}

		});
		addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				for (Overlay o : overlays) {
					o.mouseWheel(e);
				}

				if (!e.isConsumed()) {
					scale += e.getWheelRotation() * 0.1;
				}

				repaint();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				size = getSize();
				repaint();
			}
		});

		this.setTransferHandler(new TransferHandler() {

			@Override
			public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
				for (DataFlavor df : transferFlavors) {
					if (df.equals(DataFlavor.javaFileListFlavor)) {
						return true;
					}
				}
				return super.canImport(comp, transferFlavors);
			}

			@Override
			public boolean importData(TransferSupport support) {

				try {
					List list = (List) support.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);

					for (Object o : list) {
						if (o instanceof File) {
							File f = (File) o;

							openNewFile(f);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return super.importData(support);
			}
		});

		Font f = getFontAwesome();
	}

	private void loadSavedPalettes(PaletteManager pm) {
		File paletteFile = new File(Settings.getInstance().settingsDir(),
				"palettes.json");

		if (paletteFile.exists()) {
			try {
				Json.JsonObject obj = Json.parseFileObject(paletteFile);

				for (String paletteName : obj) {
					ColorPalette cp = new ColorPalette();
					String[] colors = obj.getObject(paletteName).getStringArray("colors");
					for (String clStr : colors) {
						Color c = Color.decode(clStr);
						cp.addColor(c);
					}
					pm.addPalette(paletteName, cp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void scaleToFit() {
		Dimension size = this.ctrl.getSize();
		size = new Dimension(size.width + 10, size.height + 10);
		Dimension paneSize = this.getPreferredSize();
		scale = Math.min((double) paneSize.width / size.width,
				(double) paneSize.height / size.height);
	}

	public void changeImageController(ImageController ctrl, File file) {
		// TODO: change the image controller when loading files and save the
		// old image controllers.
		this.ctrl = ctrl;

		ctrl.addBrushChangeListener(this);
		scaleToFit();
		this.repaint();
		if (file != null) {
			frame.setTitle(String.format("%s - %s", file.getName(),
					file.getParentFile().getAbsolutePath()));
		}

		// set the new image controller to have all the modify listeners that
		// the old one had
		ctrl.addAllModifyListeners(modifyListeners);

		for (Overlay o : overlays) {
			o.changeControllers(ctrl);
		}
		resetToolbar();
	}

	private void resetToolbar() {
		for (Component c : tools.getComponents()) {
			if (c instanceof JButton) {
				JButton but = (JButton) c;
				Action a = but.getAction();
				if (a instanceof Brush) {
					Brush b = (Brush) a;
					b.changeControllers(ctrl);
				}
			}
		}
	}

	private void handleMouseEvent(MouseEvent e) {
		Dimension d = ctrl.getSize();
		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, d.width, d.height);
		// Shape shp = transform.createTransformedShape(rect);
		Point2D.Double ePtDst = new Point2D.Double(0, 0);
		try {
			transform.inverseTransform(e.getPoint(), ePtDst);
		} catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}
		// System.out.println(ePtDst);
		Point pt = new Point((int) ePtDst.x, (int) ePtDst.y);
		if (rect.contains(pt)) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				ctrl.setColorAt(pt.x, pt.y);
			} else {
				ctrl.applyBrush(pt.x, pt.y);
				repaint();
			}
		}
	}

	private void updateTransform() {
		Dimension d = ctrl.getSize();
		Dimension wndSize = size;
		if (wndSize == null) {
			wndSize = new Dimension(getWidth(), getHeight());
		}
		transform.setToIdentity();
		transform.translate(wndSize.width / 2.0, wndSize.height / 2.0);
		transform.scale(scale, scale);
		// System.out.println("Scale: " + scale + " Dimensions: "
		// + String.format("%dx%d", d.width, d.height) + " Window Size: "
		// + String.format("%dx%d", wndSize.width, wndSize.height));
		transform.translate(-d.width / 2.0, -d.height / 2.0);
	}

	@Override
	public void paintComponent(Graphics init) {
		super.paintComponent(init);
		Graphics2D g = (Graphics2D) init.create();
		updateTransform();
		setImageTransform(g);
		drawBackground(g);
		ctrl.render(g);
		if (gridOn) {
			drawGrid((Graphics2D) init);
		}
		if (preview && !animation) {
			drawPreview(init);
		}
		if (preview && animation) {
			drawAnimationPreview(init);
		}
		g.dispose();
		for (Overlay o : overlays) {
			o.render((Graphics2D) init, this.getWidth(), this.getHeight());
		}
	}

	private void drawAnimationPreview(Graphics init) {
		Graphics2D g = (Graphics2D) init.create();
		Dimension imgSize = ctrl.getSize();
		if (previewImage != null) {
			imgSize = new Dimension(previewImage.getWidth(null),
					previewImage.getHeight(null));
		}
		g.setStroke(new BasicStroke(1));
		g.translate(getWidth() - Math.min(64, imgSize.width) - 1, 2);
		g.setColor(Color.yellow.darker());
		if (previewImage == null) {
			int w = g.getFontMetrics().stringWidth("N/A");
			g.drawString("N/A", imgSize.width / 2 - w / 2,
					imgSize.height / 2 + g.getFontMetrics().getAscent() / 2);
		} else {
			g.drawImage(previewImage, 0, 0, null);
		}
		g.drawRect(-1, -1, Math.min(64, imgSize.width + 1),
				Math.min(64, imgSize.height + 1));
		g.setColor(Color.yellow.darker());
		g.drawRect(-1, -1, Math.min(64, imgSize.width + 1),
				Math.min(64, imgSize.height + 1));
		g.dispose();
	}

	private void drawPreview(Graphics init) {
		Graphics2D g = (Graphics2D) init.create();

		Dimension imgSize = ctrl.getSize();
		g.setStroke(new BasicStroke(1));
		g.translate(getWidth() - Math.min(64, imgSize.width) - 1, 2);
		ctrl.render(g, Math.min(64, imgSize.width), Math.min(64, imgSize.height));
		g.setColor(Color.yellow.darker());
		g.drawRect(-1, -1, Math.min(64, imgSize.width + 1),
				Math.min(64, imgSize.height + 1));
		g.dispose();
	}

	public void setImageTransform(Graphics2D g) {
		Dimension d = ctrl.getSize();
		Dimension wndSize = size;
		if (wndSize == null) {
			wndSize = new Dimension(getWidth(), getHeight());
		}
		g.translate(wndSize.width / 2.0, wndSize.height / 2.0);
		g.scale(scale, scale);
		// System.out.println("Scale: " + scale + " Dimensions: "
		// + String.format("%dx%d", d.width, d.height) + " Window Size: "
		// + String.format("%dx%d", wndSize.width, wndSize.height));
		g.translate(-d.width / 2.0, -d.height / 2.0);
	}

	private void drawGrid(Graphics2D init) {
		Graphics2D g = (Graphics2D) init.create();
		if (scale > 1.5) {
			translateToImage(g);
			g.setColor(GRID_COLOR);
			Dimension d = ctrl.getSize();
			for (int i = 0; i <= d.width; ++i) {
				g.drawLine((int) (i * scale), 0, (int) (i * scale),
						(int) (d.height * scale));
			}

			for (int i = 0; i <= d.height; ++i) {
				g.drawLine(0, (int) (i * scale), (int) (d.width * scale),
						(int) (i * scale));
			}
		}
		g.dispose();
	}

	public void translateToImage(Graphics2D g) {
		Dimension d = ctrl.getSize();
		g.translate(getWidth() / 2.0, getHeight() / 2.0);
		g.translate(-(d.width * scale) / 2.0, -(d.height * scale) / 2.0);
	}

	private void drawBackground(Graphics2D init) {
		Graphics2D g = (Graphics2D) init.create();
		Color offWhite = new Color(250, 250, 250);
		Color gray = new Color(100, 100, 100);

		int w = 16, h = 16;

		// draw backdrop image
		if (backdrop == null) {

			int scale = 2;
			backdrop = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

			Graphics2D bg = backdrop.createGraphics();
			bg.setColor(offWhite.darker());
			bg.fillRect(0, 0, w, h);
			bg.setColor(gray.brighter());
			bg.fillRect(0, 0, w / scale, h / scale);
			bg.fillRect(w / scale, h / scale, w / scale, h / scale);
			bg.dispose();
		}

		double denominator = this.scale;
		double scale = 1 / denominator;
		Dimension d = ctrl.getSize();
		Rectangle2D.Double cSpaceRect = new Rectangle2D.Double(0, 0,
				d.width * denominator, d.height * denominator);

		if (backgroundImage == null) {
			g.scale(scale, scale);
			TexturePaint texPaint = new TexturePaint(backdrop,
					new Rectangle2D.Double(0, 0, w, h));
			g.setPaint(texPaint);
			g.fill(cSpaceRect);
		} else {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
			g.drawImage(backgroundImage, 0, 0, null);
		}

		drawSketchImages(g);
		g.dispose();
	}

	private void drawSketchImages(Graphics2D init) {
		Graphics2D g = (Graphics2D) init.create();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
		Rectangle r;
		for (Image image : sketchImages) {
			r = sketchLocations.get(image);
			g.drawImage(image, r.x, r.y, r.width, r.height, null);
		}
		g.dispose();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Dimension imageSize = NewImagePanel.showAsDialog();
		imageWidth = imageSize.width;
		imageHeight = imageSize.height;

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndDisplay(new PixelPainter(ImageController
						.createNewInstance(imageSize.width, imageSize.height), null),
						JFrame.EXIT_ON_CLOSE);
			}
		});
	}

	public static void createAndDisplay(PixelPainter painter, int closeOp) {
		ImageController ctrl = painter.getController();
		SpriteController spriteCtrl = painter.getSpriteController();
		frame = new JFrame("Pixel Painter " + version);
		frame.setLayout(new BorderLayout());
		frame.add(painter);
		setupMenus(painter, frame, closeOp);
		setupToolBar(ctrl, spriteCtrl, frame, painter);
		frame.pack();
		frame.setDefaultCloseOperation(closeOp);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		painter.requestFocus();
	}

	public SpriteController getSpriteController() {
		if (spriteController == null) {
			spriteController = new SpriteController(this, imageWidth, imageHeight);
		}
		return spriteController;
	}

	private ImageController getController() {
		return ctrl;
	}

	private static void setupToolBar(final ImageController ctrl,
			SpriteController sprites, JFrame frame2, PixelPainter pp) {
		tools = new JToolBar();
		Font f = getFontAwesome();
		FontMetrics fm = tools.getFontMetrics(f);
		String eraseUnicode = "\uf12d";
		System.out.println("Font info: " + fm.getLeading() + " ");
		toolButtonSize = new Dimension(2*fm.getMaxAdvance(), fm.getLeading() + fm.getMaxAscent()
				+ fm.getHeight() + fm.getMaxDescent());
		JButton erase = tools.add(new EraseBrush(ctrl, eraseUnicode, null));
		erase.setFont(f);
		erase.setForeground(Color.black);
		erase.setPreferredSize(toolButtonSize);
		erase.setToolTipText("Eraser");

		JButton but = tools.add(new FillMode(ctrl, "\uf576"));
		but.setFont(f);
		but.setPreferredSize(toolButtonSize);
		but.setForeground(Color.blue);
		but.setToolTipText("Bucket Fill");

		frame2.add(tools, BorderLayout.NORTH);

		overlays = new HashSet<Overlay>();
		overlays
				.add(new ColorBarOverlay(tools, ctrl, paletteManager.get("default")));
		overlays.add(new SpriteFrameBarOverlay(tools, ctrl, sprites));
		overlays.add(new ColorHoverOverlay(tools, pp, ctrl));
	}

	public static void onExit(int closeOp) {
		frame.setVisible(false);
		if (closeOp == JFrame.EXIT_ON_CLOSE) {
			System.exit(0);
		}
	}

	private static void setupMenus(final PixelPainter painter,
			final JFrame frame2, final int closeOp) {
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem newImage = new JMenuItem("New");
		newImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openNewFileDialog(frame2, painter);
			}
		});

		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();

				loadFileChooserSettings(jfc, FILE_CHOOSER.OPEN_IMAGE);

				int ret = jfc.showOpenDialog(frame2);

				if (ret == JFileChooser.APPROVE_OPTION) {
					painter.openNewFile(jfc.getSelectedFile());

					saveFileChooserSettings(jfc, FILE_CHOOSER.OPEN_IMAGE);
				}
			}
		});
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();

				loadFileChooserSettings(jfc, FILE_CHOOSER.SAVE_IMAGE);

				int ret = jfc.showSaveDialog(frame2);

				if (ret == JFileChooser.APPROVE_OPTION) {
					try {
						File file = jfc.getSelectedFile();
						boolean saved = false;
						if (file.exists()) {
							ret = JOptionPane.showConfirmDialog(frame2,
									"The file already exists do you still want to overwrite it!",
									"Overwrite Question?", JOptionPane.YES_NO_CANCEL_OPTION);
							if (ret == JOptionPane.YES_OPTION) {
								painter.getController().save(file);
								saved = true;
							}
						} else {
							File toSave = jfc.getSelectedFile();
							saveFileChooserSettings(jfc, FILE_CHOOSER.SAVE_IMAGE);
							painter.getController().save(toSave);
							saved = true;
						}
						if (saved) {
							frame2.setTitle(String.format("%s - %s", file.getName(),
									file.getParentFile().getAbsolutePath()));
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(frame2, "Error Saving File",
								"There was a problem saving the image: " + e1.getMessage(),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onExit(closeOp);
			}
		});
		file.add(newImage);
		file.addSeparator();
		file.add(open);
		file.addSeparator();
		file.add(save);
		file.addSeparator();
		file.add(exit);
		menubar.add(file);

		JMenu tools = new JMenu("Tools");
		JMenuItem createSpriteSheet = new JMenuItem("Create Sprite Sheet");
		createSpriteSheet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				createSpriteSheet(painter, frame);
			}
		});
		JMenuItem sketchImage = new JMenuItem("Add Sketch Image...");
		sketchImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();

				int ret = jfc.showOpenDialog(frame2);

				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					try {
						BufferedImage bImg = ImageIO.read(file);
						Rectangle r = new Rectangle(0, 0, bImg.getWidth(),
								bImg.getHeight());
						sketchLocations.put(bImg, r);
						sketchImages.add(bImg);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		tools.add(createSpriteSheet);
		tools.add(sketchImage);

		JMenuItem showTilePreview = new JMenuItem("Show Map Preview");
		showTilePreview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				painter.showTilePreview();
			}
		});
		tools.add(showTilePreview);

		// TODO: add more tools here
		menubar.add(tools);

		imageMenu = new JMenu("Images");
		imageMenu.setEnabled(false);
		menubar.add(imageMenu);

		paletteMenu = new JMenu("Color Palettes");
		JMenuItem paletteEditor = new JMenuItem("Palette Editor");
		paletteEditor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				painter.showPaletteEditorDialog(new PaletteEditor(paletteManager,
						"New Palette", new ColorPalette()));
			}
		});
		paletteMenu.add(paletteEditor);
		JMenuItem createPalette = new JMenuItem("Create Palette from Image");
		createPalette.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();

				int ret = jfc.showOpenDialog(frame2);

				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					try {
						BufferedImage bImg = ImageIO.read(file);
						BufferedImage newFormat = new BufferedImage(bImg.getWidth(),
								bImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = newFormat.createGraphics();
						g.drawImage(bImg, 0, 0, null);
						g.dispose();
						ColorPalette palette = ColorPalette.createFromImage(newFormat);
						Color colors[] = palette.getColors();
						System.out.println(colors.length);
						PalettePanel palettePane = new PalettePanel(palette);
						palettePane.addPropertyChangeListener(PalettePanel.COLOR_SELECTED,
								new PropertyChangeListener() {
									@Override
									public void propertyChange(PropertyChangeEvent evt) {
										Color color = (Color) evt.getNewValue();

										Brush brush = painter.getController()
												.createColorBrush(color);
										tools.add(brush);
										painter.getController().setBrush(brush);
									}
								});
						JOptionPane.showMessageDialog(frame2, palettePane);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		paletteMenu.add(createPalette);
		JMenuItem newPalette = new JMenuItem(
				new AbstractAction("Create New Palette") {

					@Override
					public void actionPerformed(ActionEvent e) {
						String response = JOptionPane.showInputDialog(
								(Component) e.getSource(), "Enter the name for the new palette",
								"New Palette Name?", JOptionPane.OK_CANCEL_OPTION);

						if (response != null && !response.trim().isEmpty()) {
							(new Thread() {
								public void run() {
									ColorPalette cp = PaletteEditor.editPalette(frame,
											paletteManager, response);
								}
							}).start();
						}
					}
				});
		paletteMenu.add(newPalette);
		if (paletteManager != null && paletteManager.hasPalettes()) {
			paletteMenu.addSeparator();
			for (Map.Entry<String, ColorPalette> cp : paletteManager.getPalettes()) {
				painter.paletteAdded(paletteManager, cp.getKey(), cp.getValue());
			}
		}
		menubar.add(paletteMenu);

		JMenu view = new JMenu("View");
		JMenuItem toggleGrid = new JCheckBoxMenuItem("Grid");
		toggleGrid.setSelected(gridOn);
		toggleGrid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				gridOn = item.isSelected();
				frame2.repaint();
			}
		});
		view.add(toggleGrid);
		JMenuItem togglePreview = new JCheckBoxMenuItem("Preview");
		togglePreview.setSelected(preview);
		togglePreview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				preview = item.isSelected();
				frame2.repaint();
			}
		});
		view.add(togglePreview);
		JMenuItem toggleAnimation = new JCheckBoxMenuItem("Animation");
		toggleAnimation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
				boolean newSelection = item.isSelected();
				frame2.repaint();
				if (newSelection && !animation) {
					startAnimator(painter, frame2);
				} else if (animator != null) {
					animator.stop();
				}
				animation = newSelection;
			}
		});
		view.add(toggleAnimation);
		menubar.add(view);

		frame2.setJMenuBar(menubar);
	}

	public static Font getFontAwesome() {
		if (fntAwesome == null) {
			try {
				InputStream fntStream = PixelPainter.class.getClassLoader()
						.getResourceAsStream("fa-solid-900.ttf");
				if (fntStream != null) {
					fntAwesome = Font.createFont(Font.TRUETYPE_FONT, fntStream);
					fntAwesome = fntAwesome.deriveFont(16f);
				} else {
					System.err.println("Fallback because I cannot load fonts!");
				}
			} catch (FontFormatException e1) {
				fntAwesome = null;
				e1.printStackTrace();
			} catch (IOException e1) {
				fntAwesome = null;
				e1.printStackTrace();
			}
		}
		return fntAwesome;
	}

	protected static void openNewFileDialog(Frame parent, PixelPainter painter) {
		final JDialog dlg = new JDialog(parent, "New File Dialog", true);
		final NewFilePanel nfp = new NewFilePanel();
		dlg.setLayout(new BorderLayout());
		dlg.add(nfp);
		JPanel pane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.5;
		c.gridx = 0;
		JButton ok = new JButton("New");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nfp.operationOk();
				dlg.setVisible(false);
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nfp.operationCancelled();
				dlg.setVisible(false);
			}
		});
		pane.add(ok, c);

		c.anchor = GridBagConstraints.WEST;
		c.gridx++;
		pane.add(cancel, c);

		dlg.add(pane, BorderLayout.SOUTH);
		dlg.pack();
		dlg.setLocationRelativeTo(parent);
		dlg.setVisible(true);

		if (!nfp.isCancelled()) {
			int width = nfp.getImageWidth();
			int height = nfp.getImageHeight();

			createNewImageFile(painter, width, height);
		}
	}

	protected static void createNewImageFile(PixelPainter painter, int width,
			int height) {
		File file = new File("./tmp_new.png");

		int i = 0;
		while (file.exists()) {
			file = new File(String.format("./tmp_new%05d.png", i));
			++i;
		}
		painter.createNewImageFile(file, width, height);
	}

	protected static void createSpriteSheet(PixelPainter painter, JFrame frame2) {

	}

	protected static void startAnimator(PixelPainter painter, JFrame frame2) {
		if (animator == null) {
			animator = new PreviewAnimator(painter, frame2);
		}
		animator.startAnimator();
	}

	protected static void loadFileChooserSettings(JFileChooser jfc,
			FILE_CHOOSER chooserType) {
		if (chooserType == FILE_CHOOSER.OPEN_IMAGE) {
			jfc.setCurrentDirectory(ApplicationSettings.getInstance()
					.getFileChooserDirectory(FILE_CHOOSER.OPEN_IMAGE));
		} else if (chooserType == FILE_CHOOSER.SAVE_IMAGE) {
			jfc.setCurrentDirectory(ApplicationSettings.getInstance()
					.getFileChooserDirectory(FILE_CHOOSER.SAVE_IMAGE));
		}
	}

	protected static void saveFileChooserSettings(JFileChooser jfc,
			FILE_CHOOSER chooserType) {
		ApplicationSettings.getInstance().setFileChooserDirectory(chooserType,
				jfc.getCurrentDirectory());
	}

	protected void showTilePreview() {
		JDialog jdiag = new JDialog();
		JPanel show = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics init) {
				super.paintComponent(init);
				Dimension d = ctrl.getSize();
				int rows = getHeight() / d.height + 1;
				int cols = getWidth() / d.width + 1;
				Graphics2D g = (Graphics2D) init.create();
				for (int i = 0; i < rows; ++i) {
					for (int k = 0; k < cols; ++k) {
						ctrl.render(g);
						g.translate(d.width, 0.0);
					}
					g.translate(-((double) cols * d.width), d.height);
				}
				g.dispose();
			}
		};
		show.setPreferredSize(new Dimension(300, 400));
		jdiag.setLayout(new BorderLayout());
		jdiag.add(show);
		jdiag.pack();
		jdiag.setLocationRelativeTo(this);
		jdiag.setVisible(true);
	}

	protected void createNewImageFile(File file, int width, int height) {
		BufferedImage bImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		try {
			ImageIO.write(bImage, "PNG", file);

			// open the file as a controller
			openNewFile(file);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error create new Image file!",
					"Image Creation Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void openNewFile(File file) {
		try {
			ImageController newCtrl = ImageController.createNewInstance(file);
			this.changeImageController(newCtrl, file);
			controllers.put(file, newCtrl);

			// reload all the image lists
			imageMenu.removeAll();
			imageMenu.setEnabled(!controllers.isEmpty());
			JMenuItem menuItem;
			for (final File f : new TreeSet<File>(controllers.keySet())) {
				menuItem = new JMenuItem(
						String.format("%s - %10s", f.getName(), f.getParent()));
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						changeImageController(controllers.get(f), f);
					}
				});

				imageMenu.add(menuItem);
			}
			imageMenu.validate();

		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(frame, "Error Loading Image",
					"There was a problem loading the image: " + e1.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void paletteAdded(final PaletteManager paletteManager,
			final String name, final ColorPalette palette) {
		JMenuItem paletteEdit = new JMenuItem("Edit...");
		paletteEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						showPaletteEditorDialog(
								new PaletteEditor(paletteManager, name, palette));
					}
				});
			}
		});
		JMenuItem paletteUse = new JMenuItem("Use");
		paletteUse.addActionListener((e) -> {
			Overlay old = null;
			for (Overlay o : overlays) {
				if (o instanceof ColorBarOverlay) {
					old = o;
				}
			}
			if (old != null) {
				overlays.remove(old);
			}
			overlays.add(new ColorBarOverlay(tools, ctrl, palette));
		});
		if (paletteMenu != null) {
			Component menuComponent = paletteMenu
					.getMenuComponent(paletteMenu.getMenuComponentCount() - 1);
			if (!(menuComponent instanceof JPopupMenu.Separator)) {
				paletteMenu.addSeparator();
			}
			JMenu paletteMenus = new JMenu(name);
			paletteMenus.add(paletteEdit);
			paletteMenus.add(paletteUse);
			paletteMenu.add(paletteMenus);
			paletteMenu.validate();
		}
	}

	protected void showPaletteEditorDialog(PaletteEditor paletteEditor) {
		final JDialog dlg = new JDialog();
		dlg.setLayout(new BorderLayout());

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlg.setVisible(false);
				dlg.dispose();
			}
		});
		dlg.add(paletteEditor);
		dlg.add(close, BorderLayout.SOUTH);
		dlg.pack();
		dlg.setLocationRelativeTo(this);
		dlg.setVisible(true);
	}

	public void setPreviewImage(Image image) {
		previewImage = image;
	}

	public void setBackgroundImage(BufferedImage bkImage) {
		backgroundImage = bkImage;
	}

	@Override
	public void brushChanged(Brush old, Brush brushNew, ImageController ctrl) {
		for (Component c : tools.getComponents()) {
			if (c instanceof JButton) {
				JButton but = (JButton) c;
				// should be set to defaults for all except selected
				// but.setBackground(new Color(0, 0, 0, 0));
			}
		}
	}

	public void addModifyListener(ModifyListener l) {
		modifyListeners.add(l);
	}

	/**
	 * Return the pixel x,y location in the image given a point within the viewer.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Point getPointInImage(int x, int y) {
		Point2D.Double ePtDst = new Point2D.Double(0, 0);
		try {
			transform.inverseTransform(new Point(x, y), ePtDst);
		} catch (NoninvertibleTransformException e1) {
			e1.printStackTrace();
		}
		return new Point((int) ePtDst.x, (int) ePtDst.y);
	}

	/**
	 * Helper method returns the same as getPointInViewer(x,y);
	 * 
	 * @param p
	 * @return
	 */
	public Point getPointInViewer(Point p) {
		return getPointInViewer(p.x, p.y);
	}

	/**
	 * Return the viewer x,y location in the image of a given pixel point within
	 * the image.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Point getPointInViewer(int x, int y) {
		Point2D.Double ePtDst = new Point2D.Double(0, 0);
		transform.transform(new Point(x, y), ePtDst);
		return new Point((int) ePtDst.x, (int) ePtDst.y);
	}

	public Dimension getPixelSize(Point p) {

		return null;
	}
}
