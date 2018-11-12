//
//  $Id: WarningController.m,v 1.4 2005/09/03 13:42:13 wakimoto Exp $
//
//  WarningController.m for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Thu Jun 16 16:38:24 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "WarningController.h"

#import "Definitions.h"

#define WINDOW_NIB_NAME @"Warning"
#define WINDOW_FRAME_AUTOSAVE_NAME @"WarningPanel"

@implementation WarningController

/* designated initializer */
- (id)init
{
    self = [ super initWithWindowNibName:WINDOW_NIB_NAME ];
    if ( self ) { 
        [ self setWindowFrameAutosaveName:WINDOW_FRAME_AUTOSAVE_NAME ];
    }
    return self;
}

/* show window2 */
- (IBAction)showWindow2:(id)sender
{
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    [ notificationCenter
        postNotificationName:SHOW_WINDOW2
        object:self
        userInfo:nil ];
    [ self close ];
}

@end
