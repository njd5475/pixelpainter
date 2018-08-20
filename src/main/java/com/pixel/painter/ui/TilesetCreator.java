package com.pixel.painter.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.pixel.painter.controller.TilesetController;
import com.pixel.painter.ui.dialog.NewImagePanel;

public class TilesetCreator extends JPanel {

	private static JFileChooser		chooser;
	private static AtomicBoolean	chooserLoaded	= new AtomicBoolean(false);

	static {

		(new Thread() {
			public void run() {
				chooser = new JFileChooser();
				chooserLoaded.set(true);
			}
		}).start();
	}

	private BufferedImage		currentImage;
	private TilesetController	ctrl;
	private JTextField			width;
	private JTextField			height;
	private JTextField			imageWidth;
	private JTextField			imageHeight;
	private boolean				created;

	public TilesetCreator() {
		super();
		setPreferredSize(new Dimension(640, 480));

		JButton create = new JButton("Create");
		JLabel lblWidth = new JLabel("Width");
		JLabel lblHeight = new JLabel("Height");
		width = new JTextField(10);
		width.setText(String.valueOf(1024));
		height = new JTextField(10);
		height.setText(String.valueOf(1024));

		JLabel lblImageWidth = new JLabel("Image Width");
		JLabel lblImageHeight = new JLabel("Image Height");
		imageWidth = new JTextField(10);
		imageHeight = new JTextField(10);

		GroupLayout g = new GroupLayout(this);
		setLayout(g);
		g.setAutoCreateContainerGaps(true);
		g.setAutoCreateGaps(true);
		g.setHorizontalGroup(g.createParallelGroup()
				.addGroup(g.createSequentialGroup()
						.addGroup(g.createParallelGroup().addComponent(lblWidth).addComponent(lblHeight)
								.addComponent(lblImageWidth).addComponent(lblImageHeight))
				.addGroup(g.createParallelGroup()
						.addComponent(width, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(height, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(imageWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(imageHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)))
				.addComponent(create, Alignment.CENTER));
		g.setVerticalGroup(g.createSequentialGroup()
				.addGroup(g.createParallelGroup(Alignment.BASELINE).addComponent(lblWidth).addComponent(width))
				.addGroup(g.createParallelGroup(Alignment.BASELINE).addComponent(lblHeight).addComponent(height))
				.addGroup(
						g.createParallelGroup(Alignment.BASELINE).addComponent(lblImageWidth).addComponent(imageWidth))
				.addGroup(
						g.createParallelGroup(Alignment.BASELINE).addComponent(lblImageHeight).addComponent(imageHeight))
				.addComponent(create));

		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewImage(Integer.parseInt(imageWidth.getText()), Integer.parseInt(imageHeight.getText()));
			}
		});

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				TilesetCreator.this.requestFocus();
			}
		});

		this.setTransferHandler(new TransferHandler() {

			@Override
			public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
				for (DataFlavor df : transferFlavors) {
					// System.out.println(transferFlavors[0].toString() + "\t" +
					// transferFlavors.length);
					if (df.equals(DataFlavor.javaFileListFlavor)) {
						return true;
					}
				}
				return super.canImport(comp, transferFlavors);
			}

			@Override
			public boolean importData(TransferSupport support) {

				try {
					List list = (List) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

					for (Object o : list) {
						if (o instanceof File) {
							File f = (File) o;

							openNewFile(f);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return super.importData(support);
			}
		});

	}

	protected void openNewFile(File f) throws IOException {
		currentImage = ImageIO.read(f);
		Dimension dlgImageSize = NewImagePanel.showAsDialog();
		create(currentImage, dlgImageSize.width, dlgImageSize.height);
	}

	public void createNewImage(int imageWidth, int imageHeight) {
		int width = Integer.parseInt(this.width.getText());
		int height = Integer.parseInt(this.height.getText());

		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		create(newImage, imageWidth, imageHeight);
		Graphics2D g = currentImage.createGraphics();
		g.fillRect(0, 0, width, height);
		g.dispose();

		create(newImage, imageWidth, imageHeight);
	}

	public void create(BufferedImage bImg, int imageWidth, int imageHeight) {
		currentImage = bImg;
		if (ctrl == null) {
			ctrl = new TilesetController(currentImage, imageWidth, imageHeight);
		}

		this.removeAll();
		this.invalidate();
		this.setLayout(new BorderLayout());
		TilesetImageViewer viewer;
		this.add(viewer = new TilesetImageViewer(currentImage, ctrl));
		this.revalidate();
		repaint();

		viewer.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (created && e.getKeyCode() == KeyEvent.VK_S) {
					while (chooserLoaded.get() == false) {
						try {
							Thread.currentThread().wait(10);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}

					int retVal = chooser.showSaveDialog(TilesetCreator.this);

					if (retVal == JFileChooser.APPROVE_OPTION) {
						File f = chooser.getSelectedFile();
						try {
							ImageIO.write(currentImage, "PNG", f);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(TilesetCreator.this, "Error while saving: " + e1.getMessage(),
									"Saving Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}

				System.out.println("Key Pressed");
			}

		});

		viewer.requestFocus();
		// state variable
		created = true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndDisplay();
			}
		});
	}

	private static void createAndDisplay() {
		JFrame frame = new JFrame("Tileset Create");
		frame.setLayout(new BorderLayout());
		TilesetCreator creator;
		frame.add(creator = new TilesetCreator());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
