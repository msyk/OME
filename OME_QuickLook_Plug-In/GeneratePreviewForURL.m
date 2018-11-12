#include <CoreFoundation/CoreFoundation.h>
#include <CoreServices/CoreServices.h>
#include <QuickLook/QuickLook.h>
#include <Cocoa/Cocoa.h>
#include <WebKit/WebKit.h>
//#include <OME/OME.h>
#include "OMEMessage.h"

/* -----------------------------------------------------------------------------
   Generate a preview for file

   This function's job is to create preview for designated file
   ----------------------------------------------------------------------------- */

OSStatus GeneratePreviewForURL(
	void *thisInterface, QLPreviewRequestRef preview, 
	CFURLRef url, CFStringRef contentTypeUTI, CFDictionaryRef options)
{
    NSAutoreleasePool* pool = [[NSAutoreleasePool alloc] init];
 
    OMEMessage* mailShow = [[OMEMessage alloc] initWithContentsOfFile: [(NSURL *)url path] ];
	NSMutableDictionary *props = [[[NSMutableDictionary alloc] init] autorelease];
	[props setObject:@"UTF-8" forKey:(NSString *)kQLPreviewPropertyTextEncodingNameKey];
	[props setObject:@"text/html" forKey:(NSString *)kQLPreviewPropertyMIMETypeKey];

	QLPreviewRequestSetDataRepresentation(
		preview,
		(CFDataRef)[[mailShow htmlWellFormedString] dataUsingEncoding:NSUTF8StringEncoding],
		kUTTypeHTML,
		(CFDictionaryRef)props);
//		CFTypeID xx = QLPreviewRequestGetTypeID();

    [pool release];
    return noErr;
}

void CancelPreviewGeneration(void* thisInterface, QLPreviewRequestRef preview)
{
    // implement only if supported
}
