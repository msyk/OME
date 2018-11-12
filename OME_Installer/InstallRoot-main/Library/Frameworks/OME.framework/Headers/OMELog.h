//
//  $Id: OMELog.h,v 1.5 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sat Dec 11 06:18:42 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

/* log calendar format */
#define CALENDAR_FORMAT @"OME(%Y/%m/%d %H:%M:%S %z): "

@interface OMELog : NSObject 
{
    NSFileHandle *fileHandle;
}

/* write string to jp.mac-ome.downloadmails.log */
+ (void)writeToDownloadmailsLogWithString:(NSString *)aString;

/* write string to jp.mac-ome.mailwriter.log */
+ (void)writeToMailwriterLogWithString:(NSString *)aString;

/* write string to jp.mac-ome.messagemaker.log */
+ (void)writeToMessagemakerLogWithString:(NSString *)aString;

/* write string to jp.mac-ome.sendmail.log */
+ (void)writeToSendmailLogWithString:(NSString *)aString;

/* write string to standard error */
+ (void)writeToStandardOutputWithString:(NSString *)aString;

/* accessor method */
- (NSFileHandle *)fileHandle;

/* designated initializer */
- (id)initWithPath:(NSString *)aPath;

@end
