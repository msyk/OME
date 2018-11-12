//
//  $Id: Window1Controller.m,v 1.3 2007/10/02 13:45:36 msyk Exp $
//
//  Window1Controller.m for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Jul 24 16:49:45 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "Window1Controller.h"

#import "Definitions.h"

#define WINDOW_NIB_NAME @"Window1"
#define WINDOW_FRAME_AUTOSAVE_NAME @"Window1"

@implementation Window1Controller

/* designated initializer */
- (id)init
{
    self = [ super initWithWindowNibName:WINDOW_NIB_NAME ];
    if ( self ) { 
        [ self setWindowFrameAutosaveName:WINDOW_FRAME_AUTOSAVE_NAME ];
    }
    return self;
}

/* local method - forward to window1 */
- (void)showWarningPanel
{
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    [ notificationCenter
        postNotificationName:SHOW_WARNING_PANEL
        object:self
        userInfo:nil ];
}

/* local method - forward to window1 */
- (void)showWindow2
{
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    [ notificationCenter
        postNotificationName:SHOW_WINDOW2
        object:self
        userInfo:nil ];
}

/* go to warning panel or window2 */
- (IBAction)nextPage:(id)sender
{
    NSFileManager *fileManager;
    NSString *omePreferences;

    fileManager = [ NSFileManager defaultManager ];
    omePreferences = [ OMEPaths omePreferences ];
    [ self close ];
    if ( [ fileManager fileExistsAtPath:omePreferences ] ) {
        [ self showWarningPanel ];
    } else {
        [ self showWindow2 ];
    }
}

@end
