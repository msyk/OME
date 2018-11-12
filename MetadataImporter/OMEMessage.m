//
//  OMEMessage.m
//  OME
//
//  Created by MURAKAMI, Yukio on 05/06/10.
//  Copyright 2005 Bitz Co., Ltd. All rights reserved.
//
//  Change History:
//
//  2005/09/11  msyk added
//              - (id)initWithData:
//              + (NSStringEncoding)analizeCharacterSet: message:
//              - (NSDictionary*)fields
//              - (NSString*)text
//  2005/09/19  merge
//

#import <OME/OME.h>
//#import <OgreKit/OgreKit.h>

@implementation OMEMessage

BOOL is_folding(const char *line)
{
	if (line == NULL)	return NO;
	if ((line[0] == '\x20') || (line[0] == '\t'))	return YES;
	return NO;
}

typedef enum _CODE_TYPE {
	CT_NEW			= 1,
	CT_OLD			= 2,
	CT_NEC			= 3,
	CT_EUC			= 4,
	CT_SJIS			= 5,
	CT_EUCORSJIS	= 6,
	CT_ASCII		= 7
} CODE_TYPE;

+ (NSString *) getNextLineString:(NSString *)str
{
	NSRange crRange = [str rangeOfString: @"\r"];
	NSRange lfRange = [str rangeOfString: @"\n"];
	if ( (crRange.location + 1) == lfRange.location )
		return @"\r\n";
	else if ( crRange.length == 0 && lfRange.length == 1 )
		return @"\n";
	else if ( crRange.length == 1 && lfRange.length == 0 )
		return @"\r";
	else
		return @"\n";	
}

/* CJKV日中韓越情報処理「9.5.1 日本語符号化方式の検出」 */
CODE_TYPE detect_code_type(const unsigned char *bytes, unsigned int length)
{
	int	index = 0;
	int	whatcode = CT_ASCII;
	while ((whatcode == CT_EUCORSJIS || whatcode == CT_ASCII) && index < length) {
		if ((index++) < length) {
			if (bytes[index] == '\x1b') {		/* ESC:ISO-2022-JP ? */
				index++;
				if (bytes[index] == '$') {		/* 2byte escape sequence ? */
					index++;
					if (bytes[index] == 'B')
						whatcode = CT_NEW;		/* JIS X 0208-1983 */
					else if (bytes[index] == '@')
						whatcode = CT_OLD;		/* JIS C 6226-1978 */
				}
				else if (bytes[index] == 'K')
					whatcode = CT_NEC;			/* NEC Nipponese */
			}
			else if ((bytes[index] >= 0x81 && bytes[index] <= 0x8D)
					|| (bytes[index] >= 0x8F && bytes[index] <= 0x9F))
				whatcode = CT_SJIS;
			else if (bytes[index] == 0x8E) {	/* SS2:EUC-JO hankaku kantakana ? */
				index++;
				if ((bytes[index] >= 0x40 && bytes[index] <= 0x7E)
						|| (bytes[index] >= 0x80 && bytes[index] <= 0xA0)
						|| (bytes[index] >= 0xE0 && bytes[index] <= 0xFC))
					whatcode = CT_SJIS;			/* Shift_JIS */
				else if (bytes[index] >= 0xA1 && bytes[index] <= 0xDF)
					whatcode = CT_EUCORSJIS;	/* unknown:Shift_JIS of EUC-JP */
			}
			else if (bytes[index] >= 0xA1 && bytes[index] <= 0xDF) {
				index++;
				if (bytes[index] >= 0xF0 && bytes[index] <= 0xFE)
					whatcode = CT_EUC;			/* EUC-JP */
				else if (bytes[index] >= 0xA1 && bytes[index] <= 0xDF)
					whatcode = CT_EUCORSJIS;	/* unknown:Shift_JIS of EUC-JP */
				else if (bytes[index] >= 0xE0 && bytes[index] <= 0xEF) {
					whatcode = CT_EUCORSJIS;	/* unknown:Shift_JIS of EUC-JP */
					while (bytes[index] >= 0x40 && index < length
							&& whatcode == CT_EUCORSJIS) {
						if (bytes[index] >= 0x81) {
							if (bytes[index] <= 0x8D
									|| (bytes[index] >= 0x8F && bytes[index] <= 0x9F))
								whatcode = CT_SJIS;		/* Shift_JIS */
							else if (bytes[index] >= 0xFD && bytes[index] <= 0xFE)
								whatcode = CT_EUC;		/* EUC-JP */
						}
						index++;
					}
				}
				else if (bytes[index] <= 0x9F)
					whatcode = CT_SJIS;			/* Shift_JIS */
			}
			else if (bytes[index] >= 0xF0 && bytes[index] <= 0xFE)
				whatcode = CT_EUC;				/* EUC-JP */
			else if (bytes[index] >= 0xE0 && bytes[index] <= 0xFE) {
				index++;
				if ((bytes[index] >= 0x40 && bytes[index] <= 0x7E)
						|| (bytes[index] >= 0x80 && bytes[index] <= 0xA0))
					whatcode = CT_SJIS;			/* Shift_JIS */
				else if (bytes[index] >= 0xFD && bytes[index] <= 0xFE)
					whatcode = CT_EUC;			/* EUC-JP */
				else if (bytes[index] >= 0xA1 && bytes[index] <= 0xFC)
					whatcode = CT_EUCORSJIS;	/* unknown:Shift_JIS of EUC-JP */
			}
		}
	}
	return whatcode;
}

+ (NSStringEncoding)analizeCharacterSet:(NSString *)charset message:(NSData *)messageData
{
	NSStringEncoding strEnc;
	if (charset != nil) {
		CFStringEncoding cfCharset = CFStringConvertIANACharSetNameToEncoding((CFStringRef)charset);
		strEnc = CFStringConvertEncodingToNSStringEncoding(cfCharset);
/*
		if (([charset caseInsensitiveCompare:@"US-ASCII"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"ASCII"] == NSOrderedSame)) {
			strEnc = NSASCIIStringEncoding;
		}
		else if (([charset caseInsensitiveCompare:@"JIS"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"ISO-2022-JP"] == NSOrderedSame)) {
			strEnc = NSISO2022JPStringEncoding;
		}
		else if (([charset caseInsensitiveCompare:@"8859_1"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"iso-ir-100"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"iso_8859-1"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"iso-8859-1"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"iso8859-1"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"latin1"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"l1"] == NSOrderedSame)) {
			strEnc = NSISOLatin1StringEncoding;
		}
		else if (([charset caseInsensitiveCompare:@"8859_2"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"iso-ir-101"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"iso_8859-2"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"iso-8859-2"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"iso8859-2"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"latin2"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"l2"] == NSOrderedSame)) {
			strEnc = NSISOLatin2StringEncoding;
		}
		else if ([charset caseInsensitiveCompare:@"EUC_JP"] == NSOrderedSame) {
			strEnc = NSJapaneseEUCStringEncoding;
		}
		else if (([charset caseInsensitiveCompare:@"SJIS"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"Shift_JIS"] == NSOrderedSame)) {
			strEnc = NSShiftJISStringEncoding;
		}
		else if (([charset caseInsensitiveCompare:@"UTF-8"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"UTF8"] == NSOrderedSame)) {
			strEnc = NSUTF8StringEncoding;
		}
		else if (([charset caseInsensitiveCompare:@"UNICODE"] == NSOrderedSame)
				|| ([charset caseInsensitiveCompare:@"UCS2"] == NSOrderedSame)) {
			strEnc = NSUnicodeStringEncoding;
		}
*/
	}
	else {
		switch (detect_code_type((const unsigned char *)[messageData bytes], [messageData length])) {
			case CT_EUC:
				strEnc = NSJapaneseEUCStringEncoding;
				break;
			case CT_SJIS:
				strEnc = NSShiftJISStringEncoding;
				break;
			case CT_ASCII:
				strEnc = NSASCIIStringEncoding;
				break;
			default:
				break;
		}
	}
	return strEnc;
}

- (void)dumpOMEMessage
{
	if (fields != nil) {
		NSEnumerator	*enumerator = [fields keyEnumerator];
		id				key;
		while ((key = [enumerator nextObject])) {
			NSLog(@"%@:%@", key, [fields objectForKey:key]);
		}
	}
	if (realMailHeaders != nil) {
		NSEnumerator	*enumerator = [realMailHeaders keyEnumerator];
		id				key;
		while ((key = [enumerator nextObject])) {
			NSLog(@"%@:%@", key, [realMailHeaders objectForKey:key]);
		}
	}
}

