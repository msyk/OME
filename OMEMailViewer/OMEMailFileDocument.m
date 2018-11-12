//
//  OMEMailFileDocument.m
//  OMEMailViewer
//
//  Created by 新居雅行 on 04/09/15.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//

#import "OMEMailFileDocument.h"


@implementation OMEMailFileDocument

- (id)init
{
    self = [super init];
    if (self) {
		
        // Add your subclass-specific initialization here.
        // If an error occurs here, send a [self release] message and return nil.
    }
    return self;
}

- (NSString *)windowNibName {
    // Implement this to return a nib to load OR implement -makeWindowControllers to manually create your controllers.
    return @"OMEMailFileDocument";
}

- (void)windowControllerDidLoadNib:(NSWindowController *) aController
{
    [super windowControllerDidLoadNib:aController];
	// Add any code here that needs to be executed once the windowController has loaded the document's window.
}

- (NSData *)dataRepresentationOfType:(NSString *)type {
    // Implement to provide a persistent data representation of your document OR remove this and implement the file-wrapper or file path based save methods.
    return nil;
}

- (BOOL)loadDataRepresentation:(NSData *)data ofType:(NSString *)type {
    // Implement to load a persistent data representation of your document OR remove this and implement the file-wrapper or file path based load methods.
	
	OMEMessage *thisMessage = [[OMEMessage alloc]initWithData:data];

	hdrSubject = [[thisMessage fields] objectForKey:@"Subject"];
	hdrFrom = [[thisMessage fields] objectForKey:@"From"];
	hdrSender = [[thisMessage fields] objectForKey:@"Sender"];
	hdrContentType = [[thisMessage fields] objectForKey:@"Content-Type"];
	hdrDate = [NSDate dateWithNaturalLanguageString:[[thisMessage fields] objectForKey:@"Date"]];
	hdrTo = [[thisMessage fields] objectForKey:@"To"];
	hdrCc = [[thisMessage fields] objectForKey:@"Cc"];
	if ( [hdrCc length] == 0 )
		hdrCc = [[thisMessage fields] objectForKey:@"CC"];
	bdyMessage = [thisMessage text];
	bdyFont = @"LucidaGrande";
	bdyFontSize = 12;

	NSArray *messageFilePathComponents = [[[self fileURL]path] pathComponents];
	NSString *includedFolderName = [messageFilePathComponents objectAtIndex:([messageFilePathComponents count]-2)];
	NSRange searchedRange = [includedFolderName rangeOfString:@".mpart"];
	if ( (searchedRange.location + searchedRange.length) == [includedFolderName length] )	{
		hasAttachments = YES;
	}
	return YES;
}

- (IBAction)setupReplyMail:(id)sender
{
    NSTask *task = [ [ [ NSTask alloc ] init ] autorelease ];
    [ task setLaunchPath : @"/usr/bin/java" ];
    [ task setCurrentDirectoryPath : NSHomeDirectory() ];
    [ task setArguments : [NSArray arrayWithObjects:@"-cp",@"/Library/Frameworks/OME.framework/Resources/Java/OME_lib.jar",@"OME.mailwriter.OME_MailWriter",@"FILE",[[self fileURL]path],nil] ];
    [ task launch ];
}

- (IBAction)showThisFile:(id)sender
{
	BOOL result = [[NSWorkspace sharedWorkspace]
						selectFile:[[self fileURL]path] 
						inFileViewerRootedAtPath:@""];
}

- (void)windowDidBecomeKey:(NSNotification *)aNotification
{
	NSWindow *targetWindow = [aNotification object];
	id contentOfWindow= [targetWindow contentView];
	NSEnumerator *enumerator = [[contentOfWindow subviews] objectEnumerator];
	NSView *aView;
	while (aView = [enumerator nextObject]) {
		NSRect f = [aView frame];
		//		if ( f.size.height > 100 )
		if ( [aView isKindOfClass:[NSScrollView class]] )	{
			[targetWindow makeFirstResponder:aView];
			break;
		}
	}
	
}

@end
