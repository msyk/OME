/*
 *  $Id: OMEMailDelivery.h,v 1.7 2005/09/03 13:42:13 wakimoto Exp $
 *
 *  Created by Shinya Wakimoto.
 *  Revision 1.1, Sat Dec 11 06:18:42 2004 UTC
 *  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
 */

#import <Foundation/Foundation.h>

@class OMEBehavior;
@class OMEPaths;
@class OMELog;
@class OMESMTPServer;

@interface OMEMailDelivery : NSObject
{
    OMEPaths *paths;
    OMEBehavior *behavior;
    OMESMTPServer *server;
    OMELog *sendmailLog;
    OMELog *standardOutputLog;
}

/* designated initializer */
- (id)initWithOMEPaths:(OMEPaths *)omePaths;

/* deliver message at paths */ 
- (void)deliverPaths:(NSArray *)aArray;

@end
