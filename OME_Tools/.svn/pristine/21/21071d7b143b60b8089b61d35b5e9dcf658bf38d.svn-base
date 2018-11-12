/*
 *  PPPDisconnect.c
 *  OME_Tools
 *
 *  Created by êVãè âÎçs on Sat Feb 02 2002.
 *  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
 *
 */

#include <stdio.h>
#include <Carbon/Carbon.h>

int main (int argc, const char * argv[]) {

    TEndpointInfo  epInfo;
    OSStatus       err;
    EndpointRef    ep;
    OTClientContextPtr outClientContext;
    Boolean portValid;
    SInt32 portIndex;
    OTPortRecord portRecord;
    UInt16 deviceType;

    Boolean debug = false;
    
    if(argc > 1)	debug = true;

    err = InitOpenTransportInContext(kInitOTForApplicationMask, &outClientContext);

    if(debug)	printf(" Error of InitOpenTransportInContext is %li.\n",err);

    if(debug)	{
        portIndex = 0;
        err = kOTNoError;
        do {
            portValid = OTGetIndexedPort(&portRecord, portIndex);
            if (portValid) {
                ep = OTOpenEndpointInContext(
                        OTCreateConfiguration(portRecord.fPortName), 0, &epInfo, &err, outClientContext);
                printf(" Error of OTOpenEndpointInContext is %li.\n",err);
                printf(" String to pass to OTCreateConfiguration is %s.\n",portRecord.fPortName);
                printf(" Name of provider module is %s.\n",portRecord.fModuleName);
                printf(" Devide Type is %li.\n",OTGetDeviceTypeFromPortRef(portRecord.fRef));
                printf(" discon of End point info is $%08lx.\n",epInfo.discon);
                printf("-\n");
                err = OTCloseProvider(ep);
            }
            portIndex += 1;
        } while ( portValid );
    }
    
    ep = OTOpenEndpointInContext(OTCreateConfiguration("ppp"), 0L, &epInfo, &err, outClientContext);
    if(debug)	{
        printf("'ppp' - Error of OTOpenEndpointInContext is %i.\n",err);
        printf("'ppp' - discon of End point is $%08lx.\n",epInfo.discon);
    }
    err = OTIoctl(ep, I_OTDisconnect, NULL);
    //err = OTSndOrderlyDisconnect(ep);
    if(debug)
        printf("'ppp' - Disconnect err: %li\n", err);
    err = OTCloseProvider(ep);


    return err;
}


