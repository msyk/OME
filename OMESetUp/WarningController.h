//
//  $Id: WarningController.h,v 1.4 2007/10/02 13:45:36 msyk Exp $
//
//  WarningController.h for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Thu Jun 16 16:38:24 2005 UTC 
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <OME/OME.h>

#define NOTIFICATION_NAME_SHOW_WINDOW2 @"OMESetUpShowWindow2"

@interface WarningController : NSWindowController

- (IBAction)showWindow2:(id)sender;

@end
