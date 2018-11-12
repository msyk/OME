package OME.mailformatinfo;
//
//  TextConverter_ja_JP.java
//  OME_JavaProject
//
// * 2009/6/28:新居:OME_JavaCore2へ移動
//  Created by 新居 雅行 on Mon Feb 11 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//
import java.util.*;
import javax.mail.internet.*;
import OME.*;

public class TextConverter_ja_JP implements TextConverter{

    public String convert(String source){
		StringBuffer sb = new StringBuffer();
		char c;
		for (int i = 0; i < source.length(); i++) {
			c  = source.charAt(i);
			switch (c) {
			case 0x005c:	// REVERSE SOLIDUS ->
			c = 0x00a5;	// Yen Mark ( by msyk )
			break;
				case 0x2016:	// DOUBLE VERTICAL LINE ->
			c = 0x2225;	// PARALLEL TO
			break;
			case 0x00a2:	// CENT SIGN ->
			c = 0xffe0;	// FULLWIDTH CENT SIGN
			break;
			case 0x00a3:	// POUND SIGN ->
			c = 0xffe1;	// FULLWIDTH POUND SIGN
			break;
			case 0x00ac:	// NOT SIGN ->
			c = 0xffe2;	// FULLWIDTH NOT SIGN
			break;
			}
			sb.append(c);
		}
		return new String(sb);
    }

    public String headerTextUnifier(String source)	{
        if(source == null)   return null;

		String returnString = source;
		byte buffer[] = source.getBytes();
		try	{
			returnString = new String(buffer, 0, buffer.length, "JISAutoDetect");
		}	catch(Exception ex)		{
			return source;
		}

        if( returnString.indexOf("=?") >= 0 )
			returnString = myDecodeString(returnString);
		return convert(returnString);
    }
    /**
     * 文字列の中にMIMEエンコードがされている個所があるかを判断し、エンコードがあれば解除する
     * 
     * @param str
     *            文字列
     * @return エンコードを解除した文字列
     */
    private String myDecodeString(String str) {
	
		if ( str == null )
			return "";
		
        int pointer = 0;
        int encStart = 0, encEnd = 0;
        StringBuffer sb = new StringBuffer("");
        while ((encStart = str.indexOf("=?", pointer)) >= 0) {
            int secondQ = str.indexOf("?", encStart + 2);
            int thirdQ = str.indexOf("?", secondQ + 1);
            encEnd = str.indexOf("?=", thirdQ + 1);
			
            if ( (secondQ < 0)||(thirdQ < 0)||(encEnd < 0) ) {
                sb.append(str.substring(pointer, encStart + 2));
                pointer = encStart + 2;
            } else {
                sb.append(str.substring(pointer, encStart));
                try {
					String decodedStr = MimeUtility.decodeText(str.substring(encStart, encEnd + 2));
						//デコードしてみる
					
					//変換できなかった文字列の割合をチェックする
					int c = 0;
					for ( int i = 0 ; i < decodedStr.length() ; i++ )
						if ( decodedStr.codePointAt( i ) == 65533 )	// 65533 is 'REPLACEMENT CHARACTER'
							c++;
					if ( ( (float)c/decodedStr.length() ) > 0.3f )
						decodedStr = MimeUtility.decodeText("=?Shift_JIS?" + str.substring( secondQ + 1));
						//変換できなかった文字列が30%以上の場合は、Shift_JISとしてデコードしてみる
						//これは単に間違えたメールを送っているだけにすぎないのだがねぇ
					sb.append(decodedStr);
                } catch (Exception e) {
                    sb.append(str.substring(encStart, encEnd + 2));
                }
                pointer = encEnd + 2;
            }
        }
        sb.append(str.substring(pointer, str.length()));
        return sb.toString();
    }
	
	public String FileNameUnifier(String source)	{
		String retunString; 
        retunString = substituteString(source, "/", ":");
        retunString = substituteString(retunString, "#", "＃");

		return retunString;
	}

    /**
     * 文字列中の単語を置き換えた文字列を求める
     * 
     * @param source
     *            元になる文字列
     * @param match
     *            引数sourceから検索する文字列
     * @param subst
     *            引数matchに指定した文字列と置き換える文字列
     * @return 置き換えた結果の文字列
     */
    private String substituteString(String source, String match, String subst) {
        int mLen = match.length();
        StringBuffer b = new StringBuffer("");
        for (int i = 0; i < source.length() - (mLen - 1); i++) {
            String flg = source.substring(i, i + mLen);
            if (flg.equalsIgnoreCase(match)) {
                b.append(subst);
                i += (mLen - 1);
            } else
                b.append(source.charAt(i));
            //                    Logging.writeMessage(i+"$"+b);
        }
        return b.toString();
    }


}
