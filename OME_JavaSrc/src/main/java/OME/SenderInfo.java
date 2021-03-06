package OME;

import java.io.File;
import java.util.Vector;

import OME.mailformatinfo.MailFormatInfo;

/**
OME_PreferencesにあるSender_Info.txtファイルやシステムプロパティをもとにして、各種設定を管理する。
<p>シングルトンパターンに従っており、アプリケーション内ではインスタンスは必ず１つである。
また、最初のgetInstanceの呼び出しがあるまでは、
インスタンス化されない。すなわち、getInstanceを呼ぶまではSender_Info.txtファイルの読み込みは行われない。
ただし、パスワードの
キーチェーンからの取得は、パスワードの取得メソッドが呼び出されたときに実行される。
<p>Sender_Info.txtファイルでの指定が基本であるが、規定のシステムプロパティがあれば、その指定が優先される。
また、規定のシステムプロパティがあれば、Sender_Info.txtファイルからの読み込みはいっさい行わないため。
プロパティにないものはデフォルトとなる。

<h2>Sender_Info.txtの差し替え</h2>
<p>OME_PreferencesにあるSender_Info.txtを使う限りは、SenderInfo.getInstance()だけでOK。</p>
<p>もし、別のファイルを一時的に送信者情報として使いたい場合は、まずはSenderInfo.getInstance( File )を呼び出す。
その後は、getInstance()は指定したファイルからの読み出しに対応する。元に戻すには、SenderInfo.releaseInstance()
を呼び出しておく。このあと、getInstanceを呼び出すと、引数なしであれば既定のSender_Info.txt（OME_Preferences
にあるもの）が使われる。つまり、次のgetInstanceで改めてインスタンスが作られる。既存のインスタンスを残しておきたければ
変数に参照を代入しておけばいいが、あまり使うことはないかも（シングルトンじゃなくなるし）。
 
<hr>
<h2>OME履歴情報</h2>
<pre>
---:新居:ある日作成
	：
2004/7/19:新居:Sender_Info.txtの差し替えに対応
2006/2/16:ぐるり:SSLの為の対応
2006/7/16:新居:SSHに対応
2009/6/28:新居:OME_JavaCore2へ移動
*/

public class SenderInfo {

    /** 新たにSenderInfoクラスのインスタンスを得る。Sender_Info.txtファイルが変更されていればその結果を反映する。
     @return 生成されたインスタンスへの参照 */
    public static SenderInfo getInstance() {
        return getInstance( senderInfoFile );
    }

    /** 新たにSenderInfoクラスのインスタンスを得る。Sender_Info.txtファイルが変更されていればその結果を反映する。
	 @paran senderInfoFile Sender_Info.txtファイル相当のファイルのありか
     @return 生成されたインスタンスへの参照 */
    public static SenderInfo getInstance( File senderFile ) {
        if ( !isInstanciate ) {
            mySelf = new SenderInfo();
            isInstanciate = true;
        }
        if (	( ! senderFile.equals(senderInfoFile) ) 
			||  ( lastModified != senderFile.lastModified() ) ) {
            mySelf.initSenderInfoFile();
            lastModified = senderFile.lastModified();
			senderInfoFile = senderFile;
        }
        return mySelf;
    }

    /** シングルトンオブジェクトの破棄。次にgetInstanceを呼び出すと、必ず新たにインスタンス化する
	*/
    public void releaseInstance() {
        isInstanciate = false;
    }

	/** オブジェクトが参照しているファイル */
	private static File senderInfoFile 
		= new File(OMEPreferences.getInstance().getOMEPref(), "Sender_Info.txt");

	/** インスタンス化したらtrueを設定する */
    private static boolean isInstanciate = false;

	/** オブジェクトが参照しているファイルの最終更新日。ファイルの書き換えを自動チェックするため */
    private static long lastModified = -1;

