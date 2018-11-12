package OME;

/**
 * 文字列をトークン化するクラスで、空白のトークンも発生されるようにしたもの。また、デリミタを文字列として指定できるようにもなっている。
 * <p>StringTokenizerは、デリミタが2つ続くとそこにはトークンはないものとする。これだと、空行をみんな消してしまうので、
 * デリミタが1つでもあれば、そこにはトークンがあるものと解釈する以下のクラスを作った。
 * 基本的には、java.util.StringTokenizerと同じように使えばいい。（2000/8/21）
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */

public class StringTokenizerX {

    private int counter = 0;

    private String defDelim = null;

    private boolean isDelReturn = false;

    private String targetStr = null;

    private int nextSepCandidate = -100;

    private int limitCount = 0;

    /**
     * StringTokenizerXを生成する
     * @param str もとになる文字列
     */
    public StringTokenizerX(String str) {
        targetStr = str;
    }

    /**
     * StringTokenizerXを生成する
     * @param str もとになる文字列
     * @param delim 区切りとする文字列（デリミタ）
     */
    public StringTokenizerX(String str, String delim) {
        defDelim = delim;
        targetStr = str;
    }

    /**
     * StringTokenizerXを生成する
     * @param str もとになる文字列
     * @param delim 区切りとする文字列（デリミタ）
     * @param returnDelims デリミタをトークンの文字列に含めるならtrue（通常は含めない）
     */
    public StringTokenizerX(String str, String delim, boolean returnDelims) {
        defDelim = delim;
        isDelReturn = returnDelims;
        targetStr = str;
    }

    /**
     * トークンがまだあるかどうかを判断する
     * @return まだあるのならtrue、もうないのならfalse
     */
    public boolean hasMoreTokens() {
        nextSepCandidate = targetStr.indexOf(defDelim, counter);
        if ((nextSepCandidate < 0) && (counter >= targetStr.length())) return false;
        return true;
    }

    /**
     * トークンの個数をカウントする。コンストラクタで指定したデリミタで区切る
     * @return トークンの個数
     */
    public int countTokens() {
        int c = 0;
        int lastIndex = 0;
        int t = 0;
        while ((lastIndex = targetStr.indexOf(defDelim, t)) >= 0) {
            c++;
            t = lastIndex + defDelim.length();
        }
        if (t < targetStr.length()) c++;
        return c;
    }

    /**
     * トークンの個数をカウントする
     * @param delim 区切るデリミタを文字列で指定する
     * @return トークンの個数
     */
    public int countTokens(String delim) {
        int c = 0;
        int lastIndex = 0;
        int t = 0;
        while ((lastIndex = targetStr.indexOf(delim, t)) >= 0) {
            c++;
            t = lastIndex + delim.length();
        }
        if (t < targetStr.length()) c++;
        return c;
    }

    /**
     * あらかじめ設定されているデリミタに応じた次のトークンを文字列で戻す
     * @return 取り出したトークンの文字列
     */
    public String nextToken() {
        return nextToken(defDelim);
    }

    /**
     * 指定されたデリミタに応じた次のトークンを文字列で戻す
     * @param delim 区切り文字となるデリミタを指定する
     * @return 取り出したトークンの文字列
     */
    public String nextToken(String delim) {
        String theToken;

        if (nextSepCandidate == -100) nextSepCandidate = targetStr.indexOf(delim, counter);
        if (nextSepCandidate < 0) {
            if (counter < targetStr.length())
                theToken = new String(targetStr.substring(counter, targetStr.length()));
            else
                theToken = new String("");
            counter = targetStr.length();
        } else {
            theToken = new String(targetStr.substring(counter, nextSepCandidate));
            if (isDelReturn) theToken = theToken + delim;
            counter = nextSepCandidate + delim.length();
        }
        nextSepCandidate = -100;
        return theToken;
    }

}