//
//  $Id: OMEPaths.m,v 1.9 2007/05/27 14:57:17 msyk Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Jan 9 12:42:39 2005 UTC 
//  Copyright (c) Shinya Wakimoto, 2005. All rights reserved.
//

#import "OMEPaths.h"

#import "OMEAlias.h"
#import "OMEDirectory.h"

/* Library */
#define LIBRARY @"Library"

/* Preferences */
#define PREFERENCES @"Preferences"

/* OME_Preferences */
#define OME_PREFERENCES @"OME_Preferences"

/* Files in OME_Preferences */
#define BEHAVIOR_INFO_TXT @"Behavior_Info.txt"
#define HEADER_ADDENDUM_TXT @"HeaderAddendum.txt"
#define MAIL_WRITER @"Mail_Writer"
#define MOVING_INFO_TXT @"Moving_Info.txt"
#define NOW_SENDING @"NowSending"
#define OME_ROOT @"OME_Root"
#define OME_ROOT_PATH @"OME_Root_Path"
#define RECEIVE_INFO_TXT @"Receive_Info.txt"
#define REPLY_COMMENT_TXT @"ReplyComment.txt"
#define SENDER_INFO_TXT @"Sender_Info.txt"
#define SIGNATURE_TXT @"Signature.txt"
#define TOP_MESSAGE_TXT @"TopMessage.txt"

/* Open_Mail_Environment ( MailBox ) */
#define DEFAULT_MAILBOX @"Open_Mail_Environment"
#define DRAFT @"Draft"
#define IN_BOX @"InBox"

/* OutBox */
#define OUT_BOX @"OutBox"
#define SENT @"Sent"

/* temp */
#define TEMP @"temp"
#define ARCHIVES @"Archives"
#define HIDE @"hide"
#define UNREAD_ALIASES @"unreadAliases"

/* logs */
#define LOGS @"logs"

/* format */
#define FORMAT @"%@\n"

/* file type */
#define WMAIL @"wmail"

/* logs */
NSString *OMEDownloadmailsLog = @"jp.mac-ome.downloadmails.log";
NSString *OMEMailwriterLog = @"jp.mac-ome.mailwriter.log";
NSString *OMEMessagemakerLog = @"jp.mac-ome.messagemaker.log";
NSString *OMESendmailLog = @"jp.mac-ome.sendmail.log";

@implementation OMEPaths

/**** class methods ****/
/* Library directory path */
+ (NSString *)library
{
    return [ NSHomeDirectory() stringByAppendingPathComponent:LIBRARY ];
}

+ (NSString *)preferences
{
    return [ [ self library ] stringByAppendingPathComponent:PREFERENCES ];
}

/* OMEPreferences directory path */
+ (NSString *)omePreferences
{
    return [ [ self preferences ]
               stringByAppendingPathComponent:OME_PREFERENCES ];
}

+ (NSString *)omeFrameworkResourcePath
{
	NSBundle* myBundle = [NSBundle bundleForClass: [self class]];
	NSString *rPath = [myBundle resourcePath];
	if ( rPath == nil )	{
		NSLog( @"OMEPaths class: couldn't get the OME framework as a bundle." );
		myBundle = [NSBundle mainBundle];
		rPath = [[myBundle resourcePath] stringByAppendingString:@"/Contents/Frameworks/OME.framework/Resources"];
	}
	return rPath;
}

/* Behavior_Info.txt in directory path */
+ (NSString *)behaviorInfoTxtInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:BEHAVIOR_INFO_TXT ];
}

/* HeaderAddendum.txt in directory path */
+ (NSString *)headerAddendumTxtInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:HEADER_ADDENDUM_TXT  ];
}

/* Mail_Writer in directory path */
+ (NSString *)mailWriterInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:MAIL_WRITER ];
}

/* Moving_Info.txt in directory path */
+ (NSString *)movingInfoTxtInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:MOVING_INFO_TXT ];
}

/* NowSending in directory path */
+ (NSString *)nowSendingInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:NOW_SENDING ];
}

/* OME_Root in directory path */
+ (NSString *)omeRootInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:OME_ROOT ];
}

/* OME_Root_Path in directory path */
+ (NSString *)omeRootPathInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:OME_ROOT_PATH ];
}

