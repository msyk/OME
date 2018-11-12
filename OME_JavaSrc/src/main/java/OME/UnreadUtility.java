package OME;

//
//  UnreadUtility.java
//  OME_JavaProject
//
//  Created by 新居 雅行 on Sat Jan 18 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

import java.io.*;

/**
 * 未読のメールファイルを管理するクラス
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */
public class UnreadUtility {

    public static void main(String args[]) {
        new UnreadUtility().init().removeHasRedAliases();
        System.exit(0);
    }

    /** 新たにMovingInfoクラスのインスタンスを得る
     @return 生成されたインスタンスへの参照 */
    public static UnreadUtility getInstance() {
        if (!isInstanciate) {
            mySelf = new UnreadUtility();
            mySelf.init();
            isInstanciate = true;
        }
        return mySelf;
    }

    private static boolean isInstanciate = false;

    private static UnreadUtility mySelf = null;

    private File unreadFolder;

    private UnreadUtility init() {
		try {
			unreadFolder = new File(OMEPreferences.getInstance().getOMETemp(), "unreadAliases");
			if (!unreadFolder.exists()) {
				unreadFolder.mkdir();
			}
			return this;
		}
		catch (Exception e) {
			
			return null;
		}
    }

    /** メールファイルのエイリアスを未読エイリアスフォルダに作成する
     @param msgItem	元となるメールファイル	*/
    public void createUnreadAlias(File msgItem) {
    	Logging.writeMessage("#### Trying to careate a bookmark file: "+msgItem.toString());
        MacAlias.createMacAliasInFolderByUniqueFileName(unreadFolder, msgItem);
    }

    /** 未読エイリアスフォルダにあるエイリアスファイルの個数を求める
     */
    public int countUnreadAlias() {
        return unreadFolder.list(new FilteringFiles()).length;
    }

    /** 未読エイリアスフォルダにあるエイリアスのうち、元ファイルが既読になったものを削除する
     */
    public void removeHasRedAliases() {
        File aliasList[] = unreadFolder.listFiles(new FilteringFiles());
        if (aliasList == null) return;
        File resolvedList[] = MacAlias.resolveMacAliases(aliasList);
        for (int i = 0; i < aliasList.length; i++) {
            if (resolvedList[i].getName().endsWith(".mpart")) {
                String folderName = resolvedList[i].getName();
                File msgFile1 = new File(resolvedList[i], folderName.substring(0, folderName.length() - 5) + ".ygm");
                File msgFile2 = new File(resolvedList[i], folderName.substring(0, folderName.length() - 5) + ".html");
                if (!msgFile1.exists() && !msgFile2.exists()) aliasList[i].delete();
            } else if ((!resolvedList[i].getName().endsWith(".ygm")) && (!resolvedList[i].getName().endsWith(".html"))) {
                aliasList[i].delete();
            }
        }
    }

    private class FilteringFiles implements FilenameFilter {

        public boolean accept(File dir, String name) {
            File targetFile = new File(dir, name);
            if (targetFile.isDirectory()) return false;
            if (name.startsWith(".")) return false;
            return true;
        }
    }

}
