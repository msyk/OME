//
//  $Id: OMEPaths.h,v 1.6 2007/04/01 03:21:56 msyk Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Jan 9 12:42:39 2005 UTC
//  Copyright (c) Shinya Wakimoto, 2005. All rights reserved.
//

#import <Foundation/Foundation.h>

/* logs */
extern NSString *OMEDownloadmailsLog;
extern NSString *OMEMailwriterLog;
extern NSString *OMEMessagemakerLog;
extern NSString *OMESendmailLog;

@interface OMEPaths : NSObject
{
    /* OMEPreferences */
    NSString *omePreferences;
    NSString *behaviorInfoTxt, *headerAddendumTxt, *mailWriter;
    NSString *movingInfoTxt, *nowSending, *omeRoot, *omeRootPath;
    NSString *receiveInfoTxt, *replyCommentTxt, *senderInfoTxt;
    NSString *signatureTxt, *topMessageTxt;
    /* Open_Mail_Environment  ( Mail Box ) */
    NSString *mailBox;
    NSString *draft, *inBox;
    /* OutBox */
    NSString *outBox;
    NSString *sent;
    /* temp */
    NSString *temp;
    NSString *archives, *hide, *unreadAliases;
    /* logs */
    NSString *logs;
    NSString *downloadmailsLog, *mailwriterLog, *messagemakerLog;
    NSString *sendmailLog;
}

/**** class methods ****/

/* OMEPreferences directory path */
+ (NSString *)omePreferences;

/* default MailBox ( Open_Mail_Ecvironment ) */
+ (NSString *)defaultMailBox;

+ (NSString *)omeFrameworkResourcePath;

/**** instance methods ****/
/*accessor methods */
/* OMEPreferences */
- (NSString *)omePreferences;
- (NSString *)behaviorInfoTxt;
- (NSString *)headerAddendumTxt;
- (NSString *)mailWriter;
- (NSString *)movingInfoTxt;
- (NSString *)nowSending;
- (NSString *)omeRoot;
- (NSString *)omeRootPath;
- (NSString *)receiveInfoTxt;
- (NSString *)replyCommentTxt;
- (NSString *)senderInfoTxt;
- (NSString *)signatureTxt;
- (NSString *)topMessageTxt;
/* Open_Mail_Environment ( Mail Box ) */
- (NSString *)mailBox;
- (NSString *)draft;
- (NSString *)inBox;
/* OutBox */
- (NSString *)outBox;
- (NSString *)sent;
/* temp */
- (NSString *)temp;
- (NSString *)archives;
- (NSString *)hide;
- (NSString *)unreadAliases;
/* logs */
- (NSString *)logs;
- (NSString *)downloadmailsLog;
- (NSString *)mailwriterLog;
- (NSString *)messagemakerLog;
- (NSString *)sendmailLog;

/* designated initializer */
- (id)initWithMailBox:(NSString *)aDirectory;

/* initializer */
- (id)init;

/* search mail drafts in OutBox */
/* return: array of absolute paths */
- (NSArray *)wmailTypeFilesInOutBox;

@end
