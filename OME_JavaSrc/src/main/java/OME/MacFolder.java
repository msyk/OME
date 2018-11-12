package OME;

import java.io.*;

/**
 * Mac OS Xのフォルダ
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii msyk@msyk.net
 * @version 10.1
 */

public class MacFolder {

    /**
	     
     @param pathname 
     @param localizable 
     @throws java.io.IOException 
     */
    public MacFolder(String pathname, boolean localizable) throws IOException {
        contractivePath = pathname;
        acceptLocalize = localizable;
        thisFolder = new File(pathname);
        if (localizable) {
            if (!thisFolder.exists()) {
                File locaizedFolder = new File(pathname + ".localized");
                if (locaizedFolder.exists()) thisFolder = locaizedFolder;
            }
        }
        if (thisFolder.exists() && thisFolder.isFile()) throw new IOException(thisFolder.getName() + " is a file.");
    }

    private File thisFolder = null;

    private String contractivePath = null;

    private boolean acceptLocalize = false;

    /**	
	     
     @param pathname 
     @param child 
     @param localizable 
     @throws java.io.IOException 
     */
    public MacFolder(String pathname, String child, boolean localizable) throws IOException {
        this(pathname + File.separator + child, localizable);
    }

    /**	  
     @param parent 
     @param child 
     @param localizable 
     @throws java.io.IOException 
     */
    public MacFolder(File parent, String child, boolean localizable) throws IOException {
        this(parent.getPath() + File.separator + child, localizable);
    }

    /** 
     @return 
     */
    public File getAsFile() {
        return thisFolder;
    }

    public String getName() {
        return thisFolder.getName();
    }

    public boolean exists() {
        return thisFolder.exists();
    }

}
