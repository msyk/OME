package OME.messagemaker;

import OME.CommandExecuter;

/**
 * 2009/6/28:新居:OME_JavaCore2へ移動
 @author Masayuki Nii msyk@msyk.net
 $Revision: 1.7 $
 */
public class GetBackupPathFromProcmail extends PartProcessor {

    /**
     @throw MessageException 
     */
    public void preProcess() throws Exception {
        String cmds[] = { "/usr/bin/tail", "-n", "1", System.getProperty("user.home") + "/.procmailrc"};
        String backupPath = (new CommandExecuter(cmds)).doCommand();
        if (backupPath.length() > 0) { //ÉoÉbÉNÉAÉbÉvÉtÉ@ÉCÉãÇÃê›íËÇ™Ç†ÇÍÇŒÅAÉvÉçÉpÉeÉBÇ…ãLò^
            setProperty("X-OME-BackupPath", backupPath.substring(0, backupPath.length() - 1));
        }
    }

    /**
     @throw MessageException 
     */
    public void processMessage() throws Exception {}

    /**
     @throw MessageException 
     */
    public void afterProcess() throws Exception {}
}
