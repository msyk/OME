//
//  $Id: OMEMail.m,v 1.7 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Tue Jan 18 15:33:26 2005 UTC
//  Copyright (c) Shinya Wakimoto, 2005. All rights reserved.
//

#import "OMEMail.h"

#import "OMEField.h"
#import "OMELines.h"
#import "WSHeader.h"

/* format */
#define C_FORMAT @"%Y/%m/%d"
#define T_FORMAT @"%Y/%m/%d %H:%M:%S"
#define FORMAT @"%@\n\n%@\n\n%@\n\n%@\n"

/* mail body */
#define LINE_SEPARATOR_A @"<!-- Real Part Headers -->"
#define LINE_SEPARATOR_B @"<!-- Real Mail Headers -->"

@implementation OMEMail

/* accessor method */
- (void)setUpperFields:(NSArray *)anArray
{
    upperFields = anArray;
}

/*accessor method */
- (NSArray *)upperFields
{
    return upperFields;
}

/* accessor method */
- (void)setMessageBody:(NSArray *)anArray
{
    messageBody = anArray;
}

/*accessor method */
- (NSArray *)messageBody
{
    return messageBody;
}

/* accessor method */
- (void)setMiddleFields:(NSArray *)anArray
{
    middleFields = anArray;
}

/*accessor method */
- (NSArray *)middleFields
{
    return middleFields;
}

/* accessor method */
- (void)setLowerFields:(NSArray *)anArray
{
    lowerFields = anArray;
}

/*accessor method */
- (NSArray *)lowerFields
{
    return lowerFields;
}

/* accessor method */
- (void)setUpperHeader:(WSHeader *)aHeader
{
    upperHeader = aHeader;
}

/*accessor method */
- (WSHeader *)upperHeader
{
    return upperHeader;
}

/* accessor method */
- (void)setLowerHeader:(WSHeader *)aHeader
{
    lowerHeader = aHeader;
}

/*accessor method */
- (WSHeader *)lowerHeader
{
    return lowerHeader;
}

/* designated initializer */
- (id)initWithUpperFields:(NSArray *)uFields
              messageBody:(NSArray *)bodyLines
             middleFields:(NSArray *)mFields
              lowerFields:(NSArray *)lFields
{
    WSHeader *uHeader, *lHeader;

    self = [ super init ];
    if ( self ) {
        /* set up essensial instances */
        [ self setUpperFields:uFields ];
        [ self setMessageBody:bodyLines ];
        [ self setMiddleFields:mFields ];
        [ self setLowerFields:lFields ];
        /* set up WSHeader instances */
        uHeader = [ [ WSHeader alloc ] initWithFields:[ self upperFields ] ];
        lHeader = [ [ WSHeader alloc ] initWithFields:[ self lowerFields ] ];
        [ self setUpperHeader:uHeader ];
        [ self setLowerHeader:lHeader ];
    }
    return self;
}

/* initializer */
- (id)initWithUpperLines:(NSArray *)uLines
             messageBody:(NSArray *)bodyLines
             middleLines:(NSArray *)mLines
              lowerLines:(NSArray *)lLines
{
    NSArray *uFields, *mFields, *lFields;

    uFields = OMEFieldsFromLines ( uLines );
    mFields = OMEFieldsFromLines ( mLines );
    lFields = OMEFieldsFromLines ( lLines );
    return [ self
               initWithUpperFields:uFields
               messageBody:bodyLines
               middleFields:mFields
               lowerFields:lFields ];
}

/* initializer */
- (id)initWithLines:(NSArray *)lines
{
    NSUInteger count, i1, i2, i3;
    NSArray *uLines, *bodyLines, *mLines, *lLines;
    NSRange aRange;

    /* separate lines */
    count = [ lines count ];
    i1 = [ lines indexOfObject:@"" ];
    i2 = [ lines indexOfObject:LINE_SEPARATOR_A ];
    i3 = [ lines indexOfObject:LINE_SEPARATOR_B ];
    if ( i2 == NSNotFound ) i2 = i3;
    /* message headers & body of message */
    aRange = NSMakeRange ( 0, i1 );
    uLines = [ lines subarrayWithRange:aRange ];
    aRange = NSMakeRange ( i1 + 1, i2 - i1 - 1 );
    bodyLines = [ lines subarrayWithRange:aRange ];
    if ( i2 == i3 ) {
        mLines = nil;
    } else {
        aRange = NSMakeRange ( i2 + 1, i3 - i2 - 1 );
        mLines = [ lines subarrayWithRange:aRange ];
    }
    aRange = NSMakeRange ( i3 + 1, count - i3 - 1 );
    lLines = [ lines subarrayWithRange:aRange ];
    return [ self
               initWithUpperLines:uLines
               messageBody:(NSArray *)bodyLines
               middleLines:mLines
               lowerLines:lLines ];
}