/* Receive_Info.txt in directory path */
+ (NSString *)receiveInfoTxtInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:RECEIVE_INFO_TXT ];
}

/* ReplyComment.txt in directory path */
+ (NSString *)replyCommentTxtInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:REPLY_COMMENT_TXT ];
}

/* Sender_Info.txt in directory path */
+ (NSString *)senderInfoTxtInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:SENDER_INFO_TXT ];
}

/* Signature.txt in directory path */
+ (NSString *)signatureTxtInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:SIGNATURE_TXT ];
}

/* TopMessage.txt in directory path */
+ (NSString *)topMessageTxtInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:TOP_MESSAGE_TXT ];
}

/* default MailBox ( Open_Mail_Ecvironment ) */
+ (NSString *)defaultMailBox
{
    return [ NSHomeDirectory()
                            stringByAppendingPathComponent:DEFAULT_MAILBOX ];
}

/* Mail Box */
+ (NSString *)mailBoxWithOMEPreferences:(NSString *)aDirectory;
{
    NSCharacterSet *whitespaceCSet;
    NSString *aString, *aRoot, *rootPath;
    OMEAlias *anAlias;

    whitespaceCSet = [ NSCharacterSet whitespaceCharacterSet ];
    aRoot = [ self omeRootInDirectory:aDirectory ];
    anAlias = [ [ OMEAlias alloc ] initWithContentsOfAliasFile:aRoot ];
    aString = [ anAlias source ];
    if ( aString ) {
        aString = [ aString stringByTrimmingCharactersInSet:whitespaceCSet ];
        if ( [ aString isEqualToString:@"" ] ) {
            aString = nil;
        }
    }
    if ( aString == nil ) {
        NSError *error;
        rootPath = [ self omeRootPathInDirectory:aDirectory ];
        aString = [ NSString stringWithContentsOfFile: rootPath 
                                             encoding: NSUTF8StringEncoding
                                                error: &error];
        if ( aString ) {
            aString = [ aString
                          stringByTrimmingCharactersInSet:whitespaceCSet ];
            if ( [ aString isEqualToString:@"" ] ) {
                aString = nil;
            }
        }
    }
    [ anAlias release ];
    return aString;
}

/* Draft in directory path */
+ (NSString *)draftInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:DRAFT ];
}

/*  InBox in directory path */
+ (NSString *)inBoxInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:IN_BOX ];
}

/*  OutBox in directory path */
+ (NSString *)outBoxInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:OUT_BOX ];
}

/*  temp in directory path */
+ (NSString *)tempInDirectory:(NSString *)aDirectory;
{
    return [ aDirectory stringByAppendingPathComponent:TEMP ];
}

/*  Sent in directory path */
+ (NSString *)sentInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:SENT ];
}

/*  Archives in directory path */
+ (NSString *)archivesInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:ARCHIVES ];
}

/*  hide in directory path */
+ (NSString *)hideInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:HIDE ];
}

/*  unreadAliases in directory path */
+ (NSString *)unreadAliasesInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:UNREAD_ALIASES ];
}

/* logs in directory path */
+ (NSString *)logsInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:LOGS ];
}

/* jp.mac-ome.downloadmails.log in directory path */
+ (NSString *)downloadmailsLogInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:OMEDownloadmailsLog ];
}

/* jp.mac-ome.mailwriter.log in directory path */
+ (NSString *)mailwriterLogInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:OMEMailwriterLog ];
}

/* jp.mac-ome.messagemaker.log in directory path */
+ (NSString *)messagemakerLogInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:OMEMessagemakerLog ];
}

/* jp.mac-ome.sendmail.log in directory path */
+ (NSString *)sendmailLogInDirectory:(NSString *)aDirectory
{
    return [ aDirectory stringByAppendingPathComponent:OMESendmailLog ];
}

/**** instance methods ****/

/* accessor method */
- (void)setOMEPreferences:(NSString *)aString
{
    [ aString retain ];
    [ omePreferences release ];
    omePreferences = aString;
}

/*accessor method */
- (NSString *)omePreferences
{
    return omePreferences;
}

/* accessor method */
- (void)setBehaviorInfoTxt:(NSString *)aString
{
    [ aString retain ];
    [ behaviorInfoTxt release ];
    behaviorInfoTxt = aString;
}

/*accessor method */
- (NSString *)behaviorInfoTxt
{
    return behaviorInfoTxt;
}

