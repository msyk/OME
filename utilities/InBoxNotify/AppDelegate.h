//
//  AppDelegate.h
//  InBoxNotify
//
//  Created by Masayuki Nii on 08/10/17.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <OME.h>
//#import <Growl/Growl.h>

#define FILE_PATH_BUFFER_SIZE	2048

@interface AppDelegate : NSObject /*<GrowlApplicationBridgeDelegate>*/ {

	IBOutlet NSApplication *myApp;
	
	@private
	NSArray *finishedFiles;
	
}
- (void)applicationDidFinishLaunching:(NSNotification *)aNotification;
- (void)scanUnreadAliasesFolder;
- (void)rebootGrawlHelperApp;

@end

void mycallback(
    ConstFSEventStreamRef streamRef,
    void *clientCallBackInfo,
    size_t numEvents,
    void *eventPaths,
    const FSEventStreamEventFlags eventFlags[],
    const FSEventStreamEventId eventIds[]);