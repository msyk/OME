//
//  $Id: OMEAttachment.m,v 1.7 2005/09/03 13:42:12 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sat Nov 13 14:49:10 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import "OMEAttachment.h"

#import <AppKit/AppKit.h>

@implementation OMEAttachment

/* accessor method */
- (void)setAttributedString:(NSAttributedString *)aAttributedString
{
    [ aAttributedString retain ];
    [ attributedString release ];
    attributedString = aAttributedString;
}

/* accessor method */
- (NSAttributedString *)attributedString
{
    return attributedString;
}

/* designated initializer */
- (id)initWithAttributedString:(NSAttributedString *)aAttributedString
{
    self = [ super init ];
    if ( self ) {
        [ self setAttributedString:aAttributedString ];
    }
    return self;
}

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath
{
    NSAttributedString *aAttributedString;
    NSCharacterSet *whitespaceCharacterSet;
    NSFileManager *fileManager;
    NSFileWrapper *fileWrapper;
    NSString *aString;
    NSTextAttachment *textAttachment;

    if ( aPath ) {
        whitespaceCharacterSet = [ NSCharacterSet
            whitespaceCharacterSet ];
        aString = [ aPath
            stringByTrimmingCharactersInSet:whitespaceCharacterSet ];
        if ( [ aString isEqualToString:@"" ] == NO ) {
            /* default file manager */
            fileManager = [ NSFileManager defaultManager ];
            /* expand tilde */
            aString = [ aString stringByExpandingTildeInPath ];
            /* check existence of file */
            if ( [ fileManager fileExistsAtPath:aString ] ) {
                /* create NSFileWrapper instance */
                fileWrapper = [ [ NSFileWrapper alloc ]
                    initWithPath:aString ];
                /* create NSTextAttachment instance */
                textAttachment = [ [ NSTextAttachment alloc ]
                    initWithFileWrapper:fileWrapper ];
                /* make NSAttributedString by NSTextAttachement */
                aAttributedString = [ NSAttributedString
                    attributedStringWithAttachment:textAttachment ];
                /* release instances */
                [ textAttachment release ];
                [ fileWrapper release ];
            } else {
                aAttributedString = nil;
            }
        } else {
            aAttributedString = nil;
        }
    } else {
        aAttributedString = nil;
    }
    return [ self initWithAttributedString:aAttributedString ];
}

/* dealloc */
- (void)dealloc
{
    [ attributedString release ];
    [ super dealloc ];
}

/* description */
- (NSString *)description
{
    return [ [ self attributedString ] description ];
}

@end
