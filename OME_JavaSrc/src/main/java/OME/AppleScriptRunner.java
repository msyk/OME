package OME;

import java.io.*;

/**
 * テキストで指定したAppleScriptのプログラムを実行させるクラス。ファイルのコメントを処理するメソッドや、ファイル指定のためのユーティリティメソッドも用意した
 *
 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
 * 
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */
public class AppleScriptRunner {

    private static String osascript = "/usr/bin/osascript"; //スクリプトを実行するコマンドのへのパス

    private static String errorMessage = null; //スクリプトのエラーメッセージを保持する変数

    private static boolean isPrintScript = false;

    /**
     * 引数に指定したAppleScriptのプログラムを実行する
     * <p>実際には、osascriptコマンドを使って実行している。スクリプトで発生した例外は、 getErrorMessageメソッドを使って取得する。スクリプトのエラーが発生したかどうかは、getErrorMessageメソッドの戻り値で判断できる。もし、nullなら、スクリプトのエラーは発生していない。
     * @param script AppleScriptのプログラムを文字列で指定する。改行はCRでかまわない。文字列はUNICODEで与えてOK
     * @return 実行結果、つまりresultの内容を文字列で戻す。文字列はUNIOCDEになっているので、そのままJavaのプログラムで使っていい
     * @exception jara.lang.Exception このメソッド内で例外が発生すると、単にそれをthrowする。たとえば、Runtime.execやInputStreamあるいはOutputStream関連の処理で例外が発生する可能性がある。この例外はcatchする必要がある。なお、スクリプト自体のエラーでは例外は発生しない
     */
    public static synchronized String doScript(String script) throws Exception {
        errorMessage = null;

        if (isPrintScript) { //スクリプトを標準出力に書き出す場合
            Logging.writeMessage("========== Execute the following script:");
            Logging.writeMessage(script);
            Logging.writeMessage("==========");
        }
        StringBuffer resultStr = new StringBuffer(""); //実行結果を得るための文字列
        StringBuffer errorStr = new StringBuffer(""); //実行エラーを得るための文字列
        try {
            Process doProcess = Runtime.getRuntime().exec(osascript); //osascriptコマンドを実行する
            InputStream errStd = doProcess.getErrorStream(); //標準入出力、診断出力へのストリームを参照
            InputStream inStd = doProcess.getInputStream();
            OutputStream outStd = doProcess.getOutputStream();
            outStd.write(script.getBytes()); //スクリプトプログラムを、出力し、osascriptの標準入力へ突っ込む
            outStd.close(); //スクリプトが終了したことを標準出力に教える
            doProcess.waitFor();
            byte buffer[] = new byte[10000]; //バッファを用意
            int readLength; //バッファから読み取ったデータ長
            while ((readLength = inStd.read(buffer, 0, 1024)) >= 0)
                //osascriptの標準出力を取得
                resultStr.append(new String(buffer, 0, readLength));
            while ((readLength = errStd.read(buffer, 0, 1024)) >= 0)
                //osascriptの診断出力を取得
                errorStr.append(new String(buffer, 0, readLength));
            if (errorStr.length() > 0) //診断出力から文字列の出力があれば、エラーであるとみなし、エラー文字列を保持
                    errorMessage = errorStr.toString();
        } catch (Exception e) { //内部で起こった例外は、呼び出し元にそのままスルー
            throw e;
        }
        return resultStr.toString(); //出力結果を文字列として戻す
    }

    /**
     * スクリプトの実行エラーがあった場合に、診断出力に出された文字列を取得する。直前に実行したdoScriptメソッドの処理に対応したエラーが得られる
     * @return エラーメッセージの文字列（エラーがないときにはnullになる）
     */
    public static String getErrorMessage() {
        return errorMessage;
    }

    /**
     * doScriptでスクリプトの実行を行う前に、スクリプトのテキストを標準出力に書き出すかどうかを設定。
     * <p>デフォルトは書き出さないようになっている。基本的にはデバッグ用の機能
     * @param value 標準出力に書き出すかどうかをboolean値で指定する
     */
    public static void setPrintScript(boolean value) {
        isPrintScript = value;
    }

