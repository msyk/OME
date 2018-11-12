package OME;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.mail.internet.MailDateFormat;

import OME.mailformatinfo.*;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import OME.mailformatinfo.MailFormatInfo;

/**
 * OME_Preferencesフォルダの位置などのOME動作に関わる基本的な設定を管理する。
 * 主に、Behavior_Info.txtファイルの内容を得るメソッドが中心だが、OMEのルートや
 * 設定フォルダなど基本的な設定を取り出すことにも使っている。
 *
 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 *
 * 2002/1/21:新居:これから、MmPreference（従来存在していたクラス）より移動する
 * 2002/1/25:新居:MmPreferencesより移動した
 *    :
 * その後:新居,金内:いろいろ修正、現在に至る
 *    :
 * 2003/7/5:新居:getAdditionalHeasers, isRemoveMe, isRemoveMeAllメソッドを追加
 * 2003/7/27:村上:isFechmailUIDLメンバ関数とFechmailUIDLメンバ変数を追加
 * 2003/8/1:新居:メソッドgetFetchMailPath, setFetchMailPathを追加。変数fetchmailPathを定義
 * 2003/8/29:新居:メソッドgetInstanceにおいて、Behavior_Info.txtファイルが修正されていれば
 *                ファイルの内容を反映するようにした
 * 2003/9/12:新居:メソッドgetForce7bit、setForce7bitを追加。ローカル変数force7bitを追加
 * 2003/9/23:新居:ローカル変数force7bitをtrueが初期値にした。
 *                Behavior_Info.txtでのこの変数を変更するキーワードを変更
 * 2003/10/16:新居:キーワードInsertFromToNewMailに対応
 * 2003/11/30:新居:アプリケーション、tempファイル位置をカスタマイズ
 * 2004/01/26:ぐるり:Security Update 2003-12-19を当てるとAPOPでメール受信できない問題に対処
 *                   メソッドgetFetchSizeLimitを追加。変数fetchSizeLimitを定義
 *                   初期値-1(fetchsizelimitを使用しない)
 *                   0以上の値を指定するとfetchsizelimitを.fetchmailrcに追加する。
 *                   fetchmail 6.2.5以上で無いと使えないので注意
 * 2005/4/30:新居:Tiger対応。openコマンドの-aパラメータがエイリアスでは機能しなくなったので、エイリアスの解決を入れる
 * 2005/5/8:新居:procmailでのバックアップファイルの作成をデフォルトでオフにして、関連のスイッチをつけた
 * 2005/8/27:新居:NowDownloadingを一定時間後に無視する設定に対応（キーワードはIgnoringDuration=）
 * 2005/9/19:新居:ログフォルダのカスタマイズに対応（キーワードはLogFolderPath=）
 * 2006/12/31:新居:フレームワークに対応
 * 2007/9/25:新居:downloadMailsMessageStandardOutputとsendMailMessageStandardOutputは常にtrueになるようにした。
 *					Behavior_Info.txtによるコントロールは切った
 * 2009/3/30:新居:いくつかのメソッドをSystemPropertyHelperクラスに移動した
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * 2009/11/10:新居:SSL証明書ファイルの読み込みに対応
 * </pre>
 *
 * @author 新居雅行（Masayuki Nii/msyk@msyk.net）
 *
 * @version $Id: OMEPreferences.java,v 1.34 2007/10/02 13:45:37 msyk Exp $
 */
public class OMEPreferences implements Cloneable {

    //オブジェクトを生成したかどうかを記憶する
    private static boolean mySelf = false;

    //Behavior_Info.txtを参照
    private static File targetFile;

    //Behavior_Info.txtの修正時間
    private static long behaviorModified;
	
	private SystemPropertyHelper sysPrefHelper = new OME.SystemPropertyHelper();

    private OMEPreferences() {}

    /**
     * 新たにMovingInfoクラスのインスタンスを得る
     * @return 生成されたインスタンスへの参照
     */
    public static OMEPreferences getInstance() {
        if (!mySelf) {
            mySelf = true;
            targetFile = new File(getOMEPrefStatic(), "Behavior_Info.txt");
            behaviorModified = 0;
            //             OMEPreferences newObject = new OMEPreferences();
            //             newObject.privateOMEDefaultMessageMakerClasses();
            //             ownStack.push(newObject);
            //             newObject.initBehaviorInfoFile();
        }

        OMEPreferences currentInstance;
        if (behaviorModified < targetFile.lastModified()) {
            if (ownStack.size() > 0) {
                pop();
            }
            currentInstance = new OMEPreferences();
            ownStack.addLast(currentInstance);
            currentInstance.privateOMEDefaultMessageMakerClasses();
            currentInstance.initBehaviorInfoFile();
        } else {
            currentInstance = (OMEPreferences) ownStack.getLast();
        }
        behaviorModified = targetFile.lastModified();
        return currentInstance;
    }

