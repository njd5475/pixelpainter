package com.pixel.painter.palettes;

import com.pixel.painter.model.ColorPalette;

public interface PaletteListener {

	void paletteAdded(PaletteManager paletteManager, String name,
			ColorPalette palette);

}
