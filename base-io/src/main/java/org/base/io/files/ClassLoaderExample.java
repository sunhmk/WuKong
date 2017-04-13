package org.base.io.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class ClassLoaderExample {
	public static void mian(String[] args) {
		String workDir = "/work/code/pic";
		List<URL> classPath = new ArrayList<>();
		try {
			classPath.add(new File(workDir + "/").toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//classPath.add(file.toURI().toURL());
		try {
			classPath.add(new File(workDir, "classes/").toURI().toURL());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File[] libs = new File(workDir, "lib").listFiles();
		if (libs != null) {
			for (File lib : libs) {
				try {
					classPath.add(lib.toURI().toURL());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// create a normal parent-delegating classloader
		URLClassLoader loader = new URLClassLoader(
				classPath.toArray(new URL[classPath.size()]));
	}
	
	/*
	  int firstArg = 0;
    String fileName = args[firstArg++];
    File file = new File(fileName);
    if (!file.exists() || !file.isFile()) {
      System.err.println("JAR does not exist or is not a normal file: " +
          file.getCanonicalPath());
      System.exit(-1);
    }
    String mainClassName = null;

    JarFile jarFile;
    try {
      jarFile = new JarFile(fileName);
    } catch (IOException io) {
      throw new IOException("Error opening job jar: " + fileName)
        .initCause(io);
    }

    Manifest manifest = jarFile.getManifest();
    if (manifest != null) {
      mainClassName = manifest.getMainAttributes().getValue("Main-Class");
    }
    jarFile.close();

    if (mainClassName == null) {
      if (args.length < 2) {
        System.err.println(usage);
        System.exit(-1);
      }
      mainClassName = args[firstArg++];
    }
    mainClassName = mainClassName.replaceAll("/", ".");

    File tmpDir = new File(System.getProperty("java.io.tmpdir"));
    ensureDirectory(tmpDir);

    final File workDir;
    try {
      workDir = File.createTempFile("hadoop-unjar", "", tmpDir);
    } catch (IOException ioe) {
      // If user has insufficient perms to write to tmpDir, default
      // "Permission denied" message doesn't specify a filename.
      System.err.println("Error creating temp dir in java.io.tmpdir "
                         + tmpDir + " due to " + ioe.getMessage());
      System.exit(-1);
      return;
    }

    if (!workDir.delete()) {
      System.err.println("Delete failed for " + workDir);
      System.exit(-1);
    }
    ensureDirectory(workDir);

    ShutdownHookManager.get().addShutdownHook(
        new Runnable() {
          @Override
          public void run() {
            FileUtil.fullyDelete(workDir);
          }
        }, SHUTDOWN_HOOK_PRIORITY);


    unJar(file, workDir);

    ClassLoader loader = createClassLoader(file, workDir);

    Thread.currentThread().setContextClassLoader(loader);
    Class<?> mainClass = Class.forName(mainClassName, true, loader);
    Method main = mainClass.getMethod("main", String[].class);
    List<String> newArgsSubList = Arrays.asList(args)
        .subList(firstArg, args.length);
    String[] newArgs = newArgsSubList
        .toArray(new String[newArgsSubList.size()]);
    try {
      main.invoke(null, new Object[] {newArgs});
    } catch (InvocationTargetException e) {
      throw e.getTargetException();
    }
	 */
	
	
	  /**
	   * Unpack matching files from a jar. Entries inside the jar that do
	   * not match the given pattern will be skipped.
	   *
	   * @param jarFile the .jar file to unpack
	   * @param toDir the destination directory into which to unpack the jar
	   * @param unpackRegex the pattern to match jar entries against
	   *
	   * @throws IOException if an I/O error has occurred or toDir
	   * cannot be created and does not already exist
	   */
	  public static void unJar(File jarFile, File toDir, Pattern unpackRegex)
	      throws IOException {
	    try (JarFile jar = new JarFile(jarFile)) {
	      int numOfFailedLastModifiedSet = 0;
	      Enumeration<JarEntry> entries = jar.entries();
	      while (entries.hasMoreElements()) {
	        final JarEntry entry = entries.nextElement();
	        if (!entry.isDirectory() &&
	            unpackRegex.matcher(entry.getName()).matches()) {
	          try (InputStream in = jar.getInputStream(entry)) {
	            File file = new File(toDir, entry.getName());
	            //ensureDirectory(file.getParentFile());
	            try (OutputStream out = new FileOutputStream(file)) {
	              //IOUtils.copyBytes(in, out, BUFFER_SIZE);
	            }
	            if (!file.setLastModified(entry.getTime())) {
	              numOfFailedLastModifiedSet++;
	            }
	          }
	        }
	      }
	      if (numOfFailedLastModifiedSet > 0) {
	        //LOG.warn("Could not set last modfied time for {} file(s)",
	        //    numOfFailedLastModifiedSet);
	      }
	    }
	  }
}
