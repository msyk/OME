package OME.mailformatinfo;

import java.io.File;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import OME.*;

/** 
 * <p>メールの国際化にかかわる各種設定を管理する。
 * <p>シングルトンパターンで動作している。getInstanceメソッドでインスタンスを取得し、get*メソッドで必要な情報を得るのが基本的な使い方である。
 * <p>言語ごとの情報、たとえば、日本語だと本文はISO-2022-JPにするとかいった情報は、characterset.xml
 * というファイルに記述する。このファイルは、まず、OME_Preferencesにあれば、それを使う。もしないなら、
 * フレームワークのResourcesにあればそれを使う。全くない場合はあらゆるエンコーディング情報をUTF-8として戻す。
 * <p>characterset.xmlは以下のような形式で、ome-localeタグで、言語情報を追加する事が可能である。
 * ome-localeエレメントのname属性が、ロケールを示す文字列となる。子要素の意味については表にまとめた。
 * </p>
<!--
 for programmers
 <?xml version="1.0" encoding="UTF-8"?>
   <ome-character-set>
    <ome-locale name="ja_JP">
      <sending-body-code>ISO-2022-JP</sending-body-code>
      <sending-body-encode />
      <sending-header-code>ISO-2022-JP</sending-header-code>
      <sending-header-encode>B</sending-header-encode>
      <uploadfile-code>Shift_JIS</uploadfile-code>
      <template-file-code>Shift_JIS</template-file-code>
      <template-insert-code>Shift_JIS</template-insert-code>
      <preffiles-code>Shift_JIS</preffiles-code>
      <mail-file-code>Shift_JIS</mail-file-code>
      <mail-converter>OME.mailformatinfo.TextConverter_ja_JP</mail-converter>
      <mail-source-defalut-code>JIS</mail-source-defalut-code>
    <charset-in-type>ISO-2022-JP</charset-in-type>
   </ome-locale>
 </ome-character-set>
-->
<!-- for javadoc -->
 <pre>
 &lt;?xml version="1.0" encoding="UTF-8"?&gt;
   &lt;ome-character-set&gt;
    &lt;ome-locale name="ja_JP"&gt;
      &lt;sending-body-code&gt;ISO-2022-JP&lt;/sending-body-code&gt;
      &lt;sending-body-encode /&gt;
      &lt;sending-header-code&gt;ISO-2022-JP&lt;/sending-header-code&gt;
      &lt;sending-header-encode&gt;B&lt;/sending-header-encode&gt;
      &lt;uploadfile-code&gt;Shift_JIS&lt;/uploadfile-code&gt;
      &lt;template-file-code&gt;Shift_JIS&lt;/template-file-code&gt;
      &lt;template-insert-code&gt;Shift_JIS&lt;/template-insert-code&gt;
      &lt;preffiles-code&gt;Shift_JIS&lt;/preffiles-code&gt;
      &lt;mail-file-code&gt;Shift_JIS&lt;/mail-file-code&gt;
      &lt;mail-converter&gt;OME.mailformatinfo.TextConverter_ja_JP&lt;/mail-converter&gt;
      &lt;mail-source-defalut-code&gt;JIS&lt;/mail-source-defalut-code&gt;
    &lt;charset-in-type&gt;ISO-2022-JP&lt;/charset-in-type&gt;
   &lt;/ome-locale&gt;
 &lt;/ome-character-set&gt;
 </pre>
 <table border=1>
 <tr><th>要素名</th><th>値の例</th><th>意味</th></tr>
 <tr><td>sending-body-code</td><td>ISO-2022-JP</td>
 <td>送信するメールそのものの本文の文字セット</td></tr>
 <tr><td>sending-body-encode<</td><td></td>
 <td>送信するメールそのものの本文に対してエンコードをするかどうか。
 ここでのエンコードは、BかQか、あるいは何も指定しないかのいずれか。</td></tr>
 <tr><td>sending-header-code</td><td>ISO-2022-JP</td>
 <td>送信するメールそのもののヘッダ部分の文字セット</td></tr>
 <tr><td>sending-header-encode</td><td>B</td>
 <td>送信するメールそのもののヘッダ部分のエンコードをするかどうか。前述を参考に。</td></tr>
 <tr><td>uploadfile-code</td><td>Shift_JIS</td>
 <td>送信メールファイル（*.wmailファイル）の中身の記述に使われている文字セット</td></tr>
 <tr><td>template-file-code</td><td>Shift_JIS</td>
 <td>テンプレートファイル（*.tempmailファイル）の中身の記述に使われている文字セット</td></tr>
 <tr><td>template-insert-code</td><td>Shift_JIS</td>
 <td>差し込みメールの場合の差し込まれるデータの文字セット</td></tr>
 <tr><td>preffiles-code</td><td>Shift_JIS</td>
 <td>OME_Preferencesフォルダにあるファイルの中身の記述に使われている文字セット</td></tr>
 <tr><td>mail-file-code</td><td>Shift_JIS</td>
 <td>受信したメールを保存するファイル（*.ygm,*.mail,*.rplyファイル）の文字セット</td></tr>
 <tr><td>mail-converter</td><td>OME.mailformatinfo.TextConverter_ja_JP</td>
 <td>受信したメールの文字列をコンバートするクラス</td></tr>
 <tr><td>mail-source-defalut-code</td><td>JIS</td>
 <td>メールに文字セットが設定されていない場合、ロケールに対してこの文字セッであると解釈される</td></tr>
 <tr><td>charset-in-type</td><td>ISO-2022-JP</td>
 <td>メールのcharset属性がこの文字セットであれば、このロケールであると解釈する</td></tr>
 </table>
 *
 * <p>ロケールを示す文字列は、ome-locale要素のname属性だが、これは、Javaの国際化機能に応じた仕様に従うものとする。
 * つまり、言語、国、バリアントをアンダースコアで結ぶというのが基本。
 * ただし、指定したロケールの定義が見つからない場合、後ろから要素をのぞいたロケールに対して存在するかどうかを調べる。
 * いっさいの適合するロケールがない場合は、文字セットはUTF-8、エンコードは「なし」が戻される。</p>
 *
 * <p>言語の追加は、characterset.xmlに追加すればよい。なお、ISO-8859のように１つの文字セットが複数のロケールに対応する場合がある。
 * 具体的には、de_DEも、fr_FRもISO-8859-1である。このとき、リストの前にあるものが優先されるので
 * 言語に応じて優先順位をcharacterset.xmlの中で移動させておく必要がある。
 * なお、de_DEが前にある場合、フランスから送られたメールも「de_DE」と認識して処理を行うが、これは現状では解決できない。
 * 特に問題は薄いと思われるので、このまま運用する事にする。</>
 <p>参考<br>
 Java Internationalization: An Overview<br>
 http://developer.java.sun.com/developer/technicalArticles/Intl/IntlIntro/<br></p>

 *
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 *		:
 * 2004/3/22:新居:言語情報をクラスで与えていたのから、XMLファイルベースに転換した。
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
 */

