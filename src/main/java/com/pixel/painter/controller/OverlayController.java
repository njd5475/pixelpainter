package com.pixel.painter.controller;

import java.awt.Container;
import java.util.HashSet;
import java.util.Set;

import com.pixel.painter.ui.overlays.Overlay;

public class OverlayController {

	private Set<Overlay>	overlays;
	private Container		parent;

	public OverlayController(Container container) {
		this.parent = container;
		overlays = new HashSet<Overlay>();
	}

}
