//
//  $Id: WSHeader.m,v 1.8 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Feb 6 17:53:57 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "WSHeader.h"

#import "OMEAddress.h"
#import "OMEField.h"

/* Function - array of fields from array of lines */
NSArray *OMEFieldsFromLines ( NSArray *anArray )
{
    BOOL stock;
    NSArray *omeFields;
    NSEnumerator *anEnumerator;
    NSString *aString, *newName, *newBody, *oldName, *oldBody;
    OMEField *newField, *oldField;

    if ( anArray ) {
        stock = NO;
        oldName = nil;
        oldBody = nil;
        omeFields = [ NSArray array ];
        anEnumerator = [ anArray objectEnumerator ];
        while ( ( aString = [ anEnumerator nextObject ] ) != nil ) {
            newField = [ [ OMEField alloc ] initWithLine:aString ];
            newName = [ newField name ];
            newBody = [ newField body ];
            if ( newName ) {
                if ( stock ) {
                    oldField = [ [ OMEField alloc ]
                                   initWithName:oldName body:oldBody ];
                    omeFields = [ omeFields arrayByAddingObject:oldField ];
                    [ oldField release ];
                }
                oldName = [ NSString stringWithString:newName ];
                if ( newBody ) {
                    oldBody = [ NSString stringWithString:newBody ];
                } else {
                    oldBody = nil;
                }
                stock = YES;
            } else {
                if ( newBody ) {
                    oldBody = [ oldBody stringByAppendingString:newBody ];
                    stock = YES;
                } else {
                    oldField = [ [ OMEField alloc ]
                        initWithName:oldName body:oldBody ];
                    omeFields = [ omeFields arrayByAddingObject:oldField ];
                    [ oldField release ];
                    oldName = nil;
                    oldBody = nil;
                    stock = NO;
                }
            } 
            [ newField release ];
        }
        oldField = [ [ OMEField alloc ] initWithName:oldName body:oldBody ];
        omeFields = [ omeFields arrayByAddingObject:oldField ];
        [ oldField release ];
    } else {
        omeFields = nil;
    }
    return omeFields;
}

/* string from array of fields */
NSString *OMEStringFromFields ( NSArray *fields )
{
    NSEnumerator *anEnumerator;
    NSString *aString, *line;
    OMEField *field;

    if ( fields ) {
        aString = @"";
        anEnumerator = [ fields objectEnumerator ];
        while ( ( field = [ anEnumerator nextObject ] ) != nil ) {
            line = [ field description ];
            aString = [ aString stringByAppendingString:line ];
            aString = [ aString stringByAppendingString:EOL ];
        }
    } else {
        aString = nil;
    }
    return aString;
}

@implementation WSHeader

/* accessor method */
- (NSArray *)attachment { return attachment; }   /* Attachment: */
- (NSArray *)bcc { return bcc; }                 /* Bcc: */
- (NSArray *)cc { return cc; }                   /* Cc: */
- (NSCalendarDate *)date { return date; }        /* Date: */
- (OMEAddress *)from { return from; }            /* From: */
- (NSString *)fromName { return fromName; }      /* FromName: */
- (NSString *)inReplyTo { return inReplyTo; }    /* In-Reply-To: */
- (NSString *)messageID { return messageID; }    /* Message-ID: */
- (NSString *)references { return references; }  /* References: */
- (OMEAddress *)replyTo { return replyTo; }      /* Reply-To: */
- (NSString *)subject { return subject; }        /* Subject: */
- (NSArray *)to { return to; }                   /* To: */
- (NSString *)xOMELocale { return xOMELocale; }  /* X-OME-Locale: */
- (NSString *)xOriginalMessageId { return xOriginalMessageId; }
                                                 /* X-Original-Message- Id */

