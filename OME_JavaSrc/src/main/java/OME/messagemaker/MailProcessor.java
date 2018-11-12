/* -*- c-basic-offset: 4; indent-tabs-mode: nil -*- */

package OME.messagemaker;

import java.io.*;

import OME.*;

/**
 * 受信したメールを処理して、メッセージファイルを処理する。
 *
 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 *
 *    :
 * 2004/1/4:新居:メールのバックアップファイルから復元する機能を追加。
 * 2004/1/14:新居:MessageMakerクラスをベースにしたものに大改造。
 * 2004/1/15:新居:エラー処理の見直し。エラーがあったときにソースを残すプログラムをMessageMakerに移動
 *			:
 * 2008/10/19:新居:Growlの呼び出しをやめた
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
 *
 * @author 新居雅行（Masayuki Nii/msyk@msyk.net）
 * $Revision: 1.22 $
 */
public class MailProcessor {

    /**	コマンドラインで利用するときに起動時に実行されるメソッド。
		引数を指定しないと、tempフォルダにあるファイルがメッセージの元のソースとして処理を行う。
		このとき、temp/originalフォルダにメールソースファイルがあると、それも処理を行う。
		originalフォルダにあるファイルは、メールが複数含まれているファイルであれば、それを分割して処理する。
		引数を指定すると、指定したメールファイルの処理を行う。
		
     @param args 処理を行うメールのソースが含まれているファイル。1ファイルは1メール分のみ。引数は1つのみ
     */
    public static void main(String args[]) {
		try {
			MailProcessor myself = new MailProcessor();
			if ( args.length == 0 )		{	//つまり引数がない場合
				File origFolder = new File(OMEPreferences.getInstance().getOMETemp(), originalSourcesFolder);
				if (origFolder.exists()) //originalsフォルダが存在すれば、そこからメールごとのファイルに切り出す
					myself.divideFromOriginal();
				myself.processDLEachFiles();
			}
			else	{
				Logging.setupLogger(loggerNameSpace);
				File mailSource = new File(args[0]); //ファイルを参照し
				try {
					MessageMaker defaultMessageMaker = MessageMaker.prepareMessageMaker();
					defaultMessageMaker.process(mailSource);	//処理を行う
				} catch (Exception ex) {
					Logging.writeMessage(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
		catch (Exception e) {
			//
		}
        System.exit(0);
    }

    private static String loggerNameSpace = "net.msyk.ome.messagemaker";

    private String backupPath; //メールのソースをバックアップしているファイルのパス

    private static String originalSourcesFolder = "originals";

    /**	temp/originalsフォルダにあるメールのソース（バックアップ）から、メールごとの個別のファイルに分割し
     tempフォルダに書き出す
     */
    public void divideFromOriginal() {

        Logging.setupLogger(loggerNameSpace);
		File tempFolder;
		try {
			tempFolder = OMEPreferences.getInstance().getOMETemp();
		}
		catch (Exception e) {
			return;
		}
        File origFolder = new File(tempFolder, originalSourcesFolder);

        byte buffer[] = new byte[20000];
        String fileNameBase = "MailSource-";
        OutputStream out = null;
        LineInputStream in = null;

        int counter = 10000;
        try {
            out = new FileOutputStream(new File(tempFolder, fileNameBase + counter));
            String originalFiles[] = origFolder.list(new FilteringFiles());
            //originalsにあるファイル一覧を取り出すが、.ではじまる非表示ファイルを含めない
            for (int i = 0; i < originalFiles.length; i++) { //ファイルそれぞれについて
                File mailBackup = new File(origFolder, originalFiles[i]); //ファイルを参照し
                Logging.writeMessage("##OME##:Open file:" + originalFiles[i]);
                int readings = 0;
                in = new LineInputStream(mailBackup);
				ByteArrayOutputStream fileContent = new ByteArrayOutputStream();
				boolean hasAnyHeaders = false, isLastLineMBOXMarker = false;
				int endPosition = 0;
                do {
                    readings = in.readLine(buffer);
                    if ((readings == -1) || isStartMail(buffer)) {
//                        out.close();
						if ( hasAnyHeaders )	{
							out = new FileOutputStream(new File(tempFolder, fileNameBase + counter));
							if ( ! isLastLineMBOXMarker )
								out.write( fileContent.toByteArray() );
							else
								out.write( fileContent.toByteArray(), 0, endPosition );
							out.close();
							Logging.writeMessage("##OME##:Store file:" + fileNameBase + counter);
							counter++;
							hasAnyHeaders = false;
							fileContent = new ByteArrayOutputStream();
						}
                    }
//                    if (readings != -1) out.write(buffer, 0, readings);
                    if (readings != -1)	{
						endPosition = fileContent.toByteArray().length;
						String currentLine = new String(buffer, 0, readings);
						fileContent.write( buffer, 0, readings );
						if ( currentLine.startsWith( "To:" ) || currentLine.startsWith( "From:" ) || currentLine.startsWith( "Subject:" ) )
							hasAnyHeaders = true;
						if ( currentLine.startsWith( "From " ))	{
							isLastLineMBOXMarker = true;
						}
					}
                } while (readings != -1);
                in.close();
				mailBackup.deleteOnExit();
            }
            //		if (in != null) { in.close(); }
            if (out != null) {
                out.close();
            }
        } catch (Exception ex) {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception ignore) {}
            Logging.writeMessage(ex.getMessage());
        }
    }

    /**	文字列の判断を行う関数。ちょっとべたべただが、効率を重視してみた。引数のbyte配列の先頭が"Return-Path: "かを判断
     @param buffer 調べるbyte配列
     @return 引数の先頭がReturn-Path:ならtrue、そうでなければfalse
     */
    private boolean isStartMail(byte[] buffer) {
        if (buffer[0] != 'R') return false;
        if (buffer[1] != 'e') return false;
        if (buffer[2] != 't') return false;
        if (buffer[3] != 'u') return false;
        if (buffer[4] != 'r') return false;
        if (buffer[5] != 'n') return false;
        if (buffer[6] != '-') return false;
        if ((buffer[7] != 'p') && (buffer[7] != 'P')) return false;
        if (buffer[8] != 'a') return false;
        if (buffer[9] != 't') return false;
        if (buffer[10] != 'h') return false;
        if (buffer[11] != ':') return false;
        if (buffer[12] != ' ') return false;
        return true;
    }
	
	
	
//	From fshigeki@mui.biglobe.ne.jp Sun Jul 27 15:57:11 2008


    /**	tempフォルダの中にあるファイルを取り出し、処理をする。メールごとに行わない後処理（通知等）もここで行う
     */
    public void processDLEachFiles() {

		try {
			Logging.setupLogger(loggerNameSpace);
			OMEPreferences omePref = OMEPreferences.getInstance();

			File dlFileFolder = omePref.getOMETemp(); //tempフォルダを参照
			String dlFiles[] = dlFileFolder.list(new FilteringFiles());
			//tempにあるファイル一覧を取り出すが、.ではじまる非表示ファイルを含めない
			for (int i = 0; i < dlFiles.length; i++) { //ファイルそれぞれについて
				File mailSource = new File(dlFileFolder, dlFiles[i]); //ファイルを参照し
				try {
					MessageMaker defaultMessageMaker = MessageMaker.prepareMessageMaker();
					defaultMessageMaker.process(mailSource);
				} catch (Exception ex) {
					Logging.writeMessage(ex.getMessage());
					ex.printStackTrace();
				} finally {
					if (omePref.isEraseFiles()) { //メールファイルを削除
						if (!mailSource.delete()) {
							Logging.writeMessage("### Can't Delete Original File, Why??");
							throw new Exception();
						}
					} //メールファイルの処理は確実に実行されるように、PartProcessorベースにしないことにする
					//ここでのforループはファイルの存在が前提になっているので、不都合はないと思われる
				}
				SerialCodeGenerator.getInstance().updateSerialFile();
			}

			//ファイルを処理したあとにGrowlで通知する
	/*        if (dlFiles.length > 0) {
				GrowlNotify.sendDownloadedMessage( dlFiles.length );
			}
	*/        //ファイルを処理したあとに実行するスクリプトの起動
			if (dlFiles.length > 0) {
				String homeFolder = System.getProperty("user.home");
				File dirs[] = { omePref.getOMEPref(), new File(homeFolder, "bin"), new File("/usr/local/bin"),
						omePref.getOMEToolsFolder()};
				String files[] = { "MailProcessed.app", "MailProcessed.sh"};
				OpenFile.searchAndOpen(dirs, files);
			}
		}
		catch (Exception ex) {
			// do nothing so far
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
