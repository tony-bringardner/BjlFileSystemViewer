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
 * ~version~V000.00.01-V000.00.00-
 */
package us.bringardner.viewer;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public interface IFile {

	public abstract String getName();

	public abstract String getParent();

	public abstract IFile getParentFile();

	public abstract String getPath();

	public abstract boolean isAbsolute();

	public abstract String getAbsolutePath();

	public abstract IFile getAbsoluteFile();

	public abstract String getCanonicalPath() throws IOException;

	public abstract IFile getCanonicalFile() throws IOException;

	public abstract URI toURI();

	public abstract boolean canRead();

	public abstract boolean canWrite();

	public abstract boolean exists();

	public abstract boolean isDirectory();

	public abstract boolean isFile();

	public abstract boolean isHidden();

	public abstract long lastModified();

	public abstract long length();

	public abstract boolean createNewFile() throws IOException;

	public abstract boolean delete();

	public abstract void deleteOnExit();

	public abstract IFile[] listFiles();

	public abstract IFile[] listFiles(FilenameFilter filter);

	public abstract IFile[] listFiles(FileFilter filter);

	public abstract boolean mkdir();

	public abstract boolean mkdirs();

	public abstract boolean renameTo(File dest);

	public abstract boolean setLastModified(long time);

	public abstract boolean setReadOnly();

	public abstract boolean setWritable(boolean writable, boolean ownerOnly);

	public abstract boolean setWritable(boolean writable);

	public abstract boolean setReadable(boolean readable, boolean ownerOnly);

	public abstract boolean setReadable(boolean readable);

	public abstract boolean setExecutable(boolean executable, boolean ownerOnly);

	public abstract boolean setExecutable(boolean executable);

	public abstract boolean canExecute();

	public abstract int compareTo(File pathname);

	public abstract boolean equals(Object obj);

	public abstract Path toPath();

}