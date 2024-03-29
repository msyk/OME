//
//  OMEEmailFileDocument2.m
//  OMEMailViewer
//
//  Created by 新居雅行 on 07/02/11.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//
/*
 2008/5/12 Icons come from
 http://dryicons.com/free-icons/preview/aesthetica-version-2/
 このアイコンはWeb利用以外は有償になるのでやめた
 */

/*
 2008/5/24
 http://lbaumann.com/
 ALBOOK
 */

#import "OMEMailFileDocument2.h"
#import "OMEPaths.h"

@implementation OMEMailFileDocument2

- (void)awakeFromNib
{
}

- (NSString *)windowNibName {
    return @"OMEMailFileDocument2";
}

- (NSData *)dataRepresentationOfType:(NSString *)type {
    return nil;
}

- (BOOL)loadDataRepresentation:(NSData *)data ofType:(NSString *)type {
    return YES;
}

- (void)windowControllerDidLoadNib:(NSWindowController *)windowController
{
    [[webView mainFrame]loadHTMLString: [thisMessage htmlWellFormedString] baseURL: thisURL];
    [[[[self windowControllers]lastObject]window]makeFirstResponder:webView];
}

- (void)webView:(WebView *)sender didFinishLoadForFrame:(WebFrame *)frame
{
    //	NSLog(@"didFinishLoadForFrame");
    
    isFinishLoading = YES;
}

- (void)webView:(WebView *)sender decidePolicyForNavigationAction:(NSDictionary *)actionInformation
        request:(NSURLRequest *)request frame:(WebFrame *)frame decisionListener:(id)listener {
    NSString *host = [[request URL] relativeString];
    NSString *scheme = [[request URL] scheme];
    
    //	NSLog(@"decidePolicyForNavigationAction");
    //	NSLog(@"Delegate: %@", [[request URL]resourceSpecifier]);
    //	NSLog(@"Scheme: %@", scheme);
    
    if ( [scheme caseInsensitiveCompare:@"file"] == NSOrderedSame ) 	{
        if ( isFinishLoading )	{
            [[NSWorkspace sharedWorkspace]openURL:[request URL]];
            [listener ignore];
        }
        else
            [listener use];
    }
    else if ( [scheme caseInsensitiveCompare:@"applewebdata"] == NSOrderedSame)
        [listener use];
    else if ( [scheme caseInsensitiveCompare:@"http"] == NSOrderedSame)		{
        [listener ignore];
        [[NSWorkspace sharedWorkspace]openURL:[request URL]];
    }
    else if ( [scheme caseInsensitiveCompare:@"https"] == NSOrderedSame)	{
        [listener ignore];
        [[NSWorkspace sharedWorkspace]openURL:[request URL]];
    }
    else if ( [scheme caseInsensitiveCompare:@"mailto"] == NSOrderedSame)	{
        [listener ignore];
        
        NSString* omeResPath = @"/Library/Frameworks/OME.framework/Resources";
        NSString* omeJavaLibPath = [omeResPath stringByAppendingString:@"/Java/OME-8.0.jar"];
        
        NSTask *task = [ [ NSTask alloc ] init ];
        [ task setLaunchPath : @"/usr/bin/java" ];
        [ task setCurrentDirectoryPath : NSHomeDirectory() ];
        NSArray *args = @[@"-cp", omeJavaLibPath, @"OME.mailwriter.OME_MailWriter",@"ADDRESS",
                          [[request URL]resourceSpecifier]];
        [ task setArguments: args];
        //       NSLog(@"%@", args);
        [ task launch ];
    }
    else if ( [scheme caseInsensitiveCompare:@"quicklook"] == NSOrderedSame)	{
        [listener ignore];
        NSString *docPath = [[request URL]resourceSpecifier];
        NSTask *task = [ [ NSTask alloc ] init ];
        [ task setLaunchPath : @"/usr/bin/qlmanage" ];
        [ task setCurrentDirectoryPath : NSHomeDirectory() ];
        [ task setArguments : [NSArray arrayWithObjects: @"-p", docPath, nil] ];
        [ task launch ];
    }
    else
        NSLog(@"Unsupported scheme");
}


- (void)webView:(WebView *)sender decidePolicyForNewWindowAction:(NSDictionary *)actionInformation
        request:(NSURLRequest *)request frame:(WebFrame *)frame decisionListener:(id)listener {
    NSString *host = [[request URL] absoluteString];
    NSLog(@"decidePolicyForNewWindowAction");
    NSLog(@"Delegate: %@", host);
    [listener use];
}

- (WebView *)webView:(WebView *)sender createWebViewWithRequest:(NSURLRequest *)request
{
    //	NSLog(@"createWebViewWithRequest");
    return sender;
}


- (BOOL)webView:(WebView *)sender shouldPerformAction:(SEL)action fromSender:(id)fromObject
{
    NSLog(@"shouldPerformAction: %@", [NSString stringWithUTF8String:sel_getName(action)]);
    return YES;
}

