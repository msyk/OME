package OME.downloadmails;

import java.io.*;
import java.util.Properties;

import OME.*;
import OME.messagemaker.MessageMaker;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.Flags.Flag;
import com.sun.mail.imap.*;
import com.sun.mail.pop3.*;

/**
 * OME_DownloadMailsアプリケーション
 * 
 * 
 * <hr>
 * <h2>OME履歴情報</h2>
 * 
 * <pre>
 *  作成者:新居雅行（Masayuki Nii/msyk@msyk.net）
 * 
 *  :
 *  その後:新居,ぐるり:いろいろ修正、現在に至る
 *  :
 *  2003/8/1:新居:fetchmailのパスをBehavior_Info.txtファイルで指定できるようにした
 *  2004/01/26:ぐるり:fetchmailの実行オプションfetchsizelimitをつけるかどうかのオプションを付加
 *  2005/5/5:新居:メール処理が終わってから処理中を示すファイルを削除するように変更
 *  2005/8/27:新居:NowDownloadingファイルが古い場合に無視する設定を追加
 *  2007/11/25:新居:fetchmailが返すエラーに対応した
 *  2009/6/28:新居:OME_JavaCore2へ移動
 *  2012/7/7:新居:完全にimapベースに移行
 * </pre>
 */
public class OME_DownloadMails implements Runnable {

	public static void main(String args[]) {
		(new OME_DownloadMails()).doDownloading();
		System.exit(0);
	}

	public OME_DownloadMails() {
	}

	public OME_DownloadMails(boolean threading) {
		if (threading) {
			(new Thread(this)).start();
		}
	}

	public void run() {
		doDownloading();
		/*
		 * synchronized(this) { notify(); };
		 */
	}

	private String loggerNameSpace = "net.msyk.ome.downloadmails";

	public void doDownloading() {
		Logging.setupLogger(loggerNameSpace);

		OMEPreferences omePref = OMEPreferences.getInstance(); // OME設定へのインスタンスを得る
		if (omePref.isDownloadMailsMessageStandardOutput())
			Logging.setAlwaysStdOut(true);

		// 未読エイリアスフォルダの中身をきれいにしておく
		// if (omePref.isUnReadAliasSupport())
		// UnreadUtility.getInstance().removeHasRedAliases();
		UnreadUtility.getInstance().removeHasRedAliases(); // 常に稼働することにしよう

//		// 二重にダウンロードしないために、NowDownloadingファイルをチェック
//		File semaphor = new File(omePref.getOMEPref(), "NowDownloading"); // セマフォファイルへの参照
//		long duration = omePref.getIgnoringDuration() * 1000;
//		if (semaphor.exists() && duration > 0) { // セマフォがあって、時限設定もある場合
//			long limitTime = semaphor.lastModified() + duration;
//			if (limitTime > System.currentTimeMillis()) { // 作成したのがずっと昔なら無視
//				Logging.writeMessage("%% OME %% Now Downloading. "
//						+ "OME doesn't download until finish the current downloading job.", loggerNameSpace);
//				return; // なにもしないで終了
//			} else
//				Logging.writeMessage("Ignoreing NowDownloading file.", loggerNameSpace);
//		} else if (semaphor.exists()) { // セマフォがあり、時限設定がない場合
//			Logging.writeMessage("%% OME %% Now Downloading. "
//					+ "OME doesn't download until finish the current downloading job.", loggerNameSpace);
//			return; // なにもしないで終了
//		}
//
//		String[] makeSemaphor = { "touch", semaphor.getPath() }; // セマフォを作成する
//		CommandExecuter mkSemCom = new CommandExecuter(makeSemaphor);
//		mkSemCom.doCommand();

		downloadingIMAP();

		// (new OME.messagemaker.MailProcessor()).processDLEachFiles(); // メール処理

//		String[] rmSemaphor = { "rm", semaphor.getPath() }; // セマフォを削除する
//		CommandExecuter rmSemCom = new CommandExecuter(rmSemaphor);
//		rmSemCom.doCommand();
//
		Logging.applicationTerminate();
	}