    /**
     * 現在の設定をスタックに入れる。同時に、現在の設定のクローンを作成
     * しその後にgetInstanceでインスタンスを要求すると、クロー
     * ンのデータを戻す。  現在の設定を残しておきながら、新しい
     * 設定の情報を作り、さらに後で現在の設定に戻すという機能を
     * 想定する。
     * <p>
     * push, popのメソッドを使う場合には、OMEPreference.getInstance()で得られた設定情報への参照は
     * 変数には入れないようにするのが得策だろう。その都度、前記のgetInstanceメソッドを呼び出して、
     * 「現在の設定データへの参照」を得るようにしないと、スタックに積み上げたデータを参照してしまう
     * 可能性がある。ただし、pushないしはpopをするまでは、getInstanceの戻り値を変数に入れておいても問
     * 題はない。言い換えれば、getInstanceで得られた値は、push、popをまたいでは継続できない。
     * 
     * <p>
     * 以下は、サンプルプログラムである。インスタンスをその都度取得するのが1つの方法だが、
     * 毎回、getInstanceメソッドを使うという手もある。
     * <p>
     * OMEPreferences omePref = OMEPreferences.getInstance();<br>
     * :<br>
     * OMEPreferences.pop();<br>
     * omePref = OMEPreferences.getInstance();
     * :<br>
     * OMEPreferences.push();<br>
     * omePref = OMEPreferences.getInstance();<p>
     *         
     */
    public static void push() {
        try {
//            ownStack.addLast(((OMEPreferences) ownStack.getLast()).clone());
            	ownStack.addLast((OMEPreferences) 
            	 ((OMEPreferences) ownStack.getLast()).clone());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * スタックの設定を現在の設定に戻す。pushメソッドの説明も参照。
     */
    public static void pop() {
        ownStack.removeLast();
    }

    private static LinkedList<OMEPreferences> ownStack = new LinkedList<OMEPreferences>();

    private File omePref = null;

    private File omeRoot = null;

    static public final String SIGNATURE_FILENAME = "Signature.txt";

    static public final String TOPMESSAGE_FILENAME = "TopMessage.txt";

    static public final String HEADER_ADDENDUM_FILENAME = "HeaderAddendum.txt";

    static private final String DRAFT_FOLDER = "Draft";

    static private final String OUTBOX_FOLDER = "OutBox";

    static private final String TEMP_FOLDER = "temp";

    /**
     * OwME_PreferencesにあるSignature.txtファイルに記載された署名の文
     * 字列を取り出す。
     *
     * @return 取り出した署名の文字列。ファイルがない場合には空文字列。
     */
    public String getSign() {
        return prefFileContents(SIGNATURE_FILENAME);
    }

    /**
     * 送信メールの最初に挿入する文字列を、OME_Preferencesにある
     * TopMessage.txtファイルから取り出す
     *
     * @return　送信メッセージに挿入する文字列。ファイルがない場合には空文字列。
     */
    public String getTopMessage() {
        return prefFileContents(TOPMESSAGE_FILENAME);
    }

    /**
     * 送信メールのヘッダに追加するする文字列を、OME_Preferencesにある
     * HeaderAddendum.txtファイルから取り出す
     *
     * @return　ヘッダに挿入する文字列。ファイルがない場合には空文字列
     */
    public String getAddHeaders() {
        return prefFileContents(HEADER_ADDENDUM_FILENAME);
    }

    /**
     * アップロードファイルとして作成可能なファイルの参照を得る。
     * Upload-n.txtという名前のファイルを、OutBoxフォルダに作成するが、
     * nの数字を1から増加させて、存在しないファイル名を得る。
     *
     * @return アップロードとして作成可能なファイル
     */
    synchronized public File newOutFile() {
        File outboxFolder;
        List<File> fileList = new ArrayList<File>();
        FilenameFilter filter = new VisibleFileFilter();

        try {
            outboxFolder = (new MacFolder(getOMERoot(), OUTBOX_FOLDER, true)).getAsFile();
        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 802 %% What's happen? OutBox is a file: " + e.getMessage());
            outboxFolder = null;
        }
        sysPrefHelper.addAll(fileList, outboxFolder.list(filter));
        if (isUseDraft()) {
            try {
                outboxFolder = (new MacFolder(getOMERoot(), DRAFT_FOLDER, true)).getAsFile();
            } catch (Exception e) {
                Logging.writeMessage("%% OME Error 802 %% What's happen? Draft is a file: " + e.getMessage());
                outboxFolder = null;
            }
            if (!outboxFolder.exists()) {
                outboxFolder.mkdirs();
            }
            sysPrefHelper.addAll(fileList, outboxFolder.list(filter));
        }

        String upFileName = "upload-";
        int index;
        for (index = 1; index < 10000000; index++) {
            if (fileList.indexOf(upFileName + index + ".wmail") < 0) {
                break;
            }
        }

        return new File(outboxFolder, upFileName + index + ".wmail");
    }

    class VisibleFileFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            boolean rVal = true;
            if (name.startsWith(".")) {
                rVal = false;
            }
            return rVal;
        }
    }

    /**
     * 引数に指定したファイルを、OMEで利用するエディタで開く
     *
     * @param messageFile 開くファイルへの参照
     */
    public void openByEditor(File messageFile) {
        OMEPreferences omePref = OMEPreferences.getInstance();
        OpenFile.byApplication(omePref.getMailWriterApp(), messageFile);
    }

    /**
     * OME_Preferences内のファイルの内容を返す。
     *
     * @param filename ファイル名
     * @return ファイルの内容。ファイルが無ければ空文字列。
     */
    private String prefFileContents(String filename) {
        try {
            File targetFile = new File(getOMEPref(), filename);
            return sysPrefHelper.readFile(targetFile);
        } catch (Exception ex) {
            Logging.writeMessage(ex.getMessage(), 2);
        }
        return "";
    }

    /**
     メール参照用のエディタアプリケーションへの参照を得る。Mail_Readerファイルへの参照となっている。　
     @return メール参照と作成のアプリケーションへの参照　　*/
    public File getMailReaderApp() {
        File mRAlias = new File(getOMEPref(), "Mail_Reader");
        if (mRAlias.exists())	{
			return MacAlias.resolveMacAlias( mRAlias ); 
		}
		File omeMailViewer = new File( "/Applications/OME_Applications.localized/OMEMailViewer.app" );
        if (omeMailViewer.exists())	{
			return omeMailViewer; 
		}
		omeMailViewer = new File( "/Applications/OME_Applications/OMEMailViewer.app" );
        if (omeMailViewer.exists())	{
			return omeMailViewer; 
		}
        return (new File("/Applications/TextEdit.app"));
    }

    /**
     メール作成用のエディタアプリケーションへの参照を得る。Mail_Writerファイルへの参照となっているが、
     エイリアスがない場合には、Mail_Readerを参照する。それでもなければ、
     @return メール参照と作成のアプリケーションへの参照　　*/
    public File getMailWriterApp() {
        File mWAlias = new File(getOMEPref(), "Mail_Writer");
        if (mWAlias.exists()) { return MacAlias.resolveMacAlias( mWAlias ); }

        File mRAlias = new File(getOMEPref(), "Mail_Reader");
        if (mRAlias.exists()) { return MacAlias.resolveMacAlias( mRAlias ); }

        return (new File("/Applications/TextEdit.app"));
    }

    /**
     * アドレスブックのアプリケーションへの参照を得る。Address_Bookファイルへの参照となっている。　
     * @return アドレスブックアプリケーションへの参照　
     */
    public File getAddressBookApp() {
        File mRAlias = new File(getOMEPref(), "Address_Book");
        if (mRAlias.exists()) { return MacAlias.resolveMacAlias( mRAlias ); }
        return (new File("/Applications/Address Book.app"));
    }

    private String fileSepString = "|";

    /**
     * 受信メールのファイル名を作成するときに、
     * 送信者名やSubjectなどの情報を区切るために挿入する文字列を得る。
     *
     * @return 区切りの文字列
     */
    public String getItemSeparator() {
        return fileSepString;
    }

    /**
     * 受信メールのファイル名を作成するときに、
     * 送信者名やSubjectなどの情報を区切るために挿入する文字列を設定する。
     *
     * @param separatorStr 区切りの文字列 
     */
    public void setItemSeparator(String separatorStr) {
        fileSepString = separatorStr;
    }

