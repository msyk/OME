package OME;

import java.io.File;
import java.util.StringTokenizer;

//import com.apple.cocoa.foundation.*;
//import com.apple.mrj.*;
//import com.apple.eio.FileManager;

//
//  MacFile.java
//  OME_JavaProject
//
//  Created by êVãè âÎçs on Thu Mar 13 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

/**
 * Mac OS Xのファイル
 *
 * <hr>
 * <h2>OME更新履歴</h2>
 * <pre>
 *    :
 * 2003/11/14:新居
 * 2004/3/22:新居
 * 2009/6/28:新居:OME_JavaCore2へ移動、ファイルタイプとano 
 * </pre>
 *
 * @author Masayuki Nii/msyk@msyk.net
 */

public class MacFile extends File {

    /**	
	     @param pathname 
     */
    public MacFile(String pathname) {
        super(pathname);
    }

    /**
		@param parent 
		@param child 
     */
    public MacFile(String parent, String child) {
        super(parent, child);
    }

    /**	
     
     @param parent 
     @param child 
	*/
    public MacFile(File parent, String child) {
        super(parent, child);
    }

    /**    
     @param thisfile 
     */
    public MacFile(File thisfile) {
        super(thisfile.getPath());
    }

    /**	     
     @param fileType 
     @param fileCreator
     */
//    public void setFileTypeAndCreator(String fileType, String fileCreator) {
//
//        String jVer = System.getProperty("java.version");
//        StringTokenizer jVarTokens = new StringTokenizer(jVer, ".");
//        int majorNum = Integer.parseInt(jVarTokens.nextToken());
//        int minorNum = Integer.parseInt(jVarTokens.nextToken());
//        double versionNum = majorNum + (minorNum / 10.0);
//
/*        if (versionNum < 1.4) {
            try { 
                if (fileType != null) MRJFileUtils.setFileType(this, new MRJOSType(fileType));
                if (fileCreator != null) MRJFileUtils.setFileCreator(this, new MRJOSType(fileCreator));
            } catch (Exception e) {
                Logging.writeMessage("%% OME Error 16 %% Error in setting file type and creator: " + e.getMessage()
                        + this.getPath());
            }
       } else {
 */
//		String filePath = this.getAbsolutePath();
//		try { 
//			if ( fileCreator != null )
//				FileManager.setFileCreator( filePath, FileManager.OSTypeToInt( fileCreator ) );
//			if ( fileType != null )
//				FileManager.setFileType( filePath, FileManager.OSTypeToInt( fileType ) );
//		} catch (java.io.IOException e) {
//            Logging.writeMessage("%% OME Error 16 %% Error in setting file type and creator: " + e.getMessage() + this.getPath());
//		}
/*            synchronized (this.lockObject) {
                int myPool = NSAutoreleasePool.push();
                NSMutableDictionary fattr = new NSMutableDictionary(NSPathUtilities.fileAttributes(this.getPath(),
                        false));
                if (fileType != null) {
                    int fType = NSHFSFileTypes.hfsTypeCodeFromFileType("'" + fileType + "'");
                    fattr.setObjectForKey(new Integer(fType), "NSFileHFSTypeCode");
                }
                if (fileCreator != null) {
                    int fCreator = NSHFSFileTypes.hfsTypeCodeFromFileType("'" + fileCreator + "'");
                    fattr.setObjectForKey(new Integer(fCreator), "NSFileHFSCreatorCode");
                }
                NSPathUtilities.setFileAttributes(this.getPath(), fattr);
                NSAutoreleasePool.pop(myPool);
            }
       }
 */    
//    }
	
	private static Object lockObject = new Object();

/**
 * @param files
 */
	public static MacFile getInstance( File files[] )	{
        for ( int ix = 0 ; ix < files.length ; ix++ )	{
			if ( files[ix].exists() )	{
				return new MacFile( files[ix] );
			}
		}
		return null;
    }
}
