package OME.messagemaker;

import javax.mail.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import OME.*;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.nio.ByteBuffer;

/**f
メールのソースのバックアップを作成するクラス
<hr>
<h2>OME更新履歴</h2>
<pre>
2005/3/28:新居:作成
2009/6/28:新居:OME_JavaCore2へ移動
java.util.ConcurrentModificationException
</pre>
 
@author Masayuki Nii msyk@msyk.net
*/

public class MailSourceBackup extends PartProcessor {

    /**
     @throw MessageException 
     */
    public void preProcess() throws Exception {}

    /**
     @throw MessageException 
     */
    public void processMessage() throws Exception {
	
    }

    /**
     @throw MessageException 
     */
    public void afterProcess() throws Exception {
	
        OMEPreferences omePref = OMEPreferences.getInstance();
        String backupFileDTStr = omePref.getCurrentDTString();
        File dlFolder = omePref.getOMETemp();
        File hideFolder = new File(dlFolder, "hide");
		String fileName = "Download-" + backupFileDTStr;
		File backupFile = new File ( hideFolder, fileName );
		
		try	{
			Part thisPart = getMailSource();
			FileOutputStream ost = new FileOutputStream( 
					backupFile.getPath(), true);
			thisPart.writeTo( ost );
			ost.close();
			Logging.writeMessage("### MailSourceBackup Processing:" 
										+ backupFile.getPath() );
		} catch ( Exception e )	{
			Logging.writeErrorMessage(435, e, 
				"Exception in MailSourceBackup class. Fail to copy the input stream to a file.");
			throw new MessageException(e);
		}
	}
}
