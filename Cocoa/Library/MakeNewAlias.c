/*
 *  $Id: MakeNewAlias.c,v 1.3 2005/09/03 13:42:12 wakimoto Exp $
 *
 *  Created by Masayuki Nii on Sat Aug 03 2002.
 */

#include "MakeNewAlias.h"

#include <Carbon/Carbon.h>

OSErr MakeRelativeAliasFile (FSSpec *targetFile, FSSpec *aliasDest);

int MakeNewAlias ( const char *aliasFile, const char *targetItem )
{
    OSStatus er;
    FSRef madeRef, targetRef, madeParentRef;
    FSSpec madeSpec, targetSpec;
    CFStringRef pathStr;
    UniChar *chars;
    int i, strLen, lastSlaPos;
    unsigned char *parentPath;
    UniChar *buffer;
    CFIndex length;
    
    if ( ( aliasFile [ 0 ] == 0 ) || ( targetItem [ 0 ] == 0 ) ) {
        er = -1;
    } else {
        er = FSPathMakeRef ( (unsigned char *)targetItem, &targetRef, NULL );
        if ( er != noErr ) goto procEnd;
        er = FSGetCatalogInfo ( &targetRef, kFSCatInfoNone, NULL, NULL,
             &targetSpec, NULL);
        if ( er != noErr ) goto procEnd;
        er = FSPathMakeRef ( (unsigned char *)aliasFile, &madeRef, NULL );
        if ( er == noErr ) {
            er = FSGetCatalogInfo ( &madeRef, kFSCatInfoNone, NULL, NULL,
                 &madeSpec, NULL);
            if ( er != noErr ) goto procEnd;
        } else {
            if ( er == fnfErr ) {
                parentPath = malloc(1024);
                lastSlaPos = -1;
                for ( i = 0; i < 1024; i++ ) {
                    parentPath [ i ] = aliasFile [ i ];
                    if ( aliasFile [ i ] == '\0' ) break;
                    if ( aliasFile [ i ] == '/' ) lastSlaPos = i;
                }
                strLen = i;
                parentPath [ lastSlaPos ] = 0;
                er = FSPathMakeRef ( parentPath, &madeParentRef, NULL );
                if ( er != noErr ) goto procEnd;
                pathStr = CFStringCreateWithCString ( NULL,
                    &aliasFile [ lastSlaPos + 1 ], kCFStringEncodingUTF8 );
                chars = (UniChar *)CFStringGetCharactersPtr ( pathStr );
                length = CFStringGetLength ( pathStr );
                if ( chars != NULL ) {
                    er = FSCreateFileUnicode ( &madeParentRef, length, chars,
                        kFSCatInfoNone, NULL, &madeRef, &madeSpec );
                } else { 
                    buffer = malloc ( length * sizeof ( UniChar ) ); 
                    CFStringGetCharacters ( pathStr, CFRangeMake ( 0, length ),
                        buffer); 
                    er = FSCreateFileUnicode ( &madeParentRef, length, buffer,
                        kFSCatInfoNone, NULL, &madeRef, &madeSpec);
                    free ( buffer ); 
                }
                free ( parentPath );
            }
        }
        er = MakeRelativeAliasFile ( &targetSpec, &madeSpec );
        if ( er != noErr ) goto procEnd;
    }
procEnd:
    if ( er != noErr )
        printf("makenewalias terminated by the error number: %d\n", (int)er);
    return er;
}

OSErr FSMakeRelativeAliasFile (FSSpec *targetFile, FSSpec *aliasDest) {

    AliasHandle theAlias;
    Boolean fileCreated;
    short rsrc;
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
    FSpCreateResFile (aliasDest, 'TEMP', 'TEMP', smSystemScript);
    if ((err = ResError ()) != noErr) goto bail;
    fileCreated = true;
        /* êVÇµÇ¢ÉtÉ@ÉCÉãÇÃÉtÉ@ÉCÉãèÓïÒÇéwíËÇµÇ‹Ç∑ */
	err = FSSetCatalogInfo ( aliasDest, kFSCatInfoFinderInfo, &catalogInfo );
    if (err != noErr) goto bail;
        /* ëäëŒìIÉGÉCÉäÉAÉXÉåÉRÅ[ÉhÇÃçÏê¨ */
    err = FSNewAlias (aliasDest, targetFile, &theAlias);
    if (err != noErr) goto bail;
        /* ÉäÉ\Å[ÉXÇÃï€ë∂ */
    rsrc = FSpOpenResFile (aliasDest, fsRdWrPerm);
    if (rsrc == -1) { err = ResError (); goto bail; }
    UseResFile (rsrc);

	HFSUniStr255 outName;
	err = FSGetCatalogInfo ( aliasDest, kFSCatInfoNone , NULL, &outName, NULL, NULL );
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

/*
    FInfo fndrInfo;
    AliasHandle theAlias;
    Boolean fileCreated;
    short rsrc;
    OSErr err;

    theAlias = NULL;
    fileCreated = false;
    rsrc = -1;
    err = FSpGetFInfo (targetFile, &fndrInfo);
    if (fndrInfo.fdType == 'APPL')
        fndrInfo.fdType = kApplicationAliasType;
    fndrInfo.fdFlags = kIsAlias;
    FSpCreateResFile (aliasDest, 'TEMP', 'TEMP', smSystemScript);
    if ((err = ResError ()) != noErr) goto bail;
    fileCreated = true;
    err = FSpSetFInfo (aliasDest, &fndrInfo);
    if (err != noErr) goto bail;
    err = NewAlias (aliasDest, targetFile, &theAlias);
    if (err != noErr) goto bail;
    rsrc = FSpOpenResFile (aliasDest, fsRdWrPerm);
    if (rsrc == -1) { err = ResError (); goto bail; }
    UseResFile (rsrc);
    AddResource ((Handle) theAlias, rAliasType, 0, aliasDest->name);
    if ((err = ResError ()) != noErr) goto bail;
    theAlias = NULL;
    CloseResFile (rsrc);
    rsrc = -1;
    if ((err = ResError ()) != noErr) goto bail;
    return noErr;
bail:
    if (rsrc != -1) CloseResFile(rsrc);
    if (fileCreated) FSpDelete(aliasDest);
    if (theAlias != NULL) DisposeHandle((Handle) theAlias);
    return err;
*/
}

