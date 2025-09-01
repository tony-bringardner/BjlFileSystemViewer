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

import java.awt.Graphics2D;
import java.awt.Image;
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

public class MacRegistry2 implements IRegistry {
	public final static String COMMAND = "/System/Library/Frameworks/CoreServices.framework/Frameworks/LaunchServices.framework/Versions/Current/Support/lsregister -dump";

	private static transient Map<String, List<Bundle>> map ;
	private static Map<String,String> mimeMap = new TreeMap<>();

	private class RegDataImpl extends IRegistry.RegData {


		private Bundle bundle;

		public RegDataImpl(Bundle bundle) {
			super(bundle.name,bundle.path);
			this.bundle = bundle;
		}

		@Override
		public List<BufferedImage> getIcons() throws IOException {

			return bundle.getIcons();
		}

		@Override
		public BufferedImage getIcon(int width, int height) throws IOException {
			return bundle.getIcon(width,height);
		}


	}

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
		List<CommandType> roles = new ArrayList<>();
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
						String [] tmp = l2.substring(7).trim().split("[,]");
						for (int i = 0; i < tmp.length; i++) {
							tmp[i] = tmp[i].trim();
							if(tmp[i].startsWith("Editor")) {
								roles.add(CommandType.Editor);
							} else if(tmp[i].startsWith("Viewer")) {
								roles.add(CommandType.Viewer);
							} 
						}

					} else if( l2.startsWith("bindings:") && l2.length()>10) {
						bindings = l2.substring(10).trim().split("[,]");
						for (int i = 0; i < bindings.length; i++) {
							bindings[i] = bindings[i].trim();
						}
					} else if( l2.startsWith("----")) {
						break;
					}
				}
			}
		}
	}

	/*
type id:                    public.shell-script (0x1363c)
bundle:                     CoreTypes (0x459c)
uti:                        public.shell-script
localizedDescription:       "ar" = "برنامج Shell النصي", "ca" = "script de shell", "cs" = "shell skript", "da" = "shell-instruks", "de" = "Shell-Skript", "el" = "σκριπτ κελύφους", "en" = "shell script", "en_AU" = "shell script", "en_GB" = "shell script", "es" = "script de shell", "es_419" = "script de shell", "es_US" = "script de shell", "fi" = "komentotulkkiskripti", "fr" = "script shell", "fr_CA" = "script de coque logicielle", "he" = "תסריט מעטפת", "hi" = "शेल स्क्रिप्ट", "hr" = "shell skripta", "hu" = "shell szkript", "id" = "skrip shell", "it" = "script shell", "ja" = "シェルスクリプト", "ko" = "셸 스크립트", "LSDefaultLocalizedValue" = "shell script", "ms" = "skrip cangkerang", "nl" = "shellscript", "no" = "shellscript", "pl" = "skrypt powłoki", "pt_BR" = "script de shell", "pt_PT" = "script da shell", "ro" = "script shell", "ru" = "основной скрипт", "sk" = "shell skript", "sl" = "skript lupine", "sv" = "kommandotolkskript", "th" = "เชลล์สคริปต์", "tr" = "kabuk betiği", "uk" = "скрипт командного рядка", "vi" = "tập lệnh shell", "zh_CN" = "shell脚本", "zh_HK" = "shell程式碼", "zh_TW" = "Shell工序指令"
flags:                      active  public  apple-internal  exported  core  trusted (0000000000000077)
conforms to:                public.script
tags:                       .sh

	 */

	private static class FileType {
		String id;
		String bundle ;
		String uti;
		String conformsTo;
		String [] tags;

		public FileType(String line, BufferedReader in) throws IOException {
			id = line.substring(line.indexOf(' ')).trim();

			while( (line=in.readLine()) != null) {

				String l2 = line.trim();

				if( l2.length()> 6) {
					int space = line.indexOf(' ');
					if( l2.startsWith("bundle:")) {
						bundle = l2.substring(space).trim();
					} else if( l2.startsWith("uti:")) {
						uti = l2.substring(space).trim();
					} else if( l2.startsWith("uti:")) {
						conformsTo = l2.substring(space).trim();					
					} else if( l2.startsWith("tags:") && l2.length()>7) {
						String [] tmp = l2.substring(space).trim().split("[,]");
						for (int i = 0; i < tmp.length; i++) {
							tmp[i] = tmp[i].trim();							 
						}
						tags = tmp;

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
		List<Claim> claims = new ArrayList<>();
		private transient String iconLocation;
		private List<BufferedImage> icons; 

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
					} else if( l2.startsWith("icons:")) {
						iconLocation = l2.substring(6).trim();
						if(!iconLocation.startsWith("/")) {
							iconLocation = path+"/"+iconLocation;							
						}
					} else if( l2.startsWith("----")) {
						break;
					}
				}
			}
		}

		public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
			Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
			BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2d = dimg.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();

			return dimg;
		}  

		public BufferedImage getIcon(int width, int height) throws IOException {

			List<BufferedImage> list = getIcons();
			if( list == null || list.size()==0) {
				return null;
			}
			BufferedImage close=null;
			for(BufferedImage image : list) {
				if( image.getWidth()==width && image.getHeight()==height) {
					return image;
				}

				if( close == null ) {
					close = image;
				} else {
					if(close.getHeight()-height<image.getHeight()-height) {
						close = image;
					}
				}
			}
			BufferedImage ret = resize(close, width, height);
			list.add(ret);
			return ret;
		}

		public List<BufferedImage> getIcons() throws IOException {
			if( icons == null ) {
				synchronized (this) {
					if( icons == null ) {

						if( iconLocation == null ) {
							icons = new ArrayList<>();
						} else {
							if( !iconLocation.startsWith("/")) {

							}
							icons = MacRegistry2.getIcons(iconLocation);
						}
					}
				}
			}
			return icons;
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
				} else if( bundle != null && bundle.name !=null ) {
					String l2 = line.trim();
					if( l2.startsWith("claim id:")) {
						claim = new Claim(l2,in);
						// add the bundle if needed
						if( bundle.executable != null && claim != null ) {
							if( claim.bindings != null && claim.bindings.length>0) {
								bundle.claims.add(claim);
								boolean isDefault = claim.rank == null ? false: claim.rank.equals("Default");

								for(String mime : claim.bindings) {
									mime= mime.trim();
									if( mime.startsWith(".")) {
										mime = mime.substring(1);
									}
									if( mime.startsWith("public.")) {
										mime = mime.substring(7);
									}
									//public.plain-text
									if( mime.equals("plain-text")) {
										mime = "text/plain";
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


		MacRegistry2 mr = new MacRegistry2();
		String file = "file.sh";

		List<RegData> list = mr.getRegisteredHandler(file,CommandType.Editor);

		if( list == null ) {
			System.out.println("No list for "+file);
		} else {
			System.out.println("list sz="+list.size()+" for "+file);
			for (RegData path : list) {
				System.out.println(path.name+" - "+path.path);
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


	public List<RegData> getRegisteredHandler(String path,CommandType type) {
		List<RegData> ret = new  ArrayList<>();
		if( map == null ) {
			synchronized (MacRegistry2.class) {
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
		if( idx >= 0 ) {

			List<Bundle> list =null;
			String ext = path.substring(idx+1);

			String mime = getMimForExt(ext);
			if( mime != null ) {
				list = map.get(mime);

				if( list != null) {
					addIfNeeded(ret,list,type);					
				}
			}

			list = getListByExtenstion(ext);
			if( list != null) {
				addIfNeeded(ret,list,type);				
			}

			if( ret.size()>1) {
				List<RegData> ret2 = new ArrayList<>();
				List<String> already = new ArrayList<>();

				for (RegData data : ret) {					
					if(!already.contains(data.name)) {
						already.add(data.name);
						ret2.add(data);
					}
				}
				ret = ret2;
			}



		}

		return ret;
	}

	private void addIfNeeded(List<RegData> ret, List<Bundle> list, CommandType type) {
		if( list.size()>0) {
			for (Bundle bundle : list) {
				for(Claim claim : bundle.claims) {
					if(type == CommandType.Any || claim.roles.contains(type)) {
						RegData data = new RegDataImpl(bundle);
						if( !ret.contains(data)) {
							ret.add(data);
						}		
					}
				}			      
			}
		}
	}

	/**
	 * https://en.wikipedia.org/wiki/Apple_Icon_Image_format
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<BufferedImage> getIcons(String path) throws IOException {
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
						ByteArrayInputStream ips = new ByteArrayInputStream(data);
						BufferedImage image = ImageIO.read(ips);
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
