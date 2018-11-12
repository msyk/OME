//
//  HTMLString.h
//  OMEMailViewer
//
//  Created by 新居雅行 on 07/02/11.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <OgreKit/OgreKit.h>

@interface NSMutableString ( OME )
- (void)linkedHTML;
- (void) convertHTMLSourceWithInsertingTagsForColorComment;
- (void)convertHTMLSource;
- (void)appendAsHTMLSource:(NSString *)aString;
- (void)appendAsHTMLSource:(NSString *)aString tag:(NSString *)aTag;
- (void)appendAsHTMLSource:(NSString *)aString tag:(NSString *)aTag attributes:(NSDictionary *)Attributes;
- (void) appendAsString:(NSString *)aString;
- (void) appendAsString:(NSString *)aString tag:(NSString *)aTag;
- (void) appendAsString:(NSString *)aString tag:(NSString *)aTag attributes:(NSDictionary *)Attributes;
- (void)PreAppendAsHTMLSource:(NSString *)aString;
- (void)PreAppendAsHTMLSource:(NSString *)aString tag:(NSString *)aTag;
- (void)PreAppendAsHTMLSource:(NSString *)aString tag:(NSString *)aTag attributes:(NSDictionary *)Attributes;
- (void)PreAppendString:(NSString *)aString;
- (void)encloseByTag:(NSString *)aTag;
- (void)encloseByTag:(NSString *)aTag attributes:(NSDictionary *)Attributes;

@end