/* designated initializer */
- (id)initWithAttachment:(NSArray *)attachmentArray 
                     bcc:(NSArray *)bccArray 
                      cc:(NSArray *)ccArray 
                    date:(NSCalendarDate *)aDate
                    from:(OMEAddress *)fromAddress
                fromName:(NSString *)fromNameString
               inReplyTo:(NSString *)inReplyToString
               messageID:(NSString *)messageIDString
              references:(NSString *)referencesString
                 replyTo:(OMEAddress *)replyToPerson
                 subject:(NSString *)subjectString
                      to:(NSArray *)toArray 
              xOMELocale:(NSString *)xOMELocaleString
      xOriginalMessageId:(NSString *)xOriginalMessageIdString
{
    self = [ super init ];
    if ( self ) {
        attachment = [ attachmentArray retain ];   /* Attachment: */
        bcc = [ bccArray retain ];                 /* Bcc: */
        cc = [ ccArray retain ];                   /* Cc: */
        date = [ aDate retain ];                   /* Date: */
        from = [ fromAddress retain ];              /* From: */
        fromName = [ fromNameString retain ];      /* FromName: */
        inReplyTo = [ inReplyToString retain ];    /* In-Reply-To: */
        messageID = [ messageIDString retain ];    /* Message-ID: */
        references = [ referencesString retain ];  /* References: */
        replyTo = [ replyToPerson retain ];        /* Reply-To: */
        subject = [ subjectString retain ];        /* Subject: */
        to = [ toArray retain ];                   /* To: */
        xOMELocale = [ xOMELocaleString retain ];  /* X-OME-Locale: */
        xOriginalMessageId = [ xOriginalMessageIdString retain ];
        /*X-Original-Message-Id */
    }
    return self;
}

/* init with empty header */
- (id)init
{
    return [ self
               initWithAttachment:nil
               bcc:nil
               cc:nil
               date:nil
               from:nil
               fromName:nil
               inReplyTo:nil
               messageID:nil
               references:nil
               replyTo:nil
               subject:nil
               to:nil
               xOMELocale:nil 
               xOriginalMessageId:nil ];
}

/* init with other header */
- (id)initWithHeader:(WSHeader *)aHeader
{
    NSArray *attachmentArray, *bccArray, *ccArray, *toArray;
    NSCalendarDate *aDate;
    NSString *fromNameString, *inReplyToString, *messageIDString;
    NSString *referencesString, *subjectString;
    NSString *xOMELocaleString, *xOriginalMessageIdString;
    OMEAddress *fromAddress, *replyToPerson;

    attachmentArray = [ aHeader attachment ];   /* Attachment: */
    bccArray = [ aHeader bcc ];                 /* Bcc: */
    ccArray = [ aHeader cc ];                   /* Cc: */
    aDate = [ aHeader date ];                   /* Date: */
    fromAddress = [ aHeader from ];             /* From: */
    fromNameString = [ aHeader fromName ];      /* FromName: */
    inReplyToString = [ aHeader inReplyTo ];    /* In-Reply-To: */
    messageIDString = [ aHeader messageID ];    /* Message-ID: */
    referencesString = [ aHeader references ];  /* References: */
    replyToPerson = [ aHeader replyTo ];        /* Reply-To: */
    subjectString = [ aHeader subject ];        /* Subject: */
    toArray = [ aHeader to ];                   /* To: */
    xOMELocaleString = [ aHeader xOMELocale ];  /* X-OME-Locale */
    xOriginalMessageIdString = [ aHeader xOriginalMessageId ];
                                                /* X-Original-Message=Id */
    return [ self
               initWithAttachment:attachmentArray
               bcc:bccArray
               cc:ccArray
               date:aDate
               from:fromAddress
               fromName:fromNameString
               inReplyTo:inReplyToString
               messageID:messageIDString
               references:referencesString
               replyTo:replyToPerson
               subject:subjectString
               to:toArray
               xOMELocale:xOMELocaleString
               xOriginalMessageId:xOriginalMessageIdString ];
}

