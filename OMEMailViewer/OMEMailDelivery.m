/*
 *  $Id: OMEMailDelivery.m,v 1.11 2005/09/03 13:42:13 wakimoto Exp $
 *
 *  Created by Shinya Wakimoto.
 *  Revision 1.1, Sat Dec 11 06:18:42 2004 UTC
 *  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
 */

#import "OMEMailDelivery.h"

#import <Message/NSMailDelivery.h>
#import "OMEAddress.h"
#import "OMEAttachment.h"
#import "OMEBehavior.h"
#import "OMEField.h"
#import "OMELog.h"
#import "OMEPaths.h"
#import "OMESMTPServer.h"
#import "OMEWMail.h"
#import "WSMutableHeader.h"

/* file type */
#define WMAIL @"wmail"

/* format */
#define HOST_NAME_FORMAT @"%@\n"

/* message format */
#define NOW_SENDING_FORMAT @"%% OME %% Now Sending.\n"
#define DELIVERY_CLASS_ERROR_FORMAT @"%% OME Error %% Delivery Class Has Not Been Configured !!!\n"
#define OK_FORMAT @"%%%% OME Message %%%% Mail Send OK to: %@\n"
#define NO_FORMAT @"%% OME Error %% Deliery Failed\n"

/* Waring Log */ 
#define WARNING @"%%%% OME Warning %%%% Two or more From headers in the sending file:%@\n"

/*description */
#define DESCRIPTION @"\npaths = \n%@\nbehavior =\n%@\nserver =\n%@\nsendMailLog = %@"

@implementation OMEMailDelivery

static BOOL nomore = NO;

+ (void)initialize
{
    int n;

    if ( nomore == NO ) {
        n = [ NSDate timeIntervalSinceReferenceDate ];
        srand ( n );
        nomore = YES;
    }
}

/* accessor method */
- (void)setPaths:(OMEPaths *)omePaths
{
    [ omePaths retain ];
    [ paths release ];
    paths = omePaths;
}

/*accessor method */
- (OMEPaths *)paths
{
    return paths;
}

/* accessor method */
- (void)setBehavior:(OMEBehavior *)aBehavior
{
    [ aBehavior retain ];
    [ behavior release ];
    behavior = aBehavior;
}

/*accessor method */
- (OMEBehavior *)behavior;
{
    return behavior;
}

/* accessor method */
- (void)setServer:(OMESMTPServer *)smtpServer
{
    [ smtpServer retain ];
    [ server release ];
    server = smtpServer;
}

/*accessor method */
- (OMESMTPServer *)server;
{
    return server;
}

/* accessor method */
- (void)setSendmailLog:(OMELog *)aLog
{
    [ aLog retain ];
    [ sendmailLog release ];
    sendmailLog = aLog;
}

/*accessor method */
- (OMELog *)sendmailLog
{
    return sendmailLog;
}

/* accessor method */
- (void)setStandardOutputLog:(OMELog *)aLog
{
    [ aLog retain ];
    [ standardOutputLog release ];
    standardOutputLog = aLog;
}

/*accessor method */
- (OMELog *)standardOutputLog
{
    return standardOutputLog;
}

/* local method */
- (void)writeLogWithString:(NSString *)aString
{
    if ( [ [ self behavior ] sendMailMessageStandardOutput ] ) {
        [ OMELog writeToStandardOutputWithString:aString ];
    }
    [ OMELog writeToSendmailLogWithString:aString ];
}

/* designated initializer */
- (id)initWithOMEPaths:(OMEPaths *)omePaths
{
    NSHost *aHost;
    NSString *hostName, *aString, *aPath;
    OMEBehavior *omeBehavior;
    OMELog *aLog;
    OMESMTPServer *smtpServer;

    self = [ super init ];
    if ( self ) {
        /* paths */
        [ self setPaths:omePaths ];
        /* OMEBehavior instance */
        aPath = [ [ self paths ] behaviorInfoTxt ];
        omeBehavior = [ [ OMEBehavior alloc ] initWithContentsOfFile:aPath ];
        [ self setBehavior:omeBehavior ];
        [ omeBehavior release ];
        /* OMESMTPServer instance */
        aPath = [ [ self paths ] senderInfoTxt ];
        smtpServer = [ [ OMESMTPServer alloc ] initWithContentsOfFile:aPath ];
        [ self setServer:smtpServer ];
        [ smtpServer release ];
        /* jp.mac-ome.sendmail.log */
        aPath = [ [ self paths ] sendmailLog ];
	aLog = [ [ OMELog alloc ] initWithPath:aPath ];
	[ self setSendmailLog:aLog ];
	[ aLog release ];
        /* standard output log */
	aLog = [ [ OMELog alloc ] init ];
	[ self setStandardOutputLog:aLog ];
	[ aLog release ];
	/* Check NSMailDelivery Class */
	if ( [ NSMailDelivery hasDeliveryClassBeenConfigured ] ) {
	    /* Host name */
	    aHost = [ NSHost currentHost ];
	    hostName =  [ aHost name ];
	    /* write Host Name to jp.mac-ome.sendmaillog */
	    aString = [ NSString stringWithFormat:HOST_NAME_FORMAT, hostName ];
	    [ self writeLogWithString:aString ];
	} else {
	    [ self writeLogWithString:DELIVERY_CLASS_ERROR_FORMAT ];
	}
    }
    return self;
}

/* dealloc */
- (void)dealloc
{
    [ paths release ];
    [ behavior release ];
    [ server release ];
    [ sendmailLog release ];
    [ standardOutputLog release ];
    [ super dealloc ];
}

