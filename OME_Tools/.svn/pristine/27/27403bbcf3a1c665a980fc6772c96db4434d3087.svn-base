//
//  SoundPlay.m
//  OME_Tools
//
//  Created by 新居 雅行 on Sun Feb 03 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#include <unistd.h>

int main (int argc, const char * argv[]) {
    OSErr er = 0;
    NSSound * theSound;
    NSString * soundName;
    NSString * argStr;
    NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];
    if(argc == 2)	{
        argStr = [NSString stringWithCString:(argv[1])];
        soundName = [NSString stringWithCString:(argv[1])];
        theSound = [NSSound soundNamed:soundName];
        [theSound play];
        while([theSound isPlaying])
            sleep(1);
    }
    else	{
        printf("You must set one parameter which is the sound name.\n");
        er = 1;
    }
    return er;
    [pool release];
}