    private String fetchmailPath = "/usr/bin/fetchmail";

    /**
     * fetchmailコマンドを起動するためのパスを得る
     * @return 区切りの文字列     
     */
    public String getFetchMailPath() {
        return fetchmailPath;
    }

    /**
     * fetchmailコマンドを起動するためのパスを設定する
     *
     * @param separatorStr fetchmailコマンドのパス（fetchmailまで含む）
     */
    public void setFetchMailPath(String fstr) {
        fetchmailPath = fstr;
    }

    private boolean force7bit = true;

    /**
     * 送信メールを強制的に7ビットでエンコーディングするかどうか
     *
     * @return trueなら7ビットでエンコーディングする      
     */
    public boolean getForce7bit() {
        return force7bit;
    }

    /**
     * 送信メールを強制的に7ビットでエンコーディングするかどうかを設定する
     * @param flag 設定値 
     */
    public void setForce7bit(boolean flag) {
        force7bit = flag;
    }

    /**
     * 受信したメールを保存するメッセージファイルのファイル名を定義する文字列を得る。
     * <p>
     * ファイル名の規則では、次のアルファベットを利用できる。アルファベットの後に半角の（）で
     * 数値を囲って指定すると、その数値の文字数を超える場合には、先頭からその文字数分だけを使用する。
     * 文字数であってバイト数ではない。
     * <table>
     *  <tr><td>F</td><td>送信者名（Fromフィールド）</td></tr>
     *  <tr><td>S</td><td>件名（Subjectフィールド）</td></tr>
     *  <tr><td>N</td><td>シリアルナンバー</td></tr>
     * </table>
     * @return ファイル名の定義文字列  
     */
    public String getFileNamePattern() {
        return fileNamePat;
    }

    /**
     * 受信したメールを保存するメッセージファイルのファイル名を定義する文字列を設定する。
     * @see #getFileNamePattern
     * @param param 設定する文字列
     */
    public void setFileNamePattern(String param) {
        fileNamePat = param;
    }

    /**
     * 受信したメールのメッセージファイルのコメントに設定する文字列の定義を得る。
     * @see #getFileNamePattern
     * @return     コメントの定義文字列   
     */
    public String getCommentPattern() {
        return commandPat;
    }

    /**
     * 受信したメールのメッセージファイルのコメントに設定する文字列の定義を設定する。
     * @see #getFileNamePattern
     * @param param コメントの定義文字列  
     */
    public void setCommentPattern(String param) {
        commandPat = param;
    }

    /**
     * ログ作成の間隔を指定する文字列を得る。
     * @return     ログ作成間隔を示す文字列        
     */
    public String getLogingPriod() {
        return logingPriod;
    }

    /**
     * 返信メールにおいて、元メールから引用した各行の最初につける文字列を得る
     * @return 引用行の最初に設定する文字列       
     */
    public String getCommentHead() {
        return commHead;
    }

    private boolean insertFromToNewMail = false;

    /**
     * 送信メールファイルを作成する時、Fromフィールドを自動的に設定する。設定する情報は、
     * Sender_Info.txtファイルの最初の項目から取り出すかどうかの指定を得る。
     *
     * @return trueなら取り出す    
     */
    public boolean isInsertFromToNewMail() {
        return insertFromToNewMail;
    }

    /**
     * OME動作上のロケール情報を得る。
     * OME_Preferences/Behavior_Info.txtに設定がない場合には、デフォル
     * トのロケールを戻す
     *
     * @return 引用行の最初に設定する文字列
     */
    public Locale getOMELocale() {
        if (omeLocale == null) omeLocale = Locale.getDefault();
        return omeLocale;
    }

    /**
     * 返信メールを作成するとき、引用文の前につけるコメントを、指定し
     * たロケールに対応したものとして得る
     *
     * @param subject 返信元メールの件名
     * @param from 返信元メールの送信者
     * @param date 返信元メールの送信日時
     * @param messageId 返信元メールのメッセージID
     * @param to 返信元メールの宛先
     * @param loc 言語情報をロケールで指定
     * @return コメントの文字列
     */
    public String getCommentLine(String subject, String from, String date, String messageId, String to, Locale loc) {
        String resultStr = "";
        File defFile = null;

        if (replyCommStr) {
            defFile = InternationalUtils.getExistsFile(getOMEPref(), "ReplyComment", ".txt", loc);
            if (defFile != null) {
                MailFormatInfo mfInfo = MailFormatInfo.getInstance();

                byte fBuffer[] = new byte[(int) defFile.length()];

                try {
                    InputStream inSt = new FileInputStream(defFile);
                    inSt.read(fBuffer);
                    inSt.close();

                    MyString contents = new MyString(new String(fBuffer, mfInfo.getPrefFilesCode(getOMELocale())));
                    contents.replace("%s", subject);
                    contents.replace("%f", from);
                    contents.replace("%d", date);
                    if ((contents.indexOf("%c") >= 0) || (contents.indexOf("%t") >= 0)) {
                        Date iDate = new MailDateFormat().parse(date, new ParsePosition(0));
                        contents.replace("%c", new SimpleDateFormat("yyyy/M/dd H:mm:ss").format(iDate));
                        contents.replace("%t", new SimpleDateFormat("yyyy/M/dd").format(iDate));
                    }
                    contents.replace("%i", messageId);
                    contents.replace("%r", to);
                    contents.replace("\\r", System.getProperty("line.separator"));
                    resultStr = contents.toString();
                } catch (Exception e) {
                    Logging.writeMessage("%% OME Error 401 %% Error in reading ReplyComment file: " + e.getMessage());
                }
            }
        }

        if ((!replyCommStr) || (defFile == null)) {
            CommentatorInterface commObj = (CommentatorInterface) InternationalUtils.getClassInstance(
                    "OME.mailformatinfo.Commentator", loc);
            resultStr = commObj.getComment(subject, from, date, messageId, to);
        }
        return resultStr;
    }

    static private final File USER_PREFERENCE_DIR = new File(System.getProperty("user.home") + "/Library/Preferences");

    //    static private final File OME_PREFERENCE_DIR =
    //      (new MacFolder(USER_PREFERENCE_DIR, "OME_Preferences", true)).getAsFile();