- (BOOL)readFromURL:(NSURL *)absoluteURL ofType:(NSString *)typeName error:(NSError **)outError
{
    NSLog(@"%@",absoluteURL);
    filePath = [absoluteURL path];
    thisMessage = [[OMEMessage alloc]initWithContentsOfFile:filePath];
    //	[thisMessage dumpOMEMessage];
    thisURL = absoluteURL;
    
    hasCertificate = [thisMessage hasCertificate];
    isMultiPartMail = [thisMessage isMultiPartMail];
    isFinishLoading = NO;
    
    return YES;
}

- (IBAction)setupReplyMail:(id)sender
{
    NSString* omeResPath = @"/Library/Frameworks/OME.framework/Resources";
    NSString* omeJavaLibPath = [omeResPath stringByAppendingString:@"/Java/OME-8.0.jar"];
    
    NSString *targetPath = [[self fileURL]path];
    NSString *cpParam = @"/Library/Frameworks/OME.framework/Resources/Java/OME-8.0.jar:/Library/Frameworks/OME.framework/Resources/Java/activation.jar:/Library/Frameworks/OME.framework/Resources/Java/mail.jar:/Library/Frameworks/OME.framework/Resources/Java/pop3.jar";
//    NSTask *task = [ [ NSTask alloc ] init ];
//    [ task setLaunchPath : @"/usr/bin/java" ];
//    [ task setCurrentDirectoryPath : NSHomeDirectory() ];
//    NSArray *args = @[@"-cp", omeJavaLibPath,
//                      @"-Dfile.encoding=UTF-8",
//                      @"OME.mailwriter.OME_MailWriter",@"FILE",
//                      [targetPath stringByAddingPercentEscapesUsingEncoding: NSUTF8StringEncoding]];
//    [ task setArguments: args];
//    NSLog(@"%@", args);
//    [ task launch ];
    
    NSString *cmd = [NSString stringWithFormat:
                     @"/usr/bin/java -cp %@ OME.mailwriter.OME_MailWriter FILE \"%@\"",
                     cpParam,
                     [targetPath stringByReplacingOccurrencesOfString: @"\""
                                                           withString: @"\\\""
                                                              options:0
                                                                range: NSMakeRange(0, targetPath.length)]];
    int returnValue = system([cmd UTF8String]);
    NSLog(@"%@", cmd);
    NSLog(@"%d", returnValue);
}

- (void)printDocumentWithSettings: (NSDictionary *)printSettings
                   showPrintPanel: (BOOL)showPrintPanel
                         delegate: (id)delegate
                 didPrintSelector: (SEL) didPrintSelector
                      contextInfo: (void *)contextInfo
{
    /*
     [[[[self windowControllers]lastObject]window] disableScreenUpdatesUntilFlush];
     [[[[self windowControllers]lastObject]window] disableFlushWindow];
     
     float printAreaWidth = [self printPageWidth];
     float printAreaHeight = [self printPageHeight];
     NSRect contentRect = NSMakeRect( 0.0, 0.0, printAreaWidth, printAreaHeight );	//just one page
     
     NSWindow *dummyWindow = [[NSWindow alloc] initWithContentRect:contentRect
     styleMask:NSBorderlessWindowMask backing: NSBackingStoreRetained  defer:NO];
     NSGraphicsContext *gc = [NSGraphicsContext graphicsContextWithWindow:dummyWindow];
     
     currentWebViewFrame = [webView frame];
     NSString *htmlSource = [[NSString alloc]initWithData:[[[webView mainFrame]dataSource]data]
     encoding:NSUTF8StringEncoding];
     
     printView = [[WebView alloc] initWithFrame:contentRect frameName:nil groupName:nil];
     
     [dummyWindow setContentView:printView];
     
     [[printView mainFrame]loadHTMLString:htmlSource baseURL:thisURL];
     WebFrameView *printWFView = [[printView mainFrame]frameView];
     [printWFView setAllowsScrolling: NO];
     
     WebPreferences *printWebPref = [WebPreferences standardPreferences];
     [printWebPref setShouldPrintBackgrounds: YES];
     [printView setPreferences: printWebPref];
     [printView setNeedsDisplay: YES];
     
     NSView *printDocView = [printWFView documentView];
     if ( [printDocView frame].size.height > printAreaHeight )
     [printView setFrameSize:NSMakeSize( printAreaWidth, [printDocView frame].size.height )];
     
     [[printWFView documentView]layout];
     [printWFView displayRectIgnoringOpacity:contentRect inContext:gc];
     
     NSPrintOperation *op = [NSPrintOperation
     printOperationWithView: printDocView
     printInfo: [self printInfo]];
     [op setShowPanels: showPrintPanel];
     [self runModalPrintOperation: op
     delegate: self
     didRunSelector: @selector(documentDidRunModalPrintOperation:success:contextInfo:)
     contextInfo: contextInfo];
     
     */
    [[[[self windowControllers]lastObject]window] disableScreenUpdatesUntilFlush];
    [[[[self windowControllers]lastObject]window] disableFlushWindow];
    
    float printAreaWidth = [self printPageWidth];
    float printAreaHeight = [self printPageHeight];
    NSRect contentRect = NSMakeRect( 0.0, 0.0, printAreaWidth, printAreaHeight );	//just one page
    
    currentWebViewFrame = [webView frame];
    NSString *htmlSource = [[NSString alloc]initWithData:[[[webView mainFrame]dataSource]data]
                                                encoding:NSUnicodeStringEncoding];
    
    printView = webView;
    [printView setFrame: contentRect];
    WebFrameView *printWFView = [[printView mainFrame]frameView];
    [printWFView setAllowsScrolling: NO];
    WebPreferences *printWebPref = [WebPreferences standardPreferences];
    [printWebPref setShouldPrintBackgrounds: YES];
    [printView setPreferences: printWebPref];
    [printView setNeedsDisplay: YES];
    NSView *printDocView = [printWFView documentView];
    
    if ( [printDocView frame].size.height > printAreaHeight )
        [printView setFrameSize:NSMakeSize( printAreaWidth, [printDocView frame].size.height )];
    
    NSPrintOperation *op = [NSPrintOperation
                            printOperationWithView: printView
                            printInfo: [self printInfo]];
    //    [op setShowPanels: showPrintPanel];
    [op showsPrintPanel];
    [self runModalPrintOperation: op
                        delegate: self
                  didRunSelector: @selector(documentDidRunModalPrintOperation:success:contextInfo:)
                     contextInfo: contextInfo];
    
}
- (void)documentDidRunModalPrintOperation: (NSDocument *)document
                                  success: (BOOL)success
                              contextInfo: (void *)contextInfo
{
    [webView setFrame: currentWebViewFrame];
    [[[webView mainFrame]frameView] setAllowsScrolling: YES];
    [webView setNeedsDisplay: YES];
    [webView drawRect:currentWebViewFrame];
    [webView display];
    [[[[self windowControllers]lastObject]window] display];
    [[[[self windowControllers]lastObject]window] enableFlushWindow];
}


