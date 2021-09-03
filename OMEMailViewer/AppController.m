#import "AppController.h"

@implementation AppController

- (void)awakeFromNib
{

	float leftExtraMargin = 30.0;

	NSPrintInfo *currentPrintInfo = [NSPrintInfo sharedPrintInfo];
	
	NSRect maxBounds = [currentPrintInfo imageablePageBounds];
	NSSize paperSize = [currentPrintInfo paperSize];
//	float bottomM = [currentPrintInfo bottomMargin];
//	float topM = [currentPrintInfo topMargin];
//	float leftM = [currentPrintInfo leftMargin];
//	float rightM = [currentPrintInfo rightMargin];
	[currentPrintInfo  setBottomMargin: maxBounds.origin.y];
	[currentPrintInfo  setTopMargin: paperSize.height-maxBounds.size.height-maxBounds.origin.y];
	[currentPrintInfo  setLeftMargin: maxBounds.origin.x + leftExtraMargin];
	[currentPrintInfo  setRightMargin: paperSize.width-maxBounds.size.width-maxBounds.origin.x];
	[currentPrintInfo  setHorizontalPagination: NSClipPagination];
	[currentPrintInfo  setVerticalPagination: NSAutoPagination];

}


@end
