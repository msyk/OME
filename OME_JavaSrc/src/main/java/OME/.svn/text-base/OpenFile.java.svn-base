package OME;

import java.io.*;

/**
 * ファイルを開く、すなわち、Finderでのダブルクリックと同じ結果になる
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */

public class OpenFile {

    /**
     * 指定したファイルを、指定したアプリケーションで開く
     * @param appFile 開くアプリケーション、あるいは起動するアプリケーション
     * @param docFile　開くファイル
     */
    static public void byApplication(File appFile, File docFile) {

        String param[] = { "open", "-a", appFile.getPath(), docFile.getPath()};
        CommandExecuter openCmd = new CommandExecuter(param);
        openCmd.doCommand();

    }

    /**
     * 指定したファイルを、StuffIt Expanderで開く。StuffIt Expanderは、なるべくがんばって探す
     * @param docFile　開くファイル
     */
    static public void byStuffItExpander(File docFile) {

        String siAvailablePath[] = { "/Applications/Utilities/StufIt Expander.app",
				"/Applications/StuffIt 10.0/StuffIt Expander.app",
				"/Applications/StuffIt 11/StuffIt Expander.app",
                "/Applications/StuffIt Deluxe 7.0J/StuffIt ドラッグ＆ドロップ/StuffIt Expander.app",
                "/Applications/StuffIt Deluxe 7.0/StuffIt Drag and Drop/StuffIt Expander.app",
                "/Applications/StuffIt Deluxe 6.5.1J/StuffIt ドラッグ＆ドロップ/StuffIt Expander.app",
                "/Applications/StuffIt Deluxe 6.5.1/StuffIt Drag and Drop/StuffIt Expander.app",
                "/Applications/StuffIt Deluxe 6.5.2/StuffIt Drag and Drop/StuffIt Expander.app",
                "/Applications/StuffIt Deluxe 6.5/StuffIt Drag and Drop/StuffIt Expander.app",
                "/Applications/StuffIt Deluxe/StuffIt Drag and Drop/StuffIt Expander.app",
                "/Applications/StuffIt Deluxe 6.0/StuffIt Drag and Drop/StuffIt Expander.app"};

        File siExpander = null;
        boolean sieExists = false;
        for (int i = 0; i < siAvailablePath.length; i++) {
            if (sieExists = (siExpander = new File(siAvailablePath[i])).exists()) break;
        }
        if (!sieExists) {
            ByteArrayOutputStream outSt = new ByteArrayOutputStream();
            String findCmdLine[] = { "find", "/Applications", "-name", "StuffIt Expander.app"};
            CommandExecuter findCmd = new CommandExecuter(findCmdLine);
            findCmd.doCommand(false, outSt);
            String findPath = outSt.toString();
            try {
                outSt.close();
            } catch (Exception e) {}
            int startPos = findPath.indexOf("/Applications");
            if (startPos < 0) return;
            int lineEndPos = startPos + findPath.substring(startPos).indexOf("\n");
            if (lineEndPos < startPos) lineEndPos = findPath.substring(startPos).length();
            siExpander = new File(findPath.substring(startPos, lineEndPos));
        }
        byApplication(siExpander, docFile);
    }

    /**
     * ディレクトリを順番に調べる、各ディレクトリに対してアプリケーションが存在すれば、そのアプリケーションを実行する
     * @param dirs　検索するディレクトリ
     * @param apps　検索するアプリケーション
     */
    static public void searchAndOpen(File dirs[], String apps[]) {
        File targetFile = null;
        for (int ix = 0; ix < dirs.length; ix++) {
            for (int iy = 0; iy < apps.length; iy++) {
                if ((targetFile = new File(dirs[ix], apps[iy])).exists()) {
                    if (apps[iy].endsWith(".app")) {
                        String param[] = { "open", targetFile.getPath()};
                        CommandExecuter openCmd = new CommandExecuter(param);
                        openCmd.doCommand();
                    } else {
                        CommandExecuter openCmd = new CommandExecuter(targetFile.getPath());
                        openCmd.doCommand();
                    }
                    return;
                }
            }
        }
    }
}
