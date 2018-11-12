//
//  OMEData.h
//  OME
//
//  Created by MURAKAMI, Yukio on 05/06/20.
//  Copyright 2005 Bitz Co., Ltd. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSData (OMEData)

- (NSEnumerator *)tokenize:(NSData *)delimiters;

@end
