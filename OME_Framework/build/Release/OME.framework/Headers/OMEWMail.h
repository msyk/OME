//
//  $Id: OMEWMail.h,v 1.4 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sun Feb 6 17:53:57 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

@class WSHeader;

@interface OMEWMail : NSObject
{
    NSArray *upperFields;
    NSString *messageBody;
}

/* accessor method */
- (NSArray *)upperFields;
- (NSString *)messageBody;

/* designated initializer */
- (id)initWithUpperFields:(NSArray *)uFields messageBody:(NSString *)aString;

/* initializer */
- (id)initWithUpperLines:(NSArray *)uLines messageBody:(NSString *)aString;

/* initializer */
- (id)initWithString:(NSString *)aString;

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath;

/* header */
- (WSHeader *)header;

/* message body string */
- (NSString *)messageBodyString;

@end
