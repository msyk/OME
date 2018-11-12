//
//  NSMutableString.m
//  OMEMailViewer
//
//  Created by 新居雅行 on 07/02/11.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

#import "OMECategory.h"

@implementation NSMutableString ( OME )

- (void)linkedHTML
{
    NSError *error;
    NSRegularExpression *regExMail
    = [NSRegularExpression regularExpressionWithPattern:
       @"([a-zA-Z0-9!$&*.=^`|~#%'+\\/?_{}-]+@[a-zA-Z0-9_\\.\\-]+)"
                                                options: 0
                                                  error: &error];
    NSRegularExpression *regExHTTP
    = [NSRegularExpression regularExpressionWithPattern:
       @"s?https?:\\/\\/[\\-_\\.!~\\*'()a-zA-Z0-9;/?:@&=+$,%#]+"
                                                options: 0
                                                  error: &error];

    NSString *replaced = [regExMail stringByReplacingMatchesInString: self
                                                             options: 0
                                                               range: NSMakeRange(0, self.length)
                                                        withTemplate: @"<a href=\"mailto:$0\">$0</a>"];
    replaced = [regExHTTP stringByReplacingMatchesInString: replaced
                                                   options: 0
                                                     range: NSMakeRange(0, replaced.length)
                                              withTemplate: @"<a href=\"$0\">$0</a>"];
    [self setString: replaced];
//    [self replaceOccurrencesOfRegularExpressionString:
//     @"(s?https?://[\\-_\\.!~\\*'()a-zA-Z0-9;/?:@&=+$,%#]+)"
//                                           withString:@"<a href=\"\\0\">\\0</a>"
//                                              options: 0 //NSCaseInsensitiveSearch
//                                                range:NSMakeRange(0, [self length])];
//	[self replaceOccurrencesOfRegularExpressionString: 
//     @"([a-zA-Z0-9!$&*.=^`|~#%'+\\/?_{}-]+@[a-zA-Z0-9_\\.\\-]+)"
//                                           withString:@"<a href=\"mailto:\\0\">\\0</a>"
//                                              options:NSCaseInsensitiveSearch
//                                                range:NSMakeRange(0, [self length])];
}


- (void) convertHTMLSourceWithInsertingTagsForColorComment
{
	NSUInteger i, pos = 0;
	NSUInteger selfLength = [self length];
	if ( selfLength == 0 ) return;
	
	NSString* cssClassNameSeed = @"commentlevel";
	NSMutableString *tempHTML = [NSMutableString stringWithCapacity:100];
	[tempHTML setString:@""];
    
    //	NSArray* eachLines = [self componentsSeparatedByString:@"\n"];
    
	while ( pos < selfLength )	{
		NSRange rangeNewLine = [self rangeOfString: @"\n" options:0 range:NSMakeRange(pos, selfLength-pos)];
		NSString* oneLine;
		if ( rangeNewLine.length == 0 )
			oneLine = [self substringWithRange:NSMakeRange(pos, selfLength-pos)];
		else
			oneLine = [self substringWithRange:NSMakeRange(pos, rangeNewLine.location -pos)];
        
		int commentCount = 0;
		for ( i = 0 ; i < [oneLine length] ; i++ )	{
			unichar c = [oneLine characterAtIndex: i];
			if ( c == '>' )
				commentCount++;
			else if ( c ==' ' )
            /* do nothing */;
			else
				i = [oneLine length];	//for ending this loop.
		}
		NSMutableString *tempHTML2 = [NSMutableString stringWithCapacity:100];
		[tempHTML2 setString:oneLine];
		[tempHTML2 replaceOccurrencesOfString:@"&" withString:@"&amp;" 
                                      options:NSCaseInsensitiveSearch range:NSMakeRange(0, [tempHTML2 length])];
		[tempHTML2 replaceOccurrencesOfString:@"<" withString:@"&lt;" 
                                      options:NSCaseInsensitiveSearch range:NSMakeRange(0, [tempHTML2 length])];
		[tempHTML2 replaceOccurrencesOfString:@">" withString:@"&gt;" 
                                      options:NSCaseInsensitiveSearch range:NSMakeRange(0, [tempHTML2 length])];
		[tempHTML appendAsString:tempHTML2 tag:@"div"
                      attributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                  [cssClassNameSeed stringByAppendingFormat:@"%d", commentCount], @"class", nil]];
        //		[tempHTML appendAsString:@"<br>"];
		if ( rangeNewLine.length == 0 )
			pos = selfLength;
		else
			pos = rangeNewLine.location + rangeNewLine.length;
	}
	[self setString:tempHTML];
}


