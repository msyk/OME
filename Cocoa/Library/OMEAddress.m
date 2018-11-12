//
//  $Id: OMEAddress.m,v 1.4 2005/09/03 13:42:12 wakimoto Exp $
//
//  Mutable Class
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Fri Apr 29 04:24:59 2005 UTC
//  Copyright (c) Shinya Wakimoto, 2005. All rights reserved.
//

#import "OMEAddress.h"

#define ADDRESS_FORMAT @"%@ <%@>"

NSString *OMEAddressPboardType = @"OMEAddressPboardType";

@implementation OMEAddress

/* accessor methods */
- (void)setNameDisplayFlag:(BOOL)flag
{
    nameDisplayFlag = flag;
}

/* accessor methods */
- (BOOL)nameDisplayFlag
{
    return nameDisplayFlag;
}

/* accessor methods */
- (void)setPrefix:(NSString *)aString
{
    [ aString retain ];
    [ prefix release ];
    prefix = aString;
}

/* accessor methods */
- (NSString *)prefix
{
    return prefix;
}

/* accessor methods */
- (void)setName:(NSString *)aString
{
    [ aString retain ];
    [ name release ];
    name = aString;
}

/* accessor methods */
- (NSString *)name
{
    return name;
}

/* accessor methods */
- (void)setSuffix:(NSString *)aString
{
    [ aString retain ];
    [ suffix release ];
    suffix = aString;
}

/* accessor methods */
- (NSString *)suffix
{
    return suffix;
}

/* accessor methods */
- (void)setEmail:(NSString *)aString
{
    [ aString retain ];
    [ email release ];
    email = aString;
}

/* accessor methods */
- (NSString *)email
{
    return email;
}

/* designated initializer */
- (id)initWithNameDisplayFlag:(BOOL)flag
                       prefix:(NSString *)aPrefix
                         name:(NSString *)aName
                       suffix:(NSString *)aSuffix
                        email:(NSString *)eMail
{
    self = [ super init ];
    if ( self ) {
        [ self setNameDisplayFlag:flag ];
        [ self setPrefix:aPrefix ];
        [ self setName:aName ];
        [ self setSuffix:aSuffix ];
        [ self setEmail:eMail ];
    }
    return self;
}

/* initializer */
- (id)initWithPrefix:(NSString *)aPrefix
                name:(NSString *)aName
              suffix:(NSString *)aSuffix
               email:(NSString *)eMail
{
    return [ self
               initWithNameDisplayFlag:YES
               prefix:aPrefix
               name:aName
               suffix:aSuffix
               email:eMail ];
}

/* initializer */
- (id)initWithName:(NSString *)aName
             email:(NSString *)eMail
{
    return [ self
               initWithPrefix:nil
               name:aName
               suffix:nil
               email:eMail ];
}

/* initializer */
- (id)initWithString:(NSString *)aString
{
    NSCharacterSet *whitespaceCharacterSet;
    NSScanner *aScanner;
    NSString *aName, *eMail, *str1, *str2;

    whitespaceCharacterSet = [ NSCharacterSet whitespaceCharacterSet ];
    aScanner = [ NSScanner scannerWithString:aString ];
    if ( [ aScanner scanUpToString:@"<" intoString:&str1 ] ) {
        if ( [ aScanner scanString:@"<" intoString:&str2 ] ) {
            [ aScanner scanUpToString:@">" intoString:&eMail ];
            [ aScanner setScanLocation:0 ];
            [ aScanner scanUpToString:@"\"" intoString:&str2 ];
            if ( [ aScanner scanString:@"\"" intoString:&str2 ] ) {
                [ aScanner scanUpToString:@"\"" intoString:&aName ];
            } else {
                aName = [ str1
                    stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
            }
        } else {
            eMail = str1;
            aName = nil;
        }
    } else {
        if ( [ aScanner scanString:@"<" intoString:&str2 ] ) {
            [ aScanner scanUpToString:@">" intoString:&eMail ];
        } else {
            eMail = nil;
        }
        aName = nil;
    }
    return [ self initWithName:aName email:eMail ];
}

/* encode */
- (void)encodeWithCoder:(NSCoder *)aCoder
{
    [ aCoder encodeValueOfObjCType:@encode(BOOL) at:&nameDisplayFlag ];
    [ aCoder encodeObject:[ self prefix ] ];
    [ aCoder encodeObject:[ self name ] ];
    [ aCoder encodeObject:[ self suffix ] ];
    [ aCoder encodeObject:[ self email ] ];
}

/* decode */
- (id)initWithCoder:(NSCoder *)aCoder
{
    self = [ super init ];
    if ( self ) {
        [ aCoder decodeValueOfObjCType:@encode(BOOL) at:&nameDisplayFlag ];
        [ self setPrefix:[ aCoder decodeObject ] ];
        [ self setName:[ aCoder decodeObject ] ];
        [ self setSuffix:[ aCoder decodeObject ] ];
        [ self setEmail:[ aCoder decodeObject ] ];
    }
    return self;
}

/* dealloc */
- (void)dealloc
{
    [ prefix release ];
    [ name release ];
    [ suffix release ];
    [ email release ];
    [ super dealloc ];
}

/* description */
- (NSString *)description
{
    NSString *aString;

    if ( nameDisplayFlag && [ self name ] ) {
        aString = [ self name ];
        if ( [ self prefix ] ) {
            aString = [ [ self prefix ]
                  stringByAppendingFormat:@" %@", aString ];
        }
        if ( [ self suffix ] ) {
            aString = [ aString
                  stringByAppendingFormat:@" %@", [ self suffix ] ];
        }
        if ( [ self email ] ) {
            aString = [ NSString
                  stringWithFormat:ADDRESS_FORMAT, aString, [ self email ] ];
        } else {
            aString = [ self name ];
        }
    } else {
        if ( [ self email ] ) {
            aString = [ self email ];
        } else {
            aString = nil;
        }
    }
    return aString;
}

/* compare */
- (NSComparisonResult)compare:(OMEAddress *)aPerson
{
    NSString *myName, *aName;

    if ( [ self name ] ) {
        myName = [ self name ];
    } else {
        myName = @"";
    }
    aName = [ aPerson name ];
    if ( aName == nil ) {
        aName = @"";
    }
    return [ myName compare:aName ];
}

@end