/*!
    @method     
    @abstract   クラスのインスタンス変数のうちrealMailHeadersを構築する

	@discussion (comprehensive description)
*/
- (void)anaData:(NSData *)data
{
	const char		REAL_MAIL_HEADERS[] = "<!-- Real Mail Headers -->";
	const char		LINE_DELIS[] = "\r\n";
	NSString		*FIELD_DELIS = @":";
	typedef enum _MSGSTATE {
		MSGSTATE_MESSAGE_FIELDS, MSGSTATE_MESSAGE_TEXT, MSGSTATE_REAL_MAIL_HEADERS
	} MSGSTATE;
	MSGSTATE		mstate = MSGSTATE_MESSAGE_FIELDS;
//	char			*fieldNamePtr = NULL;
//	unsigned int	fieldNameLength = 0;
//	char			*fieldBodyPtr = NULL;
//	unsigned int	fieldBodyLength = 0;
	NSEnumerator	*enumerator = [data tokenize:[NSData dataWithBytes:&LINE_DELIS length:strlen(LINE_DELIS)]];
	id				token;
	NSCharacterSet	*chSetField = [NSCharacterSet characterSetWithCharactersInString:FIELD_DELIS];
	NSCharacterSet	*chSetLine = [NSCharacterSet characterSetWithCharactersInString:@"\r\n"];

	mstate = MSGSTATE_MESSAGE_FIELDS;
/*	fieldNamePtr = fieldBodyPtr = NULL;*/
/*	fieldNameLength = fieldBodyLength = 0;*/
	
	NSString *beforeFieldName = NULL;
	
	while ((token = [enumerator nextObject])) {
		if (mstate != MSGSTATE_REAL_MAIL_HEADERS) {
			if (memcmp([token bytes], REAL_MAIL_HEADERS, strlen(REAL_MAIL_HEADERS)) == 0) {
				mstate = MSGSTATE_REAL_MAIL_HEADERS;
			}
		}
		else {
			NSData *tokenData = [NSData dataWithBytes:[token bytes] length:[token length]];
			NSString *thisHeaderLine = [[NSString alloc]initWithBytes:[token bytes] 
														length:[token length] 
														encoding:NSASCIIStringEncoding];
			[realHeadersString appendFormat:@"%@\n", thisHeaderLine ];
			
			NSString *field = [[[NSString alloc]initWithData:tokenData encoding:NSASCIIStringEncoding]autorelease];
//			NSString *field = [NSString stringWithCString:[token bytes] length:[token length]];
			if (is_folding([token bytes]) == NO) {
				NSString		*fieldName;
				NSString		*fieldBody;
				NSScanner		*scanner = [NSScanner scannerWithString:field];
				if ([scanner scanUpToCharactersFromSet:chSetField intoString:&fieldName]) {
				
					beforeFieldName = [NSString stringWithString:fieldName];
					
					[scanner scanCharactersFromSet:chSetField intoString:nil];
					if (![scanner scanUpToCharactersFromSet:chSetLine intoString:&fieldBody]) {
						fieldBody = [NSString string];
					}
					if ([realMailHeaders objectForKey:fieldName] == nil) {
						[realMailHeaders setObject:fieldBody forKey:fieldName];
					}
					else {
						NSMutableString	*fb = [[realMailHeaders objectForKey:fieldName] mutableCopy];
						[fb appendString:@"\n"];
						[fb appendString:fieldBody];
						[realMailHeaders setObject:fb forKey:fieldName];
					}
				}
			}
			else {
//				if ((fieldNamePtr != NULL) && (0< fieldNameLength)) {
				if ( beforeFieldName != NULL )	{
//					NSString	*fieldName = [NSString stringWithCString:fieldNamePtr length:fieldNameLength];
					NSMutableString	*fieldBody = [[realMailHeaders objectForKey:beforeFieldName] mutableCopy];
					[fieldBody appendString:field];
//					[fieldBody appendString:[NSString stringWithCString:[token bytes] length:[token length]]];
					[realMailHeaders setObject:fieldBody forKey:beforeFieldName];
				}
			}
		}
	}
}

- (void)anaMessage:(NSString *)msg
{
	NSString		*REAL_MAIL_HEADERS = @"<!-- Real Mail Headers -->";
	NSString		*cr = @"r";
	NSString		*lf = @"\n";
	NSString		*crlf = @"\r\n";
	typedef enum _MSGSTATE {
		MSGSTATE_MESSAGE_FIELDS, MSGSTATE_MESSAGE_TEXT, MSGSTATE_REAL_MAIL_HEADERS
	} MSGSTATE;
	NSString		*parsedString;
	NSRange			range, subrange;
	unsigned int	length;
	MSGSTATE		mstate = MSGSTATE_MESSAGE_FIELDS;
	NSCharacterSet	*chSetFieldDelimiter = [NSCharacterSet characterSetWithCharactersInString:@":"];
	NSCharacterSet	*chSetLineDelimiters = [NSCharacterSet characterSetWithCharactersInString:@"\r\n"];
	BOOL			isFirstLineOfBody = YES;

	length = [msg length];
	range = NSMakeRange(0, length);
	while (0 < range.length) {
		subrange = [msg lineRangeForRange:NSMakeRange(range.location, 0)];
		parsedString = [msg substringWithRange:subrange];
		if (MSGSTATE_MESSAGE_FIELDS == mstate) {
			[message appendString:parsedString];
			if (([parsedString compare:cr] == NSOrderedSame)
					|| ([parsedString compare:lf] == NSOrderedSame)
					|| ([parsedString compare:crlf] == NSOrderedSame)) {
				mstate = MSGSTATE_MESSAGE_TEXT;
			}
			else {
				NSString		*fieldName;
				NSString		*fieldBody;
				NSScanner		*scanner = [NSScanner scannerWithString:parsedString];
				if ([scanner scanUpToCharactersFromSet:chSetFieldDelimiter intoString:&fieldName]) {
					[scanner scanCharactersFromSet:chSetFieldDelimiter intoString:nil];
					if ([scanner scanUpToCharactersFromSet:chSetLineDelimiters intoString:&fieldBody]) {
						[fields setObject:fieldBody forKey:fieldName];
					}
				}
			}
		}
		else if (MSGSTATE_MESSAGE_TEXT == mstate) {
			if (	! isFirstLineOfBody ||
					[ parsedString compare:@"-->"  options:NSCaseInsensitiveSearch range:NSMakeRange(0, 3) ] != NSOrderedSame )	{
				NSRange	r = NSMakeRange(0, [REAL_MAIL_HEADERS length]);
				if ([ parsedString compare:REAL_MAIL_HEADERS options:NSCaseInsensitiveSearch range:r] == NSOrderedSame) {
					mstate = MSGSTATE_REAL_MAIL_HEADERS;
				}
				else {
					[message appendString:parsedString];
					[text appendString:parsedString];
				}
				isFirstLineOfBody = NO;
			}
		}
		else {	/* MSGSTATE_REAL_MAIL_HEADERS */
		}
		range.location = NSMaxRange(subrange);
		range.length -= subrange.length;
	}
}

/* 
//	Incorporating to initWithContentsOfFile method
- (void)readOMEMessage:(NSString *)pathToFile
{
	NSString	*X_OME_CHARSET = @"X-OME-CharSet";
	NSData	*data = [NSData dataWithContentsOfFile:(NSString *)pathToFile];
	if (data != nil) {
		NSArray *omeFilePathComp = [pathToFile pathComponents];
		NSMutableArray *omeFileParentPathComp = [NSMutableArray arrayWithArray:omeFilePathComp];
		[omeFileParentPathComp removeLastObject];
		NSString *omeFileParent = [omeFilePathComp componentsJoinedByString:@"/"];
		if ( [[omeFileParent pathExtension] caseInsensitiveCompare: @"mpart"] == NSOrderedSame )	{
			isMultiPart = YES;
			NSString *partInfoFile = [omeFileParent stringByAppendingString: @"/__OME_PartsInfo.xml"];
			NSError *err = nil;
			if ( [ [NSFileManager defaultManager] fileExistsAtPath: partInfoFile] )	{
				NSXMLDocument *partInfoXML = [[NSXMLDocument alloc] initWithContentsOfURL:[NSURL fileURLWithPath:partInfoFile]
										options:(NSXMLNodePreserveWhitespace|NSXMLNodePreserveCDATA)
										error: &err];
			}
			else	{
				partInfo = nil;
			}
		}
		[self anaData:data];
		NSString	*charset = [realMailHeaders objectForKey:X_OME_CHARSET];
		strEnc = [OMEMessage analizeCharacterSet:charset message:data];
		NSString	*msg = [[NSString alloc] initWithData:data encoding:strEnc];
		if (msg != nil) {
			[self anaMessage:msg];
		}
		[msg release];
	}
}

*/

- (id)init
{
	[super init];
	
	if ( self == nil )
		return nil;

	fields = nil;
	text = nil;
	realMailHeaders = nil;
	strEnc = NSShiftJISStringEncoding;
	isMultiPart = NO;
	partInfo = nil;
	targetFilePath = nil;
	parentPath = nil;
	hasCertificate = NO;
	partInfoVersion = 0;

	return self;
}

