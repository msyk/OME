package OME.mailformatinfo;
//
//  TextConverter_Default.java
//  OME_JavaProject
//
// * 2009/6/28:新居:OME_JavaCore2へ移動
//  Created by Masayuki Nii on 08/12/24.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

public class TextConverter_Default implements TextConverter	{
    public String convert(String source)	{
		return source;
	}
	
    public String headerTextUnifier(String source)	{
		return source;
	}

	public String FileNameUnifier(String source)	{
		String retunString; 
        retunString = substituteString(source, "/", ":");
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
