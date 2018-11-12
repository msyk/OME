/*
 *  AliasResolver.c
 *  OME_Tools
 *
 *  Created by 新居 雅行 on Sun Jan 27 2002.
 *  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
 *
 2006/6/17 by msyk /  Adopt Intel CPU and Mac OS X Tiger.
 */

#include <Carbon/Carbon.h>

#define finish if(er != noErr){printf("ERROR!");goto procEnd;}

int main(int argc, char *argv[])
{
    OSStatus er = 0;
    FSRef targetRef;
//    FSSpec targetSpec;
    UInt8* resultPath;
//    UInt32 size;
    Boolean b1, b2;
    FSCatalogInfoBitmap infoBit;
    FSCatalogInfo catInfo;
	CFStringRef tempStr;
	CFRange rangeToProcess;
	CFIndex usedBufferLength, numChars;
	UInt8	localBuffer[512];
    
    if(argc > 1)	{
//        printf("%s\n", argv[1]);

        er = FSPathMakeRef ((unsigned char *)argv[1], &targetRef, NULL);
		if ( er != noErr )	{
			tempStr = CFStringCreateWithCString(NULL, argv[1], kCFStringEncodingMacJapanese);
			rangeToProcess = CFRangeMake(0, CFStringGetLength(tempStr));

			numChars = CFStringGetBytes( tempStr, rangeToProcess, kCFStringEncodingUTF8, 
											'?', FALSE, (UInt8*)localBuffer, 512, &usedBufferLength);
			if ( numChars == 0 )	finish;
			localBuffer[usedBufferLength] = 0;
			er = FSPathMakeRef (localBuffer, &targetRef, NULL);	finish;
		}

		/*
		Javaからコマンドラインに送られる引数は、Shift JISになっている（Java 1.5から）。そのまま送ると当然エラーになるが、ここでは
		将来のことも考えて、得られた引数をパスとして解釈してエラーが出たら、Shift JISとみなして変換をして、それでもエラーなら
		エラーになるようにしてみた。
		*/

        infoBit = kFSCatInfoFinderInfo;
        er = FSGetCatalogInfo (&targetRef, infoBit, &catInfo, NULL, NULL, NULL); finish;
        
		// This part depends on endian.
		UInt16 finderFlag;
		if ( CFByteOrderGetCurrent() == CFByteOrderLittleEndian )
			finderFlag = catInfo.finderInfo[9] * 256 + catInfo.finderInfo[8];
		else
			finderFlag = catInfo.finderInfo[8] * 256 + catInfo.finderInfo[9];
			
//		printf("finderFlag: %d %d\n", finderFlag, kIsAlias);
        if ( finderFlag & kIsAlias == 0)   {   //引数のファイルはエイリアスではない
            printf("%s", argv[1]);  //引数のパスをそのまま戻す
        }
        else    {   //引数のファイルはエイリアス
            //er = ResolveAliasFile (&targetSpec, TRUE, &b1, &b2);    finish;
			er = FSResolveAliasFile (&targetRef, TRUE, &b1, &b2);    finish;
            //er = FSpMakeFSRef (&targetSpec, &targetRef);            finish;
            resultPath = (UInt8*)NewPtr(1024);
            er = FSRefMakePath (&targetRef, resultPath, 1024);      finish;
            printf("%s", resultPath);
            DisposePtr((Ptr)resultPath);
        }
    }
procEnd:
    return er;
}