/* init with array of fields */
- (id)initWithFields:(NSArray *)fields
{
    /* header fields */
    NSArray *attachments, *bccArray, *ccArray, *toArray;
    NSCalendarDate *aDate;
    NSString *fromNameString, *inReplyToString, *messageIDString;
    NSString *referencesString, *subjectString, *xOMELocaleString;
    NSString *xOriginalMessageIdString;
    /* local instances */
    NSArray *objects, *keys;
    NSArray *components, *persons;
    NSEnumerator *anEnumerator;
    NSString *aString;
    OMEField *field;
    OMEAddress *fromAddress, *replyToAddress;

    /*initializer */
    attachments = nil;              /* Attachment: */
    bccArray = nil;                 /* Bcc: */
    ccArray = nil;                  /* Cc: */
    aDate = nil;                    /* Date: */
    fromAddress = nil;              /* From: */
    fromNameString = nil;           /* FromName: */
    inReplyToString = nil;          /* In-Reply-To: */
    messageIDString = nil;          /* Message-ID: */
    referencesString = nil;         /* References: */
    replyToAddress = nil;           /* Reply-To: */
    subjectString = nil;            /* Subject: */
    toArray = nil;                  /* To: */
    xOMELocaleString = nil;         /* X-OME-Locale */
    xOriginalMessageIdString = nil; /* X-Original-Message-Id */
    /* enumerate fields */
    anEnumerator = [ fields objectEnumerator ];
    objects = [ NSArray array ];
    keys = [ NSArray array ];
    while ( ( field = [ anEnumerator nextObject ] ) != nil ) {
        aString = [ [ field name ] lowercaseString ];
        /* Attachment: */
        if ( [ aString isEqualToString:ATTACHMENT_L ] ) {
            components = [ field components ];
            if ( attachments ) {
                attachments = [ attachments
                                  arrayByAddingObjectsFromArray:components ];
            } else {
                attachments = components;
            }
            continue;
        }
        /* Bcc: */
        if ( [ aString isEqualToString:BCC_L ] ) {
            persons = [ field persons ];
            if ( bccArray ) {
                bccArray = [ bccArray arrayByAddingObjectsFromArray:persons ];
            } else {
                bccArray = persons;
            }
            continue;
        }
        /* Cc: */
        if ( [ aString isEqualToString:CC_L ] ) {
            persons = [ field persons ];
            if ( ccArray ) {
                ccArray = [ ccArray arrayByAddingObjectsFromArray:persons ];
            } else {
                ccArray = persons;
            }
            continue;
        }
        /* Date: */
        if ( [ aString isEqualToString:DATE_L ] ) {
            aDate = [ NSCalendarDate
                        dateWithString:[ field body ]
                        calendarFormat:DATE_FORMAT ];
            if ( aDate == nil ) {
                aDate = [ NSCalendarDate
                            dateWithString:[ field body ]
                            calendarFormat:DATE_FORMAT_AOL ];
                aDate = [ aDate
                            dateWithCalendarFormat:DATE_FORMAT timeZone:nil ];
            }
        }
        /* From: */
        if ( [ aString isEqualToString:FROM_L ] ) {
            fromAddress = [ [ [ OMEAddress alloc ]
                                initWithString:[ field body ] ] autorelease ];
            continue;
        }
        /* FromName: */
        if ( [ aString isEqualToString:FROM_NAME_L ] ) {
            fromNameString = [ field body ];
            continue;
        }
        /* In-Reply-To: */
        if ( [ aString isEqualToString:IN_REPLY_TO_L ] ) {
            inReplyToString = [ field body ];
            continue;
        }
        /* Message-ID: */
        if ( [ aString isEqualToString:MESSAGE_ID_L ] ) {
            messageIDString = [ field body ];
            continue;
        }
        /* References: */
        if ( [ aString isEqualToString:REFERENCES_L ] ) {
            referencesString = [ field body ];
            continue;
        }
        /* Reply-To: */
        if ( [ aString isEqualToString:REPLY_TO_L ] ) {
            replyToAddress = [ [ [ OMEAddress alloc ]
                                   initWithString:[ field body ] ] 
                                 autorelease ];
            continue;
        }
        /* Subject: */
        if ( [ aString isEqualToString:SUBJECT_L ] ) {
            subjectString = [ field body ];
            continue;
        }
        /* To: */
        if ( [ aString isEqualToString:TO_L ] ) {
            persons = [ field persons ];
            if ( toArray ) {
                toArray = [ toArray arrayByAddingObjectsFromArray:persons ];
            } else {
                toArray = persons;
            }
            continue;
        }
        /* X-OME-Locale: */
        if ( [ aString isEqualToString:X_OME_LOCALE_L ] ) {
            xOMELocaleString = [ field body ];
            continue;
        }
        /* X-Original-Message-Id: */
        if ( [ aString isEqualToString:X_ORIGINAL_MESSAGE_ID_L ] ) {
            xOriginalMessageIdString = [ field body ];
            continue;
        }
    }
    return [ self
               initWithAttachment:attachments
               bcc:bccArray
               cc:ccArray
               date:aDate
               from:fromAddress
               fromName:fromNameString
               inReplyTo:inReplyToString
               messageID:messageIDString
               references:referencesString
               replyTo:replyToAddress
               subject:subjectString
               to:toArray
               xOMELocale:xOMELocaleString
               xOriginalMessageId:xOriginalMessageIdString ];
}

