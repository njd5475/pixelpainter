package com.pixel.painter.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class CreateFontImages extends JComponent {

	private static int				PREVIEW_NUMBER	= 100;

	private static JFrame			frame;

	private static final Dimension	size			= new Dimension(25, 25);

	private static final Color		battOutline		= new Color(155, 155, 155);
	private static final int		textOffset		= 6;
	private static GeneralPath		battery			= new GeneralPath();

	static {

		double h = 9;
		double a = 2;
		battery.moveTo(0, size.height / 2.0 + h / a);
		battery.lineTo(0, size.height / 2.0 + h / a);
		battery.lineTo(4, size.height / 2.0 + h / a);
		battery.lineTo(4, size.height / 2.0 + h);
		battery.lineTo(size.width, size.height / 2.0 + h);
		battery.lineTo(size.width, size.height / 2.0 - h);
		battery.lineTo(4, size.height / 2.0 - h);
		battery.lineTo(4, size.height / 2.0 - h / a);
		battery.lineTo(0, size.height / 2.0 - h / a);
		battery.lineTo(0, size.height / 2.0 + h / a);
	}

	public CreateFontImages() {
		setPreferredSize(new Dimension(640, 480));
	}

	@Override
	public void paintComponent(Graphics init) {
		super.paintComponent(init);
		Graphics2D g = (Graphics2D) init.create();
		g.translate(getWidth() / 2 - size.width / 2, getHeight() / 2
				- size.height / 2);
		g.setColor(Color.red);
		g.drawRect(0, 0, size.width, size.height);
		drawLayers(g, size, PREVIEW_NUMBER);
	}

	public static void writeNumbers(File dir, int min, int max, Dimension size) {
		for (int i = min; i < max; ++i) {
			File file = new File(dir, "batt_" + i + ".png");
			drawToFile(file, i, size);
		}
	}

	public static void drawToFile(File file, int number, Dimension size) {
		// crop the image
		BufferedImage b = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = b.createGraphics();
		drawLayers(g, size, number);
		g.dispose();

		try {
			ImageIO.write(b, "PNG", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void drawLayers(Graphics2D g, Dimension size, int number) {
		drawBackground(g, size, number);
		g.setColor(Color.white);
		drawString(g, number, size);
	}

	private static void drawBackground(Graphics2D g, Dimension size, int number) {
		// g.translate(size.width / 2, size.height / 2);
		// g.scale(1, 1);
		// g.translate(-size.width / 2, -size.height / 2);
		g.setPaint(new GradientPaint(0, 0, Color.DARK_GRAY, 0, size.height,
				Color.LIGHT_GRAY));
		g.fill(battery);

		// in-fill
		g.setClip(battery);
		g.setPaint(new GradientPaint(0, 0, new Color(0, number == 100 ? 255
				: 225, 0), 0, size.height, new Color(0, 70, 0)));
		g.fillRect((int) (squeze(1 - number / 100.0) * size.width), 0,
				size.width, size.height);

		// draw outline
		g.setStroke(new BasicStroke(2));
		g.setColor(battOutline);
		g.draw(battery);
	}

	private static double squeze(double x) {
		System.out.println(x);
		return Math.tan((x - 0.51) * Math.PI / 1.15) / 10 + 0.5;
	}

	public static void drawString(Graphics2D g, int number, Dimension size) {
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setFont(g.getFont().deriveFont(Font.BOLD, 12.0f));
		FontMetrics m = g.getFontMetrics();
		String toDraw = String.valueOf(number);
		if (number < 100) {
			g.drawString(toDraw, textOffset, size.height / 2 + m.getHeight()
					/ 4);
		} else {
			g.drawString(toDraw, textOffset - 4,
					size.height / 2 + m.getHeight() / 4);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndDisplay(new CreateFontImages(), JFrame.EXIT_ON_CLOSE);
			}
		});
		// writeNumbers(new File("."), 0, 101);
		// String t =
		// "<item android:maxLevel=\"%d\" android:drawable=\"@drawable/batt_%d\" />";
		// for (int i = 0; i < 100; ++i) {
		// System.out.format(t + "\n", i, i);
		// }
	}

	public static void createAndDisplay(CreateFontImages createFontImages,
			int closeOp) {
		frame = new JFrame("Battery Widget Status Bar Icon Creation");
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					PREVIEW_NUMBER++;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					PREVIEW_NUMBER--;
				}

				if (PREVIEW_NUMBER > 100) {
					PREVIEW_NUMBER = 100;
				} else if (PREVIEW_NUMBER < 0) {
					PREVIEW_NUMBER = 0;
				}
				System.out.println("Key Is Being Pressed");
				frame.repaint();
			}
		});
		setupMenus(frame);
		frame.setLayout(new BorderLayout());
		frame.add(createFontImages);
		frame.pack();
		frame.setDefaultCloseOperation(closeOp);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static void setupMenus(final JFrame frame2) {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		final JMenuItem createImage = new JMenuItem("Create Images");
		file.add(createImage);
		createImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createImage.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						writeNumbers(new File("."), 0, 101, size);
						JOptionPane.showMessageDialog(frame2,
								"Images Finished Writing!");
						createImage.setEnabled(true);
					}
				}).start();

			}
		});
		menuBar.add(file);

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame2.setVisible(false);
				frame2.dispose();
				System.exit(0);
			}
		});
		file.addSeparator();
		file.add(exit);
		frame2.setJMenuBar(menuBar);
	}
}