/* initializer */
- (id)initWithString:(NSString *)aString
{
    NSArray *lines;
    OMELines *omeLines;

    omeLines = [ OMELines linesWithString:aString ];
    lines = [ omeLines array ];
    return [ self initWithLines:lines ];
}

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath
{
    NSData *aData;
    NSString *aString;

    aData = [ NSData dataWithContentsOfFile:aPath ];
    aString = [ [ NSString alloc ]
                    initWithData:aData encoding:NSShiftJISStringEncoding ];
    return [ self initWithString:aString ];
}

/* dealloc */

/* description */
- (NSString *)description
{
    NSString *aString, *uString, *bodyString, *mString, *lString;

    aString = @"\n";
    /* upper fields */
    uString = OMEStringFromFields ( [ self upperFields ] );
    aString = [ aString stringByAppendingString:uString ];
    aString = [ aString stringByAppendingString:@"\n" ];
    /* message body */
    bodyString = [ [ self messageBody ] componentsJoinedByString:@"\n" ],
    aString = [ aString stringByAppendingString:bodyString ];
    aString = [ aString stringByAppendingString:@"\n" ];
    /* middle fields */
    if ( [ self middleFields ] ) {
        aString = [ aString stringByAppendingString:LINE_SEPARATOR_A ];
        aString = [ aString stringByAppendingString:@"\n" ];
        mString = OMEStringFromFields ( [ self middleFields ] );
        aString = [ aString stringByAppendingString:mString ];
        aString = [ aString stringByAppendingString:@"\n" ];
    }
    /* lower fields */
    aString = [ aString stringByAppendingString:LINE_SEPARATOR_B ];
    aString = [ aString stringByAppendingString:@"\n" ];
    lString = OMEStringFromFields ( [ self lowerFields ] );
    aString = [ aString stringByAppendingString:lString ];
    aString = [ aString stringByAppendingString:@"\n" ];
    /* return */
    return aString;
}

/* header */
- (id)header:(SEL)method
{
    id object;

    object = [ [ self upperHeader ] performSelector:method ];
    if ( object == nil ) {
        object = [ [ self lowerHeader ] performSelector:method ];
    }
    return object;
}

/* reply comment */
- (NSString *)replyCommentWithFormat:(NSString *)aFormat
{
    NSArray *anArray;
    NSCalendarDate *aDate;
    NSString *aString, *date, *from, *messageID, *subject, *to;

    /* basic information */
    aDate = [ self header:@selector( date ) ];
    date = [ self header:@selector( dateFieldBody ) ];
    from = [ self header:@selector( fromFieldBody ) ];
    messageID = [ self header:@selector( messageIDFieldBody ) ];
    subject = [ self header:@selector( subjectFieldBody ) ];
    to = [ self header:@selector( toFieldBody ) ];
    /* make up NSString */
    aString = [ NSString stringWithString:aFormat ];
    anArray = [ aString componentsSeparatedByString:@"%c" ];
    aString = [ anArray
                  componentsJoinedByString:
                      [ aDate descriptionWithCalendarFormat:C_FORMAT ] ];
    anArray = [ aString componentsSeparatedByString:@"%d" ];
    aString = [ anArray componentsJoinedByString:date ];
    anArray = [ aString componentsSeparatedByString:@"%f" ];
    aString = [ anArray componentsJoinedByString:from ];
    anArray = [ aString componentsSeparatedByString:@"%i" ];
    aString = [ anArray componentsJoinedByString:messageID ];
    anArray = [ aString componentsSeparatedByString:@"%r" ];
    aString = [ anArray componentsJoinedByString:to ];
    anArray = [ aString componentsSeparatedByString:@"/r" ];
    aString = [ anArray componentsJoinedByString:@"\n" ];
    anArray = [ aString componentsSeparatedByString:@"%s" ];
    aString = [ anArray componentsJoinedByString:subject ];
    anArray = [ aString componentsSeparatedByString:@"%t" ];
    aString = [ anArray
                  componentsJoinedByString:
                      [ aDate descriptionWithCalendarFormat:T_FORMAT ] ];
    aString = [ aString stringByAppendingString:@"\n" ];
    return aString;
}

@end
