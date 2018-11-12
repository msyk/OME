package OME.messagemaker;

import OME.CommandExecuter;
import OME.OMEPreferences;
//import com.growl.Growl;
import java.util.ResourceBundle;
import java.util.ListResourceBundle;

/** 
 * Growlを使ってメールの到着などを知らせるためのクラス

	GrowlのJavaのソースは、OME_JavaProjectの中に含めてしまっているので、そのまま呼び出せる。
	このクラスはユーティリティクラス的なもので、スタティックなメソッドを呼び出すようになっている。
	すべてのメソッドは、Growlが稼働しているかどうかをチェックしてから動かすので、
	Growlが動いていない、あるいはインストールしていないときには原則として何もしない。
	
 * <hr>
 * <h2>OME更新履歴</h2>
 * <pre>
 * 2007/1/4:新居:開発スタート
 * 2009/6/28:新居:OME_JavaCore2へ移動、GrowlがCocoaJavaを前提にしておりSnow Leopardで動かないため、機能をとりあえず全部殺した
 * </pre>
 $Revision: 1.1 $
 */

public class GrowlNotify extends ListResourceBundle {

	static private String applicationName = "OME";
	static private String imageInMessage = "/Library/Frameworks/OME.framework/Resources/icon-ome.tif";

	static String NOTFIY_NAME_DOWNLOADED = "Download Mails";
	static String NOTFIY_NAME_SENT = "Sent Mails";
	static String NOTFIY_NAME_UNREAD = "Unread Mails";
	static String NOTFIY_ERROR_MSG = "Error Message";
	
	static private String[] myAllowdNotifies 
							=  {NOTFIY_NAME_DOWNLOADED, NOTFIY_NAME_SENT, NOTFIY_NAME_UNREAD, NOTFIY_ERROR_MSG};
	static private String[] myDefaultNotifies 
							=  {NOTFIY_NAME_DOWNLOADED, NOTFIY_NAME_SENT, NOTFIY_NAME_UNREAD, NOTFIY_ERROR_MSG};
	
	static private StringBuffer storedMessage = new StringBuffer("");
	static private String messageSeparator = ", ";
	
	public Object[][] getContents()	{	return contents;	}
	
	static final Object[][] contents = {
		{	NOTFIY_NAME_DOWNLOADED, 
				"OME has downloaded @@1@@ mail@@2@@ from following sender@@2@@."},
		{	NOTFIY_NAME_UNREAD, 
				"You have @@1@@ unread mail@@2@@."},
		{	NOTFIY_NAME_SENT, 
				"You have sent a mail."}
	};
	
	/**
		残しているメッセージをクリアする
	*/
	static public void clearMessage()	{
		storedMessage = new StringBuffer("");
	}
	/**
		残すメッセージを追加するごとに、メッセージの前に付加する文字列を指定する。つまり、区切りの文字列。ただし、既定値は", "になっている。
		@param str メッセージを区切る文字列
	*/
	static public void setMessageSeparator( String str)	{
		messageSeparator = str;
	}
	
	/**
		メッセージを残す
		@param str 追加するメッセージ
	*/
	static public void appendMessage( String str )	{
		if ( storedMessage.length() > 0 )
			storedMessage.append( messageSeparator );
		storedMessage.append( str );
	}
	
	private static String replaceStringParam( String str, String[] replaecedArray )	{
		String searching;
		StringBuffer convStr = new StringBuffer( str );
		for ( int i = 0 ; i<replaecedArray.length ; i++)	{
			searching = "@@" + String.valueOf(i+1) + "@@";
			int pos = convStr.indexOf( searching );
			while ( pos >= 0 )	{
				convStr.replace( pos, pos+searching.length(), replaecedArray[i] );
				pos = convStr.indexOf( searching );
			}
		}
		return convStr.toString();
	}
	
