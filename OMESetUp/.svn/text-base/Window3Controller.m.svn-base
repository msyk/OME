//
//  $Id: Window3Controller.m,v 1.4 2007/10/02 13:45:36 msyk Exp $
//
//  Window3Controller.m for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Fri Jul 22 16:56:44 2005 UTC
//  Copyright (c) 2005, Shinya Wakimoto. All rights reserved.
//

#import "Window3Controller.h"

#import "Definitions.h"

/* window3 */
#define WINDOW_NIB_NAME @"Window3"
#define WINDOW_FRAME_AUTOSAVE_NAME @"Window3"

/* format */
#define SIGN_FORMAT @"--\n%@\n%@\n"

NSString *OMEBehaviorInfoTxtKey = @"OMEBehaviorInfoTxtBundleKey";
NSString *OMESignatureKey = @"OMESignatureKey";

@implementation Window3Controller

/* accessor method */
- (void)setDefaultSignature:(NSString *)aString
{
    [ aString retain ];
    [ defaultSignature release ];
    defaultSignature = aString;
}

/* accessor method */
- (NSString *)defaultSignature
{
    return defaultSignature;
}

/* designated initializer */
- (id)init
{
    self = [ super initWithWindowNibName:WINDOW_NIB_NAME ];
    if ( self ) {
        [ self setWindowFrameAutosaveName:WINDOW_FRAME_AUTOSAVE_NAME ];
    }
    return self;
}

/* show window3 */
- (IBAction)showWindow:(id)sender
{
    NSString *aName, *anAddress, *aString;
    OMESMTPServer *smtpServer;

    smtpServer = [ sender smtpServer ];
    aName = [ smtpServer name ];
    anAddress = [ smtpServer address ];
    aString = [ NSString stringWithFormat:SIGN_FORMAT, aName, anAddress ];
    if ( sign ) {
        [ sign setString:aString ];
    } else {
        [ self setDefaultSignature:aString ];
    }
    [ super showWindow:sender ];
}

/* Window3.nib is loaded. */
- (void)windowDidLoad
{
    [ sign setString:[ self defaultSignature ] ];
}

/* back to window2 */
- (IBAction)showWindow2:(id)sender
{
    NSNotificationCenter *notificationCenter;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    [ notificationCenter
        postNotificationName:SHOW_WINDOW2
        object:self
        userInfo:nil ];
    [ self close ];
}

/* local method */
- (NSString *)behaviorInfoTxt
{
    NSBundle *aBundle;
    NSString *aString;

    /* main bundle */
    aBundle = [ NSBundle mainBundle ];
    /* Behavior_Info.txt */
    if ( [ isSaveOnServer state ] ) {
        aString = @"Behavior_Info1";
    } else {
        aString = @"Behavior_Info2";
    }
    return [ aBundle pathForResource:aString ofType:@"txt" ];
}

/* local method */
- (NSString *)signature
{
    return [ sign string ];
}

/* forward to  window4 */
- (IBAction)showWindow4:(id)sender
{
    NSDictionary *aDictionary;
    NSNotificationCenter *notificationCenter;
    NSString *behaviorInfoTxt, *signature;

    notificationCenter = [ NSNotificationCenter defaultCenter ];
    behaviorInfoTxt = [ self behaviorInfoTxt ];
    signature = [ self signature ];
    aDictionary = [ NSDictionary
                      dictionaryWithObjectsAndKeys:
                      behaviorInfoTxt, OMEBehaviorInfoTxtKey,
                      signature, OMESignatureKey,
                      nil ];
    [ notificationCenter
        postNotificationName:SHOW_WINDOW4
        object:self
        userInfo:aDictionary ];
    [ self close ];
}

/* dealloc */
- (void)dealloc
{
    [ defaultSignature release ];
    [ super dealloc ];
}

@end
