package OME;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.MimeUtility;
import OME.mailformatinfo.MailFormatInfo;

/** 
 * OME_PreferencesにあるMoving_Info.txtファイルをもとにして、メッセージファイルを作成するフォルダなどを決定する。
 * <p>次のような手順で利用するのを基本とする。<ol>
 * <li>getInstance()メソッドで、MovingInfoのインスタンスを作成する
 * <li>inspectMessageで、メールのソースを適用する。ここで、保存先などの情報はセットアップされる。
 * <li>保存先や起動プロセスを得る。</ol>
 * <p>メールの保存先をチェックするオブジェクトが1つあって、それに各メールを適用するといった雰囲気で使う。
 *	<p>シングルトンパターンに従っており、アプリケーション内ではインスタンスは必ず１つである。また、最初のgetInstanceの呼び出しがあるまでは、
 *	インスタンス化されない。すなわち、getInstanceを呼ぶまではMoving_Info.txtファイルの読み込みは行われない。
 *
 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 *    :
 * 2003/12/14:新居:absoluteの動作の修正、エンコードミスの修正、移動先がエイリアスの場合に対応
 * 2003/12/14:新居:ソースの余分なところを削除し、内部で使うものは内部クラスでprivateにするなど整理した
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
 *
 * @author 新居雅行（Masayuki Nii/msyk@msyk.net）
 */

public class MovingInfo {

    /** 受信フォルダ名の既定値。    */
    public static final String DEFAULT_MAIL_FOLDER = "InBox";

    /** 設定ファイル名の既定値。    */
    public static final String MOVING_INFO_FILENAME = "Moving_Info.txt";

    /** 新たにMovingInfoクラスのインスタンスを得る
     @return 生成されたインスタンスへの参照 */
    public static MovingInfo getInstance() {
        if (!isInstanciate) {
            mySelf = new MovingInfo();
            isInstanciate = true;
        }
        OMEPreferences omePref = OMEPreferences.getInstance();
        File infoFile = new File(omePref.getOMEPref(), MOVING_INFO_FILENAME);
        if (lastModified != infoFile.lastModified()) {
            mySelf.initMovingInfoFile();
            lastModified = infoFile.lastModified();
        }
        return mySelf;
    }

    /** inspectMessageメソッドを実行したかどうかを示す  */
    private static boolean isInstanciate = false;

    /** 生成したシングルトンのインスタンスを参照する変数    */
    private static MovingInfo mySelf = null;

    /** 設定ファイルの修正日を記憶する */
    private static long lastModified = -1;

