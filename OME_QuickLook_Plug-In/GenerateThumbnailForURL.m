#include <CoreFoundation/CoreFoundation.h>
#include <CoreServices/CoreServices.h>
#include <QuickLook/QuickLook.h>
#include <Cocoa/Cocoa.h>
//#include <OME/OME.h>
#include "OMEMessage.h"

/* -----------------------------------------------------------------------------
    Generate a thumbnail for file

   This function's job is to create thumbnail for designated file as fast as possible
   ----------------------------------------------------------------------------- */

OSStatus GenerateThumbnailForURL(
				void *thisInterface, 
				QLThumbnailRequestRef thumbnail, 
				CFURLRef url, 
				CFStringRef contentTypeUTI, 
				CFDictionaryRef options, 
				CGSize maxSize)
{
    NSAutoreleasePool* pool = [[NSAutoreleasePool alloc] init];
 
	NSString *thisPath = [(NSURL *)url path];
    OMEMessage *mailShow = [[OMEMessage alloc] initWithContentsOfFile: thisPath ];
	NSDictionary *mailFields = [mailShow fields];

	NSSize canvasSize = NSMakeSize( 128, 128);
    CGContextRef cgContext = QLThumbnailRequestCreateContext(thumbnail, *(CGSize *)&canvasSize, false, NULL);
    if(cgContext) {
        NSGraphicsContext* context = [NSGraphicsContext graphicsContextWithGraphicsPort:(void *)cgContext flipped:YES];
        if(context) {
			[NSGraphicsContext saveGraphicsState];
			[NSGraphicsContext setCurrentContext:context];
			[context saveGraphicsState];
			
			NSRect frameRect = NSMakeRect(0, 0, 128, 128);
			NSAffineTransform* xform = [NSAffineTransform transform];
			[xform translateXBy:0.0 yBy:frameRect.size.height];
			[xform scaleXBy:1.0 yBy:-1.0];
			[xform concat];

			NSColor *backColor;
			NSString *thisEx = [ thisPath pathExtension ];
			if ( [ thisEx caseInsensitiveCompare: @"ygm" ] == NSOrderedSame )
				backColor = [NSColor colorWithCalibratedRed:0.65 green:0.65 blue:0.65 alpha:0.6];
			else if ( [ thisEx caseInsensitiveCompare: @"mail" ] == NSOrderedSame )
				backColor = [NSColor colorWithCalibratedRed:0.66 green:0.69 blue:0.53 alpha:0.6];
			else if ( [ thisEx caseInsensitiveCompare: @"rply" ] == NSOrderedSame )
				backColor = [NSColor colorWithCalibratedRed:0.60 green:0.69 blue:0.60 alpha:0.6];
			else 
				backColor = [NSColor colorWithCalibratedRed:0.66 green:0.69 blue:0.53 alpha:0.6];
			[backColor set];
			NSRectFill(frameRect);

			NSColor *lineColor = [NSColor colorWithCalibratedRed:0.3 green:0.3 blue:0.3 alpha:0.6];
			[lineColor set];
			NSFrameRectWithWidth(NSMakeRect( 0, 0,128,128), 3);

			NSBezierPath *path = [NSBezierPath bezierPath];
			[path setLineWidth:2.0];
			[path moveToPoint:NSMakePoint( 0, 0 )];
			[path lineToPoint:NSMakePoint( 64, 50 )];
			[path lineToPoint:NSMakePoint( 128, 0 )];
			[path stroke];

			[[NSColor blackColor] set];
			NSRectFill(NSMakeRect( 66, 98, 70, 30));

			NSFont *fontOME = [NSFont fontWithName:@"Arial Bold" size: 24.0];
			NSColor *colorOME = [NSColor colorWithCalibratedRed:0.9 green:0.9 blue:0.9 alpha:1.0];
			NSDictionary *attrsDictionaryOME = [NSDictionary 
				dictionaryWithObjects: [NSArray arrayWithObjects: fontOME, colorOME, nil]
				forKeys: [NSArray arrayWithObjects: NSFontAttributeName ,NSForegroundColorAttributeName, nil]];
			NSAttributedString *strOME = [[NSAttributedString alloc] 
				initWithString: @"OME"
				attributes:attrsDictionaryOME];
			[strOME drawInRect:NSMakeRect(70, 100, 68, 28)];

			NSFont *font1 = [NSFont fontWithName:@"Hiragino Kaku Gothic Pro" size: 12.0];
			NSColor *color1 = [NSColor colorWithCalibratedRed:0.2 green:0.2 blue:0.2 alpha:1.0];
			NSDictionary *attrsDictionary1 = [NSDictionary 
				dictionaryWithObjects: [NSArray arrayWithObjects: font1, color1, nil]
				forKeys: [NSArray arrayWithObjects: NSFontAttributeName ,NSForegroundColorAttributeName, nil]];
			NSFont *font2 = [NSFont fontWithName:@"Hiragino Kaku Gothic Pro" size: 12.0];
			NSColor *color2 = [NSColor colorWithCalibratedRed:0.0 green:0.0 blue:0.5 alpha:1.0];
			NSDictionary *attrsDictionary2 = [NSDictionary 
				dictionaryWithObjects: [NSArray arrayWithObjects: font2, color2, nil]
				forKeys: [NSArray arrayWithObjects: NSFontAttributeName ,NSForegroundColorAttributeName, nil]];

			NSAttributedString *str1 = [[NSAttributedString alloc] 
				initWithString: [mailFields objectForKey: @"From"]
				attributes:attrsDictionary1];
			[str1 drawInRect:NSMakeRect(4,10,124,30)];
			NSAttributedString *str3 = [[NSAttributedString alloc] 
				initWithString: [mailFields objectForKey: @"Subject"]
				attributes:attrsDictionary2];
			[str3 drawInRect:NSMakeRect(4,54,124,124)];
			
			[NSGraphicsContext restoreGraphicsState];
        }
        QLThumbnailRequestFlushContext(thumbnail, cgContext);
        CFRelease(cgContext);
    }
    [pool release];
    return noErr;
}

void CancelThumbnailGeneration(void* thisInterface, QLThumbnailRequestRef thumbnail)
{
    // implement only if supported
}