	private SystemPropertyHelper sysPrefHelper = new OME.SystemPropertyHelper();
	/** 生成したオブジェクトを参照する変数 */
    private static SenderInfo mySelf = null;

    private String senderName = "";

    private String senderAddress = "";

    private String smtpServer = "127.0.0.1";

    private String smtpPort = "25";

    private boolean isSMTPAuth = false;

    private boolean isSSL = false;

    private boolean isSSH = false;

    private String smtpAccount = "";

    private String smtpPassword = null;
	
	private String sshCommand = null;

    /**
     Sender_Info.txtないしはシステムプロパティに記述した送信者名を得る
     @return 送信者名  */
    public String getSenderName() {
        return senderName;
    }

    /**
     Sender_Info.txtないしはシステムプロパティに記述した送信サーバのポート番号を得る
     @return 送信者名  */
    public String getSMTPPort() {
        return smtpPort;
    }

    /**
     Sender_Info.txtないしはシステムプロパティに記述した送信者のアドレスを得る
     @return 送信者のアドレス  */
    public String getSenderAddress() {
        return senderAddress;
    }

    /**
     Sender_Info.txtないしはシステムプロパティに記述したSMTPサーバのアドレスを得る
     @return SMTPサーバのアドレス  */
    public String getSMTPServer() {
        return smtpServer;
    }

    /**
     Sender_Info.txtないしはシステムプロパティで、SSLを利用するように設定されているかどうかを得る
     @return 利用するのであれば、trueを戻す  */
    public boolean isSSL() {
        return isSSL;
    }

    /**
     Sender_Info.txtないしはシステムプロパティで、SSHによるトンネリングを利用するように設定されているかどうかを得る
     @return 利用するのであれば、trueを戻す  */
    public boolean isSSH() {
        return isSSH;
    }

    /**
     Sender_Info.txtないしはシステムプロパティで、SMTP AUTHを利用するように設定されているかどうかを得る
     @return 利用するのであれば、trueを戻す  */
    public boolean isSMTPAuth() {
        return isSMTPAuth;
    }

    /**
     Sender_Info.txtないしはシステムプロパティでSMTP AUTHを利用する場合の、サーバのアカウントを得る
     @return SMTPサーバのアカウント  */
    public String getSMTPAccount() {
        return smtpAccount;
    }

    /**
     Sender_Info.txtないしはシステムプロパティでSSHによるプロパティを利用する場合の、トンネルを掘るコマンドを得る
     @return SMTPサーバのアカウント  */
    public String getSSHCommand() {
        return sshCommand;
    }

    /**
     Sender_Info.txtないしはシステムプロパティでSMTP AUTHを利用する場合の、サーバのアカウントに対するパスワードを得る
     @return パスワード  */
    public String getSMTPPassword() {
        if ((smtpPassword == null) || (smtpPassword.compareTo("") == 0)) try {
            smtpPassword = Keychain.getPassword(smtpServer, smtpAccount, "OME ");
        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 144 %% Error in Keychain accessing : " + e.getMessage());
        }
        return smtpPassword;
    }

