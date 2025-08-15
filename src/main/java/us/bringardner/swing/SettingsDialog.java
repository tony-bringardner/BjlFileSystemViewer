package us.bringardner.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIDefaults;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import us.bringardner.io.filesource.viewer.FileSourceViewer;

public class SettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new GradientPanel(GradientPanel.DIAGONAL_RIGHT);
	private JPanel buttonPane;
	private JLabel lblNewLabel;
	private JLabel textLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SettingsDialog dialog = new SettingsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SettingsDialog() {
		setBounds(100, 100, 531, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setBackground(FileSourceViewer.GradiantPanelStartColor);
		contentPanel.setForeground(FileSourceViewer.GradiantPanelEndColor);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel centerPanel = new JPanel();
		centerPanel.setBackground(FileSourceViewer.TRANSPARENT);
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(null);
		
		textLabel = new JLabel("<html>The quick brown fox jumped over the lazy dog.</html>");
		textLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actionFont();
			}
		});
		textLabel.setBorder(new TitledBorder(null, "Font", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		textLabel.setBounds(122, 14, 283, 94);
		textLabel.setOpaque(true);
		
		//textLabel.setBackground(FileSourceViewer.TRANSPARENT);
		centerPanel.add(textLabel);
		
		JSlider slider = new JSlider();
		slider.setBorder(new TitledBorder(null, "Color Theme", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		slider.setBounds(80, 141, 365, 53);
		slider.setOpaque(true);
		//slider.setBackground(FileSourceViewer.TRANSPARENT);
		centerPanel.add(slider);
		
		lblNewLabel = new JLabel("Start Color");
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actionStartColor();
			}
		});
		lblNewLabel.setBounds(6, 159, 87, 16);
		centerPanel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("End Color");
		lblNewLabel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actionEndColor();
			}
		});
		lblNewLabel_1.setBounds(454, 159, 61, 16);
		centerPanel.add(lblNewLabel_1);

		buttonPane = new GradientPanel(GradientPanel.DIAGONAL_RIGHT);
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		
		GradientButton okButton = new GradientButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionOk();
			}
		});
		buttonPane.add(okButton);
		GradientButton cancelButton = new GradientButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionCancel();
			}
		});
		buttonPane.add(cancelButton);
		buttonPane.setBackground(FileSourceViewer.GradiantPanelStartColor);
		buttonPane.setForeground(FileSourceViewer.GradiantPanelEndColor);
		
	}

	protected void actionCancel() {
		dispose();
		
	}

	protected void actionOk() {
		dispose();
		
	}

	protected void actionFont() {
		FontDialog dialog = new FontDialog();
		Font font = dialog.showDialog(textLabel.getFont());
		textLabel.setFont(font);
		
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		for(Object key : defaults.keySet()) {
			Object value = defaults.get(key);
			if (value instanceof Font) {
				defaults.put(key, font);				
			}
		}		
	}

	protected void actionEndColor() {
		Color c = JColorChooser.showDialog(this, "End Color", contentPanel.getForeground(), true);
		if( c != null ) {
			contentPanel.setForeground(c);	
			buttonPane.setForeground(c);
		};		
		
	}

	protected void actionStartColor() {
		Color c = JColorChooser.showDialog(this, "Start Color", contentPanel.getBackground(), true);
		if( c != null ) {
			contentPanel.setBackground(c);
			buttonPane.setBackground(c);
		}
				
	}
}
