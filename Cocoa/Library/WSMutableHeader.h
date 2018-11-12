//
//  $Id: WSMutableHeader.h,v 1.5 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Feb 6 17:53:57 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "WSHeader.h"

@interface WSMutableHeader : WSHeader

/* accessor method */
- (void)setAttachment:(NSArray *)anArray;          /* Attachment: */
- (void)setBcc:(NSArray *)anArray;                 /* Bcc: */
- (void)setCc:(NSArray *)anArray;                  /* Cc: */
- (void)setDate:(NSCalendarDate *)aDate;           /* Date: */
- (void)setFrom:(OMEAddress *)aPerson;             /* From: */
- (void)setFromName:(NSString *)aString;           /* FromName: */
- (void)setInReplyTo:(NSString *)aString;          /* In-Reply-To: */
- (void)setMessageID:(NSString *)aString;          /* Message-ID: */
- (void)setReferences:(NSString *)aString;         /* References: */
- (void)setReplyTo:(OMEAddress *)aPerson;          /* Reply-To: */
- (void)setSubject:(NSString *)aString;            /* Subject: */
- (void)setTo:(NSArray *)anArray;                  /* To: */
- (void)setXOMELocale:(NSString *)aString;         /* X-OME-Locale */
- (void)setXOriginalMessageId:(NSString *)aString; /* X-Original-Message-Id */

/* set Bcc */
- (void)setBccWithPerson:(OMEAddress *)aPerson;
/* add Bcc */
- (void)addBcc:(NSArray *)anArray;
- (void)addBccWithPerson:(OMEAddress *)aPerson;

/* set Cc */
- (void)setCcWithPerson:(OMEAddress *)aPerson;
/* add Cc */
- (void)addCc:(NSArray *)anArray;
- (void)addCcWithPerson:(OMEAddress *)aPerson;

/* set Date to Now*/
- (void)setDateToNow;

/* set From with FromName */
- (void) setFromWithFromName;

/* set To */
- (void)setToWithPerson:(OMEAddress *)aPerson;
/* add To */
- (void)addTo:(NSArray *)anArray;
- (void)addToWithPerson:(OMEAddress *)aPerson;

/* RemoveToMe */
- (void)removeToMeWithAddress:(NSString *)aString;

/* RemoveToMeAll */
- (void)removeToMeAllWithAddresses:(NSArray *)addresses;

@end