- (BOOL)isMultiPartMail
{
	return isMultiPart;
}
/*!
    @method     
    @abstract   (brief description)
    @discussion (comprehensive description)
*/
- (id)initWithContentsOfFile:(NSString *)pathToFile
{
/*	fields = nil;
	text = nil;
	realMailHeaders = nil;
	strEnc = NSShiftJISStringEncoding;
	isMultiPart = NO;
	partInfo = nil;
*/
	[self init];
	if (self != nil) {
		//クラスの管理に必要なオブジェクトのインスタンスを取得
		message = [[NSMutableString alloc] initWithCapacity:BUFSIZ];
		fields = [[NSMutableDictionary alloc] initWithCapacity:BUFSIZ];
		text = [[NSMutableString alloc] initWithCapacity:BUFSIZ];
		realMailHeaders = [[NSMutableDictionary alloc] initWithCapacity:BUFSIZ];
		realHeadersString = [[NSMutableString alloc] initWithCapacity:BUFSIZ];
		
		if ( [[pathToFile pathExtension] caseInsensitiveCompare: @"mpart"] != NSOrderedSame )	{
			targetFilePath = [[NSString stringWithString:pathToFile] retain];
			parentPath = [[pathToFile stringByDeletingLastPathComponent] retain];
		}
		else	{
			parentPath = [[NSString stringWithString:pathToFile] retain];
			NSString *nameInPath = [[parentPath lastPathComponent]lastPathComponent];
			targetFilePath = [parentPath stringByAppendingPathComponent:nameInPath];
			
			NSFileManager *fm = [NSFileManager defaultManager];
			NSString *fileInFolder;
			NSArray *extensionArray = [NSArray arrayWithObjects: @"ygm", @"mail", @"rply", @"html", @"htm", nil];
/*			for (NSString *oneExtension in extensionArray) {	*/
			int countOfArray = [ extensionArray count ];
			int i;
			for ( i = 0 ; i < countOfArray ; i++ )	{
				NSString *oneExtension = [ extensionArray objectAtIndex: i ];
				fileInFolder = [ targetFilePath stringByAppendingPathExtension: oneExtension ];
				if ( [ fm fileExistsAtPath: fileInFolder] )	{	
					targetFilePath = [fileInFolder retain];
					break;
				}
			}
		}

		NSString	*X_OME_CHARSET = @"X-OME-CharSet";
//		NSString	*REAL_MAIL_HEADERS = @"<!-- Real Mail Headers -->";

		if ( [[parentPath pathExtension] caseInsensitiveCompare: @"mpart"] != NSOrderedSame )	{
			//シングルパートファイルの場合
			NSData	*data = [NSData dataWithContentsOfFile:(NSString *)pathToFile];	//メールファイルを読み込み
			if (data != nil) {
				[self anaData:data];
				NSString	*charset = [realMailHeaders objectForKey:X_OME_CHARSET];
				strEnc = [OMEMessage analizeCharacterSet:charset message:data];
				NSString	*msg = [[NSString alloc] initWithData:data encoding:strEnc];
				
				// MacRomanと認識しつつも英語のメールなのに変なことがあった。メッセージがない場合は無理矢理適当なエンコードで読むしかない
				if (msg == nil)
					msg = [[NSString alloc] initWithData: data encoding: NSUTF8StringEncoding];
				
				if (msg != nil)
					[self anaMessage:msg];
				[msg release];
			}
		}
		else	{
			//マルチパートファイルの場合
			isMultiPart = YES;	//フラグを設定

			partInfo = nil;
			NSString *partInfoFile = [parentPath stringByAppendingString: @"/__OME_PartsInfo.xml"];
			if ( [ [NSFileManager defaultManager] fileExistsAtPath: partInfoFile] )
				partInfo = [self incorporateFromPartInfoFile];	//パート情報ファイルが存在する場合
			if ( partInfo == nil )	//パート情報ファイルが存在しない場合、あるいはあっても問題がある場合
				partInfo = [self incorporateFromExistingFiles];
		}
	}
//	NSLog( [partInfo description] );
	
	return self;
}

- (id)initWithContentsOfURL:(NSURL *)url
{
	return [self initWithContentsOfFile:[url path]];
}

- (NSArray *)incorporateFromExistingFiles
{
	NSError *err = nil;
	NSMutableArray *partInfoArray = [NSMutableArray arrayWithCapacity:1];
	NSString *charset;
	NSString *targetFileName;
	
	NSDirectoryEnumerator *dirEnum = [[NSFileManager defaultManager] enumeratorAtPath:parentPath];
	while ( targetFileName = [dirEnum nextObject] )	{
		NSString *currentFilePath = [[parentPath stringByAppendingString:@"/"]stringByAppendingString:targetFileName];
		NSString *currentFileExtension = [targetFileName pathExtension];
		BOOL rule1 = ([targetFileName length] >= 1) && ([targetFileName characterAtIndex:1] == '_');
		BOOL rule2 = ([targetFileName length] >= 2) && ([targetFileName characterAtIndex:1] == '_');
		BOOL rule3 = ( [currentFileExtension caseInsensitiveCompare:@"mail"] == NSOrderedSame )
					|| ( [currentFileExtension caseInsensitiveCompare:@"ygm"] == NSOrderedSame )
					|| ( [currentFileExtension caseInsensitiveCompare:@"rply"] == NSOrderedSame )
					|| ( [currentFileExtension caseInsensitiveCompare:@"html"] == NSOrderedSame )
					|| ( [currentFileExtension caseInsensitiveCompare:@"htm"] == NSOrderedSame )
					|| ( [currentFileExtension caseInsensitiveCompare:@"xml"] == NSOrderedSame )
					|| ( [currentFileExtension caseInsensitiveCompare:@"xsl"] == NSOrderedSame )
					|| ( [currentFileExtension caseInsensitiveCompare:@"txt"] == NSOrderedSame );
		if( !(rule1 && rule2) )	{
			NSMutableDictionary *currentInfo = [[NSMutableDictionary dictionaryWithCapacity:1]retain];
			[partInfoArray addObject:currentInfo];
				
			[currentInfo setObject:targetFileName forKey:@"file-name"];
			[currentInfo setObject:[self preferedMIMETypeFromFile:currentFilePath] forKey:@"content-type"];
	
			if ( rule3 )	{			//テキストファイルなら内容をチェック
				NSStringEncoding nsCharset;
				NSString *fileInfoFilePath = [[parentPath stringByAppendingString:@"__"] 
								stringByAppendingString:targetFileName];
				if ( [[NSFileManager defaultManager] fileExistsAtPath: fileInfoFilePath] )	{
					NSString *fileInfoFileContents = [NSString
						stringWithContentsOfFile: fileInfoFilePath
						encoding: NSASCIIStringEncoding 
						error:&err];
					int lengthOfFileInfoFileContents = [fileInfoFileContents length];
					NSRange headerPosition = [fileInfoFileContents rangeOfString:@"X-OME-CharSet"];
					if ( headerPosition.length != 0 )	{
						NSRange nextEndOfLine = [fileInfoFileContents rangeOfString:@"\n" options: 0 
							range:NSMakeRange(headerPosition.location, lengthOfFileInfoFileContents-headerPosition.location)];
						int headerEndPosition = -1;
						if ( nextEndOfLine.length == 0 )
							headerEndPosition = lengthOfFileInfoFileContents;
						else
							headerEndPosition = nextEndOfLine.location;
						int headerValuePosition = headerPosition.location + 15;
						NSRange charsetRange = NSMakeRange(headerValuePosition, headerEndPosition - headerValuePosition);
						charset = [fileInfoFileContents substringWithRange: charsetRange];
					}
					else	{
						nsCharset = [self predictTheCharacterSet:currentFilePath];
					}
				}
				else	{
					nsCharset = [self predictTheCharacterSet:currentFilePath];
				}
				NSString *fileTopHeaderString = @"", *realHeaderString = @"", *realPartHeaderString = @"";
				NSString *fileContents = [self fileContents: &targetFileName encoding: nsCharset
												topHeaders: &fileTopHeaderString 
												realHeaders: &realHeaderString 
												partHeaders: &realPartHeaderString];
				if ( fileContents != nil )	{
					[currentInfo setObject:fileContents forKey:@"contents"];
				//	[currentInfo setObject:????? forKey:@"characterset"];
					NSArray *eachRealHeaders;	
					NSEnumerator *enumRealHeaders;
					NSString *oneHeaderLine;
					NSString *currentHeaderLavel;
					NSMutableString *currentHeaderValue;
					NSString *newLineString = [ OMEMessage getNextLineString: fileContents ];
					if ( fileTopHeaderString != nil )	{	//ファイルの最初のヘッダがある場合
						eachRealHeaders = [fileTopHeaderString componentsSeparatedByString:newLineString];	
						enumRealHeaders = [eachRealHeaders objectEnumerator];
						currentHeaderLavel = @"";
						currentHeaderValue = [NSMutableString stringWithCapacity:256];
						while ( oneHeaderLine = [enumRealHeaders nextObject] )	{
							if( [currentHeaderLavel length] != 0 )
								[fields setObject:currentHeaderValue forKey:currentHeaderLavel];
							NSRange colonPos = [oneHeaderLine rangeOfString:@":"];
							if ( colonPos.length > 0 )	{
								currentHeaderLavel = [oneHeaderLine substringToIndex:colonPos.location];
								currentHeaderValue = [NSMutableString stringWithCapacity:256];
								[currentHeaderValue appendString:
									[oneHeaderLine substringFromIndex:colonPos.location+1]];
							}
						}
						if( [currentHeaderLavel length] != 0 )
							[fields setObject:currentHeaderValue forKey:currentHeaderLavel];
					}
					if ( realHeaderString != nil )	{	//実ヘッダがある場合
						//実ヘッダの取り出し
						eachRealHeaders = [realHeaderString componentsSeparatedByString:newLineString];	
						enumRealHeaders = [eachRealHeaders objectEnumerator];
						currentHeaderLavel = @"";
						currentHeaderValue = [NSMutableString stringWithCapacity:256];
						while ( oneHeaderLine = [enumRealHeaders nextObject] )	{
							if ( [oneHeaderLine length] > 0 )	{
								[realHeadersString appendFormat:@"%@\n", oneHeaderLine ];

								unichar firstChar = [ oneHeaderLine characterAtIndex:0 ];
								if ( firstChar == 0x0020 || firstChar == 0x0008 )	{
									// folding header line
									[currentHeaderValue appendString:newLineString];
									[currentHeaderValue appendString:oneHeaderLine];
								}
								else	{	// it has header label string.
									if( [currentHeaderLavel length] != 0 )
										[realMailHeaders setObject:currentHeaderValue forKey:currentHeaderLavel];
									NSRange colonPos = [oneHeaderLine rangeOfString:@":"];
									if ( colonPos.length > 0 )	{
										currentHeaderLavel = [oneHeaderLine substringToIndex:colonPos.location];
										currentHeaderValue = [NSMutableString stringWithCapacity:256];
										[currentHeaderValue appendString:
											[oneHeaderLine substringFromIndex:colonPos.location+1]];
									}
								}
							}
						}
						if( [currentHeaderLavel length] != 0 )
							[realMailHeaders setObject:currentHeaderValue forKey:currentHeaderLavel];
					}
				}
			}
		}
	}

	return partInfoArray;
}


