package OME;

import java.io.*;
import java.util.*;

/** 
 *	OME_PreferencesにあるReceive_Info.txtファイルをもとにして、接続するメールサーバに関する情報を管理する。
 *	<p>使い方としては、getInstanceでReceiveInfoのインスタンスを得て、nextを調べてtrueなら、各getterメソッドを使ってサーバ名などを得る。
 *	内部的にEnumerationを使っている。一連のレコードを最初から読み込みたいのなら、resetメソッドを実行すればよい。
 *	シングルトンパターンに従っており、アプリケーション内ではインスタンスは必ず１つである。また、最初のgetInstanceの呼び出しがあるまでは、
 *	インスタンス化されない。すなわち、getInstanceを呼ぶまではReceive_Info.txtファイルの読み込みは行われない。
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */

public class ReceiveInfo {

    /** 新たにReceiveInfoクラスのインスタンスを得る
     @return 生成されたインスタンスへの参照 */
    public static ReceiveInfo getInstance() {
        if (!isInstanciate) {
            mySelf = new ReceiveInfo();
            isInstanciate = true;
        }
        OMEPreferences omePref = OMEPreferences.getInstance();
        File infoFile = new File(omePref.getOMEPref(), "Receive_Info.txt");
        if (lastModified != infoFile.lastModified()) {
            mySelf.initReceiveInfoFile();
            lastModified = infoFile.lastModified();
            //			isReadFile = true;
        }
        return mySelf;
    }

    //	private static boolean isReadFile = false;
    private static long lastModified = -1;

    private static boolean isInstanciate = false;

    private static ReceiveInfo mySelf = null;

    private Enumeration e;

    private POPServerDef psdef;

    /** レコードのポインタを最初に戻す	*/
    public void reset() {
        e = receiveInfos.elements();
    }

    /** 次のレコードにポインタを移動する。戻り値がfalseなら、最後のレコードに達した。
     @return レコードをポイントしている場合にはtrue	*/
    public boolean next() {
        if (!e.hasMoreElements()) return false;
        psdef = (POPServerDef) e.nextElement();
        return true;
    }

    /** 現在のレコードのサーバ名を得る
     @return	サーバ名	*/
    public String getServerName() {
        return psdef.getServerName();
    }

    /** 現在のレコードのポート番号を得る
     @return	ポート番号	*/
    public String getServerPort() {
        return psdef.getServerPort();
    }

    /** 現在のレコードのアカウント名を得る
     @return	アカウント名	*/
    public String getAccount() {
        return psdef.getAccount();
    }

    /** 現在のレコードのパスワードを得る
     @return	パスワード	*/
    public String getPassword() {
        return psdef.getPassword();
    }

    /** 現在のレコードのプロトコルを得る
     @return	プロトコル	*/
    public String getProtocol() {
        return psdef.getProtocol();
    }
    /** 現在のレコードのオプションを得る
     @return オプション  */
    public String getOption()   {
         return psdef.getOption();
    }

    private int pointer = 0;

    private static Vector receiveInfos;

    /** Receive_Info.txtファイルの情報を読み取り、内部変数にレコードとしてそれらのデータを設定する。 */
    private void initReceiveInfoFile() { //Receive_Info.txtファイルから読み込みを行う

        OMEPreferences omePref = OMEPreferences.getInstance();

        receiveInfos = new Vector();
        try {
            LineNumberReader inFile = new LineNumberReader(new FileReader(new File(omePref.getOMEPref(),
                    "Receive_Info.txt")));
            boolean isEOF = false;
            while (!isEOF) {
                try {
                    String aLine = inFile.readLine();
                    if (aLine.equals("")) {} else if (aLine.startsWith("#")) {} else {
                        try {
                            StringTokenizerX tokens = new StringTokenizerX(aLine, ",");
                            String port = "110";
                            String host = tokens.nextToken();
                            String passwdhost = host;
                            String account = tokens.nextToken();
                            String passwd = tokens.nextToken();
                            int colonPos;
                            if ((colonPos = host.indexOf(":")) != -1) {
                                port = host.substring(colonPos + 1);
                                host = host.substring(0, colonPos);
								passwdhost = host;
                            }
                            if ((colonPos = host.indexOf(" ")) != -1)
                                passwdhost = host.substring(0, colonPos);
                            if ((passwd == null) || (passwd.compareTo("") == 0))
                                passwd = Keychain.getPassword(passwdhost, account, "OME ");
                            POPServerDef psdef = new POPServerDef(host, port, account, passwd);
                            receiveInfos.addElement(psdef);
                            if (tokens.hasMoreTokens()) psdef.setProtocol(tokens.nextToken());
                            if (tokens.hasMoreTokens()) psdef.setOption(tokens.nextToken());
                        } catch (Exception e) {
                            Logging.writeMessage("%% OME Error 32 %% Maybe mistake in Receive_Info.txt : "
                                    + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    isEOF = true;
                }
            }
            inFile.close();
            e = receiveInfos.elements();
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }
    }

    private class POPServerDef {

        private String serverName;

        private String serverPort;

        private String account;

        private String password;

        private String protocol;

        private String option;

        public POPServerDef(String server, String port, String acc, String pass) {
            serverName = server;
            account = acc;
            password = pass;
            protocol = "pop3";
            serverPort = port;
            option = "";
        }

        public void setProtocol(String pstr) {
            protocol = pstr;
        }

        public void setOption(String ostr) {
            option = ostr;
        }

        public String getServerName() {
            return serverName;
        }

        public String getServerPort() {
            return serverPort;
        }

        public String getAccount() {
            return account;
        }

        public String getPassword() {
            return password;
        }

        public String getProtocol() {
            return protocol;
        }

        public String getOption() {
            return option;
        }

    }
}
