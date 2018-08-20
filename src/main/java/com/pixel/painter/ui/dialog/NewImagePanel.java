package com.pixel.painter.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pixel.painter.model.ApplicationSettings;

public class NewImagePanel extends JPanel {

  private GroupLayout gl = null;

  JTextField          inputWidth, inputHeight;

  public NewImagePanel() {
    gl = new GroupLayout(this);
    setLayout(gl);
    JLabel lblWidth, lblHeight;
    inputWidth = new JTextField(10);
    inputHeight = new JTextField(10);
    lblWidth = new JLabel("Width");
    lblHeight = new JLabel("Height");

    gl.setAutoCreateContainerGaps(true);
    gl.setAutoCreateGaps(true);
    gl.setHorizontalGroup(gl.createSequentialGroup()
        .addGroup(gl.createParallelGroup().addComponent(lblWidth)
            .addComponent(lblHeight))
        .addGroup(gl.createParallelGroup().addComponent(inputWidth)
            .addComponent(inputHeight)));
    gl.setVerticalGroup(gl.createSequentialGroup()
        .addGroup(gl.createParallelGroup().addComponent(lblWidth)
            .addComponent(inputWidth))
        .addGroup(gl.createParallelGroup().addComponent(lblHeight)
            .addComponent(inputHeight)));

    gl.linkSize(inputWidth, inputHeight);
    gl.linkSize(lblWidth, lblHeight);
  }

  public int getImageWidth() {
    return Integer.parseInt(inputWidth.getText());
  }

  public int getImageHeight() {
    return Integer.parseInt(inputHeight.getText());
  }

  public static Dimension showAsDialog() {
    if (!ApplicationSettings.getInstance().isDefaultImageSizeSet()) {
      final AtomicBoolean done = new AtomicBoolean(false);
      final JFrame frame = new JFrame("Test NewImagePanel");
      frame.setLayout(new BorderLayout());
      NewImagePanel image;
      frame.add(image = new NewImagePanel(), BorderLayout.CENTER);
      frame.add(new JButton(new AbstractAction("Create") {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          frame.setVisible(false);
          frame.dispose();
          done.set(true);
        }
      }), BorderLayout.SOUTH);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
      Object lockWait = new Object();
      synchronized (lockWait) {
        while (!done.get()) {
          try {
            lockWait.wait(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      ApplicationSettings.getInstance()
          .setDefaultImageSize(image.getImageWidth(), image.getImageHeight());
      return new Dimension(image.getImageWidth(), image.getImageHeight());
    } else {
      return ApplicationSettings.getInstance().getDefaultImageSize();
    }
  }

  public static void main(String... args) {
    NewImagePanel.showAsDialog();
  }
}
