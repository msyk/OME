/*
 *  Utils.c
 *  OME_Core
 *
 *  Created by 新居雅行 on Wed Feb 18 2004.
 *  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
 *
 */

#include "main.h"

Boolean isSameFileName( HFSUniStr255 * f1, HFSUniStr255 *f2 )   {
	int i;
	Boolean isSame = false;
	if ( f1->length == f2->length )  {
		isSame = true;
		for (i = 0 ; i < f1->length ; i++ )
			if ( f1->unicode[i] != f2->unicode[i] )  {
				isSame = false;
				break;
			}
	}
	return isSame;
}

Boolean CheckSuffixOfFileName( HFSUniStr255 * fn, char * suffix)	{
	int i;

	size_t suffixLength = strlen(suffix);
	for ( i = 0 ; i < suffixLength ; i++)   {
		if ( fn->unicode[fn->length - suffixLength + i] != suffix[i])
			return false;
	}
	return true;
}

char * GetCStringPtrFromFileName(  HFSUniStr255 * fn )  {

	CFStringRef s = CFStringCreateWithCharacters(NULL ,fn->unicode ,fn->length);
	return (char *)CFStringGetCStringPtr(s, kCFStringEncodingUTF8);

}

Boolean isAlphaNumeric(char c)	{
    Boolean r = false;
    if(		((c>='0')&&(c<='9'))
		||  ((c>='A')&&(c<='Z'))
		||  ((c>='a')&&(c<='z')))
		r = true;
    return r;
}

unsigned char getCharFromHex(char d1, char d2)	{
    unsigned char rVal = 0;
    if((d1>='0')&&(d1<='9'))	rVal += (d1-'0')*16;
    if((d1>='A')&&(d1<='Z'))	rVal += (d1-'A'+10)*16;
    if((d1>='a')&&(d1<='z'))	rVal += (d1-'a'+10)*16;
    if((d2>='0')&&(d2<='9'))	rVal += (d2-'0')*1;
    if((d2>='A')&&(d2<='Z'))	rVal += (d2-'A'+10)*1;
    if((d2>='a')&&(d2<='z'))	rVal += (d2-'a'+10)*1;
    return rVal;
}

//フォルダを参照するFSRefとそのフォルダ内の項目名をCFStringで与えて、その項目へのFSRefを得る
OSStatus FSMakeFSRefCFString(FSRef *parentFolder, CFStringRef itemName, FSRef *targetRef)
{
    UniChar *chars, *buffer;
    CFIndex length;
    OSStatus er;
    
    chars = (UniChar*)CFStringGetCharactersPtr(itemName);
    length = CFStringGetLength(itemName);
    if (chars == NULL) { 
        length = CFStringGetLength(itemName); 
        buffer = (UniChar*)malloc(length * sizeof(UniChar)); 
        CFStringGetCharacters(itemName, CFRangeMake(0, length), buffer); 
        er = FSMakeFSRefUnicode (parentFolder ,length , buffer, kTextEncodingUnknown, targetRef); 
        free(buffer); 
    } 
    else
        er = FSMakeFSRefUnicode (parentFolder ,length , chars, kTextEncodingUnknown, targetRef);

    return er;
}
/*
//初期設定フォルダへのFSRefを取得するルーチン
OSStatus FSMakePreferencesFSRef(FSRef *prefRef)
{
//    FSSpec 	prefSpec;
//    short	prefVRef;
//    long	prefDirID;
    OSStatus	er;
//    UInt8	path[255];
    
    er = FSFindFolder(kUserDomain, kPreferencesFolderType, kDontCreateFolder, prefRef);

    if(er != noErr)	{
        strcpy((char*)path, "~/Library/Preferences");
        er = FSPathMakeRef (path, prefRef, NULL);
    }
    else	{
        er = FSMakeFSSpec(prefVRef, prefDirID, NULL, &prefSpec);
        er = FSpMakeFSRef (&prefSpec, prefRef);
    }

    return er;
}
*/
//OME_Preferencesフォルダへの参照を得る
OSStatus MakeOMEPrefrerencesFSRef(FSRef *omePrefRef)
{
	FSRef prefRef;
	OSStatus er;
	CFStringRef folderName;
	
//	er = FSMakePreferencesFSRef(&prefRef);
	er = FSFindFolder(kUserDomain, kPreferencesFolderType, kDontCreateFolder, &prefRef);
    //「OME設定」フォルダへのFSRefを取得する
    folderName = CFStringCreateWithCString (
		NULL, "OME_Preferences", kCFStringEncodingMacJapanese );
    er = FSMakeFSRefCFString(&prefRef, folderName, omePrefRef);  
    
    if(er == fnfErr)	{	//OME設定フォルダが存在しない場合、ローカライズ版をチェック
        folderName = CFStringCreateWithCString (
			NULL, "OME_Preferences.localized", kCFStringEncodingMacJapanese);
        er = FSMakeFSRefCFString(&prefRef, folderName, omePrefRef);
    }
	
	
	return er;
}

//OMEのルートフォルダへの参照を得る
OSStatus MakeOMERootFSRef(FSRef *omeRootRef)
{
	FSRef omePrefRef;
	OSStatus er;
	Boolean b1, b2;
	CFStringRef fileName;
	
	er = MakeOMEPrefrerencesFSRef(&omePrefRef); //「OME設定」フォルダへのFSRefを取得する
    fileName = CFStringCreateWithCString (
		NULL, "OME_Root", kCFStringEncodingMacJapanese );
	er = FSMakeFSRefCFString(&omePrefRef, fileName, omeRootRef);
    
	er = FSResolveAliasFile(omeRootRef, TRUE, &b1, &b2);
	return er;
}

