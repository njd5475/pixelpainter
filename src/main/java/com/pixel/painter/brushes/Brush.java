package com.pixel.painter.brushes;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;

import com.pixel.painter.controller.ImageController;

public abstract class Brush extends AbstractAction {

	private static JComponent	current;

	private ImageController		ctrl;

	public Brush(ImageController ctrl) {
		this.ctrl = ctrl;
	}

	public Brush(ImageController ctrl, String name) {
		super(name);
		this.ctrl = ctrl;
	}

	public Brush(ImageController ctrl, String name, Icon icon) {
		super(name, icon);
		this.ctrl = ctrl;
	}

	public void changeControllers(ImageController ctrl) {
		this.ctrl = ctrl;
	}

	protected ImageController getController() {
		return ctrl;
	}

	public abstract void apply(Graphics2D g, int x, int y);

	@Override
	public void actionPerformed(ActionEvent e) {
		if (current != null) {
			current.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
		}
		JComponent comp = (JComponent) e.getSource();
		ctrl.setBrush(this); // set this brush

		current = comp;
	}

	public abstract Rectangle getAffectedArea(int x, int y);

}
