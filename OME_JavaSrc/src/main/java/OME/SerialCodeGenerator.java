package OME;

import java.io.*;

/**
 * メールメッセージのファイル名にシリアルコードをつけるために、シリアルコードを自動生成する機能を提供する。
 * <p>これから、MmPreferenceより移動する（2002/1/21）
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @see MmPreferences
 *
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */

public class SerialCodeGenerator {

    /** 新たにMovingInfoクラスのインスタンスを得る
     @return 生成されたインスタンスへの参照 */
    public static SerialCodeGenerator getInstance() {
        if (mySelf == null) {
            mySelf = new SerialCodeGenerator();
        }
        OMEPreferences omePref = OMEPreferences.getInstance();
        File serialFile = new File(omePref.getOMEPref(), "Serial Number");
        if (lastModified != serialFile.lastModified()) {
            mySelf.initFromSerialNumberFile();
            lastModified = serialFile.lastModified();
        }
        return mySelf;
    }

    private static SerialCodeGenerator mySelf = null;

    private static long lastModified = -1;

    private long serialNumber = 0;

    /**
     シリアル番号を記録したファイルへの読み書きを同時に行わないようにするためのロックをファイルで行うが、そのときのロックファイルへの参照を得る
     @return シリアルファイルのロックファイルへの参照	*/
    private File getSerialLockingFile() {
        OMEPreferences omePref = OMEPreferences.getInstance();
        File lockFile = new File(omePref.getOMEPref() + "/serial_locking");
        return lockFile;
    }

    /**
     シリアル番号ファイルを利用した自動シリアル番号取得を利用できるようにする。 */
    private void initFromSerialNumberFile() {
        /*
         File lockFile = getSerialLockingFile();
         long startTime = System.currentTimeMillis();
         while(lockFile.exists())	{
         try	{
         Thread.sleep(1000);
         }
         catch (Exception e)	{
         Logging.writeMessage("Exception in Thread#sleep");
         }
         if((System.currentTimeMillis() - startTime) > (10 * 60 * 1000))	{
         Logging.writeMessage("Lock File Time Out!");
         }
         }
         
         try	{
         Runtime.getRuntime().exec("touch "+ getSerialLockingFile().getPath());
         }
         catch (Exception e)	{
         Logging.writeMessage("Exception in Runtime#exec");
         }
         */
        try {
            LineNumberReader inFile = new LineNumberReader(new FileReader(new File(OMEPreferences.getInstance()
                    .getOMEPref(), "Serial Number")));
            serialNumber = Long.parseLong(inFile.readLine());
            inFile.close();
        } catch (Exception e) {
            serialNumber = 0;
        }
    }

    /**
     シリアル番号をファイルに書き込みを行って更新処理を行う */
    public void updateSerialFile() {
        try {
            OMEPreferences omePref = OMEPreferences.getInstance();
            FileWriter outFile = new FileWriter(new File(omePref.getOMEPref(), "Serial Number"));
            outFile.write(String.valueOf(serialNumber));
            outFile.close();
            //            Runtime.getRuntime().exec("rm -f " + getSerialLockingFile().getPath());
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
        }
        ;
    }

    char myDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     現在のシリアル番号から、ファイル名に使用するコードを得る。
     @return コードの文字列	*/
    public synchronized String getSerialCode() {
        serialNumber++;
        StringBuffer sb = new StringBuffer("");
        int val = (int) serialNumber;
        while (val > 0) {
            sb.append(myDigit[val % 36]);
            val = val / 36;
        }
        sb.append("0000");
        StringBuffer sb2 = new StringBuffer("");
        for (int i = 3; i >= 0; i--)
            sb2.append(sb.charAt(i));
        int sb2Len = sb.length();
        return sb2.toString();
    }

}
