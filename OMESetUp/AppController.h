//
//  $Id: AppController.h,v 1.9 2007/10/02 13:45:36 msyk Exp $
//
//  AppController.h for OMESetUp
//
//  Created by Shinya Wakimoto.
//  Revision 1.1, Sat Dec 18 11:19:09 2004 UTC
//  Copyright (c) 2004 - 2005, Shinya Wakimoto. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import <OME/OME.h>

@class Window1Controller;
@class WarningController;
@class Window2Controller;
@class Window3Controller;
@class Window4Controller;
@class FinishController;
@class OMEPOPServer;
@class OMESMTPServer;

@interface AppController : NSObject
{
    /**** Window1 ****/
    Window1Controller *window1Controller;
    /* WarningController */
    WarningController *warningController;
    /**** Window2 ****/
    Window2Controller *window2Controller;
    /**** Window3 ****/
    Window3Controller *window3Controller;
    /**** Window4 ****/
    Window4Controller *window4Controller;
    /* FinishController */
    FinishController *finishController;
    /* local instances */
    OMEPOPServer *popServer;
    OMESMTPServer *smtpServer;
    NSString *behaviorInfoTxt;
    NSString *signature;
    NSString *mailBoxPath;
}

@end
