package com.pixel.painter.palettes;

import static com.pixel.painter.palettes.RemotePalettes.retreive;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.palettes.RemotePalettes.COLOR_NUMBER_FILTER_TYPE;
import com.pixel.painter.palettes.RemotePalettes.SORTING_TYPES;
import com.pixel.painter.ui.MaterialComponentBuilder;
import com.pixel.painter.ui.PixelPainter;
import com.pixel.painter.ui.materials.Material;
import com.pixel.painter.ui.materials.MaterialActionHandler;
import com.pixel.painter.ui.materials.MaterialBuilder;
import com.pixel.painter.ui.materials.MaterialBuilderBase;

public class FindPaletteWindow extends JDialog implements ListDataListener {

	private Thread thread;
	private JProgressBar bar;
	private ColorPalette[] palettes;
	private JPanel mainPanel = new JPanel();
	private PaletteManager manager;
	private SpinnerNumberModel numColorsModel;
	private DefaultComboBoxModel<COLOR_NUMBER_FILTER_TYPE> colorFilterOptionsModel;
	private SpinnerNumberModel pageModel;
	private DefaultComboBoxModel<SORTING_TYPES> colorSortingModel;

	public FindPaletteWindow(JFrame parent, PaletteManager manager) {
		super(parent);
		this.manager = manager;
		this.setTitle("Find a palette");
		this.setMinimumSize(new Dimension(80, 40));
		this.setPreferredSize(new Dimension((int) (parent.getWidth() * 0.8), (int) (parent.getHeight() * 0.8)));

		this.setLayout(new BorderLayout());
		JPanel filterOptions = new JPanel();
		filterOptions.setLayout(new FlowLayout(FlowLayout.LEFT));
		JComboBox<COLOR_NUMBER_FILTER_TYPE> colorFilter = new JComboBox<>(
				this.colorFilterOptionsModel = new DefaultComboBoxModel<COLOR_NUMBER_FILTER_TYPE>(
						COLOR_NUMBER_FILTER_TYPE.values()));
		this.colorFilterOptionsModel.addListDataListener(this);
		JLabel lbl = new JLabel("Number Of Colors");
		JSpinner numColors = new JSpinner(this.numColorsModel = new SpinnerNumberModel(8, 1, Integer.MAX_VALUE, 1));
		numColors.addChangeListener(this::onChange);
		filterOptions.add(lbl);
		filterOptions.add(colorFilter);
		filterOptions.add(numColors);

		JLabel sortingLbl = new JLabel("Sort By");
		JComboBox<SORTING_TYPES> sortingFilter = new JComboBox<>(
				this.colorSortingModel = new DefaultComboBoxModel<SORTING_TYPES>(SORTING_TYPES.values()));
		this.colorSortingModel.addListDataListener(this);
		filterOptions.add(sortingLbl);
		filterOptions.add(sortingFilter);

		JLabel pageLbl = new JLabel("Page");
		JSpinner page = new JSpinner(this.pageModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		this.pageModel.addChangeListener(this::onChange);
		filterOptions.add(pageLbl);
		filterOptions.add(page);

		this.add(filterOptions, BorderLayout.NORTH);
		this.add(mainPanel, BorderLayout.CENTER);

		this.retrieve();
	}

	private void onChange(ChangeEvent e) {
		this.retrieve();
	}

	private void retrieve() {
		this.retrieve((Integer) this.pageModel.getValue(), (Integer) this.numColorsModel.getValue(),
				(COLOR_NUMBER_FILTER_TYPE) this.colorFilterOptionsModel.getSelectedItem(),
				(SORTING_TYPES) this.colorSortingModel.getSelectedItem());
	}

	private void retrieve(int page, int numColors, COLOR_NUMBER_FILTER_TYPE colorNumFilter, SORTING_TYPES sortFilter) {
		mainPanel.removeAll();
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		bar = new JProgressBar();
		bar.setIndeterminate(true);
		bar.setMinimumSize(new Dimension((int) (this.getParent().getWidth() * 0.75f),
				(int) (this.getParent().getHeight() * 0.2f)));
		mainPanel.add(bar, BorderLayout.CENTER);
		mainPanel.revalidate();
		mainPanel.repaint();

		this.thread = new Thread(() -> {
			ColorPalette[] retreive = retreive(numColors, colorNumFilter, sortFilter, page);
			javax.swing.SwingUtilities.invokeLater(() -> {
				consume(retreive);
			});
		});
		this.thread.start();
	}

	private void consume(ColorPalette[] retreive) {
		String title = String.format("Got %d palettes back from lospec", retreive.length);
		this.setTitle(title);
		this.thread = null;
		this.palettes = retreive;

		mainPanel.setLayout(new GridLayout(retreive.length, 2));
		mainPanel.removeAll();
		mainPanel.remove(bar);
		mainPanel.revalidate();
		for (ColorPalette cp : this.palettes) {
			JComponent label = new JComponent() {
				public void paintComponent(Graphics g) {
					Rectangle r = this.getBounds();
					r.translate(-(int) r.getX(), -(int) r.getY());
					ColorPaletteRenderer.render((Graphics2D) g, r, cp, palettes);
					super.paintComponent(g);
				}
			};

			mainPanel.add(label);
			System.out.println(cp.getName());
		}
		mainPanel.add(new JButton(this.nextAction()));
		mainPanel.revalidate();
		mainPanel.repaint();
		this.pack();
	}

	private Action nextAction() {
		Action a = new AbstractAction("Next Page") {

			@Override
			public void actionPerformed(ActionEvent e) {
				FindPaletteWindow.this.pageModel.setValue(FindPaletteWindow.this.pageModel.getNextValue());
			}
		};
		return a;
	}

	private JComponent buildPaletteComponent(JComponent comp, ColorPalette cp) {

		MaterialComponentBuilder builder = new MaterialComponentBuilder(comp);
		MaterialBuilder matBuilder = new MaterialBuilderBase(comp);

		Color[] colors = cp.getColors();
		MaterialActionHandler noop = (m, s) -> {
		};

		Set<String> group = new HashSet<>();
		for (Color color : colors) {
			String name = color.toString();
			PixelPainter.getColorMaterial(matBuilder, name, color, noop, noop, noop);
			group.add(name);
		}

		String names[] = group.toArray(new String[group.size()]);
		matBuilder.push();
		Material matColors = matBuilder.top(5.0f).text(String.format("Name: %s", cp.getName()), Color.orange)
				.container(names).resizableComponents().build("PaletteMaterial");
		return builder.wrap(matColors);
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		this.retrieve();
	}

}