- (NSString *)preferedMIMETypeFromFile:(NSString *)targetFile
{
/*	FSRef ref;
	FSPathMakeRef ((unsigned char*)[targetFile fileSystemRepresentation], &ref, NULL);
	
	CFDictionaryRef values = NULL;
	CFStringRef attrs[1] = { kLSItemContentType };
	CFStringRef prefferdMIMEType = NULL;
	CFArrayRef attrNames = CFArrayCreate(NULL, (const void **)attrs, 1, NULL);
	if ( LSCopyItemAttributes(&ref, kLSRolesViewer, attrNames, &values) == noErr )	{
		CFTypeRef uti = CFDictionaryGetValue(values, kLSItemContentType);
		prefferdMIMEType = UTTypeCopyPreferredTagWithClass( uti, kUTTagClassMIMEType );
	}
*/
	NSString *currentFileExtension = [targetFile pathExtension];
	CFStringRef uti = UTTypeCreatePreferredIdentifierForTag(
                                                            kUTTagClassFilenameExtension, (__bridge CFStringRef)currentFileExtension, NULL);
	CFStringRef prefferdMIMEType = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType);
	if ( prefferdMIMEType == nil )	{
		NSString *currentFileExtension = [targetFile pathExtension];
		if ( ( [currentFileExtension caseInsensitiveCompare:@"ygm"] == NSOrderedSame )
			|| ( [currentFileExtension caseInsensitiveCompare:@"mail"] == NSOrderedSame )
			|| ( [currentFileExtension caseInsensitiveCompare:@"wmail"] == NSOrderedSame )
			|| ( [currentFileExtension caseInsensitiveCompare:@"rply"] == NSOrderedSame ) )
			return @"text/plain";
		else
			return @"application/octet-stream";
	}
    return (__bridge NSString *)prefferdMIMEType;
}

/*!
    @method     predictTheCharacterSet
    @abstract   指定したファイルのテキストエンコーディングを推測する。
    @discussion いろいろなエンコーディングで実際にファイルを読んでみる。
*/
- (NSStringEncoding)predictTheCharacterSet:(NSString *)filePath
{
	NSData *targetData = [NSData dataWithContentsOfFile:filePath];
	int lengthOriginal = [targetData length];

	NSCharacterSet *irCSet = [NSCharacterSet illegalCharacterSet];
	NSArray *charsetSet = [NSArray arrayWithObjects: 
		@"UTF-8", @"UTF-16", @"Shift_JIS", @"ISO-2022-JP", @"EUC-JP", @"JIS_X0201", @"CP932",
		@"EUC-JP", @"ISO-8859-1", @"US-ASCII", @"ISO-8859-2", @"ISO-8859-3", @"ISO-8859-4", @"ISO-8859-5",
		@"ISO-8859-6", @"ISO-8859-7", @"ISO-8859-8", @"ISO-8859-9", @"ISO-8859-10", @"Big5", @"GB2312", @"EUC-KR",
		nil];
		
	int score = 0;
	NSString *chosenEncoding;
	NSStringEncoding candidateEncoding = 0;
	
	for( NSString *currentCharset in charsetSet )	{
		CFStringEncoding cfCharset = CFStringConvertIANACharSetNameToEncoding((CFStringRef)currentCharset);
		NSStringEncoding nsCharset = CFStringConvertEncodingToNSStringEncoding(cfCharset);
		NSString *tempString = [[NSString alloc]initWithData:targetData encoding:nsCharset];
//		NSLog(@"%d, %d, %@", cfCharset, nsCharset, currentCharset);
		int thisRate = 0;
		int currentScore = 0;
		NSUInteger used = 0;
		BOOL succeed = NO;
		NSData *convertedData;
//		NSString *convertedString;
		if ( tempString != nil )	{
			int initLengthString = [tempString length];
			unsigned char buffer[initLengthString * 3];
			succeed = [tempString getBytes:buffer 
				maxLength:	initLengthString * 3 
				usedLength:	&used 
				encoding:	nsCharset
				options:	1 
				range:		NSMakeRange(0,initLengthString)
				remainingRange:	NULL];
			convertedData = [NSData dataWithBytesNoCopy:buffer length:used];
			int i = 0;
			for( i = 0; i < [tempString length] ; i++ )	{
				unichar c = [tempString characterAtIndex :i];
				if ( [irCSet characterIsMember:c] )	currentScore++;
//				if ( c < ' ' && c != 0x09 && c != 0x0a && c != 0x0d )	currentScore++;
//				NSLog(@"%@: %c (%x)", currentCharset, c, (unsigned int)c);
			}
			thisRate = ([tempString length] - currentScore) * 100 / [tempString length];
			if ( [convertedData length] != lengthOriginal )
				thisRate /= 2;
			else	{
				unsigned char *bytesOrigial;
				bytesOrigial = (void *)[targetData bytes];
				unsigned char *bytesConveted;
				bytesConveted = (void *)[convertedData bytes];
				int i;
				currentScore = 0;
				for ( i = 0 ; i < lengthOriginal ; i++ )
					if ( bytesOrigial[i] != bytesConveted[i] )
							currentScore++;
				thisRate *=  (lengthOriginal - currentScore) / lengthOriginal;
			}
			NSLog(@"Encoding: %@, score:%d", currentCharset, thisRate);
		}
		else
			NSLog(@"Encoding: %@, -Encoding Error-", currentCharset);

		if ( thisRate > score )	{
			score = thisRate;
			candidateEncoding = nsCharset;
			chosenEncoding = currentCharset;
		}
	}
	NSLog( @"Chosen Encoding: %@", chosenEncoding);
	return candidateEncoding;
}