    /**
     * 引数に指定したスクリプトを、Finderで実行させる。つまり、tell app "finder" .... end tellの中身だけを引数で与えて処理を実行させようというもの
     * @param script Finderに実行させるスクリプト
     * @return スクリプトの実行結果（つまりthe result）
     * @exception java.lang.Exception doScriptメソッドの例外の解説を参照
     */
    public static String doScriptByFinder(String script) throws Exception {
        try {
            return doScript("tell application \"Finder\"\r" + script + "\rend tell");
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 引数に指定したファイルに設定されているコメントを取得する
     * @param f 対象となるファイル
     * @return コメントの文字列
     */
    public static String getComment(File f) {
        try {
            return doScript("tell application \"Finder\" to get comment of " + getMacOSPath(f) + "");
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }
        return "";
    }

    /**
     * 引数に指定したファイルにコメントを設定する
     * @param f 対象となるファイル
     * @param comment 設定するコメント
     */
    public static void setComment(File f, String comment) {
        try {
            doScript("tell application \"Finder\" to set comment of " + getMacOSPath(f) + " to \"" + comment + "\"");
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }
    }

    /**
     * 引数に指定した文字列を、AppleScriptの文字列の中に指定できるようにする。
     * AppleScriptの文字列はダブルクォーテーションで囲うが、文字列の中にダブルクォーテーションを記述するには、\"の形式でなければならない。
     * @param s 元テキスト
     * @return 元テキストの中のダブルクォーテーションを、\"形式にしたもの
     */
    public static String quateEscapedString(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
            case '"':
                // ダブルクォーテーションの場合
                sb.append("\\\"");
                break;
            default:
                sb.append(s.charAt(i));
            }
        }
        return new String(sb);
    }

    /**
     * 引数に指定したファイルのAppleScriptプログラムでの参照形式の文字列を得る。
     * たとえば、/Users/msyk/test.txt に対しては、“(file "Users:msyk:test.txt" of startup disk)”という文字列が得られる
     * @param f 対象となるファイルないしはフォルダ
     * @return そのファイルを参照するAppleScriptでの文字列
     */
    public static String getMacOSPath(File f) {
        try {
            String s = new String(f.getPath());
            //getCanonicalPathとかgetAbsolutePathを使うと、なぜか濁音や半濁音がUNICODEとして分かれる
            StringBuffer b = new StringBuffer("");

            if (f.isDirectory())
                b.append("(folder (\"");
            else
                b.append("(file (\"");

            String volumeName = " of startup disk)";
            int pathTop = 1;
            if (s.startsWith("/Volumes/")) {
                int volNameEnd = s.indexOf("/", 9);
                volumeName = " of disk \"" + s.substring(9, volNameEnd) + "\")";
                pathTop = volNameEnd + 1;
            } else if (s.startsWith("~")) {
                s = System.getProperty("user.home") + s.substring(1, s.length());
                pathTop = 1;
            }
            for (int i = pathTop; i < s.length(); i++) {
                if (s.charAt(i) == '/')
                    b.append(':');
                else if (s.charAt(i) == ':')
                    b.append('/');
                else if (s.charAt(i) == '"')
                    if (i == 1)
                        b.append("ASCII character 34 & \"");
                    else if (i == s.length())
                        b.append("\" & ASCII character 34");
                    else
                        b.append("\" & ASCII character 34 & \"");
                else if (s.charAt(i) == '\\')
                    b.append("\\\\");
                else if (s.charAt(i) == '%') {
                    b.append((char) (Integer.parseInt(s.substring(i + 1, i + 3), 16)));
                    i += 2;
                } else
                    b.append(s.charAt(i));
            }
            b.append("\")");
            b.append(volumeName);

            return b.toString();
        } catch (Exception e) {
            return "error";
        }
    }
}