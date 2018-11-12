//
//  $Id: OMESMTPServer.m,v 1.12 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Thu Nov 25 14:09:47 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import "OMESMTPServer.h"

#import "OMEAddress.h"
#import "OMELines.h"

/* com.apple.internetconfig.plist */
#define INTERNET_CONFIG @"com.apple.internetconfig"
#define IC_DATA @"ic-data"

/* description */
#define DESCRIPTION1 @"%@\n%@\n%@\nAUTH,%@,%@\n"
#define DESCRIPTION2 @"%@\n%@\n%@\n\n"

@implementation OMESMTPServer

/* accessor method */
- (void)setName:(NSString *)aString
{
    name = aString;
}

/* accessor method */
- (NSString *)name
{
     return name;
}

/* accessor method */
- (void)setAddress:(NSString *)aString
{
    address = aString;
}

/* accessor method */
- (NSString *)address
{
     return address;
}

/* accessor method */
- (void)setSMTP:(NSString *)aString
{
    smtp = aString;
}

/* accessor method */
- (NSString *)smtp
{
     return smtp;
}

/* accessor method */
- (void)setAUTH:(BOOL)flag
{
    auth = flag;
}

/* accessor method */
- (BOOL)auth
{
     return auth;
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

/* designated initializer */
- (id)initWithName:(NSString *)aName
           address:(NSString *)aAddress
              smtp:(NSString *)aSMTP
              auth:(BOOL)flag
           account:(NSString *)aAccount
          password:(NSString *)aPassword;
{
    NSCharacterSet *whitespaceCharacterSet;
    NSString *aString;

    self = [ super init ];
    if ( self ) {
        whitespaceCharacterSet = [ NSCharacterSet whitespaceCharacterSet ];
        /**** first line ****/
        aString = [ aName
                      stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        if ( [ aString isEqualToString:@"" ] ) {
            [ self setName:nil ];
        } else {
            [ self setName:aString ];
        }
        /**** second line ****/
        aString = [ aAddress
                      stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        if ( [ aString isEqualToString:@"" ] ) {
            [ self setAddress:nil ];
        } else {
            [ self setAddress:aString ];
        }
        /**** third line ****/
        aString = [ aSMTP
                      stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        if ( [ aString isEqualToString:@"" ] ) {
            [ self setSMTP:nil ];
        } else {
            [ self setSMTP:aString ];
        }
        /**** fourth line ****/
        /* auth */
        [ self setAUTH:flag ];
        /* account */
        aString = [ aAccount
                      stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        if ( [ aString isEqualToString:@"" ] ) {
            [ self setAccount:nil ];
        } else {
            [ self setAccount:aString ];
        }
        /* password */
        aString = [ aPassword
                      stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        if ( [ aString isEqualToString:@"" ] ) {
            [ self setPassword:nil ];
        } else {
            [ self setPassword:aString ];
        }
    }
    return self;
}

/* initializer */
- (id)initWithName:(NSString *)aName
           address:(NSString *)aAddress
              smtp:(NSString *)aSMTP
        authString:(NSString *)authString
{
    BOOL flag;
    NSUInteger count;
    NSArray *aArray;
    NSString *aAUTH, *aAccount, *aPassword;

    flag = NO;
    aAccount = nil;
    aPassword = nil;
    aArray = [ authString componentsSeparatedByString:@"," ];
    count = [ aArray count ];
    if ( count > 0 ) {
        aAUTH = [ aArray objectAtIndex:0 ];
        if ( [ aAUTH isEqualToString:@"AUTH" ] ) {
            flag = YES;
            if ( count > 1 )
                aAccount = [ aArray objectAtIndex:1 ];
            if ( count > 2 )
                aPassword = [ aArray objectAtIndex:2 ];
        }
    }
    return [ self
               initWithName:aName
               address:aAddress
               smtp:aSMTP
               auth:flag
               account:aAccount
               password:aPassword ];
}

/* initializer */
- (id)initWithString:(NSString *)aString
{
    NSUInteger count, i;
    NSArray *anArray;
    NSString *xString;
    NSString *nameString, *addressString, *smtpString, *authString;
    OMELines *omeLines;

    nameString = nil;
    addressString = nil;
    smtpString = nil;
    authString = nil;
    if ( aString ) {
        omeLines = [ OMELines linesWithString:aString ];
        anArray = [ omeLines array ];
        count = [ anArray count ];
        if ( count > 4 ) count = 4;
        for ( i = 0; i < count; i++ ) {
            xString = [ anArray objectAtIndex:i ];
            switch ( i ) {
            case 0:
                nameString = xString;
                break;
            case 1:
                addressString = xString;
                break;
            case 2:
                smtpString = xString;
                break;
            case 3:
                authString = xString;
                break;
            default:
                break;
            }
        }
    }
    return [ self
               initWithName:nameString
               address:addressString
               smtp:smtpString
               authString:authString ];
}

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath
{
    NSString *aString;
NSError *error;
    aString = [ NSString stringWithContentsOfFile:aPath  encoding: NSUTF8StringEncoding error:&error /**/];
    return [ self initWithString:aString ];
}

/* dealloc */

/* description of smtp server */
- (NSString *)description
{
    NSString *nameString, *addressString, *smtpString;
    NSString *accountString, *passwordString, *aString;

    /* name */
    if ( [ self name ] ) {
        nameString = [ self name ];
    } else {
        nameString = @"";
    }
    /* address */
    if ( [ self address ] ) {
        addressString = [ self address ];
    } else {
        addressString = @"";
    }
    /* smtp */
    if ( [ self smtp ] ) {
        smtpString = [ self smtp ];
    } else {
        smtpString = @"";
    }
    /* account */
    if ( [ self account ] ) {
        accountString = [ self account ];
    } else {
        accountString = @"";
    }
    /* password */
    if ( [ self password ] ) {
        passwordString = [ self password ];
    } else {
        passwordString = @"";
    }
    /* description */
    if ( [ self auth ] ) {
        aString = [ NSString
                      stringWithFormat:DESCRIPTION1,
                      nameString,
                      addressString,
                      smtpString,
                      accountString,
                      passwordString ];
    } else {
        aString = [ NSString
                      stringWithFormat:DESCRIPTION2,
                      nameString,
                      addressString,
                      smtpString ];
    }
    return aString;
}

/* write to file */
- (BOOL)writeToFile:(NSString *)aPath
{
    NSString *aString;

    aString = [ self description ];
    return [ aString writeToFile:aPath atomically:YES encoding:NSShiftJISStringEncoding error:NULL];
}

/* OMEAddress instance */
- (OMEAddress *)sender
{
    return [ [ OMEAddress alloc ]
                 initWithName:[ self name ] email:[ self address ] ];
}

/* set up SMTP Host */
/* effective on MacOSX 10.3 Pantehr */
/* not effective on MacOSX 10.4 Tiger */
- (void)setUpSMTPHost
{
    NSDictionary *rootDic, *versionDic, *icAddedDic;
    NSMutableDictionary *smtpDic;
    NSString *oldString, *newString;
    NSUserDefaults *userDefaults;

    newString = [ self smtp ];
    if ( newString ) {
        userDefaults = [ NSUserDefaults standardUserDefaults ];
        rootDic = [ userDefaults persistentDomainForName:INTERNET_CONFIG ];
        versionDic = [ rootDic objectForKey:@"Version 2.5.4" ];
        icAddedDic = [ versionDic objectForKey:@"ic-added" ];
        smtpDic = [ icAddedDic objectForKey:@"SMTPHost" ];
        oldString = [ smtpDic objectForKey:IC_DATA ];
        if ( [ newString isEqualToString:oldString ] == NO ) {
            [ smtpDic setObject:newString forKey:IC_DATA ]; 
            [ userDefaults
                setPersistentDomain:rootDic forName:INTERNET_CONFIG ];
            [ userDefaults synchronize ];
        }
    }
}

@end
