//
//  OMEMailFileDocument.h
//  OMEMailViewer
//
//  Created by 新居雅行 on 04/09/15.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//

/*!
@class OMEMailFileDocument
OMEのメールファイルを処理するためのクラスです。
*/
#import <Cocoa/Cocoa.h>
#import <OME/OMEMessage.h>


@interface OMEMailFileDocument : NSDocument {
		
	NSString * hdrSubject;
	NSString * hdrFrom;
	NSString * hdrSender;
	NSString * hdrContentType;
	NSDate * hdrDate;
	NSString * hdrTo;
	NSString * hdrCc;
	NSString * bdyMessage;
	BOOL hasAttachments;
	NSString * bdyFont;
	int bdyFontSize;
	
	BOOL isFlowLayout;
	
	NSWindow * window;
	NSView * bodyView;
}

- (IBAction)setupReplyMail:(id)sender;
- (IBAction)showThisFile:(id)sender;

@end