- (NSArray *)incorporateFromPartInfoFile
{
	NSError *err = nil;
	NSMutableArray *partInfoArray = [NSMutableArray arrayWithCapacity:1];
	NSString *partInfoFile = [parentPath stringByAppendingString: @"/__OME_PartsInfo.xml"];

	NSXMLDocument *partInfoXML = [[NSXMLDocument alloc] 
						initWithContentsOfURL: [NSURL fileURLWithPath:partInfoFile]
						options: (NSXMLNodePreserveWhitespace|NSXMLNodePreserveCDATA)
						error: &err];
		//パート情報ファイルの中身からDOMを構築
	NSXMLNode* aNode = [partInfoXML rootElement];	//ノードを順番にチェック

	//データのバージョンのチェック
	NSString *versionStr = [[((NSXMLElement*)aNode) attributeForName:@"version"] stringValue];
	if ( versionStr == nil )
		partInfoVersion = 1;
	else
		partInfoVersion = [versionStr intValue];

	//ルート直下の兄弟だけを見れば良い。全部part-infoノードのはずだけど念のためにチェック
	NSArray* childrenOfRoot = [aNode children];
	int i, countNodes = [childrenOfRoot count];
	for ( i = 0 ; i < countNodes ; i++ )	{
		NSXMLElement* child = [childrenOfRoot objectAtIndex:i];
		if ( [[child name] caseInsensitiveCompare: @"part-info"] == NSOrderedSame )	{
			NSMutableDictionary *currentInfo = [[NSMutableDictionary dictionaryWithCapacity:1]retain];
			if ( [child attributeForName:@"alternative"] != NULL )
				[currentInfo setObject:@"YES" forKey:@"alternative"];
			[partInfoArray addObject:currentInfo];
			NSArray *childrenNodes = [child children];
			NSEnumerator *objectEnumerator = [childrenNodes objectEnumerator];
			NSXMLNode *currentNode;
			BOOL isAttachment = NO;
			while (currentNode = [objectEnumerator nextObject])	{
				NSString* currentValue = [currentNode stringValue];
				NSString* currentName = [currentNode name];
				[currentInfo setObject:currentValue forKey:currentName];
				if (( [currentName caseInsensitiveCompare:@"content-disposition"] == NSOrderedSame ) &&
					( [currentValue caseInsensitiveCompare:@"attachment"] == NSOrderedSame ) )
					isAttachment = YES;
			}
			if ( ( [[[currentInfo objectForKey:@"content-type"]substringToIndex:4] 
								caseInsensitiveCompare:@"text"] == NSOrderedSame ) && ! isAttachment )		{
				NSString *currentFileName = [currentInfo objectForKey:@"file-name"];
				NSString *charset = [currentInfo objectForKey:@"characterset"];
				CFStringEncoding cfCharset = CFStringConvertIANACharSetNameToEncoding((CFStringRef)charset);
				NSStringEncoding nsCharset = CFStringConvertEncodingToNSStringEncoding(cfCharset);
				NSString *fileTopHeaderString = @"", *realHeaderString = @"", *realPartHeaderString = @"";
				NSString *fileContents = [self fileContents: &currentFileName encoding: nsCharset
												topHeaders: &fileTopHeaderString 
												realHeaders: &realHeaderString 
												partHeaders: &realPartHeaderString];
				if ( fileContents == nil )	return nil;
				[currentInfo setObject:fileContents forKey:@"contents"];
				[currentInfo setObject:currentFileName forKey:@"file-name"];
				NSArray *eachRealHeaders;	
				NSEnumerator *enumRealHeaders;
				NSString *oneHeaderLine;
				NSString *currentHeaderLavel;
				NSMutableString *currentHeaderValue;
				NSString *newLineString = [ OMEMessage getNextLineString: fileContents ];
				if ( fileTopHeaderString != nil )	{	//ファイルの最初のヘッダがある場合
					eachRealHeaders = [fileTopHeaderString componentsSeparatedByString:newLineString];	
					enumRealHeaders = [eachRealHeaders objectEnumerator];
					currentHeaderLavel = @"";
					currentHeaderValue = [NSMutableString stringWithCapacity:256];
					while ( oneHeaderLine = [enumRealHeaders nextObject] )	{
						if( [currentHeaderLavel length] != 0 )
							[fields setObject:currentHeaderValue forKey:currentHeaderLavel];
						NSRange colonPos = [oneHeaderLine rangeOfString:@":"];
						if ( colonPos.length > 0 )	{
							currentHeaderLavel = [oneHeaderLine substringToIndex:colonPos.location];
							currentHeaderValue = [NSMutableString stringWithCapacity:256];
							[currentHeaderValue appendString:
								[oneHeaderLine substringFromIndex:colonPos.location+1]];
						}
					}
					if( [currentHeaderLavel length] != 0 )
						[fields setObject:currentHeaderValue forKey:currentHeaderLavel];
				}
				if ( realHeaderString != nil )	{	//実ヘッダがある場合
					//実ヘッダの取り出し
					eachRealHeaders = [realHeaderString componentsSeparatedByString:newLineString];	
					enumRealHeaders = [eachRealHeaders objectEnumerator];
					currentHeaderLavel = @"";
					currentHeaderValue = [NSMutableString stringWithCapacity:256];
					while ( oneHeaderLine = [enumRealHeaders nextObject] )	{
						if ( [oneHeaderLine length] > 0 )	{
							[realHeadersString appendFormat:@"%@\n", oneHeaderLine ];

							unichar firstChar = [ oneHeaderLine characterAtIndex:0 ];
							if ( firstChar == 0x0020 || firstChar == 0x0008 )	{
								// folding header line
								[currentHeaderValue appendString:newLineString];
								[currentHeaderValue appendString:oneHeaderLine];
							}
							else	{	// it has header label string.
								if( [currentHeaderLavel length] != 0 )
									[realMailHeaders setObject:currentHeaderValue forKey:currentHeaderLavel];
								NSRange colonPos = [oneHeaderLine rangeOfString:@":"];
								if ( colonPos.length > 0 )	{
									currentHeaderLavel = [oneHeaderLine substringToIndex:colonPos.location];
									currentHeaderValue = [NSMutableString stringWithCapacity:256];
									[currentHeaderValue appendString:
										[oneHeaderLine substringFromIndex:colonPos.location+1]];
								}
							}
						}
					}
					if( [currentHeaderLavel length] != 0 )
						[realMailHeaders setObject:currentHeaderValue forKey:currentHeaderLavel];
				}
			}
		}
	}
	return partInfoArray;
}

- (NSString *)fileContents:(NSString **)fileName 
		encoding:(NSStringEncoding)enc topHeaders:(NSString **)topHeadersPtr
		realHeaders:(NSString **)realHeadersPtr partHeaders:(NSString **)partHeadersPtr
{
	NSFileManager *myFileManager = [NSFileManager defaultManager];
	
//	topHeaders = nil;	realHeaders = 0;	partHeaders = 0;
	
	NSString *fileNamePtr = *fileName;
	int fileNameLength = [fileNamePtr length];
	int i;
	for ( i = (fileNameLength - 1) ; i > 0  ; i-- )
		if ( [fileNamePtr characterAtIndex: i] == '.' )	{
			break;
		}
	NSString *fnWOExtention = [ fileNamePtr substringToIndex: i ];
	NSString *fnExt = @".";
	fnExt = [fnExt stringByAppendingString:[ fileNamePtr pathExtension ] ];
	NSArray *extentionSet = [NSArray arrayWithObjects: fnExt, @".mail", @".ygm", @".rply", @".txt", @".html", @".htm", nil];
	NSEnumerator *elements = [extentionSet objectEnumerator];
	NSString *currentExt;
	NSString *currentPath;
	while( currentExt = [elements nextObject] )	{
		currentPath = [[[parentPath stringByAppendingString:@"/"] 
									stringByAppendingString:fnWOExtention] 
									stringByAppendingString:currentExt];
//		NSLog(currentPath);
		if ( [myFileManager fileExistsAtPath: currentPath] )	{
			NSError *error;
			NSString *fileContents = [NSString stringWithContentsOfFile: currentPath										
											encoding: enc error: &error];
			if ( fileContents == nil )	{
				NSString *errorMessage = [error localizedDescription];
				NSLog(@"OME Error: Encoding error in read the file. %@", errorMessage);
				NSStringEncoding predicatedEncoding = [self predictTheCharacterSet:currentPath];
				fileContents = [NSString stringWithContentsOfFile: currentPath										
											encoding: predicatedEncoding error: &error];
				if ( fileContents == nil )	{
					NSLog(@"OME Error: No way! Encoding error in read the file in spite of retrying. %@", errorMessage);
					return @"";
				}
			}
//			[fnWOExtention retain];
			*fileName = [fnWOExtention stringByAppendingString:currentExt];

			NSString *newLineString = [ OMEMessage getNextLineString: fileContents ];
			int startOfMessagePosition = 0;
			int endOfMessagePosition = [fileContents length];
			NSRange rule3 = NSMakeRange( 0, 0 );
			if ( [[fnWOExtention stringByAppendingString:@".mpart"] 
					caseInsensitiveCompare: [parentPath lastPathComponent]] == NSOrderedSame )	{
				rule3 = [fileContents rangeOfString:[newLineString stringByAppendingString:newLineString]];
				if ( rule3.length > 0 )	{
					startOfMessagePosition = rule3.location + [[newLineString stringByAppendingString:newLineString]length];
					NSString *tempTop = [NSString stringWithString:[fileContents substringToIndex:rule3.location]];
//					[tempTop retain];
					//check if part of header area?
					BOOL judgement = YES;
					NSArray *eachLines = [tempTop componentsSeparatedByString:@"\n"];
					for ( NSString *oneLine in eachLines )	{
						NSRange colonPosition = [oneLine rangeOfString:@":"];
						if ( colonPosition.length == 0 )	{
							judgement = NO;
							break;
						}
						int ix;
						for ( ix=0 ; ix<colonPosition.location ; ix++ )	{
							unichar c = [oneLine characterAtIndex:ix];
							if ( ! ( (c>='A'&&c<='Z') || (c>='a'&&c<='z') ) ){
								judgement = NO;
								break;
							}
						}
					}
					if ( judgement == YES )
						*topHeadersPtr = tempTop;
					else	{
						rule3 = NSMakeRange( 0, 0 );
						startOfMessagePosition = 0;
					}
				}
			}
			NSRange rule1 = [fileContents rangeOfString:@"<!-- Real Mail Headers -->"];
			NSRange rule2 = [fileContents rangeOfString:@"<!-- Real Part Headers -->"];
			if ( rule1.length > 0 )	{
				endOfMessagePosition = rule1.location;
				int stringStartPoint = rule1.location + rule1.length + [newLineString length];
				NSString *tempTop = [NSString stringWithString:[fileContents substringFromIndex:stringStartPoint]];
//				[tempTop retain];
				*realHeadersPtr = tempTop;
			}
			if ( rule2.length > 0 )	{
				endOfMessagePosition = rule2.location;
				int stringStartPoint = rule2.location + rule2.length + [newLineString length];
				NSString *tempTop = [NSString stringWithString:[fileContents substringFromIndex:stringStartPoint]];
//				[tempTop retain];
				*partHeadersPtr = tempTop;
			}
			if ( endOfMessagePosition == startOfMessagePosition )	{
				if ( ( rule1.length == 0 ) && ( rule2.length == 0 ) )
					endOfMessagePosition = [fileContents length];
				else if ( rule1.length == 0 )
					endOfMessagePosition = rule2.location;
				else if ( rule2.length == 0 )
					endOfMessagePosition = rule1.location;
				else	//こんな条件はありえないのだが、本文中にruleにひっかかるキーワードがあるとしたら…
					if ( rule1.location > rule2.location )
						endOfMessagePosition = rule1.location;
					else
						endOfMessagePosition = rule2.location;
//				startOfMessagePosition = 0;
			}
			NSRange messageRange = NSMakeRange( startOfMessagePosition, endOfMessagePosition - startOfMessagePosition );
			return [NSString stringWithString:[fileContents substringWithRange:messageRange]];
		}
	}
	return @"";
}

