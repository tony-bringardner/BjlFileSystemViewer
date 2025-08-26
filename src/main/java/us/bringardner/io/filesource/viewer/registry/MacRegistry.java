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
 * @author Tony Bringardner   
 *
 *
 * ~version~V000.01.10-V000.00.01-V000.00.00-
 */
package us.bringardner.io.filesource.viewer.registry;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import us.bringardner.io.filesource.viewer.IRegistry;

public class MacRegistry implements IRegistry {
	public final static String COMMAND = "/System/Library/Frameworks/CoreServices.framework/Frameworks/LaunchServices.framework/Versions/Current/Support/lsregister -dump";

	private static transient Map<String, List<Bundle>> map ;
	private static Map<String,String> mimeMap = new TreeMap<>();

	static {
		mimeMap.put("bmp","image/bmp");
		mimeMap.put("csh","application/x-csh");
		mimeMap.put("css","text/css");
		mimeMap.put("csv","text/csv");
		mimeMap.put("doc","application/msword");
		mimeMap.put("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		mimeMap.put("gif","image/gif");
		mimeMap.put("htm","text/html");
		mimeMap.put("html","text/html");
		mimeMap.put("ico","image/vnd.microsoft.icon");
		mimeMap.put("ics","text/calendar");
		mimeMap.put("jpeg","image/jpeg");
		mimeMap.put("jpg","image/jpeg");
		mimeMap.put("js","JavaScripttext/javascript");
		mimeMap.put("json","application/json");
		mimeMap.put("jsonld","application/ld+json");
		mimeMap.put("mjs","text/javascript");
		mimeMap.put("mp3","audio/mpeg");
		mimeMap.put("mpeg","video/mpeg");
		mimeMap.put("odp","application/vnd.oasis.opendocument.presentation");
		mimeMap.put("ods","application/vnd.oasis.opendocument.spreadsheet");
		mimeMap.put("odt","application/vnd.oasis.opendocument.text");
		mimeMap.put("png","image/png");
		mimeMap.put("pdf","application/pdf");
		mimeMap.put("php","application/x-httpd-php");
		mimeMap.put("ppt","application/vnd.ms-powerpoint");
		mimeMap.put("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
		mimeMap.put("rtf","application/rtf");
		mimeMap.put("sh","application/x-sh");
		mimeMap.put("svg","image/svg+xml");
		mimeMap.put("tif","image/tiff");
		mimeMap.put("tiff","image/tiff");
		mimeMap.put("txt","text/plain");
		mimeMap.put("text","text/plain");
		mimeMap.put("vsd","application/vnd.visio");
		mimeMap.put("xhtml","application/xhtml+xml");
		mimeMap.put("xls","application/vnd.ms-excel");
		mimeMap.put("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		mimeMap.put("xml","text/xml");

	}
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
						//(0xe7e4)
						int idx = path.lastIndexOf('(');
						if( idx > 0 ) {
							path = path.substring(0,idx).trim();
						}
					} else if( l2.startsWith("name:")) {
						name = l2.substring(6).trim();
					} else if( l2.startsWith("executable:") && l2.length()> 11 ) {
						executable = l2.substring(11).trim();
					} else if( l2.startsWith("----")) {
						break;
					}
				}
			}
		}

	}

	public synchronized static void   init() throws IOException {
		map = new TreeMap<String,List<Bundle>>();
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
			while(!line.startsWith("----")) {
				line = in.readLine();
			}

			while( line != null ) {
				if( line.startsWith("----")) {
					bundle = null;
				} else if( line.startsWith("bundle id:")) {
					bundle = new Bundle(line, in);
				} else if( bundle != null ) {
					String l2 = line.trim();
					if( l2.startsWith("claim id:")) {
						claim = new Claim(l2,in);

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

										if( mime.startsWith("public.")) {
											mime = mime.substring(7);
										}
										//public.plain-text
										if( mime.equals("plain-text")) {
											mime = "text/plain";
										} else if( mime.startsWith("plain.")) {
											System.out.println(mime);
										}
										if( mime.equals("txt") || mime.equals("text")) {
											mime = "text/plain";
										}
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

		List<RegData> list = mr.getRegisteredHandler(file.getAbsolutePath());

		if( list == null ) {
			System.out.println("No list for "+file);
		} else {
			System.out.println("list sz="+list.size()+" for "+file);
			for (RegData path : list) {
				System.out.println(path.path);
			}
		}



	}




	private String getMimForExt(String ext) {
		if( ext.startsWith(".")) {
			ext = ext.substring(1);
		}
		String ret = mimeMap.get(ext);

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


	public List<RegData> getRegisteredHandler(String path) {
		List<RegData> ret = new  ArrayList<>();
		if( map == null ) {
			synchronized (MacRegistry.class) {
				if( map == null ) {
					try {
						init();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		int idx= path.lastIndexOf('.');
		if( idx > 0 ) {

			List<Bundle> list =null;
			String ext = path.substring(idx+1);

			String mime = getMimForExt(ext);
			if( mime != null ) {
				list = map.get(mime);
				if( list != null) {
					for (Bundle bundle : list) {
						RegData data = new RegData(bundle.name, bundle.path);
						if( !ret.contains(data)) {
							ret.add(data);
						}      
					}
				}
			}

			list = getListByExtenstion(ext);
			if( list != null) {

				for (Bundle bundle : list) {
					RegData data = new RegData(bundle.name, bundle.path);
					if( !ret.contains(data)) {
						ret.add(data);
					}
				}
			}




		}

		return ret;
	}

	/**
	 * https://en.wikipedia.org/wiki/Apple_Icon_Image_format
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public List<BufferedImage> getIcon(String path) throws IOException {
		List<BufferedImage> ret = new ArrayList<>();
		File file = new File(path);
		try(DataInputStream in = new DataInputStream( new FileInputStream(path))) {
			if(!(in.read()=='i' && in.read()=='c' && in.read()=='n' && in.read()=='s')) {
				throw new IOException("Invalid icns file");
			}
			int len = in.readInt();
			if( len != file.length()) {
				throw new IOException("Invalid icns file");
			}
			while(in.available()>0) {
				in.readInt();
				len = in.readInt();
				if( len > 0 ) {
					byte [] data = new byte[len-8];
					in.readFully(data);
					{
						BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
						if( image != null ) {
							ret.add(image);
						}
					}
				}
			}
		}

		return ret;
	}

}
