package OME.textformatter;

//
//  LineDevider.java
//  OME_SendMail
//
//  Created by 新居 雅行 on Sun Dec 30 2001.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//

import java.io.*;

/**
 * 指定したバイト数ごとに改行を入れるというテキスト整形機能を提供するクラス
 *
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */

public class LineDevider {

    /**
     *    元の文字列に対して、指定したバイト数ごとに改行を入れるた文字列を作成する。
     *	@return	変換した結果の文字列
     *	@param source 元になるテキスト
     *	@param lineByte 何バイトごとに区切るか
     */
    public static String convert(String source, int lineByte) {
        return convert(source, lineByte, "");
    }

    /**
     *    元の文字列に対して、指定したバイト数ごとに改行を入れるた文字列を作成する。
     *	@return	変換した結果の文字列
     *	@param source 元になるテキスト
     *	@param lineByte 何バイトごとに区切るか
     *	@param prefix 変換後の1行目行の頭につける文字列。この文字列のバイト数も数える。2行目以降は等価な空白を行の頭にうめる
     */
    public static String convert(String source, int lineByte, String prefix) {
        StringBuffer buf = new StringBuffer("");
        convertAppend(buf, source, lineByte, prefix);
        return buf.toString();
    }

    /**
     *    元の文字列に対して、指定したバイト数ごとに改行を入れるた文字列を作成する。
     *	@param buffer 変換した文字列を、ここに指定したStringBufferにappendしてつなげていく
     *	@param source 元になるテキスト
     *	@param lineByte 何バイトごとに区切るか
     */
    public static void convertAppend(StringBuffer buffer, String source, int lineByte) {
        convertAppend(buffer, source, lineByte, "");
    }

    /**
     *    元の文字列に対して、指定したバイト数ごとに改行を入れるた文字列を作成する。
     *	@param buffer 変換した文字列を、ここに指定したStringBufferにappendしてつなげていく
     *	@param source 元になるテキスト
     *	@param lineByte 何バイトごとに区切るか
     *	@param prefix 変換後の1行目行の頭につける文字列。この文字列のバイト数も数える。2行目以降は等価な空白を行の頭にうめる
     */
    public static void convertAppend(StringBuffer buffer, String source, int lineByte, String prefix) {
        convertAppend(buffer, source, lineByte, prefix, true);
    }

    /**
     *  元の文字列に対して、指定したバイト数ごとに改行を入れるた文字列を作成する。
     *	@param buffer 変換した文字列を、ここに指定したStringBufferにappendしてつなげていく
     *	@param source 元になるテキスト
     *	@param lineByte 何バイトごとに区切るか（-1にすると一切の処理はしない）
     *	@param prefix 変換後の各行の頭につける文字列。この文字列のバイト数も数える
     *	@param spacePrefix trueなら2行目以降、行の先頭はprefixに指定した文字列と同じ幅の空白で埋める
     */
    public static void convertAppend(StringBuffer buffer, String source, int lineByte, String prefix,
            boolean spacePrefix) {

        if (source.length() == 0) return;
        if (lineByte < 0) {
            buffer.append(source);
            return;
        }

        int byteLength = 0;
        boolean isPrefixSet = false;
        int newLineLength = newLine.length();
        String equivSpacePrefix;
        int prefixByteCount;

        try {
            byte prefixArray[] = prefix.getBytes("Shift_JIS");
            prefixByteCount = prefixArray.length;
            StringBuffer equivSpacePrefixBuffer = new StringBuffer("");
            for (int inx = 0; inx < prefixArray.length; inx++)
                if (prefixArray[inx] < 0) {
                    equivSpacePrefixBuffer.append("　");
                    inx++;
                } else
                    equivSpacePrefixBuffer.append(" ");
            equivSpacePrefix = equivSpacePrefixBuffer.toString();
        } catch (Exception e) {
            equivSpacePrefix = "　";
            prefixByteCount = 2;
        }

        if (prefix.length() != 0) {
            buffer.append("　");
            buffer.append(prefix);
            isPrefixSet = true;
            buffer.append("　");
            byteLength += (prefixByteCount + 4);
        }
        char beforeChar = source.charAt(0);
        buffer.append(beforeChar);
        byteLength += getByteCount(beforeChar);

        boolean throughAfterNewLine = false;

        for (int pos = 1; pos < source.length(); pos++) {
            char posChar = source.charAt(pos);

            if (source.substring(pos, pos + newLineLength).compareTo(newLine) == 0) {
                byteLength = 0;
                buffer.append(newLine);
                pos += (newLineLength - 1);
                throughAfterNewLine = true;
                isPrefixSet = false;
                if (pos >= source.length()) break;
            } else {
                if ((byteLength >= lineByte) && !isInhibitLineTopChar(posChar) && !isInhibitLineEndChar(beforeChar)) {
                    if ((isCJK(posChar) && !isSpace(posChar)) || (isCJK(beforeChar) && isWordElement(posChar))
                            || (!isWordElement(beforeChar) && isWordElement(posChar))) {
                        buffer.append(newLine);
                        byteLength = 0;
                    }
                }
            }
            if ((byteLength == 0) && (prefix.length() != 0) && ((pos + 1) < source.length())) {
                buffer.append("　");
                if (isPrefixSet && spacePrefix)
                    buffer.append(equivSpacePrefix);
                else {
                    buffer.append(prefix);
                    isPrefixSet = true;
                }
                buffer.append("　");
                byteLength += (prefixByteCount + 4);
            }
            if (!throughAfterNewLine) {
                buffer.append(posChar);
                byteLength += getByteCount(posChar);
                beforeChar = posChar;
            } else {
                throughAfterNewLine = false;
                beforeChar = 0x00;
            }
        }
    }

