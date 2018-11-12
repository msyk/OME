//
//  $Id: OMEMail.h,v 1.5 2005/09/03 13:42:13 wakimoto Exp $
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Tue Jan 18 15:33:26 2005 UTC
//  Copyright (c) Shinya Wakimoto, 2005. All rights reserved.
//

#import <Foundation/Foundation.h>

@class WSHeader;

@interface OMEMail : NSObject
{
    /* essencial instances */
    NSArray *upperFields;
    NSArray *messageBody;
    NSArray *middleFields;
    NSArray *lowerFields;
    /* local instances */
    WSHeader *upperHeader;    
    WSHeader *lowerHeader;    
}

/* accessor method */
- (NSArray *)upperFields;
- (NSArray *)messageBody;
- (NSArray *)middleFields;
- (NSArray *)lowerFields;
- (WSHeader *)upperHeader;    
- (WSHeader *)lowerHeader;    

/* designated initializer */
- (id)initWithUpperFields:(NSArray *)uFields
              messageBody:(NSArray *)bodyLines
             middleFields:(NSArray *)mFields
              lowerFields:(NSArray *)lFields;

/* initializer */
- (id)initWithUpperLines:(NSArray *)uLines
             messageBody:(NSArray *)bodyLines
             middleLines:(NSArray *)mLines
              lowerLines:(NSArray *)lLines;

/* initializer */
- (id)initWithLines:(NSArray *)lines;

/* initializer */
- (id)initWithString:(NSString *)aString;

/* initializer */
- (id)initWithContentsOfFile:(NSString *)aPath;

/* header */
- (id)header:(SEL)method;

/* reply comment */
- (NSString *)replyCommentWithFormat:(NSString *)aString;

@end
