//
//  AppDelegate.m
//  OpenUnreadMessage
//
//  Created by 新居雅行 on 2014/08/01.
//  Copyright (c) 2014年 msyk.net. All rights reserved.
//

#import "AppDelegate.h"

@interface AppDelegate ()

@end

@implementation AppDelegate

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification {
    NSFileManager *fm = [NSFileManager defaultManager];
    //    NSString *omeCore = @"/Applications/OME_Applications.localized/OME_Core.app";
    NSString *omeViewer = @"/Applications/OME_Applications.localized/OMEMailViewer.app";
    NSString *omeTemp = [NSHomeDirectory() stringByAppendingPathComponent: @"/Open_Mail_Environment/temp"];
    NSString *omeUnreadAliases = [omeTemp stringByAppendingPathComponent:@"unreadAliases"];
    NSURL *omeUnreadAliasesURL = [NSURL fileURLWithPath: omeUnreadAliases];
    NSError *error;
    BOOL shouldBeRetry = YES;
    int retryCounter = 1000;
    BOOL isStale;
    BOOL result;
    NSString *oneItem;
    while (shouldBeRetry && retryCounter > 0)   {
        shouldBeRetry = NO;
        retryCounter--;
        NSArray *aliasesArray = [fm contentsOfDirectoryAtPath: omeUnreadAliases error: &error];
        for (oneItem in aliasesArray)	{
            NSString *aPath = [omeUnreadAliases stringByAppendingPathComponent:oneItem];
            NSURL *aliasURL = [NSURL fileURLWithPath: aPath];
#ifdef DEBUG
            NSLog(@"Alias: %@", aPath);
#endif
            NSData *bm = [NSURL bookmarkDataWithContentsOfURL: aliasURL
                                                        error: &error];
            if (error)  {
                NSLog(@"%@", error);
            }
            NSURL *originalURL = [NSURL URLByResolvingBookmarkData: bm
                                                           options: NSURLBookmarkResolutionWithoutUI
                                                     relativeToURL: omeUnreadAliasesURL
                                               bookmarkDataIsStale: &isStale
                                                             error: &error];
            if (isStale)  {
#ifdef DEBUG
                NSLog(@"Bookmark is staled.");
#endif
            }
            if (error)  NSLog(@"%@", error);
            
            NSString *rPath = originalURL.path;
            result = [[NSWorkspace sharedWorkspace] openFile:rPath withApplication: omeViewer];
            if ( result ) {
                NSURL *changedURL = nil;
                if ([originalURL.pathExtension isEqualToString: @"ygm"])   {
                    changedURL = [originalURL.URLByDeletingPathExtension URLByAppendingPathExtension: @"mail"];
                } else if ([originalURL.pathExtension isEqualToString: @"html"])   {
                    changedURL = [originalURL.URLByDeletingPathExtension URLByAppendingPathExtension: @"htm"];
                }
                if (changedURL)   {
                    result = [fm moveItemAtURL: originalURL toURL: changedURL error: &error];
                    if (error)  NSLog(@"%@", error);
                    result = [fm removeItemAtURL: aliasURL error:  &error];
                    if (error)  NSLog(@"%@", error);
                    if ([changedURL.pathExtension isEqualToString: @"htm"])  {
                        NSDictionary *mailAttrs = [fm attributesOfItemAtPath: changedURL.path
                                                                       error: &error];
                        NSDate *fmDate = mailAttrs[NSFileModificationDate];
                        NSURL *mailFolder = [changedURL URLByDeletingLastPathComponent];
                        BOOL dateChange = [fm setAttributes: @{NSFileModificationDate: fmDate}
                                               ofItemAtPath: mailFolder.path
                                                      error: &error];
                        if (! dateChange)  NSLog(@"%@", error);
                    }
                }
            } else {
                shouldBeRetry = YES;
                NSLog(@"Can't open: %@", rPath);
            }
        }
    }
    [[NSApplication sharedApplication] terminate:self];
}

- (void)applicationWillTerminate:(NSNotification *)aNotification {
    // Insert code here to tear down your application
}

@end