/* accessor method */
- (void)setHeaderAddendumTxt:(NSString *)aString
{
    [ aString retain ];
    [ headerAddendumTxt release ];
    headerAddendumTxt = aString;
}

/*accessor method */
- (NSString *)headerAddendumTxt
{
    return headerAddendumTxt;
}

/* accessor method */
- (void)setMailWriter:(NSString *)aString
{
    [ aString retain ];
    [ mailWriter release ];
    mailWriter = aString;
}

/*accessor method */
- (NSString *)mailWriter
{
    return mailWriter;
}

/* accessor method */
- (void)setMovingInfoTxt:(NSString *)aString
{
    [ aString retain ];
    [ movingInfoTxt release ];
    movingInfoTxt = aString;
}

/*accessor method */
- (NSString *)movingInfoTxt
{
    return movingInfoTxt;
}

/* accessor method */
- (void)setNowSending:(NSString *)aString
{
    [ aString retain ];
    [ nowSending release ];
    nowSending = aString;
}

/*accessor method */
- (NSString *)nowSending;
{
    return nowSending;
}

/* accessor method */
- (void)setOMERoot:(NSString *)aString
{
    [ aString retain ];
    [ omeRoot release ];
    omeRoot = aString;
}

/*accessor method */
- (NSString *)omeRoot
{
    return omeRoot;
}

/* accessor method */
- (void)setOMERootPath:(NSString *)aString
{
    [ aString retain ];
    [ omeRootPath release ];
    omeRootPath = aString;
}

/*accessor method */
- (NSString *)omeRootPath
{
    return omeRootPath;
}

/* accessor method */
- (void)setReceiveInfoTxt:(NSString *)aString
{
    [ aString retain ];
    [ receiveInfoTxt release ];
    receiveInfoTxt = aString;
}

/*accessor method */
- (NSString *)receiveInfoTxt
{
    return receiveInfoTxt;
}

/* accessor method */
- (void)setReplyCommentTxt:(NSString *)aString
{
    [ aString retain ];
    [ replyCommentTxt release ];
    replyCommentTxt = aString;
}

/*accessor method */
- (NSString *)replyCommentTxt
{
    return replyCommentTxt;
}

/* accessor method */
- (void)setSenderInfoTxt:(NSString *)aString
{
    [ aString retain ];
    [ senderInfoTxt release ];
    senderInfoTxt = aString;
}

/*accessor method */
- (NSString *)senderInfoTxt
{
    return senderInfoTxt;
}

/* accessor method */
- (void)setSignatureTxt:(NSString *)aString
{
    [ aString retain ];
    [ signatureTxt release ];
    signatureTxt = aString;
}

/*accessor method */
- (NSString *)signatureTxt
{
    return signatureTxt;
}

/* accessor method */
- (void)setTopMessageTxt:(NSString *)aString
{
    [ aString retain ];
    [ topMessageTxt release ];
    topMessageTxt = aString;
}

/*accessor method */
- (NSString *)topMessageTxt
{
    return topMessageTxt;
}

/* accessor method */
- (void)setMailBox:(NSString *)aString
{
    [ aString retain ];
    [ mailBox release ];
    mailBox = aString;
}

/*accessor method */
- (NSString *)mailBox
{
    return mailBox;
}

/* accessor method */
- (void)setDraft:(NSString *)aString
{
    [ aString retain ];
    [ draft release ];
    draft = aString;
}

/*accessor method */
- (NSString *)draft
{
    return draft;
}

/* accessor method */
- (void)setInBox:(NSString *)aString
{
    [ aString retain ];
    [ inBox release ];
    inBox = aString;
}

/*accessor method */
- (NSString *)inBox
{
    return inBox;
}

/* accessor method */
- (void)setOutBox:(NSString *)aString
{
    [ aString retain ];
    [ outBox release ];
    outBox = aString;
}

/*accessor method */
- (NSString *)outBox
{
    return outBox;
}

/* accessor method */
- (void)setSent:(NSString *)aString
{
    [ aString retain ];
    [ sent release ];
    sent = aString;
}

/*accessor method */
- (NSString *)sent
{
    return sent;
}

/* accessor method */
- (void)setTemp:(NSString *)aString
{
    [ aString retain ];
    [ temp release ];
    temp = aString;
}

