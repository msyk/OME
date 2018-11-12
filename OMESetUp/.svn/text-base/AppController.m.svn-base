//
//  $Id: AppController.m,v 1.15 2007/10/02 13:45:36 msyk Exp $
//
//  AppController.m for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sat Dec 18 11:19:09 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import "AppController.h"

#import "Window1Controller.h"
#import "WarningController.h"
#import "Window2Controller.h"
#import "Window3Controller.h"
#import "Window4Controller.h"
#import "FinishController.h"

#import "Definitions.h"

@implementation AppController

/* accessor method */
- (void)setPOPServer:(OMEPOPServer *)omePOPServer
{
    [ omePOPServer retain ];
    [ popServer release ];
    popServer = omePOPServer;
}

/* accessor method */
- (OMEPOPServer *)popServer
{
    return popServer;
}

/* accessor method */
- (void)setSMTPServer:(OMESMTPServer *)omeSMTPServer
{
    [ omeSMTPServer retain ];
    [ smtpServer release ];
    smtpServer = omeSMTPServer;
}

/* accessor method */
- (OMESMTPServer *)smtpServer
{
    return smtpServer;
}

/* accessor method */
- (void)setBehaviorInfoTxt:(NSString *)aString
{
    [ aString retain ];
    [ behaviorInfoTxt release ];
    behaviorInfoTxt = aString;
}

/* accessor method */
- (NSString *)behaviorInfoTxt
{
    return behaviorInfoTxt;
}

/* accessor method */
- (void)setSignature:(NSString *)aString
{
    [ aString retain ];
    [ signature release ];
    signature = aString;
}

/* accessor method */
- (NSString *)signature
{
    return signature;
}

/* accessor method */
- (void)setMailBoxPath:(NSString *)aString
{
    [ aString retain ];
    [ mailBoxPath release ];
    mailBoxPath = aString;
}

/* accessor method */
- (NSString *)mailBoxPath
{
    return mailBoxPath;
}

/* show window1 */
- (void)showWindow1:(NSNotification *)aNotification
{
    if ( window1Controller == nil ) {
        window1Controller = [ [ Window1Controller alloc ] init ];
    }
    [ window1Controller showWindow:self ];
}

/* show warning panel */
- (void)showWarningPanel:(NSNotification *)aNotification
{
    if ( warningController == nil ) {
        warningController = [ [ WarningController alloc ] init ];
    }
    [ warningController showWindow:self ];
}

/* show windowd2 */
- (void)showWindow2:(NSNotification *)aNotification
{
    if ( window2Controller == nil ) {
        window2Controller = [ [ Window2Controller alloc ] init ];
    }
    [ window2Controller showWindow:self ];
}

/* show windowd3 */
- (void)showWindow3:(NSNotification *)aNotification
{
    NSDictionary *aDictionary;
    OMEPOPServer *omePOPServer;
    OMESMTPServer *omeSMTPServer;

    /* Set popServer & smtpServer instances */
    aDictionary = [ aNotification userInfo ];
    if ( aDictionary ) {
        omePOPServer = [ aDictionary objectForKey:OMEPOPServerKey ];
        [ self setPOPServer:omePOPServer ];
        omeSMTPServer = [ aDictionary objectForKey:OMESMTPServerKey ];
        [ self setSMTPServer:omeSMTPServer ];
    }
    /* Show Window3 */
    if ( window3Controller == nil ) {
        window3Controller = [ [ Window3Controller alloc ] init ];
    }
    [ window3Controller showWindow:self ];
}

/* show window4 */
- (void)showWindow4:(NSNotification *)aNotification
{
    NSDictionary *aDictionary;
    NSString *aString;

    /* Set behaviorInfoTxt & signature instances */
    aDictionary = [ aNotification userInfo ];
    if ( aDictionary ) {
        aString = [ aDictionary objectForKey:OMEBehaviorInfoTxtKey ];
        [ self setBehaviorInfoTxt:aString ];
        aString = [ aDictionary objectForKey:OMESignatureKey ];
        [ self setSignature:aString ];
    }
    /* Show Window4 */
    if ( window4Controller == nil ) {
        window4Controller = [ [ Window4Controller alloc ] init ];
    }
    [ window4Controller showWindow:self ];
}

