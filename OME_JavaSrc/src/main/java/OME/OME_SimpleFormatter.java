package OME;

import java.text.DateFormat;
import java.util.*;
import java.util.logging.*;

/**

 <hr>
 <h2>OME更新履歴</h2>
 <pre>
  Masayuki Nii/msyk@msyk.net
  Tetsuya Kaneuchi/)

 2003/8/2:新居:???
 2002/8/6:金内:null???
 2009/6/28:新居:OME_JavaCore2へ移動
 </pre>
 */
public class OME_SimpleFormatter extends java.util.logging.Formatter {

    public String format(LogRecord record) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        String lineTopStr = "OME(" + df.format(Calendar.getInstance().getTime()) + "): ";
        String msg = record.getMessage();
        if (msg == null) {
            msg = "null";
        }
        StringTokenizer tokens = new StringTokenizer(msg, "\r\n");
        StringBuffer logString = new StringBuffer();
        while (tokens.hasMoreTokens())
            logString.append(lineTopStr + tokens.nextToken() + "\n");
        return logString.toString();
    }

}
