/*
 *  MakeNewAlias.c
 *  OME_Tools
 *
 *  Created by Êñ∞Â±Ö ÈõÖË°å on Sat Aug 03 2002.
 *  Copyright (c) 2002 __MyCompanyName__. All rights reserved.
 *
 */

#include <Carbon/Carbon.h>

int main(int argc, char **argv);
OSErr FSMakeRelativeAliasFile (FSRef *targetFile, FSRef *aliasDest, FSSpec *aliasDesSepc);

int main(int argc, char **argv)
{
    OSStatus er;
    FSRef madeRef, targetRef, madeParentRef;
    FSSpec madeSpec, targetSpec;
    CFStringRef pathStr, tempStr;
	CFRange rangeToProcess;
    UniChar *chars;
    int i, strLen, lastSlaPos;
    unsigned char *parentPath;
    UniChar *buffer;
    CFIndex length;
	CFIndex usedBufferLength, numChars;
	UInt8	localBuffer[512];
    
	if(argc == 3)	{
    
//        printf("OME:makenewalias:\nAlias File:%s\nTarget To:%s\n", argv[1], argv[2]);
		
		/*	Êú¨Êù•„ÅØ„ÄÅargv[1]„ÅÆ„Ç®„Ç§„É™„Ç¢„Çπ„Ç™„É™„Ç∏„Éä„É´„Éï„Ç°„Ç§„É´„ÅÆ„Éë„Çπ„ÅØUTF-8„Åß„ÇÇ„Çâ„Åà„Çã„ÅØ„Åö„Å†„Å£„Åü„Åå„ÄÅJava 1.5„Åã„Çâ
			„Å™„Åú„ÅãShift_JIS„Åß„ÇÑ„Å£„Å¶„Åè„Çã„Çà„ÅÜ„Å´„Å™„Å£„Åü„ÄÇ„Åó„Çá„ÅÜ„Åå„Å™„ÅÑ„ÅÆ„Åß„ÄÅ„Åù„ÅÆ„Åæ„ÅæFSRef„Çí‰Ωú„Å£„Å®„Åü„Å®„Åç„Å´„Ç®„É©„Éº„Åå
			„Åß„Åü„Çâ„ÄÅShift_JIS„Å´Â§âÊèõ„Åó„Å¶„Åø„Çã„Å®„ÅÑ„ÅÜ„Éó„É≠„Çª„Çπ„ÇíÂÖ•„Çå„Å¶„Åø„Åü„ÄÇ2007/11/19
		*/
   
        er = FSPathMakeRef ((unsigned char *)argv[2], &targetRef, NULL);
        if(er != noErr)	{
			tempStr = CFStringCreateWithCString(NULL, argv[2], kCFStringEncodingMacJapanese);
			rangeToProcess = CFRangeMake(0, CFStringGetLength(tempStr));

			numChars = CFStringGetBytes( tempStr, rangeToProcess, kCFStringEncodingUTF8, 
											'?', FALSE, (UInt8*)localBuffer, 512, &usedBufferLength);
			if ( numChars == 0 )	{
				er = 10021;
				goto procEnd;
			}
			localBuffer[usedBufferLength] = 0;
/*			rangeToProcess.location += numChars;
			rangeToProcess.length -= numChars;
*/
			er = FSPathMakeRef (localBuffer, &targetRef, NULL);
//			printf("OME:makenewalias:Target To(Altanete):%s\n",localBuffer);
		}
        if(er != noErr)	goto procEnd;
		
        er = FSGetCatalogInfo (&targetRef, kFSCatInfoNone, NULL, NULL, &targetSpec, NULL);
        if(er != noErr)	goto procEnd;
        
        er = FSPathMakeRef ((unsigned char *)argv[1], &madeRef, NULL);
        if(er == noErr)	{	//„Éï„Ç°„Ç§„É´„ÅåÂ≠òÂú®„Åô„ÇãÂ†¥Âêà„ÄÅ„ÅÑ„Å£„Åü„ÇìÂâäÈô§„Åô„Çã
			FSDeleteObject(&madeRef);
            if(er != noErr)	goto procEnd;
        }
		parentPath = malloc(1024);
		lastSlaPos = -1;
		for(i=0; i<1024; i++)	{
			parentPath[i] = argv[1][i];
			if(argv[1][i] == '\0')	break;
			if(argv[1][i] == '/')	lastSlaPos = i;
		}
		strLen = i;
		parentPath[lastSlaPos] = 0;
	
		er = FSPathMakeRef (parentPath, &madeParentRef, NULL);
		if(er != noErr)	goto procEnd;

		pathStr = CFStringCreateWithCString (NULL, &argv[1][lastSlaPos+1], kCFStringEncodingUTF8);
		chars = (UniChar *)CFStringGetCharactersPtr(pathStr);
		length = CFStringGetLength(pathStr);
		if (chars != NULL)
			er = FSCreateFileUnicode(&madeParentRef, length, chars, kFSCatInfoNone, NULL, &madeRef, &madeSpec);
		if (chars == NULL) { 
			buffer = malloc(length * sizeof(UniChar)); 
			CFStringGetCharacters(pathStr, CFRangeMake(0, length), buffer); 
			er = FSCreateFileUnicode(&madeParentRef, length, buffer, kFSCatInfoNone, NULL, &madeRef, &madeSpec);
			free(buffer); 
		}
		free(parentPath);

        er = FSMakeRelativeAliasFile (&targetRef, &madeRef, &madeSpec);
        if(er != noErr)	goto procEnd;

    }
    else	{
        er = -1;
        printf("Usage: makenewalias aliasFile targetItem\n");
    }
procEnd:
    if ( er != noErr )
        printf("makenewalias terminated by the error number: %d\n%s", (int)er, argv[2]);
    return er;
}

