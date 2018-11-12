package OME;

import java.io.*;
import java.util.*;

/**
 * Mac OS X環境でのエイリアスファイルを管理するクラス。コマンドのmakenewaliasとresolvaliasが利用できる環境が必要なので、とりあえずはOME限定
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * 2014/7/28:新居:bmutil コマンドが/usr/local/binに必要
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 */

public class MacAlias {

	private static File commandFolder = OMEPreferences.getInstance().getOMEToolsFolder();

	/**	エイリアスファイルを作成する

     @param place エイリアスファイルを作成するディレクトリ
     @param target エイリアスファイルのオリジナル
	 */
	public static void createMacAlias(File place, File target) {

		// String cmd[] = { commandFolder.getPath() + "/makenewalias", place.getPath(), target.getPath()};
		String cmd[] = { "/usr/local/bin/bmutil", "-c", place.getPath(), target.getPath()};
		(new CommandExecuter(cmd)).doCommand();

	}

	/**	エイリアスファイルを作成する

     @param place エイリアスファイルを作成するディレクトリ。エイリアスファイルのファイル名はオリジナルと同じ
     @param target エイリアスファイルのオリジナル
	 */
	public static void createMacAliasInFolder(File place, File target) {

		//        String cmd[] = { commandFolder.getPath() + "/makenewalias", place.getPath() + "/" + target.getName(),
		//                target.getPath()};
		String cmd[] = { "/usr/local/bin/bmutil", "-c", place.getPath() + "/" + target.getName(),
				target.getPath()};
		CommandExecuter cm = new CommandExecuter(cmd);
		String outputString = cm.doCommand();
		if (cm.returnValue != 0)	{
			Logging.writeMessage("Output of makealials: " + outputString);
		}
	}

	/**	エイリアスファイルを作成する

     @param place エイリアスファイルを作成するディレクトリ。エイリアスファイルのファイル名はアルファベットと数字からなるランダムな8文字で、そのフォルダに存在しないファイル名を自動的に生成して割り当てる
     @param target エイリアスファイルのオリジナル
     @return 作成されたエイリアスファイル（エラーチェックがちょっと甘いので注意が必要）
	 */
	public static File createMacAliasInFolderByUniqueFileName(File place, File target) {
		File aliasFile = null;
		do {
			StringBuffer uniqueFNameB = new StringBuffer();
			for (int i = 0; i < 8; i++) {
				uniqueFNameB.append(charArray[(int) (Math.random() * charArray.length)]);
			}
			aliasFile = new File(place, uniqueFNameB.toString());
		} while (aliasFile.exists());

		Logging.writeMessage("bmutil will try to make alias file: " + aliasFile.getName(), 2);
		Logging.writeMessage(" Original file's path: " + target.getPath(), 2);

		//        String cmd[] = { commandFolder.getPath() + "/makenewalias", aliasFile.getPath(), target.getPath()};      
		String cmd[] = { "/usr/local/bin/bmutil", "-c", aliasFile.getPath(), target.getPath()};
		CommandExecuter cm = new CommandExecuter(cmd);
		String outputString = cm.doCommand();
		if (cm.returnValue != 0)
			Logging.writeMessage("Output of makenewalias: " + outputString);
		return aliasFile;
	}

	static public char charArray[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
		'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	/**	エイリアスファイルのオリジナルを探す

     @param target エイリアスファイル
     @return オリジナルファイル
	 */
	public static File resolveMacAlias(File target) {
		if (target == null) return null;
		//        String cmd[] = { commandFolder.getPath() + "/resolvalias", target.getPath()};
		String cmd[] = { "/usr/local/bin/bmutil", "-r", target.getPath()};
		String returnValue = (new CommandExecuter(cmd)).doCommand();
		if (returnValue.length() < 1){
			return null;
		}
		if (returnValue.charAt(returnValue.length() -1) == 10)	{
			returnValue = returnValue.substring(0, returnValue.length() - 1);
		}
		return new File(returnValue);
	}

	/**	複数のエイリアスファイルをオリジナルをまとめて探す

     @param target エイリアスファイルの配列
     @return オリジナルファイルの配列
	 */
	public static File[] resolveMacAliases(File target[]) {
		if (target == null) return null;
		//        String cmd[] = { commandFolder.getPath() + "/resolvalias", ""};    
		String cmd[] = { "/usr/local/bin/bmutil", "-r", ""};
		CommandExecuter ce = new CommandExecuter(cmd);
		List<File> originalFiles = new ArrayList<File>();
		for (int i = 0; i < target.length; i++) {
			cmd[2] = target[i].getPath();
			String returnValue = ce.doCommand();
			if (returnValue.charAt(returnValue.length() -1) == 10)	{
				returnValue = returnValue.substring(0, returnValue.length() - 1);
			}
			originalFiles.add(new File(returnValue));
		}
		return (File[]) originalFiles.toArray(new File[0]);
	}

	/**	複数のエイリアスファイルをオリジナルをまとめて探す

     @param place エイリアスファイルが存在するフォルダ
     @return オリジナルファイルの配列
	 */
	public static File[] resolveMacAliases(File place) {
		//        String cmd[] = { commandFolder.getPath() + "/resolvalias", ""};
		String cmd[] = { "/usr/local/bin/bmutil", "-r", ""};
		CommandExecuter ce = new CommandExecuter(cmd);
		List<File> originalFiles = new ArrayList<File>();
		File target[] = place.listFiles(new VisibleFileOnlyFilder());
		for (int i = 0; i < target.length; i++) {
			cmd[2] = target[i].getPath();
			String returnValue = ce.doCommand();
			if (returnValue.charAt(returnValue.length() -1) == 10)	{
				returnValue = returnValue.substring(0, returnValue.length() - 1);
			}
			originalFiles.add(new File(returnValue));
		}
		return (File[]) originalFiles.toArray(new File[0]);
	}
}

class VisibleFileOnlyFilder implements FileFilter {

	public boolean accept(File pathname) {
		if (pathname.isDirectory()) return false;
		if (pathname.getName().startsWith(".")) return false;
		return true;
	}
}
