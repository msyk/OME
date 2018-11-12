/*
 *  updatefinder.c
 *  OME_Tools
 *
 *  Created by êVãè âÎçs on Tue Jun 17 2003.
 *  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
 
 ÉRÉ}ÉìÉhÅFupdatefinder file...
 
 à¯êîÇ…éwíËÇÇµÇΩÉtÉ@ÉCÉãÇ†ÇÈÇ¢ÇÕÉtÉHÉãÉ_Ç…ä÷ÇµÇƒÅAFinderÇÃï\é¶ÉEÉCÉìÉhÉEÇÉAÉbÉvÉfÅ[ÉgÇ∑ÇÈÅB
 ÇΩÇ∆Ç¶ÇŒÅAçÏê¨ÇµÇΩÉtÉ@ÉCÉãÇ†ÇÈÇ¢ÇÕÇªÇÃÉtÉ@ÉCÉãÇ™Ç†ÇÈÉtÉHÉãÉ_Çà¯êîÇ…ÇµÇƒåƒÇ—èoÇ∑Ç∆ÅA
 ÇªÇÃçÄñ⁄Ç™ï\é¶Ç≥ÇÍÇƒÇ¢ÇÈÉEÉCÉìÉhÉEÇ™îwå„Ç…Ç†Ç¡ÇƒÇ‡ÉAÉbÉvÉfÅ[ÉgÇ≥ÇÍÇƒÅAÉtÉ@ÉCÉãÇÃë∂ç›Ç™
 FinderÇ≈ämîFÇ≈Ç´ÇÈÅB
 
 ==============
 
 AppleÇÃÉTÉìÉvÉãÉRÅ[ÉhÅuMoreAppleEventsÅvÇóòópÅBàÍïîÇÃä÷êîÇæÇØÇégÇ¡ÇƒÇ¢ÇÈÇ™ÅA
 ÇπÇ¡Ç©Ç≠Ç»ÇÃÇ≈ÅAMoreAppleEventsÇÃÇ∑Ç◊ÇƒÇÃÉvÉçÉOÉâÉÄÉ\Å[ÉXÇÅAÉvÉçÉWÉFÉNÉgÇ…ä‹ÇﬂÇÈÅB
 
 MoreAppleEvents
 http://developer.apple.com/samplecode/Sample_Code/Interapplication_Comm/MoreAppleEvents.htm

 *
 */

#include <Carbon/Carbon.h>

int main(int argc, char *argv[])
{
    int rVal, i;
    FSRef targetRef;
    
    if ( argc <= 1 )	{
        printf("Usage: updatefinder file...\n");
        rVal = -1;
    }
    else	{
        for ( i = 1 ; i < argc ; i++)	{
            rVal = FSPathMakeRef ((unsigned char *) argv[i] , &targetRef, NULL );
            rVal = MoreFEUpdateItemFSRef(&targetRef);
        }
    }
    return rVal;
}