/* MakeRelativeAliasFile „ÅØ targetFile „ÇíÂèÇÁÖß„Åô„Çã„Ç®„Ç§„É™„Ç¢„Çπ„Éï„Ç°„Ç§„É´„Çí
   aliasDest „Å´‰ΩúÊàê„Åó„Åæ„Åô„ÄÇ„Ç®„Ç§„É™„Ç¢„Çπ„ÅØÁõ∏ÂØæÁöÑ„Éë„Çπ„ÅßÊåáÂÆö„Åï„Çå„Åæ„Åô„ÄÇÔºàTechnote 1188Ôºâ */
    
OSErr FSMakeRelativeAliasFile (FSRef *targetFile, FSRef *aliasDest, FSSpec *aliasDesSepc) {
//    FInfo fndrInfo;
    AliasHandle theAlias;
    Boolean fileCreated;
    ResFileRefNum rsrc;
    OSErr err;
        /* „É≠„Éº„Ç´„É´Â§âÊï∞„ÅÆÊ∫ñÂÇô */
    theAlias = NULL;
    fileCreated = false;
    rsrc = -1;
	FSCatalogInfo catalogInfo;

	/* „Ç®„Ç§„É™„Ç¢„Çπ„Éï„Ç°„Ç§„É´„ÅÆ„Éï„Ç°„Ç§„É´ÊÉÖÂ†±„ÅÆÊ∫ñÂÇô */
	if ( CFByteOrderGetCurrent() == CFByteOrderLittleEndian )	{
			catalogInfo.finderInfo[9] = 0x80;
			catalogInfo.finderInfo[8] = 0x00;
		}
		else	{
			catalogInfo.finderInfo[8] = 0x80;
			catalogInfo.finderInfo[9] = 0x00;
		}

        /* Êñ∞„Åó„ÅÑ„Éï„Ç°„Ç§„É´„Çí‰ΩúÊàê„Åó„Åæ„Åô */
//    FSpCreateResFile (aliasDesSepc, 'TEMP', 'TEMP', smSystemScript);
	HFSUniStr255 outName;
	FSRef aliasDestParent;
	HFSUniStr255 rsrcName;
	err = FSGetResourceForkName( &rsrcName );
	err = FSGetCatalogInfo ( aliasDest, kFSCatInfoNone, NULL, &outName, NULL, &aliasDestParent );
 	err = FSCreateResourceFile ( &aliasDestParent, outName.length, outName.unicode, 
									kFSCatInfoNone, NULL, rsrcName.length, rsrcName.unicode, NULL, NULL );

    if ( err != noErr) goto bail;
    fileCreated = true;
        /* êVÇµÇ¢ÉtÉ@ÉCÉãÇÃÉtÉ@ÉCÉãèÓïÒÇéwíËÇµÇ‹Ç∑ */
	err = FSSetCatalogInfo ( aliasDest, kFSCatInfoFinderInfo, &catalogInfo );
    if (err != noErr) goto bail;
        /* ëäëŒìIÉGÉCÉäÉAÉXÉåÉRÅ[ÉhÇÃçÏê¨ */
    err = FSNewAlias (aliasDest, targetFile, &theAlias);
    if (err != noErr) goto bail;
        /* ÉäÉ\Å[ÉXÇÃï€ë∂ */
 
//	rsrc = FSpOpenResFile (aliasDesSepc, fsRdWrPerm);
	err = FSOpenResourceFile ( aliasDest, rsrcName.length, rsrcName.unicode, fsRdWrPerm, &rsrc );

    if (rsrc == -1) { err = ResError (); goto bail; }
    UseResFile (rsrc);

//	HFSUniStr255 outName;
//	err = FSGetCatalogInfo ( aliasDest, kFSCatInfoNone , NULL, &outName, NULL, NULL );
    AddResource ((Handle) theAlias, rAliasType, 0, (ConstStr255Param)&outName);
    if ((err = ResError ()) != noErr) goto bail;
    theAlias = NULL;
    CloseResFile (rsrc);
    rsrc = -1;
    if ((err = ResError ()) != noErr) goto bail;
        /* ÂÆå‰∫Ü */
    return noErr;
bail:
    if (rsrc != -1) CloseResFile(rsrc);
    if (fileCreated) FSDeleteObject(aliasDest);
    if (theAlias != NULL) DisposeHandle((Handle) theAlias);
    return err;
}
