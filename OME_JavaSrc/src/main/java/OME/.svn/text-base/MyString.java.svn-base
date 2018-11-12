package OME;

/**
 * java.lang.Stringクラスを発展させて、置換機能を追加したもの。
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */

public class MyString {

    private String thisString;

    /**
     * MyStringオブジェクトを生成する
     * @param s 記録する文字列
     */
    public MyString(String s) {
        thisString = s;
    }

    /**
     * String形式の文字列を得る
     * @return 文字列
     */
    public String toString() {
        return thisString;
    }

    /**
     * 引数にある文字列を検索してその場所を戻す。
     * @param str 検索する文字列
     * @return 文字列の場所。最初は0で存在しない場合は-1
     */
    public int indexOf(String str) {
        return thisString.indexOf(str);
    }

    /**
     * 記録している文字列の中から文字列を探し出して置換し、変換した結果を戻す。大文字と小文字の違いは無視する
     * @param match 検索する文字列
     * @param subst 置き換える文字列
     * @return 文字列を置き換えた結果のMyStringオブジェクトへの参照
     */
    public MyString substituteString(String match, String subst) {
        int mLen = match.length();
        int strLen = thisString.length();
        if (strLen < mLen) return this;
        StringBuffer b = new StringBuffer("");
        int i;
        for (i = 0; i < (strLen - (mLen - 1)); i++) {
            String flg = thisString.substring(i, i + mLen);
            if (flg.equalsIgnoreCase(match)) {
                b.append(subst);
                i += (mLen - 1);
            } else
                b.append(thisString.charAt(i));
        }
        if (i < strLen) b.append(thisString.substring(i));
        return new MyString(b.toString());
    }

    /**
     * 記録している文字列の中から文字列を探し出して置換する。大文字と小文字の違いは無視する
     * @param match 検索する文字列
     * @param subst 置き換える文字列
     */
    public void replace(String match, String subst) {
        int mLen = match.length();
        int strLen = thisString.length();
        if (strLen < mLen) return;
        StringBuffer b = new StringBuffer("");
        int i;
        for (i = 0; i < (strLen - (mLen - 1)); i++) {
            String flg = thisString.substring(i, i + mLen);
            if (flg.equalsIgnoreCase(match)) {
                b.append(subst);
                i += (mLen - 1);
            } else
                b.append(thisString.charAt(i));
        }
        if (i < strLen) b.append(thisString.substring(i));
        thisString = b.toString();
    }

    /**
     * MyStringで管理している文字列の内容について、HTML可能な形式に変更する
     */
    public void htmlReady() {
        substituteString("&", "&amp;");
        substituteString("<", "&lt;");
        substituteString(">", "&gt;");
    }

}
