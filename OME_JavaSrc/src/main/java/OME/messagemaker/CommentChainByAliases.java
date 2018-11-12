package OME.messagemaker;

import java.io.File;
import java.util.StringTokenizer;

import javax.mail.Part;

import OME.*;

/** 

 * <hr>
 * <h2>OME更新履歴</h2>
 * <pre>
 * 2004/1/14:新居:
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
 $Revision: 1.8 $
 */

public class CommentChainByAliases extends PartProcessor {

    /**
     @throw MessageException èàóùÇ™é∏îsÇµÇΩÇ∆Ç´ÇÃó·äO
     */
    public void preProcess() throws Exception {}

    /**
     @throw MessageException èàóùÇ™é∏îsÇµÇΩÇ∆Ç´ÇÃó·äO
     */
    public void processMessage() throws Exception {}

    /**
     @throw MessageException èàóùÇ™é∏îsÇµÇΩÇ∆Ç´ÇÃó·äO
     */
    public void afterProcess() throws Exception {

        OMEPreferences omePref = OMEPreferences.getInstance();
        Part thisPart = getMailSource();

        try {
            File aRefFolder = new File(omePref.getOMETemp(), "refByAliases");
            if (!aRefFolder.exists()) aRefFolder.mkdir();
            File msgItem = (File) getProperty("RootOfMessage");

            String msgID = concatStringArray(thisPart.getHeader("Message-ID"));
            if (msgID != null) {
                File itemRefFolder = new File(aRefFolder, msgID + ".midref");
                if (!itemRefFolder.exists()) itemRefFolder.mkdir();
                MacAlias.createMacAliasInFolderByUniqueFileName(itemRefFolder, msgItem);
                Logging.writeMessage("Make Alias --> at: " + itemRefFolder + "\n original: " + msgItem);
            }

            String inRepTo = concatStringArray(thisPart.getHeader("In-Reply-To"));
            if (inRepTo != null) {
                File itemRefFolder = new File(aRefFolder, inRepTo + ".iftref");
                if (!itemRefFolder.exists()) itemRefFolder.mkdir();
                MacAlias.createMacAliasInFolderByUniqueFileName(itemRefFolder, msgItem);
                Logging.writeMessage("Make Alias --> at: " + itemRefFolder + "\n original: " + msgItem);
            }

            String references = concatStringArray(thisPart.getHeader("References"));
            if (references != null) {
                StringTokenizer tokens = new StringTokenizer(references, " ,\r\n");
                while (tokens.hasMoreTokens()) {
                    String refStr = tokens.nextToken();
                    File itemRefFolder = new File(aRefFolder, refStr + ".refref");
                    if (!itemRefFolder.exists()) itemRefFolder.mkdir();
                    MacAlias.createMacAliasInFolderByUniqueFileName(itemRefFolder, msgItem);
                    Logging.writeMessage("Make Alias --> at: " + itemRefFolder + "\n original: " + msgItem);
                }
            }
        } catch (Exception igunored) {}
        ;
    }

    private String concatStringArray(String array[]) {
        if (array == null) return null;
        if (array.length == 1) return array[0];
        StringBuffer sb = new StringBuffer(array[0]);
        for (int i = 1; i < array.length; i++)
            sb.append(array[i]);
        return sb.toString();
    }
}
