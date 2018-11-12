//
//  $Id: OMELog.m,v 1.6 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sat Dec 11 06:18:42 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import "OMELog.h"

#import "OMEPaths.h"

NSString *OMELogStringKey = @"OMELogStringKey";
NSString *OMEStandardOutput = @"OMEStandardOutput";

@implementation OMELog

/* write string to jp.mac-ome.downloadmails.log */
+ (void)writeToDownloadmailsLogWithString:(NSString *)aString
{
    NSDictionary *aDictionary;
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    aDictionary = [ NSDictionary
                      dictionaryWithObject:aString forKey:OMELogStringKey ];
    [ notificationCenter
        postNotificationName:OMEDownloadmailsLog
        object:self
        userInfo:aDictionary ];
}

/* write string to jp.mac-ome.mailwriter.log */
+ (void)writeToMailwriterLogWithString:(NSString *)aString
{
    NSDictionary *aDictionary;
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    aDictionary = [ NSDictionary
                      dictionaryWithObject:aString forKey:OMELogStringKey ];
    [ notificationCenter
        postNotificationName:OMEMailwriterLog
        object:self
        userInfo:aDictionary ];
}

/* write string to jp.mac-ome.messagemaker.log */
+ (void)writeToMessagemakerLogWithString:(NSString *)aString
{
    NSDictionary *aDictionary;
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    aDictionary = [ NSDictionary
                      dictionaryWithObject:aString forKey:OMELogStringKey ];
    [ notificationCenter
        postNotificationName:OMEMessagemakerLog
        object:self
        userInfo:aDictionary ];
}

/* write string to jp.mac-ome.sendmail.log */
+ (void)writeToSendmailLogWithString:(NSString *)aString
{
    NSDictionary *aDictionary;
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    aDictionary = [ NSDictionary
                      dictionaryWithObject:aString forKey:OMELogStringKey ];
    [ notificationCenter
        postNotificationName:OMESendmailLog
        object:self
        userInfo:aDictionary ];
}

/* write string to standard error */
+ (void)writeToStandardOutputWithString:(NSString *)aString
{
    NSDictionary *aDictionary;
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    aDictionary = [ NSDictionary
                      dictionaryWithObject:aString forKey:OMELogStringKey ];
    [ notificationCenter
        postNotificationName:OMEStandardOutput
        object:self
        userInfo:aDictionary ];
}

/* accessor method */
- (void)setFileHandle:(NSFileHandle *)aHandle
{
    fileHandle = aHandle;
}

/*accessor method */
- (NSFileHandle *)fileHandle
{
    return fileHandle;
}

/* write NSString to log file */
- (void)writeString:(NSString *)aString
{
    NSDate *aDate;
    NSData *aData;
    NSString *dateString, *logString;

    aDate = [ NSDate date ];
    dateString = [ aDate
                     descriptionWithCalendarFormat:CALENDAR_FORMAT
                     timeZone:nil
                     locale:nil ];
    logString = [ dateString stringByAppendingString:aString ];
    aData = [ logString dataUsingEncoding:NSUTF8StringEncoding ];
    [ [ self fileHandle ] writeData:aData ];
}

/* write to log file with notification */
- (void)writeWithNotification:(NSNotification *)aNotification
{
    NSDictionary *aDictionary;
    NSString *aString;

    aDictionary = [ aNotification userInfo ];
    aString = [ aDictionary objectForKey:OMELogStringKey ];
    [ self writeString:aString ];
}

/* designated initializer */
- (id)initWithPath:(NSString *)aPath
{
    NSFileHandle *aFileHandle;
    NSFileManager *fileManager;
    NSNotificationCenter *notificationCenter;

    self = [ super init ];
    if ( self ) {
        /* default notification center */
        notificationCenter = [ NSNotificationCenter defaultCenter ];
        if ( aPath ) {
            /* default NSFileManager */
            fileManager = [ NSFileManager defaultManager ];
            /* create file at aPath */
            if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
                [ fileManager
                    createFileAtPath:aPath contents:nil attributes:nil ];
            }
            /* set up fileHandle */
            aFileHandle = [ NSFileHandle
                              fileHandleForWritingAtPath:aPath ];
            [ self setFileHandle:aFileHandle ];
            [ [ self fileHandle ] seekToEndOfFile ];
            /* add self as observer of default notification center */
            [ notificationCenter
                addObserver:self
                selector:@selector(writeWithNotification:)
                name:[ aPath lastPathComponent ]
                object:[ self class ] ];
        } else {
            /* set up fileHandle */
            aFileHandle = [ NSFileHandle fileHandleWithStandardOutput ];
            [ self setFileHandle:aFileHandle ];
            /* add self as observer of default notification center */
            [ notificationCenter
                addObserver:self
                selector:@selector(writeWithNotification:)
                name:OMEStandardOutput
                object:[ self class ] ];
        }
    }
    return self;
}

/* initializer */
- (id)init
{
    return [ self initWithPath:nil ];
}

/* dealloc */
- (void)dealloc
{
    NSNotificationCenter *notificationCenter;

    /* remove self from default notification center */
    notificationCenter = [ NSNotificationCenter defaultCenter ];
    [ notificationCenter removeObserver:self ];
    /* release instances */
    /* dealloc */
//    [super dealloc];
}

/* description */
- (NSString *)description
{
    return [ [ self fileHandle ] description ];
}

@end