- (id)initWithData:(NSData *)messageData
{
	NSString	*X_OME_CHARSET = @"X-OME-CharSet";
	fields = nil;
	text = nil;
	realMailHeaders = nil;
	strEnc = NSShiftJISStringEncoding;
	isMultiPart = NO;
	partInfo = nil;

	if (self = [super init]) {
		message = [[NSMutableString alloc] initWithCapacity:BUFSIZ];
		fields = [[NSMutableDictionary alloc] initWithCapacity:BUFSIZ];
		text = [[NSMutableString alloc] initWithCapacity:BUFSIZ];
		realMailHeaders = [[NSMutableDictionary alloc] initWithCapacity:BUFSIZ];
		realHeadersString = [[NSMutableString alloc] initWithCapacity:BUFSIZ];

		if (messageData != nil) {
			[self anaData:messageData];
			NSString	*charset = [realMailHeaders objectForKey:X_OME_CHARSET];
			strEnc = [OMEMessage analizeCharacterSet:charset message:messageData];
			NSString	*msg = [[NSString alloc] initWithData:messageData encoding:strEnc];
			if (msg != nil) {
				[self anaMessage:msg];
			}
			[msg release];
		}
	}
	return self;
}

- (void)dealloc
{
	[message release];
	[fields release];
	[text release];
	[realMailHeaders release];
	[realHeadersString release];
	[targetFilePath  release];
	[parentPath  release];

	[super dealloc];
}

- (NSDictionary*)fields
{
	return fields;
}

- (NSDictionary*)realMailHeaders
{
	return realMailHeaders;
}

- (NSString*)realHeadersString
{
	return realHeadersString;
}

- (NSString*)text
{
	return text;
}

- (void)setMetadataAttributes:(NSMutableDictionary *)attributes
{
	/* [self dumpOMEMessage]; */
	/*
	 * kMDItemAuthorEmailAddresses     Fromアドレス
	 * kMDItemAuthors                  From氏名
	 * kMDItemContentType              OME書類のUTI, public.data, public.item, public.message
	 * kMDItemLastUsedDate             Dataヘッダーフィールドの値と一致（2004-11-01 17:25:05 +0900）
	 * kMDItemRecipientEmailAddresses  Toアドレスのリスト
	 * kMDItemRecipients               To氏名（またはアドレス）
	 * kMDItemTextContent              電文
	 */
	if ([realMailHeaders objectForKey:@"Message-ID"] != nil) {
		[attributes setObject:[realMailHeaders objectForKey:@"Message-ID"] forKey:(NSString *)kMDItemIdentifier];
	}
	if ([fields objectForKey:@"Subject"] != nil) {
		[attributes setObject:[fields objectForKey:@"Subject"] forKey:(NSString *)kMDItemTitle];
	}
	if ([fields objectForKey:@"From"] != nil) {
		[attributes setObject:[NSArray arrayWithObject:[fields objectForKey:@"From"]]
				forKey:(NSString *)kMDItemWhereFroms];
	}
	if (message != nil) {
		[attributes setObject:message forKey:(NSString *)kMDItemTextContent];
	}
}

- (NSString *)stringForHeader:(NSString *)headerName	{
	NSEnumerator *theEnum = [ fields keyEnumerator ];
	NSString *currentName;
	while ( currentName = [ theEnum nextObject ] )	{
		if ( [ currentName caseInsensitiveCompare:headerName] == NSOrderedSame )	{
			return [ fields objectForKey:currentName ];
		}	
	}
	theEnum = [ realMailHeaders keyEnumerator ];
	while ( currentName = [ theEnum nextObject ] )	{
		if ( [ currentName caseInsensitiveCompare:headerName] == NSOrderedSame )	{
			return [ realMailHeaders objectForKey:currentName ];
		}	
	}
	return nil;
}

- (NSArray *)partInfo
{
	return partInfo;
}

- (NSString *)parentPath
{
	return parentPath;
}

- (BOOL)hasCertificate
{
	return hasCertificate;
}

