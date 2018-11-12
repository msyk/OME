/** 
 * Growlに表示する日本語のメッセージ

 * <hr>
 * <h2>OME更新履歴</h2>
 * <pre>
 * 2007/1/7:新居:開発スタート
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
 */
package OME.messagemaker;

import java.util.ListResourceBundle;
import OME.messagemaker.GrowlNotify;

public class GrowlNotify_ja extends ListResourceBundle {

	public Object[][] getContents()	{	return contents;	}

	static final Object[][] contents = {
		{	GrowlNotify.NOTFIY_NAME_DOWNLOADED,
				"OMEは@@1@@通のメールをダウンロードしました。送信者は以下の通りです。"},
		{	GrowlNotify.NOTFIY_NAME_UNREAD, 
				"現在、@@1@@通の未読メールがあります。"},
		{	GrowlNotify.NOTFIY_NAME_SENT, 
				"メールの送信ができました。"}
	};

}
