package com.pixel.painter.brushes;

import com.pixel.painter.controller.ImageController;

public interface BrushChangeListener {

	public void brushChanged(Brush old, Brush brushNew, ImageController ctrl);

}