public class MailFormatInfo /* extends ListResourceBundle */{

    /** 
     * 新たにMailFormatInfoクラスのインスタンスを得る
     * 
     * @return 生成されたインスタンスへの参照 
     * */
    public static MailFormatInfo getInstance() {
        if (mySelf == null) {
            mySelf = new MailFormatInfo();
            mySelf.initializeByCharactersetFile();
        }
        return mySelf;
    }

    private static MailFormatInfo mySelf = null;

    private MailFormatInfo() {}

    /** キャラクタセットの情報XMLファイルの中身と等価なDOMオブジェクト */
    private Document charsetDOM = null;

    /** ロケールの文字列を手がかりにして、DOMの特定のノードとを結びつけるマップ */
    private Map charsetMap = null;

    /** キャラクタセットからロケールを指定するためのマップ */
    private Map localeMap = null;

    private void initializeByCharactersetFile() {

        charsetMap = new HashMap();
        localeMap = new HashMap();

        File charctersetFiles[] = { new File(OMEPreferences.getInstance().getOMEPref(), "characterset.xml"),
                new File(OMEPreferences.getInstance().getOMEToolsFolder(), "characterset.xml")};
        try {
            charsetDOM = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    MacFile.getInstance(charctersetFiles));
            Element rootElement = charsetDOM.getDocumentElement();
            NodeList localeNodeList = rootElement.getElementsByTagName("ome-locale");
            for (int i = 0; i < localeNodeList.getLength(); i++) {
                Element targetNode = (Element) (localeNodeList.item(i));
                String localeName = targetNode.getAttribute("name");
                charsetMap.put(localeName, targetNode);

                String charsetInType = null;
                try {
                    charsetInType = targetNode.getElementsByTagName("charset-in-type").item(0).getFirstChild()
                            .getNodeValue();
                } catch (Exception e) { /* do nothing */}
                localeMap.put(charsetInType, InternationalUtils.createLocaleFromString(localeName));
            }
        } catch (javax.xml.parsers.ParserConfigurationException pcEx) {
            //parse()
            pcEx.printStackTrace();
        } catch (org.xml.sax.SAXException saxEx) {
            //parse()
            saxEx.printStackTrace();
        } catch (java.io.IOException ioEx) {
            //parse()
            ioEx.printStackTrace();
        }
    }

    private String getKeyValueLocale(String key, Locale loc, String defaultValue) {
        String language = loc.getLanguage();
        String country = loc.getCountry();
        String variant = loc.getVariant();
        Object charsetInfo = charsetMap.get(language + "_" + country + "_" + variant);
        if (charsetInfo == null) {
            charsetInfo = charsetMap.get(language + "_" + country);
            if (charsetInfo == null) {
                charsetInfo = charsetMap.get(language);
                if (charsetInfo == null) return defaultValue;
            }
        }

        try {
            return ((Element) charsetInfo).getElementsByTagName(key).item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    //////////////////////////////////////////////////////////////////////
    /**	
     * キャラクタセットからそのメールを扱うロケールを求める<p>変換テーブルは、このクラスの中のprivate配列
     *	charsetLocaleTableに記録させているだけなので、追加する場合にはソースの修正が必要。
     *	@param cset キャラクタセットを示す文字列（ex "ISO-2022-JP"）
     *	@return そのキャラクタセットから推測されるロケール
     */
    public Locale getMailSourceCharset(String cset) {
        Locale result = (Locale) localeMap.get(cset);
        if (result == null) result = Locale.getDefault();
        return result;
    }

    //////////////////////////////////////////////////////////////////////
    /**	
     * SMTPサーバに送るメール本文のコード体系を得る（デフォルトのロケール）
     *	@return コード体系を示す文字列（エンコーディング名：ex "JIS"）
     */
    public String getSendingBodyCode() {
        return getSendingBodyCode(Locale.getDefault());
    }

    /**	
     * 指定したロケールにおいてSMTPサーバに送るメール本文のコード体系を得る
     *	@param key	ロケールを指定
     *	@return コード体系を示す文字列（エンコーディング名：ex "JIS"）
     */
    public String getSendingBodyCode(Locale loc) {
        return getKeyValueLocale("sending-body-code", loc, "UTF-8");
    }

    /**
     * 	指定したロケールにおいてSMTPサーバに送るメール本文のコード体系を得る（デフォルトのロケール）
     *	@param locString　ロケールを指定する文字列
     *	@return コード体系を示す文字列（エンコーディング名：ex "JIS"）
     */
    public String getSendingBodyCode(String locString) {
        return getSendingBodyCode(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**
     * 	SMTPサーバに送るメール本文のエンコーディング（デフォルトのロケール）
     *	@return エンコード方法を示す文字列（エンコーディング名：ex "B"）
     */
    public String getSendingBodyEncode() {
        return getSendingBodyEncode(Locale.getDefault());
    }

    /**
     * 	指定したロケールにおいてSMTPサーバに送るメール本文のエンコーディング
     *	@param key	ロケールを指定
     *	@return エンコード方法を示す文字列（エンコーディング名：ex "B"）
     */
    public String getSendingBodyEncode(Locale loc) {
        return getKeyValueLocale("sending-body-encode", loc, "");
    }

    /**	
     * 指定したロケールにおいてSMTPサーバに送るメール本文のエンコーディング
     *	@param locString　ロケールを指定する文字列
     *	@return エンコード方法を示す文字列（エンコーディング名：ex "B"）
     */
    public String getSendingBodyEncode(String locString) {
        return getSendingBodyEncode(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**	
     * SMTPサーバに送るヘッダテキストのコード体系（デフォルトのロケール）
     *	@return コード体系を示す文字列（エンコーディング名：ex "JIS"）
     */
    public String getSendingHeaderCode() {
        return getSendingHeaderCode(Locale.getDefault());
    }

    /**	指定したロケールにおいてSMTPサーバに送るヘッダテキストのコード体系
     *	@param key	ロケールを指定
     *	@return コード体系を示す文字列（エンコーディング名：ex "JIS"）
     */
    public String getSendingHeaderCode(Locale loc) {
        return getKeyValueLocale("sending-header-code", loc, "UTF-8");
    }

    /**	
     * 指定したロケールにおいてSMTPサーバに送るヘッダテキストのコード体系
     *	@param locString　ロケールを指定する文字列
     *	@return コード体系を示す文字列（エンコーディング名：ex "JIS"）
     */
    public String getSendingHeaderCode(String locString) {
        return getSendingHeaderCode(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**	
     * SMTPサーバに送るヘッダテキストのエンコーディング（デフォルトのロケール）
     *	@return エンコード方法を示す文字列（エンコーディング名：ex "B"）
     */
    public String getSendingHeaderEncode() {
        return getSendingHeaderEncode(Locale.getDefault());
    }

    /**	
     * 指定したロケールにおいてSMTPサーバに送るヘッダテキストのエンコーディング
     *	@param key	ロケールを指定
     *	@return エンコード方法を示す文字列（エンコーディング名：ex "B"）
     */
    public String getSendingHeaderEncode(Locale loc) {
        return getKeyValueLocale("sending-header-encode", loc, "");
    }

    /**	
     * 指定したロケールにおいてSMTPサーバに送るヘッダテキストのエンコーディング
     *	@param locString　ロケールを指定する文字列
     *	@return エンコード方法を示す文字列（エンコーディング名：ex "B"）
     */
    public String getSendingHeaderEncode(String locString) {
        return getSendingHeaderEncode(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**	
     * 送信メールファイル（Outboxフォルダに作られる）のコード体系（デフォルトのロケール）
     *	@return エンコード方法を示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getUploadFileCode() {
        return getUploadFileCode(Locale.getDefault());
    }

    /**	
     * 指定したロケールにおいて送信メールファイル（Outboxフォルダに作られる）のコード体系
     *	@param key	ロケールを指定
     *	@return エンコード方法を示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getUploadFileCode(Locale loc) {
        return getKeyValueLocale("uploadfile-code", loc, "UTF-8");
    }

    /**	
     * 指定したロケールにおいて送信メールファイル（Outboxフォルダに作られる）のコード体系
     *	@param locString　ロケールを指定する文字列
     *	@return エンコード方法を示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getUploadFileCode(String locString) {
        return getUploadFileCode(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**	OME_MailWriterが読み込むテンプレートファイルの文字コード（デフォルトのロケール）
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getTemplateFileCode() {
        return getTemplateFileCode(Locale.getDefault());
    }

    /**	
     * 指定したロケールにおいてOME_MailWriterが読み込むテンプレートファイルの文字コード
     *	@param key	ロケールを指定
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getTemplateFileCode(Locale loc) {
        return getKeyValueLocale("template-file-code", loc, "UTF-8");
    }

    /**	指定したロケールにおいてOME_MailWriterが読み込むテンプレートファイルの文字コード
     *	@param locString　ロケールを指定する文字列
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getTemplateFileCode(String locString) {
        return getTemplateFileCode(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**	OME_MailWriterに読み込ませる差し込みファイルの文字コード（デフォルトのロケール）
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getTeplateInsertCode() {
        return getTeplateInsertCode(Locale.getDefault());
    }

    /**	
     * 指定したロケールにおいてOME_MailWriterに読み込ませる差し込みファイルの文字コード
     *	@param key	ロケールを指定
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getTeplateInsertCode(Locale loc) {
        return getKeyValueLocale("template-insert-code", loc, "UTF-8");
    }

    /**	
     * 指定したロケールにおいてOME_MailWriterに読み込ませる差し込みファイルの文字コード
     *	@param locString　ロケールを指定する文字列
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getTeplateInsertCode(String locString) {
        return getTeplateInsertCode(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**	OME設定ファイルのエンコード（デフォルトのロケール）
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getPrefFilesCode() {
        return getPrefFilesCode(Locale.getDefault());
    }

    /**	指定したロケールにおいてOME設定ファイルのエンコード
     *	@param key	ロケールを指定
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getPrefFilesCode(Locale loc) {
        return getKeyValueLocale("preffiles-code", loc, "UTF-8");
    }

    /**	
     * 指定したロケールにおいてOME設定ファイルのエンコード
     *	@param locString　ロケールを指定する文字列
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getPrefFilesCode(String locString) {
        return getPrefFilesCode(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**	
     * 受信したメールファイルの文字コード（デフォルトのロケール）
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getMailFileCode() {
        return getMailFileCode(Locale.getDefault());
    }

    /**	指定したロケールにおいて受信したメールファイルの文字コード
     *	@param key	ロケールを指定
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getMailFileCode(Locale loc) {
        return getKeyValueLocale("mail-file-code", loc, "UTF-8");
    }

    /**	
     * 指定したロケールにおいて受信したメールファイルの文字コード
     *	@param locString　ロケールを指定する文字列
     *	@return 文字コードを示す文字列（エンコーディング名：ex "SJIS"）
     */
    public String getMailFileCode(String locString) {
        return getMailFileCode(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**	
     * 受信したメールの文字列変換を行うクラス（デフォルトのロケール）
     *	@return クラス名
     */
    public String getMailConverter() {
        return getMailConverter(Locale.getDefault());
    }

    /**	
     * 指定したロケールにおいて受信したメールの文字列変換を行うクラス
     *	@param key	ロケールを指定
     *	@return クラス名
     */
    public String getMailConverter(Locale loc) {
        return getKeyValueLocale("mail-converter", loc, "UTF-8");
    }

    /**	
     * 指定したロケールにおいて受信したメールの文字列変換を行うクラス
     *	@param locString　ロケールを指定する文字列
     *	@return クラス名
     */
    public String getMailConverter(String locString) {
        return getMailConverter(InternationalUtils.createLocaleFromString(locString));
    }

    //////////////////////////////////////////////////////////////////////
    /**	
     * 受信メールのエンコードが不記載の場合のソースの文字コード解釈（デフォルトのロケール）
     *	@return 文字コードを示す文字列（エンコーディング名：ex "JIS"）
     */
    public String getMailSourceDefaultCode() {
        return getMailSourceDefaultCode(Locale.getDefault());
    }

    /**	
     * 指定したロケールにおいて受信メールのエンコードが不記載の場合のソースの文字コード解釈
     *	@param key	ロケールを指定
     *	@return 文字コードを示す文字列（エンコーディング名：ex "JIS"）
     */
    public String getMailSourceDefaultCode(Locale loc) {
        return getKeyValueLocale("mail-source-defalut-code", loc, "UTF-8");
    }

    /**	
     * 指定したロケールにおいて受信メールのエンコードが不記載の場合のソースの文字コード解釈
     *	@param locString　ロケールを指定する文字列
     *	@return 文字コードを示す文字列（エンコーディング名：ex "JIS"）
     */
    public String getMailSourceDefaultCode(String locString) {
        return getMailSourceDefaultCode(InternationalUtils.createLocaleFromString(locString));
    }
}
