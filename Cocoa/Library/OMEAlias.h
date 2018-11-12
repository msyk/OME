//
//  $Id: OMEAlias.h,v 1.2 2005/09/03 13:42:12 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Mon Jan 10 06:48:30 2005 UTC
//  Copyright (C) Shinya Wakimoto, 2005. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OMEAlias : NSObject
{
    NSString *source, *alias;
}

/* accessor methods */
- (NSString *)source;
- (NSString *)alias;

/* designated initializer */
- (id)initWithSource:(NSString *)sourceFile alias:(NSString *)aliasFile;

/* initializer */
- (id)initWithContentsOfAliasFile:(NSString *)aliasFile;

/* Alias Operations */
//- (BOOL)makeNewAlias;

@end
