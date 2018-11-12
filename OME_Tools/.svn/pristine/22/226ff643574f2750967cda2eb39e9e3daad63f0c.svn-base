/*
 *  mergeres.c
 *  OME_Tools
 *
 *  Created by 新居 雅行 on Fri Oct 11 2002.
 *  Copyright (c) 2002 __MyCompanyName__. All rights reserved.
 *
http://users.phg-online.de/tk/netatalk/doc/Apple/v1/

 */

#include <Carbon/Carbon.h>
#include <CoreServices/CoreServices.h>
#include <stdio.h>

int main(int argc, char *argv[]);
OSErr MergeResource (FSRef *targetFile, FSRef *resFile);

int main(int argc, char *argv[])
{
    OSStatus	er;
    FSRef	resRef, targetRef;
    CFStringRef tempStr;
	CFRange rangeToProcess;
	CFIndex usedBufferLength, numChars;
	UInt8	localBuffer[512];

    if( argc != 3 )	{
        er = -1;
        printf("Usage: mergeres TargetFile ResoureceFile\n");
    }
    else	{
		printf("OME:mergeres:\nTarget File:%s\nResrouce File:%s\n", argv[1], argv[2]);

		er = FSPathMakeRef ((unsigned char *)argv[1], &targetRef, NULL);
        if(er != noErr)	{
			tempStr = CFStringCreateWithCString(NULL, argv[1], kCFStringEncodingMacJapanese);
			rangeToProcess = CFRangeMake(0, CFStringGetLength(tempStr));

			numChars = CFStringGetBytes( tempStr, rangeToProcess, kCFStringEncodingUTF8, 
											'?', FALSE, (UInt8*)localBuffer, 512, &usedBufferLength);
			if ( numChars == 0 )	{
				er = 10021;	goto procEnd;
			}
			localBuffer[usedBufferLength] = 0;
			er = FSPathMakeRef (localBuffer, &targetRef, NULL);
			printf("OME:mergeres:Target To(Altanete):%s\n",localBuffer);
			if(er != noErr)	{
				printf("Error %d in making FSRef from argv[1] by FSPathMakeRef.\n", (int)er);
				goto procEnd;
			}
		}

        er = FSPathMakeRef ((unsigned char *)argv[2], &resRef, NULL);
        if(er != noErr)	{
			tempStr = CFStringCreateWithCString(NULL, argv[2], kCFStringEncodingMacJapanese);
			rangeToProcess = CFRangeMake(0, CFStringGetLength(tempStr));

			numChars = CFStringGetBytes( tempStr, rangeToProcess, kCFStringEncodingUTF8, 
											'?', FALSE, (UInt8*)localBuffer, 512, &usedBufferLength);
			if ( numChars == 0 )	{
				er = 10021;	goto procEnd;
			}
			localBuffer[usedBufferLength] = 0;
			er = FSPathMakeRef (localBuffer, &resRef, NULL);
			printf("OME:mergeres:Resourse File(Altanete):%s\n",localBuffer);
			if(er != noErr)	{
				printf("Error %d in making FSRef from argv[2] by FSPathMakeRef.\n", (int)er);
				goto procEnd;
			}
		}

        er = MergeResource (&targetRef, &resRef);
			if(er != noErr)	{
				printf("Error %d in Resourse Merging.\n", (int)er);
				goto procEnd;
			}
		
		er = FSDeleteObject( &resRef );
    }

procEnd:
    return (int)er;
}

