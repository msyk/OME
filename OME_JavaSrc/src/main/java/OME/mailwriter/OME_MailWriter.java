package OME.mailwriter;

//
//  MailWriter.java
//
import java.io.*;
import java.util.*;

import OME.*;
import OME.mailformatinfo.*;

/**
 * 送信メールファイルを作成するクラスおよびアプリケーション。
 *  送信メールを作成します。送信メールの元テキストを作り、エディタで開くことなどを行います。

 <hr>
 <h2>OME履歴情報</h2>
 <pre>
 作成者:新居雅行（Masayuki Nii/msyk@msyk.net）

 :
 2003/10/16:新居:キーワードInsertFromToNewMailに対応
 2004/2/8:高階:返信もとファイルを含む.mpartフォルダの修正日をもとにもどすようにした
 2004/9/27:かねうち:Preferencesヘッダが付かないバグを修正
 2005/10/10:新居:生成する送信ファイルの文字セットはcharacterset.xmlから取得するようにした
 2009/6/28:新居:OME_JavaCore2へ移動
 </pre>
 */
public class OME_MailWriter extends Object {

    /**	アプリケーションのエントリーポイント。
     *	コマンド引数-なし：OutBoxに新規に送信メールファイルを作成し、設定されているMail_Readerのエディタで開きます。
     *   第1引数「STDIN」：
     *   第1引数「FILE」：
     *   第1引数「ADDRESS」：
     *   第1引数「TEMPLATE」：
     *	@param args コマンド呼び出し引数
     */
    public static void main(String args[]) {
        if (args.length > 0) {
            if (args[0].compareToIgnoreCase("STDIN") == 0)	{
                (new OME_MailWriter()).makeUploadFile(System.in, null);
            }
            else if (args[0].compareToIgnoreCase("FILE") == 0)	{
            	String fPath = args[1].replaceAll("%20", " ").replaceAll("%25", "%");
                (new OME_MailWriter()).makeUploadFile(new File(fPath));
            }
            else if (args[0].compareToIgnoreCase("ADDRESS") == 0)
                (new OME_MailWriter()).makeUploadFile(args[1]);
            else if (args[0].compareToIgnoreCase("TEMPLATE") == 0)
                    (new OME_MailWriter()).makeUploadFileByTemplate(new File(args[1]));
            else if (args[0].compareToIgnoreCase("OPEN") == 0)
                    (new OME_MailWriter()).makeUploadFileByTemplate(new File(args[1]));
        } else
            (new OME_MailWriter()).makeUploadFile((String) null);
    }

    /*
     public OME_MailWriter() {
     new Application();
     }
     
     public void makeUploadFile(String toAddressLine)	{
     makeUploadFile(toAddressLine);
     }
     */
    /**	送信メールファイルを作成（makeUploadFile(String)を呼び出す）
     @param sourceMail 返信もとメールファイル
     */
    public void makeUploadFile(File sourceMail) {
        makeUploadFile(sourceMail, null);
    }

    /**	送信メールファイルを作成（単に作成する）
     */
    public void makeUploadFile() {
        makeUploadFile((String) null);
    }

    /**	単にファイルを開くが、
     @param sourceMail テンプレートファイル
     */
    public void openFile(File sourceFile) {
        Logging.setupLogger(loggerNameSpace);
	}
	