    /**
     * OME_Preferencesフォルダへの参照を得る（スタティック版〜システムプロパティからの取得はできない）
     *
     * @return  OME_Preferencesフォルダへの参照 
     */
    public static File getOMEPrefStatic() {
        File omePref = null;
        try {
            omePref = new MacFolder(USER_PREFERENCE_DIR, "OME_Preferences", true).getAsFile();
            if (!omePref.exists()) {
                omePref = null;
                throw new Exception("OME_Preferences does not exists.");
            }
        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 801 %% OME_Preferences is illegal: " + e.getMessage());
        }

        return omePref;
    }

    /**
     * OME_Preferencesフォルダへの参照を得る
     *
     * @return  OME_Preferencesフォルダへの参照 
     */
    public File getOMEPref() {
        if (omePref == null) {
            try {
                omePref = sysPrefHelper.getFile("net.msyk.ome.OMEPrefFolder", (new MacFolder(USER_PREFERENCE_DIR, "OME_Preferences",
                        true)).getAsFile());
                if (!omePref.exists()) {
                    omePref = null;
                    throw new Exception("OME_Preferences does not exists.");
                }
            } catch (Exception e) {
                Logging.writeMessage("%% OME Error 801 %% OME_Preferences is illegal: " + e.getMessage());
            }
        }
        return omePref;
    }

    /**
     * OMEのアプリケーションが存在するフォルダへの参照を得る
     * @return     OMEのアプリケーションが存在するフォルダへの参照
     */

    public File getOMEApplicationsFolder() {
/*        String propString = System.getProperty("net.msyk.ome.OMEApplicationsFolder");
        if (propString != null) {
            omeAppFolder = new File(propString);
            return omeAppFolder;
        }

*/
		if ( omeAppFolder != null)	return omeAppFolder;
		
        if ( omeApplicationFolder.length() > 0 )	{
			omeAppFolder = new File(omeApplicationFolder);
			return omeAppFolder;
		}
		String targetAppName ="/OME_SendMail.app";
		String appFolderUsually = "/Applications/OME_Applications.localized";
		String omeAppFolderEver = "/OME_Applications";
		String userHome = System.getProperty( "user.home" );
		
		File candidateItem = new File( userHome + appFolderUsually + targetAppName );
		if ( candidateItem.exists() )	{
			omeAppFolder = candidateItem.getParentFile();
			return omeAppFolder;
		}
		candidateItem = new File( appFolderUsually + targetAppName );
		if ( candidateItem.exists() )	{
			omeAppFolder = candidateItem.getParentFile();
			return omeAppFolder;
		}
		candidateItem = new File( userHome + appFolderUsually + omeAppFolderEver + targetAppName );
		if ( candidateItem.exists() )	{
			omeAppFolder = candidateItem.getParentFile();
			return omeAppFolder;
		}
		candidateItem = new File( appFolderUsually + omeAppFolderEver + targetAppName );
		if ( candidateItem.exists() )	{
			omeAppFolder = candidateItem.getParentFile();
			return omeAppFolder;
		}
		return null;
    }

    private File omeAppFolder = null;

    /**
     * OMEのフレームワークが存在するフォルダを得る
     * @return     OMEのアプリケーションが存在するフォルダへの参照
     */
    public File getOMEFrameworkFolder() {
		String frameworkRPath = "/Library/Frameworks/OME.framework";
		String userHome = System.getProperty( "user.home" );
		File candidateFolder = new File( userHome + frameworkRPath);
		if ( candidateFolder.exists() )
			return candidateFolder;
		 candidateFolder = new File( frameworkRPath);
		if ( candidateFolder.exists() )
			return candidateFolder;
		 candidateFolder = new File( "/System" + frameworkRPath);
		if ( candidateFolder.exists() )
			return candidateFolder;
		return null;
    }
    /**
     * OMEのツールが存在するフォルダへの参照を得る
     * @return     OMEのアプリケーションが存在するフォルダへの参照
     */
    public File getOMEToolsFolder() {
/*        String propString = System.getProperty("net.msyk.ome.OMEToolsFolder");
        if (propString != null)
            return new File(propString);
*/        
        if (omeToolsFolder == null) {
            omeToolsFolder = new File(getOMEFrameworkFolder() + "/Resources");
        }
        return omeToolsFolder;
    }

    private File omeToolsFolder = null;

    /**
     * OMEのルートフォルダの中にあるtempフォルダへの参照を得る。ただし、OMEのルートを認識するときに、tempへのショートカットを
     * プリファレンス内に作り、実際にはそのショートカットへの参照を行うのみとする。
     * @return tempフォルダへの参照
     */
    public File getOMETemp() throws Exception	{
        if (omeRoot == null) getOMERoot();
        try {
            return (new MacFolder(getOMEPref(), TEMP_FOLDER, true)).getAsFile();
        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 804 %% Why 'temp' is a file?: " + e.getMessage());
			throw e;
			//    return null;
        }

    }
    /**
     * OMEのログフォルダへの参照を得る。
     * @return ログフォルダへの参照
     */
    public File getOMELog() {
		if ( omeLogFolder.indexOf("~") == 0 )	{
			omeLogFolder = System.getProperty("user.home") + omeLogFolder.substring( 1 );
		}
        try {
            return (new MacFolder(omeLogFolder, true)).getAsFile();
        } catch (Exception e) {
            System.out.println("%% OME Error 805 %% Why log folder is a file?: " + e.getMessage());
            return null;
        }

    }

    /**
     * OMEのルートフォルダへの参照を得る。OME_Preferencesにある
     * OME_Rootというエイリアスを手がかりにするが、そのファイルが存在
     * しない場合にはアプリケーションを終了させる
     *
     * @return OMEルートへの参照
     */
    public File getOMERoot() {
/*        String propString = System.getProperty("net.msyk.ome.OMERootFolder");
        if (propString != null) {
            omeRoot = new File(propString);
        }
*/
        if (omeRoot == null) {
            File omeRootPointerFile = new File(getOMEPref().getPath() + "/OME_Root");
            if (!omeRootPointerFile.exists()) {
                Logging.writeMessage("%% OME Error 24 %% Nothing OME_Root File");
                System.exit(1);
            }
            omeRoot = MacAlias.resolveMacAlias(new File(getOMEPref().getPath() + "/OME_Root"));
//            String cmd[] = { getOMEToolsFolder() + "/resolvalias", getOMEPref().getPath() + "/OME_Root"};
//            CommandExecuter ex = new CommandExecuter(cmd);
//            omeRoot = new File(ex.doCommand());

            File shortcutToTempFolder = new File(getOMEPref().getPath(), TEMP_FOLDER);
            String cmd3[] = { "rm", "-f", shortcutToTempFolder.getPath()};
            (new CommandExecuter(cmd3)).doCommand();

            File realTempFolder;
            if ((omeTempFolder == null) || (omeTempFolder.length() < 1))
                realTempFolder = new File(omeRoot.getPath(), TEMP_FOLDER);
            else
                realTempFolder = new File(omeTempFolder);
            String cmd2[] = { "ln", "-s", realTempFolder.getPath(), shortcutToTempFolder.getPath()};
            (new CommandExecuter(cmd2)).doCommand();
        }

        return omeRoot;
    }