/* init with array of lines */
- (id)initWithLines:(NSArray *)anArray
{
    NSArray *fields;

    fields = OMEFieldsFromLines ( anArray );
    return [ self initWithFields:fields ];
}

/* dealloc */
- (void)dealloc
{
    [ attachment release ];         /* Attachment: */
    [ bcc release ];                /* Bcc: */
    [ cc release ];                 /* Cc: */
    [ date release ];               /* Date: */
    [ from release ];               /* From: */
    [ fromName release ];           /* FromName: */
    [ inReplyTo release ];          /* In-Reply-To: */
    [ messageID release ];          /* Message-ID: */
    [ references release ];         /* References: */
    [ replyTo release ];            /* Reply-To: */
    [ subject release ];            /* Subject: */
    [ to release ];                 /* To: */
    [ xOMELocale release ];         /* X-OME-Locale: */
    [ xOriginalMessageId release ]; /* X-Original-Message-Id */
    [ super dealloc ];
}

/* Attachment: */
- (NSString *)attachmentFieldBody
{
    return OMEJoinedComponents ( [ self attachment ] );
}

/* Attachment: */
- (NSString *)attachmentField
{
    NSString *aString;

    aString = [ self attachmentFieldBody ];
    if ( aString ) {
        aString = [ NSString
                      stringWithFormat:FIELD_LF_FORMAT, ATTACHMENT, aString ];
    } 
    return aString;
}

/* Bcc: */
- (NSString *)bccFieldBody
{
    return [ [ self bcc ] componentsJoinedByString:@", " ];
}

/* Bcc: */
- (NSString *)bccField
{
    NSString *aString;

    aString = [ self bccFieldBody ];
    if ( aString ) {
        aString = [ NSString stringWithFormat:FIELD_LF_FORMAT, BCC, aString ];
    } 
    return aString;
}

/* Cc: */
- (NSString *)ccFieldBody
{
    return [ [ self cc ] componentsJoinedByString:@", " ];
}

/* Cc: */
- (NSString *)ccField
{
    NSString *aString;

    aString = [ self ccFieldBody ];
    if ( aString ) {
        aString = [ NSString stringWithFormat:FIELD_LF_FORMAT, CC, aString ];
    } 
    return aString;
}

/* Date: */
- (NSString *)dateFieldBody
{
    return [ [ self date ] descriptionWithCalendarFormat:DATE_FORMAT ];
}

/* Date: */
- (NSString *)dateField
{
    NSString *aString;

    aString = [ self dateFieldBody ];
    if ( aString ) {
        aString = [ NSString stringWithFormat:FIELD_LF_FORMAT, DATE, aString ];
    } 
    return aString;
}

/* From: */
- (NSString *)fromFieldBody
{
    return [ [ self from ] description ];
}

/* From: */
- (NSString *)fromField
{
    NSString *aString;

    aString = [ self fromFieldBody ];
    if ( aString == nil ) {
        aString = @"";
    }
    return [ NSString stringWithFormat:FIELD_LF_FORMAT, FROM, aString ];
}