    /**	送信メールファイルを作成テンプレートファイルから作成
     @param sourceMail テンプレートファイル
     */
    public void makeUploadFileByTemplate(File sourceFile) {
        Logging.setupLogger(loggerNameSpace);

        String nextLine = System.getProperty("line.separator");
        OMEPreferences omePref = OMEPreferences.getInstance();
        MailFormatInfo mfInfo = MailFormatInfo.getInstance();

        byte[] templateBuffer = new byte[10000];
        File messageFile = omePref.newOutFile();

        try {
            LineInputStream inSt = new LineInputStream(sourceFile);
            OutputStream outFile = new FileOutputStream(messageFile);

            int lineLength;
            boolean isHeader = true;
            String contentsTemplate = null;

            while ((lineLength = inSt.readLineNoNL(templateBuffer)) != -1) {
                try {
                    contentsTemplate = new String(templateBuffer, 0, lineLength, mfInfo.getTemplateFileCode());
                } catch (Exception e) {
                    Logging.writeMessage("%% OME Error 272 %% Error in decode template file: " + e.getMessage());
                    throw e;
                }
                if (isHeader & (lineLength == 0)) { //ヘッダの最後の行の次にきたら
                    if (omePref.isIncludeCC()) {
                        SenderInfo sInfo = SenderInfo.getInstance();
                        outFile.write(("Cc: " + sInfo.getSenderName() + " <" + sInfo.getSenderAddress() + ">")
                                .getBytes(mfInfo.getUploadFileCode()));
                    }
                    if (omePref.isIncludeBCC()) {
                        SenderInfo sInfo = SenderInfo.getInstance();
                        outFile.write(("Bcc: " + sInfo.getSenderName() + " <" + sInfo.getSenderAddress() + ">")
                                .getBytes(mfInfo.getUploadFileCode()));
                    }
                }
                if (!isHeader & omePref.isHTMLExpanding())
                    outFile.write(htmlReady(contentsTemplate).getBytes(mfInfo.getUploadFileCode()));
                else
                    outFile.write(contentsTemplate.getBytes(mfInfo.getUploadFileCode()));

                outFile.write(nextLine.getBytes());
                if (lineLength == 0) {
					if ( isHeader )
						outFile.write(nextLine.getBytes());
					isHeader = false;
				}
            }
            inSt.close();

            if (!omePref.isHTMLExpanding())
                outFile.write(omePref.getSign().getBytes(mfInfo.getUploadFileCode()));
            else
                outFile.write(htmlReady(omePref.getSign()).getBytes(mfInfo.getUploadFileCode()));

            outFile.close();

        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 271 %% Error in processing template file: " + e.getMessage());
        }

    //    new MacFile(messageFile).setFileTypeAndCreator("TEXT", null);

        omePref.openByEditor(messageFile);
        System.exit(0);
    }

    private byte originalMail[] = null;

    private boolean isStandardInput = false;

    private String loggerNameSpace = "net.msyk.ome.mailwriter";
	
    private String[][] prefFileNames = {
		{ "Sender_Info.txt", "Signature.txt", "ReplyComment.txt", "HeaderAddendum.txt" },
		{ ".Sender_Info.txt", ".Signature.txt", ".ReplyComment.txt", ".HeaderAddendum.txt" },
	};

    /**	送信メールファイルを作成（makeUploadFile(String)を呼び出す）
     @param sourceMail 返信もとメールファイル（同一フォルダに設定ファイルがあれば、その設定を一時的に反映させる）
     @param toAddressLine 送信アドレス（Toに自動的に設定）
     */
    private void makeUploadFile(File sourceMail, String toAddressLine) {

        Logging.setupLogger(loggerNameSpace);
		
		File sourceDir = sourceMail.getParentFile();	//メールソースが含まれているディレクトリ
		if ( sourceDir.getName().endsWith( ".mpart" ) ) //マルチパートメールなら
			sourceDir = sourceDir.getParentFile();		//さらにその上位ディレクトリ
		File[] alternatePrefFiles = new File[ (prefFileNames[0]).length ];
		Arrays.fill( alternatePrefFiles, null );
		File tempFile;
		for ( int i = 0 ; i < prefFileNames.length ;  i++ ) {
			for ( int j = 0 ; j < prefFileNames[i].length ;  j++ ) {
				if ( ( tempFile = new File( sourceDir, prefFileNames[i][j] )).exists() )
					alternatePrefFiles[j] = tempFile;
			}
		}
		for ( int i = 0 ; i < alternatePrefFiles.length ;  i++ ) {
			if ( alternatePrefFiles[i] != null )	{
				Logging.writeMessage(
					"%% OME_MailWriter %% Recognize the preference file in the source folder: " 
					+ alternatePrefFiles[i].getName() );
			}
		}
		
        originalMail = new byte[(int) sourceMail.length()];
        try {
            InputStream inSt = new FileInputStream(sourceMail);
            inSt.read(originalMail);
            makeUploadFile(toAddressLine);
            inSt.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logging.writeMessage("%% OME Error 263 %% Error in read the source file: " + e.getMessage());
        }

        originalMail = null;

        try {
            String sourceMailName = sourceMail.getName();
            int lastPeriod = sourceMailName.lastIndexOf(".");
            File sourceMailNewName = new File(sourceMail.getParent(), sourceMailName.substring(0, lastPeriod) + ".rply");
            long mailTime = sourceMail.lastModified(); // 元メールの修正時間
            sourceMail.renameTo(sourceMailNewName);
            // 親が mpart だったら修正時間を元に戻す (高階追加)
            if (sourceMail.getParent().endsWith(".mpart")) {
                Logging.writeMessage("%% OME Set LastModified");
                new File(sourceMail.getParent()).setLastModified(mailTime);
            }
            // ここまで
        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 262 %% Error in setting file type: " + e.getMessage());
        }
    }

