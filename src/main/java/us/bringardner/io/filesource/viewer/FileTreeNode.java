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
 * ~version~V000.01.28-V000.01.04-V000.00.01-V000.00.00-
 */
package us.bringardner.io.filesource.viewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import us.bringardner.io.filesource.FileSource;

public class FileTreeNode implements TreeNode {
	FileTreeNode parent;
	List<FileTreeNode> kids;
	FileSource dir;
	String shortName;
	String displayName;
	boolean showHidden = true;
	boolean showExtention = false;

	public FileTreeNode(FileSource file,boolean showHidden,boolean showExtention) {
		this.dir = file;
		this.showHidden = showHidden;
		this.showExtention = showExtention;;
		shortName = displayName = file.getName();
		int idx = shortName.lastIndexOf(".");
		if( idx > 0 ) {
			shortName = shortName.substring(0,idx);
		}
		try {
			FileSource link = file.getLinkedTo();
			if( link != null ) {
				displayName += " -> "+link.getCanonicalPath();
				shortName += " -> "+link.getCanonicalPath();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FileTreeNode(FileSource file,FileTreeNode parent,boolean showHidden,boolean showExtention) {
		this(file,showHidden,showExtention);
		this.parent = parent;
		this.showHidden = parent.showHidden;
		this.showExtention = parent.showExtention;
	}

	public FileSource getDir() {
		return dir;
	}


	public boolean isShowExtention() {
		return showExtention;
	}

	public void setShowExtention(boolean showExtention) {
		this.showExtention = showExtention;
		if( kids != null ) {
			for(FileTreeNode node : kids) {
				node.setShowExtention(showExtention);
			}
		}
	}

	public boolean isShowHidden() {
		return showHidden;
	}

	public void setShowHidden(boolean showHidden) {
		this.showHidden = showHidden;
		if( kids != null ) {
			for(FileTreeNode node : kids) {
				node.setShowHidden(showHidden);
			}
		}
		
	}

	public boolean kidsLoaded() {
		return kids != null;
	}

	/*
	private void setKidsHidden() {
		kidsHidden = new ArrayList<FileTreeNode>();
		for(FileTreeNode node : kids) {
			FileSource f = node.dir;
			boolean hide = f.isHidden() || f.getName().startsWith(".");
			if( showHidden || !hide) {
				kidsHidden.add(node);
			}
		}
	}
	 */

	List<FileTreeNode> getKids() {
		if( kids == null ) {
			if(!isTraversable()) {
				kids = new ArrayList<FileTreeNode>();
			} else {
				synchronized (this) {
					if( kids == null ) {

						ArrayList<FileTreeNode> tmp = new ArrayList<FileTreeNode>();
						try {

							FileSource[] list = dir.listFiles();
							if( list != null ) {
								for(FileSource f :  list ) {
									if( f.isDirectory() ) {

										boolean hide = f.isHidden() || f.getName().startsWith(".");
										if( showHidden || !hide) {
											FileTreeNode node = new FileTreeNode(f, this,showHidden,showExtention);
											tmp.add(node);
										}
									}
								}
								Collections.sort(tmp, new Comparator<FileTreeNode>() {

									public int compare(FileTreeNode arg0, FileTreeNode arg1) {
										return arg0.getDir().getName().compareToIgnoreCase(arg1.getDir().getName());
									}
								});
							}
							kids = tmp;
							//setKidsHidden();
							//System.out.println("end get kids time="+(System.currentTimeMillis()-start));
						} catch(IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
		return kids;
	}


	private boolean isTraversable() {
		boolean ret = false;
		if( dir != null ) {
			try {
				if((ret=dir.canRead()) ) {
					FileSource link = dir.getLinkedTo();
					if( link != null) {
						ret = !link.isChildOfMine(dir);				
					}
				}
			} catch (IOException e) {
			}
		}
		return ret;
	}

	public Enumeration<TreeNode> children() {

		return new Enumeration<TreeNode>() {
			{
				getKids();
			}
			int pos = 0;
			List<FileTreeNode> k = kids;
			public boolean hasMoreElements() {
				return pos < k.size();
			}


			public TreeNode nextElement() {
				return k.get(pos++);
			}

		};
	}




	public boolean equals(Object arg0) {
		boolean ret = false;
		if (arg0 instanceof FileTreeNode) {
			String one = ((FileTreeNode) arg0).dir.getAbsolutePath();
			String two = dir.getAbsolutePath();

			ret = one.equals(two);
			// System.out.println("eq("+ret+") dir="+one+","+two); 
		} else {
			System.out.println("eq not type="+arg0.getClass().getName());
		}
		return ret;
	}


	public boolean getAllowsChildren() {
		return true;
	}


	public TreeNode getChildAt(int arg0) {
		return getKids().get(arg0);
	}


	public int getChildCount() {
		return getKids().size();
	}


	public int getIndex(TreeNode arg0) {
		return getKids().indexOf(arg0);
	}

	public void reset() throws IOException {
		kids = null;
		dir.refresh();
	}


	public TreeNode getParent() {
		return parent;
	}

	public TreePath getPath() {
		List<Object> nodes = new ArrayList<Object>();
		nodes.add(this);
		FileTreeNode treeNode = (FileTreeNode) getParent();
		while (treeNode != null) {
			nodes.add(0, treeNode);
			treeNode = (FileTreeNode) treeNode.getParent();
		}

		return  new TreePath(nodes.toArray());
	}




	public boolean isLeaf() {
		boolean ret = false;
		List<FileTreeNode> k = getKids();
		if( k != null ) {
			ret = k.size() == 0  ;
		}

		return ret;
	}

	public String toString() {
		return showExtention? displayName:shortName;
	}

	/**
	 * This will remove a child from the node but does not impact
	 * the underlying file system.  Used for manipulating the UI only.
	 * 
	 * @param idx
	 */
	public void remove(int idx) {
		throw new RuntimeException("FileTreeNode.remove Not implemented");
		/*
		if( kids != null &&  kids.size() > idx) {
			kids.remove(idx);

		}
		 */
	}

	public void add(FileTreeNode newKid) {
		//  used for lazy loading	
		throw new RuntimeException("FileTreeNode.add Not implemented");
		/*
		if( kids == null ) {
			kids = new ArrayList<FileTreeNode>();
		}
		kids.add(newKid);
		 */
	}

	public void insert(FileTreeNode newChild, int index) {
		throw new RuntimeException("FileTreeNode.insert Not implemented");
		/*
		if(kids == null ) {
			kids = new ArrayList<FileTreeNode>();
		}
		kids.add(index, newChild);
		 */
	}

	public void setKids(List<FileTreeNode> children) {
		kids = children;

	}

	/**
	 * JTree evidently uses a hash map to track expended
	 * nodes.  The only way to expand a node it to tie the hashcode to the string value;  
	 */

	public int hashCode() {
		if( dir == null ) {
			throw new RuntimeException("dir is null in hasCode");
		}
		return dir.hashCode();
	}


}
