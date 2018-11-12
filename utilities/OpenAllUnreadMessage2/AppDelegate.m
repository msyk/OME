//
//  AppDelegate.m
//  OpenAllUnreadMessage2
//
//  Created by Masayuki Nii on 08/10/14.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

#import "AppDelegate.h"
//#import "OMEBehavior.h"

OSStatus ResolveAliasPath(char *sourceCStr, char *resolvedCStr);

@implementation AppDelegate

- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
//	char omeRootCStr[FILE_PATH_BUFFER_SIZE];
//	char resolvedPath[FILE_PATH_BUFFER_SIZE];

	NSString *omeCore = @"/Applications/OME_Applications.localized/OME_Core.app";
	
    NSString *omeTemp = @"/Users/msyk/Open_Mail_Environment/temp";
 //   NSString *omeTemp = [[[ OMEBehavior alloc] initOnCurrentEnv] tempFolderPath];

/*	NSString *omePref = [OMEPaths omePreferences];
	NSString *omeRoot = [omePref stringByAppendingPathComponent:@"OME_Root"];
	
	[omeRoot getCString:(char*)&omeRootCStr maxLength:FILE_PATH_BUFFER_SIZE encoding:NSUTF8StringEncoding];
	AliasResolver((char*)omeRootCStr, (char*)&resolvedPath);
	omeRoot = [NSString stringWithCString:(char*)&resolvedPath encoding:NSUTF8StringEncoding];
	
	NSString *omeTemp = [omeRoot stringByAppendingPathComponent:@"temp"];
*/	NSString *omeUnreadAliases = [omeTemp stringByAppendingPathComponent:@"unreadAliases"];
    NSError *error;
    NSArray *aliasesArray = [[NSFileManager defaultManager] contentsOfDirectoryAtPath: omeUnreadAliases
                                                                                error: &error];
	NSString *oneItem;
	for ( oneItem in aliasesArray )	{
		NSString *aPath = [omeUnreadAliases stringByAppendingPathComponent:oneItem];
		//[aPath getCString:(char*)&omeRootCStr maxLength:FILE_PATH_BUFFER_SIZE encoding:NSUTF8StringEncoding];
		//AliasResolver((char*)omeRootCStr, (char*)&resolvedPath);
        NSURL *aliasURL = [NSURL fileURLWithPath: aPath];
        NSData *bm = [NSURL bookmarkDataWithContentsOfURL: aliasURL
                                                    error: &error];
        if (error)  {
            NSLog(@"%@", error);
        }
        NSURL *originalURL = [NSURL URLByResolvingBookmarkData: bm
                                                       options: 0
                                                 relativeToURL: nil
                                           bookmarkDataIsStale: nil
                                                         error: &error];
        if (error)  {
            NSLog(@"%@", error);
        }

        //NSString *rPath = [NSString stringWithCString:(char*)&resolvedPath encoding:NSUTF8StringEncoding];
        NSString *rPath = originalURL.path;
		BOOL isOpen = [[NSWorkspace sharedWorkspace] openFile:rPath withApplication: omeCore];
		if ( ! isOpen )
			NSLog( @"Coundn't open the file: %@", rPath );
	}

//	NSArray *params = [NSArray arrayWithObjects: @"-e", @"tell application \"GrowlHelperApp\" to quit", nil ];
//	NSTask *quitGrowlTask = [ NSTask launchedTaskWithLaunchPath: @"/usr/bin/osascript" arguments: params ];
//	[ quitGrowlTask waitUntilExit ];
//
//	params = [NSArray arrayWithObject: @"/Library/PreferencePanes/Growl.prefPane/Contents/Resources/GrowlHelperApp.app" ];
//	quitGrowlTask = [ NSTask launchedTaskWithLaunchPath: @"/usr/bin/open" arguments: params ];
//	[ quitGrowlTask waitUntilExit ];
//
	[mySelf terminate:self];
}

@end

/*

#define finish if(er != noErr){printf("ERROR!");goto procEnd;}

OSStatus ResolveAliasPath(char *sourceCStr, char *resolvedCStr)
{
    OSStatus er = 0;
    FSRef targetRef;
    Boolean b1, b2;
    FSCatalogInfoBitmap infoBit;
    FSCatalogInfo catInfo;
 
	er = FSPathMakeRef ((unsigned char *)sourceCStr, &targetRef, NULL);
	infoBit = kFSCatInfoFinderInfo;
	er = FSGetCatalogInfo (&targetRef, infoBit, &catInfo, NULL, NULL, NULL); finish;
	
	// This part depends on endian.
	UInt16 finderFlag;
	if ( CFByteOrderGetCurrent() == CFByteOrderLittleEndian )
		finderFlag = catInfo.finderInfo[9] * 256 + catInfo.finderInfo[8];
	else
		finderFlag = catInfo.finderInfo[8] * 256 + catInfo.finderInfo[9];
		
	if ( finderFlag & kIsAlias == 0)   {   //引数のファイルはエイリアスではない
		resolvedCStr = resolvedCStr;  //引数のパスをそのまま戻す
	}
	else    {   //引数のファイルはエイリアス
		er = FSResolveAliasFile (&targetRef, TRUE, &b1, &b2);    finish;
		er = FSRefMakePath (&targetRef, (unsigned char *)resolvedCStr, 1024);      finish;
	}
procEnd:
    return er;
}
*/