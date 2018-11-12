package OME.messagemaker;

import java.io.*;
import java.text.ParsePosition;
import java.text.DateFormat;
import java.util.*;

import javax.mail.MessagingException;
import javax.mail.internet.*;
import javax.mail.Part;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import OME.*;
import OME.mailformatinfo.*;
//import OME.messagemaker.StandardSave.OnePartInfo;
//import OME.messagemaker.StandardSave.OnePartInfo;

/**
 * OMEでの標準的なメールの処理を行うクラス（PartProcessorの継承クラス）。
 * <p>
 * もともとは、OME.messagemaker.PartExpanderとしてつくられていたクラスを汎用化（PartProcessorベース）にした。
 * <p>
 * 
 * 要求するプロパティ（expandAllPartsメソッドで利用）
 * <table border=2>
 * <tr>
 * <td>キー</td>
 * <td>値</td>
 * <td>説明</td>
 * </tr>
 * <tr>
 * <td>X-OME-BackupPath</td>
 * <td>Stringオブジェクト</td>
 * <td>バックアップしているファイルへの絶対パス。このクラスで、メールのX-OME-BackupPathヘッダを追加</td>
 * </tr>
 * </table>
 * 
 * 残すプロパティ（expandAllPartsメソッドで設定）
 * <table border=2>
 * <tr>
 * <td>キー</td>
 * <td>値</td>
 * <td>説明</td>
 * </tr>
 * <tr>
 * <td>FiringFile</td>
 * <td>Fileオブジェクト</td>
 * <td>起動プロセスの引数</td>
 * <td>
 * <tr>
 * <td>RootOfMessage</td>
 * <td>Fileオブジェクト</td>
 * <td></td>
 * </tr>
 * </table>
 * 
 * <hr>
 * <h2>OME履歴情報</h2>
 * 
 * <pre>
 * 
 * 
 *  2003/11/27:新居:開発スタート、とりあえず、PartExpanderのコードをコピペ
 *  2004/1/14:新居:その後にPartExpanderを改訂したので、改めてコードをコピペ
 *  2005/9/19:新居:添付ファイルだけのメールに、添付ファイルにヘッダを付け加えていたバグを直した。
 *                ついでに、いろいろ修正。添付ファイルのヘッダやファイルリストのXMLファイルを作る。
 *  2007/5/13:新居:getValueおよびgetCharSetメソッドを廃止。ContentTypeクラスを使う方がより確実なため。
 * 			しかしながら、ContentTypeにエンコード文字があるとパースエラーを出すなど問題があるため、
 * 			結局自分でパースすることに（MyContentTypeクラス）
 * 			JavaMail 1.4でまともになっていることを期待するしかないか
 *  2007/5/13:新居:charset=CP932の場合にエラーが出てしまっていたのを対処
 *  2007/5/13:新居:AppleDoubleの処理がうまくいかず、リソースをマージしなかったのを対処
 *  2007/12/8:ヘッダのないパートはなにも処理をしなくした
 * 			マルチパートファイルのパート情報ファイルに、Content-Dispositionの情報を入れるようにした
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * 
 * </pre>
 * 
 * @author Masayuki Nii（新居雅行） msyk@msyk.net（originally PartExpander by 新居、かねうち）
 *         $Revision: 1.24 $
 */

public class StandardSave extends PartProcessor {

	private File savingFolder;

	private String sender = null;

	private String subject = null;

	private String toAddr = null;

	private String sendDate = null;

	// private String headers = null;
	private MyContentType contentType = null;

	private String mainFileName = null;

	// private String mainFileNameSeed = null;

	private File firingFile = null;

	private File RootItem = null;

	private String nextLine = System.getProperty("line.separator");

	// private String doubleQuote = "\"";

	private String topLevelCharset = null;

	private boolean isMadeMailMessage = false; // 件名や差出人情報、およびヘッダ情報を書き出しを行ったかどうか

	private int partCounter = 0;

	private Hashtable<String, String> idTable = new Hashtable<String, String>();

	private boolean isHTMLMaking = false;

	private File targetHTMLFile = null;

	private int depth = 0;

	private boolean isAppleDouble = false; // AppleDoubleの処理中かどうか

	private Vector<String> appleDoubleFiles = null;

	private Locale thisMailLocale;

	private TreeSet<OnePartInfo> partsInfoSet = null; // マルチパート情報を保存するオブジェクト

	private MimePart topLeveMessage;

	private boolean isAlternative = false;

	private TextConverter txConv = null;

	/*
	 * JavaMail 1.4を使うようになったらかな public StandardSave () { super();
	 * 
	 * System.setProperty("mail.mime.decodeparameters", "true");
	 * System.setProperty("mail.mime.encodeparameters", "true"); }
	 */
	/**
	 * 最初に呼び出される処理メソッドで、複数のオブジェクトで処理するときには順番に処理される。
	 * 
	 * @throw MessageException 処理が失敗したときの例外
	 */
	public void preProcess() throws Exception {
		Logging.writeMessage("StandardSave::preProcess");
		try {
			pickupFundamentalInformations();
		} catch (Exception e) {
			// throw new MessageException(e);
			throw e;
		}
	}