    /** メールの保存先や起動プロセスの判定を行う。
     @param msg	判定を行うメッセージ	*/
    public void inspectMessage(Part msg) {

        isInspedted = true;

        storingFolder = null;
        firingProcess = null;
        movingOptions = null;

        MovingDef resultMD = new MovingDef(); //Moving_Infoからのどんな情報を適用するかを記録
        resultMD.movingRPath = DEFAULT_MAIL_FOLDER; //既定の保存フォルダを設定

        OMEPreferences omePref = OMEPreferences.getInstance();
        boolean isMatch = false;

        checkingloop: for (int hedaderIX = 0; hedaderIX < mmInfos.size(); hedaderIX++) {
            //定義にあるヘッダ情報を順番にチェック
            HeaderDef hd = (HeaderDef) mmInfos.get(hedaderIX);

            Enumeration e;
            try {
                e = msg.getAllHeaders(); //メールのヘッダを取得する
            } catch (Exception exp) {
                return;
            }

            while (e.hasMoreElements()) {
                Header h = (Header) e.nextElement(); //メールのヘッダの1つを取り出す
                String fieldNameInMail = h.getName();
                String fieldValueInMail = myDecodeString(convertOneLine(h.getValue())).toLowerCase();

                if (omePref.isCCSameAsTo() && (fieldNameInMail.equalsIgnoreCase("CC"))) fieldNameInMail = "To";

                if (hd.headerLabel.equalsIgnoreCase(fieldNameInMail)) {
                    //メールのヘッダと定義のヘッダが同じなら
                    for (int itemIX = 0; itemIX < hd.size(); itemIX++) {
                        MovingDef md = (MovingDef) hd.get(itemIX);
                        if ((md.criteria.equals("*")) || (fieldValueInMail.indexOf(md.criteria) >= 0)) {
                            isMatch = true;
                            if (!md.movingRPath.equals("")) {
                                resultMD.movingRPath = md.movingRPath;
                                resultMD.storingFolder = md.storingFolder;
                            }
                            if (!md.option.equals("")) resultMD.option = md.option;
                            if (!md.firingProcess.equals("")) if (resultMD.firingProcess.equals(""))
                                resultMD.firingProcess = md.firingProcess;
                            else
                                resultMD.firingProcess += "|" + md.firingProcess;
                            if ((!md.criteria.equals("*")) && (md.option.indexOf("continue") < 0)) break checkingloop;
                        }
                    }
                }
            }
        }

        if (resultMD.storingFolder == null) { //一致する設定がない場合、既定値をFile型にも反映
            resultMD.storingFolder = new File(OMEPreferences.getInstance().getOMERoot(), resultMD.movingRPath);
        }
        storingFolder = resultMD.storingFolder;

        Logging.writeMessage("メッセージを保存するフォルダ: " + resultMD.movingRPath);
        firingProcess = resultMD.firingProcess;
        movingOptions = resultMD.option;
    }

    /*  inspectMessageメソッドを実行した後、以下の変数に判定結果が設定される */
    private boolean isInspedted = false;

    private File storingFolder = null;

    private String firingProcess = null;

    private String movingOptions = null;

    /** メールのチェックを行ったかどうかを判定する。
     @return	判定を行ったかどうか	*/
    public boolean isInspected() {
        return isInspedted;
    }

    /** このメールの保存先フォルダを得る。
     @return	保存先フォルダ	*/
    public File getStoringFolder() {
        return storingFolder;
    }

    /** メールのファイル化を行った後に実行するプロセスを得る。
     @return	プロセスのファイルへの絶対パス	*/
    public String getFiringProcess() {
        return firingProcess;
    }

    /** Moving_Info.txtに記載され、条件に一致する定義についてのオプション情報を得る。
     @return	プロセスのファイルへの絶対パス	*/
    public String getMovingOptions() {
        return movingOptions;
    }

    /** Moving_Info.txtのオプションで、未読エイリアスを作らないという定義があるかどうか
     @return	作成するならtrue、しないならfalse	*/
    public boolean isDontMakeUnreadAlias() {
        return movingOptions.indexOf("NoUnreadAlias") < 0;
    }

    /** Moving_Info.txtの内容を展開するリンクリスト   */
    private static LinkedList mmInfos;

    /** Moving_Info.txtファイルの内容を読み出し、変数等に設定を行う。
     */
    private void initMovingInfoFile() { //Moving_Info.txt

        OMEPreferences omePref = OMEPreferences.getInstance();
        HeaderDef currentHeaderDef = null;
		byte buffer[] = new byte[10000];

        mmInfos = new LinkedList();
        try {
			String fileEncoding = MailFormatInfo.getInstance().getPrefFilesCode();
			LineInputStream inFile = new LineInputStream(new File(omePref.getOMEPref(), MOVING_INFO_FILENAME));
 //           LineNumberReader inFile = new LineNumberReader(new FileReader(new File(omePref.getOMEPref(),
 //                   MOVING_INFO_FILENAME)));
            boolean isEOF = false;
            while (!isEOF) {
                try {
					int byteCount = inFile.readLineNoNL(buffer);
					
					if ( byteCount < 0 )
						isEOF = true;
					else if ( byteCount < 2 )	{}	//行内には最低数バイトは必要、空行を含めて無視
                    else if (buffer[0] == '#')	{}	//#で始まる行も無視
                    else if (buffer[0] == '[')	{	//検索ヘッダ
    					for ( int i = 0 ; i < byteCount ; i++ )		{
							if (( buffer[i] == ']' ) || ( buffer[i] == ':' ))	{
								String item = new String( buffer, 1, i-1, fileEncoding );
								mmInfos.add( currentHeaderDef = new HeaderDef( item ) );
								break;
							}
						}
                    } else {
                        if (currentHeaderDef != null)	{
							String item = new String( buffer, 0, byteCount, fileEncoding );
                            currentHeaderDef.addMovingDef(item);
						}
                        else
                            Logging.writeMessage("%% OME Error 911 %% Format error in Moving_info.txt: ");
                    }
                } catch (Exception e) {
                    isEOF = true;
                }
            }
            inFile.close();

        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }
    }

