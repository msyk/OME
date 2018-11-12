//
//  $Id: OMEAttachment.h,v 1.6 2005/09/03 13:42:12 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sat Nov 13 14:49:10 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OMEAttachment : NSObject
{
    NSAttributedString *attributedString;
}

/* accessor method */
- (NSAttributedString *)attributedString;

/* designated initializer */
- (id)initWithAttributedString:(NSAttributedString *)aAttributedString;

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath;

@end
