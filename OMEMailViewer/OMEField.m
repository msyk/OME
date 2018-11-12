//
//  $Id: OMEField.m,v 1.6 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Jan 30 15:34:55 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "OMEField.h"

#import "OMEAddress.h"

/* Format */
#define FIELD_BODY_SEPARATOR @", "

/* Functions - joined components */
NSString *OMEJoinedComponents ( NSArray *anArray )
{
    NSString *aString;

    if ( anArray ) {
        if ( [ anArray count ] > 0 ) {
            aString = [ anArray
                componentsJoinedByString:FIELD_BODY_SEPARATOR ];
        } else {
            aString = nil;
        }
    } else {
        aString = nil;
    }
    return aString;
}

@implementation OMEField

static BOOL nomore = NO;
static char buf [ 256 ];

+ (void)initialize
{
    char *p;
    int i;

    if ( nomore == NO ) {
        p = buf;
        for ( i = 0x21; i < 0x3a; i++ ) {
            *p = i;
            p++;
        }
        for ( i = 0x3b; i < 0x7f; i++ ) {
            *p = i;
            p++;
        }
        *p = 0;
        nomore = YES;
    }
}

/* accessor methods */
- (void)setName:(NSString *)aString
{
    name = aString;
}

/* accessor methods */
- (NSString *)name
{
    return name;
}

/* accessor methods */
- (void)setBody:(NSString *)aString
{
    body = aString;
}

/* accessor methods */
- (NSString *)body
{
    return body;
}

/* designated initializer */
- (id)initWithName:(NSString *)aName body:(NSString *)aBody
{
    self = [ super init ];
    if ( self ) {
        [ self setName:aName ];
        [ self setBody:aBody ];
    }
    return self;
}

/* initializer */
- (id)initWithLine:(NSString *)aLine
{
    BOOL flag, flag1, flag2;
    NSUInteger i;
    NSCharacterSet *characterSet, *whitespaceAndNewlineCSet;
    NSScanner *aScanner;
    NSString *aName, *aBody, *aString;

    /* initialization */
    flag = NO;
    /* set up NSCharacterSet instance */
    whitespaceAndNewlineCSet = [ NSCharacterSet
                                   whitespaceAndNewlineCharacterSet ];
    /* set up custom NSCharacterSet instance */
    aString = [ NSString stringWithCString:buf encoding:NSUTF8StringEncoding];
    characterSet = [ NSCharacterSet
                       characterSetWithCharactersInString:aString ];
    /* set up NSScanner instance */
    aScanner = [ NSScanner scannerWithString:aLine ];
    [ aScanner setCharactersToBeSkipped:nil ];
    /* search name & body */
    flag1 = [ aScanner scanCharactersFromSet:characterSet intoString:&aName ];
    if ( flag1 ) {
        flag2 = [ aScanner scanString:@":" intoString:&aString ];
        if ( flag2 ) {
            if ( [ aScanner isAtEnd ] == NO ) {
                i = [ aScanner scanLocation ];
                aBody = [ aLine substringFromIndex:i ];
                flag = YES;
            }
        }
    }
    if ( flag == NO ) {
        aName = nil;
        aBody = aLine;
    }
    aBody = [ aBody stringByTrimmingCharactersInSet:whitespaceAndNewlineCSet ];
    if ( [ aBody isEqualToString:@"" ] ) {
        aBody = nil;
    }
    return [ self initWithName:aName body:aBody ];
}

/* initializer */
- (id)initWithLineOfFile:(NSString *)aPath
{
    NSData *aData;
    NSString *aString;

    aData = [ NSData dataWithContentsOfFile:aPath ];
    aString = [ [ NSString alloc ]
                  initWithData:aData encoding:NSShiftJISStringEncoding ];
    return [ self initWithLine:aString ];
}

/* dealloc */

/* components of field body */
-  (NSArray *)components
{
    NSArray *anArray;
    NSScanner *aScanner;
    NSString *aString;

    anArray = [ NSArray array ];
    aScanner = [ NSScanner scannerWithString:[ self body ] ];
    while ( [ aScanner scanUpToString:@"," intoString:&aString ] ) {
        anArray = [ anArray arrayByAddingObject:aString ];
        [ aScanner scanString:@"," intoString:&aString ];
    }
    return anArray;
}

/* persons of field body */
-  (NSArray *)persons
{
    NSArray *anArray;
    NSScanner *aScanner;
    NSString *aString;
    OMEAddress *anAddress;

    anArray = [ NSArray array ];
    aScanner = [ NSScanner scannerWithString:[ self body ] ];
    while ( [ aScanner scanUpToString:@"," intoString:&aString ] ) {
        anAddress = [ [ OMEAddress alloc ] initWithString:aString ];
        anArray = [ anArray arrayByAddingObject:anAddress ];
        [ aScanner scanString:@"," intoString:&aString ];
    }
    return anArray;
}

/* description */
- (NSString *)description
{
    NSString *aString;

    if ( [ self name ] ) {
        if ( [ self body ] ) {
            aString = [ NSString stringWithFormat:FIELD_FORMAT,
                [ self name ], [ self body ] ];
        } else {
            aString = [ [ self name ] stringByAppendingString:@":" ];
        }
    } else {
        aString = @"    ";
        aString = [ aString stringByAppendingString:[ self body ] ];
    }
    return aString;
}

@end