/*accessor method */
- (NSString *)temp
{
    return temp;
}

/* accessor method */
- (void)setArchives:(NSString *)aString
{
    [ aString retain ];
    [ archives release ];
    archives = aString;
}

/*accessor method */
- (NSString *)archives
{
    return archives;
}

/* accessor method */
- (void)setHide:(NSString *)aString
{
    [ aString retain ];
    [ hide release ];
    hide = aString;
}

/*accessor method */
- (NSString *)hide
{
    return hide;
}

/* accessor method */
- (void)setUnreadAliases:(NSString *)aString
{
    [ aString retain ];
    [ unreadAliases release ];
    unreadAliases = aString;
}

/*accessor method */
- (NSString *)unreadAliases
{
    return unreadAliases;
}

/* accessor method */
- (void)setLogs:(NSString *)aString
{
    [ aString retain ];
    [ logs release ];
    logs = aString;
}

/*accessor method */
- (NSString *)logs
{
    return logs;
}

/* accessor method */
- (void)setDownloadmailsLog:(NSString *)aString
{
    [ aString retain ];
    [ downloadmailsLog release ];
    downloadmailsLog = aString;
}

/*accessor method */
- (NSString *)downloadmailsLog
{
    return downloadmailsLog;
}

/* accessor method */
- (void)setMailwriterLog:(NSString *)aString
{
    [ aString retain ];
    [ mailwriterLog release ];
    mailwriterLog = aString;
}

/*accessor method */
- (NSString *)mailwriterLog
{
    return mailwriterLog;
}

/* accessor method */
- (void)setMessagemakerLog:(NSString *)aString
{
    [ aString retain ];
    [ messagemakerLog release ];
    messagemakerLog = aString;
}

/*accessor method */
- (NSString *)messagemakerLog;
{
    return messagemakerLog;
}

/* accessor method */
- (void)setSendmailLog:(NSString *)aString
{
    [ aString retain ];
    [ sendmailLog release ];
    sendmailLog = aString;
}

/*accessor method */
- (NSString *)sendmailLog;
{
    return sendmailLog;
}

/* designated initializer */
- (id)initWithMailBox:(NSString *)aDirectory
{
    NSString *aString;

    self = [ super init ];
    if ( self ) {
        /* OMEPreferences */
        aString = [ [ self class ] omePreferences ];
        [ self setOMEPreferences:aString ];
        aString = [ [ self class ] 
                      behaviorInfoTxtInDirectory:[ self omePreferences ] ];
        [ self setBehaviorInfoTxt:aString ];
        aString = [ [ self class ] 
                      headerAddendumTxtInDirectory:[ self omePreferences ] ];
        [ self setHeaderAddendumTxt:aString ];
        aString = [ [ self class ] 
                      mailWriterInDirectory:[ self omePreferences ] ];
        [ self setMailWriter:aString ];
        aString = [ [ self class ] 
                      movingInfoTxtInDirectory:[ self omePreferences ] ];
        [ self setMovingInfoTxt:aString ];
        aString = [ [ self class ] 
                      nowSendingInDirectory:[ self omePreferences ] ];
        [ self setNowSending:aString ];
        aString = [ [ self class ] 
                      omeRootInDirectory:[ self omePreferences ] ];
        [ self setOMERoot:aString ];
        aString = [ [ self class ] 
                      omeRootPathInDirectory:[ self omePreferences ] ];
        [ self setOMERootPath:aString ];
        aString = [ [ self class ] 
                      receiveInfoTxtInDirectory:[ self omePreferences ] ];
        [ self setReceiveInfoTxt:aString ];
        aString = [ [ self class ] 
                      replyCommentTxtInDirectory:[ self omePreferences ] ];
        [ self setReplyCommentTxt:aString ];
        aString = [ [ self class ] 
                      senderInfoTxtInDirectory:[ self omePreferences ] ];
        [ self setSenderInfoTxt:aString ];
        aString = [ [ self class ] 
                      signatureTxtInDirectory:[ self omePreferences ] ];
        [ self setSignatureTxt:aString ];
        aString = [ [ self class ] 
                      topMessageTxtInDirectory:[ self omePreferences ] ];
        [ self setTopMessageTxt:aString ];
        /* Open_Mail_Environment ( Mail Box ) */
        [ self setMailBox:aDirectory ];
        aString = [ [ self class ] draftInDirectory:[ self mailBox ] ];
        [ self setDraft:aString ];
        aString = [ [ self class ] inBoxInDirectory:[ self mailBox ] ];
        [ self setInBox:aString ];
        aString = [ [ self class ] outBoxInDirectory:[ self mailBox ] ];
        [ self setOutBox:aString ];
        aString = [ [ self class ] sentInDirectory:[ self outBox ] ];
        [ self setSent:aString ];
        aString = [ [ self class ] tempInDirectory:[ self mailBox ] ];
        [ self setTemp:aString ];
        aString = [ [ self class ] archivesInDirectory:[ self temp ] ];
        [ self setArchives:aString ];
        aString = [ [ self class ] hideInDirectory:[ self temp ] ];
        [ self setHide:aString ];
        aString = [ [ self class ] unreadAliasesInDirectory:[ self temp ] ];
        [ self setUnreadAliases:aString ];
        /* logs */
        aString = [ [ self class ] logsInDirectory:[ self temp ] ];
        [ self setLogs:aString ];
        aString = [ [ self class ] downloadmailsLogInDirectory:[ self logs ] ];
        [ self setDownloadmailsLog:aString ];
        aString = [ [ self class ] mailwriterLogInDirectory:[ self logs ] ];
        [ self setMailwriterLog:aString ];
        aString = [ [ self class ] messagemakerLogInDirectory:[ self logs ] ];
        [ self setMessagemakerLog:aString ];
        aString = [ [ self class ] sendmailLogInDirectory:[ self logs ] ];
        [ self setSendmailLog:aString ];
    }
    return self;
}