/* description */
- (NSString *)description
{
    return [ NSString
               stringWithFormat:DESCRIPTION,
               [ [ self paths ] description ],
               [ [ self behavior ] description ],
               [ [ self server ] description ],
               [ [ self sendmailLog ] description ] ];
}

/* move the receiver's file to the other directory */
- (BOOL)movePath:(NSString *)aPath toDirectory:(NSString *)aDirectory
{
    int n;
    NSFileManager *fileManager;
    NSString *newPath, *pathExtension;

    /* last path component */
    newPath = [ aPath lastPathComponent ];
    pathExtension = [ newPath pathExtension ];
    newPath = [ newPath stringByDeletingPathExtension ];
    /* create random number */
    n = rand () % 1000000;
    /* create NSFileManager instance */
    fileManager = [ NSFileManager defaultManager ];
    /* define file path by random number */
    newPath = [ NSString stringWithFormat:@"%@%06d", newPath, n ];
    newPath = [ newPath stringByAppendingPathExtension:pathExtension ];
    newPath = [ aDirectory stringByAppendingPathComponent:newPath ];
    return [ fileManager movePath:aPath toPath:newPath handler:nil ];
}

/* local method */
- (NSDictionary *)headerDictionary
{
    NSBundle *mainBundle;
    NSDictionary *aDictionary;
    NSString *aPath, *aBody, *aName;
    OMEField *aField;

    aDictionary = nil;
    mainBundle = [ NSBundle mainBundle ];
    aPath = [ mainBundle pathForResource:@"AdditionalField" ofType:@"txt" ];
    if ( aPath ) {
        aField = [ [ OMEField alloc ] initWithLineOfFile:aPath ];
	aBody = [ aField body ];
	aName = [ aField name ];
        aDictionary = [ NSDictionary dictionaryWithObject:aBody forKey:aName ];
	[ aField release ];
    }
    return aDictionary;
}

/* deliver message at path */
- (BOOL)deliverMessageAtPath:(NSString *)aPath
{
    BOOL result;
    NSArray *anArray;
    NSAttributedString *attributedString;
    NSDictionary *aDictionary;
    NSEnumerator *aEnumerator;
    NSMutableAttributedString *mutableAttributedString;
    NSMutableDictionary *mutableDictionary;
    NSString *aString;
    OMEAttachment *anAttachment;
    OMEAddress *anAddress;
    OMEWMail *wmail;
    WSMutableHeader *aHeader;

    /* create wmail instance */
    wmail = [ [ OMEWMail alloc ] initWithContentsOfFile:aPath ];
    /* OMEHeaders instance */
    aHeader = [ [ WSMutableHeader alloc ] initWithHeader:[ wmail header ] ];
    if ( [ aHeader from ] ) {
        [ aHeader setFromWithFromName ];
        aString = [ NSString
                      stringWithFormat:WARNING, [ aPath lastPathComponent ] ];
        [ self writeLogWithString:aString ];
    } else {
        anAddress = [ [ self server ] sender ];
        [ aHeader setFrom:anAddress ];
    }
    /* message body */
    aString = [ wmail messageBodyString ];
    /* create NSMutableAttributedString instance */
    mutableAttributedString = [ [ NSMutableAttributedString alloc ]
                                  initWithString:aString ];
    /* check Attachment */
    anArray = [ aHeader attachment ];
    if ( anArray ) {
        aEnumerator = [ anArray objectEnumerator ];
        while ( ( aString = [ aEnumerator nextObject ] ) != nil ) {
            anAttachment = [ [ OMEAttachment alloc ]
                               initWithContentsOfFile:aString ];
            attributedString = [ anAttachment attributedString ];
            if ( attributedString ) {
                [ mutableAttributedString
                    appendAttributedString:attributedString ];
            }
            [ anAttachment release ];
        }
    }
    /* set "Date:" to Now */
    [ aHeader setDateToNow ];
    /* dictionary of message headers */
    mutableDictionary = [ NSMutableDictionary dictionary ];
    aDictionary = [ aHeader messageHeadersForNSMailDelivery ];
    [ mutableDictionary addEntriesFromDictionary:aDictionary ];
    aDictionary = [ self headerDictionary ];
    [ mutableDictionary addEntriesFromDictionary:aDictionary ];
    /* deliver message */
    result = [ NSMailDelivery
                 deliverMessage:mutableAttributedString
                 headers:mutableDictionary
                 format:NSMIMEMailFormat
                 protocol:nil ];
    /* write result to jp.mac-ome.sendmail.log */
    if ( result ) {
        [ self movePath:aPath toDirectory:[ [ self paths ] sent ] ];
        aString = [ aHeader toFieldBody ];
        aString = [ NSString stringWithFormat:OK_FORMAT, aString ];
        [ self writeLogWithString:aString ];
    } else {
        [ self writeLogWithString:NO_FORMAT ];
    }
    /* release */
    [ mutableAttributedString release ];
    [ aHeader release ];
    [ wmail release ];
    /* return */
    return result;
}

/* deliver message at paths */ 
- (void)deliverPaths:(NSArray *)anArray
{
    NSEnumerator *aEnumerator;
    NSFileManager *fileManager;
    NSString *aString, *nowSending;

    fileManager = [ NSFileManager defaultManager ];
    nowSending = [ [ self paths ] nowSending ];
    if ( [ fileManager fileExistsAtPath:nowSending ] ) {
        [ self writeLogWithString:NOW_SENDING_FORMAT ];
    } else {
        [ fileManager
            createFileAtPath:nowSending contents:nil attributes:nil ];
        aEnumerator = [ anArray objectEnumerator ];
        while ( ( aString = [ aEnumerator nextObject ] ) != nil ) {
            [ self deliverMessageAtPath:aString ];
        }
        [ fileManager removeFileAtPath:nowSending handler:nil ];
    }
}

@end