	static public void sendErrorMessage( String str )	{
		if ( isNotGrowlRunnning() )	return;

/*		Growl target = new Growl( applicationName, imageInMessage );
		try	{
			target.setAllowedNotifications( myAllowdNotifies );
			target.setDefaultNotifications( myDefaultNotifies );
			target.register();
			target.notifyGrowlOf( 
				NOTFIY_ERROR_MSG, applicationName, 
				str);
		} catch(Exception e)	{
			System.out.println(e);
		}	
*/	}
	/**
		メールをダウンロードしたときの通知を表示するメソッド。メッセージはクリックするまで表示される。
		@param countMessages ダウンロードしたメッセージの数を渡す
	*/
	static public void sendDownloadedMessage( int countMessages )	{
	
		if ( isNotGrowlRunnning() )	return;

/*		Growl target = new Growl( applicationName, imageInMessage );
		try	{
			target.setAllowedNotifications( myAllowdNotifies );
			target.setDefaultNotifications( myDefaultNotifies );
			target.register();
			String description = 
				ResourceBundle
					.getBundle( "OME.messagemaker.GrowlNotify", OMEPreferences.getInstance().getOMELocale() )
						.getString( NOTFIY_NAME_DOWNLOADED );
			String[] repStr = { String.valueOf( countMessages ), (countMessages>1)?"s":"" };
			description = replaceStringParam( description,  repStr );
			target.notifyGrowlOf( 
				NOTFIY_NAME_DOWNLOADED, applicationName, 
				description + "\n\n" + storedMessage, 
				true);
		} catch(Exception e)	{
			System.out.println(e);
		}
		clearMessage();
*/	}

	/**
		未読メールがあることを通知を表示するメソッド
		@param countUnreadMessages 未読メッセージの数を渡す
	*/
	static public void sendUnreadMessage( int countUnreadMessages )	{
	
		if ( isNotGrowlRunnning() )	return;

/*		Growl target = new Growl( applicationName, imageInMessage );
		try	{
			target.setAllowedNotifications( myAllowdNotifies );
			target.setDefaultNotifications( myDefaultNotifies );
			target.register();
			String description = ResourceBundle
					.getBundle( "OME.messagemaker.GrowlNotify", OMEPreferences.getInstance().getOMELocale() )
						.getString( NOTFIY_NAME_UNREAD );
			String[] repStr = { String.valueOf( countUnreadMessages ), (countUnreadMessages>1)?"s":"" };
			description = replaceStringParam( description,  repStr );
			target.notifyGrowlOf( NOTFIY_NAME_UNREAD, applicationName, description);
		} catch(Exception e)	{
			System.out.println(e);
		}
*/	}

	/**
		メールが送信できたことを通知するメッセージを表示する
	*/
	static public void sendSentMessage()	{
	
		if ( isNotGrowlRunnning() )	return;
		
/*		Growl target = new Growl( applicationName, imageInMessage );
		try	{
			target.setAllowedNotifications( myAllowdNotifies );
			target.setDefaultNotifications( myDefaultNotifies );
			target.register();
			String description = ResourceBundle
					.getBundle( "OME.messagemaker.GrowlNotify", OMEPreferences.getInstance().getOMELocale() )
						.getString( NOTFIY_NAME_SENT );
			target.notifyGrowlOf( NOTFIY_NAME_SENT, applicationName, description);
		} catch(Exception e)	{
			System.out.println(e);
		}
*/	}
	
	/**
		Growlが稼働しているかをチェックする
		psコマンドで実行しているプロセスのコマンド名を取得して、そこにGrowlHelperAppが動いているかを調べている。
		@return 稼働していればfalse、稼働していない場合はtrue
	*/
	static private boolean isNotGrowlRunnning()	{
		String[] processCheckCommand = { "ps", "cx", "-o", "command" };
		String result = new CommandExecuter( processCheckCommand ).doCommand();
		if ( result.indexOf( "GrowlHelperApp" ) > 0 )	return false;
		System.out.println( "Growl is not running." );
		return true;
	}

}