/* initializer */
- (id)init
{
    NSString *aString;

    /* OMEPreferences */
    aString = [ [ self class ] omePreferences ];
    /* MailBox */
    aString = [ [ self class ] mailBoxWithOMEPreferences:aString ];
    return [ self initWithMailBox:aString ];
}

/* dealloc */
- (void)dealloc
{
    /* OMEPreferences */
    [ omePreferences release ];
    [ behaviorInfoTxt release ];
    [ headerAddendumTxt release ];
    [ mailWriter release ];
    [ movingInfoTxt release ];
    [ nowSending release ];
    [ omeRoot release ];
    [ omeRootPath release ];
    [ receiveInfoTxt release ];
    [ replyCommentTxt release ];
    [ senderInfoTxt release ];
    [ signatureTxt release ];
    [ topMessageTxt release ];
    /* Open_Mail_Environment ( Mail Box ) */
    [ mailBox release ];
    [ draft release ];
    [ inBox release ];
    [ outBox release ];
    [ sent release ];
    [ temp release ];
    [ archives release ];
    [ hide release ];
    [ unreadAliases release ];
    /* logs */
    [ logs release ];
    [ downloadmailsLog release ];
    [ mailwriterLog release ];
    [ messagemakerLog release ];
    [ sendmailLog release ];
    /* dealloc */
    [ super dealloc ];
}

/* description */
- (NSString *)description
{
    NSString *aString;

    aString = @"\n";
    /* OMEPreferences */
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self omePreferences ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self behaviorInfoTxt ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self headerAddendumTxt ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self mailWriter ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self movingInfoTxt ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self nowSending ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self omeRoot ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self omeRootPath ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self receiveInfoTxt ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self replyCommentTxt ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self senderInfoTxt ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self signatureTxt ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self topMessageTxt ] ];
    /* Open_Mail_Environment ( Mail Box ) */
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self mailBox ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self draft ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self inBox ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self outBox ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self sent ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self temp ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self archives ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self hide ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self unreadAliases ] ];
    /* logs */
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self logs ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self downloadmailsLog ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self mailwriterLog ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self messagemakerLog ] ];
    aString = [ aString
                  stringByAppendingFormat:FORMAT, [ self sendmailLog ] ];
    /* return */
    return aString;
}

/* search mail drafts in OutBox */
/* return: array of absolute paths */
- (NSArray *)wmailTypeFilesInOutBox
{
    NSArray *anArray;
    OMEDirectory *aDirectory;

    aDirectory = [ [ OMEDirectory alloc ] initWithPath:[ self outBox ] ];
    anArray = [ aDirectory pathsMatchingExtension:WMAIL ];
    [ aDirectory release ];
    return anArray;
}

@end
