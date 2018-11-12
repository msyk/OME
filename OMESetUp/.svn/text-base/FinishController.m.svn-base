//
//  $Id: FinishController.m,v 1.6 2007/10/02 13:45:36 msyk Exp $
//
//  FinishController.m for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Thu Jun 16 16:38:24 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "FinishController.h"

/* java */
#define JAVA @"/usr/bin/java"
#define WINDOW_NIB_NAME @"Finish"
#define WINDOW_FRAME_AUTOSAVE_NAME @"FinishPanel"

@implementation FinishController

/* designated initializer */
- (id)init
{
    self = [ super initWithWindowNibName:WINDOW_NIB_NAME ];
    if ( self ) { 
        [ self setWindowFrameAutosaveName:WINDOW_FRAME_AUTOSAVE_NAME ];
    }
    return self;
}

/* download mails */
- (IBAction)downloadMails:(id)sender
{
    NSArray *anArray;
    NSTask *aTask;

    /* create a NSTask instance */
    aTask = [ [ NSTask alloc ] init ];
    /* set up array of arguments */
    anArray = [ NSArray arrayWithObjects:@"-cp",
        @"/Library/Frameworks/OME.framework/Resources/OME_lib.jar",
        @"OME.downloadmails.OME_DownloadMails",
        nil ];
    /* set up and lauch aTask */
    [ aTask setLaunchPath:JAVA ];
    [ aTask setArguments:anArray ];
    [ aTask launch ];
    [ aTask waitUntilExit ];
    [ aTask release ];
}

@end
