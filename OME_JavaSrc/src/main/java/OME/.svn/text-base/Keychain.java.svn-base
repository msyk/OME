package OME;

/**
 * Mac OS Xのキーチェーン（Keychain）からパスワードを取得したりあるいは書き込みを行う。kcpasswordコマンドが必要
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */
public class Keychain {

    private static String kcpassword = OMEPreferences.getInstance().getOMEToolsFolder() + "/kcpassword";

    //kcpasswordコマンドの絶対パス

    /**
     * キーチェーンに設定されているインターネットパスワードの項目から、パスワードの文字列を取り出す
     * <p>Kyechain ManagerのKCFindInternetPasswordを利用してパスワードを取得する。ポート番号は「任意」、セキュリティドメインは無指定となっている。したがって、デフォルトのキーチェーンからの取得となるl
     * @param host	ホスト名
     * @param account	アカウント名
     * @param protocol	プロトコル名
     * @return 引数で指定した項目のパスワード
     * @exception jara.lang.Exception パスワードが何らかの理由で取得できないときに発生する
     */
    public static synchronized String getPassword(String host, String account, String protocol) throws Exception {
        String param[] = new String[4]; //コマンドパラメータ用の配列
        param[0] = kcpassword;
        param[1] = "get";
        param[2] = host;
        param[3] = account;
        //        param[4] = protocol;

        CommandExecuter cExec = new CommandExecuter(param);
        return (cExec.doCommand());

        /*
         StringBuffer resultStr = new StringBuffer("");
         try	{
         Process doProcess = Runtime.getRuntime().exec(param);	//kcpasswordコマンドを実行する
         InputStream inStd = doProcess.getInputStream();
         doProcess.waitFor();
         if(doProcess.exitValue() != 0)	{	//プロセスの戻り値が0でないならエラー
         String errMessage = "%% OME Error 87 %% Keychain.java(OME)Error, Can't retreve password et al. : "+doProcess.exitValue();
         Logging.writeMessage(errMessage);
         throw(new Exception(errMessage));
         }
         byte buffer[] = new byte[1024];	//バッファを用意
         int readLength;	//バッファから読み取ったデータ長
         while((readLength = inStd.read(buffer, 0 , 1024)) >= 0)	//kcpasswordの標準出力を取得
         resultStr.append(new String(buffer, 0, readLength));
         }
         catch(Exception e)	{	//内部で起こった例外は、呼び出し元にそのままスルー
         throw e;
         }
         return (new String(resultStr.toString()));
         */
    }

    /**
     * キーチェーンにインターネットパスワードを設定する
     * <p>Kyechain ManagerのKCFindInternetPasswordを利用してパスワードを取得する。ポート番号は「任意」、セキュリティドメインは無指定となっている。したがって、デフォルトのキーチェーンへの設定となる。
     * @param host	ホスト名
     * @param account	アカウント名
     * @param passwd	パスワード
     * @param protocol	プロトコル名
     * @return 引数で指定した項目のパスワード
     * @exception jara.lang.Exception パスワードが何らかの理由でパスワードが設定されないときに発生する
     */
    public static synchronized void setPassword(String host, String account, String passwd, String protocol)
            throws Exception {
        String param[] = new String[6]; //コマンドパラメータ用の配列
        param[0] = kcpassword;
        param[1] = "set";
        param[2] = host;
        param[3] = account;
        param[4] = passwd;
        param[5] = protocol;
        try {
            Process doProcess = Runtime.getRuntime().exec(param); //kcpasswordコマンドを実行する
            doProcess.waitFor();
            if (doProcess.exitValue() != 0) //プロセスの戻り値が0でないならエラー
                    throw (new Exception("Keychain.java(OME)Error" + doProcess.exitValue()));
        } catch (Exception e) { //内部で起こった例外は、呼び出し元にそのままスルー
            throw e;
        }
    }
}