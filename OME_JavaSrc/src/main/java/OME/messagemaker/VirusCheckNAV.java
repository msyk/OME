package OME.messagemaker;

import java.io.File;

import javax.mail.Part;

import OME.*;

/** 
 */
public class VirusCheckNAV extends PartProcessor {

    /**
     * @throws MessageException 
     */
    public void preProcess() throws Exception {}

    /**
     * @throws MessageException 
     */
    public void processMessage() throws Exception {
        Part thisPart = getMailSource();
    }

    /**
     * @throws MessageException 
     */
    public void afterProcess() throws Exception {
        File target = (File) getProperty("RootOfMessage");
        if (target == null) return;
        Logging.writeMessage("Scanning --> " + target.getPath());

        String cmd[] = { "/usr/bin/navx", "-f", "-a", target.getPath()};
        CommandExecuter comEx = new CommandExecuter(cmd);
        comEx.doCommand(false, System.out);
    }
}