    /** ヘッダの文字列のデコードを行う。文字列の途中でMIMEでコードがあっても対処している。
     実際のデコード処理は、JavaMailのMIMEUtilityを使っている。
     @param str ヘッダのフィールドに対する値
     @return デコードした結果の文字列    */
    private String myDecodeString(String str) {

//        System.out.println("#######myDecodeString:" + str);

        int pointer = 0;
        int encStart = 0, encEnd = 0;
        StringBuffer sb = new StringBuffer("");
        while ((encStart = str.indexOf("=?", pointer)) >= 0) { //エンコードの最初
            encEnd = str.indexOf("?=", encStart); //エンコードの最後を求める
            if (encEnd < 0) { //エンコードの最後がない場合
                break;
            }
            sb.append(str.substring(pointer, encStart));

//            System.out.println("#######MimeUtility.decodeText:" + str.substring(encStart, encEnd + 2));
            try {
                sb.append(MimeUtility.decodeText(str.substring(encStart, encEnd + 2)));
            } catch (Exception e) { //デコードがエラーした場合はそのまま
                sb.append(str.substring(encStart, encEnd + 2));
            }
            pointer = encEnd + 2;
        }
        sb.append(str.substring(pointer, str.length()));
        return sb.toString();
    }

    /** ヘッダを１行にまとめるためのサブルーチン
     @param s 数行になった文字列
     @return 1行にまとめた文字列    */
    private String convertOneLine(String s) {
        StringBuffer sb = new StringBuffer(255);
        StringTokenizer st = new StringTokenizer(s, "\r\n");
        boolean isFirstLine = true;
        while (st.hasMoreTokens()) {
            if (isFirstLine)
                sb.append(st.nextToken());
            else
                sb.append(st.nextToken().substring(1));
            isFirstLine = false;
        }
        return sb.toString();
    }

