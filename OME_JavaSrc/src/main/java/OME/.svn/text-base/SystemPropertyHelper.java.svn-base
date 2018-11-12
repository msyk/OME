package OME;

import java.io.*;
import java.text.*;
import java.util.*;

//
//  SystemPropertyHelper.java
//  OME_JavaProject
//
// * 2009/6/28:新居:OME_JavaCore2へ移動
//  Created by Masayuki Nii on 09/03/30.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

public class SystemPropertyHelper {
    // システムプロパティ取得用ヘルパーメソッド
    // プロパティのキーと、それが設定されていなかった時のデフォルト値を指定する。

	private boolean setUpByProp = false;
	
	public boolean isSetUpByProp()	{	return setUpByProp;	}

    /**
     * 文字列プロパティを取得する
     */
    public String getString(String key, String def) {
        String value = System.getProperty(key);
        if (value != null) {
            setUpByProp = true;
            return value;
        }
        return def;
    }

    /**
     * File型のプロパティを取得する
     */
    public File getFile(String key, File def) {
        String value = System.getProperty(key);
        if (value != null) {
            setUpByProp = true;
            return new File(value);
        }
        return def;
    }

    /**
     * boolean型のプロパティを取得する
     */
    public boolean getBoolean(String key, boolean def) {
        String value = System.getProperty(key);
        if (value != null) {
            setUpByProp = true;
            return Boolean.valueOf(value).booleanValue();
        }
        return def;
    }

    /**
     * int型のプロパティを取得する
     */
    public int getInt(String key, int def) {
        try {
            String value = System.getProperty(key);
            if (value != null) {
                setUpByProp = true;
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException ex) {}
        return def;
    }

    /**
     * Locale型のプロパティを取得する
     */
    public Locale getLocale(String key, Locale def) {
        String value = System.getProperty(key);
        if (value != null) {
            setUpByProp = true;
            return InternationalUtils.createLocaleFromString(value);
        }
        return def;
    }

    // その他のユーティリティ

    /**
     * ファイルの中身を文字列として返す。
     *
     * @param file 読み込むファイル。
     * @return fileの中身の文字列
     * @exception IOException ファイルが読み込めなかった時
     */
    public String readFile(File file) throws IOException {
        int length = (int) file.length();
        byte[] buffer = new byte[length];
        InputStream is = null;

        OME.mailformatinfo.MailFormatInfo mfInfo = OME.mailformatinfo.MailFormatInfo.getInstance();

        try {
            is = new BufferedInputStream(new FileInputStream(file));
            is.read(buffer, 0, length);
            return new String(buffer, mfInfo.getPrefFilesCode(OMEPreferences.getInstance().getOMELocale()));
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
                // ignore;
            }
        }
    }

    /**
     * コレクションに配列の中身をすべて追加する。
     *
     * @param collection コレクション
     * @param array 追加する配列
     */
    public void addAll(Collection collection, Object[] array) {
        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                collection.add(array[i]);
            }
        }
    }

}
