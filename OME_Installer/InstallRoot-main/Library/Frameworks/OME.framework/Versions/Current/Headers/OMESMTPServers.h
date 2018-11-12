//
//  $Id: OMESMTPServers.h,v 1.2 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Tue Jan 18 15:41:55 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OMESMTPServers : NSObject
{
    NSArray *array;
}

/* accessor methods : smtp */
- (NSArray *)array;

/* designated initializer */
- (id)initWithArray:(NSArray *)anArray;

/* initializer */
- (id)initWithString:(NSString *)aString;

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath;

/* Addresses */
- (NSArray *)addresses;

@end