    /**	送信メールファイルを作成（makeUploadFile(String)を呼び出す）
     @param inSt ストリーム入力。ここから返信もとメールの中身を取り出す
     @param toAddressLine 送信アドレス（Toに自動的に設定）
     */
    private void makeUploadFile(InputStream inSt, String toAddressLine) {

        isStandardInput = true;

        byte buffer[] = new byte[10000];
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        int cnt;
        try {
            while ((cnt = inSt.read(buffer)) >= 0)
                byteOut.write(buffer, 0, cnt);
            originalMail = byteOut.toByteArray();
            makeUploadFile(toAddressLine);
            inSt.close();
            byteOut.close();
        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 264 %% Error in setting file type: " + e.getMessage());
        }

        originalMail = null;
    }

    /**	送信メールファイルを作成（これがメインとなるメソッド）
     メンバ変数のoriginalMailに返信もとメールファイルを指定すれば、そのメールへの返信を行う
     @param toAddressLine 送信アドレス（Toに自動的に設定）
     */
    public void makeUploadFile(String toAddressLine) {
        String aLine = null;
        boolean isInRepTo = false;
        String nextLine = System.getProperty("line.separator");
        byte newLineBytes[] = nextLine.getBytes();
        OMEPreferences omePref = OMEPreferences.getInstance();
        MailFormatInfo mfInfo = MailFormatInfo.getInstance();
        Locale omeLocale = omePref.getOMELocale();

        String toStr = "";
        if (toAddressLine != null) toStr = toAddressLine;

        String ccStr = "";
        String bccStr = "";
        String inRepToStr = "";
        String refStr = "";
        String subjStr = "";
        String originalSubject = "";
        String originalFrom = "";
        String dateStr = "";
        String origToStr = "";
        StringBuffer message = new StringBuffer("");
        StringBuffer relaHeader = new StringBuffer("");
        String mailCharSet = null;
        String originalLocale = null;

        String charSet = null;
        Locale thisMailLocale = null;
        String encoding = mfInfo.getUploadFileCode(omeLocale);

        //	System.out.println("###"+encoding);

        if (originalMail != null) { //返信する元のメールファイルが指定されている場合

            //元メールのキャラクタセットを求める。元メールに残されているヘッダから、Content-Typeを探し、そこにあるcharsetを探す
            try {
                int partHeaderStart = -1, mailHeaderStart = -1, searchStart = -1;
                partHeaderStart = byteArrayMatch(originalMail, "<!-- Real Part Headers -->".getBytes(), 0);
                if (partHeaderStart < 0) {
                    mailHeaderStart = byteArrayMatch(originalMail, "<!-- Real Mail Headers -->".getBytes(), 0);
                    searchStart = mailHeaderStart;
                } else {
                    searchStart = partHeaderStart;
                }

                int omeCSPos = byteArrayMatch(originalMail, "X-OME-CharSet: ".getBytes(), searchStart);
                if (omeCSPos >= 0) {
                    int CRPos = byteArrayMatch(originalMail, "\r".getBytes(), omeCSPos);
                    int LFPos = byteArrayMatch(originalMail, "\n".getBytes(), omeCSPos);
                    int lineEnd = -1;
                    if ((CRPos == -1) && (LFPos == -1))
                        lineEnd = originalMail.length;
                    else if (CRPos == -1)
                        lineEnd = LFPos;
                    else if (LFPos == -1)
                        lineEnd = CRPos;
                    else
                        lineEnd = Math.min(LFPos, CRPos);
                    mailCharSet = new String(originalMail, omeCSPos + 15, lineEnd - (omeCSPos + 15));

                    int omeLocPos = byteArrayMatch(originalMail, "X-OME-Locale: ".getBytes(), searchStart);
                    int ulPos = byteArrayMatch(originalMail, "_".getBytes(), omeLocPos);
                    CRPos = byteArrayMatch(originalMail, "\r".getBytes(), omeLocPos);
                    LFPos = byteArrayMatch(originalMail, "\n".getBytes(), omeLocPos);
                    lineEnd = -1;
                    if ((CRPos == -1) && (LFPos == -1))
                        lineEnd = originalMail.length;
                    else if (CRPos == -1)
                        lineEnd = LFPos;
                    else if (LFPos == -1)
                        lineEnd = CRPos;
                    else
                        lineEnd = Math.min(LFPos, CRPos);
                    String language = new String(originalMail, omeLocPos + 14, ulPos - (omeLocPos + 14));
                    String loc = new String(originalMail, ulPos + 1, lineEnd - (ulPos + 1));
                    omeLocale = new Locale(language, loc);
                } else {
                    int setStart = 0, setEnd = 0;
                    int cTypePos = byteArrayMatch(originalMail, "Content-Type: ".getBytes(), searchStart);
                    if (cTypePos >= 0) {
                        int csetPos = byteArrayMatch(originalMail, "charset=".getBytes(), cTypePos);
                        setStart = csetPos + 8;
                        if (csetPos >= 0) {
                            if (originalMail[setStart] == '"') {
                                setStart = setStart + 1;
                                setEnd = setStart;
                                while (originalMail[setEnd] != '"')
                                    setEnd++;
                            } else {
                                while (originalMail[setEnd] != ';' || originalMail[setEnd] != ' '
                                        || originalMail[setEnd] != '\r' || originalMail[setEnd] != '\n'
                                        || originalMail[setEnd] != ',') {
                                    setEnd++;
                                    if (setEnd >= originalMail.length) break;
                                }
                            }
                        }
                    }
                    charSet = new String(originalMail, setStart, setEnd-setStart).toUpperCase().trim();
//                    omeLocale = CharsetToLocale.getInstance().getSuitableLocale(charSet);
                    omeLocale = MailFormatInfo.getInstance().getMailSourceCharset(charSet);
                    mailCharSet = mfInfo.getMailFileCode(omeLocale);
                }
                Logging.writeMessage("Original Message's Locale:" + omeLocale.toString());
            } catch (Exception e) {
                Logging.writeMessage("%% OME Error 257 %% Error in search charset in read original: " + e.getMessage());
            }

            byte buffer[] = new byte[5000];
            try {
                LineInputStream inSt = new LineInputStream(originalMail);

                subjStr += omePref.getSubjectPrefix();
                boolean isEOF = false;
                boolean isContents = false;
                boolean isRealHeader = false;
                String inputEncode = mailCharSet;
                if (isStandardInput) inputEncode = "UTF8";
                int aLineLen;
                while (isEOF == false) {
                    aLineLen = inSt.readLineNoNL(buffer);
                    if (aLineLen < 0) break;
                    aLine = new String(buffer, 0, aLineLen, inputEncode);
                    if (isContents) {
                        if ((aLine.indexOf("<!-- Real Mail Headers -->") == 0)
                                || (aLine.indexOf("<!-- Real Part Headers -->") == 0)) {
                            isRealHeader = true;
                            isContents = false;
                        } else
                            message.append("> " + aLine + nextLine);
                    } else if (isRealHeader) {
                        if (aLine.startsWith(" ") || aLine.startsWith("\t")) {
                            relaHeader.append(" ");
                            relaHeader.append(aLine.trim());
                        } else
                            relaHeader.append(nextLine + aLine);
                    } else {
                        if (aLine.equals(""))
                            isContents = true;
                        else if (aLine.startsWith("From: ")) {
                            toStr = aLine.substring(6);
                            originalFrom = new String(toStr);
                        } else if (aLine.startsWith("To: ")) {
                            origToStr = aLine.substring(4);
                            if (omePref.isIncludeToCC()) {
                                if (!ccStr.equals("")) ccStr += ", ";
                                ccStr += aLine.substring(4);
                            }
                        } else if (aLine.startsWith("Subject: ")) {
                            originalSubject = aLine.substring(9);

                            String formedSubject = originalSubject;
                            int cleanStartPoint = formedSubject.indexOf('[');
                            if (omePref.isCleanBr() && (cleanStartPoint >= 0)) {
                                int endPoint = formedSubject.indexOf(']', cleanStartPoint);
                                if (endPoint > cleanStartPoint)
                                        formedSubject = new String(formedSubject.substring(0, cleanStartPoint).trim()
                                                + formedSubject.substring(endPoint + 1).trim());
                            }
                            cleanStartPoint = 0;
                            if (omePref.isCleanRe())
                                    while ((cleanStartPoint + 3 <= formedSubject.length())
                                            && (formedSubject.substring(cleanStartPoint, cleanStartPoint + 2)
                                                    .toUpperCase().equals("RE"))
                                            && (isKigou(formedSubject.charAt(cleanStartPoint + 2)))) {
                                        cleanStartPoint += 2;
                                        while ((cleanStartPoint < formedSubject.length())
                                                && (isKigou(formedSubject.charAt(cleanStartPoint))))
                                            cleanStartPoint++;
                                    }
                            subjStr += formedSubject.substring(cleanStartPoint).trim();
                        } else if (aLine.startsWith("Date: ")) dateStr = aLine.substring(6);
                    }
                }
                inSt.close();

                String rhStr = relaHeader.toString();
                String rhStrNoCase = relaHeader.toString().toLowerCase();

                int topPoint, endPoint;
                if ((topPoint = rhStrNoCase.indexOf("cc: ")) >= 0) {
                    endPoint = rhStr.indexOf(nextLine, topPoint);
                    if (endPoint < 0) endPoint = rhStr.length();
                    if (!ccStr.equals("")) ccStr += ", ";
                    ccStr += rhStr.substring(topPoint + 4, endPoint);
                }
                if ((topPoint = rhStrNoCase.indexOf("references: ")) >= 0) {
                    endPoint = rhStr.indexOf(nextLine, topPoint);
                    if (endPoint < 0) endPoint = rhStr.length();
                    refStr = rhStr.substring(topPoint + 12, endPoint);
                }
                if ((topPoint = rhStrNoCase.indexOf("message-id: ")) >= 0) {
                    endPoint = rhStr.indexOf(nextLine, topPoint);
                    if (endPoint < 0) endPoint = rhStr.length();
                    inRepToStr = rhStr.substring(topPoint + 12, endPoint);
                    if (!refStr.equals("")) {
                        refStr += " " + inRepToStr;
                    } else {
                        refStr += inRepToStr;
                    }
                }
                topPoint = -1;
                while ((topPoint = rhStrNoCase.indexOf("reply-to: ", topPoint + 1)) >= 0) {
                    if (!rhStr.substring(topPoint - 3, topPoint).equalsIgnoreCase("In-")) {
                        endPoint = rhStr.indexOf(nextLine, topPoint);
                        if (endPoint < 0) endPoint = rhStr.length();
                        toStr = rhStr.substring(topPoint + 10, endPoint);
                        isInRepTo = true;
                    }
                }
                message.insert(0, omePref.getCommentLine(originalSubject, originalFrom, dateStr, inRepToStr, origToStr,
                        omeLocale)
                        + nextLine);

            } catch (Exception e) {
                Logging.writeMessage("%% OME Error 253 %% Error in read original: " + e.getMessage());
                e.printStackTrace();
            }

        }

        SenderInfo sInfo = SenderInfo.getInstance();

        if (omePref.isIncludeCC() && !isInRepTo) if (ccStr.indexOf(sInfo.getSenderAddress()) < 0) {
            if (!ccStr.equals("")) ccStr += ",";
            ccStr += sInfo.getSenderName() + " <" + sInfo.getSenderAddress() + ">";
        }
        if (omePref.isIncludeBCC() && !isInRepTo) if (bccStr.indexOf(sInfo.getSenderAddress()) < 0) {
            if (!bccStr.equals("")) bccStr += ",";
            bccStr += sInfo.getSenderName() + " <" + sInfo.getSenderAddress() + ">";
        }
        File messageFile = null;

        if (omePref.isRemoveMeAll()) {
            String[] allAddress = sInfo.getSenderAddressAll();
            for (int ix = 0; ix < allAddress.length; ix++) {
                toStr = removeAddress(toStr, allAddress[ix]);
                ccStr = removeAddress(ccStr, allAddress[ix]);
                bccStr = removeAddress(bccStr, allAddress[ix]);
                Logging.writeMessage("OME # Remove the address from To/CC/BCC: " + allAddress[ix]);
            }
        } else if (omePref.isRemoveMe()) {
            toStr = removeAddress(toStr, sInfo.getSenderAddress());
            ccStr = removeAddress(ccStr, sInfo.getSenderAddress());
            bccStr = removeAddress(bccStr, sInfo.getSenderAddress());
            Logging.writeMessage("OME # Remove the address from To/CC/BCC: " + sInfo.getSenderAddress());
        }

        try {
            messageFile = omePref.newOutFile();
            //if (mailCharSet != null) encoding = mailCharSet;
			//送信メールファイルのエンコードは、元ファイルと同じではなく、charset.xmlに合わせる
			encoding = MailFormatInfo.getInstance().getUploadFileCode();
			
            //	        System.out.println("######"+omeLocale+"####"+encoding);
            OutputStream outFile = new FileOutputStream(messageFile);
            outFile.write(omePref.getAddHeaders().getBytes(encoding));
            if (!refStr.equals("")) {
                outFile.write(("References: ").getBytes(encoding));
                outFile.write(refStr.getBytes(encoding));
                outFile.write(nextLine.getBytes());
            }
            if (!inRepToStr.equals("")) {
                outFile.write("In-Reply-To: ".getBytes(encoding));
                outFile.write(inRepToStr.getBytes(encoding));
                outFile.write(nextLine.getBytes());
            }
            outFile.write("To: ".getBytes(encoding));
            outFile.write(toStr.getBytes(encoding));
            outFile.write(nextLine.getBytes());
            if (!ccStr.equals("")) {
                outFile.write("Cc: ".getBytes(encoding));
                outFile.write(ccStr.getBytes(encoding));
                outFile.write(nextLine.getBytes());
            }
            if (!bccStr.equals("")) {
                outFile.write("Bcc: ".getBytes(encoding));
                outFile.write(bccStr.getBytes(encoding));
                outFile.write(nextLine.getBytes());
            }
            outFile.write("Subject: ".getBytes(encoding));
            outFile.write(subjStr.getBytes(encoding));
            outFile.write(nextLine.getBytes());

            if (omePref.isInsertFromToNewMail()) {
                outFile.write("From: ".getBytes(encoding));
                outFile.write(sInfo.getSenderName().getBytes(encoding));
                outFile.write(" <".getBytes(encoding));
                outFile.write(sInfo.getSenderAddress().getBytes(encoding));
                outFile.write(">".getBytes(encoding));
                outFile.write(nextLine.getBytes());
            }

            outFile.write(nextLine.getBytes());

            if (!omePref.isHTMLExpanding()) {
                outFile.write(omePref.getTopMessage().getBytes(encoding));
                outFile.write(message.toString().getBytes(encoding));
                outFile.write(nextLine.getBytes());
                outFile.write(omePref.getSign().getBytes(encoding));
            } else {
                outFile.write(htmlReady(omePref.getTopMessage()).getBytes(encoding));
                outFile.write(htmlReady(message.toString()).getBytes(encoding));
                outFile.write(nextLine.getBytes());
                outFile.write(htmlReady(omePref.getSign()).getBytes(encoding));
            }
            outFile.close();
        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 250 %% Error in writing to file: " + e.getMessage());
        }

        //ファイルタイプとクリエイターの設設定
    //    (new OME.MacFile(messageFile)).setFileTypeAndCreator("TEXT", null);

        omePref.openByEditor(messageFile);
    }

