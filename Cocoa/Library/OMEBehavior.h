//
//  $Id: OMEBehavior.h,v 1.12 2007/12/02 11:09:58 msyk Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Fri Nov 26 18:23:38 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OMEBehavior : NSObject
{
    NSDictionary *dictionary;
}

/* Quick initializer method */
- (id)initOnCurrentEnv;

/*accessor methods */
- (NSDictionary *)dictionary;

/* designated initializer */
- (id)initWithDictionary:(NSDictionary *)aDictionary;

/* initializer */
- (id)initWithLines:(NSArray *)aArray;
- (id)initWithContentsOfFile:(NSString *)aPath;

/* write to file */
- (BOOL)writeToFile:(NSString *)aPath;

/* count */
- (unsigned)count;

/* object for key */
- (id)objectForKey:(id)aKey;

/* key enumerator */
- (NSEnumerator *)keyEnumerator;

/* AddressNamePrefix */ 
- (NSString *)addressNamePrefix;

/* AddressNameSuffix */ 
- (NSString *)addressNameSuffix;

/* DontCleanMLAdditionalSubject */
- (BOOL)dontCleanMLAdditionalSubject;

/* DontCleanReInSubject */
- (BOOL)dontCleanReInSubject;

/* IncludeCC */
- (BOOL)includeBCC;

/* IncludeCC */
- (BOOL)includeCC;

/* IncludeToCC */
- (BOOL)includeToCC;

/* InsertFromToNewMail */
- (BOOL)insertFromToNewMail;

/* MessageCommentHead*/ 
- (NSString *)tempFolderPath;

/* MessageCommentHead*/ 
- (NSString *)messageCommentHead;

/* OneLineBytes */
- (int)oneLineBytes;

/* RemoveToMe */
- (BOOL)removeToMe;

/* RemoveToMeAll */
- (BOOL)removeToMeAll;

/* ReplyCommentSupply */
- (BOOL)replyCommentSupply;

/* SendMailMessageStandardOutput */
- (BOOL)sendMailMessageStandardOutput;

/* SendMailMessageShow */
- (BOOL)sendMailMessageShow;

/* DownloadMailsMessageShow */
- (BOOL)downloadMailsMessageShow;

/* DownloadMailsMessageStandardOutput */
- (BOOL)downloadMailsMessageStandardOutput;

/* SubjectPrefix */ 
- (NSString *)subjectPrefix;

/* UseDraft */
- (BOOL)useDraft;

/* AdditionalHeaders */
- (NSArray *)additionalHeaders;
@end
