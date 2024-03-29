//
//  $Id: OMEAlias.m,v 1.4 2005/09/04 10:23:15 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Mon Jan 10 06:48:30 2005 UTC
//  Copyright (C) Shinya Wakimoto, 2005. All rights reserved.
//

#import "OMEAlias.h"

//#import "AliasResolver.h"
//#import "MakeNewAlias.h"

/* Format */
#define DESCRIPTION @"\nsource = %@\nalias = %@"

@implementation OMEAlias

/* accessor method */
- (void)setSource:(NSString *)aString
{
    source = aString ;
}

/* accessor method */
- (NSString *)source
{
    return source;
}

/* accessor method */
- (void)setAlias:(NSString *)aString
{
    alias = aString ;
}

/* accessor method */
- (NSString *)alias
{
    return alias;
}

/* designated initializer */
- (id)initWithSource:(NSString *)sourceFile alias:(NSString *)aliasFile
{
    self = [ super init ];
    if ( self ) {
        [ self setSource:sourceFile ];
        [ self setAlias:aliasFile ];
    }
    return self;
}

/* initializer */
- (id)initWithContentsOfAliasFile:(NSString *)aliasFile
{
    NSError *error;
    NSURL *aliasURL = [NSURL fileURLWithPath: aliasFile];
    NSURL *resolvedURL = [NSURL URLByResolvingAliasFileAtURL: aliasURL
                                                     options: NSURLBookmarkResolutionWithoutUI error: &error];
    return [ self initWithSource:resolvedURL.path alias:aliasFile ];
}

/* dealloc */

/* description */
- (NSString *)description
{
    return [ NSString stringWithFormat:DESCRIPTION,
        [ self source ], [ self alias ] ];
}

/* Alias Operations */
/*
- (BOOL)makeNewAlias
{
    BOOL flag;
    int er;

    er = MakeNewAlias ( [ [ self alias ] fileSystemRepresentation ],
        [ [ self source ] fileSystemRepresentation ] );
    if ( er == 0 ) {
        flag = YES;
    } else {
        flag = NO;
    }
    return flag;
}
*/
@end
