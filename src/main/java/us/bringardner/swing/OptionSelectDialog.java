/**
 * <PRE>
 * 
 * Copyright Tony Bringarder 1998, 2025 <A href="http://bringardner.com/tony">Tony Bringardner</A>
 * 
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       <A href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</A>
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  </PRE>
 *   
 *   
 *	@author Tony Bringardner   
 *
 *
 * ~version~V000.01.40-V000.01.39-V000.01.27-V000.01.26-V000.01.V21_5-V000.01.21_3-V000.01.18-V000.01.17-V000.01.05-V000.01.00-
 */
package us.bringardner.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import us.bringardner.io.filesource.viewer.IRegistry;



public class OptionSelectDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public enum Response {Yes,No}

	public static final ImageIcon warningIcon = new ImageIcon(OptionSelectDialog.class.getResource("/us/bringardner/io/filesource/viewer/icons/Warning100x100.png"));
	public static final ImageIcon ErrorIcon   = new ImageIcon(OptionSelectDialog.class.getResource("/us/bringardner/io/filesource/viewer/icons/Error100x100.png"));
	public static final ImageIcon MessageIcon   = new ImageIcon(OptionSelectDialog.class.getResource("/us/bringardner/io/filesource/viewer/icons/Globe100x100.png"));



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		showInputDialog(null, null, null);
		
		
	}

	private final JPanel contentPanel = new GradientPanel(3);
	private ImageIcon icon = MessageIcon;	
	Color startColor = new Color(242, 206, 113);
	Color endColor = new Color(150, 123, 40);
	
	private boolean canceled=false;
	private String message="";
	private String yesButtonText="Yes";
	private String noButtonText="No";
	private GradientButton yesButton;
	private GradientButton noButton;
	private List<IRegistry.RegData> list;
	private JTable table;
	private JTextField textField;
	
	public String getYesButtonText() {
		return yesButtonText;
	}
	public void setYesButtonText(String yesButtonText) {
		this.yesButtonText = yesButtonText;
	}
	public String getNoButtonText() {
		return noButtonText;
	}
	public void setNoButtonText(String noButtonText) {
		this.noButtonText = noButtonText;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ImageIcon getIcon() {
		return icon;
	}
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
	public Color getStartColor() {
		return startColor;
	}
	public void setStartColor(Color startColor) {
		this.startColor = startColor;
	}
	public Color getEndColor() {
		return endColor;
	}
	public void setEndColor(Color endColor) {
		this.endColor = endColor;
	}

	public void setShowYesButton(boolean b) {
		yesButton.setVisible(b);
	}
	
	public void setShowNoButton(boolean b) {
		noButton.setVisible(b);
	}
	
	/**
	 * Create the dialog.
	 */
	public OptionSelectDialog() {
		setBounds(100, 100, 450, 264);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setBackground(startColor);
		contentPanel.setForeground(endColor);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel() {
			@Override
			public int getColumnCount() {
				return 2;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnIndex == 0 ? Icon.class:String.class;
			}
			
			@Override
			public Object getValueAt(int row, int column) {
				return column == 0 ? "":list.get(row).name;				
			}
		});
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPanel.add(panel, BorderLayout.SOUTH);
		
		JLabel lblNewLabel = new JLabel("Other");
		panel.add(lblNewLabel);
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(20);
		
		GradientButton browseButton = new GradientButton("OK");
		browseButton.setText("...");
		panel.add(browseButton);

		{
			JPanel buttonPanel = new GradientPanel(GradientPanel.DIAGONAL_RIGHT);
			buttonPanel.setOpaque(false);
			buttonPanel.setBackground(contentPanel.getBackground());
			buttonPanel.setForeground(contentPanel.getForeground());
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			{
				yesButton = new GradientButton("OK");
				yesButton.setText(yesButtonText);
				yesButton.addActionListener((e)->{
					canceled = false;
					dispose();
				});
				buttonPanel.add(yesButton);
			}
			{
				noButton = new GradientButton("Cancel");
				noButton.setText(noButtonText);
				noButton.addActionListener((e)->{
					canceled = true;
					dispose();
				});
				{
					Component horizontalStrut = Box.createHorizontalStrut(20);
					buttonPanel.add(horizontalStrut);
				}
				buttonPanel.add(noButton);
			}
		}
	}


		
	public static String showInputDialog(String message, String title,String defaultText) {
		OptionSelectDialog d = new OptionSelectDialog();
		d.setAlwaysOnTop(true);
		d.setMessage(message);
		d.setTitle(title);
		d.setNoButtonText("Cancel");
		d.setYesButtonText("Okay");
		
		return null;
	}
}
