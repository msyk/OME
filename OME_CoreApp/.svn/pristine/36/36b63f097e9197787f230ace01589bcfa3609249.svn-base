#include "main.h"

struct AliasOfUnread	{
	FSRef	aliasFile;
	FSRef	mailFile;
	HFSUniStr255	aliasFileName;
	HFSUniStr255	mailFileName;
	Ptr	nextRecord;
};
typedef struct AliasOfUnread AliasOfUnread;
typedef AliasOfUnread* AliasOfUnreadPtr;

AliasOfUnreadPtr topOfList = NULL;

void AliasListInitialize()	{
	FSRef omeUnreadAliasRef;
	FSIterator  iterator, iteratorAlt;
	ItemCount actualObjects;
	FSCatalogInfoBitmap whichInfo;
	AliasOfUnreadPtr newObject;
	OSStatus er;
	Boolean b1, b2;
	int counter = 0;
	
#ifdef DEBUG_MSG
	printf("AliasListInitialize starts:\n");
#endif
	er = MakeOMEUnreadAliasFSRef(&omeUnreadAliasRef);
	if ( er != noErr )  {
		printf("Error[%d]: Can't get OME unreadAliases folder.\n", (int)er);
		printf("---------- Terminate OME_Core.app.\n");
		QuitApplicationEventLoop();
		QuitApplicationEventLoop();
		return;
	}
	er = FSOpenIterator (&omeUnreadAliasRef, kFSIterateFlat, &iterator);
	if ( er != noErr )  {
		printf("Error[%d]: at FSOpenIterator.\n", (int)er);
		return;
	}
	whichInfo = kFSCatInfoNone;
	
	topOfList = NULL;
	//エイリアスファイルのリストアップ
	AliasOfUnreadPtr beforeObject = NULL;
	er = noErr;
	while ( er == noErr )	{
		
		newObject = (AliasOfUnreadPtr)NewPtr( sizeof ( AliasOfUnread ) );
		
		er = FSGetCatalogInfoBulk(iterator, 1, &actualObjects,
				NULL, whichInfo, NULL, &newObject->aliasFile, 
				NULL, &newObject->aliasFileName);

		if ( er == noErr )  {
			newObject->mailFile = newObject->aliasFile;
			er = FSResolveAliasFile(&newObject->mailFile, TRUE, &b1, &b2);
			if ( er == noErr )  {
				er = FSGetCatalogInfo(&newObject->mailFile, whichInfo, NULL,
					&newObject->mailFileName, NULL, NULL);
				if ( er == noErr )  {
					if (		CheckSuffixOfFileName( &newObject->mailFileName, ".ygm")
							||	CheckSuffixOfFileName( &newObject->mailFileName, ".html") ){
#ifdef DEBUG_MSG
	printf("--Enter file: %s\n", GetCStringPtrFromFileName(  &newObject->aliasFileName )  );
#endif
						newObject->nextRecord = (Ptr)beforeObject;
						beforeObject = newObject;
						counter++;
					}
					else	{
#ifdef DEBUG_MSG
	printf("--Delete file: %s\n", GetCStringPtrFromFileName(  &newObject->aliasFileName )  );
#endif
						er = FSDeleteObject( &newObject->aliasFile );
					}
				}
			}
		}
	}
	topOfList = beforeObject;

	if ( er != noErr && er != errFSNoMoreItems )   {
			//errFSNoMoreItems（-1417）は、FSGetCatalogInfoで最後まで項目を取ったことを示す
		printf("Error[%d]: at Loop in AliasListInitialize function.\n", (int)er);
		printf("---------- Terminate OME_Core.app.\n");
		QuitApplicationEventLoop();
	}

	er = FSCloseIterator( iterator );
	
/*
	OMEのルートのtemp/unreadAliasesフォルダの内容しかチェックしていないので、OMEルートを複数運用する場合に
	未読エイリアスが即座に消えてくれないという問題が発生する。具体的には、モバイルアカウントで、ネットワークホームにある
	OMEルートを参照しつつ、ローカルでも別のOME運用をしたいという場合があったから。
	
	そこで、OME_Preferences管理下のOMEルートのtempフォルダに、unreadAliasesAltというエイリアスを作り
	そこに、追加のunreadAliasesを運用できるようにしてみた。
	(2005/2/12)
	
	しかしながら、それでは問題があることが分かった。さらに仕様を変更して、OME_Preferencesに、「OME_Root_Alt」
	という名前のエイリアスが、さらに別のOMEルートを参照するという規則に変更した。
	(2005/2/13)
*/

	er = MakeOMEUnreadAliasAltFSRef(&omeUnreadAliasRef);
	if ( er != noErr )  {	//追加のOMEルートがなければ即座に戻る。エラーメッセージもいらないでしょう。
		return;
	}
	er = FSResolveAliasFile(&omeUnreadAliasRef, TRUE, &b1, &b2);
	er = FSOpenIterator (&omeUnreadAliasRef, kFSIterateFlat, &iteratorAlt);
	if ( er != noErr )  {
		printf("Error[%d]: at FSOpenIterator in alternative unreadAliases.\n", (int)er);
		return;
	}

#ifdef DEBUG_MSG
	printf("--Scan the alternative OME root.\n");
#endif

	whichInfo = kFSCatInfoNone;
	
	beforeObject = topOfList;
	er = noErr;
	while ( er == noErr )	{
		
		newObject = (AliasOfUnreadPtr)NewPtr( sizeof ( AliasOfUnread ) );
		
		er = FSGetCatalogInfoBulk(iteratorAlt, 1, &actualObjects,
				NULL, whichInfo, NULL, &newObject->aliasFile, 
				NULL, &newObject->aliasFileName);

		if ( er == noErr )  {
			newObject->mailFile = newObject->aliasFile;
			er = FSResolveAliasFile(&newObject->mailFile, TRUE, &b1, &b2);
			if ( er == noErr )  {
				er = FSGetCatalogInfo(&newObject->mailFile, whichInfo, NULL,
					&newObject->mailFileName, NULL, NULL);
				if ( er == noErr )  {
					if (		CheckSuffixOfFileName( &newObject->mailFileName, ".ygm")
							||	CheckSuffixOfFileName( &newObject->mailFileName, ".html") ){
#ifdef DEBUG_MSG
	printf("--Enter file: %s\n", GetCStringPtrFromFileName(  &newObject->aliasFileName )  );
#endif
						newObject->nextRecord = (Ptr)beforeObject;
						beforeObject = newObject;
						counter++;
					}
					else	{
#ifdef DEBUG_MSG
	printf("--Delete file: %s\n", GetCStringPtrFromFileName(  &newObject->aliasFileName )  );
#endif
						er = FSDeleteObject( &newObject->aliasFile );
					}
				}
			}
		}
	}
	topOfList = beforeObject;

	er = FSCloseIterator( iteratorAlt );

#ifdef DEBUG_MSG
	printf("Setup Aliases List, Elements: %d\n", counter);
#endif
}

