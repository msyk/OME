//
//  OMEEmailFileDocument2.h
//  OMEMailViewer
//
//  Created by 新居雅行 on 07/02/11.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <WebKit/WebKit.h>
//#import "OME.h"
#import "OMEMessage.h"

@interface OMEMailFileDocument2 : NSDocument {

	IBOutlet WebView *webView;
	IBOutlet NSWindow *headerSheet;
	IBOutlet NSTextView *headerText;

	BOOL isMultiPartMail;
	BOOL hasCertificate;
	BOOL isFinishLoading;
	
	OMEMessage *thisMessage;
	NSURL *thisURL;
	NSString *filePath;
	WebView *printView;
	NSRect currentWebViewFrame;
	
}

- (IBAction)setupReplyMail:(id)sender;
- (IBAction)showThisFile:(id)sender;
- (IBAction)showThisFolder:(id)sender;
- (IBAction)showThisHeaders:(id)sender;
- (IBAction)showThisCert:(id)sender;
- (IBAction)headerSheetClose:(id)sender;
- (void)printDocumentWithSettings:(NSDictionary *)printSettings 
			showPrintPanel:(BOOL)showPrintPanel 
			delegate:(id)delegate 
			didPrintSelector:(SEL)didPrintSelector 
			contextInfo:(void *)contextInfo;
- (float)printPageHeight;
- (float)printPageWidth;
- (NSString *)cleanupBodyText:(NSString *)source;
/*
- (NSString *)createMailHeaderHTML:(NSString *)value label:(NSString *)label idstring:(NSString *)idstring;
- (NSString *)linkedHTML:(NSString *)source;
- (void)getOutHeaderAndBodyFromHTML:(NSString *)source headerText:(NSString **)headerText bodyText:(NSString **)bodyText;
- (NSString *)createLinkToFile:(NSString *)fileName inDirectory:(NSString *)fileParent;
*/
@end

#define	toolbarTagReply			1001
#define	toolbarTagHeader		1002
#define	toolbarTagAttachment	1003
#define	toolbarTagCertificate	1004
