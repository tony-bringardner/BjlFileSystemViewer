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
 * ~version~V000.01.22-V000.01.10-V000.01.08-V000.00.01-V000.00.00-
 */
package us.bringardner.io.filesource.viewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import us.bringardner.core.BaseThread;
import us.bringardner.io.filesource.FileSource;
import us.bringardner.io.filesource.fileproxy.FileProxyFactory;
import us.bringardner.io.filesource.viewer.registry.MacRegistry;
import us.bringardner.io.filesource.viewer.registry.WindowsRegistry;

/**
 * If the file is remote we'll download a copy and monitor
 * it for changes.
 * 
 * @author Tony Bringardner
 *
 */
public class EditMonitorThread extends BaseThread {
	//We could use the java desktop but then we have no way to monitor the editing process
	private static IRegistry registry;


	public static IRegistry getRegistry() {
		if( registry == null ) {
			String os = System.getProperty("os.name");
			if( os.startsWith("Mac")) {
				registry = new MacRegistry();
			} else if( os.startsWith("Win")) {
				registry = new WindowsRegistry();
			} else {
				registry = new IRegistry() {
					List<IRegistry.RegData> empty = new ArrayList<>();
					
					public List<IRegistry.RegData> getRegisteredHandler(String file) {
						return empty;
					}					
				};
			}
		}

		return registry;
	}

	private File local;
	private FileSource remote;
	private FileSourceViewer browser;
	
	public EditMonitorThread(FileSourceViewer browser,FileSource remote, File local) {
		this.local = local;
		this.remote = remote;
		this.browser = browser;
	}

	
	public void run() {
		running = true;
		long maxWait = 30000;
		try {
			long start = System.currentTimeMillis();
			while( local.length() != remote.length()) {
				try {
					Thread.sleep(100);				
				} catch (InterruptedException e) {
				}
				
				if( System.currentTimeMillis()-start > maxWait) {
					int res = JOptionPane.showConfirmDialog(browser.getFrame(), "The downlaod of "+remote.getName()+" has not completed.\nWould you like to continue waiting?");
					if( res == JOptionPane.OK_OPTION) {
						start = System.currentTimeMillis();
					} else {
						return;
					}
				}
			}
		} catch (IOException e1) {
			browser.showError("Error validating download", e1);
			return;
		}

		long len = local.length();
		long lastMod = local.lastModified();

		Process process = startEditor();

		if( process == null ) {
			JOptionPane.showMessageDialog(browser.getFrame(), "No Editor availible for "+remote.getName()+"\ntry 'open' instead.","",JOptionPane.ERROR_MESSAGE);
			return;
		}

		boolean error = false;

		while( running ) {
			try {
				Thread.sleep(100);				
			} catch (InterruptedException e) {
			}

			if( local.length() != len || local.lastModified() != lastMod) {
				int res = JOptionPane.showConfirmDialog(browser.getFrame(), local.getName()+" has changed.\nWould you like to replace the original file?");
				if( res == JOptionPane.OK_OPTION) {
					//CopyDialog dialog = new CopyDialog(browser.getFrame());
					List<CopyTransaction> txs = new ArrayList<CopyTransaction>();
					try {
						txs.add(new CopyTransaction(FileProxyFactory.getFileSource(local.getAbsolutePath()), remote, null, browser));
						//dialog.show(txs,browser,true,showHidden,showExtention);
					} catch (Throwable e) {
						error = true;					
						JOptionPane.showMessageDialog(browser.getFrame(), "Could not replace "+remote,"",JOptionPane.ERROR_MESSAGE);
					}
				}
				lastMod = local.lastModified();
				len = local.length();
			}
			try {
				process.exitValue();
				//  no error means application has terminated;
				stop();
			} catch(Throwable e) {

			}
		}

		if(!error && local.length() != len || local.lastModified() != lastMod) {
			int res = JOptionPane.showConfirmDialog(browser.getFrame(), local.getName()+" has changed.\nWould you like to replace the original file?");
			if( res == JOptionPane.OK_OPTION) {
				//CopyDialog dialog = new CopyDialog(browser.getFrame());
				List<CopyTransaction> txs = new ArrayList<CopyTransaction>();
				try {
					txs.add(new CopyTransaction(FileProxyFactory.getFileSource(local.getAbsolutePath()), remote, null, browser));
					//dialog.show(txs,browser,true,showHidden,showExtention);
				} catch (Throwable e) {
					JOptionPane.showInternalMessageDialog(browser.getFrame(), "Could not replace "+remote,"",JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		try {
			local.delete();
		} catch (Exception e) {
		}
		running = false;
	}


	private Process startEditor() {
		Process ret = null;
		List<IRegistry.RegData> list = getRegistry().getRegisteredHandler(local.getAbsolutePath());
		String path = local.getAbsolutePath();
		for (IRegistry.RegData exe : list) {
			try {
				ret = Runtime.getRuntime().exec(exe.path+" "+path);
				break;
			} catch (IOException e) {
				logDebug("Error executing "+exe, e);
			}

		}

		return ret;
	}

	
}