    /**
     * ログファイルの設定基準に応じた、日付等の文字列を得る<p>
     * 設定基準が「D」で始まっていれば、たとえば「2001-1-1」。
     * 設定基準が「W」で始まっていれば、たとえば「2001-W1」。
     * 設定基準が「M」で始まっていれば、たとえば「2001-1」。
     * 設定基準が「Y」で始まっていれば、たとえば「2001」。
     * これ以外の設定基準なら、「2001-1-1-12-34-56」
     * @return 日付けなどの文字列 
     */
    public String getCurrentDTString() {
        Calendar cal = Calendar.getInstance();
        if (getLogingPriod().startsWith("D")) { 
			return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1)
                + "-" + cal.get(Calendar.DAY_OF_MONTH); }
        if (getLogingPriod().startsWith("W")) { 
			return cal.get(Calendar.YEAR) + "-W" + (cal.get(Calendar.WEEK_OF_YEAR)); }
        if (getLogingPriod().startsWith("M")) { 
			return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1); }
        if (getLogingPriod().startsWith("Y")) {
            return String.valueOf(cal.get(Calendar.YEAR));
        } else {
            return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" 
					+ cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.HOUR_OF_DAY) + "-" 
					+ cal.get(Calendar.MINUTE) + "-" + cal.get(Calendar.SECOND);
        }
    }

    /**
     * メールを送信するときに、Ccに自分宛のアドレスを自動的に含めるかどうか　
     * @return true：含める/false：含めない
     */
    public boolean isIncludeCC() {
        return includeCC;
    }

    private boolean includeCC = false;

    /**
     * メールを送信するときに、Bccに自分宛のアドレスを自動的に含めるかどうか
     * @return true：含める/false：含めない
     */
    public boolean isIncludeBCC() {
        return includeBCC;
    }

    private boolean includeBCC = false;

    /**
     * 返信をするときに、元のメールのCcにあるアドレスを、返信メールのToに指定するかどうか
     * @return true：含める/false：含めない 
     */
    public boolean isIncludeToCC() {
        return includeToCC;
    }

    private boolean includeToCC = false;

    /**
     * メールを送信するとき、メールアドレス以外の文字列を強制的に撤去するかどうか
     * @return true：含める/false：含めない 
     */
    public boolean isForceAddressOnly() {
        return forceAddressOnly;
    }

    private boolean forceAddressOnly = false;

    /**
     * メールの振り分け設定において、Toに対する条件を同時にCcに対しても適用して判断するかどうか
     * @return true：Ccにも適用/false：Toのみ　
     */
    public boolean isCCSameAsTo() {
        return ccSameAsTo;
    }

    private boolean ccSameAsTo = false;

    /**
     * ダウンロードしたメールソースを残しておくかどうか
     * @return true：残す/false：残さない　
     */
    public boolean isMoveFiles() {
        return moveFiles;
    }

    private boolean moveFiles = false;

    /**
     * ダウンロードして作成されたメールファイルを削除するかどうか
     * @return true：含める/false：含めない　
     */
    public boolean isEraseFiles() {
        return eraseFiles;
    }

    private boolean eraseFiles = true;

    /**
     * Finder情報の設定を行わない場合にはtrueにする
     * @return true：設定しない/false：設定する　
     */
    public boolean isNoSetFAttr() {
        return noSetFAttr;
    }

    private boolean noSetFAttr = false;

    /**
     * 返信メールの作成で、元のメールのSubjectの先頭のRe等を取り除く
     * @return true：取り除く/false：そのまま　
     */
    public boolean isCleanRe() {
        return cleanRe;
    }

    private boolean cleanRe = true;

    /**
     * 返信メールの作成で、元のメールのSubjectにある最初の [ ] で囲われた文字を取り除く
     * @return true：取り除く/false：そのまま　
     */
    public boolean isCleanBr() {
        return cleanBr;
    }

    private boolean cleanBr = true;

    /**
     * メールをダウンロードするとき、取り込んだメールのメッセージをサーバに残しておく　
     *  @return true：残す/false：残さない　
     */
    public boolean isKeepMessage() {
        return keepMessage;
    }

    private boolean keepMessage = false;

    /**
     * メールのダウンロードを行った直後に、PPP接続を自動的に切る
     * @return true：切る/false：そのまま　
     */
    public boolean isForceDisconnect() {
        return forceDisconnect;
    }

    private boolean forceDisconnect = false;

    /**
     * メール送信においてJavaMailのデバッグモードで稼働させる
     * @return true：デバッグモード/false：通常モード　
     */
    public boolean isSMTPDebug() {
        return smtpDebug;
    }

    private boolean smtpDebug = false;

    /**
     * fetchmailをverboseモードで稼働し、メッセージをConsoleに表示させる
     * @return true：verboseモード/false：通常モード 
     */
    public boolean isFetchmailV() {
        return fetchmailV;
    }

    private boolean fetchmailV = false;

    /**
     * fetchmail 6.2.5以降専用
     * fetchsizelimitの設定値
     * 0にしないとAPOPで受信出来ないケースがある
     * @return サイズを受信する数(メッセージの数)
     */
    public int getFetchSizeLimit() {
        return fetchSizeLimit;
    }

    private int fetchSizeLimit = -1;

    /**
     * メールのダウンロードにおいて、サーバにあるすべてのメッセージをダウンロードする。
     * 基本的には1度読み取ったけどもサーバに残してあるメールも取り込める
     * @return true：全てダウンロード/false：未読をダウンロード 
     */
    public boolean isFetchmailAllDL() {
        return fetchmailAllDL;
    }

    private boolean fetchmailAllDL = false;

    /**
     * メールのダウンロードにおいて、OMEで未読のメッセージのをダウンロードする。
     * 本機能はUIDLに対応したPOPサーバに対してのみ有効。
     *
     * @return true：未読をダウンロード/false：本機能を無効にする 
     */
    public boolean isFetchmailUIDL() {
        return fetchmailUIDL;
    }

    private boolean fetchmailUIDL = false;

    /**
     * メールの作成、送信処理において、ドラフトモード（下書きモード）の動作を行う 
     * @return true：下書きモード/false：下書きを利用していない	   
     */
    public boolean isUseDraft() {
        return useDraft;
    }

    private boolean useDraft = false;

    /**
     * 受信メールに対するウィルスキャンを行うかどうか
     * @return true：行う/false：行わない 
     */
    public boolean isVirusScaning() {
        return virusScaning;
    }

    private boolean virusScaning = false;

    /**
     * 送信メールにおいて、メールに含まれているHTML等のタグ記述を規則に従って展開する
     * @return true：展開する/false：展開しない　
     */
    public boolean isHTMLExpanding() {
        return htmlExpanding;
    }

    private boolean htmlExpanding = false;

    /**
     * ヘッダテキストの強制変換を行うかどうかを調べる。
     * 強制変換しないと、たとえば、SubjectにJISコードをそのまま乗せたようなメールでの
     *  文字化けになるが、かといって送られるメールが常に日本語コードとは限らないわけで、
     * デフォルトでは強制変換しないようにしてある。
     * 
     * @return true：変換する/false：変換しない　
     */
    public boolean isHeaderTextUnifying() {
        return headerTextUnifying;
    }

    private boolean headerTextUnifying = false;

    /**
     * OME_DownloadMailsで、処理結果を逐一表示するウインドウを表示するかどうか？
     *
     * @return true：表示する/false：表示しない　
     */
    public boolean isDownloadMailsMessageShow() {
        return downloadMailsMessageShow;
    }

    private boolean downloadMailsMessageShow = false;

    /**
     * OME_SendMailで、処理結果を逐一表示するウインドウを表示するかどうか？
     * @return true：表示する/false：表示しない　
     */
    public boolean isSendMailMessageShow() {
        return sendMailMessageShow;
    }

    private boolean sendMailMessageShow = false;

    /**
     * OME_DownloadMailsで、処理結果やエラーメッセージを、標準出力（Console.log）にも出力するかどうか
     * @return true：出力する/false：出力しない　
     */
    public boolean isDownloadMailsMessageStandardOutput() {
        return downloadMailsMessageStandardOutput;
    }

    private boolean downloadMailsMessageStandardOutput = true;

    /**
     * OME_SendMailで、処理結果やエラーメッセージを、標準出力（Console.log）にも出力するかどうか
     * @return true：出力する/false：出力しない　
     */
    public boolean isSendMailMessageStandardOutput() {
        return sendMailMessageStandardOutput;
    }

    private boolean sendMailMessageStandardOutput = true;

    /**
     * OME_SendMailで、処理結果やエラーメッセージを、標準出力（Console.log）にも出力するかどうか
     * @return true：出力する/false：出力しない　
     */
    public boolean isCommentChainSupport() {
        return commentChainSupport;
    }

    private boolean commentChainSupport = true;

    /**
     * OME_SendMailで、処理結果やエラーメッセージを、標準出力（Console.log）にも出力するかどうか
     * @return true：出力する/false：出力しない　
     */
    public boolean isUnReadAliasSupport() {
        return unReadAliasSupport;
    }

    private boolean unReadAliasSupport = true;

    /**
     * ToやCcフィールドで自分宛の宛先を排除する。Sender_Info.txtの最初の1項目（2行目）だけをチェック
     * @return true：出力する/false：出力しない　
     */
    public boolean isRemoveMe() {
        return removeMe;
    }

    private boolean removeMe = false;

    /**
     * ToやCcフィールドで自分宛の宛先を排除する。
     * Sender_Info.txtのすべてのメールをチェック（2, 6, 10...行目）
     *
     * @return true：出力する/false：出力しない　
     */
    public boolean isRemoveMeAll() {
        return removeMeAll;
    }

    private boolean removeMeAll = false;

    /**
     * NowDownloading/NowSendingファイルがここで指定した時間より古い場合、無視してダウンロードや送信をする
	 * デフォルトは-1であるため、時限設定はされていない。
     *
     * @return 秒単位で戻す（-1なら、存在すれば必ず処理はしないという動作）　
     */
    public long getIgnoringDuration() {
        return ignoringDuration;
    }

    private long ignoringDuration = -1;
	
	/**
	 * JavaVMつまりjavaコマンドのパスを指定する。J2SE 1.5によって一部動かない機能が出るため、強制的に1.4.2を動かすのが目的
	 * @return JavaVMへのパス（文字列）
	 */
	public String getJavaVMPath() {
		return javaVMPath;
	}
	
	private String javaVMPath = "/System/Library/Frameworks/JavaVM.framework/Commands/java";
