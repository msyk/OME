package OME.sendmail;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

import OME.*;
import OME.mailformatinfo.MailFormatInfo;
import OME.textformatter.*;
import OME.messagemaker.GrowlNotify;

import com.sun.mail.smtp.SMTPTransport;

/**
 メールを送信するためのクラス

 <hr>
 <h2>OME履歴情報</h2>
 <pre>
 作成者:新居雅行（Masayuki Nii/msyk@msyk.net）

 ：
 2003/8/11:新居:DIGEST-MD5対応（JavaMail 1.3.1）
 2005/9/4:新居:NowSendingファイルがあっても古い場合は無視するオプションの追加
 2006/2/16:ぐるり:SSL対応開始
 2006/7/16:新居:SSHに対応
 2008/1/6:新居:莉ファクタリング
 2009/6/28:新居:OME_JavaCore2へ移動
 2009/11/10:新居:SSL証明書ファイルの読み込みに対応
 </pre>
 */
public class OME_SendMail implements Runnable {

    public static void main(String args[]) {
		if ( args.length == 0 )
			(new OME_SendMail()).sendingOutBoxFiles(true);
		else	{
			if ( args[0].equalsIgnoreCase("STDIN") )
				(new OME_SendMail()).sendingOneMessage(null);
			else
				(new OME_SendMail()).sendingOneMessage(new File(args[0]));
			System.exit(0);
		}
    }

    private Session thisSession;

    private Transport tr;

    private String loggerNameSpace = "net.msyk.ome.sendmail";
	
	private String archivingTempPath = "/tmp/ome/";

	private OMEPreferences omePref;
	
	private SenderInfo sInfo;

	private MailFormatInfo mfInfo;

    public OME_SendMail() {
		omePref = OMEPreferences.getInstance();
		sInfo = SenderInfo.getInstance();
		mfInfo = MailFormatInfo.getInstance();
		 
		Logging.setupLogger(loggerNameSpace);
        if (omePref.isDownloadMailsMessageStandardOutput())
			Logging.setAlwaysStdOut(true);
	}

    public OME_SendMail(boolean threading) {
        if (threading) {
            (new Thread(this)).start();
        }
    }

    public void run() {
        sendingOutBoxFiles(false);
        synchronized (this) {
            notify();
        }
        ;
    }

    synchronized public void sendingOutBoxFiles(boolean autoExit) {

		if ( ! setupSemafor() )		return;

        File outBox = new File(omePref.getOMERoot(), "OutBox"); //OutBoxフォルダを参照
        File uploadFiles[] = outBox.listFiles(new uploadFileFilter()); //OutBoxフォルダにあるファイル一覧を取得する

        if (uploadFiles.length > 0) { //OutBoxにファイルが存在する場合
			if ( sInfo.isSSH() )
				waitForSSHPort();
			setupSession();
			
			try	{
				for (int i = 0; i < uploadFiles.length; i++)
					if (sendingMsgFile(uploadFiles[i]))
						renameUploadFile( uploadFiles[i] );
				tr.close();
			} catch (Exception e) {
				Logging.writeErrorMessage( 303, e, e.getMessage() );
			}
			GrowlNotify.sendSentMessage();
			clearnUpSemafor();
		}
        if (autoExit) System.exit(0);
    }

    synchronized public void sendingOneMessage(File sendingFile) {
		if ( sInfo.isSSH() )
			waitForSSHPort();
		setupSession();
		
		try	{
			if ( sendingMsgFile( sendingFile ) )
				renameUploadFile( sendingFile );
			tr.close();
		} catch (Exception e) {
			Logging.writeErrorMessage( 303, e, e.getMessage() );
		}
    }

