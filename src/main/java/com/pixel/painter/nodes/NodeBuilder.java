package com.pixel.painter.nodes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import com.pixel.painter.controller.ImageController;

/**
 * This is like an inverse flood fill algorithm that finds all related colors
 * and breaks them down into manageable boxes of the same color. Starting with
 * the center of the image and working out to adjacent pixels. These related
 * boxes of filled color are then made into a tree of nodes the root node is the
 * center box of color. <br/>
 * Nodes can become gradient fills, and also have effects on them determined by
 * the inverse effect algorithms which basically takes the colors and analyzes
 * if the effect would produce the shown image. The pattern type correlation can
 * be expensive and could become an exhaustive search.
 * 
 * @author Nick
 */
public class NodeBuilder {

	private ImageController	imgCtrl;

	// the bounds of the actual colors minus clear colors
	private Rectangle		trueBounds;

	public NodeBuilder(ImageController imgCtrl) {
		this.imgCtrl = imgCtrl;
	}

	public void defineBounds() {
		Dimension size = imgCtrl.getSize();
		int x = size.width / 2;
		int y = size.height / 2;
		Color c = imgCtrl.sample(size.width / 2, size.height / 2);

	}
}
