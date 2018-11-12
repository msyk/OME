//
//  $Id: Window4Controller.m,v 1.3 2007/10/02 13:45:36 msyk Exp $
//
//  Window4Controller.m for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Fri Jul 22 16:56:44 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "Window4Controller.h"

#import "Definitions.h"

/* window4 */
#define WINDOW_NIB_NAME @"Window4"
#define WINDOW_FRAME_AUTOSAVE_NAME @"Window4"

/* open panel */
#define TITLE_KEY @"title"
#define DEFAULT_TITLE @"Select Mailbox Folder"
#define PROMPT_KEY @"prompt"
#define DEFAULT_PROMPT @"Select"

NSString *OMEMailBoxPathKey = @"OMEMailBoxPathKey";

@implementation Window4Controller

/* designated initializer */
- (id)init
{
    self = [ super initWithWindowNibName:WINDOW_NIB_NAME ];
    if ( self ) { 
        [ self setWindowFrameAutosaveName:WINDOW_FRAME_AUTOSAVE_NAME ];
    }
    return self;
}

/* show window4 */
- (IBAction)showWindow:(id)sender
{
    NSString *aPath;

    if ( mailBoxPath ) {
        aPath = [ OMEPaths defaultMailBox ];
        [ mailBoxPath setStringValue:aPath ];
    }
    [ super showWindow:sender ];
}

/* Window4.nib is loaded. */
- (void)windowDidLoad
{
    NSString *aPath;

    aPath = [ OMEPaths defaultMailBox ];
    [ mailBoxPath setStringValue:aPath ];
}

/* back to window3 */
- (IBAction)showWindow3:(id)sender
{
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    [ notificationCenter
        postNotificationName:SHOW_WINDOW3
        object:self
        userInfo:nil ];
    [ self close ];
}

/* Open Panel */
- (IBAction)selectFolder:(id)sender
{
    NSBundle *aBundle;
    NSOpenPanel *openPanel;
    NSString *aPath, *titleString, *promptString;

    /* create NSBundle instance */
    aBundle = [ NSBundle mainBundle ];
    titleString =  [ aBundle
                       localizedStringForKey:TITLE_KEY
                       value:DEFAULT_TITLE
                       table:nil ];
    promptString =  [ aBundle
                        localizedStringForKey:PROMPT_KEY
                        value:DEFAULT_PROMPT
                        table:nil ];
    /* create NSOpenPanel instance */
    openPanel = [ NSOpenPanel openPanel ];
    [ openPanel setAllowsMultipleSelection:NO ];
    [ openPanel setCanChooseDirectories:YES ];
    [ openPanel setCanChooseFiles:NO ];
    [ openPanel setPrompt:promptString ];
    [ openPanel setTitle:titleString ];
    [ openPanel runModalForDirectory:NSHomeDirectory() file:nil types:nil ]; 
    aPath = [ openPanel filename ];
    [ mailBoxPath setStringValue:aPath ];
}

/* forward to FinishPanel */
- (IBAction)showFinishPanel:(id)sender
{
    NSDictionary *aDictionary;
    NSNotificationCenter *notificationCenter;
    NSString *aPath;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    aPath = [ mailBoxPath stringValue ];
    aDictionary = [ NSDictionary
                      dictionaryWithObject:aPath forKey:OMEMailBoxPathKey ];
    [ notificationCenter
        postNotificationName:SHOW_FINISH_PANEL
        object:self
        userInfo:aDictionary ];
    [ self close ];
}

@end
