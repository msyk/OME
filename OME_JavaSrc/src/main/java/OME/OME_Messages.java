package OME;

import java.util.ListResourceBundle;

/**

 * 2009/6/28:新居:OME_JavaCore2へ移動

*/
public class OME_Messages extends ListResourceBundle {

	public Object[][] getContents()	{	return contents;	}
	
	static final Object[][] contents = {
		{	"327",	"Error in Sending Mail: Invalid addresses are specified in To/CC/Bcc fields. "
					+ "The following is returned error message from SMTP server. "
					+ "Collect the invalid addresses and retry.\n\n@@1@@" },
		{	"328",	"Error in Sending Mail: Destination address didn't specify in To/CC/Bcc fields. "
					+ "Or, other errors not to be able to send the message."},
		{	"304",	"Error in Sending Mail: Unknown SMTP host that specifies as the SMTP server. "
					+ "Check the following host name.\n\n@@1@@" },
		{	"305",	"Error in Sending Mail: Kind of connection problem. The following are possible reasons: "
					+ "SMTP server doesn't exist, Specified host is not SMTP server, "
					+ "It doesn't specify SMTP Auth in spite of it is required, "
					+ "Invalid port number, SMTP server doesn't permit to relay a message."
					+ "\n\nThis client recognized the error: @@1@@" },
		{	"306",	"Error in Sending Mail: Authentication failure, ex. bad password, nonexistent user name." },

// Fetchmail error message: from 3001...

		{	"3000", "Mail Downloading: Error message from fetchmail: fetchmail reported an error when OME tried to download mails from mail server." },
		{	"3001", "Mail Downloading: Message from fetchmail: There was no mail awaiting retrieval. (There may have been old mail"
						+" still on the server but not selected for retrieval.)" },
		{	"3002", "Mail Downloading: Error message from fetchmail: An error was encountered when attempting to open a socket to"
						+" retrieve mail. If you don't know what a socket is, don't worry"
						+" about it -- just treat this as an 'unrecoverable error'. This"
						+" error can also be because a protocol fetchmail wants to use is"
						+" not listed in /etc/services." },
		{	"3003", "Mail Downloading: Error message from fetchmail: The user authentication step failed. This usually means that a"
						+" bad user-id, password, or APOP id was specified. Or it may mean"
						+"that you tried to run fetchmail under circumstances where it did"
						+" not have standard input attached to a terminal and could not"
						+" prompt for a missing password." },
		{	"3004", "Mail Downloading: Error message from fetchmail: Some sort of fatal protocol error was detected." },
		{	"3005", "Mail Downloading: Error message from fetchmail: There was a syntax error in the arguments to fetchmail." },
		{	"3006", "Mail Downloading: Error message from fetchmail: The run control file had bad permissions." },
		{	"3007", "Mail Downloading: Error message from fetchmail: There was an error condition reported by the server. Can also"
						+" fire if fetchmail timed out while waiting for the server." },
		{	"3008", "Mail Downloading: Error message from fetchmail: Client-side exclusion error. This means fetchmail either found"
						+" another copy of itself already running, or failed in such a way"
						+"that it isn't sure whether another copy is running." },
		{	"3009", "Mail Downloading: Error message from fetchmail: The user authentication step failed because the server responded"
						+" \"lock busy\". Try again after a brief pause! This error is not"
						+" implemented for all protocols, nor for all servers. If not"
						+" implemented for your server, \"3\" will be returned instead, see"
						+" above. May be returned when talking to qpopper or other servers"
						+" that can respond with \"lock busy\" or some similar text containing the word \"lock\"." },
		{	"3010", "Mail Downloading: Error message from fetchmail: The fetchmail run failed while trying to do an SMTP port open or transaction." },
		{	"3011", "Mail Downloading: Error message from fetchmail: Fatal DNS error. Fetchmail encountered an error while performing a DNS lookup at startup and could not proceed." },
		{	"3012", "Mail Downloading: Error message from fetchmail: BSMTP batch file could not be opened." },
		{	"3013", "Mail Downloading: Error message from fetchmail: Poll terminated by a fetch limit (see the --fetchlimit option)." },
		{	"3014", "Mail Downloading: Error message from fetchmail: Server busy indication." },
		{	"3023", "Mail Downloading: Error message from fetchmail: Internal error. You should see a message on standard error with details." }
	};
}
