//
//  $Id: OMEPOPServer.h,v 1.2 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sat Dec 18 11:19:09 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OMEPOPServer : NSObject
{
    NSString *server;
    NSString *account;
    NSString *password;
    NSString *protocol;
}

/* accessor methods  */
- (NSString *)server;
- (NSString *)account;
- (NSString *)password;
- (NSString *)protocol;

/* designated initializer */
- (id)initWithServer:(NSString *)aServer
             account:(NSString *)aAccount
            password:(NSString *)aPassword
            protocol:(NSString *)aProtocol;

/* initializer */
- (id)initWithString:(NSString *)aString;

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath;

/* write to file */
- (BOOL)writeToFile:(NSString *)aPath;

@end
