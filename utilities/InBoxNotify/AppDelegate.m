//
//  AppDelegate.m
//  InBoxNotify
//
//  Created by Masayuki Nii on 08/10/17.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "AppDelegate.h"

@implementation AppDelegate

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    NSString *omeTemp = [[[ OMEBehavior alloc] initOnCurrentEnv] tempFolderPath];
    
    /*	NSString *omePref = [OMEPaths omePreferences];
     NSString *omeRoot = [omePref stringByAppendingPathComponent:@"OME_Root"];
     
     char omeRootCStr[FILE_PATH_BUFFER_SIZE];
     char resolvedPath[FILE_PATH_BUFFER_SIZE];
     
     [omeRoot getCString:(char*)&omeRootCStr maxLength:FILE_PATH_BUFFER_SIZE encoding:NSUTF8StringEncoding];
     OSStatus st = AliasResolver(omeRootCStr, resolvedPath);
     omeRoot = [NSString stringWithCString:(char*)&resolvedPath encoding:NSUTF8StringEncoding];
     
     NSString *omeTemp = [omeRoot stringByAppendingPathComponent:@"temp"];
     */	NSString *omeUnreadAliases = [omeTemp stringByAppendingPathComponent:@"unreadAliases"];
    
    char checkingPath[ [omeUnreadAliases length] ];
    [omeUnreadAliases getCString:(char*)&checkingPath maxLength:FILE_PATH_BUFFER_SIZE encoding:NSUTF8StringEncoding];
    
    NSArray *pathsToWatch = [NSArray arrayWithObject:omeUnreadAliases];
    FSEventStreamContext context = {0, self, NULL, NULL, NULL};
    FSEventStreamRef stream = FSEventStreamCreate(NULL,
                                                  &mycallback,
                                                  &context,
                                                  (CFArrayRef)pathsToWatch,
                                                  kFSEventStreamEventIdSinceNow, /* Or a previous event ID */
                                                  5.0,
                                                  kFSEventStreamCreateFlagNone /* Flags explained in reference */
                                                  );
    FSEventStreamScheduleWithRunLoop(stream, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
    BOOL startedOK = FSEventStreamStart(stream);
    if (!startedOK) {
        NSLog(@"failed to start the FSEventStream");
    }
    finishedFiles = [NSMutableArray array];
    [finishedFiles retain];
    [self scanUnreadAliasesFolder];
}

void mycallback(
                ConstFSEventStreamRef streamRef,
                void *clientCallBackInfo,
                size_t numEvents,
                void *eventPaths,
                const FSEventStreamEventFlags eventFlags[],
                const FSEventStreamEventId eventIds[])
{
    int i;
    //    char **paths = eventPaths;
    
    // printf("Callback called\n");
    for (i=0; i<numEvents; i++) {
        //int count;
        /* flags are unsigned long, IDs are uint64_t */
        //printf("Change %llu in %s, flags %lu\n", eventIds[i], paths[i], eventFlags[i]);
    }
    id myself = clientCallBackInfo;
    [myself scanUnreadAliasesFolder];
}