OSErr MergeResource (FSRef *targetFile, FSRef *resFile) {
    OSErr	er;
    SInt16	targetRefNum, resRefNum;
    SInt32	magicNumber, versionNumber, entryID, offset, length;
    SInt32	createDate, modDate, backupDate, accessDate;
    SInt64 bias;
    SInt16	numberOfEntries;
    HFSUniStr255 resourceForkName, dataForkName;
    ByteCount actualCount;
    Ptr	buffer;
    int i, j;
    FSCatalogInfo catalogInfo;

    er = FSGetResourceForkName ( &resourceForkName );
                if (er != noErr)	goto bail;
    er = FSGetDataForkName ( &dataForkName );
                if (er != noErr)	goto bail;

    er = FSOpenFork ( resFile, dataForkName.length, dataForkName.unicode, 
            fsCurPerm, &resRefNum);
                if (er != noErr)	goto bail;

    er = FSReadFork ( resRefNum, fsFromStart, 0L, 4, &magicNumber, &actualCount);
	if (er != noErr)	goto finishWork;
	magicNumber = CFSwapInt32( magicNumber );
	
    er = FSReadFork ( resRefNum, fsFromStart, 4L, 4, &versionNumber, &actualCount);
	if (er != noErr)	goto finishWork;
	versionNumber = CFSwapInt32( versionNumber );
	
    er = FSReadFork ( resRefNum, fsFromStart, 24L, 2, &numberOfEntries, &actualCount);
	if (er != noErr)	goto finishWork;
	numberOfEntries = CFSwapInt16( numberOfEntries );

    for ( i = 0 ; i < numberOfEntries ; i++ )	{
        er = FSReadFork ( resRefNum, fsFromStart, 26+i*12, 4, &entryID, &actualCount);
		if (er != noErr)	goto finishWork;
		entryID = CFSwapInt32( entryID );
		
        er = FSReadFork ( resRefNum, fsFromStart, 26+i*12+4, 4, &offset, &actualCount);
		if (er != noErr)	goto finishWork;
		offset = CFSwapInt32( offset );
		
        er = FSReadFork ( resRefNum, fsFromStart, 26+i*12+8, 4, &length, &actualCount);
		if (er != noErr)	goto finishWork;
		length = CFSwapInt32( length );
		
        switch(entryID)	{
            case 2:		//Resource Fork
                printf("OME#mergeres - Merge the resource fork.\n");
                buffer = NewPtr(length);
                er = FSReadFork ( resRefNum, fsFromStart, offset, length, buffer, &actualCount);
                            if (er != noErr)	goto finishWork;
                er = FSOpenFork ( targetFile, 
                        resourceForkName.length, resourceForkName.unicode, 
                        fsRdWrPerm, &targetRefNum);
                            if (er != noErr)	goto bail;
                er = FSWriteFork ( targetRefNum, fsFromStart, 0L, length, buffer, &actualCount);
                            if (er != noErr)	goto finishWork;
                er = FSCloseFork ( targetRefNum );
                            if (er != noErr)	goto finishWork;
                break;
            case 8:	//時刻
                printf("OME#mergeres - Set any date informations.\n");
                er = FSReadFork ( resRefNum, fsFromStart, offset, 4, &createDate, &actualCount);
				if (er != noErr)	goto finishWork;
				createDate = CFSwapInt32( createDate );

                er = FSReadFork ( resRefNum, fsFromStart, offset+4, 4, &modDate, &actualCount);
				if (er != noErr)	goto finishWork;
				modDate = CFSwapInt32( modDate );

                er = FSReadFork ( resRefNum, fsFromStart, offset+8, 4, &backupDate, &actualCount);
				if (er != noErr)	goto finishWork;
				backupDate = CFSwapInt32( backupDate );

                er = FSReadFork ( resRefNum, fsFromStart, offset+12, 4, &accessDate, &actualCount);
				if (er != noErr)	goto finishWork;
				accessDate = CFSwapInt32( accessDate );

                bias = ((2000L-1904L)*365L+(2000L-1904L)%4L);	//3027456000
                bias *= 24L*60L*60L;
                catalogInfo.createDate.highSeconds = 0;
                catalogInfo.createDate.lowSeconds = bias;
                catalogInfo.createDate.lowSeconds += createDate;
                catalogInfo.contentModDate.highSeconds = 0;
                catalogInfo.contentModDate.lowSeconds = bias;
                catalogInfo.contentModDate.lowSeconds += modDate;
                catalogInfo.attributeModDate.highSeconds = 0;
                catalogInfo.attributeModDate.lowSeconds = bias;
                catalogInfo.attributeModDate.lowSeconds += modDate;
                catalogInfo.accessDate.highSeconds = 0;
                catalogInfo.accessDate.lowSeconds = bias;
                catalogInfo.accessDate.lowSeconds += accessDate;
                catalogInfo.backupDate.highSeconds = 0;
                catalogInfo.backupDate.lowSeconds = bias;
                catalogInfo.backupDate.lowSeconds += backupDate;
                er = FSSetCatalogInfo ( targetFile, 
                        kFSCatInfoCreateDate + kFSCatInfoContentMod + kFSCatInfoAttrMod 
                        + kFSCatInfoAccessDate + kFSCatInfoBackupDate, &catalogInfo);
                            if (er != noErr)	goto finishWork;
                break;
/*
AppleDoubleの日付時刻データは、2000年正月を0とし、そこからの秒数を記録している。
一方、CatalogInfoの日付は、16bit+32ビットが、1904年からの秒数を記録する。
ここで、1904年から2000年までの秒数を求めたのがbiasの値だが、その値は32ビットの範囲を超えない。
したがって、CatalogInfoの日付の最上位16ビットは0でよい。
だけども、設定する日付けが2040年を超えると、このままではだめで、CatalogInfoの日付データの設定では
highSecondsフィールドにビットが立つことになるだろう。
だが、今から38年後、このコードが動いているかどうかはは微妙だが、まだ現役なら、ソースの修正が必要になる。
2002/10/12
*/
            case 9:	//Finder情報
                printf("OME#mergeres - Set the Finder Information.\n");
				SInt8 x;
				if ( length == 32 )	{	//定義通りだとこうなるはずだが
					for ( j = 0 ; j < 16 ; j ++ )	{
						er = FSReadFork ( resRefNum, fsFromStart, offset+j, 1, &x, &actualCount);
						if (er != noErr)	goto finishWork;
						catalogInfo.finderInfo[j] = x;
					}
					for ( j = 0 ; j < 16 ; j ++ )	{
						er = FSReadFork ( resRefNum, fsFromStart, offset+16+j, 1, &x, &actualCount);
						if (er != noErr)	goto finishWork;
						catalogInfo.extFinderInfo[j] = x;
					}
					er = FSSetCatalogInfo ( targetFile, kFSCatInfoFinderInfo + kFSCatInfoFinderXInfo, &catalogInfo );
					if (er != noErr)	goto finishWork;
				}
				else if ( length == 10 )	{	//Mail.appはなぜか10バイト、FinderInfoの最初の10バイトでしょうね
					for ( j = 0 ; j < 10 ; j ++ )	{
						er = FSReadFork ( resRefNum, fsFromStart, offset+j, 1, &x, &actualCount);
						if (er != noErr)	goto finishWork;
						catalogInfo.finderInfo[j] = x;
					}
					er = FSSetCatalogInfo ( targetFile, kFSCatInfoFinderInfo + kFSCatInfoFinderXInfo, &catalogInfo );
					if (er != noErr)	goto finishWork;
				}
				else {
					printf("OME#mergeres - It doesn't true finder information because the length is short.\n");
				}
                break;
        }
    }
    er = FSCloseFork ( resRefNum );
    return noErr;
finishWork:
bail:
    return er;
}


