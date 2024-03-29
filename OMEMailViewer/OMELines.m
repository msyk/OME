//
//  $Id: OMELines.m,v 1.5 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Mon Jan 10 06:51:55 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "OMELines.h"

#import "OMEJapanese.h"

/* Format */
#define HEAD @" %@"

@implementation OMELines

// convert a string to an array of separated lines 
//     new line characters
//         U+000D (\r or CR)
//         U+2028 (Unicode line separator)
//         U+000A (\n or LF)
//         U+2029 (Unicode paragraph separator)
//         \r\n, in that order (also known as CRLF)
//     *) separated line does not include new line character.
+ (OMELines *)linesWithString:(NSString *)aString
{
    return [ [ self alloc ] initWithString:aString ];
}

// convert a line string to an array of folded lines
//    *) original line does not include new line character.
//    *) each folded line does not include new line character.
+ (OMELines *)linesWithLine:(NSString *)aLine oneLineBytes:(int)oneLineBytes
{
    return [ [ self alloc ]
                 initWithLine:aLine oneLineBytes:oneLineBytes ];
}

// convert array of lines to an array of folded lines
//    *) original line does not include new line character.
//    *) each folded line does not include new line character.
+ (OMELines *)linesWithArray:(NSArray *)anArray
                oneLineBytes:(int)oneLineBytes
          messageCommentHead:(NSString *)messageCommentHead
{
    BOOL fold; 
    unichar *buf;
    NSArray *bArray, *cArray;
    NSEnumerator *aEnumerator;
    NSString *aLine, *bLine, *fullWidthTilde, *messageCommentHead2;

    /* Define Full-Width-Tilde */
    buf = malloc ( 256 );
    buf [ 0 ] = 0xff5e;
    fullWidthTilde = [ NSString stringWithCharacters:buf length:1 ];
    /* messageCommentHead2 */
    messageCommentHead2 = [ NSString
                              stringWithFormat:HEAD, messageCommentHead ];
    /* folding process */
    cArray = [ NSArray array ];
    aEnumerator = [ anArray objectEnumerator ]; 
    while ( ( aLine = [ aEnumerator nextObject ] ) != nil ) {
        /* replace full width tildes with @"~" */
        bArray = [ aLine componentsSeparatedByString:fullWidthTilde ];
        bLine = [ bArray componentsJoinedByString:@"~" ];
        /* check head character of line */
        fold = NO;
        if ( [ bLine hasPrefix:messageCommentHead ] == NO ) {
            if ( [ bLine hasPrefix:messageCommentHead2 ] == NO ) {
                bArray = [ [ OMELines
                               linesWithLine:bLine oneLineBytes:oneLineBytes ]
                             array ];
                fold = YES;
            }
        }
        if ( fold ) {
            cArray = [ cArray arrayByAddingObjectsFromArray:bArray ];
        } else {
            cArray = [ cArray arrayByAddingObject:bLine ];
        }
    }
    free ( buf );
    return  [ [ self alloc ] initWithArray:cArray ];
}

/* accessor method */
- (void)setArray:(NSArray *)anArray
{
    array = anArray;
}

/* accessor method */
- (NSArray *)array
{
    return array;
}

/* designated initalizer */
- (id)initWithArray:(NSArray *)anArray
{
    self = [ super init ];
    if ( self ) {
        [ self setArray:anArray ];
    }
    return self;
}

/* initializer */
// convert a string to an array of separated lines 
//     new line characters
//         U+000D (\r or CR)
//         U+2028 (Unicode line separator)
//         U+000A (\n or LF)
//         U+2029 (Unicode paragraph separator)
//         \r\n, in that order (also known as CRLF)
//     *) separated line does not include new line character.
- (id)initWithString:(NSString *)aString
{
    NSUInteger startIndex, lineEndIndex, contentsEndIndex, length;
    NSRange aRange;
    NSArray *anArray;
    NSString *aLine;

    anArray = [ NSArray array ];
    aRange = NSMakeRange ( 0, 0 );
    [ aString
        getLineStart:(NSUInteger *)&startIndex
        end:(NSUInteger *)&lineEndIndex
        contentsEnd:(NSUInteger *)&contentsEndIndex
        forRange:aRange ];
    while ( startIndex != lineEndIndex ) {
        length = contentsEndIndex - startIndex;
        aRange = NSMakeRange ( startIndex, length );
        aLine = [ aString substringWithRange:aRange ]; 
        anArray = [ anArray arrayByAddingObject:aLine ];
        if ( lineEndIndex == contentsEndIndex ) break;
        aRange = NSMakeRange ( lineEndIndex, 0 );
        [ aString
            getLineStart:(NSUInteger *)&startIndex
            end:(NSUInteger *)&lineEndIndex
            contentsEnd:(NSUInteger *)&contentsEndIndex
            forRange:aRange ];
    }
    return [ self initWithArray:anArray ];
}

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath
{
    NSString *aString;
NSError *error;
    aString = [ NSString stringWithContentsOfFile:aPath  encoding: NSUTF8StringEncoding error:&error /**/];
    return [ self initWithString:aString ];
}

