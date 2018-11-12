package OME.mailwriter;

import java.io.*;
import java.util.*;

import OME.*;
import OME.mailformatinfo.MailFormatInfo;

public class MailMerger {

    /*
     MailMergerのコマンドラインインタフェース

     java OME.mailwriter.MailMerger -t templateFile -i inserting -f formatNum
     
     templateFile - テンプレートファイルのパス
     inserting - 標準入力から差し込みをするなら、STDINというキーワード、ファイルからならファイルのパス
     formatNum - フォーマット番号（デフォルトは1）
     
     -tを指定しない場合には、他のパラメータは意味をなさない。
     -tを指定しないときには差し込み処理は行われない

 * 2009/6/28:新居:OME_JavaCore2へ移動
     */

    public static void main(String args[]) {

        File templateFile = null;
        InputStream insertStream = null;
        int insertFormat = 1001;
        boolean ignoreTopLine = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-t")) {
                i++;
                templateFile = new File(args[i]);
            } else if (args[i].equals("-i")) {
                i++;
                if (args[i].equals("STDIN"))
                    insertStream = System.in;
                else {
                    try {
                        insertStream = new FileInputStream(new File(args[i]));
                    } catch (Exception e) {
                        Logging.writeMessage("%% OME Error 255 %% File not found in -i param: " + e.getMessage());
                    }
                }
            } else if (args[i].equals("-f")) {
                i++;
                insertFormat = 1000 + Integer.parseInt(args[i]);
            } else if (args[i].equals("-ignore")) {
                ignoreTopLine = true;
            }
        }
        if (templateFile != null) {
            switch (insertFormat) {
            case 1001:
                (new MailMerger()).processingFormat1(templateFile, insertStream);
                //フォーマット番号1の処理
                break;
            case 1002:
                (new MailMerger()).processingFormat2(templateFile, insertStream, ignoreTopLine);
                //フォーマット番号2の処理
                break;

            default:
                Logging.writeMessage("%% OME Error 276 %% Unknown insert file format num: " + insertFormat);
            } //	switch
        }
        System.exit(0);
    }

    /*    
     フォーマット番号　1 の処理内容について
     
     テンプレートファイル：
     テンプレートファイルに含まれている「@@n@@」が、差し込みデータと置き換わる。nは差し込みデータのいくつ目か
     「@@n@@」は1行内に含まれていないといけない
     差し込みファイル、ないしはストリーム
     漢字コードは、Shift-JISでエンコード
     <!--OME_MailWriter:ITEMDELIMITER-->　という文字列が切れ目となる
     サイズは128KBが上限となる。空のアイテムは利用できない
     
     @@9001@@　→ 2002年1月29日
     @@9002@@　→ 2002/1/29
     @@9003@@　→ 14時28分30秒
     @@9004@@　→ 14:28:30
     @@9005@@　→ 14時28分
     @@9006@@　→ 14:28
     */

    public void processingFormat1(File templateFile, InputStream insertStream) {

        String nextLine = System.getProperty("line.separator");
        byte newLineBytes[] = nextLine.getBytes();
        OMEPreferences omePref = OMEPreferences.getInstance();
        MailFormatInfo mfInfo = MailFormatInfo.getInstance();

        int readingBytes = 0;
        int offset = 0;
        byte buffer[] = new byte[1024 * 128];
        //128KBのバッファ、つまり、差し込みデータはこのサイズを超えないように！
        try {
            while ((readingBytes = insertStream.read(buffer, offset, 1024 * 10)) != -1)
                offset += readingBytes;
        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 276 %% IO error insertion: " + e.getMessage());
        }

        String allStream = null;
        try {
            allStream = new String(buffer, 0, offset, mfInfo.getTeplateInsertCode());
            insertStream.close();
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }

        StringTokenizerX insertToken = new StringTokenizerX(allStream, "<!--OME_MailWriter:ITEMDELIMITER-->");
        Vector items = new Vector();
        while (insertToken.hasMoreTokens()) {
            items.add(insertToken.nextToken());
        }
        try {
            File messageFile = omePref.newOutFile();
            FileOutputStream outFile = new FileOutputStream(messageFile);

            byte templateBuffer[] = new byte[(int) templateFile.length()];
            InputStream inSt = new FileInputStream(templateFile);
            inSt.read(templateBuffer);
            inSt.close();
            int bPointer = 0;
            int index;
            while (bPointer < templateBuffer.length) {
                for (index = bPointer; index < templateBuffer.length; index++) {
                    if (newLineBytes.length == 2) {
                        if ((templateBuffer[index] == newLineBytes[0])
                                && (templateBuffer[index + 1] == newLineBytes[1])) {
                            index += 2;
                            break;
                        }
                    } else if (newLineBytes.length == 1) {
                        if (templateBuffer[index] == newLineBytes[0]) {
                            index++;
                            break;
                        }
                    }
                }
                int aLineLen = index - newLineBytes.length - bPointer;
                String aLine = new String(templateBuffer, bPointer, aLineLen, mfInfo.getTemplateFileCode());
                bPointer = index;

                int fieldStart = 0, fieldEnd = -1, itemNum;
                while ((fieldStart = aLine.indexOf("@@", fieldEnd + 1)) != -1) {
                    fieldEnd = aLine.indexOf("@@", fieldStart + 1);
                    if (fieldEnd != -1) {
                        itemNum = Integer.parseInt(aLine.substring(fieldStart + 2, fieldEnd));
                        if (itemNum == 9001) {
                            Calendar cal = Calendar.getInstance();
                            aLine = aLine.substring(0, fieldStart) + cal.get(Calendar.YEAR) + "年"
                                    + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日"
                                    + aLine.substring(fieldEnd + 2);
                        } else if (itemNum == 9002) {
                            Calendar cal = Calendar.getInstance();
                            aLine = aLine.substring(0, fieldStart) + cal.get(Calendar.YEAR) + "/"
                                    + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH)
                                    + aLine.substring(fieldEnd + 2);
                        } else if (itemNum == 9003) {
                            Calendar cal = Calendar.getInstance();
                            aLine = aLine.substring(0, fieldStart) + cal.get(Calendar.HOUR_OF_DAY) + "時"
                                    + cal.get(Calendar.MINUTE) + "分" + cal.get(Calendar.SECOND) + "秒"
                                    + aLine.substring(fieldEnd + 2);
                        } else if (itemNum == 9004) {
                            Calendar cal = Calendar.getInstance();
                            aLine = aLine.substring(0, fieldStart) + cal.get(Calendar.HOUR_OF_DAY) + ":"
                                    + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND)
                                    + aLine.substring(fieldEnd + 2);
                        } else if (itemNum == 9005) {
                            Calendar cal = Calendar.getInstance();
                            aLine = aLine.substring(0, fieldStart) + cal.get(Calendar.HOUR_OF_DAY) + "時"
                                    + cal.get(Calendar.MINUTE) + "分" + aLine.substring(fieldEnd + 2);
                        } else if (itemNum == 9006) {
                            Calendar cal = Calendar.getInstance();
                            aLine = aLine.substring(0, fieldStart) + cal.get(Calendar.HOUR_OF_DAY) + ":"
                                    + cal.get(Calendar.MINUTE) + ":" + aLine.substring(fieldEnd + 2);
                        } else if (itemNum < items.size())
                                aLine = aLine.substring(0, fieldStart) + items.elementAt(itemNum)
                                        + aLine.substring(fieldEnd + 2);
                    }
                }
                outFile.write(aLine.getBytes(mfInfo.getUploadFileCode()));
                outFile.write(nextLine.getBytes(mfInfo.getUploadFileCode()));
            }
            outFile.close();
        } catch (Exception e) {
            Logging.writeMessage("%% OME Error 278 %% Error in template inserting: " + e.getMessage());
        }
    }

    /*    
     フォーマット番号　2 の処理内容について
     
     テンプレートファイル：
     テンプレートファイルに含まれている「@@n@@」が、差し込みデータと置き換わる。nは差し込みデータのいくつ目か
     「@@n@@」は1行内に含まれていないといけない
     差し込みファイル、ないしはストリーム
     改行コードで区切られる1行ごとに、ファイルが作成される。1行はタブで区切られ、テンプレートファイルの
     @@1@@が最初の項目、@@2@@が次の項目に置き換わる。
     */
    public void processingFormat2(File templateFile, InputStream insertStream, boolean ignore1stLine) {

        MailFormatInfo mfInfo = MailFormatInfo.getInstance();

        long tempSize = templateFile.length();
        byte tempBuffer[] = new byte[(int) tempSize];
        try {
            InputStream inSt = new FileInputStream(templateFile);
            inSt.read(tempBuffer);
            inSt.close();
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }

        String tempContentsString = null;
        try {
            tempContentsString = new String(tempBuffer, mfInfo.getTemplateFileCode());
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }

        byte lineBuffer[] = new byte[4096];
        int index = 0;
        int red;
        try {
            while ((red = insertStream.read()) >= 0) {
                if (red == 10) {
                    if (!ignore1stLine) makeUploadFile(tempContentsString, lineBuffer, index);
                    ignore1stLine = false;
                    index = 0;
                } else if (red == 13) {
                    if (!ignore1stLine) makeUploadFile(tempContentsString, lineBuffer, index);
                    ignore1stLine = false;
                    if ((red = insertStream.read()) == 10) {
                        index = 0;
                    } else {
                        lineBuffer[0] = (byte) red;
                        index = 1;
                    }
                } else {
                    lineBuffer[index] = (byte) red;
                    index++;
                }
            }
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }
    }

    private void makeUploadFile(String tempContentsString, byte lineBuffer[], int index) {

        MailFormatInfo mfInfo = MailFormatInfo.getInstance();
        OMEPreferences omePref = OMEPreferences.getInstance();

        StringTokenizerX insertingItems = null;
        try {
            insertingItems = new StringTokenizerX(new String(lineBuffer, 0, index, mfInfo.getTeplateInsertCode()), "\t");
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }

        int tokens = insertingItems.countTokens();

        MyString tempContents = new MyString(tempContentsString);
        if (!omePref.isHTMLExpanding()) tempContents.htmlReady();

        for (int i = 1; i <= tokens; i++) {
            String repStr = insertingItems.nextToken();
            String substStr = "@@" + i + "@@";
            int pos = tempContents.toString().indexOf(substStr);
            if (pos >= 0) {
                tempContents.replace(substStr, repStr);
            }
        }
        try {
            OutputStream outFile = new FileOutputStream(omePref.newOutFile());
            outFile.write(tempContents.toString().getBytes(mfInfo.getUploadFileCode()));
            outFile.close();
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }

    }

}