	private void downloadingIMAP() {
		Logging.setupLogger(loggerNameSpace);

		OMEPreferences omePref = OMEPreferences.getInstance(); // OME設定へのインスタンスを得る
		if (omePref.isDownloadMailsMessageStandardOutput())
			Logging.setAlwaysStdOut(true);

		ReceiveInfo rInfo = ReceiveInfo.getInstance();
		rInfo.reset();
		while (rInfo.next()) {
			Properties props = System.getProperties();
			Session session = Session.getInstance(props);
			// session.setDebug(true);
			Store store = null;
			boolean isIMAP = true;
			String sign = "";
			try {
				if (rInfo.getProtocol().indexOf("imap") > -1) {
					if (rInfo.getOption().indexOf("SSL") > -1) {
						store = session.getStore("imaps");
						sign = "IMAP over SSL";
					} else {
						store = session.getStore("imap");
						sign = "IMAP";
					}
				} else {
					if (rInfo.getOption().indexOf("SSL") > -1) {
						store = session.getStore("pop3s");
						sign = "POP3 over SSL";
					} else {
						store = session.getStore("pop3");
						sign = "POP3";
					}
					isIMAP = false;
				}

				store.connect(rInfo.getServerName(), Integer.parseInt(rInfo.getServerPort()), rInfo.getAccount(), rInfo.getPassword());
				Logging.setupLogger(loggerNameSpace);
				Logging.writeMessage("%% OME %% Connection Established [" + sign + "]" + rInfo.getServerName()+" by "+rInfo.getAccount());

				// Open a Folder
				Folder folder = store.getFolder("INBOX");
				if (folder == null || !folder.exists()) {
					Logging.writeErrorMessage(2003, null, "IMAP or POP3 folder is not valid.");
					System.exit(1);
				}
				folder.open(Folder.READ_WRITE);

				Message[] msgs = folder.getMessages();
				FetchProfile fp = new FetchProfile();
				fp.add(FetchProfile.Item.CONTENT_INFO);
				fp.add(FetchProfile.Item.ENVELOPE);
				fp.add(FetchProfile.Item.FLAGS);
				folder.fetch(msgs, fp);
				for (int i = 0; i < folder.getMessageCount(); i++) {
					// if (i > 3)
					// break;
					try {
						if (msgs[i] != null) {
							if (!msgs[i].isSet(Flags.Flag.DELETED)) {
								MessageMaker maker = MessageMaker.prepareMessageMaker();
								MimeBodyPart wholePart;
								if (isIMAP) {
									wholePart = new MimeBodyPart(((IMAPMessage) msgs[i]).getMimeStream());
								} else {
									wholePart = new MimeBodyPart(((POP3Message) msgs[i]).getMimeStream());
								}
								String fromData = "(Can't detect From)";
								try	{
									Address[] froms = msgs[i].getFrom();
									StringBuffer fromString = new StringBuffer();
									if (froms != null) {
										for (int k = 0; k < froms.length; k++) {
											if (k != 0) {
												fromString.append(", ");
											}
											fromString.append(froms[k].toString());
										}
										fromData = fromString.toString();
									}
								} catch (Exception e)	{
									
								}
								
								Logging.writeMessage("%% OME %% Download Mail: id=["
										+ ((MimeMessage) msgs[i]).getMessageID() + "]" + ", From:" + fromData);

								maker.process(wholePart);

								Logging.setupLogger(loggerNameSpace);

								if (!omePref.isKeepMessage()) {
									msgs[i].setFlag(Flag.DELETED, true);
								}
							}
						}
					} catch (Exception ex) {
						Logging.setupLogger(loggerNameSpace);
						Logging.writeErrorMessage(2001, ex, "Error in processing one message.");
					}
				}
				if (!omePref.isKeepMessage()) {
					try {
						folder.expunge();
					} catch (Exception e) {
						Logging.writeMessage("%% OME %% expunge method doesn't support.");
					}
				}
				folder.close(!omePref.isKeepMessage());
				store.close();
			} catch (Exception ex) {
				Logging.writeErrorMessage(2002, ex, "Error in downloading messages.");
			}
		}
	}
	//

	// Mountain Lion doesn't contain the fetchmail command. Oh my god.
	/*
	 * code spnipets was in doDownloading(); File frcFile = setupFetchMail();
	 * setupProcMail();
	 * 
	 * String fmCom = omePref.getFetchMailPath(); //fetchmailを起動して実行 if
	 * (omePref.isFetchmailV()) fmCom += " -v"; if (omePref.isFetchmailAllDL())
	 * fmCom += " -a"; if (omePref.isFetchmailUIDL()) fmCom += " -U";
	 * 
	 * CommandExecuter comex = new CommandExecuter(fmCom); int
	 * returnValueFromFetchmail = comex.doCommandWithLogging(false);
	 * 
	 * switch ( returnValueFromFetchmail ) { // case 1: // Error code 1 doesn't
	 * means kind of error, it just says nothing to download now. case 2: case
	 * 13: Logging.writeErrorMessage( 3000 + returnValueFromFetchmail, null, ""
	 * ); break; case 3: case 4: case 5: case 6: case 7: case 8: case 9: case
	 * 10: case 11: case 12: case 14: case 23:
	 * Logging.followingMessageIsImportant(); Exception ex = new Exception (
	 * "Error in fetchmail." ); Logging.writeErrorMessage( 3000, (Exception)ex,
	 * "" ); Logging.writeErrorMessage( 3000 + returnValueFromFetchmail,
	 * (Exception)ex, "" ); break; };
	 * 
	 * frcFile.delete(); //.fetchmailrcファイルを削除する
	 */
	/*
	 * if (omePref.isForceDisconnect()) { fmCom =
	 * omePref.getOMEToolsFolder().getPath() + "/pppdiscon"; comex = new
	 * CommandExecuter(fmCom); String r = comex.doCommand(); //pppdisconを起動する
	 * Logging.writeMessage("Disconnect PPP connection." + r, loggerNameSpace);
	 * }
	 */

