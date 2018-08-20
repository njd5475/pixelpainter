package com.pixel.painter.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pixel.painter.controller.ImageController;

public class SpriteSheetPanel extends JPanel {

	private JFrame			frame;
	private PixelPainter	painter;
	protected Object		width;
	protected Object		height;
	protected String		spCount;
	private boolean			cancelled;

	public SpriteSheetPanel(JFrame frame, PixelPainter painter) {
		super(new BorderLayout());
		this.frame = frame;
		this.painter = painter;
		getSheetSize();
	}

	private void getSheetSize() {
		cancelled = false;
		final JDialog dlg = new JDialog(frame, true);
		JPanel pane = new JPanel();
		dlg.add(pane);
		final JButton create = new JButton("Create");
		create.setEnabled(false);
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlg.setVisible(false);
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlg.setVisible(false);
				dlg.dispose();
				cancelled = true;
			}
		});

		JComboBox cmbWidth = new JComboBox();
		for (int i = 8; i < 256; i *= 2) {
			cmbWidth.addItem(i);
		}
		JLabel lblWidth = new JLabel("Tile Width");

		JComboBox cmbHeight = new JComboBox();
		for (int i = 8; i < 256; i *= 2) {
			cmbHeight.addItem(i);
		}
		JLabel lblHeight = new JLabel("Tile Height");

		JTextField spriteCount = new JTextField(4);
		JLabel lblSpriteCount = new JLabel("Sprite Count");

		GridBagLayout gbl = new GridBagLayout();
		pane.setLayout(gbl);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		pane.add(lblWidth, c);

		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		pane.add(cmbWidth, c);

		c.gridy++;
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		pane.add(lblHeight, c);

		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		pane.add(cmbHeight, c);

		c.gridy++;
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		pane.add(lblSpriteCount, c);

		c.gridx++;
		pane.add(spriteCount, c);

		c.gridy++;
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.5;
		pane.add(create, c);

		c.gridx++;
		pane.add(cancel, c);

		// add listeners
		cmbWidth.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				width = e.getItem();
			}
		});

		cmbHeight.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				height = e.getItem();
			}
		});

		spriteCount.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				JTextField field = ((JTextField) e.getComponent());
				spCount = field.getText();
				if (!field.getText().trim().isEmpty()) {
					create.setEnabled(true);
				}
			}
		});

		dlg.setLocationRelativeTo(frame);
		dlg.pack();
		dlg.setVisible(true);
		if (!cancelled) {
			System.out.format("%s, %sx%s\n", spCount.toString(),
					width.toString(), height.toString());
		} else {
			System.out.println("Sheet cancelled");
		}

		dlg.dispose();
	}

	public static void main(String... args) {
		JFrame frame = new JFrame();
		PixelPainter painter = new PixelPainter(
				ImageController.createNewInstance(8,8), null);
		frame.setSize(100, 100);
		frame.setLocationRelativeTo(null);

		SpriteSheetPanel panel = new SpriteSheetPanel(frame, painter);
	}
}
