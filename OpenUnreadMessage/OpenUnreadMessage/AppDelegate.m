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
    NSError *error;
    
    NSFileManager *fm = [NSFileManager defaultManager];
    //    NSString *omeCore = @"/Applications/OME_Applications.localized/OME_Core.app";
    
    NSString *home = fm.homeDirectoryForCurrentUser.path;
    NSString *omePrefs = [[[home stringByAppendingPathComponent: @"Library"]
                           stringByAppendingPathComponent: @"Preferences"]
                          stringByAppendingPathComponent: @"OME_Preferences"];
    NSString *viewerAlias = [omePrefs stringByAppendingPathComponent: @"Mail_Reader"];
    NSString *omeRootAlias = [omePrefs stringByAppendingPathComponent: @"OME_Root"];
    
    NSURL *omeViewerURL = [NSURL URLByResolvingAliasFileAtURL: [NSURL fileURLWithPath: viewerAlias]
                                                      options: NSURLBookmarkResolutionWithoutUI | NSURLBookmarkResolutionWithoutMounting
                                                        error: &error];
    NSString *omeViewer = omeViewerURL.path;
    
    NSURL *omeRootURL = [NSURL URLByResolvingAliasFileAtURL: [NSURL fileURLWithPath: omeRootAlias]
                                                      options: NSURLBookmarkResolutionWithoutUI | NSURLBookmarkResolutionWithoutMounting
                                                        error: &error];
    NSString *omeRoot = omeRootURL.path;
    
    NSString *omeTemp = [omeRoot stringByAppendingPathComponent: @"temp"];
    NSString *omeUnreadAliases = [omeTemp stringByAppendingPathComponent:@"unreadAliases"];
    NSURL *omeUnreadAliasesURL = [NSURL fileURLWithPath: omeUnreadAliases];
    BOOL shouldBeRetry = YES;
    int retryCounter = 1000;
    BOOL isStale;
    BOOL result;
    NSString *oneItem;
    while (shouldBeRetry && retryCounter > 0)   {
        shouldBeRetry = NO;
        retryCounter--;
        
        int maxCount = 100; // 2021-9-4 msyk limit to 100 files openning.
        
        NSArray *aliasesArray = [fm contentsOfDirectoryAtPath: omeUnreadAliases error: &error];
        for (oneItem in aliasesArray)    {
            
            if(maxCount <= 0) {
                [[NSApplication sharedApplication] terminate:self];
            }
            
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
                maxCount--;
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