//もう一つのOMEのルートフォルダへの参照を得る
OSStatus MakeOMERootAltFSRef(FSRef *omeRootRef)
{
	FSRef omePrefRef;
	OSStatus er;
	Boolean b1, b2;
	CFStringRef fileName;
	
	er = MakeOMEPrefrerencesFSRef(&omePrefRef); //「OME設定」フォルダへのFSRefを取得する
    fileName = CFStringCreateWithCString (
		NULL, "OME_Root_Alt", kCFStringEncodingMacJapanese );
	er = FSMakeFSRefCFString(&omePrefRef, fileName, omeRootRef);
    
	er = FSResolveAliasFile(omeRootRef, TRUE, &b1, &b2);
	return er;
}

//OMEのtempフォルダへの参照を得る
OSStatus MakeOMETempFSRef(FSRef *omeTempRef)
{
	FSRef omeRootRef;
	OSStatus er;
	CFStringRef fileName;
	
	er = MakeOMERootFSRef(&omeRootRef);
    fileName = CFStringCreateWithCString (
		NULL, "temp", kCFStringEncodingMacJapanese );
	er = FSMakeFSRefCFString(&omeRootRef, fileName, omeTempRef);
	return er;
}

//もう一つのOMEのtempフォルダへの参照を得る
OSStatus MakeOMETempAltFSRef(FSRef *omeTempRef)
{
	FSRef omeRootRef;
	OSStatus er;
	CFStringRef fileName;
	
	er = MakeOMERootAltFSRef(&omeRootRef);
    fileName = CFStringCreateWithCString (
		NULL, "temp", kCFStringEncodingMacJapanese );
	er = FSMakeFSRefCFString(&omeRootRef, fileName, omeTempRef);
	return er;
}

//OMEのunreadAliasesフォルダへの参照を得る
OSStatus MakeOMEUnreadAliasFSRef(FSRef *omeUnreadAliasRef)
{
	FSRef omeTempRef;
	OSStatus er;
	CFStringRef fileName;
	
	er = MakeOMETempFSRef(&omeTempRef);
    fileName = CFStringCreateWithCString (
		NULL, "unreadAliases", kCFStringEncodingMacJapanese );
	er = FSMakeFSRefCFString(&omeTempRef, fileName, omeUnreadAliasRef);
	return er;
}

//もう一つのOMEのunreadAliasesフォルダへの参照を得る
OSStatus MakeOMEUnreadAliasAltFSRef(FSRef *omeUnreadAliasRef)
{
	FSRef omeTempRef;
	OSStatus er;
	CFStringRef fileName;
	
	er = MakeOMETempAltFSRef(&omeTempRef);
    fileName = CFStringCreateWithCString (
		NULL, "unreadAliases", kCFStringEncodingMacJapanese );
	er = FSMakeFSRefCFString(&omeTempRef, fileName, omeUnreadAliasRef);
	return er;
}

OSStatus MakeLaunchInfoFromAlias(
			LSLaunchFSRefSpec * launchInfo,
			CFStringRef aliasName,
			char * altanateAppPath) {
			
	OSStatus er;
	FSRef omePrefRef, *targetRefPtr;
	Boolean d1, d2, isFolder;
	
	er = MakeOMEPrefrerencesFSRef( &omePrefRef );
	targetRefPtr = (FSRef *)NewPtr( sizeof(FSRef) );
	er = FSMakeFSRefCFString(&omePrefRef, aliasName, targetRefPtr);

	if(er != fnfErr)
		er = FSResolveAliasFile(targetRefPtr, true, &d1, &d2);
	else
		er = FSPathMakeRef ((UInt8*)altanateAppPath, targetRefPtr, &isFolder);

	launchInfo->appRef=targetRefPtr;
	launchInfo->numDocs = 1;
	launchInfo->passThruParams = nil;
	launchInfo->launchFlags = kLSLaunchDefaults;
	launchInfo->asyncRefCon = 0;  
	
	return er;
}

OSStatus MakeLaunchInfoFromAlias3Alts(
			LSLaunchFSRefSpec * launchInfo,
			CFStringRef aliasName,
			char * altanateAppPath1,
			char * altanateAppPath2,
			char * altanateAppPath3) {
			
	OSStatus er;
	FSRef omePrefRef, *targetRefPtr;
	Boolean d1, d2, isFolder;
	
	er = MakeOMEPrefrerencesFSRef( &omePrefRef );
	targetRefPtr = (FSRef *)NewPtr( sizeof(FSRef) );
	er = FSMakeFSRefCFString(&omePrefRef, aliasName, targetRefPtr);

	if(er != fnfErr)
		er = FSResolveAliasFile(targetRefPtr, true, &d1, &d2);
	else	{
		er = FSPathMakeRef ((UInt8*)altanateAppPath1, targetRefPtr, &isFolder);
		if(er == fnfErr)	{
			er = FSPathMakeRef ((UInt8*)altanateAppPath2, targetRefPtr, &isFolder);
			if(er == fnfErr)	{
				er = FSPathMakeRef ((UInt8*)altanateAppPath3, targetRefPtr, &isFolder);
			}
		}
	}

	launchInfo->appRef=targetRefPtr;
	launchInfo->numDocs = 1;
	launchInfo->passThruParams = nil;
	launchInfo->launchFlags = kLSLaunchDefaults;
	launchInfo->asyncRefCon = 0;  
	
	return er;
}