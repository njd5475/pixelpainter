package com.pixel.painter.animation;

import java.awt.Image;

import javax.swing.JFrame;

import com.pixel.painter.controller.SpriteController;
import com.pixel.painter.ui.PixelPainter;

public class PreviewAnimator {

	private Thread				animation;
	private JFrame				frame;
	private PixelPainter		painter;
	protected boolean			stopped;
	private SpriteController	spriteCtrl;

	public PreviewAnimator(PixelPainter painter, JFrame frame2) {
		this.painter = painter;
		this.frame = frame2;
		spriteCtrl = painter.getSpriteController();
	}

	public void startAnimator() {
		animation = new Thread(new Runnable() {

			static final int	DEFAULT_FRAME_TIME	= 1000;
			long				time;
			long				lasttime			= System.currentTimeMillis();
			private int			frameTime;
			private int			frameIndex;

			Object				lock				= new Object();

			@Override
			public void run() {
				System.out.println("Animation Started");
				while (!stopped) {
					synchronized (lock) {

						time = System.currentTimeMillis();

						long deltaMs = time - lasttime;

						lasttime = time;

						frameTime -= deltaMs;

						Image[] images = spriteCtrl.getFrames();

						if (images.length > 0) {
							if (frameTime < 0) {
								frameTime = DEFAULT_FRAME_TIME;
								++frameIndex;
								if (frameIndex >= images.length) {
									frameIndex = 0;
								}
							}

							painter.setPreviewImage(images[frameIndex]);
						}
						frame.repaint();

						try {
							lock.wait(16);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				System.out.println("Animation Stopped");
			}

		});

		animation.start();
	}

	public void stop() {

		stopped = true;
	}
}
