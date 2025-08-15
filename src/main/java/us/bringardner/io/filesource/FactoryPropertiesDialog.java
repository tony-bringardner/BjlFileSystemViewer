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
 * ~version~V000.01.41-V000.01.18-V000.00.01-V000.00.00-
 */
/*
 * ConnectioinPropertiesDialog.java
 *
 * 
 */

package us.bringardner.io.filesource;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import us.bringardner.swing.MessageDialog;

/**
 *
 * @author  Tony Bringardner
 */
public class FactoryPropertiesDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	private boolean cancel;
	private FileSourceFactory factory;
	private Component edit;
	private boolean testing;
	private boolean accepted;
	private javax.swing.JButton cancelButton;
	private javax.swing.JPanel centerPanel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JButton okButton;
	private javax.swing.JPanel southPanel;
	private javax.swing.JButton testButton;
	private JPanel northPanel;
	private JComboBox<String> comboBox;

	
	/** Creates new form ConnectioinPropertiesDialog */
	public FactoryPropertiesDialog() {
		super();
		setModal(true);
		initComponents();
	}



	public boolean isCancel() {
		return cancel;
	}



	public FileSourceFactory getFactory() {
		return factory;
	}



	private void initComponents() {

		southPanel = new javax.swing.JPanel();
		testButton = new javax.swing.JButton();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		centerPanel = new javax.swing.JPanel();
		jLabel1 = new JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		testButton.setText("Test Connection");
		testButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				testButtonActionPerformed(evt);
			}
		});
		southPanel.add(testButton);

		okButton.setText("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});
		southPanel.add(okButton);

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
		southPanel.add(cancelButton);

		getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

		centerPanel.setLayout(null);

		jLabel1.setText("There are no connection properties for the FileSource Factory.");
		centerPanel.add(jLabel1);
		jLabel1.setBounds(70, 120, 325, 16);

		getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

		northPanel = new JPanel();
		getContentPane().add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new BorderLayout(0, 0));

		comboBox = new JComboBox<String>();

		comboBox.setModel(new DefaultComboBoxModel<String>(getFactoryIds()));
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {				
				setFactory(
						FileSourceFactory.getFileSourceFactory(comboBox.getSelectedItem().toString())
						);
			}
		});


		northPanel.add(comboBox, BorderLayout.WEST);

		pack();
	}

	/**
	 * 
	 * @return list of all remote file systems 
	 */
	private String [] getFactoryIds() {
		String[] avail = FileSourceFactory.getRegisterdFactories();
		List<String> list = new ArrayList<String>();
		String proxy = FileSourceFactory.fileProxyFactory.getTypeId();
		for (String nm : avail) {
			if( !proxy.equals(nm)) {
				list.add(nm);
			}
		}

		if( list.size() == 0) {
			list.add(proxy);
		}

		return list.toArray(new String[list.size()]);
	}



	private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if( !testing ) {
			if (edit instanceof IConnectionPropertiesEditor) {
				IConnectionPropertiesEditor tmp = (IConnectionPropertiesEditor) edit;

				testConnect(tmp.getProperties(),new Runnable() {

					@Override
					public void run() {
						MessageDialog.showMessageDialog( "Connected to "+factory.getTitle(), "");
					}
				}, new Runnable() {

					@Override
					public void run() {
						MessageDialog.showWarringDialog("Could not connect to "+factory.getTitle(),"");

					}
				});
			}
		}
	}

	private synchronized void testConnect(final Properties properties, final Runnable ok, final Runnable failed)  {
		testing = true;
		final Cursor cuurent = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					if( factory.connect(properties)) {
						factory.listRoots();
						if( ok != null && !isCancel()) {
							ok.run();
						}
					} else {
						factory.disConnect();
						if( failed != null && !isCancel()) {
							failed.run();
						}
					}
				}  catch (Exception e) {
					if( !isCancel()) {
						if( failed != null && !isCancel()) {
							failed.run();
						} else {
							MessageDialog.showErrorDialog( e.getMessage(), "Could not connect");
						}
					}
				}	

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						setCursor(cuurent);
					}
				});

				testing = false;
			}
		}).start();

	}



	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.cancel = true;
		dispose();
	}

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if( !testing ) {
			
			if (edit instanceof IConnectionPropertiesEditor) {
				IConnectionPropertiesEditor tmp = (IConnectionPropertiesEditor) edit;
				testConnect(tmp.getProperties(),new Runnable() {

					@Override
					public void run() {
						accepted = true;
						dispose();
					}
				}, new Runnable() {

					@Override
					public void run() {
						MessageDialog.showErrorDialog("Could not connect to "+factory.getTitle(),"");
					}
				});

			} else {
				accepted = true;
				dispose();
			}
		}
	}


	public void setFactory(FileSourceFactory factory) {
		this.factory = factory;
		Container content = getContentPane();

		if( edit != null ) {
			content.remove(edit);
		} else {
			content.remove(centerPanel);	
		}
		edit = factory.getEditPropertiesComponent();

		if (edit == null) {
			PropertyPanel p = new PropertyPanel();
			p.setProperties(factory);
			edit = p;
		}
		content.add(edit, java.awt.BorderLayout.CENTER);
		pack();
		invalidate();

	}

	public void showDialog() {
		showDialog(null);
	}
	
	public void showDialog(FileSourceFactory factory) {
		this.factory = factory;
		setLocationRelativeTo(null);
		String[] fids = getFactoryIds();
		setFactory(FileSourceFactory.getFileSourceFactory(fids[0]));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if( ! accepted ) {
					//  if the users closes the window without pressing ok or cancel we cancel the operation
					cancel = true;
				}
			}
			
		});
		setVisible(true);
		if(!cancel) {
			if (edit instanceof IConnectionPropertiesEditor) {
				IConnectionPropertiesEditor tmp = (IConnectionPropertiesEditor) edit;
				Properties prop = tmp.getProperties();
				factory.setConnectionProperties(prop);
			}
		}
	}



}