- (void) convertHTMLSource
{
	[self replaceOccurrencesOfString:@"&" withString:@"&amp;" 
                             options:NSCaseInsensitiveSearch range:NSMakeRange(0, [self length])];
	[self replaceOccurrencesOfString:@"<" withString:@"&lt;" 
                             options:NSCaseInsensitiveSearch range:NSMakeRange(0, [self length])];
	[self replaceOccurrencesOfString:@">" withString:@"&gt;" 
                             options:NSCaseInsensitiveSearch range:NSMakeRange(0, [self length])];
	[self replaceOccurrencesOfString:@"\r\n" withString:@"<br>" 
                             options:NSCaseInsensitiveSearch range:NSMakeRange(0, [self length])];
	[self replaceOccurrencesOfString:@"\n" withString:@"<br>" 
                             options:NSCaseInsensitiveSearch range:NSMakeRange(0, [self length])];
	[self replaceOccurrencesOfString:@"\r" withString:@"<br>" 
                             options:NSCaseInsensitiveSearch range:NSMakeRange(0, [self length])];
}

- (void) appendAsHTMLSource:(NSString *)aString
{
	[self appendAsHTMLSource:aString tag:nil attributes:nil];
}

- (void) appendAsHTMLSource:(NSString *)aString tag:(NSString *)aTag
{
	[self appendAsHTMLSource:aString tag:aTag attributes:nil];
}

- (void) appendAsHTMLSource:(NSString *)aString tag:(NSString *)aTag attributes:(NSDictionary *)Attributes
{
	if ( aString == nil )	aString = @"";
	NSMutableString* converted =[NSMutableString stringWithCapacity: 100];
	[converted setString: aString];
	[converted convertHTMLSource];
	[converted encloseByTag:aTag attributes:Attributes];
	[self appendString:converted];
}

- (void) appendAsString:(NSString *)aString
{
 	[self appendAsString:aString tag:nil attributes:nil];
}

- (void) appendAsString:(NSString *)aString tag:(NSString *)aTag
{
	[self appendAsString:aString tag:aTag attributes:nil];
}

- (void) appendAsString:(NSString *)aString tag:(NSString *)aTag attributes:(NSDictionary *)Attributes
{
	if ( aString == nil )	aString = @"";
	NSMutableString* converted =[NSMutableString stringWithCapacity: 100];
	[converted setString: aString];
	[converted encloseByTag:aTag attributes:Attributes];
	[self appendString:converted];
}

- (void)PreAppendAsHTMLSource:(NSString *)aString;
{
	[self PreAppendAsHTMLSource:aString tag:nil attributes:nil];
}
- (void)PreAppendAsHTMLSource:(NSString *)aString tag:(NSString *)aTag;
{
	[self PreAppendAsHTMLSource:aString tag:aTag attributes:nil];
}
- (void)PreAppendAsHTMLSource:(NSString *)aString tag:(NSString *)aTag attributes:(NSDictionary *)Attributes
{
	if ( aString == nil )	aString = @"";
	NSMutableString* converted =[NSMutableString stringWithCapacity: 100];
	[converted setString:aString];
	[converted convertHTMLSource];
	[converted encloseByTag:aTag attributes:Attributes];
	[self PreAppendString:converted];
}

- (void)PreAppendString:(NSString *)aString
{
	[self insertString:aString atIndex:0];
}


- (void)encloseByTag:(NSString *)aTag
{
	[self encloseByTag:aTag attributes:nil];
}

- (void)encloseByTag:(NSString *)aTag attributes:(NSDictionary *)Attributes
{
	if ( aTag != nil )	{
		NSMutableString* expandAttrs =[NSMutableString stringWithCapacity: 100];
		[expandAttrs setString:@""];
		if ( Attributes != nil )	{
			NSEnumerator* enumerator = [Attributes keyEnumerator];
			id key;
			while (( key = [enumerator nextObject] ))
				[expandAttrs appendFormat:@" %@=\"%@\"", key, [Attributes objectForKey: key]];
		}
		[self insertString:[NSString stringWithFormat:@"<%@%@>",aTag,expandAttrs] atIndex:0];
		[self appendFormat:@"</%@>",aTag];
	}
}
@end