/* initializer */
// convert a line string to an array of folded lines
//    *) original line does not include new line character.
//    *) each folded line does not include new line character.
- (id)initWithLine:(NSString *)aLine oneLineBytes:(int)oneLineBytes
{
    NSUInteger count, i, i0, j, length, width, prevwidth, backcount, w;
    unichar character, prevch, ch;
    NSRange aRange;
    NSArray *anArray;
    NSCharacterSet *halfWidthSet;
    NSCharacterSet *alphanumericSet, *hyphenSet, *parenthesisSet;
    NSString *newLine, *tempString;
    /* NSCharacterSet */
    halfWidthSet = [ NSCharacterSet
                       characterSetWithRange:NSMakeRange ( 0x20, 0xb0 ) ];
    tempString = [ NSString stringWithCString:ALPHANUMERIC_SET  encoding: NSUTF8StringEncoding ];
    alphanumericSet = [ NSCharacterSet
                          characterSetWithCharactersInString:tempString ];
    tempString = [ NSString stringWithCString:HYPHEN_SET  encoding: NSUTF8StringEncoding ];
    hyphenSet = [ NSCharacterSet
                    characterSetWithCharactersInString:tempString ];
    tempString = [ NSString stringWithCString:PARENTHESIS_SET  encoding: NSUTF8StringEncoding ];
    parenthesisSet = [ NSCharacterSet
                         characterSetWithCharactersInString:tempString ];
    /* folding process */ 
    count = 0;
    i0 = 0;
    prevwidth = 0;
    prevch = 0;
    length = [ aLine length ];
    anArray = [ NSArray array ];
    for ( i = 0; i < length; i++ ) {
        character = [ aLine characterAtIndex:i ];
        if ( [ halfWidthSet characterIsMember:character ] )
            width = 1;
        else
            width = 2;
        count += width;
        if ( count > oneLineBytes ) {
            if ( [ alphanumericSet characterIsMember:character ] ) {
                backcount = 0;
                for ( j = i - 1; j > i0; j-- ) {
                    ch = [ aLine characterAtIndex:j ];
                    if ( ! [ alphanumericSet characterIsMember:ch ] ) 
                        break;
                    if ( [ halfWidthSet characterIsMember:ch ] )
                        w = 1;
                    else
                        w = 2;
                    backcount += w;
                }
                if ( j > i0 ) {
                    aRange = NSMakeRange ( i0, j - i0 + 1 );
                    count = width + backcount;
                    i0 = j + 1;
                } else {
                    aRange = NSMakeRange ( i0, i - i0 );
                    count = width;
                    i0 = i;
                }
            } else {
                /* Japanese Hyphenation */
                if ( [ parenthesisSet characterIsMember:prevch ] ||
                     [ hyphenSet characterIsMember:character ] )
                {
                    aRange = NSMakeRange ( i0, i - i0 - 1 );
                    count = width + prevwidth;
                    i0 = i - 1;
                } else {
                    aRange = NSMakeRange ( i0, i - i0 );
                    count = width;
                    i0 = i;
                }
            }
            newLine = [ aLine substringWithRange:aRange ];
            anArray = [ anArray arrayByAddingObject:newLine ];
        }
        prevch = character;
        prevwidth = width;
    }
    if ( i != i0 ) {
        aRange = NSMakeRange ( i0, i - i0 );
        newLine = [ aLine substringWithRange:aRange ];
        anArray = [ anArray arrayByAddingObject:newLine ];
    }
    return [ self initWithArray:anArray ];
}

/* dealloc */

/* description */
- (NSString *)description
{
    NSString *aString;

    aString = [ [ self array ] componentsJoinedByString:@"\n" ];
    aString = [ aString stringByAppendingString:@"\n" ];
    return aString;
}

/* count */
- (unsigned)count
{
    return (unsigned int)[ [ self array ] count ];
}

/* object */
- (id)objectAtIndex:(unsigned int)index
{
    return [ [ self array ] objectAtIndex:index ];
}

@end