/* FromName: */
- (NSString *)fromNameFieldBody
{
    return [ self fromName ];
}

/* FromName: */
- (NSString *)fromNameField
{
    NSString *aString;

    aString = [ self fromNameFieldBody ];
    if ( aString ) {
        aString =  [ NSString
                       stringWithFormat:FIELD_LF_FORMAT, FROM_NAME, aString ];
    }
    return aString;
}

/* In-Reply-To: */
- (NSString *)inReplyToFieldBody
{
    return [ self inReplyTo ];
}

/* In-Reply-To: */
- (NSString *)inReplyToField
{
    NSString *aString;

    aString = [ self inReplyToFieldBody ];
    if ( aString ) {
        aString = [ NSString
            stringWithFormat:FIELD_LF_FORMAT, IN_REPLY_TO, aString ];
    }
    return aString;
}

/* Message-ID: */
- (NSString *)messageIDFieldBody
{
    return [ self messageID ];
}

/* Message-ID: */
- (NSString *)messageIDField
{
    NSString *aString;

    aString = [ self messageIDFieldBody ];
    if ( aString ) {
        aString = [ NSString
            stringWithFormat:FIELD_LF_FORMAT, MESSAGE_ID, aString ];
    }
    return aString;
}

/* References: */
- (NSString *)referencesFieldBody
{
    return [ self references ];
}

/* References: */
- (NSString *)referencesField
{
    NSString *aString;

    aString = [ self referencesFieldBody ];
    if ( aString ) {
        aString = [ NSString
            stringWithFormat:FIELD_LF_FORMAT, REFERENCES, aString ];
    }
    return aString;
}

/* Reply-To: */
- (NSString *)replyToFieldBody
{
    return [ [ self replyTo ] description ];
}

/* Reply-To: */
- (NSString *)replyToField
{
    NSString *aString;

    aString = [ self replyToFieldBody ];
    if ( aString ) {
        aString = [ NSString
            stringWithFormat:FIELD_LF_FORMAT, REPLY_TO, aString ];
    }
    return aString;
}

/* Subject: */
- (NSString *)subjectFieldBody
{
    return [ self subject ];
}

/* Subject: */
- (NSString *)subjectField
{
    NSString *aString;

    aString = [ self subject ];
    if ( aString == nil ) {
        aString = @"";
    }
    return [ NSString stringWithFormat:FIELD_LF_FORMAT, SUBJECT, aString ];
}

/* To: */
- (NSString *)toFieldBody
{
    return [ [ self to ] componentsJoinedByString:@", " ];
}

/* To: */
- (NSString *)toField
{
    NSString *aString;

    aString = [ self toFieldBody ];
    if ( aString == nil ) {
        aString = @"";
    }
    return [ NSString stringWithFormat:FIELD_LF_FORMAT, TO, aString ];
}

/* X-OME-Locale: */
- (NSString *)xOMELocaleFieldBody
{
     return [ self xOMELocale ];
}

/* X-OME-Locale: */
- (NSString *)xOMELocaleField
{
    NSString *aString;

    aString = [ self xOMELocaleFieldBody ];
    if ( aString ) {
        aString = [ NSString
            stringWithFormat:FIELD_LF_FORMAT, X_OME_LOCALE, aString ];
    }
    return aString;
}

/* X-Original-Message-Id: */
- (NSString *)xOriginalMessageIdFieldBody
{
    return [ self xOriginalMessageId ];
}

/* X-Original-Message-Id: */
- (NSString *)xOriginalMessageIdField
{
    NSString *aString;

    aString = [ self xOriginalMessageIdFieldBody ];
    if ( aString ) {
        aString = [ NSString
            stringWithFormat:FIELD_LF_FORMAT, X_ORIGINAL_MESSAGE_ID, aString ];
    }
    return aString;
}