- (void)scanUnreadAliasesFolder
{
    NSString *omeTemp = [[[ OMEBehavior alloc] initOnCurrentEnv] tempFolderPath];
    
    //	NSString *omePref = [OMEPaths omePreferences];
    //	NSString *omeRoot = [omePref stringByAppendingPathComponent:@"OME_Root"];
    
//    char omeRootCStr[FILE_PATH_BUFFER_SIZE];
//    char resolvedPath[FILE_PATH_BUFFER_SIZE];
    
    //	[omeRoot getCString:(char*)&omeRootCStr maxLength:FILE_PATH_BUFFER_SIZE encoding:NSUTF8StringEncoding];
    //	/*OSStatus st = */AliasResolver(omeRootCStr, resolvedPath);
    //	omeRoot = [NSString stringWithCString:(char*)&resolvedPath encoding:NSUTF8StringEncoding];
    
    //	NSString *omeTemp = [omeRoot stringByAppendingPathComponent:@"temp"];
    NSString *omeUnreadAliases = [omeTemp stringByAppendingPathComponent:@"unreadAliases"];
    
    NSLog( @"scanUnreadAliasesFolder: %@", omeUnreadAliases );
    
    NSError *error;
    NSArray *aliasesArray = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:omeUnreadAliases error:&error];
    int countArray = 0;
    
    NSString *oneItem;
    BOOL isFirstItem = YES;
    NSMutableString *msg = [ NSMutableString stringWithCapacity: 0 ];
    [ msg appendString: @"Mail from:\n\n" ];
    
    NSUserNotificationCenter *center = [NSUserNotificationCenter defaultUserNotificationCenter];
    
    for ( oneItem in aliasesArray )	{
        if ( ! [ finishedFiles containsObject: oneItem ] )	{
            NSString *aPath = [omeUnreadAliases stringByAppendingPathComponent:oneItem];
            //[aPath getCString:(char*)&omeRootCStr maxLength:FILE_PATH_BUFFER_SIZE encoding:NSUTF8StringEncoding];
            //AliasResolver( omeRootCStr, resolvedPath );
            NSURL *aliasURL = [NSURL fileURLWithPath: aPath];
            NSData *bm = [NSURL bookmarkDataWithContentsOfURL: aliasURL
                                                        error: &error];
            NSURL *originalURL = [NSURL URLByResolvingBookmarkData: bm
                                                           options: 0
                                                     relativeToURL: nil
                                               bookmarkDataIsStale: nil
                                                             error: &error];
            //NSString *rPath = [NSString stringWithCString:(char*)&resolvedPath encoding:NSUTF8StringEncoding];
            NSString *rPath = originalURL.path;
            OMEMessage *curMsg = [[OMEMessage alloc] initWithContentsOfFile: rPath];
            if ( ! isFirstItem )
                [ msg appendString: @", " ];
            [ msg appendString: [[ curMsg fields ] objectForKey: @"From" ]];
            isFirstItem = NO;
            countArray++;
            
            NSUserNotification *notification = [[NSUserNotification alloc]init];
            notification.title = @"OME - Mail Received";
            notification.subtitle = [[ curMsg fields ] objectForKey: @"From" ];
            notification.informativeText = [[ curMsg fields ] objectForKey: @"Subject" ];
            [center deliverNotification: notification];
        }
    }
    [finishedFiles release];
    finishedFiles = aliasesArray;
    [finishedFiles retain];
    
    //	if ( countArray > 0 )	{
    //		NSString *omeFWResources = [OMEPaths omeFrameworkResourcePath];
    
    //		NSData *iconData = [NSData dataWithContentsOfFile:[omeFWResources stringByAppendingPathComponent:@"icon-ome.tif"]];
    //		NSLog( @"Growl Message: %@", msg );
    //		[GrowlApplicationBridge setGrowlDelegate:self];
    //		[GrowlApplicationBridge notifyWithTitle: @"OME - Unread Mails" description: msg 
    //			notificationName: @"Download Mails" iconData: iconData priority: 0 isSticky: YES clickContext:nil];
    //	}
}

- (void)rebootGrawlHelperApp
{
    //	NSArray *params = [NSArray arrayWithObjects: @"-e", @"tell application \"GrowlHelperApp\" to quit", nil ];
    //	NSTask *quitGrowlTask = [ NSTask launchedTaskWithLaunchPath: @"/usr/bin/osascript" arguments: params ];
    //	[ quitGrowlTask waitUntilExit ];
    //
    //	params = [NSArray arrayWithObject: @"/Library/PreferencePanes/Growl.prefPane/Contents/Resources/GrowlHelperApp.app" ];
    //	quitGrowlTask = [ NSTask launchedTaskWithLaunchPath: @"/usr/bin/open" arguments: params ];
    //	[ quitGrowlTask waitUntilExit ];
}

@end
