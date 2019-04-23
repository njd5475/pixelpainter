package com.pixel.painter.controller;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import com.pixel.painter.ui.PixelPainter;

public class SpriteController {

	private int													width;
	private int													height;
	private PixelPainter								painter;
	private Map<Integer, BufferedImage>	images;
	private int													index;

	public SpriteController(PixelPainter painter, int imgWidth, int imgHeight) {
		if (imgWidth == 0 || imgHeight == 0) {
			throw new IllegalArgumentException(
					"Cannot have a sprite controller images with 0 size");
		}
		this.painter = painter;
		images = new HashMap<Integer, BufferedImage>();
		this.width = imgWidth;
		this.height = imgHeight;
	}

	public void setImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void createNewImage() {
		images.put(images.size() + 1,
				new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
	}

	public void createNewImage(BufferedImage image) {
		images.put(images.size() + 1, image);
	}

	public void changeImage(int index) {
		if (index != this.index && images.containsKey(index)) {
			this.index = index;
			painter.changeImageController(
					SingleImageController.createNewInstance(images.get(index)), null);
			// set the last image as the background sketch images
			if (index - 1 > 0 && images.containsKey(index - 1)) {
				painter.setBackgroundImage(images.get(index - 1));
			} else if (images.containsKey(index - 1)) {
				painter.setBackgroundImage(images.get(images.size()));
			}
		} else {
			System.err.println("Invalid index to SpriteController.changeImage");
		}
	}

	public Image[] getFrames() {
		LinkedList<Image> frames = new LinkedList<Image>();

		for (Integer i : new TreeSet<Integer>(images.keySet())) {
			frames.add(images.get(i));
		}

		return frames.toArray(new Image[frames.size()]);
	}

	public int getFrameCount() {
		return images.size();
	}

	public int getImageWidth() {
		return width;
	}

	public int getImageHeight() {
		return height;
	}
}