- (BOOL)knowsPageRange:(NSRangePointer)range {
    NSView *printDocView = [[[printView mainFrame]frameView] documentView];
    
    NSRect bounds = [printDocView bounds];
    float printHeight = [self printPageHeight];
    
    range->location = 1;
    range->length = NSHeight(bounds) / printHeight + 1;
    return YES;
}

- (NSRect)rectForPage:(int)page {
    NSView *printDocView = [[[printView mainFrame]frameView] documentView];
    NSRect bounds = [printDocView bounds];
    float pageHeight = [self printPageHeight];
    return NSMakeRect( NSMinX(bounds), NSMaxY(bounds) - page * pageHeight,
                      NSWidth(bounds), pageHeight );
}

- (float)printPageHeight {
    NSPrintInfo *currentPrintInfo = [self printInfo];
    NSSize pageSize = [currentPrintInfo paperSize];
    float printAreaHeight = pageSize.height - [currentPrintInfo topMargin] - [currentPrintInfo bottomMargin];
    
    float scale = [[[currentPrintInfo dictionary] objectForKey:NSPrintScalingFactor] floatValue];
    return printAreaHeight / scale;
}

- (float)printPageWidth {
    
    NSPrintInfo *currentPrintInfo = [self printInfo];
    NSSize pageSize = [currentPrintInfo paperSize];
    float printAreaWidth = pageSize.width - [currentPrintInfo rightMargin] - [currentPrintInfo leftMargin];
    float scale = [[[currentPrintInfo dictionary] objectForKey:NSPrintScalingFactor] floatValue];
    return printAreaWidth / scale;
}

- (IBAction)showThisFile:(id)sender
{
    BOOL result = [[NSWorkspace sharedWorkspace]
                   selectFile:[[self fileURL]path]
                   inFileViewerRootedAtPath:@""];
}

- (void)windowDidBecomeKey:(NSNotification *)aNotification
{
    /*
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
     */
}

- (NSString *)cleanupBodyText:(NSString *)source
{
    return source;
}

- (IBAction)showThisFolder:(id)sender{}

- (IBAction)showThisCert:(id)sender{}

- (IBAction)showThisHeaders:(id)sender
{
    NSApplication* myApp = [NSApplication sharedApplication];
    NSString *readHeaders = [NSString stringWithString:[thisMessage realHeadersString]];
    [headerText setString:readHeaders];
    [myApp beginSheet: headerSheet
       modalForWindow: [[[self windowControllers]lastObject]window]
        modalDelegate: self 
       didEndSelector: @selector(headerSheetDidEnd:returnCode:contextInfo:)
          contextInfo: nil];
    [myApp endSheet: headerSheet];
}

- (void) headerSheetDidEnd: (NSWindow *) sheet 
                returnCode: (int) returnCode 
               contextInfo: (void *) contextInfo
{
}

- (IBAction)headerSheetClose:(id)sender
{
    NSApplication* myApp = [NSApplication sharedApplication];
    [myApp stopModal];
    [headerSheet orderOut:self];
}

- (BOOL)validateToolbarItem:(NSToolbarItem *)toolbarItem {
    BOOL enable = YES;
    if ([toolbarItem tag] == toolbarTagAttachment)
        enable = isMultiPartMail;
    if ([toolbarItem tag] == toolbarTagCertificate)
        enable = hasCertificate;
    return enable;
}

@end