    /** Moving_Info.txtファイルの内容をこのクラスのオブジェクトに展開したときに
     その結果を調査するためのメソッド。
     @return オブジェクトの中身をダンプした文字列  */
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        for (int headerIX = 0; headerIX < mmInfos.size(); headerIX++) {
            HeaderDef hd = (HeaderDef) (mmInfos.get(headerIX));
            sb.append(hd.headerLabel + "\\n");
            for (int itemIX = 0; itemIX < hd.size(); itemIX++) {
                MovingDef md = (MovingDef) hd.get(itemIX);
                sb.append("-------------------------------\n");
                sb.append("Criteria : " + md.criteria + "\n");
                sb.append("Path : " + md.movingRPath + "\n");
                sb.append("RealPath : " + md.storingFolder.getPath() + "\n");
                sb.append("Option : " + md.option + "\n");
                sb.append("Process : " + md.firingProcess + "\n");
            }
        }
        return sb.toString();
    }

    /* 内部クラスに変更した（2003/12/14：新居）    */
    /** 振り分けに関する情報を管理するクラス。Moving_Info.txtの[フィールド名:]のひとかたまりを
     ひとつのオブジェクトとしてリンクリストとして管理する。
     */
    private class HeaderDef extends LinkedList {

        private String headerLabel; //ヘッダのフィールド名、つまり[フィールド名:]の中身

        /** コンストラクタ。新たな[フィールド名:]が登場したときに呼び出す。
         @param hStr フィールド名の文字列  */
        public HeaderDef(String hStr) {
            super();
            headerLabel = hStr;
        }

        /** 新しい振り分けに関する定義を追加する
         @param info Moving_Info.txtファイルの1行分の文字列を指定する。   */
        public void addMovingDef(String info) {
            MovingDef md = new MovingDef();
            md.setMovingDef(info);
            this.add(md);
        }
    }

    /* 内部クラスに変更した（2003/12/14：新居）    */
    /** 振り分けに関する情報を管理するクラス。Moving_Info.txtの1行ずつを
     ひとつのオブジェクトとしてリンクリストとして管理する。つまり、HeaderDefクラスのオブジェクト１つに
     対して、このMovingDefクラスのオブジェクトがいくつか存在する。
     */
    class MovingDef {

        /* メンバ変数をそのまま参照するためにpublicとした   */
        public String criteria = "";

        public String movingRPath = "";

        public String option = "";

        public String firingProcess = "";

        public File storingFolder = null;

        /*  文字列の指定位置からカンマのある場所を探すが、ダブルクォートと{}の間にあるカンマは無視する
         @param  str    調べる文字列
         @param startPosition ここで指定した位置から後を調べる
         @return カンマの位置、見つからない場合は-1を戻す  */
        private int searchNextDelimiter(String str, int startPosition) {

            for (int i = startPosition; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c == ',') return i;
                if (c == '"') do {
                    i++;
                    if (i >= str.length()) return -1;
                } while (str.charAt(i) != '"');
                if (c == '{') do {
                    i++;
                    if (i >= str.length()) return -1;
                } while (str.charAt(i) != '}');
            }
            return -1;
        }

        /*  Moving_Info.txtファイルの1行分のテキストから、オブジェクトに展開する。
         @param  info    設定ファイルのテキスト1行分  */
        public void setMovingDef(String info) {
            int lenInfo = info.length();

            int firstSep = info.indexOf(",");
            if (firstSep <= 0) {
            	firstSep = lenInfo;
            }

            int secondSep = info.indexOf(",", firstSep + 1);
            if (secondSep <= firstSep) {
            	secondSep = lenInfo;
            }

            int thirdSep = searchNextDelimiter(info, secondSep + 1);
            if (thirdSep <= secondSep) {
            	thirdSep = lenInfo;
            }

            try {
                criteria = info.substring(0, firstSep).toLowerCase();
                if (secondSep > firstSep) {
                	movingRPath = info.substring(firstSep + 1, secondSep);
                }
                if (movingRPath.length() > 0) {
                    StringBuffer sb = new StringBuffer("");
                    StringTokenizer pathToken = new StringTokenizer(movingRPath, ":");
                    while (pathToken.hasMoreTokens()) {
                        String curItemName = pathToken.nextToken();
                        for (int i = 0; i < curItemName.length(); i++) {
                            char oneChar = curItemName.charAt(i);
                            if (oneChar == '/')
                                sb.append("%2f");
                            else
                                sb.append(oneChar);
                        }
                        sb.append("/");
                    }
                    movingRPath = sb.toString();
                }
                if (thirdSep > secondSep) option = info.substring(secondSep + 1, thirdSep);
                if (lenInfo > thirdSep) firingProcess = info.substring(thirdSep + 1, lenInfo);

                if (option.indexOf("absolute") < 0)
                    storingFolder = new File(OMEPreferences.getInstance().getOMERoot(), movingRPath);
                else
                    storingFolder = new File("/Volumes/" + movingRPath);
                if (!storingFolder.exists()) storingFolder.mkdirs();
                //Logging.writeMessage("#1# : "+ storingFolder);
                storingFolder = MacAlias.resolveMacAlias(storingFolder);
                //Logging.writeMessage("#2# : "+ storingFolder);
            } catch (Exception e) {
                Logging.writeMessage("%% OME Error 912 %% Error on parsing Moving_info.txt: " + e.getMessage());
            }
        }
    }
}
