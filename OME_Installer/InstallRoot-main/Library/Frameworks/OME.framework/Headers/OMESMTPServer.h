//
//  $Id: OMESMTPServer.h,v 1.8 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Thu Nov 25 14:09:47 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

@class OMEAddress;

@interface OMESMTPServer : NSObject
{
    NSString *name;
    NSString *address;
    NSString *smtp;
    BOOL auth;
    NSString *account;
    NSString *password;
}

/* accessor methods : smtp */
- (NSString *)name;
- (NSString *)address;
- (NSString *)smtp;
- (BOOL)auth;
- (NSString *)account;
- (NSString *)password;

/* designated initializer */
- (id)initWithName:(NSString *)aName
           address:(NSString *)aAddress
              smtp:(NSString *)aSMTP
              auth:(BOOL)flag
           account:(NSString *)aAccount
          password:(NSString *)aPassword;

/* initializer */
- (id)initWithName:(NSString *)aName
           address:(NSString *)aAddress
              smtp:(NSString *)aSMTP
        authString:(NSString *)authString;

/* initializer */
- (id)initWithString:(NSString *)aString;

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath;

/* write to file */
- (BOOL)writeToFile:(NSString *)aPath;

/* OMEAddress instance */
- (OMEAddress *)sender;

/* set up SMTP Host */
/* effective on MacOSX 10.3 Pantehr */
/* not effective on MacOSX 10.4 Tiger */
- (void)setUpSMTPHost;

@end
