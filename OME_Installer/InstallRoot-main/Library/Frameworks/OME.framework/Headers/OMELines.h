//
//  $Id: OMELines.h,v 1.3 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Mon Jan 10 06:51:55 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface OMELines : NSObject
{
    NSArray *array;
}

// convert a string to an array of separated lines 
//     new line characters
//         U+000D (\r or CR)
//         U+2028 (Unicode line separator)
//         U+000A (\n or LF)
//         U+2029 (Unicode paragraph separator)
//         \r\n, in that order (also known as CRLF)
//     *) separated line does not include new line character.
+ (OMELines *)linesWithString:(NSString *)aString;

// convert a line string to an array of folded lines
//    *) original line does not include new line character.
//    *) each folded line does not include new line character.
+ (OMELines *)linesWithLine:(NSString *)aLine oneLineBytes:(int)oneLineBytes;

// convert array of lines to an array of folded lines
//    *) original line does not include new line character.
//    *) each folded line does not include new line character.
+ (OMELines *)linesWithArray:(NSArray *)anArray
                oneLineBytes:(int)oneLineBytes
          messageCommentHead:(NSString *)messageCommentHead;

/* accessor method */
- (NSArray *)array;

/* designated initalizer */
- (id)initWithArray:(NSArray *)anArray;

/* initializer */
// convert a string to an array of separated lines 
//     new line characters
//         U+000D (\r or CR)
//         U+2028 (Unicode line separator)
//         U+000A (\n or LF)
//         U+2029 (Unicode paragraph separator)
//         \r\n, in that order (also known as CRLF)
//     *) separated line does not include new line character.
- (id)initWithString:(NSString *)aString;

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath;

/* initializer */
// convert a line string to an array of folded lines
//    *) original line does not include new line character.
//    *) each folded line does not include new line character.
- (id)initWithLine:(NSString *)aLine oneLineBytes:(int)oneLineBytes;

/* description */
- (NSString *)description;

/* count */
- (unsigned)count;

/* object */
- (id)objectAtIndex:(unsigned int)index;

@end
