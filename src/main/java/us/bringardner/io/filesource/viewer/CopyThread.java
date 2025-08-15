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
 * ~version~V000.01.39-V000.01.08-V000.00.01-V000.00.00-
 */
package us.bringardner.io.filesource.viewer;

import java.io.InputStream;
import java.io.OutputStream;

import us.bringardner.core.BaseThread;
import us.bringardner.io.filesource.FileSource;

public class CopyThread extends BaseThread {

	private FileSource source;
	private FileSource dest;
	private FileSourceViewer.CopyProgressListner listener;
	private int bufferSize=1024;

	public CopyThread(FileSource source, FileSource dest, FileSourceViewer.CopyProgressListner listener) {
		this.source = source;
		this.dest  = dest;
		this.listener = listener;
	}

	public void run() {
		running = started = true;
		InputStream in = null;
		OutputStream out = null;
		
		byte [] buffer = new byte[bufferSize];
		try {
			
			FileSource target = dest;
			if( dest.isDirectory()) {
				target = dest.getChild(source.getName());
			}
			in = source.getInputStream();
			out = target.getOutputStream();
			long bytesCopied = 0;
			
			if( listener != null ) {
				listener.copyStarted(source.length());
			}
			while(!stopping && running ) {
				if( listener !=null ) {
					if(listener.isCanceled()) {
						stop();
						continue;
					}
					while(listener.isPaused()) {
						Thread.sleep(10);
					}
				}
				int got = in.read(buffer);
				if( got < 0 ) {
					stop();	
				} else if( got > 0) {
					if( listener !=null ) {
						if(listener.isCanceled()) {
							stop();
							continue;
						}
						while(listener.isPaused()) {
							Thread.sleep(10);
						}
					}
					
					out.write(buffer, 0, got);
					bytesCopied+=got;
					if( listener != null ) {
						listener.updateProgress(bytesCopied);
					}
					if( listener !=null ) {
						if(listener.isCanceled()) {
							stop();
						}
						while(listener.isPaused()) {
							Thread.sleep(10);
						}
					}					
				}
			}

		} catch (Exception e) {
			if( listener != null ) {
				listener.error(e);
			} else {
				e.printStackTrace();
			}
		} finally {
			if( out!=null ) {
				try {
					out.close();
				} catch (Exception e2) {
				}
			}
			if( in!=null ) {
				try {
					in.close();
				} catch (Exception e2) {
				}
			}
			running = false;
			if( listener != null ) {
				listener.copyComplete();
			}
		}				
	}

}
