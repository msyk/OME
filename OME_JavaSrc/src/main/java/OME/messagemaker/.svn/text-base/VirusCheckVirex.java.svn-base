package OME.messagemaker;

import java.io.File;

import OME.*;

/** 
 $Revision: 1.8 $
 */
public class VirusCheckVirex extends PartProcessor {

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

        File target = (File) getProperty("RootOfMessage");
        if (target == null) return;

        Logging.writeMessage("Scanning --> " + target.getPath());
        String cmd[] = { "/usr/local/vscanx", "--secure", "-v", "-r", "-f", target.getPath()};
        CommandExecuter comEx = new CommandExecuter(cmd);
        comEx.doCommand(false, System.out);
    }
}