    private class uploadFileFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            return name.toLowerCase().startsWith("upload");
        }
    }

	private boolean setupSemafor()	{
		File semaphor = new File(omePref.getOMEPref(), "NowSending"); //セマフォファイルへの参照
		long duration = omePref.getIgnoringDuration() * 1000;
		if ( semaphor.exists() && duration > 0 )	{	//セマフォがあって、時限設定もある場合
			long limitTime = semaphor.lastModified() + duration;
			if ( limitTime > System.currentTimeMillis()	){	//作成したのがずっと昔なら無視
	            Logging.writeMessage("%% OME %% Now Sending. " + 
					"OME doesn't send until finish the current sending job.", loggerNameSpace);
    	        return false; //なにもしないで終了
			}
			else 
	            Logging.writeMessage("Ignoreing NowSending file.", loggerNameSpace);
		}
		else if ( semaphor.exists() ){	//セマフォがあり、時限設定がない場合
	            Logging.writeMessage("%% OME %% Now NowSending. " + 
					"OME doesn't send until finish the current sending job.", loggerNameSpace);
            return false; //なにもしないで終了
        }
        String[] makeSemaphor = { "touch", semaphor.getPath()}; //セマフォを作成する
        CommandExecuter mkSemCom = new CommandExecuter(makeSemaphor);
        mkSemCom.doCommand();
		return true;
	}

	private void clearnUpSemafor()	{
		File semaphor = new File(omePref.getOMEPref(), "NowSending"); //セマフォファイルへの参照
        String[] rmSemaphor = { "rm", semaphor.getPath()}; //セマフォを削除する
        CommandExecuter rmSemCom = new CommandExecuter(rmSemaphor);
        rmSemCom.doCommand();
	}

	private void waitForSSHPort()	{
		int returnValue = new CommandExecuter(sInfo.getSSHCommand()).doCommandWithLogging(true);
				
		//トンネルするポートが開くのを待つ
		Socket socketSMTP;
		boolean isSocketOpened = false;
		for (	long startTime = System.currentTimeMillis();	//現在の時間を記録
				(System.currentTimeMillis() -  startTime) < 30000 ; )	{	//30秒するとタイムアウト
			try{
				socketSMTP = new Socket( sInfo.getSMTPServer(), 
									Integer.parseInt(sInfo.getSMTPPort()) );	//SMTPのポートをチェック
				socketSMTP.close();		//開いていればこちらにくる
				isSocketOpened = true;
				break;					//forループを終了
			} catch ( IOException ioException )		{
				//ポートが開いていないと、new Socketでこの例外が発生する
			//	Logging.writeMessage(ioException.getMessage(), loggerNameSpace);
			//	ioException.printStackTrace();
			} catch ( Exception e )		{
				Logging.writeMessage(e.getMessage(), loggerNameSpace);
				e.printStackTrace();
			}
		}
		if ( ! isSocketOpened )	{	//ソケットが開いていない場合はメッセージを出して終了する
			Logging.writeMessage("%% OME Error 309 %% " + 
				"Failture of SSH Tunneling. Maybe fail to open the socket.", loggerNameSpace);
			return;
		}
	}
	
	private void setupSession()	{
		Properties props = new Properties();
		props.put("mail.smtp.host", sInfo.getSMTPServer());
		props.put("mail.smtp.auth", String.valueOf(sInfo.isSMTPAuth()));
		props.put("mail.smtp.saslrealms", sInfo.getSMTPServer());
		props.put("mail.smtp.user", String.valueOf(sInfo.getSMTPAccount()));
		props.put("mail.smtp.port", sInfo.getSMTPPort());
		props.put("mail.smtp.ssl", String.valueOf(sInfo.isSSL()));
		props.put("mail.smtp.starttls.enable", String.valueOf(sInfo.isSSL()));

		if ( omePref.getUseKeyFileWithPassword().length() > 0 )	{
			System.setProperty( "javax.net.ssl.trustStorePassword", omePref.getUseKeyFileWithPassword() );
			System.setProperty( "javax.net.ssl.trustStore", System.getProperty("user.home") + "/.keystore" );
		}
		
		try {
			Logging.writeMessage(java.net.InetAddress.getLocalHost().getHostName());
		} catch (Exception e) {
			Logging.writeMessage("%% OME Error 306 %% Can't Get HostName: " + e.getMessage());
			props.put("mail.smtp.localhost", "localhost");
		}
		try {
			if (sInfo.isSMTPAuth())
				thisSession = Session.getDefaultInstance(props, new MyAuthenticator(sInfo.getSMTPAccount(), sInfo
						.getSMTPPassword()));
			else
				thisSession = Session.getDefaultInstance(props);
			thisSession.setDebug(omePref.isSMTPDebug());
			tr = thisSession.getTransport("smtp");
			((SMTPTransport) tr).setSASLRealm(sInfo.getSMTPServer());

			try	{
				if (sInfo.isSMTPAuth())
					tr.connect(sInfo.getSMTPServer(), -1, sInfo.getSMTPAccount(), sInfo.getSMTPPassword());
				else
					tr.connect(sInfo.getSMTPServer(), -1, null, null);
			} catch ( Exception ex )		{
				Exception originalEx = getOriginalException( ex );
				if ( ex instanceof javax.mail.AuthenticationFailedException )	{
					/*	SMTP認証が通らないとき, , 認証できない
						javax.mail.AuthenticationFailedException
					*/
					String[] messageParams = { };
					Logging.writeErrorMessage( 306, (Exception)ex, messageParams );
					throw new Exception( "" );
				}
				else if ( ex instanceof javax.mail.MessagingException )	{
					if ( originalEx instanceof java.net.UnknownHostException )	{
						/*	送信メールサーバに指定したホストが応答しない
							javax.mail.MessagingException
							java.net.UnknownHostException						
						*/
						String[] messageParams = {sInfo.getSMTPServer()};
						Logging.writeErrorMessage( 304, (Exception)ex, messageParams );
						throw new Exception( "" );
					}
					else if ( originalEx instanceof java.net.ConnectException )	{
						/*	ポートが違う場合, 認証が必要なのに指定がない／例外のクラストメッセージ
							javax.mail.MessagingException
							java.net.ConnectException(Operation timed out)
						*/
						/*	ホストは存在するけど、SMTPサーバが立っていない場合
							javax.mail.MessagingException
							java.net.ConnectException（Connection Refused）
						*/
						String[] messageParams = { ex.getLocalizedMessage() };
						Logging.writeErrorMessage( 305, (Exception)ex, messageParams );
						throw new Exception( "" );
					}
					else
						throw ex;
				}
				else
					throw ex;
			}
		} catch (Exception e) {
			Logging.writeErrorMessage( 303, e, e.getMessage() );
		}
	}

	private void renameUploadFile( File targetFile )	{
		if( targetFile == null )	return;
        File outBox = new File(omePref.getOMERoot(), "OutBox"); //OutBoxフォルダを参照
        File hideBox = new File(outBox, "Sent"); //Sentフォルダを参照
		int extStart = targetFile.getName().lastIndexOf(".");
		String origName = targetFile.getName().substring(0, extStart);
		String newName = origName + Integer.toString((int) (Math.random() * 1000000)) + ".wmail";
		while ((new File(hideBox, newName)).exists()) {
			newName = origName + Integer.toString((int) (Math.random() * 1000000)) + ".wmail";
		}
		targetFile.renameTo(new File(hideBox, newName));
	}
	
    public boolean sendingMsgFile(File msgFile) throws Exception {
        boolean returnVal = true;
        String subjStr = "";
        String fromAddStr = sInfo.getSenderAddress();
        String fromNameStr = sInfo.getSenderName();
        String toStr = "";
        String ccStr = "";
        String bccStr = "";
        String irtStr = "";
        String refStr = "";
        String cType = "";
        Locale dlocale = omePref.getOMELocale();
        Locale omeLocale = omePref.getOMELocale();
        StringBuffer bodyMsg = new StringBuffer("");
        String newLine = System.getProperty("line.separator");
        byte newLineBytes[] = newLine.getBytes();
        Vector attachList = new Vector();
        Properties addHeaders = new Properties();
		HTMLExpander expander = null;
		String proccessedBody = "";
		String fileName = msgFile.getName();
		byte buffer[] = new byte[15000];

        try {
            OME.LineInputStream inFile = null;
			if ( msgFile != null )
				inFile = new OME.LineInputStream(msgFile);
			else
				inFile = new OME.LineInputStream(System.in);

            boolean isEOF = false;
            boolean isHeader = true;
            while (isEOF == false) {
                int lineLen = inFile.readLineNoNL(buffer);
                if (lineLen < 0)
                    isEOF = true;
                else {
                    String aLine = new String(buffer, 0, lineLen, mfInfo.getUploadFileCode(omeLocale));
                    if (aLine == null) break;
                    if (isHeader) { //ヘッダとして作成したデータの取り出し
                        if (aLine.toLowerCase().startsWith("subject: "))
                            subjStr = aLine.substring(9);
                        else if (aLine.toLowerCase().startsWith("from: ")) {
                            if (!fromAddStr.equals(""))
								Logging.writeMessage(
									"%% OME Warning 346 %% Two or more From header in the sending file:"
                                    + fileName);
                            InternetAddress fromAddrArray[] = InternetAddress.parse(aLine.substring(6), true);
                            addressMIME(fromAddrArray, dlocale);
                            fromAddStr = fromAddrArray[0].getAddress();
                            String personal = fromAddrArray[0].getPersonal();
                            if (personal != null && personal.length() > 0) fromNameStr = personal;
                            //                            if( fromAddrArray[0].getPersonal().length() > 0 )
                            //                                fromNameStr = fromAddrArray[0].getPersonal();
                        } else if (aLine.toLowerCase().startsWith("fromname: ")) {
                            if (!fromNameStr.equals(""))
								Logging.writeMessage(
									"%% OME Warning 341 %% Two or more FromName header in the sending file:"
                                    + fileName);
                            fromNameStr = aLine.substring(10);
                        } else if (aLine.toLowerCase().startsWith("to: "))
                            if (toStr.equals(""))
                                toStr = aLine.substring(4);
                            else
                                toStr = toStr + ", " + aLine.substring(4);
                        else if (aLine.toLowerCase().startsWith("cc: "))
                            if (ccStr.equals(""))
                                ccStr = aLine.substring(4);
                            else
                                ccStr = ccStr + ", " + aLine.substring(4);
                        else if (aLine.toLowerCase().startsWith("bcc: "))
                            if (bccStr.equals(""))
                                bccStr = aLine.substring(4);
                            else
                                bccStr = bccStr + ", " + aLine.substring(5);
                        else if (aLine.toLowerCase().startsWith("in-reply-to: ")) {
                            if (!irtStr.equals(""))
                                    Logging
                                            .writeMessage("%% OME Warning 342 %% Two or more In-Reply-To header in the sending file:"
                                                    + fileName);
                            irtStr = aLine.substring(13);
                        } else if (aLine.toLowerCase().startsWith("references: ")) {
                            if (!refStr.equals(""))
                                    Logging
                                            .writeMessage("%% OME Warning 343 %% Two or more References header in the sending file:"
                                                    + fileName);
                            refStr = aLine.substring(12);
                        } else if (aLine.toLowerCase().startsWith("locale: ")) {
                            if (!fromNameStr.equals(""))
                                    Logging
                                            .writeMessage("%% OME Warning 344 %% Two or more Locale header in the sending file:"
                                                    + fileName);
                            dlocale = InternationalUtils.createLocaleFromString(aLine.substring(8));
                        } else if (aLine.toLowerCase().startsWith("content-type: ")) {
                            if (!cType.equals(""))
                                    Logging
                                            .writeMessage("%% OME Error 345 %% Two or more Content-Type header in the sending file:"
                                                    + fileName);
                            cType = aLine.substring(13);
                        } else if (aLine.toLowerCase().startsWith("attachment: ")) {
							File attachFile;
							if ( aLine.charAt(12) == '~' )
								attachFile = new File(System.getProperty("user.home") + aLine.substring(13));
							else
								attachFile = new File(aLine.substring(12));
								
                            if (attachFile.exists())
								if ( attachFile.isDirectory() )	{
									String aName = attachFile.getName();
									File arcFile = new File ( archivingTempPath + aName + ".zip" );
									attachList.add(arcFile);
									(new File(archivingTempPath)).mkdirs();
									String[] com = { "ditto", "-c", "-k",  "--sequesterRsrc", "--keepParent", 
											attachFile.getPath(), arcFile.getPath() };
									new CommandExecuter(com).doCommandWithLogging(false);
								}
								else
									attachList.add(attachFile);
                            else
                                Logging.writeMessage("%% OME Error 307 %% Nothing attathcment file. "
                                        + aLine.substring(12));
                        } else {
                            int spPos = aLine.indexOf(" ");
                            if ((spPos >= 1) && (spPos < aLine.length()))
                                    addHeaders.setProperty(aLine.substring(0, spPos - 1), aLine.substring(spPos + 1));
                        }
                    } else if (aLine != null) { //本文の処理。ここでは本文を分離するだけ。
						bodyMsg.append(aLine); 
                        bodyMsg.append(newLine); //末尾の改行
					}
                    if (aLine.equals("")) //空行ならそれ以後はヘッダではない
                            isHeader = false;
				}
            }
            inFile.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logging.followingMessageIsImportant();
            Logging.writeMessage("%% OME Error 301 %% Read error in message file. " + e.getMessage());
            returnVal = false;
        }
//System.out.println( "####### Original Body\n"+bodyMsg.toString()+"\n\n");
		/* 本文の処理	*/
		if (omePref.isHTMLExpanding())	{	//HTML展開が指定されているとき
			expander = new HTMLExpander();
			proccessedBody = expander.expandHTML( bodyMsg.toString() );
		} else {
			proccessedBody = bodyMsg.toString();
		}
//System.out.println( "####### Proccessed Body\n"+proccessedBody+"\n\n");
		String eachBodyLines[] = proccessedBody.split( newLine, -1 );
		bodyMsg = new StringBuffer("");
		for ( int i = 0 ; i < eachBodyLines.length ;  i++ )	{
//System.out.println( "####### Body line:"+eachBodyLines[i]+"\n\n");
			if (eachBodyLines[i].startsWith("<!--OME_SendMail:NOBREAK-->")) {//行の最初がこのディレクティブなら
				bodyMsg.append( eachBodyLines[i].substring(27) ); //ディレクティブを削除して追加
			}
			else if (eachBodyLines[i].startsWith("<!--OME_SendMail:INDENTING")) {//行の最初がこのディレクティブなら
							/*	<!--OME_SendMail:INDENTING:x:y:z-->
								x:左インデント幅、y:右インデント幅、z:箇条書きマークor番号など	*/
				String[] params = eachBodyLines[i].split( ":", -1 );
				int leftIndent = 3; int rightIndent = 0; String markStr = "";
				try	{
					leftIndent = Integer.parseInt( params[2] );
					rightIndent = Integer.parseInt( params[3] );
					markStr = params[4];
				} catch ( Exception e )	{	/* Exception Ignoring */	}
				int endOfDirective = eachBodyLines[i].indexOf( "-->" );
				LineDevider.convertAppend(bodyMsg, 
					eachBodyLines[i].substring( endOfDirective+3 ), omePref.getOneLineLimit(), markStr);
			}
			else	{
				if (eachBodyLines[i].startsWith(omePref.getCommentHead())) //行の最初が引用符である場合
					bodyMsg.append(eachBodyLines[i]); //改行しないでそのまま本文に
				else		//改行して本文にセットする
					LineDevider.convertAppend(bodyMsg, eachBodyLines[i], omePref.getOneLineLimit());
			}
			bodyMsg.append(newLine); //末尾の改行
		}
//System.out.println( "####### Final Body\n"+bodyMsg.toString()+"\n\n");

        if (bodyMsg.length() == 0) {
            Logging.followingMessageIsImportant();
            Logging.writeMessage("%% OME Error 325 %% Can't send. No body in message file. " + fileName);
            return false;
        }
        //System.out.println(toStr);
        //System.out.println(bccStr);
        //System.out.println(ccStr);
        MimeMessage msg;
        try {
            msg = new MimeMessage(thisSession);

            thisSession.getProperties().put("mail.from", fromAddStr);
            InternetAddress fromAddr = new InternetAddress();
            fromAddr.setAddress(fromAddStr);
            //            fromAddr.setPersonal(MimeUtility.encodeText(fromNameStr, "ISO-2022-JP", "B"));
            if (!fromNameStr.trim().equals("")) {
                fromAddr.setPersonal(MimeUtility.encodeText(fromNameStr, mfInfo.getSendingHeaderCode(dlocale), mfInfo
                        .getSendingHeaderEncode(dlocale)));
            }
            msg.setFrom(fromAddr);

            InternetAddress toAddrArray[] = InternetAddress.parse(toStr, true);
            addressMIME(toAddrArray, dlocale);
            msg.setRecipients(Message.RecipientType.TO, toAddrArray);
            if (!ccStr.equals("")) {
                InternetAddress ccAddrArray[] = InternetAddress.parse(ccStr, true);
                addressMIME(ccAddrArray, dlocale);
                msg.setRecipients(Message.RecipientType.CC, ccAddrArray);
            }
            if (!bccStr.equals("")) {
                InternetAddress bccAddrArray[] = InternetAddress.parse(bccStr, true);
                addressMIME(bccAddrArray, dlocale);
                msg.setRecipients(Message.RecipientType.BCC, bccAddrArray);
            }

            msg.setSubject(MimeUtility.encodeText(subjStr, mfInfo.getSendingHeaderCode(dlocale), mfInfo
                    .getSendingHeaderEncode(dlocale)));
            msg.setSentDate(new Date());
            msg.setHeader("X-Mailer", "OME for OS X Mountain Lion <http://msyk.net/ome> (Build Date: " + BuildDate.ofString()
                    + ")");
            if (!irtStr.equals("")) msg.setHeader("In-Reply-To", irtStr);
            if (!refStr.equals("")) msg.setHeader("References", refStr);

            Enumeration e = addHeaders.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                msg.setHeader(key, addHeaders.getProperty(key));
            }
        } catch (Exception e) {
            Logging.followingMessageIsImportant();
            Logging.writeMessage("%% OME Error 327 %% Sending Error in setting headers:" + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        try {
            if (attachList.size() < 1) {
                if (cType.length() == 0)
                    msg.setText(bodyMsg.toString(), mfInfo.getSendingBodyCode(dlocale));
                else
                    msg.setContent(bodyMsg.toString(), cType);
            } else {
                MimeMultipart bodyPart = new MimeMultipart();

                MimeBodyPart mainMsg = new MimeBodyPart();

                mainMsg.setText(bodyMsg.toString(), mfInfo.getSendingBodyCode(dlocale));
                bodyPart.addBodyPart(mainMsg);
                /*                
                 String fNameEnc = mfInfo.getSendingBodyEncode(dlocale);
                 if(fNameEnc.length() < 1)
                 fNameEnc = "B";
                 */
                for (int i = 0; i < attachList.size(); i++) {
					File targetFile = (File) (attachList.get(i));
                    MimeBodyPart attachPart = new MimeBodyPart();
//					attachPart.attachFile( (File) (attachList.get(i)) );

                    attachPart.setDataHandler(new DataHandler(new FileDataSource(targetFile)));

                    /* Before skmail library, originally created filename encoder.
                     attachPart.setFileName(
                     MimeUtility.encodeText(targetFile.getName(), 
                     mfInfo.getSendingBodyCode(dlocale), fNameEnc));	
					 */

					skmail_MailUtility.setFileName(attachPart, targetFile.getName(),
                            mfInfo.getSendingBodyCode(dlocale), dlocale.getCountry());

                    bodyPart.addBodyPart(attachPart);
                }
                msg.setContent(bodyPart);
            }

        } catch (Exception e) {
            Logging.followingMessageIsImportant();
			Logging.writeErrorMessage( 328, e, e.getMessage() );
//            Logging.writeMessage("%% OME Error 328 %% File Attachment error:" + e.getMessage());
//            e.printStackTrace();
            throw e;
        }

        try {
            if (OMEPreferences.getInstance().getForce7bit()) msg.setHeader("Content-Transfer-Encoding", "7bit");
            msg.saveChanges();
			Address[] destinations = msg.getAllRecipients();
			tr.sendMessage(msg, destinations);
			//            Transport.send(msg);
			Logging.writeMessage("%% OME Message %% Mail Send OK to: " + toStr);
			if ( ! ccStr.equals("") )
				Logging.writeMessage("%% OME Message %%   and Carbon Copy to: " + ccStr);
			if ( ! bccStr.equals("") )
				Logging.writeMessage("%% OME Message %%   and Blind Carbon Copy to: " + bccStr);

			thisSession.getProperties().remove("mail.from");

		} catch ( Exception ex )		{
			Exception originalEx = getOriginalException( ex );
			if ( ex instanceof javax.mail.SendFailedException )	{
				if ( originalEx instanceof com.sun.mail.smtp.SMTPAddressFailedException )	{
					/*	送信先アドレスが正しくないとき
						javax.mail.SendFailedException(Invalid Address)
						com.sun.mail.smtp.SMTPAddressFailedException(550...)
					*/
					String[] messageParams = {originalEx.getLocalizedMessage()};
					Logging.writeErrorMessage( 327, (Exception)ex, messageParams );
					throw new Exception( "" );
				}
				else
					throw ex;
			}
			else if ( ex instanceof java.lang.NullPointerException )	{
				/*	getAllRecipientsで送信先が得られないとき（他のエラーの可能性もあるだろうけど）
				*/
				String[] messageParams = {originalEx.getLocalizedMessage()};
				Logging.writeErrorMessage( 328, (Exception)ex, messageParams );
				throw new Exception( "" );
			}
			else
				throw ex;
		}
		
		if ( (new File( archivingTempPath )).exists()	)	{
			String[] com = { "rm", "-rf", archivingTempPath };
			new CommandExecuter(com).doCommandWithLogging(false);
		}		
		return returnVal;
    }

    /**	
     @param 
     @return 
     */
    private void addressMIME(InternetAddress addr[], Locale dlocale) {
        MailFormatInfo mfInfo = MailFormatInfo.getInstance();
        for (int i = 0; i < addr.length; i++) {
            String aName = addr[i].getPersonal();
            if (aName != null) {
                try {
                    addr[i].setPersonal(MimeUtility.encodeText(aName, mfInfo.getSendingHeaderCode(dlocale), mfInfo
                            .getSendingHeaderEncode(dlocale)));
                } catch (Exception e) {
                    Logging.writeMessage("%% OME Error 304 %% Error at Address Encoding: " + aName);
                }
            }
        }
    }

    /**	SMTP認証のためのクラス
     @param 
     @return 
     */
    private class MyAuthenticator extends javax.mail.Authenticator {

        String account, password;

        public MyAuthenticator(String accStr, String passStr) {
            account = accStr;
            password = passStr;
        }

        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
            return new javax.mail.PasswordAuthentication(account, password);
        }
    }

