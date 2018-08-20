package com.pixel.painter.rescale;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RescaleImage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File file = new File(args[0]);

		int tWidth = 32;
		int tHeight = 32;

		try {
			BufferedImage img = (BufferedImage) ImageIO.read(file);

			double scaleFactor = 0;
			if (img.getWidth() >= img.getHeight()) {
				scaleFactor = (double) tWidth / img.getWidth();
			} else if (img.getHeight() > img.getWidth()) {
				scaleFactor = (double) tHeight / img.getHeight();
			}
			int width = (int) (scaleFactor * img.getWidth());
			int height = (int) (scaleFactor * img.getHeight());
			Image scaledInstance = img.getScaledInstance(width, height,
					BufferedImage.SCALE_AREA_AVERAGING);

			BufferedImage bimg = new BufferedImage(tWidth, tHeight,
					BufferedImage.TYPE_INT_ARGB);

			Graphics2D g = bimg.createGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, bimg.getWidth(), bimg.getHeight());
			// draw the scaled instance centered
			g.drawImage(scaledInstance,
					bimg.getWidth() / 2 - scaledInstance.getWidth(null) / 2,
					bimg.getHeight() / 2 - scaledInstance.getHeight(null) / 2,
					null);
			g.dispose();

			ImageIO.write(
					bimg,
					"PNG",
					new File(file.getName().substring(0,
							file.getName().lastIndexOf('.'))
							+ "_" + tWidth + "_new.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