	/*
	 * private File setupFetchMail() {
	 * 
	 * StringBuffer fContent = new StringBuffer("");
	 * 
	 * OMEPreferences omePref = OMEPreferences.getInstance(); //OME設定へのインスタンスを得る
	 * ReceiveInfo rInfo = ReceiveInfo.getInstance(); rInfo.reset();
	 * 
	 * while (rInfo.next()) { fContent.append("poll ");
	 * fContent.append(rInfo.getServerName()); fContent.append(" port ");
	 * fContent.append(rInfo.getServerPort()); fContent.append(" proto ");
	 * fContent.append(rInfo.getProtocol()); fContent.append(" user \"");
	 * fContent.append(rInfo.getAccount()); fContent.append("\" pass \"");
	 * fContent.append(rInfo.getPassword()); fContent.append("\" "); if
	 * (omePref.isKeepMessage()) fContent.append("keep ");
	 * fContent.append("mda \"/usr/bin/procmail\" ");
	 * fContent.append("fetchlimit " + omePref.getDownloadLimit()); if
	 * (omePref.getDownloadSizeLimit() > 0) fContent.append(" limit " +
	 * omePref.getDownloadSizeLimit()); if (omePref.getFetchSizeLimit() >= 0)
	 * fContent.append(" fetchsizelimit " + omePref.getFetchSizeLimit()); //
	 * fContent.append(" auth password"); fContent.append(" no mimedecode");
	 * if(rInfo.getOption() != "") fContent.append(" "+rInfo.getOption());
	 * fContent.append("\n"); } String homeDir =
	 * System.getProperty("user.home"); File rcFile = new File(homeDir,
	 * ".fetchmailrc"); try { FileWriter outFile = new FileWriter(rcFile);
	 * outFile.write(fContent.toString()); outFile.close(); //
	 * Runtime.getRuntime().exec("chmod 600 " + rcFile.getAbsolutePath());
	 * String[] setFilePrev = { "chmod", "600", rcFile.getPath()};
	 * CommandExecuter setFilePrevCommand = new CommandExecuter(setFilePrev);
	 * setFilePrevCommand.doCommand(); } catch (Exception ex) {
	 * Logging.followingMessageIsImportant(); Logging.writeErrorMessage(33, ex,
	 * "Error in writing .fetchmailrc : ", loggerNameSpace); } return rcFile; }
	 * 
	 * private void setupProcMail() { try { OMEPreferences omePref =
	 * OMEPreferences.getInstance(); //OME設定へのインスタンスを得る String backupFileDTStr =
	 * omePref.getCurrentDTString(); //ログファイル名に含める日付文字列 File dlFolder =
	 * omePref.getOMETemp(); //tempフォルダを参照 File hideFolder = new File(dlFolder,
	 * "hide"); //hideフォルダを参照
	 * 
	 * StringBuffer fContent = new StringBuffer("");
	 * fContent.append("LOGFILE="); fContent.append(hideFolder.getPath());
	 * fContent.append("/procmail_log-"); fContent.append(backupFileDTStr);
	 * fContent.append(".txt\n\n");
	 * 
	 * // fContent.append(":0"); //
	 * fContent.append(dlFolder.getPath()+"/lockfile"); //
	 * fContent.append("\n*\n{\n"); if (omePref.isMoveFiles())
	 * fContent.append(":0 c : \n"); else fContent.append(":0 : \n");
	 * fContent.append(dlFolder.getPath()); fContent.append("\n"); if
	 * (omePref.isMoveFiles()) { fContent.append(":0 : \n");
	 * fContent.append(hideFolder.getPath()); fContent.append("/Download-");
	 * fContent.append(backupFileDTStr); fContent.append("\n"); }
	 * 
	 * String homeDir = System.getProperty("user.home"); File dotProcmailRCFile
	 * = new File(homeDir, ".procmailrc"); FileOutputStream outFile = new
	 * FileOutputStream( dotProcmailRCFile );
	 * outFile.write(fContent.toString().getBytes("utf-8")); outFile.close();
	 * 
	 * String[] setFilePrev = { "chmod", "600", dotProcmailRCFile.getPath()};
	 * CommandExecuter setFilePrevCommand = new CommandExecuter(setFilePrev);
	 * setFilePrevCommand.doCommand();
	 * 
	 * } catch (Exception ex) { Logging.followingMessageIsImportant();
	 * Logging.writeErrorMessage(31, ex, "Error in writing .procmailrc.",
	 * loggerNameSpace); } }
	 */
}
