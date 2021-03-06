//
//  $Id: OMEDirectory.m,v 1.4 2005/09/03 13:42:12 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Wed Jan 12 15:05:52 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "OMEDirectory.h"

@implementation OMEDirectory

/* accessor method */
- (void)setPath:(NSString *)aPath
{
    path = aPath;
}

/* accessor method */
- (NSString *)path
{
    return path;
}

/* accessor method */
- (void)setContents:(NSArray *)anArray
{
    contents = anArray;
}

/* accessor method */
- (NSArray *)contents
{
    return contents;
}

/* designated initalizer */
- (id)initWithPath:(NSString *)aPath
{
    NSArray *anArray;
    NSFileManager *fileManager;

    self = [ super init ];
    if ( self ) {
        fileManager = [ NSFileManager defaultManager ];
        [ self setPath:aPath ];
        NSError *error;
        anArray = [ fileManager contentsOfDirectoryAtPath: [ self path ]
                                                    error: &error ];
        [ self setContents:anArray ];
    }
    return self;
}

/* dealloc */

/* all paths */
- (NSArray *)allPaths
{
    NSArray *anArray;
    NSEnumerator *aEnumerator;
    NSString *content, *aPath;

    anArray = [ NSArray array ];
    aEnumerator = [ [ self contents ] objectEnumerator ];
    while ( ( content = [ aEnumerator nextObject ] ) != nil ) {
        aPath = [ [ self path ] stringByAppendingPathComponent:content ];
        anArray = [ anArray arrayByAddingObject:aPath ];
    }
    return anArray;
}

/* paths matching extension */
- (NSArray *)pathsMatchingExtension:(NSString *)pathExtension
{
    NSArray *anArray;
    NSEnumerator *aEnumerator;
    NSString *aString, *aPath, *content, *pExtension;

    anArray = [ NSArray array ];
    aEnumerator = [ [ self contents ] objectEnumerator ];
    while ( ( content = [ aEnumerator nextObject ] ) != nil ) {
        aString = [ content stringByDeletingPathExtension ];
        pExtension = [ content pathExtension ];
        if ( [ aString hasSuffix:@"~" ] == NO ) {
            if ( [ pExtension isEqualToString:pathExtension ] ) {
                aPath = [ [ self path ]
                            stringByAppendingPathComponent:content ];
                anArray = [ anArray arrayByAddingObject:aPath ];
            }
        }
    }
    return anArray;
}

/* description */
- (NSString *)description
{
    return [ [ self allPaths ] componentsJoinedByString:@"\n" ];
}

@end
