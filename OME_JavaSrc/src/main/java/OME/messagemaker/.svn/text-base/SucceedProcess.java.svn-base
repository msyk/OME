package OME.messagemaker;

import java.io.File;
import java.util.*;

import javax.mail.Part;

import OME.*;

/** 
 * メールファイルを作成した後にスクリプト処理するなどを行うプロセッサ
 * <hr>
 * <h2>OME更新履歴</h2>
 * <pre>
 * 2004/1/14:新居:
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
 $Revision: 1.8 $
 */

public class SucceedProcess extends PartProcessor {

    /**
     @throw MessageException 
     */
    public void preProcess() throws Exception {}

    /**
     @throw MessageException 
     */
    public void processMessage() throws Exception {
        Part thisPart = getMailSource();

    }

    /**
     @throw MessageException 
     */
    public void afterProcess() throws Exception {

        File firingFile = (File) getProperty("FireingFile");
        if ((firingFile == null) || (!firingFile.exists())) return;

        String sucProcesses = (String) getProperty("FiringProcess");
        if ((sucProcesses == null) || (sucProcesses.length() < 1)) return;

        OMEPreferences omePref = OMEPreferences.getInstance();

        StringTokenizer tokens = new StringTokenizer(sucProcesses, "|");
        while (tokens.hasMoreTokens()) {
            boolean isOpen = true;
            String fireProc = tokens.nextToken();
            if (fireProc.length() > 0) {
                if (fireProc.startsWith("*")) {
                    isOpen = false; 
                    fireProc = fireProc.substring(1);
                }
                if (!fireProc.startsWith("/")) fireProc = omePref.getOMEToolsFolder().getPath() + "/" + fireProc;
                if (isOpen)
                    OpenFile.byApplication(new File(fireProc), firingFile);
                else {
                    StringTokenizer params = new StringTokenizer(fireProc, " ");
                    ArrayList pList = new ArrayList();
                    while (params.hasMoreTokens())
                        pList.add(params.nextToken());
                    pList.add(firingFile);
                    Logging.writeMessage((new CommandExecuter(pList.toArray())).doCommand());
                }
            }
        }
    }
}
