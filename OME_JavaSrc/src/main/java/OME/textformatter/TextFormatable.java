package OME.textformatter;

import java.util.*;

/**
 * テキスト整形クラスの基底クラス。横幅のバイト数を考慮するなどの機能を設けたフォーマットクラス
 * until 2001/12/30
 *
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */

public abstract class TextFormatable {

    static int serialNum; //ol liタグで予約

    static boolean isNumbering; //ol liタグで予約

    static int widthByBytes = OME.OMEPreferences.getInstance().getOneLineLimit();

    static String newLine = System.getProperty("line.separator");

    public abstract String formating(String item, Properties props, Object sender);

    String unbracket(String source) {
        char startChar = source.charAt(0);
        char lastChar = source.charAt(source.length() - 1);
        if (startChar == lastChar)
                if ((startChar == '\"') || (startChar == '\'')) return source.substring(1, source.length() - 1);
        return source;
    }
}