/* local method */
- (BOOL)isArray:(NSArray *)anArray includeString:(NSString *)aString
{
    BOOL flag;
    NSComparisonResult result;
    NSEnumerator *anEnumerator;
    NSString *eString;
    OMEAddress *aPerson;

    flag = NO;
    anEnumerator = [ anArray objectEnumerator ];
    while ( ( aPerson = [ anEnumerator nextObject ] ) != nil ) {
        eString = [ aPerson email ];
        result = [ eString caseInsensitiveCompare:aString ];
        if ( result == NSOrderedSame ) {
            flag = YES;
            break;
        }
    }
    return flag;
}

/* search */
- (BOOL)isBccIncludeString:(NSString *)aString
{
    NSArray *anArray;

    anArray = [ self bcc ];
    return [ self isArray:anArray includeString:aString ];
}

/* search */
- (BOOL)isCcIncludeString:(NSString *)aString
{
    NSArray *anArray;

    anArray = [ self cc ];
    return [ self isArray:anArray includeString:aString ];
}

/* search */
- (BOOL)isToIncludeString:(NSString *)aString
{
    NSArray *anArray;

    anArray = [ self to ];
    return [ self isArray:anArray includeString:aString ];
}

/* description */
- (NSString *)description
{
    NSString *aString, *objectString;

    /* initialization */
    aString = @"\n";
    /* Attachment: */
    objectString = [ self attachmentField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* Bcc: */
    objectString = [ self bccField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* Cc: */
    objectString = [ self ccField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* Date: */
    objectString = [ self dateField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* From: */
    objectString = [ self fromField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* FromName: */
    objectString = [ self fromNameField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* In-Reply-To: */
    objectString = [ self inReplyToField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* Message-ID: */
    objectString = [ self messageIDField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* References: */
    objectString = [ self referencesField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* Reply-To: */
    objectString = [ self replyToField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* Subject: */
    objectString = [ self subjectField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* To: */
    objectString = [ self toField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* X-OME-Locale: */
    objectString = [ self xOMELocaleField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    /* X-Original-Message-Id: */
    objectString = [ self xOriginalMessageIdField ];
    if ( objectString ) {
        aString = [ aString stringByAppendingString:objectString ];
    }
    return aString;
}

/* dictionary without NSArray instances as entry */
- (NSDictionary *)messageHeadersForNSMailDelivery
{
    NSArray *objects, *keys;

    /* initialization */
    objects = [ NSArray array ];
    keys = [ NSArray array ];
    /* Bcc: */
    if ( [ self bcc ] ) {
        objects = [ objects arrayByAddingObject:[ self bccFieldBody ] ];
        keys = [ keys arrayByAddingObject:BCC ];
    }
    /* Cc: */
    if ( [ self cc ] ) {
        objects = [ objects arrayByAddingObject:[ self ccFieldBody ] ];
        keys = [ keys arrayByAddingObject:CC ];
    }
    /* Date: */
    if ( [ self date ] ) {
        objects = [ objects arrayByAddingObject:[ self dateFieldBody ] ];
        keys = [ keys arrayByAddingObject:DATE ];
    }
    /* From: */
    if ( [ self from ] ) {
        objects = [ objects arrayByAddingObject:[ self fromFieldBody ] ];
        keys = [ keys arrayByAddingObject:FROM ];
    }
    /* In-Reply-To: */
    if ( [ self inReplyTo ] ) {
        objects = [ objects arrayByAddingObject:[ self inReplyToFieldBody ] ];
        keys = [ keys arrayByAddingObject:IN_REPLY_TO ];
    }
    /* References: */
    if ( [ self references ] ) {
        objects = [ objects arrayByAddingObject:[ self referencesFieldBody ] ];
        keys = [ keys arrayByAddingObject:REFERENCES ];
    }
    /* Subject */
    if ( [ self subject ] ) {
        objects = [ objects arrayByAddingObject:[ self subjectFieldBody ] ];
        keys = [ keys arrayByAddingObject:SUBJECT ];
    }
    /* To: */
    if ( [ self to ] ) {
        objects = [ objects arrayByAddingObject:[ self toFieldBody ] ];
        keys = [ keys arrayByAddingObject:TO ];
    }
    return [ NSDictionary dictionaryWithObjects:objects forKeys:keys ];
}

@end
