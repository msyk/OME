//
//  $Id: OMEDirectory.h,v 1.2 2005/09/03 13:42:12 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Wed Jan 12 15:05:52 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OMEDirectory : NSObject
{
    NSString *path;
    NSArray *contents;
}

/* accessor method */
- (NSString *)path;
- (NSArray *)contents;

/* designated initalizer */
- (id)initWithPath:(NSString *)aPath;

/* all paths */
- (NSArray *)allPaths;

/* paths matching extension */
- (NSArray *)pathsMatchingExtension:(NSString *)pathExtension;

@end