- (NSString *)htmlWellFormedString
{
	return [self htmlFormedString:DefaultCSSFileName printCSS:DefaultCSSFileNamePrint];
}
- (NSString *)htmlFormedString:(NSString *)cssFileNameForDisplay printCSS:(NSString *)cssFileNameForPrint
{
/*	// Prepare regular expressions
	regexURL = [OGRegularExpression 
		regularExpressionWithString:@"s?https?:\\/\\/[\\-_\\.!~\\*'()a-zA-Z0-9;/?:@&=+$,%#]+"];
	regexMailAddress = [OGRegularExpression 
		regularExpressionWithString:@"([a-zA-Z0-9!$&*.=^`|~#%'+\\/?_{}-]+@[a-zA-Z0-9_\\.\\-]+)"];
		
		//http://www.din.or.jp/%7Eohzaki/perl.htm#httpURL
*/
	OMEBehavior *omeBehavior = [[OMEBehavior alloc]initOnCurrentEnv];
	
	// Obtain pathes of CSS files
	NSFileManager *myFileManager = [NSFileManager defaultManager];
	
	NSString *cssDir = [[OMEPaths omePreferences] stringByAppendingString: @"/"];
	
	NSString *cssPath = [cssDir stringByAppendingString:cssFileNameForDisplay];
	if ( [myFileManager fileExistsAtPath:cssPath] == NO )	{
		cssDir = [OMEPaths omeFrameworkResourcePath];
//		cssPath = [cssDir stringByAppendingString:cssFileNameForDisplay];
		cssPath = [cssDir stringByAppendingPathComponent:cssFileNameForDisplay];
		if ( [myFileManager fileExistsAtPath:cssPath] == NO )	{
			NSLog(@"OME Error: CSS file for screen doesn't exist anyhwere.");
			cssPath = @"/Library/Frameworks/OME.framework/Resources/OMEMailViewer.css";
		}
	}

	cssDir = [[OMEPaths omePreferences] stringByAppendingString: @"/"];
	NSString *cssPathPrint;
	if ( cssFileNameForPrint != NULL )	{
		cssPathPrint = [cssDir stringByAppendingString:cssFileNameForPrint];
		if ( [myFileManager fileExistsAtPath:cssPathPrint] == NO )	{
			cssDir = [OMEPaths omeFrameworkResourcePath];
			cssPathPrint = [cssDir stringByAppendingPathComponent:cssFileNameForPrint];
			if ( [myFileManager fileExistsAtPath:cssPath] == NO )	{
				NSLog(@"OME Error: CSS file for printer doesn't exist anyhwere.");
				cssPathPrint = @"/Library/Frameworks/OME.framework/Resources/OMEMailViewerPrint.css";
			}
		}
	}
	
	NSString* cssURL = @"file://";	//[[NSURL fileURLWithPath:cssPath]absoluteString]
	cssURL = [cssURL stringByAppendingString:cssPath];
	NSString* cssPrintURL;
	if ( cssFileNameForPrint != NULL )
		cssPrintURL = [@"file://" stringByAppendingString:cssPathPrint];
	else
		cssPrintURL = NULL;
	// Prepare variables for HTML sources
	NSMutableString* htmlSourceTmp = [NSMutableString stringWithCapacity:100];
	[htmlSourceTmp setString:@""];
	NSMutableString* tempHTML = [NSMutableString stringWithCapacity:100];
//	NSMutableString* tempHTML2 = [NSMutableString stringWithCapacity:100];

	[htmlSourceTmp appendString:
		[self createMailHeaderHTML:[[self fields] objectForKey:@"From"]
			label:@"From:" idstring:@"header-item-from"]];
	[htmlSourceTmp appendString:
		[self createMailHeaderHTML:[[self fields] objectForKey:@"Subject"]
			label:@"Subject:" idstring:@"header-item-subject"]];

	[NSDateFormatter setDefaultFormatterBehavior:NSDateFormatterBehavior10_4];
	NSDateFormatter* dateFormatter = [[[NSDateFormatter alloc]init]autorelease];
	[dateFormatter setDateStyle:NSDateFormatterLongStyle];
	[dateFormatter setTimeStyle:NSDateFormatterLongStyle];
	NSString *originalHdrDate = [[self fields] objectForKey:@"Date"];
	if ( originalHdrDate == nil )
		originalHdrDate = [[self realMailHeaders] objectForKey:@"Date"];
	NSString* hdrDateString;
	if ( originalHdrDate != nil )	{
		NSDate* hdrDate = [NSDate dateWithNaturalLanguageString:originalHdrDate];
		hdrDateString = [dateFormatter stringFromDate:hdrDate ];
	}
	else	
		hdrDateString = @"Can't get the Date filed.";
	[tempHTML setString:@""];
	[tempHTML appendAsHTMLSource:@"Sender Date: " tag:@"b"];
	[tempHTML appendAsHTMLSource:originalHdrDate];
	[tempHTML encloseByTag:@"span"
		attributes:[NSDictionary dictionaryWithObject:@"header-original-date" forKey:@"class"]];
	[htmlSourceTmp appendString:
		[self createMailHeaderHTML: [hdrDateString stringByAppendingString: tempHTML]
			label:@"Date:" idstring:@"header-item-date"]];

	[htmlSourceTmp appendString:
		[self createMailHeaderHTML:[[self fields] objectForKey:@"To"]
			label:@"To:" idstring:@"header-item-to"]];

	NSEnumerator *theEnum = [[omeBehavior additionalHeaders]objectEnumerator];
	NSString *headerName;
	while ( headerName = [ theEnum nextObject ] )	{
		NSString* headerValue = [self stringForHeader:headerName];
		if ( headerValue != nil )
			[htmlSourceTmp appendString: [self createMailHeaderHTML:headerValue
				label:[headerName stringByAppendingString:@": "] idstring:@"header-item-cc"]];
	}
	
	//Finish the mail header section
	[htmlSourceTmp appendAsHTMLSource: @"" tag:@"br" 
		attributes:[NSDictionary dictionaryWithObject: @"mail-header-end" forKey: @"class"]];
	
	//Mail Body

	BOOL isContainsTextPart = NO;
	BOOL isContainsHTMLPart = NO;
	if( [self isMultiPartMail] )	{
//		NSArray *partInfo = [thisMessage partInfo];
		NSEnumerator *elements = [partInfo objectEnumerator];
		NSDictionary *currentPart;
		while( currentPart = [elements nextObject] )	{
			NSString *contentType = [currentPart objectForKey:@"content-type"];
			NSArray *contentTypeComponents = [contentType componentsSeparatedByString:@";"];
			NSString *contentBaseType = [contentTypeComponents objectAtIndex:0];
			if ( [contentBaseType caseInsensitiveCompare:@"text/plain"] == NSOrderedSame )
				isContainsTextPart = YES;
			if ( [contentBaseType caseInsensitiveCompare:@"text/html"] == NSOrderedSame )
				isContainsHTMLPart = YES;
		}
	}

	NSString *fileExtension = [targetFilePath pathExtension];
	BOOL isHTMLlocally = NO;
	if (	( [fileExtension caseInsensitiveCompare:@"html"] == NSOrderedSame ) 
		 || ( [fileExtension caseInsensitiveCompare:@"htm"] == NSOrderedSame ) )	{
				isHTMLlocally = YES;
	}

	if ( isHTMLlocally )
		[htmlSourceTmp appendAsHTMLSource: @" " tag:@"div" 
			attributes:[NSDictionary dictionaryWithObject: @"spacer1" forKey: @"class"]];

	NSMutableString *collectHeader = [NSMutableString stringWithCapacity:100];
	if( [self isMultiPartMail] == NO )	{
		if ( ! isHTMLlocally )	{
			[tempHTML setString:@""];
			[tempHTML setString: [self text]];
			[tempHTML convertHTMLSourceWithInsertingTagsForColorComment];
			[tempHTML linkedHTML];
			[tempHTML encloseByTag: @"div"
				attributes: [NSDictionary dictionaryWithObject: @"body-text" forKey: @"class"]];
			[htmlSourceTmp	appendString:tempHTML];
		}
		else {
			NSString *headerTextL = nil;
			NSString *bodyText = nil;
			[self getOutHeaderAndBodyFromHTML:[self text] 
					headerText: &headerTextL bodyText: &bodyText];
			[htmlSourceTmp	appendString:bodyText];
			[collectHeader appendString:headerTextL];
		}
	}
	else	{
		if ( partInfoVersion < 2 )	{	// Part info file is first version or nothing at all.
			NSEnumerator *elements = [partInfo objectEnumerator];
			NSDictionary *currentPart;
			while( currentPart = [elements nextObject] )	{
				NSString *fileName = [currentPart objectForKey:@"file-name"];
				NSString *contentType = [currentPart objectForKey:@"content-type"];
				NSArray *contentTypeComponents = [contentType componentsSeparatedByString:@";"];
				NSString *contentBaseType = [contentTypeComponents objectAtIndex:0];
				NSArray *contentBaseTypeComponents = [contentBaseType componentsSeparatedByString:@"/"];
	//			NSError *error = nil;
				NSString *currentContents = [currentPart objectForKey:@"contents"];
				if ( currentContents == nil )	currentContents = @"";
				
				BOOL isTextPart = ( [contentBaseType caseInsensitiveCompare:@"text/plain"] == NSOrderedSame );
				BOOL isHTMLPart = ( [contentBaseType caseInsensitiveCompare:@"text/html"] == NSOrderedSame );
				
				if ( isTextPart && ( ! isContainsHTMLPart ) )	{
					[tempHTML setString:@""];
					[tempHTML setString: currentContents];
					[tempHTML convertHTMLSource];
					[tempHTML linkedHTML];
					[tempHTML encloseByTag: @"div"
						attributes: [NSDictionary dictionaryWithObject: @"body-text" forKey: @"class"]];
					[htmlSourceTmp	appendString:tempHTML];
				}
				else if ( isTextPart &&     isContainsHTMLPart )
					if ( ! isHTMLlocally )	{
						[tempHTML setString:@""];
						[tempHTML setString: currentContents];
						[tempHTML convertHTMLSource];
						[tempHTML linkedHTML];
						[tempHTML encloseByTag: @"div"
							attributes: [NSDictionary dictionaryWithObject: @"body-text" forKey: @"class"]];
						[htmlSourceTmp	appendString:tempHTML];
					}
					else	{
						[htmlSourceTmp appendString: 
							[self createLinkToFile: fileName 
								inDirectory: [self parentPath]]];
					}
				else if ( isHTMLPart && ( ! isContainsTextPart ) ) 	{
					NSString *headerTextL;
					NSString *bodyText;
					[self getOutHeaderAndBodyFromHTML:currentContents
							headerText: &headerTextL bodyText: &bodyText];
					[htmlSourceTmp	appendString:bodyText];
					[collectHeader appendString:headerTextL];
				}
				else if ( isHTMLPart &&     isContainsTextPart )	{
					if ( isHTMLlocally )	{
						NSString *headerTextL;
						NSString *bodyText;
						[self getOutHeaderAndBodyFromHTML:currentContents
								headerText: &headerTextL bodyText: &bodyText];
						[htmlSourceTmp	appendString:bodyText];
						[collectHeader appendString:headerTextL];
					}
					else	{
						[htmlSourceTmp appendString: 
							[self createLinkToFile: fileName 
								inDirectory: [self parentPath]]];
					}
				}
				else if ( [[contentBaseTypeComponents objectAtIndex:0] caseInsensitiveCompare:@"image"] 
							== NSOrderedSame )	{
					NSString *srcURL = @"file://";
					srcURL = [[[srcURL stringByAppendingString: [self parentPath]]
								stringByAppendingString: @"/"]
								stringByAppendingString: fileName];
					[tempHTML setString:@""];
					[tempHTML appendAsHTMLSource:@"" tag:@"img"
						attributes:[NSDictionary dictionaryWithObjectsAndKeys:
							srcURL, @"src", nil]];
					[tempHTML encloseByTag:@"div"
						attributes:[NSDictionary dictionaryWithObject:@"body-image" forKey:@"class"]];
					[htmlSourceTmp appendString:tempHTML];
				}
				else	{
					[htmlSourceTmp appendString: 
						[self createLinkToFile: fileName 
							inDirectory: [self parentPath]]];
					if ( [[fileName pathExtension]caseInsensitiveCompare: @"p7s"] == NSOrderedSame )
							hasCertificate = YES;
				}
			}
		}
		else	{	// Pert info file version is after 2.
		
			NSDictionary *currentPart;
			NSString *altPartFNHTML = nil;
			NSString *altPartFNText = nil;
			int alternatePartCount = 0;
			for ( currentPart in partInfo )	{
				if ( [currentPart objectForKey:@"alternative"] != nil )	{
					alternatePartCount++;
					NSString *fileName = [currentPart objectForKey:@"file-name"];
					NSString *contentType = [currentPart objectForKey:@"content-type"];
					if ( [contentType rangeOfString:@"text/plain"].location == 0 )
						altPartFNText = fileName;
					else if ( [contentType rangeOfString:@"text/html"].location == 0 )
						altPartFNHTML = fileName;
//				NSLog(@"Alternative Part: %@", fileName);
				}
			}
			NSString *suppressingFileName = nil;
			if (( alternatePartCount == 2 ) && ( altPartFNHTML != nil ) && ( altPartFNText != nil ))
					suppressingFileName = altPartFNText;

			NSEnumerator *elements = [partInfo objectEnumerator];
			while( currentPart = [elements nextObject] )	{
				NSString *fileName = [currentPart objectForKey:@"file-name"];
				if (	( suppressingFileName == nil ) 
					 ||	(( suppressingFileName != nil ) && ( [ suppressingFileName compare:fileName ] != NSOrderedSame )))	{
					NSString *contentType = [currentPart objectForKey:@"content-type"];
					NSString *contentDisposition = [currentPart objectForKey:@"content-disposition"];
					if ( contentDisposition == nil )
						contentDisposition = @"NOTHING";
					NSArray *contentTypeComponents = [contentType componentsSeparatedByString:@";"];
					NSString *contentBaseType = @"";
					NSString *contentSubType = @"";
					NSString *currentContents = [currentPart objectForKey:@"contents"];
//					BOOL isAlaternate = ( [currentPart objectForKey:@"alternate"] != NULL );
					if ( currentContents == nil )	currentContents = @"";
					if ( [contentTypeComponents count] > 0 )	{
						NSArray *contentBaseTypeComponents = [[contentTypeComponents objectAtIndex:0] componentsSeparatedByString:@"/"];
						if ( [contentBaseTypeComponents count] > 0 )
							contentBaseType = [contentBaseTypeComponents objectAtIndex:0];
						if ( [contentBaseTypeComponents count] > 1 )
							contentSubType = [contentBaseTypeComponents objectAtIndex:1];
					}
					if ( [contentDisposition caseInsensitiveCompare:@"attachment"] == NSOrderedSame )	{
							[htmlSourceTmp appendString: 
								[self createLinkToFile: fileName 
									inDirectory: [self parentPath]]];
					}
					else if ( [contentBaseType caseInsensitiveCompare:@"image"] == NSOrderedSame )	{
						NSString *srcURL = @"file://";
						srcURL = [[[srcURL stringByAppendingString: [self parentPath]]
									stringByAppendingString: @"/"]
									stringByAppendingString: fileName];
						[tempHTML setString:@""];
						[tempHTML appendAsHTMLSource:@"" tag:@"img"
							attributes:[NSDictionary dictionaryWithObjectsAndKeys:
								srcURL, @"src", nil]];
						[tempHTML encloseByTag:@"div"
							attributes:[NSDictionary dictionaryWithObject:@"body-image" forKey:@"class"]];
						[htmlSourceTmp appendString:tempHTML];
					}
					else if ( [contentBaseType caseInsensitiveCompare:@"text"] == NSOrderedSame )	{
						if ( [contentSubType caseInsensitiveCompare:@"html"] == NSOrderedSame )	{
							NSString *headerTextL;
							NSString *bodyText;
							[self getOutHeaderAndBodyFromHTML:currentContents
									headerText: &headerTextL bodyText: &bodyText];
							[htmlSourceTmp	appendString:bodyText];
							[collectHeader appendString:headerTextL];
						}
						else	{
							[tempHTML setString:@""];
							[tempHTML setString: currentContents];
							[tempHTML convertHTMLSource];
							[tempHTML linkedHTML];
							[tempHTML encloseByTag: @"div"
								attributes: [NSDictionary dictionaryWithObject: @"body-text" forKey: @"class"]];
							[htmlSourceTmp	appendString:tempHTML];
						}
					}
					else	{
						[htmlSourceTmp appendString: 
								[self createLinkToFile: fileName 
									inDirectory: [self parentPath]]];

					}
				}
			}
		}
	}
	[htmlSourceTmp encloseByTag:@"body"];
	if ( ! isHTMLlocally )
		[tempHTML setString:@""];
	else {
//		NSString *headerTextL = nil;
//		NSString *bodyText = nil;
//		[self getOutHeaderAndBodyFromHTML:[thisMessage text] headerText: &headerTextL bodyText: &bodyText];
		[tempHTML setString:collectHeader];
	}
	[tempHTML appendAsHTMLSource:@"" tag:@"link"
		attributes:[NSDictionary dictionaryWithObjectsAndKeys:
			@"stylesheet", @"rel", cssURL, @"href", @"text/css", @"type", @"all", @"media", nil]];
	if ( cssPrintURL != NULL )
		[tempHTML appendAsHTMLSource:@"" tag:@"link"
			attributes:[NSDictionary dictionaryWithObjectsAndKeys:
				@"stylesheet", @"rel", cssPrintURL, @"href", @"text/css", @"type", @"print", @"media", nil]];
	[tempHTML encloseByTag:@"head"];

	[tempHTML appendString:htmlSourceTmp];
	[tempHTML encloseByTag:@"html"];
	
//	NSLog(tempHTML);
	
	return tempHTML;

/*
	参考にしたサイト
	http://www.din.or.jp/%7Eohzaki/perl.htm#HTML_Tag
	http://hodade.adam.ne.jp/seiki/page.php?s_mail
*/
}


