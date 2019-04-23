package com.pixel.painter.controller;

import java.awt.image.BufferedImage;

public class TilesetController {

	private BufferedImage	image;
	private int				tileWidth;
	private int				tileHeight;
	private int				rows;
	private int				cols;

	public TilesetController(BufferedImage tilesetImage, int tw, int th) {
		this.image = tilesetImage;
		this.tileWidth = tw;
		this.tileHeight = th;
		this.rows = image.getHeight() / th;
		this.cols = image.getWidth() / tw;
	}

	public ImageController getController(int col, int row) {
		return new SingleImageController(image.getSubimage(col * tileWidth, row
				* tileHeight, tileWidth, tileHeight), true);
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

}