void AliasListFinalize()	{
	//リストの破壊
	AliasOfUnreadPtr nextObject, currentObject = topOfList;
	while ( currentObject != NULL )	{
		nextObject = (AliasOfUnreadPtr)currentObject->nextRecord;
		DisposePtr( (Ptr) currentObject );
		currentObject = nextObject;
	}
	topOfList = NULL;
}

void RefreshAliasList()	{
	//エイリアスの一覧を得て
	//リストにないものをリストに追加

#ifdef DEBUG_MSG
	UnsignedWide startTime, endTime;
	printf("RefreshAliasList starts:\n");
	Microseconds(&startTime);
#endif
	AliasListFinalize();
	AliasListInitialize();
#ifdef DEBUG_MSG
	Microseconds(&endTime);
	printf("RefreshAliasList ends, Processing time: %ld ms\n",
		(endTime.lo-startTime.lo)/1000 );
//		UnsignedWideToUInt64(endTime)-UnsignedWideToUInt64(startTime));
#endif
}

void DeleteAlias( FSRef * targetMailFile )	{
	//リストから探して
	//そのエイリアスを削除する
	HFSUniStr255 targetFileName;
	OSStatus er;
	
	er = FSGetCatalogInfo(targetMailFile, kFSCatInfoNone, NULL,
			&targetFileName, NULL, NULL);

#ifdef DEBUG_MSG
	printf("Delete Request: %s\n",GetCStringPtrFromFileName( &targetFileName) );
#endif

	AliasOfUnreadPtr nextObject, currentObject = topOfList, beforeObject = NULL;
	while ( currentObject != NULL )	{
		nextObject = (AliasOfUnreadPtr)currentObject->nextRecord;
		if ( isSameFileName( &currentObject->mailFileName, &targetFileName )  )   {
                  //未読エイリアスファイルを削除する
#ifdef DEBUG_MSG
	printf("--Match file: %s\n",GetCStringPtrFromFileName( &currentObject->mailFileName) );
	printf("--Delete file: %s\n", GetCStringPtrFromFileName(&currentObject->aliasFileName) );
#endif
			er = FSDeleteObject( &currentObject->aliasFile );
			if ( beforeObject == NULL )
				topOfList = nextObject;
			else
				beforeObject->nextRecord = (Ptr)nextObject;
			DisposePtr( (Ptr) currentObject );
			break;
		}
		else
			beforeObject = currentObject;
		currentObject = nextObject;
	}
}

/*

void Listing()  {
	AliasOfUnreadPtr nextObject, currentObject = topOfList;
	while ( currentObject != NULL )	{
		printf("%s\n", currentObject->aliasFileName.unicode);
		nextObject = (AliasOfUnreadPtr)currentObject->nextRecord;
		currentObject = nextObject;
	}
}
*/