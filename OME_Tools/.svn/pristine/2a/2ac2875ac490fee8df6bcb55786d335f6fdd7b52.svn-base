/*
 *  speaking.c
 *  OME_Tools
 *
 *  Created by êVãè âÎçs on Sun Feb 03 2002.
 *  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
 *
 */

#include <Carbon/Carbon.h>

int main (int argc, const char * argv[]) {

    OSErr er;
    
    if(argc == 2)	{
        c2pstr(argv[1]);
        er = SpeakString(argv[1]);
        while(SpeechBusy() == 1)	{
            sleep(1);
        }
    }
    else	{
        printf("You must set one parameter which is speaking text.\n");
        er = 1;
    }
    return er;

}