    private void initSenderInfoFile() { //Sender_Info.txtファイルから設定を読み込む

//        String propString;
//        boolean setUpByProp = false;
		
		//起動時のプロパティでパラメータを設定されているかをチェック

//       includeCC =			sysPrefHelper.getBoolean(	"net.msyk.ome.Behavior_Info.IncludeCC",			includeCC);
 
		senderName =	sysPrefHelper.getString(	"net.msyk.ome.Sender_Info.SenderName",	senderName);
		senderAddress = sysPrefHelper.getString(	"net.msyk.ome.Sender_Info.SenderAddress", senderAddress);
        smtpServer =	sysPrefHelper.getString(	"net.msyk.ome.Sender_Info.SMTPServer",	smtpServer);
        isSMTPAuth =	sysPrefHelper.getBoolean(	"net.msyk.ome.Sender_Info.SMTPAuth",		isSMTPAuth);
        isSSL =			sysPrefHelper.getBoolean(	"net.msyk.ome.Sender_Info.SSL",			isSSL);
		smtpAccount =	sysPrefHelper.getString(	"net.msyk.ome.Sender_Info.SMTPAccount",	smtpAccount);
        smtpPassword =	sysPrefHelper.getString(	"net.msyk.ome.Sender_Info.SMTPPassword",	smtpPassword);

        if (sysPrefHelper.isSetUpByProp()) return;	//プロパティ指定が1つでもあれば、Sender_Info.txtファイルは読み込まない

        OMEPreferences omePref = OMEPreferences.getInstance();
        MailFormatInfo mfInfo = MailFormatInfo.getInstance();
        String prefFileCode = mfInfo.getPrefFilesCode(omePref.getOMELocale());

        byte buffer[] = new byte[10000];
        int c = 0;

        try {	// Sender_Info.txtファイルから読み込む
            LineInputStream inFile = new LineInputStream(new File(omePref.getOMEPref(), "Sender_Info.txt"));
            c = inFile.readLineNoNL(buffer);	//1行目は全体が送信者名
            senderName = new String(buffer, 0, c, prefFileCode);
            c = inFile.readLineNoNL(buffer);	//2行目は全体が送信者のメールアドレス
            senderAddress = new String(buffer, 0, c, prefFileCode);
            c = inFile.readLineNoNL(buffer);	//3行目は全体がSMTPサーバのアドレス
            smtpServer = new String(buffer, 0, c, prefFileCode);
            int colonPos;
            if ((colonPos = smtpServer.indexOf(":")) > 0) {	// 3行目にコロンが含まれていれば
                smtpPort = smtpServer.substring(colonPos + 1);	//コロン以降をポート
                smtpServer = smtpServer.substring(0, colonPos);	//コロン以前をサーバアドレスに分離
            }
            try {
                c = inFile.readLineNoNL(buffer);	//4行目の読み込み、カンマでトークナイズ
                StringTokenizerX lineTokens = new StringTokenizerX(new String(buffer, 0, c, prefFileCode), ",");
                String firstToken = lineTokens.nextToken();	//最初のトークンを読んで
                if (firstToken.indexOf("SSL") >= 0) {	//SSLの場合
                    isSSL = true;
                }
                if (firstToken.indexOf("AUTH") >= 0) {	//SMTP認証を行う場合
                    isSMTPAuth = true;
				}
                if (firstToken.indexOf("SSH") >= 0) {	//SSHでトンネリングを行う場合
                    isSSH = true;
				}
				try	{
                    smtpAccount = lineTokens.nextToken();	//2つ目のトークンが認証のユーザ名
                    smtpPassword = lineTokens.nextToken();	//3つ目のトークンが認証のパスワード
                    sshCommand = lineTokens.nextToken();	//4つ目のトークンがトンネリングに使うコマンド
                }
				catch (Exception e) {}
				
            }
			catch (Exception e) {}
            inFile.close();

        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 143 %% Maybe mistake in Sender_Info.txt : " + e.getMessage());
        }
    }

    /**
     Sender_Info.txtに記述した送信者のアドレスを得る
     @return 送信者のアドレスすべて  */
    public String[] getSenderAddressAll() {

        Vector returnValue = new Vector();

        OMEPreferences omePref = OMEPreferences.getInstance();

        byte buffer[] = new byte[10000];
        int c = 0;

        try {
            LineInputStream inFile = new LineInputStream(new File(omePref.getOMEPref(), "Sender_Info.txt"));
            int lineCounter = 0;
            while ((c = inFile.readLineNoNL(buffer)) >= 0) {
                if ((lineCounter % 4 == 1) && (c > 1)) returnValue.add(new String(buffer, 0, c));
                lineCounter++;
            }
            inFile.close();
        } catch (Exception e) {}
        return (String[]) returnValue.toArray(new String[returnValue.size()]);
    }

}
