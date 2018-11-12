//
//  $Id: Window2Controller.h,v 1.3 2007/10/02 13:45:36 msyk Exp $
//
//  Window2Controller.h for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Fri Jul 22 16:56:44 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <OME/OME.h>

extern NSString *OMEPOPServerKey;
extern NSString *OMESMTPServerKey;

@interface Window2Controller : NSWindowController
{
    /* Receiving Mail */
    IBOutlet NSTextField *popServer;
    IBOutlet NSTextField *popAccount;
    IBOutlet NSSecureTextField *popPassword;
    IBOutlet NSButton *isAPOP;
    /* Sending Mail */
    IBOutlet NSTextField *senderName;
    IBOutlet NSTextField *senderAddress;
    IBOutlet NSTextField *smtpServer;
    IBOutlet NSButton *isSMTPAuth;
    IBOutlet NSTextField *smtpAccount;
    IBOutlet NSSecureTextField *smtpPassword;
}

- (IBAction)copyToSMTP:(id)sender;
- (IBAction)showWindow1:(id)sender;
- (IBAction)showWindow3:(id)sender;

@end
