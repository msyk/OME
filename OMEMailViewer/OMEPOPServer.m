//
//  $Id: OMEPOPServer.m,v 1.2 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sat Dec 18 11:19:09 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import "OMEPOPServer.h"

/* description */
#define DESCRIPTION @"%@,%@,%@,%@"

/* format */
#define FORMAT @"%@\n"

@implementation OMEPOPServer

/* accessor method */
- (void)setServer:(NSString *)aString
{
    server = aString;
}

/* accessor method */
- (NSString *)server
{
     return server;
}

/* accessor method */
- (void)setAccount:(NSString *)aString
{
    account = aString;
}

/* accessor method */
- (NSString *)account
{
     return account;
}

/* accessor method */
- (void)setPassword:(NSString *)aString
{
    password = aString;
}

/* accessor method */
- (NSString *)password
{
     return password;
}

/* accessor method */
- (void)setProtocol:(NSString *)aString
{
    protocol = aString;
}

/* accessor method */
- (NSString *)protocol
{
     return protocol;
}

/* designated initializer */
- (id)initWithServer:(NSString *)aServer
             account:(NSString *)aAccount
            password:(NSString *)aPassword
            protocol:(NSString *)aProtocol
{
    NSCharacterSet *whitespaceCharacterSet;
    NSString *aString;

    self = [ super init ];
    if ( self ) {
        whitespaceCharacterSet = [ NSCharacterSet whitespaceCharacterSet ];
        aString = [ aServer
                      stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        [ self setServer:aString ];
        aString = [ aAccount
                      stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        [ self setAccount:aString ];
        aString = [ aPassword
                      stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        [ self setPassword:aString ];
        aString = [ aProtocol
                      stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        [ self setProtocol:aString ];
    }
    return self;
}

/* initializer */
- (id)initWithString:(NSString *)aString
{
    NSUInteger count;
    NSArray *aArray;
    NSString *aServer, *aAccount, *aPassword, *aProtocol;

    aServer = nil;
    aAccount = nil;
    aPassword = nil;
    aProtocol = nil;
    aArray = [ aString componentsSeparatedByString:@"," ];
    count = [ aArray count ];
    if ( count > 0 )
        aServer = [ aArray objectAtIndex:0 ];
    if ( count > 1 )
        aAccount = [ aArray objectAtIndex:1 ];
    if ( count > 2 )
        aPassword = [ aArray objectAtIndex:2 ];
    if ( count > 3 )
        aProtocol = [ aArray objectAtIndex:3 ];
    return [ self
               initWithServer:aServer
               account:aAccount
               password:aPassword
               protocol:aProtocol ];
}

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath
{
    NSString *aString;
    NSError *error;
    
    aString = [ NSString stringWithContentsOfFile:aPath encoding: NSUTF8StringEncoding error:&error /*NSError *error;*/];
    return [ self initWithString:aString ];
}

/* dealloc */

/* description */
- (NSString *)description
{
    return [ NSString
               stringWithFormat:DESCRIPTION,
               [ self server ],
               [ self account ],
               [ self password ],
               [ self protocol ] ];
}

/* write to file */
- (BOOL)writeToFile:(NSString *)aPath
{
    NSString *aString;

    aString = [ self description ];
    aString = [ NSString stringWithFormat:FORMAT, aString ];
    return [ aString writeToFile:aPath atomically:YES encoding:NSShiftJISStringEncoding error:NULL];
}

@end
