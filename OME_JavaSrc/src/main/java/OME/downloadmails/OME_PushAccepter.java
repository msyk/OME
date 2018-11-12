package OME.downloadmails;

import OME.*;

import java.util.*;
import java.io.*;
import javax.activation.*;

import javax.mail.*;
import javax.mail.event.*;
import com.sun.mail.imap.*;


/**
 <hr>
 <h2>OME履歴情報</h2>
 <pre>
 作成者:新居雅行（Masayuki Nii/msyk@msyk.net）
 2009/11/10:新居:プッシュ対応したくなったので作ってみた
 </pre>

 */
public class OME_PushAccepter implements MessageCountListener {

    private String loggerNameSpace = "net.msyk.ome.downloadmails";
	private Folder folder;

    public static void main(String args[]) {
        (new OME_PushAccepter()).doIdling();
        System.exit(0);
    }

	public OME_PushAccepter()	{
        Logging.setupLogger(loggerNameSpace);

        OMEPreferences omePref = OMEPreferences.getInstance(); //OME設定へのインスタンスを得る
        if (omePref.isDownloadMailsMessageStandardOutput())
			Logging.setAlwaysStdOut(true);
		ReceiveInfo rInfo = ReceiveInfo.getInstance();
        rInfo.reset();
		rInfo.next();
		
		try {
			Properties props = System.getProperties();
			Session session = Session.getInstance(props);
			session.setDebug(true);
			Store store = null;
			if ( rInfo.getProtocol().indexOf("SSL") > -1 )	{
				 store = session.getStore("imaps");
			} else {
				 store = session.getStore("imap");
			}
			
			store.connect(	rInfo.getServerName(),	Integer.parseInt( rInfo.getServerPort()), 
							rInfo.getAccount(),		rInfo.getPassword()		);

			// Open a Folder
			folder = store.getFolder( "INBOX" );
			if (folder == null || !folder.exists()) {
				System.out.println("Invalid folder");
				System.exit(1);
			}

			folder.open(Folder.READ_WRITE);

			// Add messageCountListener to listen for new messages
			folder.addMessageCountListener( this );
			
		} catch (Exception ex) {
			System.out.println( "Cause-->" + ex.getCause() );
			System.out.println( "Message-->" + ex.getMessage() );
			ex.printStackTrace();
		}
	}
	//
	
	public void messagesRemoved(MessageCountEvent ev) {	}
	
	public void messagesAdded(MessageCountEvent ev) {
		Message[] msgs = ev.getMessages();
		System.out.println("Got " + msgs.length + " new messages");

		// Just dump out the new messages
		for (int i = 0; i < msgs.length; i++) {
			try {
				System.out.println("-----");
				System.out.println("Message " +
				msgs[i].getMessageNumber() + ":");
				msgs[i].writeTo(System.out);
			} catch (IOException ioex) {
				ioex.printStackTrace();
			} catch (MessagingException mex) {
				mex.printStackTrace();
			}
		}
		
	}

	public void doIdling()	{
		try	{
			// Check mail once in "freq" MILLIseconds
			boolean supportsIdle = false;
			try {
				if (folder instanceof IMAPFolder) {
					IMAPFolder f = (IMAPFolder)folder;
					f.idle();
					supportsIdle = true;
					System.out.println("IDLE true");
				}
			} catch (FolderClosedException fex) {
				throw fex;
			} catch (MessagingException mex) {
				supportsIdle = false;
			}
			for (;;) {
				if (supportsIdle && folder instanceof IMAPFolder) {
					IMAPFolder f = (IMAPFolder)folder;
					f.idle();
					System.out.println("IDLE done");
				} else {
					Thread.sleep( 1000 ); // sleep for freq milliseconds

					// This is to force the IMAP server to send us
					// EXISTS notifications.
					folder.getMessageCount();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