	/**
	 * メール処理の中心的なメソッドで、複数オブジェクトで処理するときには並列処理される。
	 * このメソッドをオーバライドしたクラスは、スレッドベースで動くことを考慮して作成される必要がある。
	 * 
	 * @throw MessageException 処理が失敗したときの例外
	 */
	public void processMessage() throws Exception {

		Logging.writeMessage("StandardSave::processMessage");

		try {
			// pickupFundamentalInformations();

			partsInfoSet = new TreeSet<OnePartInfo>(new LazyComparator()); // マルチパート情報を保存するオブジェクト

			topLeveMessage = (MimeBodyPart) getMailSource();
			try {
				expandOnePart(topLeveMessage); // 再帰的にパートの展開を行う
			} catch (Exception e) {
				throw e;
			}
			setProperty("FiringFile", firingFile);
			setProperty("RootOfMessage", RootItem);

			if (RootItem == null)
				RootItem = firingFile;
			if (isHTMLMaking)
				processingHTMLFile();

			if (contentType.getPrimaryType().equalsIgnoreCase("multipart")) {
				if (partsInfoSet != null && (partsInfoSet.size() > 0)) {
					Document partsInfoSetDom = null;
					try {
						partsInfoSetDom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
						Element rootElement = partsInfoSetDom.createElement("mail-message-info");
						rootElement.setAttribute("version", "2");
						// version 2 has content-disposition attribute.
						// no vertion attribute means prior to 2, i.e. first
						// verstion.
						Node rootNode = partsInfoSetDom.appendChild(rootElement);

						int counter = 1;
						for (Iterator<OnePartInfo> i = partsInfoSet.iterator(); i.hasNext();) {
							OnePartInfo targetPart = (i.next());

							Element onePartNode = partsInfoSetDom.createElement("part-info");
							onePartNode.setAttribute("serial", new Integer(counter++).toString());
							if (targetPart.option != null) {
								onePartNode.setAttribute("alternative", "yes");
							}
							rootNode.appendChild(onePartNode);

							Element currentElement = partsInfoSetDom.createElement("file-name");
							currentElement.appendChild(partsInfoSetDom.createTextNode(targetPart.mailFile));
							onePartNode.appendChild(currentElement);
							currentElement = partsInfoSetDom.createElement("characterset");
							currentElement.appendChild(partsInfoSetDom.createTextNode(targetPart.usedCharacterSet));
							onePartNode.appendChild(currentElement);
							currentElement = partsInfoSetDom.createElement("content-type");
							currentElement
									.appendChild(partsInfoSetDom.createTextNode(targetPart.contentType.toString()));
							onePartNode.appendChild(currentElement);
							if ((targetPart.disposition != null) && (targetPart.disposition.length() > 0)) {
								currentElement = partsInfoSetDom.createElement("content-disposition");
								currentElement.appendChild(partsInfoSetDom.createTextNode(targetPart.disposition));
								onePartNode.appendChild(currentElement);
							}
						}
					} catch (ParserConfigurationException pcEx) {
						pcEx.printStackTrace();
					}
					try {
						Transformer tf = TransformerFactory.newInstance().newTransformer();
						File outputFile = new File(savingFolder, "__OME_PartsInfo.xml");
						tf.transform(new DOMSource(partsInfoSetDom), new StreamResult(outputFile));
						Logging.writeMessage("Wrote the file __OME_PartsInfo.xml.", 2);
					} catch (javax.xml.transform.TransformerConfigurationException tcEx) {
						Logging.writeErrorMessage(109, tcEx, "Error in writing the __OME_PartsInfo.xml.");
						throw new MessageException();
					} catch (javax.xml.transform.TransformerException tEx) {
						Logging.writeErrorMessage(108, tEx, "Error in writing the __OME_PartsInfo.xml.");
						throw new MessageException();
					}
				}
				try {
					if (!OMEPreferences.getInstance().isNoSetFAttr()) {
						savingFolder.setLastModified(new MailDateFormat().parse(sendDate, new ParsePosition(0))
								.getTime());
					}
				} catch (Exception e) {
					Logging.writeErrorMessage(11, e, "Can't modify file date.");
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 最後に呼び出される処理メソッドで、複数のオブジェクトで処理するときには順番に処理される。
	 * 
	 * @throw MessageException 処理が失敗したときの例外
	 */
	public void afterProcess() throws Exception {

	}

	/**
	 * メッセージから基本的な情報を取り出すもので、最初に呼び出される。メール自体のヘッダからタイトルを抜き出すなどを行う。
	 */
	private void pickupFundamentalInformations() {

		MimeBodyPart msgBody = (MimeBodyPart) getMailSource();
		savingFolder = (File) parentMM.getProperty("StoringFolder");
		Logging.writeMessage("[savingFolder]: " + savingFolder);
		// savingFolder = new File("/Users/msyk/Open_Mail_Environment");

		// Initializing the text converter class for Language-specific
		// processingå
		try {
			txConv = (TextConverter) Class.forName(MailFormatInfo.getInstance().getMailConverter(Locale.getDefault()))
					.newInstance();
		} catch (Exception e) {
			try {
				txConv = (TextConverter) Class.forName("OME.mailformatinfo.TextConverter_Default").newInstance();
			} catch (Exception e2) {
			}
		}

		try {
			sender = msgBody.getHeader("From", "\n\r");
			if ((txConv != null) && OMEPreferences.getInstance().isHeaderTextUnifying()) {
				sender = txConv.headerTextUnifier(sender);
			}
			sender = convertOneLine(sender);
			// GrowlNotify.appendMessage(sender);
		} catch (Exception e) {
			Logging.writeErrorMessage(4, e, "Nothing sender information. ");
			sender = new String("-送信者名なし-");
		}

		try {
			subject = msgBody.getHeader("Subject", "\n\r");
			if ((txConv != null) && OMEPreferences.getInstance().isHeaderTextUnifying()) {
				subject = txConv.headerTextUnifier(subject);
			}
			subject = convertOneLine(subject);
		} catch (Exception e) {
			Logging.writeErrorMessage(5, e, "Nothing subject information.");
			subject = new String("-件名なし-");
		}

		try {
			toAddr = msgBody.getHeader("To", "\n\r");
			if ((txConv != null) && OMEPreferences.getInstance().isHeaderTextUnifying()) {
				toAddr = txConv.headerTextUnifier(toAddr);
			}
			toAddr = convertOneLine(toAddr);
		} catch (Exception e) {
			Logging.writeErrorMessage(6, e, "Nothing To infomation.");
			toAddr = new String("-宛先なし-");
		}

		try {
			sendDate = msgBody.getHeader("Date", "\n\r");
		} catch (Exception e) {
			Logging.writeErrorMessage(7, e, "Nothing Date Information.");
		}

		Logging.writeMessage("From: " + sender);
		Logging.writeMessage("To: " + toAddr);
		Logging.writeMessage("Subject: " + subject);
		Logging.writeMessage("Date: " + sendDate);

		try {
			contentType = new MyContentType(msgBody.getContentType());
		} catch (Exception e) {
			Logging.writeErrorMessage(8, e, "Nothing Content-Type.");
		}

		// UNIX向けのファイルだから、スラッシュを変換する
		mainFileName = parseMailInfoString(OMEPreferences.getInstance().getFileNamePattern());
		if (txConv != null) {
			mainFileName = txConv.FileNameUnifier(mainFileName);
		}

		if ((contentType != null) && (contentType.getPrimaryType().equalsIgnoreCase("multipart"))) { // マルチパートなら
			try {
				while ((new File(savingFolder, mainFileName + ".mpart")).exists())
					mainFileName += "*";
				savingFolder = new File(savingFolder, mainFileName + ".mpart");
				savingFolder.mkdir(); // 保存するフォルダを作成する
				RootItem = savingFolder;
			} catch (Exception e) {
				Logging.writeErrorMessage(10, e, "Can't make folder for multipart mail by normal way.");

				String altFileName = parseMailInfoString("N");
				while ((new File(savingFolder, altFileName + ".mpart")).exists())
					altFileName += "*";
				savingFolder = new File(savingFolder, altFileName + ".mpart");
				savingFolder.mkdir(); // 保存するフォルダを作成する
				RootItem = savingFolder;
			}
		}
		while ((new File(savingFolder, mainFileName + ".ygm")).exists())
			mainFileName += "*";
		mainFileName += ".ygm";
	}

	/**
	 * パートの中身を展開する
	 * 
	 * @param msg
	 *            パートのデータ
	 * @throws java.lang.Exception
	 *             エラーが発生したとき
	 */
	private void expandOnePart(MimePart msg) throws Exception {

		if (!msg.getAllHeaders().hasMoreElements())
			return;
		// パートのヘッダがまったくないというのはなにか間違っている。つまりメールのソースじゃないものを読み込んでいるので、無視する

		depth++;

		MyContentType contentType = null; // パートのタイプ
		try {
			contentType = new MyContentType(msg.getContentType());

			/*
			 * ContentType cType = new ContentType( contentType ); String
			 * contentTypeBase = cType.getBaseType(); //eg. "text/plain" String
			 * contentTypePrimary = cType.getPrimaryType(); //eg. "text" String
			 * contentTypeSub = cType.getSubType(); //eg. "plain" String
			 * contentCharacterSet = cType.getParameter( "charset" ); String a =
			 * "";
			 */
		} catch (Exception ec) {
			Logging.writeErrorMessage(12, ec, "Error in part extracting: Can't get content type.");
			throw ec;
		}

		String contentID = null;
		try {
			contentID = msg.getContentID();
		} catch (Exception ec) {
			Logging.writeMessage("Error in part extracting: Can't get content id.", 2);
		}

		String disposition = null;
		try {
			disposition = msg.getDisposition();
		} catch (Exception ec) {
			String header[] = msg.getHeader("Content-Disposition");
			int semicolonPos = header[0].indexOf(';');
			if (semicolonPos < 0)
				semicolonPos = header[0].length();
			String tempDisposition = header[0].substring(0, semicolonPos);
			if (tempDisposition.equalsIgnoreCase(Part.ATTACHMENT))
				disposition = Part.ATTACHMENT;
			else if (tempDisposition.equalsIgnoreCase(Part.INLINE))
				disposition = Part.INLINE;
			else
				Logging.writeMessage("Error in part extracting: Can't get content disposition.", 2);
		}

		String partFileName = null; // パートのファイル名
		try { // ファイル名は、ContentDispositionヘッダにあるものを優先する
			String partFileNameContentType = contentType.getParameter("name");
			String partFileNameContentDisposition = skmail_MailUtility.getFileName(msg);
			if (partFileNameContentDisposition != null)
				partFileName = partFileNameContentDisposition;
			else if (partFileNameContentType != null)
				partFileName = partFileNameContentType;
		} catch (Exception ec) {
			Logging.writeMessage("Error in part extracting: Can't get file name.", 2);
		}

		// メールの中身を取り出す
		Object content = null; // このパートの中身
		try {
			content = msg.getContent();
		} catch (Exception ec) {
			String message = ec.getMessage();
			String className = ec.getClass().getName();
			if (className.equalsIgnoreCase("java.io.UnsupportedEncodingException")) {
				if (message.equalsIgnoreCase("CP932")) {
					// キャラクタセットがCP932ならこのエラーが出てしまうので、別途内容を取得。
					// ここでは、先のプロセスにつなげるため、contentがStringであればよい
					content = "";
				} else if (message.equalsIgnoreCase("iso2022-jp")) {
					// Mailman対応、なんちゅーか、こんなことなんでせないかんのかと思うが…
					content = "";
				}
			} else {
				content = "";
				Logging.writeErrorMessage(71, ec, "Error in get content of message.");
				// throw ec;
			}
		} // JavaMail 1.3.1現在、message/rfc822でも例外は発生しないようになっている

		// Charsetの情報を取り出す
		String charSet = contentType.getParameter("charset");
		if (charSet != null) {
			// charSet = charSet.toUpperCase().trim();
			topLevelCharset = charSet;
		} else {
			charSet = topLevelCharset; // ない場合（通常は、パートの一部）は、上位パートの情報をそのまま使う
		}
		Logging.writeMessage("---> Decided Charset: " + charSet);

		// Charsetに応じたロケールを設定する
		thisMailLocale = MailFormatInfo.getInstance().getMailSourceCharset(charSet);

		// バックアップファイルのパスを取得して、それをヘッダに残す
		String backupPath = (String) parentMM.getProperty("X-OME-BackupPath");
		if (backupPath != null && backupPath.length() > 0)
			msg.setHeader("X-OME-BackupPath", backupPath);

		// 以下はファイル名をきめるプロセス
		if ((partFileName != null) && (txConv != null))	{
			partFileName = txConv.FileNameUnifier(partFileName);
		}
		String fileName = determineFilePath(partFileName, contentType.toString());
		Logging.writeMessage("---> Decided File: " + fileName);

		makeContentIdTable(contentID, fileName); // HTMLファイルの場合のcontent-idテーブルの保持

		if (content instanceof MimeMultipart) { // 中身がマルチパートの場合
			processMultipart(msg, fileName);
		} else if (content instanceof String) { // 中身が文字列の場合
			processStringContent(msg, charSet, fileName);
		} else if (content instanceof MimeMessage) { // 中身がmessage/rfc822
			processAttachedMessage(content, contentType, disposition, fileName);
		} else if (content instanceof InputStream) {// 中身がストリームの場合
			if (depth == 1) { // シングルパートのHTMLファイルで、ここに相当する場合がある模様
				processStringContent(msg, charSet, fileName);
			} else { // マルチパートの添付ファイルの場合
				File attachedFile = processInputStreamContent(content, contentType, disposition, fileName, msg);
				if (contentType.getBaseType().equalsIgnoreCase("application/mac-binhex40")) {// BinHexで来た場合展開する
					OpenFile.byStuffItExpander(attachedFile); // TigerにはStuffItはもうないよ…どうする？
				}
			}
		}
		depth--;
	}

	/**
	 * @param contentID
	 * @param fileName
	 */
	private void makeContentIdTable(String contentID, String fileName) {
		// Content-IDのテーブル設定（素材を添付したメールの場合の処理）
		if (contentID != null && contentID.length() > 0) {
			int startPos = 0;
			int lastPos = contentID.length();
			if (contentID.charAt(0) == '<')
				startPos = 1;
			if (contentID.charAt(lastPos - 1) == '>')
				lastPos--;
			idTable.put(contentID.substring(startPos, lastPos), fileName);
		}
	}

	/**
	 * @param partFileName
	 * @param contentType
	 * @return
	 */
	private String determineFilePath(String partFileName, String contentType) {
		String fileName = partFileName;
		if (partFileName == null) {
			if (!isMadeMailMessage) {
				fileName = mainFileName;
			} else {
				fileName = new String("Part" + partCounter + ".txt");
				partCounter++;
			}
		}

		if (contentType != null && contentType.indexOf("text/html") >= 0) {
			if (fileName.endsWith(".ygm"))
				fileName = fileName.substring(0, fileName.length() - 4);
			if (!fileName.endsWith(".html"))
				fileName = fileName + ".html";
			isHTMLMaking = true;
			targetHTMLFile = new File(savingFolder, fileName);
		}
		if (isAppleDouble) {
			if (contentType.indexOf("application/applefile") > -1)
				fileName = fileName + ".rsrc";
			appleDoubleFiles.add(fileName);
		}
		return fileName;
	}

	/**
	 * ファイル名のエンコードを解除する・・・本来は不要だと思うが念のため2003/5/18
	 * 
	 * @param partFileName
	 * @return
	 */
	/*
	 * private String decodePartFileName(String partFileName) { String returnStr
	 * = null; if (partFileName.indexOf("=?") >= 0) //デコードがかかっていたら returnStr =
	 * myDecodeString(partFileName); else try { //日本語のコードが何か分からないという想定で変換してみる
	 * //日本語でない場合でASCIIコードの場合、まずいと考えられる returnStr = new
	 * String(partFileName.getBytes(), "JISAutoDetect"); } catch (Exception ex)
	 * { Logging.writeErrorMessage(13, ex, "Can't resolve file name anymore.");
	 * returnStr = partFileName; } partFileName = substituteString(returnStr,
	 * "/", "／"); return partFileName; }
	 */
	/**
	 * マルチパートメールの処理
	 * 
	 * @param message
	 *            処理するパート
	 * @param fileName
	 * @throws MessagingException
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	private void processMultipart(MimePart message, String fileName) throws MessagingException, Exception,
			UnsupportedEncodingException, FileNotFoundException {

		Logging.writeMessage("---> Processing MimeMultipart");

		if (message.isMimeType("multipart/appledouble")) { // AppleDoubleならフラグをたてる
			isAppleDouble = true;
			appleDoubleFiles = new Vector<String>();
		}

		if (message.isMimeType("multipart/alternative")) { // Alternative
															// Partならフラグをたてる
			isAlternative = true;
		}

		MimeMultipart mPart = null; // マルチパートの中身を取り出す
		int partCount = -1; // パートの個数を数える

		try {
			mPart = new MimeMultipart(message.getDataHandler().getDataSource());
			partCount = mPart.getCount();
		} catch (Exception e) {
			Logging.writeErrorMessage(114, e, "Error in MimeMultipart generating.");
			throw e;
		}
		for (int i = 0; i < partCount; i++) {
			MimeBodyPart thisPart = null;
			try {
				thisPart = (MimeBodyPart) mPart.getBodyPart(i); // このパートについて
			} catch (Exception e) {
				Logging.writeErrorMessage(14, e, "Error in getBodyPart.");
				throw e;
			}
			expandOnePart(thisPart); // 処理を行う
		}

		if (isAppleDouble) {
			processAppleDouble();
		}
		isAppleDouble = false;
		isAlternative = false;

		// マルチパートのメールで、メッセージに該当するパートがない場合、ヘッダなどをファイルに保存しておく
		if (!isMadeMailMessage && depth == 1) {
			String mailFileCode = MailFormatInfo.getInstance().getMailFileCode(thisMailLocale);
			File messageFile = new File(savingFolder, fileName);
			PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					messageFile), mailFileCode)));
			writer.println("From: " + sender);
			writer.println("To: " + toAddr);
			writer.println("Subject: " + subject);
			writer.println("Date: " + sendDate);

			// メッセージファイルの冒頭のヘッダ部分に追加がある場合の書き込み処理
			List addHeaders = OMEPreferences.getInstance().getAdditionalHeaders();
			if (addHeaders != null) {
				ListIterator iterator = addHeaders.listIterator();
				while (iterator.hasNext()) {
					String headerField = (String) iterator.next();
					String headerString[] = topLeveMessage.getHeader(headerField);
					if (headerString != null) {
						for (int ix = 0; ix < headerString.length; ix++) {
							if ((txConv != null) && OMEPreferences.getInstance().isHeaderTextUnifying())
								writer.println(headerField + ": "
										+ txConv.headerTextUnifier(convertOneLine(headerString[ix])));
							else
								writer.println(headerField + ": " + convertOneLine(headerString[ix]));
						}
					}
				}
			}

			writer.println();
			writer.println("<!-- Real Mail Headers -->");
			/*
			 * writer.print(headers); writer.println("X-OME-CharSet:
			 * "+mailFileCode); writer.println("X-OME-Locale:
			 * "+thisMailLocale.toString());
			 */
			try {
				Enumeration hd = ((MimeBodyPart) getMailSource()).getAllHeaderLines();
				while (hd.hasMoreElements()) {
					String aHeaderStr = hd.nextElement().toString();
					if ((aHeaderStr != null) && (aHeaderStr.length() != 0))
						writer.print(aHeaderStr + nextLine);
				}
			} catch (Exception e) {
				Logging.writeErrorMessage(9, e, "Error in headers extracting.");
			}

			isMadeMailMessage = true;
			firingFile = messageFile; // これを開かざるを得ないかと
			writer.close();
		}
	}

	/**
	 * @param content
	 * @param contentType
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws MessagingException
	 */
	private void processAttachedMessage(Object content, MyContentType contentType, String disposition, String fileName)
			throws FileNotFoundException, IOException, MessagingException {
		// マルチパートの一部として message/rfc822 が添付されて
		// いると content が MimeMessage になる。
		MimeMessage mimeMessage = (MimeMessage) content;

		String filePath = fileName;

		// パートファイル情報を設定する
		if (partsInfoSet != null)
			partsInfoSet.add(new OnePartInfo(filePath, contentType, MailFormatInfo.getInstance().getMailFileCode(
					thisMailLocale), disposition, isAlternative ? "Alternative" : null));

		Logging.writeMessage("---> Processing MimeMessage: " + contentType + " ->" + filePath);

		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(new File(savingFolder, filePath)));
			mimeMessage.writeTo(out);
			out.close();

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	/**
	 * @param content
	 * @param contentType
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private File processInputStreamContent(Object content, MyContentType contentType, String disposition,
			String fileName, MimePart message) throws Exception {

		Logging.writeMessage("---> Processing InputStream: " + contentType + " ->" + fileName);

		// 共通の処理
		File attachedFile = new File(savingFolder, fileName);

		// パートファイル情報を設定する

		if (partsInfoSet != null)
			if (!(isAppleDouble && contentType.getBaseType().equalsIgnoreCase("application/applefile")))
				partsInfoSet.add(new OnePartInfo(fileName, contentType, MailFormatInfo.getInstance().getMailFileCode(
						thisMailLocale), disposition, isAlternative ? "Alternative" : null));
		try {
			// firingFile = attachedFile;
			FileOutputStream outSt = new FileOutputStream(attachedFile);
			byte[] buffer = new byte[10000];
			int bLen;
			do {
				bLen = ((InputStream) content).read(buffer, 0, 10000);
				if (bLen >= 0)
					outSt.write(buffer, 0, bLen);
			} while (bLen >= 0);
			outSt.close();

			// String creatorSign = hex2String(contentType.getParameter(
			// "x-mac-creator" ));
			// if (creatorSign != null) {
			// new MacFile(attachedFile).setFileTypeAndCreator(null,
			// creatorSign);
			// }
			// String fileType = hex2String(contentType.getParameter(
			// "x-mac-type" ));
			// if (fileType != null) {
			// new MacFile(attachedFile).setFileTypeAndCreator(fileType, null);
			// }
			String permissionCode = contentType.getParameter("x-unix-mode");
			if (permissionCode != null) {
				String[] com = { "chmod", permissionCode, attachedFile.getPath() };
				new CommandExecuter(com).doCommandWithLogging(false);
			}

			makePartInfoFile(message, fileName);

		} catch (Exception ec) {
			Logging.writeErrorMessage(3, ec, "Stream part error.");
			ec.printStackTrace();
			// throw ec;
		}
		return attachedFile;
	}

	/**
	 * 
	 * @param message
	 * @param fileName
	 * @throws Exception
	 */
	private void makePartInfoFile(MimePart message, String fileName) throws Exception {
		try {
			PrintWriter partInfoWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(savingFolder, "__Headers of " + fileName + ".txt")), MailFormatInfo
							.getInstance().getMailFileCode(thisMailLocale))));
			for (Enumeration<String> e = message.getAllHeaderLines(); e.hasMoreElements();) {
				partInfoWriter.println(e.nextElement());
			}
			partInfoWriter.close();

		} catch (Exception e) {
			Logging.writeErrorMessage(44, e, "Error in writing to file.");
			throw e;
		}
	}

	/**
	 * @param message
	 * @param charset
	 *            メールメッセージ側で指定されているキャラクタセット
	 * @param fileName
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	private void processStringContent(MimePart message, String charset, String fileName) throws Exception,
			UnsupportedEncodingException {

		// メールの中身を取り出す
		Object content = null; // このパートの中身
		try {
			content = message.getContent();
			if (content instanceof java.io.InputStream) { // シングルパートのHTMLメールでまれにInputStreamと認識される事がある
				int contentSize = message.getSize();
				byte[] buffer = new byte[contentSize];
				InputStream contentStream = ((MimeBodyPart) message).getInputStream();
				contentStream.read(buffer);
				contentStream.close();
				content = new String(buffer); // ほんとうはエンコードが必要な気がするのだけど…
			}
		} catch (Exception ec) {
			String exceptionMsg = ec.getMessage();
			String className = ec.getClass().getName();
			if (className.equalsIgnoreCase("java.io.UnsupportedEncodingException")) {
				if (exceptionMsg.equalsIgnoreCase("CP932")) {
					// このエラーが出た場合は、なまデータをストリームで読み出す
					// charset=CP932の場合に例外が出て処理ができなくなるのを回避するため
					// なぜか、この場合、ストリームに00が最後にくっついてきてそれが悪さをするので、00が出てきたらそこまでをエンコードするようにした。
					// http://www.ingrid.org/java/i18n/encoding/shift_jis.html
					int contentSize = message.getSize();
					byte[] buffer = new byte[contentSize];
					InputStream contentStream = ((MimeBodyPart) message).getInputStream();
					contentStream.read(buffer);
					contentStream.close();
					int endOfStream = 0;
					for (endOfStream = 0; endOfStream < contentSize; endOfStream++)
						if (buffer[endOfStream] == 0)
							break;

					content = new String(buffer, 0, endOfStream, "MS932");
				} else if (exceptionMsg.equalsIgnoreCase("iso2022-jp")) {
					// Mailman対策、このエラーが出た場合は、なまデータをストリームで読み出す
					int contentSize = message.getSize();
					byte[] buffer = new byte[contentSize];
					InputStream contentStream = ((MimeBodyPart) message).getInputStream();
					contentStream.read(buffer);
					contentStream.close();
					int endOfStream = 0;
					for (endOfStream = 0; endOfStream < contentSize; endOfStream++)
						if (buffer[endOfStream] == 0)
							break;

					content = new String(buffer, 0, endOfStream, "ISO-2022-JP");
				}
			} else {
				// Logging.writeErrorMessage(71, ec,
				// "Error in get content of message.");
				// throw ec;

				content = "Contents of MIME message can't retrieved for an error.";

			}
		} // JavaMail 1.3.1現在、message/rfc822でも例外は発生しないようになっている

		String disposition = "";
		try {
			disposition = message.getDisposition();
		} catch (Exception ec) {
			Logging.writeMessage("Error in part extracting: Can't get disposition.", 2);
		}

		String contentType = null; // パートのタイプ
		try {
			contentType = message.getContentType();
		} catch (Exception ec) {
			Logging.writeErrorMessage(12, ec, "Error in part extracting: Can't get content type.");
			throw ec;
		}
		String encoding = null;
		try {
			encoding = message.getEncoding();
		} catch (Exception ec) {
			Logging.writeMessage("Error in part extracting: Can't get encoding.", 2);
		}

		int contentLength = 0;
		try {
			contentLength = ((String) content).length();
		} catch (Exception ec) { /* Ignore error */
		}

		if (contentLength == 0)
			Logging.writeMessage("---> Processing " + content.getClass().getName() + "/" + disposition + "/"
					+ contentType + "(Size isn't available)");
		else {
			Logging.writeMessage("---> Processing String/" + disposition + "/" + contentType + "("
					+ String.valueOf(contentLength) + ")");
			if (contentLength < 1)
				return;
			// 中身が何もないパートはなにもしない
		}

		boolean isAttachment = false;
		if (disposition != null)
			isAttachment = (disposition.toLowerCase().indexOf("attachment") >= 0);

		if ((disposition != null) && isAttachment) {
			// 添付されたテキストファイルはヘッダからエンコード情報がわからないので、そのまま保存をする
			Logging.writeMessage("---> Extract part data just original one. /" + encoding + " -->" + fileName);
			try {
				InputStream originalStream = null;
				try { // まずは、そのまま取り出そうと試みる。
					originalStream = message.getInputStream();
				} catch (MessagingException ex) { // だめな場合
					// MimeBodyPartなら getRawInputStream() で再試行。
					if (!(message instanceof MimeBodyPart))
						throw ex;
					if (encoding == null || "".equals(encoding))
						encoding = "8bit";
					originalStream = MimeUtility.decode(((MimeBodyPart) message).getRawInputStream(), encoding);
				}

				// パートファイル情報を設定する
				if (partsInfoSet != null)
					partsInfoSet.add(new OnePartInfo(fileName, new MyContentType(contentType), MailFormatInfo
							.getInstance().getMailFileCode(thisMailLocale), disposition, isAlternative ? "Alternative"
							: null));

				FileOutputStream outSt = new FileOutputStream(new File(savingFolder, fileName));
				byte[] buffer = new byte[10000];
				int bLen;
				do {
					bLen = originalStream.read(buffer, 0, 10000);
					if (bLen != -1)
						outSt.write(buffer, 0, bLen);
				} while (bLen >= 0);
				outSt.close();
			} catch (Exception ec) {
				Logging.writeErrorMessage(401, ec, "extract myself error.");
				ec.printStackTrace();
				throw ec;
			}
			// パート情報をテキストファイルに書き出す
			makePartInfoFile(message, fileName);
		} else {
			/*
			 * TextConverter txConv = null; try { txConv = (TextConverter)
			 * Class.
			 * forName(MailFormatInfo.getInstance().getMailConverter(thisMailLocale
			 * )).newInstance(); } catch (Exception e) { }
			 */
			OME.StringTokenizerX stx;

			// 単純なテキストなのにキャラクタセットがない場合には、内容について、強制的に変換を行う
			if (contentType.toLowerCase().indexOf("text/plain") >= 0 && charset == null) {
				content = (Object) (new String(((String) content).getBytes(), MailFormatInfo.getInstance()
						.getMailSourceDefaultCode(thisMailLocale)));
			}
			// ヘッダで使う文字列関連の処理を行う
			/*
			 * if ((txConv != null) && (
			 * OMEPreferences.getInstance().isHeaderTextUnifying() )) { subject
			 * = txConv.headerTextUnifier(subject); toAddr =
			 * txConv.headerTextUnifier(toAddr); sender =
			 * txConv.headerTextUnifier(sender); }
			 */
			String mailFileCode = MailFormatInfo.getInstance().getMailFileCode(thisMailLocale);
			String codingOnSource = mailFileCode;
			boolean isHTMLorXML = (contentType.indexOf("text/html") > -1) || (contentType.indexOf("text/xml") > -1);
			if (isHTMLorXML) { // HTMLメールやXMLメールの場合、ソースからCharsetを探す
				// mailFileCode = findCharset(content, mailFileCode);
			}
			if (mailFileCode.equalsIgnoreCase("utf_8")) {
				mailFileCode = "UTF-8";
			} else if (mailFileCode.equalsIgnoreCase("utf-8")) {
				mailFileCode = "UTF-8";
			} else if (mailFileCode.equalsIgnoreCase("latin1_swedish_ci")) {
				mailFileCode = "ISO-8859-1";
			} else if (mailFileCode.equalsIgnoreCase("iso-8859-a")) {
				mailFileCode = "ISO8859_1";
			} else if (mailFileCode.equalsIgnoreCase("utf-1")) {
				mailFileCode = "UTF-8";
			} else if (mailFileCode.equalsIgnoreCase("iso2022-jp")) {
				mailFileCode = "ISO-2022-JP";
			} else if (mailFileCode.equalsIgnoreCase("iso=2022-jp")) {
				mailFileCode = "ISO-2022-JP";
			} else if (mailFileCode.indexOf("1252") > 0) {
				mailFileCode = "Cp1252";
			} else if (mailFileCode.startsWith("3d") || mailFileCode.startsWith("3D")) {
				mailFileCode = mailFileCode.substring(2);
			} else if (mailFileCode.equalsIgnoreCase("cp932")) {
				mailFileCode = "MS932";
			}

			message.setHeader("X-OME-CharSet", mailFileCode);
			message.setHeader("X-OME-Locale", thisMailLocale.toString());

			// パートファイル情報を設定する
			if (partsInfoSet != null)
				partsInfoSet.add(new OnePartInfo(fileName, new MyContentType(contentType), mailFileCode, disposition,
						isAlternative ? "Alternative" : null
				/* MailFormatInfo.getInstance().getMailFileCode(thisMailLocale) */));

			File messageFile = new File(savingFolder, fileName);
			Logging.writeMessage("Try to use the FileOutputStream with encoding: '" + mailFileCode + "', originally '"
					+ codingOnSource + "'");
			Logging.writeMessage("savingFolder: '" + savingFolder);
			Logging.writeMessage("Target File: '" + messageFile.toString());
			try {

				// 指定の文字コードで出力する PrintWriter を作成す
				// る。改行に println() を用いることで、プラット
				// フォームに合った改行コードとなる。
				PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
						messageFile), mailFileCode)));
				if (!isMadeMailMessage && !isAttachment) {
					if (isHTMLorXML) {
						writer.println("<!--");
					}
					writer.println("From: " + sender);
					writer.println("To: " + toAddr);
					writer.println("Subject: " + subject);
					writer.println("Date: " + sendDate);

					// メッセージファイルの冒頭のヘッダ部分に追加がある場合の書き込み処理
					List<String> addHeaders = OMEPreferences.getInstance().getAdditionalHeaders();
					if (addHeaders != null) {
						ListIterator<String> iterator = addHeaders.listIterator();
						while (iterator.hasNext()) {
							String headerField = (String) iterator.next();
							String headerString[] = topLeveMessage.getHeader(headerField);
							if (headerString != null) {
								for (int ix = 0; ix < headerString.length; ix++) {
									if ((txConv != null) && OMEPreferences.getInstance().isHeaderTextUnifying())
										writer.println(headerField + ": "
												+ txConv.headerTextUnifier(convertOneLine(headerString[ix])));
									else
										writer.println(headerField + ": " + convertOneLine(headerString[ix]));
								}
							}
						}
					}

					writer.println();
					if (isHTMLorXML) {
						writer.println("-->");
					}
				}
				stx = new OME.StringTokenizerX(content.toString(), "\r\n");
				while (stx.hasMoreTokens()) {
					if (txConv == null) {
						writer.println(stx.nextToken());
					} else {
						writer.println(txConv.convert(stx.nextToken()));
					}
				}

				// マルチパートの場合、パートのヘッダ情報を残す
				if (depth > 1) {
					if (!isAttachment) {
						writer.println();
						writer.println("<!-- Real Part Headers -->");
						if (isHTMLorXML) {
							writer.println("<!--");
						}
						for (Enumeration<String> e = message.getAllHeaderLines(); e.hasMoreElements();) {
							writer.println(e.nextElement());
						}
						if (isHTMLorXML) {
							writer.println("-->");
						}
					} else
						makePartInfoFile(message, fileName);
				}

				// メールのヘッダをファイルに残す
				if (!isMadeMailMessage && !isAttachment) {
					writer.println();
					writer.println("<!-- Real Mail Headers -->");
					if (isHTMLorXML) {
						writer.println("<!--");
					}

					/*
					 * writer.print(headers); writer.println("X-OME-CharSet:
					 * "+mailFileCode); writer.println("X-OME-Locale:
					 * "+thisMailLocale.toString());
					 */
					try {
						Enumeration hd = ((MimeBodyPart) getMailSource()).getAllHeaderLines();
						while (hd.hasMoreElements()) {
							String aHeaderStr = hd.nextElement().toString();
							if ((aHeaderStr != null) && (aHeaderStr.length() != 0))
								writer.print(aHeaderStr + nextLine);
						}
					} catch (Exception e) {
						Logging.writeErrorMessage(9, e, "Error in headers extracting.");
					}

					if (isHTMLorXML) {
						writer.println("-->");
					}

					isMadeMailMessage = true;
					firingFile = messageFile;
				}
				writer.close();
				if (writer.checkError()) {
					throw new IOException("Error during writing file " + messageFile);
				}
			} catch (Exception e) {
				Logging.writeErrorMessage(15, e, "Error in writing to file.");
				throw e;
			}

			if (!OMEPreferences.getInstance().isNoSetFAttr()) { // Finder属性を設定する場合
				// new MacFile(messageFile).setFileTypeAndCreator("TEXT",
				// "ome1");
				// ファイルタイプとクリエイタを設定

				long longTime = 0;
				if (sendDate != null) {
					try { // ファイルの修正日をメールのデータから取り出す
						longTime = new MailDateFormat().parse(sendDate, new ParsePosition(0)).getTime();
						messageFile.setLastModified(longTime);
						// 正しい日付書式なら、この方法でエラーは出ないが、"2007-3-2 10:04:01"のような頭の悪いメールがあったりする
						// 無理に解析しようとしたけど、標準機能ではできないようなので、パス！
					} catch (java.lang.NullPointerException pEx) {
						try {
							longTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).parse(sendDate)
									.getTime();
							messageFile.setLastModified(longTime);
						} catch (Exception e) {
							Logging.writeErrorMessage(21, e, "Error in setting file modified date: ");
							// throw e;
						}
					} catch (Exception e) {
						Logging.writeErrorMessage(21, e, "Error in setting file modified date: ");
						// throw e;
					}
				}
			}
			try { // コメントに設定を行う場合
				if ((OMEPreferences.getInstance().getCommentPattern() != null)
						&& (!OMEPreferences.getInstance().getCommentPattern().equals(""))) {
					// AppleScriptRunner.setPrintScript(true);
					AppleScriptRunner.setComment(messageFile, AppleScriptRunner
							.quateEscapedString(parseMailInfoString(OMEPreferences.getInstance().getCommentPattern())));
					String errMsg;
					if ((errMsg = AppleScriptRunner.getErrorMessage()) != null)
						Logging.writeErrorMessage(23, null, "Script Error in setting file to comment: ");
				}
			} catch (Exception e) {
				Logging.writeErrorMessage(22, e, "Error in setting file to comment: " + messageFile);
			}
		}
	}

	/**
	 * @param content
	 * @param mailFileCode
	 * @return
	 */
	private String findCharset(Object content, String mailFileCode) {
		String contentsSource = content.toString().toLowerCase();
		int index = 0;
		int pos1;
		while ((pos1 = contentsSource.indexOf("<meta", index)) >= 0) {
			int pos2 = contentsSource.indexOf(">", pos1);
			String currentMetaTag = contentsSource.substring(pos1, pos2);
			index = pos2;
			int pos3 = currentMetaTag.indexOf("charset");
			if (pos3 >= 0) {
				String afterCharset = currentMetaTag.substring(pos3 + 8);
				if (afterCharset.charAt(0) == '\"') {
					mailFileCode = afterCharset.substring(1, afterCharset.indexOf('\"', 1)).trim();
				} else if (afterCharset.charAt(0) == '\'') {
					mailFileCode = afterCharset.substring(1, afterCharset.indexOf('\'', 1)).trim();
				} else {
					int posArray[] = new int[3];
					posArray[0] = afterCharset.indexOf(' ');
					posArray[1] = afterCharset.indexOf('\"');
					posArray[2] = afterCharset.indexOf('\'');

					for (int x = 0; x < (posArray.length - 1); x++) {
						int minVal = posArray[x];
						int minPos = x;
						for (int y = x + 1; y < posArray.length; y++)
							if (posArray[y] < minVal) {
								minVal = posArray[y];
								minPos = y;
							}
						int temp = posArray[x];
						posArray[x] = posArray[minPos];
						posArray[minPos] = temp;
					}
					int minimumValue = -1;
					for (int x = 0; x < posArray.length; x++)
						if (posArray[x] > 0) {
							minimumValue = posArray[x];
							break;
						}

					if (minimumValue > 1)
						mailFileCode = afterCharset.substring(0, minimumValue).trim();
					else
						mailFileCode = afterCharset.trim();
				}
			}
		}
		return mailFileCode;
	}

	/**
	 * @param omePref
	 */
	private void processAppleDouble() {
		isAppleDouble = false;
		String targetFile = appleDoubleFiles.elementAt(0);
		String resFile = appleDoubleFiles.elementAt(1);
		if (targetFile.length() > resFile.length()) {
			resFile = appleDoubleFiles.elementAt(0);
			targetFile = appleDoubleFiles.elementAt(1);
		}
		File targetFileRef = new File(savingFolder.getPath() + "/" + targetFile);
		File resFileRef = new File(savingFolder.getPath() + "/" + resFile);
		boolean x = targetFileRef.exists();

		String[] cmdMerge = { OMEPreferences.getInstance().getOMEToolsFolder().getPath() + "/mergeres",
				targetFileRef.getPath(), resFileRef.getPath() };
		String outputString = (new CommandExecuter(cmdMerge)).doCommand();
		if ((outputString != null) && (outputString.length() > 3))
			Logging.writeMessage("Output of mergeres: " + outputString);
	}

	/**
	 * 文字列中の単語を置き換えた文字列を求める
	 * 
	 * @param source
	 *            元になる文字列
	 * @param match
	 *            引数sourceから検索する文字列
	 * @param subst
	 *            引数matchに指定した文字列と置き換える文字列
	 * @return 置き換えた結果の文字列
	 */
	/*
	 * private String substituteString(String source, String match, String
	 * subst) { int mLen = match.length(); StringBuffer b = new
	 * StringBuffer(""); for (int i = 0; i < source.length() - (mLen - 1); i++)
	 * { String flg = source.substring(i, i + mLen); if
	 * (flg.equalsIgnoreCase(match)) { b.append(subst); i += (mLen - 1); } else
	 * b.append(source.charAt(i)); // Logging.writeMessage(i+"$"+b); } return
	 * b.toString(); }
	 */
	/**
	 * byte配列内の検索を行い、見つかった位置を求める。大文字と小文字の区別はしない（processingHTMLFileメソッド内部で利用
	 * 
	 * @param s
	 *            検索対象のbyte配列
	 * @param x
	 *            調べるbyte配列（検索条件）
	 * @param start
	 *            引数sの配列の検査開始位置
	 * @return 検索して見つかった位置
	 */
	private int byteArrayMatch(byte[] s, byte[] x, int start) {
		int returnValue = -1;
		boolean isMatch = false;
		int i, j, k;
		for (i = start; i < (s.length - x.length); i++) {
			isMatch = false;
			for (j = 0; j < x.length; j++) {
				byte sChar = s[i + j];
				if (sChar >= 'a' && sChar <= 'z')
					sChar -= 32;
				byte xChar = x[j];
				if (xChar >= 'a' && xChar <= 'z')
					xChar -= 32;
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

	/**
	 * byte配列の一部と別のbyte配列が等しいかどうかを調べる。
	 * 大文字と小文字の区別はしない（processingHTMLFileメソッド内部で利用）
	 * <p>
	 * つまり、配列sのstart位置から、配列xの内容と同じかどうかを調べる
	 * 
	 * @param s
	 *            検査対象のbyte配列
	 * @param x
	 *            調べるbyte配列
	 * @param start
	 *            引数sの配列の検査開始位置
	 * @return 等しければtrue、そうでなければfalse
	 */
	private boolean byteArrayCompare(byte[] s, byte[] x, int start) {
		if (s.length <= start + x.length)
			return false;
		for (int j = 0; j < x.length; j++) {
			byte sChar = s[start + j];
			if (sChar >= 'a' && sChar <= 'z')
				sChar -= 32;
			byte xChar = x[j];
			if (xChar >= 'a' && xChar <= 'z')
				xChar -= 32;
			if (xChar != sChar)
				return false;
		}
		return true;
	}

	/**
	 * byte配列の検索開始位置以降、最初に出てくる空白ないしはタブの位置を求める（processingHTMLFileメソッド内部で利用）
	 * 
	 * @param s
	 *            検索対象のbyte配列
	 * @param start
	 *            検索開始位置
	 * @return 空白ないしはタブの位置
	 */
	private int byteArrayPassBlank(byte[] s, int start) {
		int i = start;
		while ((i < s.length) && (s[i] == ' ' || s[i] == '\t'))
			i++;
		return i;
	}

	/**
	 * HTMLファイルの後処理。おもに、別パートを参照している場合、そのIDをファイル名に置き換える処理をしている
	 */
	private void processingHTMLFile() {
		if (targetHTMLFile == null)
			return;

		byte buffer[];
		try {
			InputStream inSt = new FileInputStream(targetHTMLFile);
			buffer = new byte[(int) targetHTMLFile.length()];
			inSt.read(buffer);
			inSt.close();
		} catch (Exception e) {
			Logging.writeErrorMessage(16, e, "Error in html file reading.");
			return;
		}

		try {
			OutputStream outFile = new FileOutputStream(targetHTMLFile);
			// new File(targetHTMLFile.getParent(),
			// "mod-"+targetHTMLFile.getName()));

			int beforeWPos = 0;
			int posSrc = 0;
			while ((posSrc = byteArrayMatch(buffer, "SRC=".getBytes(), posSrc)) > 0) {
				int nextPos = byteArrayPassBlank(buffer, posSrc + 4);
				boolean isDQuoted = false;
				if (buffer[nextPos] == '\"') {
					isDQuoted = true;
					nextPos++;
				}
				int startPos = nextPos;
				if (byteArrayCompare(buffer, "cid:".getBytes(), nextPos)) {
					while ((buffer.length > nextPos)
							&& ((buffer[nextPos] != ' ') && (buffer[nextPos] != '\r') && (buffer[nextPos] != '\n') && (buffer[nextPos] != '\"')))
						nextPos++;
					String cIDstr = new String(buffer, startPos + 4, nextPos - startPos - 4);
					String idStr = idTable.get(cIDstr);

					outFile.write(buffer, beforeWPos, startPos - beforeWPos);
					if (idStr != null)
						outFile.write(idStr.getBytes());
					beforeWPos = nextPos;
				} else {
				}
				posSrc = nextPos;
			}
			outFile.write(buffer, beforeWPos, buffer.length - beforeWPos);
			outFile.close();
			if (!OMEPreferences.getInstance().isNoSetFAttr()) { // Finder属性を設定する場合
				// new MacFile(targetHTMLFile).setFileTypeAndCreator("TEXT",
				// "ome1");
				// ファイルタイプとクリエイタを設定

				long longTime = 0;
				if (sendDate != null) {
					try { // ファイルの修正日をメールのデータから取り出す
						longTime = new MailDateFormat().parse(sendDate, new ParsePosition(0)).getTime();
						targetHTMLFile.setLastModified(longTime);
						// 正しい日付書式なら、この方法でエラーは出ないが、"2007-3-2 10:04:01"のような頭の悪いメールがあったりする
						// 無理に解析しようとしたけど、標準機能ではできないようなので、パス！
					} catch (java.lang.NullPointerException pEx) {
						try {
							longTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).parse(sendDate)
									.getTime();
							targetHTMLFile.setLastModified(longTime);
						} catch (Exception e) {
							Logging.writeErrorMessage(21, e, "Error in setting file modified date: ");
							// throw e;
						}
					} catch (Exception e) {
						Logging.writeErrorMessage(21, e, "Error in setting file modified date: ");
						// throw e;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logging.writeErrorMessage(17, e, "Error in html file writing.");
			return;
		}
	}

	/**
	 * 複数行に渡っているヘッダを1行にまとめる
	 * 
	 * @param s
	 *            複数行のヘッダ
	 * @return 1行にまとめたヘッダ
	 */
	private String convertOneLine(String s) {
		StringBuffer sb = new StringBuffer(255);
		if (s != null) {
			StringTokenizer st = new StringTokenizer(s, "\r\n");
			boolean isFirstLine = true;
			while (st.hasMoreTokens()) {
				if (isFirstLine)
					sb.append(st.nextToken());
				else
					sb.append(st.nextToken().substring(1).trim());
				isFirstLine = false;
			}
		}
		return sb.toString();
	}

	/**
	 * 文字列の中にMIMEエンコードがされている個所があるかを判断し、エンコードがあれば解除する
	 * 
	 * @param str
	 *            文字列
	 * @return エンコードを解除した文字列
	 */
	/*
	 * private String myDecodeString(String str) {
	 * 
	 * if ( str == null ) return "";
	 * 
	 * int pointer = 0; int encStart = 0, encEnd = 0; StringBuffer sb = new
	 * StringBuffer(""); while ((encStart = str.indexOf("=?", pointer)) >= 0) {
	 * int secondQ = str.indexOf("?", encStart + 2); int thirdQ =
	 * str.indexOf("?", secondQ + 1); encEnd = str.indexOf("?=", thirdQ + 1);
	 * 
	 * if (encEnd < 0) { sb.append(str.substring(pointer, encStart + 2));
	 * pointer = encStart + 2; } else { sb.append(str.substring(pointer,
	 * encStart)); try { String decodedStr =
	 * MimeUtility.decodeText(str.substring(encStart, encEnd + 2)); //デコードしてみる
	 * 
	 * //変換できなかった文字列の割合をチェックする int c = 0; for ( int i = 0 ; i <
	 * decodedStr.length() ; i++ ) if ( decodedStr.codePointAt( i ) == 65533 )
	 * // 65533 is 'REPLACEMENT CHARACTER' c++; if ( (
	 * (float)c/decodedStr.length() ) > 0.3f ) decodedStr =
	 * MimeUtility.decodeText("=?Shift_JIS?" + str.substring( secondQ + 1));
	 * //変換できなかった文字列が30%以上の場合は、Shift_JISとしてデコードしてみる
	 * //これは単に間違えたメールを送っているだけにすぎないのだがねぇ sb.append(decodedStr); } catch
	 * (Exception e) { sb.append(str.substring(encStart, encEnd + 2)); } pointer
	 * = encEnd + 2; } } sb.append(str.substring(pointer, str.length())); return
	 * sb.toString(); }
	 */
	/**
	 * CR+LFで区切られた文字列を、CRだけにする。つまり、CRに続くLFを取り除く
	 * <p>
	 * （これ、必要ないような気がしている〜2003/5/18 新居）
	 * 
	 * @param s
	 *            元になる文字列
	 * @return 変換した文字列
	 */
	private String convertCRLFtoCR(String s) {
		StringBuffer sb = new StringBuffer(255);
		StringTokenizer st = new StringTokenizer(s, "\r", true);
		while (st.hasMoreTokens()) {
			String oneLine = st.nextToken();
			if (oneLine.charAt(0) == '\n')
				sb.append(oneLine.substring(1, oneLine.length()));
			else
				sb.append(oneLine);
		}
		return sb.toString();
	}

	/**
	 * ファイル名やコメントに設定する文字列のパターンに応じて、メールのデータからそれらの文字列を作成する
	 * 
	 * @param パータンを記述する文字列
	 * @return パターンに従ってメールのデータを埋め込んだ文字列
	 */
	private String parseMailInfoString(String pattern) {

		SerialCodeGenerator serialGen = SerialCodeGenerator.getInstance();

		String pStr = pattern;
		int pStrLen = pStr.length();
		StringBuffer fileNameCandidate = new StringBuffer("");
		int strWidth = -1;
		for (int i = 0; i < pStrLen; i++) {
			switch (pStr.charAt(i)) {
			case 'F':
				if (fileNameCandidate.length() > 0)
					fileNameCandidate.append(OMEPreferences.getInstance().getItemSeparator());
				if (pStr.charAt(i + 1) == '(') {
					for (int k = i + 2; k < pStrLen; k++) {
						if (pStr.charAt(k) == ')') {
							strWidth = Integer.parseInt(pStr.substring(i + 2, k));
							i = k;
							break;
						}
					}
				}
				if ((strWidth < 0) || (sender.length() < strWidth))
					fileNameCandidate.append(sender);
				else {
					fileNameCandidate.append(sender.substring(0, strWidth));
					strWidth = -1;
				}
				break;
			case 'S':
				if (fileNameCandidate.length() > 0)
					fileNameCandidate.append(OMEPreferences.getInstance().getItemSeparator());
				if (pStr.charAt(i + 1) == '(') {
					for (int k = i + 2; k < pStrLen; k++) {
						if (pStr.charAt(k) == ')') {
							strWidth = Integer.parseInt(pStr.substring(i + 2, k));
							i = k;
							break;
						}
					}
				}
				if ((strWidth < 0) || (subject.trim().length() < strWidth))
					fileNameCandidate.append(subject.trim());
				else {
					fileNameCandidate.append(subject.trim().substring(0, strWidth));
					strWidth = -1;
				}
				break;
			case 'N':
				if (fileNameCandidate.length() > 0)
					fileNameCandidate.append(OMEPreferences.getInstance().getItemSeparator());
				fileNameCandidate.append(serialGen.getSerialCode());
				break;
			}
		}

		char dst[] = new char[fileNameCandidate.length()];
		fileNameCandidate.getChars(0, fileNameCandidate.length(), dst, 0);
		return new String(dst);
	}

	/**
	 * 引数に、ContentTypeヘッダの文字列を指定し、そこからキャラクタセットを示す文字列を取り出す。
	 * ContentTypeクラスを使って処理する事にしたので、この関数は使いません。(2007/5/13）
	 * 
	 * @param str
	 *            ContentTypeヘッダの文字列
	 * @return キャラクタセットを示す文字列
	 */
	/*
	 * private String getCharSet(String str) { // Charsetの情報を取り出す。結果は大文字でまとめる
	 * return getValue(str, "charset=").toUpperCase().trim(); }
	 */
	/**
	 * ヘッダのデータから引数に指定した属性の値を取り出す。つまり、charset="Shift_JIS"などと
	 * いったデータが想定されるときに、このヘッダのデータの中にある「charset」を属性として指定し Shift_JISの文字列を得る
	 * ContentTypeクラスを使って処理する事にしたので、この関数は使いません。(2007/5/13）
	 * 
	 * @param source
	 *            検索する元データ、ヘッダのフィールドデータを想定
	 * @param key
	 *            属性の名前
	 * @return 属性の値
	 */
	/*
	 * private String getValue(String source, String key) { String value = null;
	 * int csPos = source.toLowerCase().indexOf(key.toLowerCase()); if (csPos >
	 * -1) { int setStart = 0, setEnd = 0; if ( csPos + key.length() >=
	 * source.length() ) { value = ""; } else { if (source.charAt(csPos +
	 * key.length()) == '"') { setStart = csPos + key.length() + 1; setEnd =
	 * setStart + source.substring(setStart).indexOf('"'); if (setStart >
	 * setEnd) setEnd = source.length(); } else { setStart = csPos +
	 * key.length(); setEnd = setStart +
	 * source.substring(setStart).indexOf(';'); if (setEnd < setStart) setEnd =
	 * setStart + source.substring(setStart).indexOf(' '); if (setEnd <
	 * setStart) setEnd = setStart + source.substring(setStart).indexOf('\r');
	 * if (setEnd < setStart) setEnd = setStart +
	 * source.substring(setStart).indexOf('\n'); if (setEnd < setStart) setEnd =
	 * source.length(); } value = source.substring(setStart, setEnd); } } return
	 * value; }
	 */
	/**
	 * 16進表記の文字列をコードとして読み取った文字列を得る（ファイルタイプやクリエイタのヘッダ処理用。
	 * したがって、コードはASCIIコードと限定しているため、日本語処理はこのままではできない）
	 * 
	 * @param 16進表記された文字列
	 * @return 引数を変換した文字列
	 */
	private String hex2String(String hex) {
		if (hex == null)
			return null;
		try {
			int highDigit, lowDigit;
			int zeroCode = Character.getNumericValue('0');
			char[] strAr = new char[hex.length() / 2];
			for (int i = 0; i < hex.length() / 2; i++) {
				highDigit = Character.getNumericValue(hex.charAt(i * 2)) - zeroCode;
				if (highDigit > 9)
					highDigit -= 7;
				if (highDigit > 15)
					highDigit -= 32;
				lowDigit = Character.getNumericValue(hex.charAt(i * 2 + 1)) - zeroCode;
				if (lowDigit > 9)
					lowDigit -= 7;
				if (lowDigit > 15)
					lowDigit -= 32;
				strAr[i] = (char) (highDigit * 16 + lowDigit);
			}
			return new String(strAr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** 
     */
	private class LazyComparator implements Comparator<Object> {

		public int compare(Object o1, Object o2) {
			return 1;
		}
	}

	/**
	 * パート1つ1つを管理するクラス
	 */
	private class OnePartInfo {
		public String mailFile;
		public String usedCharacterSet;
		public MyContentType contentType;
		public String disposition;
		public String option;

		// public Set containedSet = null;

		public OnePartInfo(String fName, MyContentType cType, String cSet, String cDisposition, String cOption) {
			mailFile = fName;
			contentType = cType;
			usedCharacterSet = cSet;
			disposition = cDisposition;
			option = cOption;
		}
	}

	private class MyContentType extends ContentType {

		public MyContentType(String s) {
			super();

			int firstSlashPos = s.indexOf('/');
			int firstSemiColonPos = s.indexOf(';');
			if (firstSlashPos < 0) {
				if (firstSemiColonPos < 0)
					this.setPrimaryType(s.trim());
				else
					this.setPrimaryType(s.substring(0, firstSemiColonPos).trim());
			} else {
				if (firstSemiColonPos < 0) {
					this.setPrimaryType(s.substring(0, firstSlashPos).trim());
					this.setSubType(s.substring(firstSlashPos + 1).trim());
				} else {
					this.setPrimaryType(s.substring(0, firstSlashPos));
					this.setSubType(s.substring(firstSlashPos + 1, firstSemiColonPos));
				}
			}
			ParameterList paramList = new ParameterList();

			String restOfSource = s.substring(firstSemiColonPos + 1);
			int nextSemicolonPos, equalPos;
			while ((nextSemicolonPos = restOfSource.indexOf(';')) > -1) {
				equalPos = restOfSource.indexOf('=');
				if ((equalPos > -1) && (equalPos < nextSemicolonPos)) {
					paramList.set(restOfSource.substring(0, equalPos).trim(),
							makeItCleanText(restOfSource.substring(equalPos + 1, nextSemicolonPos)));
				} else {
					Logging.writeMessage("Error in parsing Content-Type header.");
				}
				restOfSource = restOfSource.substring(nextSemicolonPos + 1);
			}
			if (restOfSource.length() > 0) {
				equalPos = restOfSource.indexOf('=');
				if (equalPos > -1) {
					paramList.set(restOfSource.substring(0, equalPos).trim(),
							makeItCleanText(restOfSource.substring(equalPos + 1).trim()));
				} else {
					Logging.writeMessage("Error in parsing Content-Type header.");
				}
			}

			if (paramList.size() > 0)
				this.setParameterList(paramList);
			// System.out.println(this.toString());
		}

		String makeItCleanText(String s) {
			String target = s.trim();
			if (target.startsWith("\"") && target.endsWith("\"") && target.length() > 1)
				target = target.substring(1, target.length() - 1);
			if (target.startsWith("\'") && target.endsWith("\'") && target.length() > 1)
				target = target.substring(1, target.length() - 1);
			/*
			 * TextConverter txConv = null; try { txConv = (TextConverter)
			 * Class.
			 * forName(MailFormatInfo.getInstance().getMailConverter(thisMailLocale
			 * )).newInstance(); } catch (Exception e) { }
			 */if (txConv != null) {
				// if ( OMEPreferences.getInstance().isHeaderTextUnifying() ) {
				target = txConv.headerTextUnifier(target);
				// }
			}
			return target;
		}
	}

	/**
	 * @param partFileName
	 * @param contentType
	 * @param charSet
	 * @return
	 */
	/*
	 * private String forceGetPartFileName(String partFileName, String
	 * contentType, String charSet) { int posName; int endPos; String convCType
	 * = contentType; try { convCType = new String(contentType.getBytes(),
	 * charSet); } catch (Exception e) { Logging.writeErrorMessage(113, e,
	 * "Encoding Error in forceGetPartFileName."); }
	 * 
	 * posName = convCType.indexOf("name="); endPos = convCType.length();
	 * posName = posName + 5; if (convCType.charAt(posName) == '\"') { posName =
	 * posName + 1; endPos = convCType.indexOf('\"', posName); } else { for (int
	 * i = posName + 5; i < convCType.length(); i++) { char c =
	 * convCType.charAt(i); if ((c == ' ') || (c == ';') || (c == ',') || (c ==
	 * '\r') || (c == '\n')) { endPos = i; break; } } } if (endPos >= 0)
	 * partFileName = convCType.substring(posName, endPos);
	 * 
	 * Logging.writeMessage("Pickup forthly the Part File Name:" +
	 * partFileName); return partFileName; }
	 */
	/**
	 * パートのデータをそのままファイルに保存する（1回しか使っていないので、サブルーチンにはしないことにした）
	 * 
	 * @param part
	 *            処理対象パート
	 * @param cType
	 *            このパートのコンテントタイプ
	 * @param encoding
	 *            このパートのエンコード
	 * @param fileName
	 *            保存するファイル名
	 */
	/*
	 * private void extractByMyself(MimePart part, String cType, String
	 * encoding, String fileName) throws Exception {
	 * Logging.writeMessage("---> Extract part data by myself /" + encoding +
	 * " -->" + fileName);
	 * 
	 * try { InputStream originalStream = null; try { // まずは、そのまま取り出そうと試みる。
	 * originalStream = part.getInputStream(); } catch (MessagingException ex) {
	 * // MimeBodyPartなら getRawInputStream() で再試行。 if (!(part instanceof
	 * MimeBodyPart)) throw ex; if (encoding == null || "".equals(encoding)) {
	 * encoding = "8bit"; } originalStream = MimeUtility.decode(((MimeBodyPart)
	 * part).getRawInputStream(), encoding); }
	 * 
	 * File mimeFile = new File(savingFolder, fileName); FileOutputStream outSt
	 * = new FileOutputStream(mimeFile);
	 * 
	 * byte[] buffer = new byte[10000]; int bLen; do { bLen =
	 * originalStream.read(buffer, 0, 10000); if (bLen != -1)
	 * outSt.write(buffer, 0, bLen); } while (bLen >= 0); outSt.close(); } catch
	 * (Exception ec) { Logging.writeErrorMessage(401, ec,
	 * "extract myself error."); ec.printStackTrace(); throw ec; } }
	 */
}
