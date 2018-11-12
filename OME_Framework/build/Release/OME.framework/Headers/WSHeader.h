//
//  $Id: WSHeader.h,v 1.6 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Feb 6 17:53:57 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

/* "Date:" format */
#define DATE_FORMAT @"%a, %e %b %Y %H:%M:%S %z"
#define DATE_FORMAT_AOL @"%a, %e %b %Y %H:%M:%S %Z"

/* Function - array of fields from array of lines */
NSArray *OMEFieldsFromLines ( NSArray *anArray );

/* string from array of fields */
NSString *OMEStringFromFields ( NSArray *fields );

@class OMEAddress;

@interface WSHeader : NSObject
{
    NSArray *attachment;          /* Attachment: */
    NSArray *bcc;                 /* Bcc: */
    NSArray *cc;                  /* Cc: */
    NSCalendarDate *date;         /* Date: */
    OMEAddress *from;             /* From: */
    NSString *fromName;           /* FromName: */
    NSString *inReplyTo;          /* In-Reply-To: */
    NSString *messageID;          /* Message-ID: */
    NSString *references;         /* References: */
    OMEAddress *replyTo;          /* Reply-To: */
    NSString *subject;            /* Subject: */
    NSArray *to;                  /* To: */
    NSString *xOMELocale;         /* X-OME-Locale: */
    NSString *xOriginalMessageId; /* X-Original-Message-Id */
}

/*accessor methods */
- (NSArray *)attachment;          /* Attachment: */
- (NSArray *)bcc;                 /* Bcc: */
- (NSArray *)cc;                  /* Cc: */
- (NSCalendarDate *)date;         /* Date: */
- (OMEAddress *)from;             /* From: */
- (NSString *)fromName;           /* FromName: */
- (NSString *)inReplyTo;          /* In-Reply-To: */
- (NSString *)messageID;          /* Message-ID: */
- (NSString *)references;         /* References: */
- (OMEAddress *)replyTo;          /* Reply-To: */
- (NSString *)subject;            /* Subject: */
- (NSArray *)to;                  /* To: */
- (NSString *)xOMELocale;         /* X-OME-Locale: */
- (NSString *)xOriginalMessageId; /* X-Original-Messge-Id */

/* designated initializer */
- (id)initWithAttachment:(NSArray *)attachmentArray
                     bcc:(NSArray *)bccArray 
                      cc:(NSArray *)ccArray 
                    date:(NSCalendarDate *)aDate
                    from:(OMEAddress *)fromPerson
                fromName:(NSString *)fromNameString
               inReplyTo:(NSString *)inReplyToString
               messageID:(NSString *)messageIDString
              references:(NSString *)referencesString
                 replyTo:(OMEAddress *)replyToPerson
                 subject:(NSString *)subjectString
                      to:(NSArray *)toArray 
              xOMELocale:(NSString *)xOMELocaleString
      xOriginalMessageId:(NSString *)xOriginalMessageIdString;

/* init with empty header */
- (id)init;

/* init with other header */
- (id)initWithHeader:(WSHeader *)aHeader;

/* init with array of fields */
- (id)initWithFields:(NSArray *)fields;

/* init with array of lines */
- (id)initWithLines:(NSArray *)anArray;

/* Attachment: */
- (NSString *)attachmentFieldBody;
- (NSString *)attachmentField;

/* Bcc: */
- (NSString *)bccFieldBody;
- (NSString *)bccField;

/* Cc: */
- (NSString *)ccFieldBody;
- (NSString *)ccField;

/* Date: */
- (NSString *)dateFieldBody;
- (NSString *)dateField;

/* From: */
- (NSString *)fromFieldBody;
- (NSString *)fromField;

/* FromName: */
- (NSString *)fromNameFieldBody;
- (NSString *)fromNameField;

/* In-Reply-To: */
- (NSString *)inReplyToFieldBody;
- (NSString *)inReplyToField;

/* Message-ID: */
- (NSString *)messageIDFieldBody;
- (NSString *)messageIDField;

/* References: */
- (NSString *)referencesFieldBody;
- (NSString *)referencesField;

/* Reply-To: */
- (NSString *)replyToFieldBody;
- (NSString *)replyToField;

/* Subject: */
- (NSString *)subjectFieldBody;
- (NSString *)subjectField;

/* To: */
- (NSString *)toFieldBody;
- (NSString *)toField;

/* X-OME-Locale: */
- (NSString *)xOMELocaleFieldBody;
- (NSString *)xOMELocaleField;

/* X-Original-Message-ID: */
- (NSString *)xOriginalMessageIdFieldBody;
- (NSString *)xOriginalMessageIdField;

/* search */
- (BOOL)isBccIncludeString:(NSString *)aString;
- (BOOL)isCcIncludeString:(NSString *)aString;
- (BOOL)isToIncludeString:(NSString *)aString;

/* description */
- (NSString *)description;

/* dictionary without NSArray instances as entry */
- (NSDictionary *)messageHeadersForNSMailDelivery;

@end
