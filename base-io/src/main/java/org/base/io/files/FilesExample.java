package org.base.io.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FilesExample {
	public static void main(String[] args) {
		//source for spark diskstore.scala blockmanager.scala
		File file = new File("/work/code/pic/3.jpg");
		try {
			FileChannel channel = new RandomAccessFile(file, "r").getChannel();
			if (file.length() < 1024 * 1024) {
				ByteBuffer buf = ByteBuffer.allocate((int) file.length());
				try {
					channel.position(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					while (buf.remaining() != 0) {
						if (channel.read(buf) == -1) {
							throw new IOException(
									"Reached EOF before filling buffer");
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				buf.flip();
			    //new ChunkedByteBuffer(buf);
			}
			else
			{
				try {
					channel.map(MapMode.READ_ONLY, 0, file.length());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 //new ChunkedByteBuffer(channel.map(MapMode.READ_ONLY, 0, file.length))
			}
			try {
				channel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * def getBytes(blockId: BlockId): ChunkedByteBuffer = { val file =
			 * diskManager.getFile(blockId.name) val channel = new
			 * RandomAccessFile(file, "r").getChannel Utils.tryWithSafeFinally {
			 * // For small files, directly read rather than memory map if
			 * (file.length < minMemoryMapBytes) { val buf =
			 * ByteBuffer.allocate(file.length.toInt) channel.position(0) while
			 * (buf.remaining() != 0) { if (channel.read(buf) == -1) { throw new
			 * IOException("Reached EOF before filling buffer\n" + s
			 * "offset=0\nfile=${file.getAbsolutePath}\nbuf.remaining=${buf.remaining}"
			 * ) } } buf.flip() new ChunkedByteBuffer(buf) } else { new
			 * ChunkedByteBuffer(channel.map(MapMode.READ_ONLY, 0, file.length))
			 * } } { channel.close() } }
			 */
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		FileChannel channel = null;
		//write
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			channel = fileOutputStream.getChannel();
			//channel.write(bytes);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				channel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    
 //file.delete();
        // java.nio.file.Files
		//com.google.common.io.Files;
		
		
	}
	
	
	  /**IOUtils.java--hadoop
	   * Return the complete list of files in a directory as strings.<p/>
	   *
	   * This is better than File#listDir because it does not ignore IOExceptions.
	   *
	   * @param dir              The directory to list.
	   * @param filter           If non-null, the filter to use when listing
	   *                         this directory.
	   * @return                 The list of files in the directory.
	   *
	   * @throws IOException     On I/O error
	   */
	  public static List<String> listDirectory(File dir, FilenameFilter filter)
	      throws IOException {
	    ArrayList<String> list = new ArrayList<String> ();
	    try (DirectoryStream<Path> stream =
	             Files.newDirectoryStream(dir.toPath())) {
	      for (Path entry: stream) {
	        String fileName = entry.getFileName().toString();
	        if ((filter == null) || filter.accept(dir, fileName)) {
	          list.add(fileName);
	        }
	      }
	    } catch (DirectoryIteratorException e) {
	      throw e.getCause();
	    }
	    return list;
	  }
}
