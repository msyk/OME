//
//  $Id: Window2Controller.m,v 1.3 2007/10/02 13:45:36 msyk Exp $
//
//  Window2Controller.m for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Fri Jul 22 16:56:44 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "Window2Controller.h"

#import "Definitions.h"

#define WINDOW_NIB_NAME @"Window2"
#define WINDOW_FRAME_AUTOSAVE_NAME @"Window2"

NSString *OMEPOPServerKey = @"OMEPOPServerKey";
NSString *OMESMTPServerKey = @"OMESMTPServerKey";

@implementation Window2Controller

/* designated initializer */
- (id)init
{
    self = [ super initWithWindowNibName:WINDOW_NIB_NAME ];
    if ( self ) { 
        [ self setWindowFrameAutosaveName:WINDOW_FRAME_AUTOSAVE_NAME ];
    }
    return self;
}

/* copy POP configuration to SMTP configuration */
- (IBAction)copyToSMTP:(id)sender
{
    NSString *aAccount, *aPassword;

    aAccount = [ popAccount stringValue ];
    aPassword = [ popPassword stringValue ];
    [ smtpAccount setStringValue:aAccount ];
    [ smtpPassword setStringValue:aPassword ];
}

/* back to window1 */
- (IBAction)showWindow1:(id)sender
{
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    [ notificationCenter
        postNotificationName:SHOW_WINDOW1
        object:self
        userInfo:nil ];
    [ self close ];
}

/* local method */
- (OMEPOPServer *)omePOPServer
{
    NSString *aServer, *aAccount, *aPassword, *aProtocol;

    aServer = [ popServer stringValue ];
    aAccount = [ popAccount stringValue ];
    aPassword = [ popPassword stringValue ];
    if ( [ isAPOP state ] ) {
        aProtocol = @"APOP";
    } else {
        aProtocol = @"POP3";
    }
    return [ [ [ OMEPOPServer alloc ]
                 initWithServer:aServer
                 account:aAccount
                 password:aPassword
                 protocol:aProtocol ]
               autorelease ]; 
}

/* local method */
- (OMESMTPServer *)omeSMTPServer
{
    BOOL auth;
    NSString *aName, *aAddress, *aSMTP, *aAccount, *aPassword;

    aName = [ senderName stringValue ];
    aAddress = [ senderAddress stringValue ];
    aSMTP = [ smtpServer stringValue ];
    auth = [ isSMTPAuth state ];
    aAccount = [ smtpAccount stringValue ];
    aPassword = [ smtpPassword stringValue ];
    return [ [ [ OMESMTPServer alloc ]
                 initWithName:aName
                 address:aAddress
                 smtp:aSMTP
                 auth:auth
                 account:aAccount
                 password:aPassword ]
               autorelease ];
}

/* forward to window3 */
- (IBAction)showWindow3:(id)sender
{
    NSDictionary *aDictionary;
    NSNotificationCenter *notificationCenter;
    OMEPOPServer *omePOPServer;
    OMESMTPServer *omeSMTPServer;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    omePOPServer = [ self omePOPServer ];
    omeSMTPServer = [ self omeSMTPServer ];
    aDictionary = [ NSDictionary
                      dictionaryWithObjectsAndKeys:
                      omePOPServer, OMEPOPServerKey,
                      omeSMTPServer, OMESMTPServerKey,
                      nil ];
    [ notificationCenter
        postNotificationName:SHOW_WINDOW3
        object:self
        userInfo:aDictionary ];
    [ self close ];
}

@end
