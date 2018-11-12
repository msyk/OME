package OME;

//
//  LineInputStream.java
//  ProcessingDLFile
//
//  Created by 新居 雅行 on Mon Dec 24 2001.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//

import java.io.*;

/**
 * 行入力を行うインプットストリーム。オブジェクトを生成したときに、ファイルの中身をある程度読んで最初から検索をして、
 * ファイルの改行の種類を探っておく。その後、その検知した改行に合わせて、改行までを読み込む。
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */
public class LineInputStream {

    private int stillRedChar;

    private boolean isStillRed = false;

    private InputStream currentStream = null;

    private String newLine = System.getProperty("line.separator");

    private byte newLineBytes[] = newLine.getBytes();

    /** 引数にFileクラスを指定したコンストラクタ */
    public LineInputStream(File f) throws Exception {
        currentStream = new FileInputStream(f);
    }

    /** 引数にInputStreamクラスを指定したコンストラクタ */
    public LineInputStream(InputStream st) throws Exception {
        currentStream = st;
    }

    public LineInputStream(byte[] buffer) throws Exception {
        ByteArrayInputStream inSt = new ByteArrayInputStream(buffer);
        currentStream = inSt;
    }

    public void close() throws IOException {
        currentStream.close();
    }

    /** ファイルから1行分読み取って、それを引数のbyte配列に入れる
     @param b ファイルから読み取ったデータを入れる配列。末尾の改行も入る（末尾の改行はプラットフォームの改行コードに統一される）
     @return 読み取ったバイト数（最後まで読み取ったら-1を戻す）
     @exception java.lang.Exception 引数bで指定したバッファがあふれた場合や、ファイルの読み取りに問題がある場合
     */
    public int readLine(byte[] b) throws Exception {
        int index = 0;
        int red;
        boolean isRedChar = false;
        if (isStillRed) {
            red = stillRedChar;
            isStillRed = false;
            if (red == 10) {
                for (int i = 0; i < newLineBytes.length; i++) {
                    b[index] = newLineBytes[i];
                    index++;
                }
                return index;
            } else if (red == 13) {
                for (int i = 0; i < newLineBytes.length; i++) {
                    b[index] = newLineBytes[i];
                    index++;
                }
                red = currentStream.read();
                if (red != 10) {
                    stillRedChar = red;
                    isStillRed = true;
                }
                return index;
            }
            b[index] = (byte) red;
            index++;
        }
        try {
            while ((red = currentStream.read()) >= 0) {
                //Logging.writeMessage("LineInputStream: "+red);
                isRedChar = true;
                if (red == 10) {
                    for (int i = 0; i < newLineBytes.length; i++) {
                        b[index] = newLineBytes[i];
                        index++;
                    }
                    return index;
                } else if (red == 13) {
                    for (int i = 0; i < newLineBytes.length; i++) {
                        b[index] = newLineBytes[i];
                        index++;
                    }
                    red = currentStream.read();
                    if (red != 10) {
                        stillRedChar = red;
                        isStillRed = true;
                    }
                    return index;
                }
                b[index] = (byte) red;
                index++;
                if (index >= b.length) throw (new Exception("Buffer Overflow"));
            }
        } catch (Exception e) {
            throw e;
        }
        if (isRedChar) return index;
        return -1;
    }

    /** ファイルから1行分読み取って、それを引数のbyte配列に入れる
     @param b ファイルから読み取ったデータを入れる配列。末尾の改行は入らない
     @return 読み取ったバイト数（最後まで読み取ったら-1を戻す）
     @exception java.lang.Exception 引数bで指定したバッファがあふれた場合や、ファイルの読み取りに問題がある場合
     */
    public int readLineNoNL(byte[] b) throws Exception {
        int index = 0;
        int red;
        boolean isRedChar = false;

        if (isStillRed) {
            red = stillRedChar;
            isStillRed = false;
            if (red == 10) {
                return index;
            } else if (red == 13) {
                red = currentStream.read();
                if (red != 10) {
                    stillRedChar = red;
                    isStillRed = true;
                }
                return index;
            }
            b[index] = (byte) red;
            index++;
        }

        try {
            while ((red = currentStream.read()) >= 0) {
                isRedChar = true;
                if (red == 10) {
                    return index;
                } else if (red == 13) {
                    red = currentStream.read();
                    if (red != 10) {
                        stillRedChar = red;
                        isStillRed = true;
                    }
                    return index;
                }
                b[index] = (byte) red;
                index++;
                if (index >= b.length) throw (new Exception("Buffer Overflow"));
            }
        } catch (Exception e) {
            throw e;
        }
        if (isRedChar) return index;
        //Logging.writeMessage("LineInputStream: returning");
        return -1;
    }
}