//	private String javaVMPath = "/System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Commands/java";

	public String getUseKeyFileWithPassword() {
		return useKeyFileWithPassword;
	}
	private String useKeyFileWithPassword = "";

    private boolean replyCommStr = false; //返信のコメントを定義文字列を使って定義する

    private String fileNamePat = "F(15)S(25)N";

    private String commandPat = "";

    private String logingPriod = "Daily";

    private String commHead = ">";

    private String subjPrefix = "Re-";

    private Locale omeLocale;

    private List<String> messageMakerClasses = new Vector<String>();

    private List<String> additionalHeaders = null;

    private String omeApplicationFolder = "";

    private String omeTempFolder = "";
	
	private String omeLogFolder = "~/Library/Logs/OME";

    /**
     * メッセージ作成時に呼び出すクラス名の集合の初期値を設定するメソッド
     */
    void privateOMEDefaultMessageMakerClasses() {
        messageMakerClasses.add("StandardSave");
        messageMakerClasses.add("UnreadAliases");
		messageMakerClasses.add("MailSourceBackup");
        messageMakerClasses.add("SucceedProcess");
    }

    /**
     * メッセージ作成時に呼び出すクラス名の集合
     * @return メッセージ作成で利用するクラス群
     */
    public List<String> getMessageMakerClasses() {
        return messageMakerClasses;
    }

    /**
     * メッセージの最初に追加するフィールド
     * @return メッセージの最初に追加するフィールド
     */
    public List<String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    /**
     * 返信メールを作成する場合のもとメールのSubjectの前につける文字列を得る。
     * デフォルトは「Re-」であるが、Behavior_Info.txtファイルで指定できる
     * @return 返信メールのSubjectにおいて元メールの前につける文字列  
     */
    public String getSubjectPrefix() {
        return subjPrefix;
    }

    /**
     * fetchmailでサーバあたり１回のセッションでダウンロードするメッセージ数の上限値
     * @return	   設定されている上限値 
     */
    public int getDownloadLimit() {
        return downloadLimit;
    }

    private int downloadLimit = 50;

    /**
     * fetchmailでダウンロードするメッセージ最大サイズ
     * @return サイズの上限値（バイト数）
     */
    public int getDownloadSizeLimit() {
        return downloadSizeLimit;
    }

    private int downloadSizeLimit = 0;

    /**
     * 送信メールで、一定バイト数ごとに改行を自動挿入するが、そのときのバイト数
     *
     * @return	   設定されているバイト数 
     */
    public int getOneLineLimit() {
        return oneLineLimit;
    }

    private int oneLineLimit = 76;

    /**
     * mailto:リンクのクリックや、アドレス帳からの選択で送信メールファイルを作成する時、
     * 宛先の名前の前につける文字列
     *
     * @return	   宛先の前につける文字列
     */
    public String getAddressNamePrefix() {
        return addNamePrefix;
    }

    private String addNamePrefix = "";

    /**
     * mailto:リンクのクリックや、アドレス帳からの選択で送信メールファイルを作成する時、
     * 宛先の名前の末尾につける文字列
     *
     * @return	   宛先の末尾につける文字列
     */
    public String getAddressNameSuffix() {
        return addNameSuffix;
    }

    private String addNameSuffix = "";

