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
 * ~version~V000.01.10-V000.00.01-V000.00.00-
 */
package us.bringardner.io.filesource.viewer.registry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.bringardner.io.filesource.viewer.IRegistry;

public class MacRegistry implements IRegistry {
	public final static String COMMAND = "/System/Library/Frameworks/CoreServices.framework/Frameworks/LaunchServices.framework/Versions/Current/Support/lsregister -dump";

	private static Map<String, List<Bundle>> map ;

	private static class Claim {
		@SuppressWarnings("unused")
		String id;
		@SuppressWarnings("unused")
		String name ;
		String rank;
		String [] roles;
		String [] bindings;

		public Claim(String line, BufferedReader in) throws IOException {
			id = line.substring(15).trim();
			while( (line=in.readLine()) != null) {

				String l2 = line.trim();
				if( l2.length()> 6) {
					if( l2.startsWith("name:")) {
						name = l2.substring(6).trim();
					} else if( l2.startsWith("rank:")) {
						rank = l2.substring(6).trim();
					} else if( l2.startsWith("roles:") && l2.length()>7) {
						roles = l2.substring(7).trim().split("[,]");
					} else if( l2.startsWith("bindings:") && l2.length()>10) {
						bindings = l2.substring(10).trim().split("[,]");
					} else if( l2.startsWith("----")) {
						break;
					}
				}
			}
		}
	}

	private static class Bundle {
		@SuppressWarnings("unused")
		String id;
		String path;
		String executable;
		String name;

		public Bundle(String line, BufferedReader in) throws IOException {
			id = line.substring(15).trim();
			while( (line = in.readLine()) != null) {
				String l2 = line.trim();
				if( l2.length() > 6) {
					if( l2.startsWith("path:")) {
						path = l2.substring(6).trim();
					} else	if( l2.startsWith("name:")) {
						name = l2.substring(6).trim();
					} else	if( l2.startsWith("executable:") && l2.length()> 11 ) {
						executable = l2.substring(11).trim();
					} else if( l2.startsWith("----")) {
						break;
					}
				}
			}
		}

	}

	public synchronized static void   init() throws IOException {
		map = new HashMap<String,List<Bundle>>();
		Process proc = Runtime.getRuntime().exec(COMMAND);
		BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		Bundle bundle = null;
		Claim claim = null;
		/*
bundle  id:            25552
        path:          /Applications/QuickTime Player.app
        name:          QuickTime Player
        executable:    Contents/MacOS/QuickTime Player

 		claim   id:            16696
                bindableKey:   56975
                generation:    8696
                name:          QuickTime Player Composition
                rank:          Default
                roles:         Editor
                flags:         apple-internal  relative-icon-path
                icon:          Contents/Resources/Composition.icns
                bindings:      com.apple.quicktimeplayerx-composition

		 */
		try {
			String line = in.readLine();
			while( line != null ) {
				if( line.startsWith("----")) {
					bundle = null;
				} else if( line.startsWith("bundle	id:")) {
					bundle = new Bundle(line, in);
				} else if( bundle != null ) {
					String l2 = line.trim();
					if( l2.startsWith("claim\tid:")) {
						claim = new Claim(l2,in);
						if( !"TextEdit".equals(bundle.name) ) {
							// add the bundle if needed
							if( bundle.executable != null && claim != null ) {
								if( claim.roles != null) {
									boolean isEdit = false;
									for(String role : claim.roles){
										if( role.equals("Editor")) {
											isEdit = true;
											break;
										}
									}
									isEdit = true;

									if( isEdit && claim.bindings != null && claim.bindings.length>0) {
										boolean isDefault = claim.rank == null ? false: claim.rank.equals("Default");

										for(String mime : claim.bindings) {
											mime = mime.trim();
											List<Bundle> list = map.get(mime);
											if( list == null ) {
												list = new ArrayList<Bundle>();
												map.put(mime, list);
											}
											if( isDefault ) {
												// default goes at the start of the list
												list.add(0, bundle);
											} else {
												// other goes at the end
												list.add( bundle);
											}
										}
									}

								}
							}
							claim = null;
						} 
					}
				}

				line = in.readLine();
			}
		} catch(java.io.EOFException e) {
			// ignore
		} finally {
			try {
				in.close();
			} catch(Throwable e) {}
		}

	}

	public static void main(String [] args) throws IOException {



		MacRegistry mr = new MacRegistry();
		File file = new File("Users/tony/Pictures/plus.txt");

		List<String> list = mr.getRegisteredEditor(file);
		if( list == null ) {
			System.out.println("No list for "+file);
		} else {
			System.out.println("list sz="+list.size()+" for "+file);
			for (String path : list) {
				System.out.println(path);
			}
		}


	}

	@Override
	public List<String> getRegisteredEditor(File file) {
		List<String> ret = new  ArrayList<>();
		ret.add("open -Wn");
		
		return ret;
	}

	private String getMimForExt(String ext) {
		String ret = null;
		if( ext.equals("jpg")) {
			ret = "jpeg";
		} else if( ext.equals("pdf")) {
			ret = "com.adobe.pdf";
		} else if (ext.equals("txt")) {
			ret = "plain-text";
		}
		return ret;
	}

	private List<Bundle> getListByExtenstion(String ext) {
		List<Bundle> list = map.get(ext);
		if( list == null ) {
			ext = "."+ext;
			list = map.get(ext);
			if( list == null ) {
				ext = "public"+ext;
				list = map.get(ext);
			}
		} 
		return list;
	}

	
	public List<String> getRegisteredEditorx(File file) {
		List<String> ret = new  ArrayList<>();
		if( map == null ) {
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
				map = null;
			}
		}
	
		if( map != null ) {
			String name = file.getName();
			int idx= name.lastIndexOf('.');
			if( idx > 0 ) {
				String ext = name.substring(idx+1);
	
				List<Bundle> list = getListByExtenstion(ext);
				if( list == null ) {
					String mime = getMimForExt(ext);
					if( mime != null ) {
						list = getListByExtenstion(mime);
					}
				}
	
				if( list != null) {
					for (Bundle bundle : list) {
						if( bundle.executable.startsWith("/")) {
							ret.add(bundle.executable);
						} else {
							ret.add(bundle.path+"/"+bundle.executable);
						}
					}
				}
			}
		}
		return ret;
	}

}
