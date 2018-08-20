package com.pixel.painter.ui.dialog;

import java.awt.BorderLayout;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class NewFilePanel extends JPanel {

	private SpinnerNumberModel	widthNumber;
	private SpinnerNumberModel	heightNumber;
	private boolean				cancelled;

	public NewFilePanel() {
		cancelled = true;
		JSpinner widthSpinner = new JSpinner(
				widthNumber = new SpinnerNumberModel(32, 8, 256, 8));
		JSpinner heightSpinner = new JSpinner(
				heightNumber = new SpinnerNumberModel(32, 8, 256, 8));
		JLabel lblWidth = new JLabel("Width");
		JLabel lblHeight = new JLabel("Height");

		GroupLayout gl = new GroupLayout(this);
		setLayout(gl);

		// setBorder(BorderFactory.createLineBorder(Color.red));

		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);

		gl.setHorizontalGroup(gl
				.createSequentialGroup()
				.addGroup(
						gl.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(lblWidth).addComponent(lblHeight))
				.addGroup(
						gl.createParallelGroup().addComponent(widthSpinner)
								.addComponent(heightSpinner)));

		gl.setVerticalGroup(gl
				.createSequentialGroup()
				.addGroup(
						gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblWidth)

								.addComponent(widthSpinner))
				.addGroup(
						gl.createParallelGroup().addComponent(lblHeight)
								.addComponent(heightSpinner)));

		gl.linkSize(lblWidth, lblHeight);
		gl.linkSize(widthSpinner, heightSpinner);
	}

	public int getImageHeight() {
		return (Integer) heightNumber.getValue();
	}

	public int getImageWidth() {
		return (Integer) widthNumber.getValue();
	}

	public static void main(String... args) {
		JFrame frame = new JFrame("New File Panel Test");
		frame.setLayout(new BorderLayout());
		frame.add(new NewFilePanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void operationOk() {
		cancelled = false;
	}

	public void operationCancelled() {
		cancelled = true;
	}

	public boolean isCancelled() {
		return cancelled;
	}
}