/* local method */
- (void)doConfigurate
{
    int i;
    NSFileManager *fileManager;
    NSString *aPath, *omePreferences;
    OMEAlias *anAlias;
    OMEPaths *omePaths;

    /* default NSFileManager */
    fileManager = [ NSFileManager defaultManager ];
    /* check OME_Preferences */
    omePreferences = [ OMEPaths omePreferences ];
    i = 0;
    aPath = omePreferences;
    while ( [ fileManager fileExistsAtPath:aPath ] ) {
        i += 1;
        aPath = [ omePreferences stringByAppendingFormat:@".%d", i ];
    }
    if ( i > 0 ) {
        [ fileManager movePath:omePreferences toPath:aPath handler:nil ];
    } 
    [ fileManager createDirectoryAtPath:omePreferences attributes:nil ];
    /* MailBox */
    if ( [ fileManager fileExistsAtPath:[ self mailBoxPath ] ] == NO ) {
        [ fileManager
            createDirectoryAtPath:[ self mailBoxPath ] attributes:nil ];
    }
    /* create OMEPaths instance */
    omePaths = [ [ OMEPaths alloc ] initWithMailBox:[ self mailBoxPath ] ];
    /* Receive_Info.txt */
    aPath = [ omePaths receiveInfoTxt ];
    [ [ self popServer ] writeToFile:aPath ];
    /* Sender_Info.txt */
    aPath = [ omePaths senderInfoTxt ];
    [ [ self smtpServer ] writeToFile:aPath ];
    /* Behavior_Info.txt */
    aPath = [ omePaths behaviorInfoTxt ];
    [ fileManager copyPath:[ self behaviorInfoTxt ] toPath:aPath handler:nil ];
    /* Signature.txt */
    aPath = [ omePaths signatureTxt ];
    [ [ self signature ] writeToFile:aPath atomically:YES ];
    /* movingInfo.txt */
    aPath = [ omePaths movingInfoTxt ];
    [ fileManager createFileAtPath:aPath contents:nil attributes:nil ];
    /* OME_Root_Path */
    aPath = [ omePaths omeRootPath ];
    [ [ self mailBoxPath ] writeToFile:aPath atomically:YES ];
    /* OME_Root */
    aPath = [ omePaths omeRoot ];
    anAlias = [ [ OMEAlias alloc ]
                  initWithSource:[ self mailBoxPath ] alias:aPath ];
    [ anAlias makeNewAlias ];
    [ anAlias release ];
    /* Draft */
    aPath = [ omePaths draft ];
    if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
        [ fileManager createDirectoryAtPath:aPath attributes:nil ];
    }
    /* InBox */
    aPath = [ omePaths inBox ];
    if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
        [ fileManager createDirectoryAtPath:aPath attributes:nil ];
    }
    /* OutBox */
    aPath = [ omePaths outBox ];
    if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
        [ fileManager createDirectoryAtPath:aPath attributes:nil ];
    }
    /* Sent */
    aPath = [ omePaths sent ];
    if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
        [ fileManager createDirectoryAtPath:aPath attributes:nil ];
    }
    /* temp */
    aPath = [ omePaths temp ];
    if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
        [ fileManager createDirectoryAtPath:aPath attributes:nil ];
    }
    /* Archives */
    aPath = [ omePaths archives ];
    if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
        [ fileManager createDirectoryAtPath:aPath attributes:nil ];
    }
    /* hide */
    aPath = [ omePaths hide ];
    if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
        [ fileManager createDirectoryAtPath:aPath attributes:nil ];
    }
    /* logs */
    aPath = [ omePaths logs ];
    if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
        [ fileManager createDirectoryAtPath:aPath attributes:nil ];
    }
    /* unreadAliases */
    aPath = [ omePaths unreadAliases ];
    if ( [ fileManager fileExistsAtPath:aPath ] == NO ) {
        [ fileManager createDirectoryAtPath:aPath attributes:nil ];
    }
    /* release instance */
    [ omePaths release ];
}

/* show finish panel */
- (void)showFinishPanel:(NSNotification *)aNotification
{
    NSDictionary *aDictionary;
    NSString *aPath;

    /* Set mailBoxPath instances */
    aDictionary = [ aNotification userInfo ];
    if ( aDictionary ) {
        aPath = [ aDictionary objectForKey:OMEMailBoxPathKey ];
        [ self setMailBoxPath:aPath ];
    }
    /* set up OME files */
    [ self doConfigurate ];
    /* show FinishPanel */
    if ( finishController == nil ) {
        finishController = [ [ FinishController alloc ] init ];
    }
    [ finishController showWindow:self ];
}

/* Delegate Method to NSApp*/
- (void)applicationDidFinishLaunching:(NSNotification *)aNotification
{
    NSNotificationCenter *notificationCenter;

    /* set up notification */
    notificationCenter = [ NSNotificationCenter defaultCenter ];
    [ notificationCenter
        addObserver:self
        selector:@selector(showWindow1:)
        name:SHOW_WINDOW1
        object:nil ];
    [ notificationCenter
        addObserver:self
        selector:@selector(showWarningPanel:)
        name:SHOW_WARNING_PANEL
        object:nil ];
    [ notificationCenter
        addObserver:self
        selector:@selector(showWindow2:)
        name:SHOW_WINDOW2
        object:nil ];
    [ notificationCenter
        addObserver:self
        selector:@selector(showWindow3:)
        name:SHOW_WINDOW3
        object:nil ];
    [ notificationCenter
        addObserver:self
        selector:@selector(showWindow4:)
        name:SHOW_WINDOW4
        object:nil ];
    [ notificationCenter
        addObserver:self
        selector:@selector(showFinishPanel:)
        name:SHOW_FINISH_PANEL
        object:nil ];
    /* show window1 */
    [ self showWindow1:nil ];
}

/* delegate method of NSApplication */
- (void)applicationWillTerminate:(NSNotification *)aNotification
{
    NSNotificationCenter *notificationCenter;

    /* remove notification observer */
    notificationCenter = [ NSNotificationCenter defaultCenter ];
    [ notificationCenter removeObserver:self ];
    /* release NSWindowController instances */
    [ window1Controller release ];
    [ warningController release ];
    [ window2Controller release ];
    [ window3Controller release ];
    [ window4Controller release ];
    [ finishController release ];
    /* release local instances */
    [ popServer release ];
    [ smtpServer release ];
    [ behaviorInfoTxt release ];
    [ signature release ];
    [ mailBoxPath release ];
}

@end
