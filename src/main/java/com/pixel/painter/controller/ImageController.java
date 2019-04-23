package com.pixel.painter.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.pixel.painter.brushes.Brush;
import com.pixel.painter.brushes.BrushChangeListener;
import com.pixel.painter.brushes.ColorBrush;
import com.pixel.painter.brushes.EraseBrush;
import com.pixel.painter.ui.ModifyListener;

public class ImageController {

	private final BufferedImage image;
	private Brush brush;
	private final UndoManager manager;
	private final int pxArr[] = new int[4];
	private Color fillColor;
	private Set<BrushChangeListener> brushListeners;
	private Set<ModifyListener> modifyListeners;
	private Map<Color, Set<Point>> colorPoints;

	protected ImageController(int width, int height) {
		this(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), true);
	}

	public ImageController(BufferedImage image, boolean modifyOriginal) {
		if (!modifyOriginal) {
			this.image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = this.image.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
		} else {
			this.image = image;
		}
		colorPoints = new HashMap<Color, Set<Point>>();
		mapImageByColor();
		brushListeners = new HashSet<BrushChangeListener>();
		modifyListeners = new HashSet<ModifyListener>();
		manager = new UndoManager();
	}

	private void mapImageByColor() {
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				Color sample = this.sample(x, y);
				Set<Point> points = colorPoints.get(sample);
				if (points == null) {
					points = new HashSet<Point>();
					colorPoints.put(sample, points);
				}

				points.add(new Point(x, y));
			}
		}
	}

	public static ImageController createNewInstance(File file) throws IOException {
		return new ImageController(ImageIO.read(file), false);
	}

	public static ImageController createNewInstance(BufferedImage image) {
		return new ImageController(image, true);
	}

	public static ImageController createNewInstance(int width, int height) {
		return new ImageController(width, height);
	}

	public Dimension getSize() {
		return new Dimension(image.getWidth(), image.getHeight());
	}

	public void setBrush(Brush brush) {
		if (brush == null) {
			throw new NullPointerException("Brush setting is null");
		}
		Brush old = this.brush;
		this.brush = brush;
		notifyBrushChangeListeners(old, brush);
	}

	public void addBrushChangeListener(BrushChangeListener l) {
		brushListeners.add(l);
	}

	private void notifyBrushChangeListeners(Brush old, Brush update) {
		for (BrushChangeListener l : brushListeners) {
			l.brushChanged(old, update, this);
		}
	}

	public void applyBrush(int x, int y) {
		if (brush == null) {
			return;
		}
		Graphics2D g = image.createGraphics();
		brush.apply(g, x, y);
		g.dispose();
		UndoableEdit edit = createUndoableEdit(brush, x, y);
		if (edit != null) {
			manager.addEdit(edit);
		}
	}

	private UndoableEdit createUndoableEdit(final Brush brush, final int x, final int y) {
		final Rectangle r = brush.getAffectedArea(x, y);
		if (r == null) {
			return null;
		}

		final int[] oldData = sample(r); // this could become too expensive
		return new AbstractUndoableEdit() {

			@Override
			public boolean canRedo() {
				return true;
			}

			@Override
			public boolean canUndo() {
				return true;
			}

			@Override
			public void redo() throws CannotRedoException {
				Graphics2D g = image.createGraphics();
				brush.apply(g, x, y);
				g.dispose();
			}

			@Override
			public void undo() throws CannotUndoException {
				WritableRaster raster = image.getRaster();
				// reset raster for the affected area.
				raster.setPixels(r.x, r.y, r.width, r.height, oldData);
			}

		};

	}

	private int[] sample(Rectangle r) {
		int[] data = new int[r.width * r.height * 4];
		WritableRaster raster = image.getRaster();
		raster.getPixels(r.x, r.y, r.width, r.height, data);
		return data;
	}

	public Brush createColorBrush(Color color) {
		return ColorBrush.createColorBrush(this, color);
	}

	public void render(Graphics2D g) {
		g.drawImage(image, 0, 0, null);
	}

	public void render(Graphics2D g, int width, int height) {
		g.drawImage(image, 0, 0, width, height, null);
	}

	public void save(File selectedFile) throws IOException {
		this.save(selectedFile, "PNG");
	}
	
	public void save(File selectedFile, String extension) throws IOException {
		System.out.println("Saving with extensions " + extension);
		ImageIO.write(image, extension, selectedFile);
	}

	public Color sample(int x, int y) {
		int argb = image.getRGB(x, y);
		return new Color(argb, true);
	}

	public void clearColor(int x, int y) {
		// reset raster for the affected area.
		image.setRGB(x, y, 0);
	}

	public void setColorAt(int x, int y, Color c) {
		// move color in map
		Color sample = sample(x, y);
		if (colorPoints.containsKey(sample)) {
			colorPoints.get(sample).remove(new Point(x, y));
			if (colorPoints.get(sample).isEmpty()) {
				colorPoints.remove(sample);
			}
		}
		image.setRGB(x, y, c.getRGB());
		// resample
		sample = sample(x, y);
		Set<Point> points = colorPoints.get(sample);
		if (points == null) {
			points = new HashSet<Point>();
			colorPoints.put(sample, points);
		}
		points.add(new Point(x, y));
	}

	public void setColorAt(int x, int y) {
		Color c = sample(x, y);
		if (c.getAlpha() == 0) {
			this.setBrush(new EraseBrush(this));
		} else {
			// find brush with color and change to that brush
			this.setBrush(ColorBrush.createColorBrush(this, c));
		}
		fillColor = c;
	}

	public Brush getBrush() {
		return brush;
	}

	public void setFillColor(Color fill) {
		fillColor = fill;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void addAllModifyListeners(Set<ModifyListener> ls) {
		modifyListeners.addAll(ls);
	}

	public BufferedImage getImage() {
		return image;
	}

	public Set<Point> getAll(int red, int green, int blue, int alpha) {
		return colorPoints.get(new Color(red, green, blue, alpha));
	}

	public void setAllColorsAt(int x, int y, Color newColor) {
		Color originalColor = this.sample(x, y);
		Set<Point> pts = colorPoints.get(originalColor);
		colorPoints.remove(originalColor);
		colorPoints.put(newColor, pts);
		for (Point pt : pts) {
			setColorAt(pt.x, pt.y, newColor);
		}
	}

	public static ImageController createNewDefaultInstance() {
		return createNewInstance(32, 32);
	}



}
