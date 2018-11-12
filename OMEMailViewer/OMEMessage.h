//
//  OMEMessage.h
//  OME
//
//  Created by MURAKAMI, Yukio on 05/06/10.
//  Copyright 2005 Bitz Co., Ltd. All rights reserved.
//
//  Modified by NII, Masayuki after that.

//  Change History:
//
//  2005/09/11  msyk added
//              - (id)initWithData:
//              + (NSStringEncoding)analizeCharacterSet: message:
//              - (NSDictionary*)fields
//              - (NSString*)text
//  2005/09/19  merge
//  2007/09/26  msyk modified a lot of codes, adapted to multi-file mail.
//

#import <Foundation/Foundation.h>

//#import <OME/OME.h>
//#import <OgreKit/OgreKit.h>

#define DefaultCSSFileName @"OMEMailViewer.css"
#define DefaultCSSFileNamePrint @"OMEMailViewerPrint.css"

/*!
	@class       OMEMessage 
	@superclass  NSObject
	@abstract    OMEの受信メールファイルの内容を処理したいときに、メールファイルをオブジェクトとして扱えるようにするクラス。
	@discussion  OMEの受信メールファイルの内容を処理したいときに、メールファイルをオブジェクトとして扱えるようにするクラス。
*/
@interface OMEMessage : NSObject {
	NSMutableString		*message;
	NSMutableDictionary	*fields;
	NSMutableString		*text;
	NSMutableDictionary	*realMailHeaders;
	NSMutableString		*realHeadersString;
	NSStringEncoding	strEnc;
	BOOL				isMultiPart;
	NSArray				*partInfo;
	NSString			*targetFilePath;
	NSString			*parentPath;
	BOOL				hasCertificate;
	int					partInfoVersion;

//	OGRegularExpression *regexURL;
//	OGRegularExpression *regexMailAddress;
}
/*! 
 @method analizeCharacterSet:message: 
 @abstract メッセージのエンコード情報を得る
 @discussion キャラクタセットから、Cocoaでのエンコード情報を得る。キャラクタセットを指定しない場合、
	CJKV日中韓越情報処理「9.5.1 日本語符号化方式の検出」に基づき、日本語の文字列の文字コード（エンコード体系）を検出する。
 @param charset メールファイルから得られるキャラクタセット
 @param messageData メールのメッセージ
 @result エンコード情報
*/
+ (NSStringEncoding)analizeCharacterSet:(NSString *)charset message:(NSData *)messageData;

/*!
    @method     init
    @abstract   <#(brief description)#>
    @discussion <#(comprehensive description)#>
    @result     <#(description)#>
*/
- (id)init;
- (id)initWithContentsOfFile:(NSString *)pathToFile;
- (id)initWithContentsOfURL:(NSURL *)url;
- (id)initWithData:(NSData *)messageData;
//- (void)dealloc;
- (NSDictionary*)fields;
- (NSDictionary*)realMailHeaders;
- (NSString*)realHeadersString;
- (NSString*)text;
- (void)setMetadataAttributes:(NSMutableDictionary *)attributes;
- (NSString *)stringForHeader:(NSString *)headerName;
- (NSArray *)partInfo;
- (BOOL)isMultiPartMail;
- (NSString *)fileContents:(NSString **)fileName 
		encoding:(NSStringEncoding)enc topHeaders:(NSString **)topHeaders 
		realHeaders:(NSString **)realHeaders partHeaders:(NSString **)partHeaders;
- (NSString *)parentPath;
+ (NSString*) getNextLineString:(NSString *)str;
- (NSArray *)incorporateFromExistingFiles;
- (NSArray *)incorporateFromPartInfoFile;
- (NSStringEncoding)predictTheCharacterSet:(NSString *)filePath;
- (NSString *)preferedMIMETypeFromFile:(NSString *)targetFile;
- (BOOL)hasCertificate;
- (NSString *)htmlWellFormedString;
- (NSString *)htmlFormedString:(NSString *)cssFileNameForDisplay printCSS:(NSString *)cssFileNameForPrint;
- (NSString *)createLinkToFile:(NSString *)fileName inDirectory:(NSString *)fileParent;
- (NSString *)createMailHeaderHTML:(NSString *)value label:(NSString *)label idstring:(NSString *)idstring;
- (NSString *)linkedHTML:(NSString *)source;
- (void)getOutHeaderAndBodyFromHTML:(NSString *)source headerText:(NSString **)headerTextL bodyText:(NSString **)bodyText;

@end
