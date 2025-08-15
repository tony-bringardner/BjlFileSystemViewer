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
 * ~version~V000.01.17-V000.01.08-V000.00.01-V000.00.00-
 */
package us.bringardner.viewer;

import org.junit.Test;

import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.JFCTestHelper;
import junit.extensions.jfcunit.RobotTestHelper;
import junit.extensions.jfcunit.TestHelper;
import us.bringardner.io.filesource.FileSourceFactory;
import us.bringardner.io.filesource.viewer.FileSourceViewer;

public class TestViewer extends JFCTestCase {

	FileSourceViewer viewer;
	
	protected void setUp( ) throws Exception {
		super.setUp( );
		// Choose the text Helper
		setHelper( new JFCTestHelper( ) ); // Uses the AWT Event Queue.
		setHelper( new RobotTestHelper( ) ); // Uses the OS Event Queue.
		viewer = new FileSourceViewer();
		 viewer.setup(FileSourceFactory.fileProxyFactory,true);
		viewer.show();
	}

	protected void tearDown( ) throws Exception {
		viewer = null;
		TestHelper.cleanUp( this );	
		super.tearDown( );
	}

	@Test
	public void test() {
		//fail("Not yet implemented");
		System.out.println("ok");

	}

}
