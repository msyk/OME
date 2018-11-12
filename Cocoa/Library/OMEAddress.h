//
//  $Id: OMEAddress.h,v 1.2 2005/09/03 13:42:12 wakimoto Exp $
//
//  Mutable class
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Fri Apr 29 04:24:59 2005 UTC.
//  Copyright (c) Shinya Wakimoto, 2005. All rights reserved.
//

#import <Foundation/Foundation.h>

extern NSString *OMEAddressPboardType;

@interface OMEAddress : NSObject <NSCoding>
{
    BOOL nameDisplayFlag;
    NSString *prefix, *name, *suffix, *email;
}

/* accessor methods */
- (void)setNameDisplayFlag:(BOOL)flag;
- (BOOL)nameDisplayFlag;
- (void)setPrefix:(NSString *)aString;
- (NSString *)prefix;
- (void)setName:(NSString *)aString;
- (NSString *)name;
- (void)setSuffix:(NSString *)aString;
- (NSString *)suffix;
- (void)setEmail:(NSString *)aString;
- (NSString *)email;

- (id)initWithString:(NSString *)aString;

/* designated initializer */
- (id)initWithNameDisplayFlag:(BOOL)flag
		       prefix:(NSString *)aPrefix
			 name:(NSString *)aName
		       suffix:(NSString *)aSuffix
			email:(NSString *)eMail;

/* initializer */
- (id)initWithPrefix:(NSString *)aPrefix
		name:(NSString *)aName
	      suffix:(NSString *)aSuffix
	       email:(NSString *)eMail;

/* initializer */
- (id)initWithName:(NSString *)aName
	     email:(NSString *)eMail;

/* description */
- (NSString *)description;

/* compare */
- (NSComparisonResult)compare:(OMEAddress *)aPerson;

@end