//    private boolean setUpByProp = false;

    private void initBehaviorInfoFile() {

     //   String propString;

        includeCC =			sysPrefHelper.getBoolean(	"net.msyk.ome.Behavior_Info.IncludeCC",			includeCC);
        includeToCC =		sysPrefHelper.getBoolean(	"net.msyk.ome.Behavior_Info.IncludeToCC",			includeToCC);
        forceAddressOnly =	sysPrefHelper.getBoolean(	"net.msyk.ome.Behavior_Info.ForceAddressOnly",	forceAddressOnly);
        ccSameAsTo =		sysPrefHelper.getBoolean(	"net.msyk.ome.Behavior_Info.CheckCCwithTo",		ccSameAsTo);
        //	RefilingOriginal = getBoolean("net.msyk.ome.Behavior_Info.DontSaveOriginalMessage",
        //				      RefilingOriginal);
        keepMessage =		sysPrefHelper.getBoolean(	"net.msyk.ome.Behavior_Info.KeepMailInServer",	keepMessage);
        smtpDebug =			sysPrefHelper.getBoolean(	"net.msyk.ome.Behavior_Info.SMTPDebugMode",		smtpDebug);
        fileNamePat =		sysPrefHelper.getString(	"net.msyk.ome.Behavior_Info.FileNamePattern",		fileNamePat);
        commandPat =		sysPrefHelper.getString(	"net.msyk.ome.Behavior_Info.CommentPattern",		commandPat);
        commHead =			sysPrefHelper.getString(	"net.msyk.ome.Behavior_Info.MessageCommentHead",	commHead);
        downloadLimit =		sysPrefHelper.getInt(		"net.msyk.ome.Behavior_Info.DownloadLimit",		downloadLimit);
        oneLineLimit =		sysPrefHelper.getInt(		"net.msyk.ome.Behavior_Info.OneLineBytes",		oneLineLimit);
        logingPriod =		sysPrefHelper.getString(	"net.msyk.ome.Behavior_Info.LogingPeriod",		logingPriod);
        htmlExpanding =		sysPrefHelper.getBoolean(	"net.msyk.ome.Behavior_Info.HTMLExpanding",		htmlExpanding);
        omeLocale =			sysPrefHelper.getLocale(	"net.msyk.ome.Behavior_Info.Locale",				omeLocale);

        if (sysPrefHelper.isSetUpByProp()) return;

        try {
            //		  File targetFile = new File(getOMEPref(),"Behavior_Info.txt");
            int fLen = (int) (targetFile.length());

            char[] buffer = new char[fLen];
            FileReader inFile = new FileReader(targetFile);
            inFile.read(buffer, 0, fLen);
            inFile.close();

            String fileContent = new String(buffer);
            parsePreferences(fileContent);
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }
        //	messageMakerClasses.add("PartExpander");
    }

    /**
     * Behavior_Info.txtのファイルの中身を、インスタンス変数に反映させる
     * @param prefStr 設定テキスト
     */
    public void parsePreferences(String prefStr) {

        includeCC = checkKeyword(prefStr, "IncludeCC", includeCC);
        includeBCC = checkKeyword(prefStr, "IncludeBCC", includeBCC);
        includeToCC = checkKeyword(prefStr, "IncludeToCC", includeToCC);
        forceAddressOnly = checkKeyword(prefStr, "ForceAddressOnly", forceAddressOnly);
        //	    AddPrefix = checkKeyword(fileContent, "NoPrefix", AddPrefix);
        //	    Fold31Chars = checkKeyword(fileContent, "LongFileNameOK", Fold31Chars);
        ccSameAsTo = checkKeyword(prefStr, "CheckCCwithTo", ccSameAsTo);
        //	    CodeAfterName = checkKeyword(fileContent, "CodeAfterName", CodeAfterName);
        //	    StayHere = checkKeyword(fileContent, "StayHere", StayHere);

        //	moveFiles = ! checkKeywordAbsolute(prefStr, "DontMoveFile");
        moveFiles = checkKeywordAbsolute(prefStr, "MoveFileByProcmail");
        eraseFiles = checkKeyword(prefStr, "EraseFile", eraseFiles);

        noSetFAttr = checkKeyword(prefStr, "NotSetFinderAttribute", noSetFAttr);
        //	    MoveIfError = checkKeyword(fileContent, "NoMoreErrorMoving", MoveIfError);
        //	    RefilingOriginal = checkKeyword(fileContent, "DontSaveOriginalMessage", RefilingOriginal);

        keepMessage = checkKeyword(prefStr, "KeepMailInServer", keepMessage);
        //	forceDisconnect = checkKeyword(prefStr, "disconnectAfterDownload", forceDisconnect);
        //	    WaitMailProcessing = checkKeyword(fileContent, "DontWaitMailProcessing", WaitMailProcessing);

        smtpDebug = checkKeyword(prefStr, "SMTPDebugMode", smtpDebug);
        fetchmailV = checkKeyword(prefStr, "FetchMailVerboseMode", fetchmailV);
        fetchSizeLimit = getIntParameterOfKeyword(prefStr, "FetchSizeLimit=", fetchSizeLimit);
        fetchmailAllDL = checkKeyword(prefStr, "FetchMailAllDownload", fetchmailAllDL);
        fetchmailUIDL = checkKeyword(prefStr, "FetchMailUIDL", fetchmailUIDL);
        htmlExpanding = checkKeyword(prefStr, "HTMLExpanding", htmlExpanding);
        useDraft = checkKeyword(prefStr, "UseDraft", useDraft);
        cleanRe = checkKeyword(prefStr, "DontCleanReInSubject", cleanRe);
        cleanBr = checkKeyword(prefStr, "DontCleanMLAdditionInSubject", cleanBr);
        insertFromToNewMail = checkKeyword(prefStr, "InsertFromToNewMail", insertFromToNewMail);
        replyCommStr = checkKeyword(prefStr, "ReplyCommentSupply", replyCommStr);
//        virusScaning = checkKeyword(prefStr, "VirusScaning", virusScaning);
        headerTextUnifying = checkKeyword(prefStr, "HeaderTextUnifying", headerTextUnifying);
        sendMailMessageShow = checkKeyword(prefStr, "SendMailMessageShow", sendMailMessageShow);
        downloadMailsMessageShow = checkKeyword(prefStr, "DownloadMailsMessageShow", downloadMailsMessageShow);
/*
        sendMailMessageStandardOutput = checkKeyword(prefStr, "SendMailMessageStandardOutput",
                sendMailMessageStandardOutput);
        downloadMailsMessageStandardOutput = checkKeyword(prefStr, "DownloadMailsMessageStandardOutput",
                downloadMailsMessageStandardOutput);
*/
        commentChainSupport = checkKeyword(prefStr, "SuppressCommentChaining", commentChainSupport);
        unReadAliasSupport = checkKeyword(prefStr, "SuppressUnreadAlias", unReadAliasSupport);
        fileNamePat = getParameterOfKeyword(prefStr, "FileNamePattern=", fileNamePat);
        commandPat = getParameterOfKeyword(prefStr, "CommentPattern=", commandPat);
        commHead = getParameterOfKeyword(prefStr, "MessageCommentHead=", commHead);
        removeMe = checkKeyword(prefStr, "RemoveToMe", removeMe);
        removeMeAll = checkKeyword(prefStr, "RemoveToMeAll", removeMeAll);
        force7bit = checkKeyword(prefStr, "NotForce7bit", force7bit);

        downloadLimit = getIntParameterOfKeyword(prefStr, "DownloadLimit=", downloadLimit);
        downloadSizeLimit = getIntParameterOfKeyword(prefStr, "DownloadSizeLimit=", downloadSizeLimit);
        oneLineLimit = getIntParameterOfKeyword(prefStr, "OneLineBytes=", oneLineLimit);
        ignoringDuration = (long)getIntParameterOfKeyword(prefStr, "IgnoringDuration=", (int)ignoringDuration);

        logingPriod = getParameterOfKeyword(prefStr, "LogingPeriod=", logingPriod);
        subjPrefix = getParameterOfKeyword(prefStr, "SubjectPrefix=", subjPrefix);

        addNamePrefix = getParameterOfKeyword(prefStr, "AddressNamePrefix=", addNamePrefix);
        addNameSuffix = getParameterOfKeyword(prefStr, "AddressNameSuffix=", addNameSuffix);

        omeLocale = getLocaleParameterOfKeyword(prefStr, "Locale=", omeLocale);
        messageMakerClasses = getVectorParameterOfKeyword(prefStr, "MessageMakerClasses=", messageMakerClasses);
        additionalHeaders = getVectorParameterOfKeyword(prefStr, "AdditionalHeaders=", additionalHeaders);
        fetchmailPath = getParameterOfKeyword(prefStr, "FetchMailPath=", fetchmailPath);
        omeApplicationFolder = getParameterOfKeyword(prefStr, "OMEApplicationsPath=", omeApplicationFolder);
        omeTempFolder = getParameterOfKeyword(prefStr, "TempFolderPath=", omeTempFolder);
        omeLogFolder = getParameterOfKeyword(prefStr, "LogFolderPath=", omeLogFolder);
		
        javaVMPath = getParameterOfKeyword(prefStr, "JavaVMPath=", javaVMPath);
        useKeyFileWithPassword = getParameterOfKeyword(prefStr, "UseKeyFileWithPassword=", useKeyFileWithPassword);
    }

    /**
     * textにkeyが含まれていたら、currentValueの否定を返す。
     *
     * @param text 全体の文字列
     * @param key text中で検索する文字列
     * @param currentValue 現在のboolean値
     * @return keyがtext内にあったらcurrentValueの否定。なければcurrentValue。
     */
    private boolean checkKeyword(String text, String key, boolean currentValue) {
        return (text.indexOf(key) >= 0) ? (!currentValue) : (currentValue);
    }

    /**
     * textにkeyが含まれていたら、trueを返す。含まれていなければfalseを返す。
     *
     * @param text 全体の文字列
     * @param key text中で検索する文字列
     * @return keyがtext内にあったらtrue。なければfalse。
     */
    private boolean checkKeywordAbsolute(String text, String key) {
        return (text.indexOf(key) >= 0) ? true : false;
    }

    private Locale getLocaleParameterOfKeyword(String aLine, String keyword, Locale def) {
        String value = getParameterOfKeyword(aLine, keyword, null);
        if (value == null) return def;
        return InternationalUtils.createLocaleFromString(value);
    }

    private int getIntParameterOfKeyword(String aLine, String keyword, int def) throws NumberFormatException {
        String value = getParameterOfKeyword(aLine, keyword, null);
        if (value == null) { return def; }
        return Integer.parseInt(value);
    }

    private String getParameterOfKeyword(String aLine, String keyword, String def) {
        int keyPos = aLine.indexOf(keyword);
        if (keyPos < 0) { return def; }
        int specifyStart = keyPos + keyword.length();
        char separator = aLine.charAt(specifyStart);
        specifyStart++;
        int sepcifyEnd = aLine.indexOf(separator, specifyStart);
        return new String(aLine.substring(specifyStart, sepcifyEnd));
    }

    /**
     * 配列表記（keyword={"xxxxx","xxxxx"}）からVectorを得る
     */
    private List<String> getVectorParameterOfKeyword(String aLine, String keyword, List<String> def) {
        int keyPos = aLine.indexOf(keyword);
        if (keyPos < 0) return def;
        int specifyStart = keyPos + keyword.length();
        specifyStart++;
        int sepcifyEnd = aLine.indexOf("}", specifyStart);
        if (sepcifyEnd <= specifyStart) { return def; }
        Vector<String> tempV = new Vector<String>();
        StringTokenizer tokens = new StringTokenizer(aLine.substring(specifyStart, sepcifyEnd), ", \"");
        while (tokens.hasMoreTokens()) {
            tempV.add(tokens.nextToken());
        }
        return tempV;
    }
}
