//
//  $Id: WSMutableHeader.m,v 1.6 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Feb 6 17:53:57 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "WSMutableHeader.h"

#import "OMELog.h"
#import "OMEAddress.h"

/* Log Format */
#define REMOVE_FORMAT @"OME # Remove the address from To/CC/BCC: %@\n"

@implementation WSMutableHeader

/* accessor method */
- (void)setAttachment:(NSArray *)anArray
{
    [ anArray retain ];
    [ attachment release ];
    attachment = anArray;
}

/* accessor method */
- (void)setBcc:(NSArray *)anArray
{
    [ anArray retain ];
    [ bcc release ];
    bcc = anArray;
}

/* accessor method */
- (void)setCc:(NSArray *)anArray
{
    [ anArray retain ];
    [ cc release ];
    cc = anArray;
}

/* accessor method */
- (void)setDate:(NSCalendarDate *)aDate
{
    [ aDate retain ];
    [ date release ];
    date = aDate;
}

/* accessor method */
- (void)setFrom:(OMEAddress *)aPerson
{
    [ aPerson retain ];
    [ from release ];
    from = aPerson;
}

/* accessor method */
- (void)setFromName:(NSString *)aString
{
    [ aString retain ];
    [ fromName release ];
    fromName = aString;
}

/* accessor method */
- (void)setInReplyTo:(NSString *)aString
{
    [ aString retain ];
    [ inReplyTo release ];
    inReplyTo = aString;
}

/* accessor method */
- (void)setMessageID:(NSString *)aString
{
    [ aString retain ];
    [ messageID release ];
    messageID = aString;
}

/* accessor method */
- (void)setReferences:(NSString *)aString
{
    [ aString retain ];
    [ references release ];
    references = aString;
}

/* accessor method */
- (void)setReplyTo:(OMEAddress *)aPerson
{
    [ aPerson retain ];
    [ replyTo release ];
    replyTo = aPerson;
}

/* accessor method */
- (void)setSubject:(NSString *)aString
{
    [ aString retain ];
    [ subject release ];
    subject = aString;
}

/* accessor method */
- (void)setTo:(NSArray *)anArray
{
    [ anArray retain ];
    [ to release ];
    to = anArray;
}

/* accessor method */
- (void)setXOMELocale:(NSString *)aString
{
    [ aString retain ];
    [ xOMELocale release ];
    xOMELocale = aString;
}

/* accessor method */
- (void)setXOriginalMessageId:(NSString *)aString
{
    [ aString retain ];
    [ xOriginalMessageId release ];
    xOriginalMessageId = aString;
}

/* set Bcc */
- (void)setBccWithPerson:(OMEAddress *)aPerson
{
    NSArray *anArray;

    anArray = [ NSArray arrayWithObject:aPerson ];
    [ self setBcc:anArray ];
}

/* add Bcc */
- (void)addBcc:(NSArray *)anArray
{
    NSArray *bArray;

    bArray = [ self bcc ];
    bArray = [ bArray arrayByAddingObjectsFromArray:anArray ];
    [ self setBcc:bArray ];
}

/* add Bcc */
- (void)addBccWithPerson:(OMEAddress *)aPerson
{
    NSArray *anArray;

    anArray = [ self bcc ];
    anArray = [ anArray arrayByAddingObject:aPerson ];
    [ self setBcc:anArray ];
}

/* set Cc */
- (void)setCcWithPerson:(OMEAddress *)aPerson
{
    NSArray *anArray;

    anArray = [ NSArray arrayWithObject:aPerson ];
    [ self setCc:anArray ];
}

/* add Cc */
- (void)addCc:(NSArray *)anArray
{
    NSArray *bArray;

    bArray = [ self cc ];
    bArray = [ bArray arrayByAddingObjectsFromArray:anArray ];
    [ self setCc:bArray ];
}

/* add Cc */
- (void)addCcWithPerson:(OMEAddress *)aPerson
{
    NSArray *anArray;

    anArray = [ self cc ];
    anArray = [ anArray arrayByAddingObject:aPerson ];
    [ self setCc:anArray ];
}

/* set Date to Now*/
- (void)setDateToNow
{
    NSCalendarDate *aDate;

    aDate = [ NSCalendarDate calendarDate ];
    [ self setDate:aDate ];
}

/* set From with FromName */
- (void)setFromWithFromName
{
    NSString *oldName, *newName, *email;
    OMEAddress *anAddress;

    anAddress = [ self from ];
    oldName = [ anAddress name ];
    email = [ anAddress email ];
    if ( oldName == nil ) {
        newName = [ self fromName ];
        if ( newName ) {
            anAddress = [ [ OMEAddress alloc ]
                            initWithName:newName email:email ];
            [ self setFrom:anAddress ];
            [ anAddress release ];
        }
    }
}

/* set To */
- (void)setToWithPerson:(OMEAddress *)anAddress
{
    NSArray *anArray;

    anArray = [ NSArray arrayWithObject:anAddress ];
    [ self setTo:anArray ];
}

/* add To */
- (void)addTo:(NSArray *)anArray
{
    NSArray *bArray;

    bArray = [ self to ];
    bArray = [ bArray arrayByAddingObjectsFromArray:anArray ];
    [ self setTo:bArray ];
}

/* add To */
- (void)addToWithPerson:(OMEAddress *)anAddress
{
    NSArray *anArray;

    anArray = [ self to ];
    anArray = [ anArray arrayByAddingObject:anAddress ];
    [ self setTo:anArray ];
}

/* local method */
- (NSArray *)arrayByRemovingObjectIncludingString:(NSString *)aString
                                        fromArray:(NSArray *)anArray
{
    NSArray *newArray;
    NSComparisonResult result;
    NSEnumerator *anEnumerator;
    NSString *eString;
    OMEAddress *anAddress;

    newArray = [ NSArray array ];
    anEnumerator = [ anArray objectEnumerator ];
    while ( ( anAddress = [ anEnumerator nextObject ] ) != nil ) {
        eString = [ anAddress email ];
        result = [ eString caseInsensitiveCompare:aString ];
        if ( result != NSOrderedSame ) {
            newArray = [ newArray arrayByAddingObject:anAddress ];
        }
    }
    if ( [ newArray count ] == 0 ) {
        newArray = nil;
    }
    return newArray;
}

/* RemoveToMe */
- (void)removeToMeWithAddress:(NSString *)aString
{
    NSArray *anArray;
    NSString *logString;

    /* Bcc: */
    anArray = [ self bcc ];
    anArray = [ self
                  arrayByRemovingObjectIncludingString:aString
                  fromArray:anArray ];
    [ self setBcc:anArray ];
    /* Cc: */
    anArray = [ self cc ];
    anArray = [ self
                  arrayByRemovingObjectIncludingString:aString
                  fromArray:anArray ];
    [ self setCc:anArray ];
    /* To: */
    anArray = [ self to ];
    anArray = [ self
                  arrayByRemovingObjectIncludingString:aString
                  fromArray:anArray ];
    [ self setTo:anArray ];
    /* log */
    logString = [ NSString stringWithFormat:REMOVE_FORMAT, aString ];
    [ OMELog writeToMailwriterLogWithString:logString ];
}

/* RemoveToMeAll */
- (void)removeToMeAllWithAddresses:(NSArray *)addresses
{
    NSEnumerator *anEnumerator;
    NSString *aString;

    anEnumerator = [ addresses objectEnumerator ];
    while ( ( aString = [ anEnumerator nextObject ] ) != nil ) {
        [ self removeToMeWithAddress:aString ];
    }
}

@end
