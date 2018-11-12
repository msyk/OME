package OME;

import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.ResourceBundle;
import java.util.logging.*;

/**
 OMEのエラーメッセージを処理するためのクラス。目的としては、コンソール出力、ログファイルへの保存、
 そして、GUI階層へのメッセージ伝達を行うようになっている。<p>
 基本的な使い方は、setupLoggerメソッドを使って、Loggerの初期化を行う。一般には最初にこれをする
 だけでいいが、ネームスペースを別のものに切り替えたいときは再度呼び出せば良い。<p>
 このsetupLoggerメソッドによって、JavaのLoggerのインスタンスを、指定したネームスペースで取得
 する。併せて、~/Logs/OME/フォルダに、「ネームスペース.log」という名前のログファイル
 を作成し、そこにログを溜め込む。このフォルダがない場合は自動的に作成する。なお、標準出力には必ず
 出力されるようになっている。
 もし、ダイアログボックスなどのGUI階層でメッセージを表示させたい場合は、setRedirectorメソッドに
 よって、MessageAcceptableインタフェースを仕込んだクラスを設定する。すると、ログ書き込みがあれば、
 そのクラスの指定のメソッドが呼び出されるようになるので、そちらで適当なコンポーネントに、ログを
 追加する。<p>
 そして、writeMessageないしはwriteErrorMessageメッセージを呼び出せば、メッセージが処理される。
 ただし、いずれもいくつかのバージョンがあるが、とりあえず、既存のソースのwriteMessage(String)が
 そのまま処理できるように、カレントのネームスペースという考え方をクラスに取り入れている。つまり、
 setupLoggerとwriteMessage(String)で、とりあえず使えると言えば使える状態になっている。<p>
 なお、エラー番号や例外などを含めたメソッドも定義しているが、このあたりは若干流動的であるので、注意して
 ほしい。エラーメッセージの国際化やあるいはデータベース化などいろいろやらないといけないことがある。<p>
 アプリケーションを分けた場合にはその利用方法で問題はなく、おそらく現状のOMEでは問題は出ないと
 思うが、1つのアプリケーションインスタンスで、異なった処理が、しかも並列で行われる場合は、カレントの
 ネームスペースという考え方ではなく、ネームスペースを逐次指定するメソッドを使うことが望ましいだろう。
 <hr>
 <h2>OME履歴情報</h2>
 <pre>
 作成者:新居雅行（Masayuki Nii/msyk@msyk.net）

 :
 2003/7/21:新居:Loggerベースになるように書き直した
 2003/8/29:新居:Javaのヴァージョンを判断して、1.3以前なら単に標準出力されるだけにした
 2005/9/19:新居:ログフォルダのカスタマイズに対応。エラー表示でスタックトレースも表示
 2005/9/23:新居:メッセージにレベルを設定できるようにした
 2009/6/28:新居:OME_JavaCore2へ移動
 </pre>
 */

public class Logging {

    static private MessageAcceptable redirector = null;

    static private boolean isStdOut = false;

    static private boolean isImportant = false;

    static private boolean autoReturn = true;

    static private String currentNameSpace = "";

    static private double versionNum;
	
	static private int messageLevel = 2;
	
	static ResourceBundle messageList;
	
	static FileHandler fh = null;

    static {
        String jVer = System.getProperty("java.version");
        StringTokenizer jVarTokens = new StringTokenizer(jVer, ".");
        int majorNum = Integer.parseInt(jVarTokens.nextToken());
        int minorNum = Integer.parseInt(jVarTokens.nextToken());
        versionNum = majorNum + (minorNum / 10.0);
		messageList = ResourceBundle.getBundle( "OME.OME_Messages", OMEPreferences.getInstance().getOMELocale() );
    }

    /**
     現在のネームスペースを設定する
     @param nameSpace ネームスペースを指定する文字列
     */
    static public void setCurrentNameSpace(String nameSpace) {
        currentNameSpace = nameSpace;
    }

    /**
     現在のネームスペースを取得する
     @return 現在のネームスペースの文字列
     */
    static public String getCurrentNameSpace() {
        return currentNameSpace;
    }