    /**	HTMLメールを作成する場合、元テキストをHTML化してもそのままになるように変換。
     つまり、＆と＜、＞（もちろん半角）を&形式に変換している。
     @param s 元の文字列
     @return 変換した文字列
     */
    private String htmlReady(String s) {
        MyString ms = new MyString(s);
        ms = ms.substituteString("&", "&amp;");
        ms = ms.substituteString("<", "&lt;");
        ms = ms.substituteString(">", "&gt;");
        return ms.toString();
    }

    /**	半角で数字とアルファベット以外の文字（つまり記号）かどうかをチェック
     @param c チェックする文字
     @return 記号ならtrue、そうでなければfalse（全角文字はすべてfalseになる）
     */
    private boolean isKigou(char c) {
        boolean retVal = true;
        if ((c >= '0') && (c <= '9'))
            retVal = false;
        else if ((c >= 'A') && (c <= 'Z'))
            retVal = false;
        else if ((c >= 'a') && (c <= 'z'))
            retVal = false;
        else if (c >= '\u0080') retVal = false;
        if (c == '[') retVal = true;
        return retVal;
    }

    /** 宛先のリストから、自分自身のアドレスを取り除く
     @param fieldStr 宛先のリスト
     @param address 取り除くアドレス
     @return 取り除いた結果の宛先のリスト
     */
    private String removeAddress(String fieldStr, String address) {
        int addressPos, ix, iy;
        StringBuffer resultStr = new StringBuffer(fieldStr);
        while ((addressPos = resultStr.indexOf(address)) > -1) {
            boolean isDeleteFromTop = true;
            for (ix = addressPos; ix > 0; ix--) {
                if (resultStr.charAt(ix) == ',') {
                    isDeleteFromTop = false;
                    break;
                }
            }
            for (iy = addressPos + address.length(); iy < resultStr.length(); iy++) {
                if (resultStr.charAt(iy) == ',') break;
            }
            if (isDeleteFromTop && (iy < resultStr.length())) iy++;
            resultStr.delete(ix, iy);
        }
        return resultStr.toString().trim();
    }

