//
//  OMEData.m
//  OME
//
//  Created by MURAKAMI, Yukio on 05/06/20.
//  Copyright 2005 Bitz Co., Ltd. All rights reserved.
//

#import "OMEData.h"

@interface OMEDataEnumerator : NSEnumerator
{
	NSData			*_data;
	NSData			*_delimiters;
	char			*_indicator;
	unsigned int	_length;
	unsigned int	_idx;
}

- (id)initWithData:(NSData *)data delimiters:(NSData *)delimiters;
- (NSArray *)allObjects;
- (id)nextObject;
@end

@implementation OMEDataEnumerator

static inline BOOL _isDelimiter(char c, const char *delimiters, const unsigned int delisLen)
{
	unsigned int	tmpIdx = 0;
	while ((tmpIdx < delisLen) && (c != delimiters[tmpIdx])) {
		tmpIdx++;
	}
	return (tmpIdx < delisLen);
}

- (id)initWithData:(NSData *)data delimiters:(NSData *)delimiters
{
	self = [super init];
	if (self) {
		_data = data;
		[_data retain];
		_delimiters = delimiters;
		[_delimiters retain];
		_indicator = (char *)[_data bytes];
		_length = [_data length];
		_idx = 0;
	}
	return self;
}

- (void)dealloc
{
	[_data release];
	[_delimiters release];
	[super dealloc];
}

- (NSArray*)allObjects
{
	NSMutableArray	*array = [NSMutableArray array];
	id				token;

	while ((token = [self nextObject])) {
		[array addObject:token];
	}

	return array;
}

- (id)nextObject
{
	char			*delis = (char *)[_delimiters bytes];
	unsigned int	delisLen = [_delimiters length];
	unsigned int	tmpIdx;
	
	while (_isDelimiter(_indicator[_idx], delis, delisLen)) {
		_idx++;
	}
	tmpIdx = _idx;
	
	if (_length <= tmpIdx) {
		return nil;
	}
	
	while (_idx < _length) {
		if (_isDelimiter(_indicator[_idx], delis, delisLen)) {
			break;
		}
		_idx++;
	}
	return [NSData dataWithBytes:&(_indicator[tmpIdx]) length:(_idx - tmpIdx)];
}

@end

@implementation NSData (OMEData)

- (NSEnumerator*)tokenize:(NSData*)delimiters
{
	return [[[OMEDataEnumerator alloc] initWithData:self delimiters:delimiters] autorelease];
}

@end