- (NSString *)createLinkToFile:(NSString *)fileName inDirectory:(NSString *)fileParent
{
	NSString *srcURL = @"file://";
	srcURL = [[[srcURL stringByAppendingString: fileParent]
				stringByAppendingString: @"/"]
				stringByAppendingString: fileName];
	NSURL *linkURL = [NSURL fileURLWithPath:srcURL];
	NSMutableString *tempHTML = [NSMutableString stringWithCapacity:100];
	[tempHTML setString:@""];
	[tempHTML appendAsHTMLSource:fileName tag:@"a"
		attributes:[NSDictionary dictionaryWithObjectsAndKeys:
			(NSString* )CFURLGetString((CFURLRef)linkURL), @"href", nil]];
/*
	[tempHTML appendAsHTMLSource:@"" tag:@"span"
		attributes:[NSDictionary dictionaryWithObjectsAndKeys:
			@"margin-left: 16px;", @"style", nil]];

	NSString *srcQLURL = @"quicklook://";
	srcQLURL = [[[srcQLURL stringByAppendingString: fileParent]
				stringByAppendingString: @"/"]
				stringByAppendingString: fileName];
	NSURL *linkQLURL = [NSURL fileURLWithPath:srcQLURL];
	[tempHTML appendAsHTMLSource:@"QuickLook" tag:@"a"
		attributes:[NSDictionary dictionaryWithObjectsAndKeys:
			(NSString* )CFURLGetString((CFURLRef)linkQLURL), @"href", nil]];
*/
	[tempHTML encloseByTag:@"div"
		attributes:[NSDictionary dictionaryWithObject:@"body-link" forKey:@"class"]];
	return tempHTML;
}

- (NSString *)createMailHeaderHTML:(NSString *)value label:(NSString *)label idstring:(NSString *)idstring
{
	NSMutableString* tempHTML = [NSMutableString stringWithCapacity:100];
	NSMutableString* tempHTML2 = [NSMutableString stringWithCapacity:100];
	
	if( value != nil )
		[tempHTML setString: [self linkedHTML: value]];
	[tempHTML2 setString:@""];
	[tempHTML encloseByTag: @"div"
		attributes:[NSDictionary dictionaryWithObject: @"header-data-wrap" forKey: @"class"]];
	[tempHTML encloseByTag: @"div" 
		attributes:[NSDictionary dictionaryWithObject: @"header-data" forKey: @"class"]];
	[tempHTML2 appendAsHTMLSource:label tag: @"div"
		attributes:[NSDictionary dictionaryWithObject: @"header-label" forKey: @"class"]];
	[tempHTML appendString:tempHTML2];
	[tempHTML encloseByTag: @"div" 
		attributes:[NSDictionary dictionaryWithObject: idstring forKey: @"id"]];
	[tempHTML appendString: @"<div class=\"div.devide-header\">"];
	return tempHTML;
}

- (NSString *)linkedHTML:(NSString *)source
{
    return source;
/*	return	[regexMailAddress replaceAllMatchesInString:
			[regexURL replaceAllMatchesInString: source
					withString:@"<a href=\"\\0\">\\0</a>"] 
					withString:@"<a href=\"mailto:\\0\">\\0</a>"];*/
}

- (void)getOutHeaderAndBodyFromHTML:(NSString *)source 
			headerText:(NSString **)headerTextL 
			bodyText:(NSString **)bodyText
{
	if ( source == nil )
		source = @"";

	NSRange headerStart = [source rangeOfString: @"<head" options: NSCaseInsensitiveSearch];
	NSRange headerEnd = [source rangeOfString: @"</head" options: NSCaseInsensitiveSearch];
	NSRange headerRange;
	if (	( headerStart.location != NSNotFound ) && ( headerEnd.location != NSNotFound ) &&
			( headerStart.length > 0 ) && ( headerEnd.length > 0 ) )	{
		NSRange headerOpenFinish = [source rangeOfString: @">" 
					options: NSCaseInsensitiveSearch
					range: NSMakeRange(headerStart.location, [source length] - headerStart.location)];
		headerRange = NSMakeRange( headerOpenFinish.location + 1, headerEnd.location - headerOpenFinish.location - 1 );
	}
	else	{
		headerRange = NSMakeRange( 0, 0 );
	}

	NSRange bodyStart = [source rangeOfString: @"<body" options: NSCaseInsensitiveSearch];
	NSRange bodyEnd = [source rangeOfString: @"</body" options: NSCaseInsensitiveSearch];
	NSRange bodyRange;
	if (	( bodyStart.location != NSNotFound ) && ( bodyEnd.location != NSNotFound ) &&
			( bodyStart.length > 0 ) && ( bodyEnd.length > 0 ) )	{
		NSRange bodyOpenFinish = [source rangeOfString: @">" 
					options: NSCaseInsensitiveSearch
					range: NSMakeRange(bodyStart.location, [source length] - bodyStart.location)];
		bodyRange = NSMakeRange( bodyOpenFinish.location + 1, bodyEnd.location - bodyOpenFinish.location - 1 );
	}
	else if ( ( bodyStart.location != NSNotFound ) && ( bodyStart.length != NSNotFound ) )	{
		NSRange bodyOpenFinish = [source rangeOfString: @">" 
					options: NSCaseInsensitiveSearch
					range: NSMakeRange(bodyStart.location, [source length] - bodyStart.location)];
		bodyRange = NSMakeRange( bodyOpenFinish.location + 1, [source length] - bodyOpenFinish.location - 1 );
	}
	else	{
		bodyRange = NSMakeRange( 0, [source length] );
	}

	*headerTextL = [NSString stringWithString:[source substringWithRange: headerRange]];
	*bodyText = [NSString stringWithString:[source substringWithRange: bodyRange]];	
}



@end