    /**	半角の数字かどうかをチェック
     @param c チェックする文字
     @return 数字ならtrue、そうでなければfalse（全角の数字はfalseになる）
     */
    private boolean isNumber(char c) {
        boolean retVal = false;
        if ((c >= '0') && (c <= '9')) retVal = true;
        return retVal;
    }

    /**	バイト配列内から、バイトデータを検索する
     @param s 検索対象となるバイト配列
     @param x 検索するデータが含まれるバイト配列
     @param start パラメータsの何バイト目から検索するか
     @return 最初から何バイト目に検索データが含まれているか。存在しない場合には-1を戻す
     */
    private int byteArrayMatch(byte[] s, byte[] x, int start) {
        int returnValue = -1;
        boolean isMatch = false;
        int i, j, k;
        for (i = start; i < (s.length - x.length); i++) {
            isMatch = false;
            for (j = 0; j < x.length; j++) {
                byte sChar = s[i + j];
                if (sChar >= 'a' && sChar <= 'z') sChar -= 32;
                byte xChar = x[j];
                if (xChar >= 'a' && xChar <= 'z') xChar -= 32;
                if (sChar == xChar)
                    isMatch = true;
                else {
                    isMatch = false;
                    break;
                }
            }
            if (isMatch) {
                returnValue = i;
                break;
            }
        }
        return returnValue;
    }
}
