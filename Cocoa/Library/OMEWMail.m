//
//  $Id: OMEWMail.m,v 1.6 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Feb 6 17:53:57 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "OMEWMail.h"

#import "OMEField.h"
#import "OMELines.h"
#import "WSHeader.h"
#import "WSMutableHeader.h"

@implementation OMEWMail

/*accessor method */
- (void)setUpperFields:(NSArray *)anArray
{
    [ anArray retain ];
    [ upperFields release ];
    upperFields = anArray;
}

/*accessor method */
- (NSArray *)upperFields
{
    return upperFields;
}

/* accessor method */
- (void)setMessageBody:(NSString *)aString
{
    [ aString retain ];
    [ messageBody release ];
    messageBody = aString;
}

/*accessor method */
- (NSString *)messageBody
{
    return messageBody;
}

/* designated initializer */
- (id)initWithUpperFields:(NSArray *)uFields messageBody:(NSString *)aString
{
    self = [ super init ];
    if ( self ) {
        [ self setUpperFields:uFields ];
        [ self setMessageBody:aString ];
    }
    return self;
}

/* initializer */
- (id)initWithUpperLines:(NSArray *)uLines messageBody:(NSString *)aString
{
    NSArray *uFields;

    uFields = OMEFieldsFromLines ( uLines );
    return [ self initWithUpperFields:uFields messageBody:aString ];
}

/* initializer */
- (id)initWithString:(NSString *)aString
{
    unsigned startIndex, lineEndIndex, contentsEndIndex, length;
    NSRange aRange;
    NSArray *anArray;
    NSString *aLine, *bodyString;

    anArray = [ NSArray array ];
    aRange = NSMakeRange ( 0, 0 );
    [ aString
        getLineStart:&startIndex
        end:&lineEndIndex
        contentsEnd:&contentsEndIndex
        forRange:aRange ];
    while ( startIndex != lineEndIndex ) {
        length = contentsEndIndex - startIndex;
        if ( length == 0 ) break;
        aRange = NSMakeRange ( startIndex, length );
        aLine = [ aString substringWithRange:aRange ]; 
        anArray = [ anArray arrayByAddingObject:aLine ];
        if ( lineEndIndex == contentsEndIndex ) break;
        aRange = NSMakeRange ( lineEndIndex, 0 );
        [ aString
            getLineStart:&startIndex
            end:&lineEndIndex
            contentsEnd:&contentsEndIndex
            forRange:aRange ];
    }
    bodyString = [ aString substringFromIndex:lineEndIndex ];
    return [ self initWithUpperLines:anArray messageBody:bodyString ];
}

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath
{
    NSData *aData;
    NSString *aString;

    /* read Shift-JIS file */
    aData = [ NSData dataWithContentsOfFile:aPath ];
    /* make Unicode string */
    aString = [ [ [ NSString alloc ]
                    initWithData:aData encoding:NSShiftJISStringEncoding ]
                  autorelease ];
    return [ self initWithString:aString ];
}

/* dealloc */
- (void)dealloc
{
    [ upperFields release ];
    [ messageBody release ];
    [ super dealloc ];
}

/* header */
- (WSHeader *)header
{
    WSMutableHeader *wsHeader;

    wsHeader = [ [ [ WSMutableHeader alloc ]
                     initWithFields:[ self upperFields ] ] autorelease ];
    [ wsHeader setFromWithFromName ];
    return wsHeader;
}

/* message body string */
- (NSString *)messageBodyString
{
    unichar *buf;
    NSArray *anArray;
    NSString *aString, *fullWidthTilde;

    /* Define Full-Width-Tilde */
    buf = malloc ( 256 );
    buf [ 0 ] = 0xff5e;
    fullWidthTilde = [ NSString stringWithCharacters:buf length:1 ];
    free ( buf );
    /* replace full width tildes with @"~" */
    anArray = [ [ self messageBody ]
                  componentsSeparatedByString:fullWidthTilde ];
    aString = [ anArray componentsJoinedByString:@"~" ];
    return aString;
}

/* description */
- (NSString *)description
{
    NSString *aString, *uString, *bodyString;

    aString = @"";
    /* upper fields */
    uString = OMEStringFromFields ( [ self upperFields ] );
    aString = [ aString stringByAppendingString:uString ];
    aString = [ aString stringByAppendingString:@"\n" ];
    /* message body */
    bodyString = [ self messageBodyString ];
    /* return */
    aString = [ aString stringByAppendingString:bodyString ];
    return aString;
}

@end