    /*
     構想（笑）　2003/7/26
     こんだけパラメータを決められたらと思ったりして

     １２２２２２２２２２２２２２２２２２２２２２２７
     ３●４＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝８８８９
     ３４４＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝８８８９
     ３ＢＢＢＢＢＢＢＢＢＢＢＢＢＢＢＢＢＢＢＢＢＢ９
     ３●４＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝８８８９
     ３４４＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝８８８９
     ５６６６６６６６６６６６６６６６６６６６６６６Ａ

     another params.

     and width of 「4」
     and width of 「8」
     has innner line i.e. 「B」

     */
    private static String inhibitLinTopStr = "」）";

    private static String newLine = System.getProperty("line.separator");

    private static boolean isInhibitLineTopChar(char c) {
        int cType = Character.getType(c);
        if (cType == Character.END_PUNCTUATION) return true;
        if (cType == Character.OTHER_PUNCTUATION) return true;
        if (cType == Character.SPACE_SEPARATOR) return true;
        if (inhibitLinTopStr.indexOf(c) != -1) return true;
        return false;
    }

    private static boolean isInhibitLineEndChar(char c) {
        int cType = Character.getType(c);
        //        if(cType == Character.MODIFIER_LETTER)                return true;
        if (cType == Character.START_PUNCTUATION) return true;
        return false;
    }

    private static int getByteCount(char c) {
        try {
            return String.valueOf(c).getBytes("Shift_JIS").length;
        } catch (Exception e) {
            return 1;
        }
    }

    private static boolean isSpace(char c) {
        int cType = Character.getType(c);
        if (cType == Character.SPACE_SEPARATOR) return true;
        return false;
    }

    private static boolean isWordElement(char c) {
        int cType = Character.getType(c);
        if (cType == Character.DECIMAL_DIGIT_NUMBER) return true;
        if (cType == Character.LETTER_NUMBER) return true;
        if (cType == Character.OTHER_NUMBER) return true;
        if (cType == Character.CURRENCY_SYMBOL) return true;
        if (cType == Character.UPPERCASE_LETTER) return true;
        if (cType == Character.LOWERCASE_LETTER) return true;
        if (cType == Character.TITLECASE_LETTER) return true;
        return false;
    }

    private static boolean isCJK(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        if (block.equals(Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)) return true;
        if (block.equals(Character.UnicodeBlock.HIRAGANA)) return true;
        if (block.equals(Character.UnicodeBlock.KATAKANA)) return true;
        if (block.equals(Character.UnicodeBlock.BOPOMOFO)) return true;
        if (block.equals(Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO)) return true;
        if (block.equals(Character.UnicodeBlock.KANBUN)) return true;
        if (block.equals(Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS)) return true;
        if (block.equals(Character.UnicodeBlock.CJK_COMPATIBILITY)) return true;
        if (block.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) return true;
        if (block.equals(Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS)) return true;
        return false;
    }
/*
    public static void main(String arg[]) {

        StringBuffer b = new StringBuffer();
        String s = "それこそいいかげん、たくさんの国でEnglishが使われている。\nはいはい確かに「そうですね！。」と言いたい。\n\tWe must sing a song well.\n\n";
        for (int i = 10; i < 35; i++) {
            LineDevider.convertAppend(b, s, i, "◎", true);
            b.append("-\n");
        }
        try {
            OutputStream outFile = new FileOutputStream(new File("/Users/msyk/result.log"));
            outFile.write(b.toString().getBytes("Shift_JIS"));
            outFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
}
