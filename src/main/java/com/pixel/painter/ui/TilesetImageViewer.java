package com.pixel.painter.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import com.pixel.painter.controller.ImageController;
import com.pixel.painter.controller.TilesetController;

public class TilesetImageViewer extends JPanel {

	private BufferedImage		currentImage;
	private TilesetController	ctrl;
	protected Rectangle			tileRect;

	public TilesetImageViewer(BufferedImage currentImage,
			TilesetController controller) {
		this.currentImage = currentImage;
		this.ctrl = controller;

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				setCurrentTile(e.getX(), e.getY());
				repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				setCurrentTile(e.getX(), e.getY());
				repaint();
			}
		});

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					editTile();
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					deleteTile();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		this.setTransferHandler(new TransferHandler() {

			@Override
			public boolean canImport(JComponent comp,
					DataFlavor[] transferFlavors) {
				for (DataFlavor df : transferFlavors) {
					if (df.equals(DataFlavor.javaFileListFlavor)) {
						return true;
					}
				}
				return super.canImport(comp, transferFlavors);
			}

			@Override
			public boolean importData(TransferSupport support) {

				try {
					List list = (List) support.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);

					for (Object o : list) {
						if (o instanceof File) {
							File f = (File) o;

							loadFileToTile(f);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return super.importData(support);
			}
		});

		DropTarget dt = new DropTarget() {

			@Override
			public synchronized void dragOver(DropTargetDragEvent dtde) {
				Point location = dtde.getLocation();
				setCurrentTile(location.x, location.y);
				repaint();
				super.dragOver(dtde);
			}

			@Override
			public synchronized void drop(DropTargetDropEvent dtde) {
				DataFlavor[] transferFlavors = dtde.getCurrentDataFlavors();
				for (DataFlavor df : transferFlavors) {
					if (df.equals(DataFlavor.javaFileListFlavor)) {
						dtde.acceptDrop(DnDConstants.ACTION_COPY);
						try {
							List list = (List) dtde.getTransferable()
									.getTransferData(
											DataFlavor.javaFileListFlavor);
							for (Object o : list) {
								if (o instanceof File) {
									File f = (File) o;

									loadFileToTile(f);
								}
							}
							return;
						} catch (UnsupportedFlavorException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				dtde.rejectDrop();
			}

		};

		this.setDropTarget(dt);

		this.requestFocus();
	}

	public void saveFile() {
		JFileChooser chooser = new JFileChooser();
		int showSaveDialog = chooser.showSaveDialog(TilesetImageViewer.this);
		if (showSaveDialog == JFileChooser.APPROVE_OPTION) {
			try {
				ImageIO.write(TilesetImageViewer.this.currentImage, "PNG",
						chooser.getSelectedFile());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	protected void loadFileToTile(File f) {
		try {
			Point p = this.getMousePosition();

			BufferedImage read = ImageIO.read(f);
			Graphics2D g = currentImage.createGraphics();
			g.drawImage(read, tileRect.x, tileRect.y, tileRect.x
					+ tileRect.width, tileRect.y + tileRect.height, 0, 0,
					read.getWidth(), read.getHeight(), null);
			g.dispose();
			repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void deleteTile() {
		Graphics graphics = currentImage.getGraphics();
		graphics.clearRect(tileRect.x, tileRect.y, tileRect.width,
				tileRect.height);
		graphics.dispose();
		repaint();
	}

	protected void editTile() {
		PixelPainter painter = new PixelPainter(new ImageController(
				currentImage.getSubimage(tileRect.x, tileRect.y,
						tileRect.width, tileRect.height), true), null);
		PixelPainter.createAndDisplay(painter, JFrame.DISPOSE_ON_CLOSE);
	}

	protected void setCurrentTile(int x, int y) {
		AffineTransform transform = new AffineTransform();
		transform.scale(this.getWidth()/(double)currentImage.getWidth(), this.getHeight()/(double)currentImage.getHeight());
		
		int col = x / ctrl.getTileWidth();
		int row = y / ctrl.getTileHeight();
		int tw = ctrl.getTileWidth();
		int th = ctrl.getTileHeight();
		tileRect = new Rectangle(col * tw, row * th, tw, th);
	}

	public void paintComponent(Graphics init) {
		Graphics2D g = (Graphics2D) init.create();
		g.drawImage(currentImage, 0, 0, this.getWidth(), this.getHeight(), null);
		if (tileRect != null) {
			g.setColor(Color.gray);
			g.draw(tileRect);
		}
		g.dispose();
	}

}