//    public boolean isNoLineDeviding;

//    private boolean isComplateExpand;

//    private int startPoint = 0;

    /**	送信メールメッセージの中にあるHTMLの記述を、テキストに展開する、実際の展開処理は
     OME.textformatter.*にあるタグ名と同じ名前のクラスで行う。（HTMLExpanderクラスの導入によりこのメソッドは廃止します）
     @param source HTMLを含むメッセージ
     @return HTML展開した結果
     */
    private String expandHTML(String source) {
		System.out.println( "OME_SendMailクラスのexpandHTMLメソッドは機能しません。");
		return null;
/*
        String retVal;
        int tagTop = source.indexOf("<", startPoint);
        int tagEnd = source.indexOf(">", tagTop);
        if ((tagTop >= 0) && (tagEnd >= 0)) {
            startPoint = tagEnd + 1;
            StringBuffer thisLine = new StringBuffer(source.substring(0, tagTop));
            StringTokenizer params = new StringTokenizer(source.substring(tagTop + 1, tagEnd));
            int cTokens = params.countTokens();
            if (cTokens > 0) { //タグ内に何もなければ、何もしない
                String tagOriginal = params.nextToken();
                String tag = tagOriginal.toLowerCase();
                Properties paramProps = new Properties();
                while (params.hasMoreTokens()) {
                    String itemParam = params.nextToken();
                    int eqPos = itemParam.indexOf("=");
                    if (eqPos < 0)
                        paramProps.setProperty(itemParam.trim(), "");
                    else
                        paramProps.setProperty(itemParam.substring(0, eqPos).trim().toLowerCase(), itemParam.substring(
                                eqPos + 1).trim());
                }

                tagEnd++;
                int termTagPos = source.indexOf("</" + tagOriginal + ">", tagEnd);
                String item = "";
                if (termTagPos >= 0) {
                    isComplateExpand = false;
                    item = source.substring(tagEnd, termTagPos);
                    tagEnd = termTagPos + tagOriginal.length() + 3;
                }
                try {
                    TextFormatable tagProc = (TextFormatable) Class.forName("OME.textformatter." + tag).newInstance();
                    String expandTag = tagProc.formating(item, paramProps, this);
                    thisLine.append(expandTag);
                } catch (Exception e) {
                    thisLine.append(item);
                }
            }
            startPoint = thisLine.length();
            thisLine.append(source.substring(tagEnd));
            retVal = thisLine.toString();
        } else
            retVal = source;
        if (retVal.length() == 0) retVal = "";
        return retVal;
*/
    }
	
	private Exception getOriginalException( Exception ex )	{
		Exception currentException = ex;
		Exception previousException = ex;
		while ( currentException != null )	{
			previousException = currentException;
			if ( currentException instanceof javax.mail.MessagingException )	
				currentException =  ((javax.mail.MessagingException)currentException).getNextException();
			else
				currentException = null;
		}
		return previousException;
	}

}
