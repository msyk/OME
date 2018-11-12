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

/**
メールソースをそのまま保存するプロセッサ
<hr>
<h2>OME更新履歴</h2>
<pre>
2005/3/21:新居:着手
2009/6/28:新居:OME_JavaCore2へ移動
</pre>
 
@author Masayuki NiiÅiêVãèâÎçsÅj msyk@msyk.net
*/

public class DirectSave extends PartProcessor {

    /**
     @throw MessageException 
     */
    public void preProcess() throws MessageException {}

    /**
     @throw MessageException 
     */
    public void processMessage() throws MessageException {
		
		File savingFolder = (File) getProperty("StoringFolder");
		
//        OMEPreferences omePref = OMEPreferences.getInstance();
        SerialCodeGenerator serialGen = SerialCodeGenerator.getInstance();
		String fileName = serialGen.getSerialCode() + ".ygm";
		
		if ( ((String) getProperty( "EntryOfProcess" )).equals( "File" ) )	{
			try	{
				File originalFile = (File) getProperty( "OriginalSourceFile" );
				ReadableByteChannel src = Channels.newChannel( new FileInputStream( originalFile ) );
				WritableByteChannel dst = Channels.newChannel( 
					new FileOutputStream( new File ( savingFolder, fileName ) ) );
				
				ByteBuffer buffer = ByteBuffer.allocateDirect( 10000 );
				while ( src.read( buffer ) != -1 )	{
					buffer.flip();
					while ( buffer.hasRemaining() )
						dst.write( buffer );
					buffer.clear();
				}
				src.close();	dst.close();
				Logging.writeMessage("### DirectSave Processing:" 
							+ savingFolder.getCanonicalPath() + "/" + fileName);
			} catch ( Exception e )	{
				Logging.writeErrorMessage(431, e, 
					"Exception in DirectSave class. Fail to copy a mail source file.");			
				throw (MessageException)e;
			}
		}
		else	{	//
		}
			try	{
				Part thisPart = getMailSource();
				thisPart.writeTo(new FileOutputStream( new File ( savingFolder, fileName )) );
			} catch ( Exception e )	{
				Logging.writeErrorMessage(432, e, 
					"Exception in DirectSave class. Fail to copy the input stream to a file.");			
				throw (MessageException)e;
			}
    }

    /**
     @throw MessageException 
     */
    public void afterProcess() throws MessageException {}
}