    /**
     指定したネームスペースでのログ機能を初期化する。具体的にはロガーのインスタンスを取得し、
     ログディレクトリを確保し、フォーマッタを独自のシンプル形式のものに置き換える。
     @param nameSpace ネームスペースを指定する文字列
     */
    static public void setupLogger(String nameSpace) {
        if (versionNum >= 1.4) {
            Logger targetLogger = Logger.getLogger(nameSpace);

            File omeLog = OMEPreferences.getInstance().getOMELog();
            if (!omeLog.exists()) omeLog.mkdirs();

            String fPattern = omeLog.getPath() + "/" + nameSpace + ".log";
            try {
            	if ( fh != null ){
            		fh.close();
            	}
                fh = new FileHandler(fPattern, true);
                fh.setEncoding("utf-8");
                fh.setFormatter(new OME_SimpleFormatter());
                targetLogger.addHandler(fh);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //親のロガーは、コンソールに出力するもの。SimpleFormatterを使っているけどフォーマッタを差し替える
            Logger parentLogger = targetLogger.getParent();
            Handler handlers[] = parentLogger.getHandlers();
            for (int ix = 0; ix < handlers.length; ix++) {
                handlers[ix].setFormatter(new OME_SimpleFormatter());
                try {
                    handlers[ix].setEncoding("utf-8");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        setCurrentNameSpace(nameSpace);
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
	
	static private Exception getNextException(Exception ex)	{
		if ( ex instanceof javax.mail.MessagingException )	
			return ((javax.mail.MessagingException)ex).getNextException();
		return null;	
	}
    /**
     エラーメッセージを出力する
     @param errNum エラー番号
     @param ex 現在発生している例外（ない場合はnull）
     @param msg エラーメッセージへのパラメータ
     @param nameSpace エラーを出力するネームスペース
     */
    static public void writeErrorMessage(int errNum, Exception ex, String[] msg, String nameSpace) {
		writeErrorMessage( 
			errNum, 
			/*currentException*/ null, 
			replaceStringParam( messageList.getString( String.valueOf( errNum ) ), msg ), 
			nameSpace );
	}
    /**
     エラーメッセージを出力する
     @param errNum エラー番号
     @param ex 現在発生している例外（ない場合はnull）
     @param msg エラーメッセージへのパラメータ
     */
    static public void writeErrorMessage(int errNum, Exception ex, String[] msg) {
		writeErrorMessage( 
			errNum, 
			ex, 
			msg, 
			getCurrentNameSpace() );
	}
    /**
     エラーメッセージを出力する
     @param errNum エラー番号
     @param ex 現在発生している例外（ない場合はnull）
     @param msg エラーメッセージ
     @param nameSpace エラーを出力するネームスペース
     */
    static public void writeErrorMessage(int errNum, Exception ex, String msg, String nameSpace) {
		if ( (msg == null) || (msg.length() == 0) )	{	//エラーメッセージを国際化するとしても、ちょっとずつするとして・・・
			try {
				msg = messageList.getString( String.valueOf( errNum ) );
			} catch ( java.util.MissingResourceException resEx )	{
			}
		}
		
		if (ex != null)	{
			writeMessage("%% OME Error " + errNum + " %% " + msg + " :" + ex.getLocalizedMessage(), nameSpace);
			StringWriter stWriter = new StringWriter();
			PrintWriter prWriter = new PrintWriter(stWriter);
			ex.printStackTrace(prWriter);
			writeMessage(stWriter.toString(), nameSpace);
			try	{
				stWriter.close();
			} catch(Exception e)	{	}
		}
		else
			writeMessage("%% OME Error " + errNum + " %% " + msg, nameSpace);
		if ( msg.length() != 0 )
			OME.messagemaker.GrowlNotify.sendErrorMessage( msg );
    }

    /**
     現在のネームスペースに、エラーメッセージを出力する
     @param errNum エラー番号
     @param ex 現在発生している例外（ない場合はnull）
     @param msg エラーメッセージ
     */
    static public void writeErrorMessage(int errNum, Exception ex, String msg) {
        writeErrorMessage(errNum, ex, msg, getCurrentNameSpace());
    }

	/**
	 メッセージのレベルを指定する。デバッグ時のメッセージを、ソースをそのまま通常は表示しないようにするなどに使える。
	 レベルは、デフォルトでは１になっていて、writeMessageメソッドのレベル指定しないものは、レベルが１で送出される。
	 たとえば、デバッグ用のメッセージは、writeMessageメソッドでレベルを２や３にしておく、デバッグ中はsetLevel(2)
	 にしておけば、そのメッセージは出るが、デバッグが終わればレベル設定をやめればよい。
	 @param level レベルの数値
	*/
	public void setLevel( int level )	{
		messageLevel = level;
	}
    /**
     現在のネームスペースにメッセージを出力する
     @param msg メッセージ
     */
    synchronized static public void writeMessage(String msg) {
        writeMessage(msg, getCurrentNameSpace());
    }

    /**
     現在のネームスペースにメッセージを出力する
     @param msg メッセージ
     @param level メッセージのレベル
     */
    synchronized static public void writeMessage(String msg, int level) {
        writeMessage(msg, getCurrentNameSpace(), level);
    }

    /**
     メッセージを出力する
     @param msg メッセージ
     @param nameSpace 出力するネームスペース
     */
    synchronized static public void writeMessage(String msg, String nameSpace) {
		writeMessage( msg, nameSpace, 1 );
	}
    /**
     メッセージを出力する（このメソッドがとりあえずは本体）
     @param msg メッセージ
     @param nameSpace 出力するネームスペース
     @param level メッセージのレベル
     */
    synchronized static public void writeMessage(String msg, String nameSpace, int level) {
		if ( messageLevel < level )	return;
        setCurrentNameSpace(nameSpace);
        if (versionNum >= 1.4) {
            Logger targetLogger = Logger.getLogger(nameSpace);
            if (isStdOut)
                targetLogger.setUseParentHandlers(true);
            else
                targetLogger.setUseParentHandlers(false);

            if (isImportant)
                targetLogger.warning(msg);
            else
                targetLogger.info(msg);
        } else {
            System.out.println(msg);
        }
        if (redirector != null) {
            if (isImportant) {
                redirector.followingMessageIsImportant();
                isImportant = false;
            }
            if (autoReturn)
                redirector.passMessage(msg + System.getProperty("line.separator"));
            else
                redirector.passMessage(msg);
        }
        isImportant = false;
    }
	
    /**
     エラーメッセージをリダイレクトするオブジェクトを定義する
     @param obj リダイレクト先のオブジェクト
     */
    static public void setRedirector(MessageAcceptable obj) {
        redirector = obj;
    }

    /**
     エラーメッセージを標準出力にも出力するかどうかを設定する
     @param stdOut trueなら標準出力にも出力する
     */
    static public void setAlwaysStdOut(boolean stdOut) {
        isStdOut = stdOut;
    }

    /**
     このメソッド以降、最初に現れるwriteMessageメソッドの出力を、重要ないしは警告メッセージとして扱う
     */
    static public void followingMessageIsImportant() {
        isImportant = true;
    }

    /**
     エラーメッセージをリダイレクトする先に、terminateApplicationメッセージを送る（ほぼDeprecated?）
     */
    static public void applicationTerminate() {
        if (redirector != null) redirector.terminateApplication();
    }
	
}
