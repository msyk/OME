//
//  $Id: OMEField.h,v 1.5 2005/09/03 13:42:12 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Jan 30 15:34:55 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

/* return character */
#define CR @"\r" /* $0d: MacOS */
#define LF @"\n" /* $0a: unix */
#define CRLF @"\r\n" /* Windows ( dos ): RFC2822 reguration */

/* OME Environment */
#define EOL LF

/* mail headers */
#define ATTACHMENT @"Attachment"
#define CC @"Cc"
#define BCC @"Bcc"
#define DATE @"Date"
#define FROM @"From"
#define FROM_NAME @"FromName"
#define IN_REPLY_TO @"In-Reply-To"
#define MESSAGE_ID @"Message-ID"
#define RECEIVED @"Received"
#define REFERENCES @"References"
#define REPLY_TO @"Reply-To"
#define SUBJECT @"Subject"
#define TO @"To"
#define X_OME_LOCALE @"X-OME-Locale"
#define X_ORIGINAL_MESSAGE_ID @"X-Original-Message-Id"

/* mail headers */
#define ATTACHMENT_L @"attachment"
#define CC_L @"cc"
#define BCC_L @"bcc"
#define DATE_L @"date"
#define FROM_L @"from"
#define FROM_NAME_L @"fromname"
#define IN_REPLY_TO_L @"in-reply-to"
#define MESSAGE_ID_L @"message-id"
#define RECEIVED_L @"received"
#define REFERENCES_L @"references"
#define REPLY_TO_L @"reply-to"
#define SUBJECT_L @"subject"
#define TO_L @"to"
#define X_OME_LOCALE_L @"x-ome-locale"
#define X_ORIGINAL_MESSAGE_ID_L @"x-original-message-id"

/* Format */
#define FIELD_FORMAT @"%@: %@"
#define FIELD_LF_FORMAT @"%@: %@\n" /* LF = $0a: unix */

/* Functions - joined components */
NSString *OMEJoinedComponents ( NSArray *anArray );

@interface OMEField : NSObject
{
    /* filed definition */
    NSString *name; /* field name */
    NSString *body; /* field body */
}

/* accessor methods */
- (NSString *)name;
- (NSString *)body;

/* designated initializer */
- (id)initWithName:(NSString *)aName body:(NSString *)eMail;

/* initializer */
- (id)initWithLine:(NSString *)aLine;
- (id)initWithLineOfFile:(NSString *)aPath;

/* components of field body */
- (NSArray *)components;

/* persons of field body */
- (NSArray *)persons;

@end
