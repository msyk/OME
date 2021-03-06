package OME.messagemaker;

import java.io.File;

import OME.Logging;
import OME.UnreadUtility;

/** 
 * 作成したメールファイルへのエイリアスを作成するプロセッサ
 * <hr>
 * <h2>OME更新履歴</h2>
 * <pre>
 * 2004/1/14:新居:
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
 $Revision: 1.8 $
 */

public class UnreadAliases extends PartProcessor {

    /**
     @throw MessageException
     */
    public void preProcess() throws Exception {}

    /**
     @throw MessageException
     */
    public void processMessage() throws Exception {}

    /**
     @throw MessageException
     */
    public void afterProcess() throws Exception {
		Logging.writeMessage("UnreadAliases::afterProcess");
       File firingFile = (File) parentMM.getProperty("FiringFile");
        if ((firingFile != null) && (firingFile.exists())) {
        	UnreadUtility.getInstance().createUnreadAlias(firingFile);
        }
    }

}
