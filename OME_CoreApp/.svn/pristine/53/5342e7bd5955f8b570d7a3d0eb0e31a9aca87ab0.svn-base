/*
	Mac OS Mail Environment
	Open Messages
	by Masayuki Nii, Feb 2, 2001 Starting
	
        Mac_OS_Xnizing started at Mar 14, 2001 by Masayuki Nii
*/

#include "main.h"

//Globals
LSLaunchFSRefSpec launchInfoTextReader; //LaunchServeの構造体（テキストメール参照用）
LSLaunchFSRefSpec launchInfoHTMLReader; //LaunchServeの構造体（HTMLメール参照用）
FSRef omeRootRef, omePrefRef;

int main(void)
{
    OSErr	er;
	FSRef	configFSRef, omePrefRef;
    CFStringRef folderName;
	Boolean isFolder;
	
	int refreshInterval = 30;
        
//    InitCursor();
//    LSInit(kLSRequestBasicFlagsOnly);   //LaunchServicesの初期化
    
    //AppleEventハンドラのインプリメント
    er = AEInstallEventHandler(
				kCoreEventClass, kAEOpenDocuments,
                NewAEEventHandlerUPP((AEEventHandlerProcPtr)&HandleOpenEvent), 
				0, false);
    er = AEInstallEventHandler(
				kCoreEventClass, kAEQuitApplication, 
                NewAEEventHandlerUPP((AEEventHandlerProcPtr)&HandleQuitEvent), 
				0, false);
    er = AEInstallEventHandler(
				kInternetEventClass, kAEGetURL, 
                NewAEEventHandlerUPP((AEEventHandlerProcPtr)&HandleOpenLocation), 
				0, false);
	er = InstallEventLoopIdleTimer( GetMainEventLoop(), refreshInterval, refreshInterval, 
				NewEventLoopIdleTimerUPP(TimerAction) , NULL, NULL);

    //「OME設定」フォルダへのFSRefを取得する
	er = MakeOMEPrefrerencesFSRef(&omePrefRef);
	
    if(er == fnfErr)	{	//OME設定フォルダが存在しない場合
        er = FSPathMakeRef((UInt8 *)"/Applications/OME_Applications/OME_Configrator.app", 
					&configFSRef, &isFolder);
        er = LSOpenFSRef(&configFSRef, nil);
            //初期設定を行うアプリケーションを起動して、OME_Coreは終了する
		return 0;
    }
	
	//OME設定フォルダが存在する場合
	//メール参照を行うアプリケーションへの参照を得る
	folderName = CFStringCreateWithCString (
					NULL, "Mail_Reader", kCFStringEncodingMacJapanese);
	er = MakeLaunchInfoFromAlias3Alts( 
			&launchInfoTextReader, 
			folderName,
			"/Applications/OME_Applications.localized/OMEMailViewer.app", 
			"/Applications/OME_Applications/OMEMailViewer.app",
			"/Applications/TextEdit.app");

	//HTMLメール参照を行うアプリケーションへの参照を得る
	folderName = CFStringCreateWithCString (
					NULL, "HTML_Reader", kCFStringEncodingMacJapanese);
	er = MakeLaunchInfoFromAlias3Alts( 
			&launchInfoHTMLReader, 
			folderName,
			"/Applications/OME_Applications.localized/OMEMailViewer.app", 
			"/Applications/OME_Applications/OMEMailViewer.app",
			"/Applications/TextEdit.app");

	AliasListInitialize();		//エイリアスの管理リストを構築

	system("sleep 1");

	RunApplicationEventLoop();

	AliasListFinalize();
//	LSTerm();
    return 0;
}

pascal OSErr HandleOpenEvent(
				const AppleEvent *theAppleEvent, 
				AppleEvent *reply, 
				UInt32 theRefCon)
{
	OSErr	er;
	long		actualSize, i, itemsInList;
	AEDesc	directParam;
	AEDescList*	docListPtr;
	AEKeyword	keywd;
	DescType		returnedType;
	FSRef myFSRef, newFSRef;
	FSCatalogInfoBitmap whichInfo;
	FSCatalogInfo cInfo;
	HFSUniStr255 outName;
	FSRef parentRef;

	directParam.dataHandle = nil;
	docListPtr = (AEDescList*)NewPtr(sizeof(AEDescList));
	
	er = AEGetParamDesc(theAppleEvent, keyDirectObject, typeWildCard, &directParam);
	if(er)	goto CleanUp;

	//AppleEventレコードから、ファイル情報を取得する
	er = AECoerceDesc(&directParam, typeAEList, docListPtr);
	er = AECountItems(docListPtr, &itemsInList);

	whichInfo = kFSCatInfoContentMod + kFSCatInfoCreateDate + kFSCatInfoAttrMod + kFSCatInfoAccessDate;
	//カタログ情報から取り出す情報
	
	for ( i=1 ; i<= itemsInList ; i++)	{
		er = AEGetNthPtr(docListPtr, i, typeFSRef, &keywd, &returnedType, 
							(Ptr)&myFSRef, sizeof(FSRef), &actualSize);
		er = FSGetCatalogInfo(&myFSRef, whichInfo, &cInfo, &outName, nil, &parentRef);
												//ファイルから情報を取り出す
		DeleteAlias(&myFSRef);
		if(		(outName.unicode[outName.length -4] == '.')	&&
				(outName.unicode[outName.length -3] == 'y')	&&
				(outName.unicode[outName.length -2] == 'g')	&&
				(outName.unicode[outName.length -1] == 'm'))	{	//ファイル名の末尾が.ygmなら
			outName.unicode[outName.length -3] = 'm';
			outName.unicode[outName.length -2] = 'a';
			outName.unicode[outName.length -1] = 'i';
			outName.unicode[outName.length] = 'l';	//新しいファイル名として、.ygmを.mailにしたものを用意する
			outName.length++;

			er = FSRenameUnicode (&myFSRef, outName.length, outName.unicode, 
									kTextEncodingUnknown, &newFSRef);	//ファイル名の変更
			launchInfoTextReader.itemRefs = &newFSRef;
			er = LSOpenFromRefSpec(&launchInfoTextReader, nil);
		}
		else if(	(outName.unicode[outName.length -5] == '.')	&&
					(outName.unicode[outName.length -4] == 'h')	&&
					(outName.unicode[outName.length -3] == 't')	&&
					(outName.unicode[outName.length -2] == 'm')	&&
					(outName.unicode[outName.length -1] == 'l'))	{	//ファイル名の末尾が.htmlなら
			outName.length--;	//新しいファイル名として、.htmlを.htmにしたものを用意する

			er = FSRenameUnicode (&myFSRef, outName.length, outName.unicode, 
									kTextEncodingUnknown, &newFSRef);	//ファイル名の変更
			launchInfoHTMLReader.itemRefs = &newFSRef;
			er = LSOpenFromRefSpec(&launchInfoHTMLReader, nil);
		}
		else if(
					(outName.unicode[outName.length -4] == '.')	&&
					(outName.unicode[outName.length -3] == 'h')	&&
					(outName.unicode[outName.length -2] == 't')	&&
					(outName.unicode[outName.length -1] == 'm')
				)	{	//ファイル名の末尾が.ygmなら
			launchInfoHTMLReader.itemRefs = &myFSRef;
			er = LSOpenFromRefSpec(&launchInfoHTMLReader, nil);

		}
		else	{
			launchInfoTextReader.itemRefs = &myFSRef;
			er = LSOpenFromRefSpec(&launchInfoTextReader, nil);
		}
		er = FSSetCatalogInfo(&parentRef, whichInfo, &cInfo);	//フォルダの日付をファイルと同じにする
	}
	er = AEDisposeDesc(docListPtr);
	DisposePtr((Ptr)docListPtr);
        
CleanUp:

    //LSTerm();
    //QuitApplicationEventLoop();

    return er;
}

pascal OSErr HandleQuitEvent(
			const AppleEvent *theAppleEvent, 
			AppleEvent *reply, 
			UInt32 theRefCon)
{
//    LSTerm();
    QuitApplicationEventLoop();
    return noErr;
}

pascal void TimerAction(
				EventLoopTimerRef inTimer, 
				EventLoopIdleTimerMessage inState, 
				void *inUserData)  {
#ifdef DEBUG_MSG
	printf("-------start timer action\n");
#endif
	RefreshAliasList();
}

//	mailto URLに関するRFC <ftp://ftp.nic.ad.jp/rfc/rfc2368.txt>

pascal OSErr HandleOpenLocation(
				const AppleEvent *theAppleEvent, 
				AppleEvent *reply, 
				UInt32 theRefCon)
{
    OSErr	er = noErr;
    AEDesc	directParam;
    Handle	strHandle;
    Size	strLen;
    char	cstr[512], decodeStr[512], nameArray[3][128];
    int		ix, iy, arindex;
    CFRange	eqPos;
    short 	outFile;
    Boolean	isSubject = false;

    CFStringRef lineEnd = CFSTR("\n");

    directParam.dataHandle = nil;
    er = AEGetParamDesc(theAppleEvent, keyDirectObject, typeWildCard, &directParam);
            if ( er != noErr )	{ printf("#OME_Core Error: Position=907, Code=%d \r", er);	goto CleanUp; }
    strHandle = (Handle)(directParam.dataHandle);
    strLen = GetHandleSize(strHandle);		//AppleEventからデータを取り出す
    
    CFMutableStringRef messages = CFStringCreateMutable(NULL, 0);	//テンプレートに書き込む内容のバッファ
    
    CFStringRef paramString = CFStringCreateWithBytes (NULL, (UInt8 *)*strHandle, strLen, kCFStringEncodingMacJapanese, false);
    CFArrayRef pA = CFStringCreateArrayBySeparatingStrings (NULL, paramString, CFSTR("?"));	//?で区切った文字列を取り出す
    if(pA == NULL)	goto CleanUp;
    
    CFIndex c = CFArrayGetCount(pA);
    CFIndex i;
    for ( i = 0 ; i < c ; i++ ) {	//?で区切ったそれぞれのブロックをチェック
        CFStringRef curParam = CFArrayGetValueAtIndex(pA, i);
        
        if(CFStringCompareWithOptions (curParam, CFSTR("mailto:"), 
                CFRangeMake(0, 7), kCFCompareCaseInsensitive) == 0){	//mailto:で始まる文字列なら
            CFMutableStringRef curValue = CFStringCreateMutableCopy (NULL, 1024,
                                                CFStringCreateWithSubstring (NULL, curParam, 
                                                    CFRangeMake(7, CFStringGetLength(curParam)-7)));
            if(CFStringGetCString (curValue, cstr, 512, kCFStringEncodingMacJapanese))	{
                nameArray[0][0] = 0;	nameArray[1][0] = 0;	nameArray[2][0] = 0;	
                ix = 0;	iy = 0; arindex = 0;
                while(cstr[ix] != 0x00)	{
                    if((cstr[ix] == '%')&&(cstr[ix+1] == '2')&&(cstr[ix+2]) == '0')	{
                        nameArray[arindex][iy] = 0;
                        arindex++;	iy = 0;	ix++;	ix++;	ix++;
                    }
                    else if((cstr[ix] == '%')&&(isAlphaNumeric(cstr[ix+1]))&&(isAlphaNumeric(cstr[ix+2])))	{
                        nameArray[arindex][iy] = getCharFromHex(cstr[ix+1], cstr[ix+2]);
                        iy++;	ix++;	ix++;	ix++;
                    }
                    else	{
                        nameArray[arindex][iy] = cstr[ix];
                        iy++;   ix++;
                    }
                }
                nameArray[arindex][iy] = 0x00;
                
                if(nameArray[0][0] == 0)	{
                }
                else if(nameArray[1][0] == 0)	{
                    curValue = CFStringCreateMutableCopy (NULL, 1024, 
                        CFStringCreateWithCString (NULL, nameArray[0], kCFStringEncodingUTF8));
                }
                else if(nameArray[2][0] == 0)	{
                    curValue = CFStringCreateMutableCopy (NULL, 1024, 
                        CFStringCreateWithCString (NULL, nameArray[0], kCFStringEncodingUTF8));
                    CFStringAppendCString (curValue, "さん", kCFStringEncodingMacJapanese);
                    CFStringAppendCString (curValue, " ", kCFStringEncodingUTF8);
                    CFStringAppendCString (curValue, nameArray[1], kCFStringEncodingUTF8);
                }
                else	{
                    curValue = CFStringCreateMutableCopy (NULL, 1024, 
                        CFStringCreateWithCString (NULL, nameArray[0], kCFStringEncodingUTF8));
                    CFStringAppendCString (curValue, nameArray[1], kCFStringEncodingUTF8);
                    CFStringAppendCString (curValue, "さん", kCFStringEncodingMacJapanese);
                    CFStringAppendCString (curValue, " ", kCFStringEncodingUTF8);
                    CFStringAppendCString (curValue, nameArray[2], kCFStringEncodingUTF8);
                }
            }
            CFStringAppend(messages, CFSTR("To: "));
            CFStringAppend(messages, curValue);
            CFStringAppend(messages, lineEnd);
        }
        else{	//mailtoで始まる文字列でないのなら
            eqPos = CFStringFind (curParam, CFSTR("="), 0);
            if (eqPos.location == kCFNotFound)	goto CleanUp;
            CFMutableStringRef curHeader = CFStringCreateMutableCopy (NULL, 1024, 
                CFStringCreateWithSubstring (NULL, curParam, CFRangeMake(0, eqPos.location)));
            CFStringRef curValueA = CFStringCreateWithSubstring (NULL, curParam, 
                                        CFRangeMake(eqPos.location+eqPos.length, 
                                        CFStringGetLength(curParam)-eqPos.location-eqPos.length));

            if(CFStringGetCString (curValueA, cstr, 512, kCFStringEncodingMacJapanese))	{
                ix = 0;	iy = 0;
                while(cstr[ix] != 0x00)	{
                    if((cstr[ix] == '%')&&(isAlphaNumeric(cstr[ix+1]))&&(isAlphaNumeric(cstr[ix+2])))	{
                        decodeStr[iy] = getCharFromHex(cstr[ix+1], cstr[ix+2]);
                        iy++;	ix++;	ix++;	ix++;
                    }
                    else	{
                        decodeStr[iy] = cstr[ix];
                        iy++;   ix++;
                    }
                }
                decodeStr[iy] = 0x00;
                curValueA = CFStringCreateWithCString (NULL, decodeStr, kCFStringEncodingUTF8);
            }
            CFStringCapitalize (curHeader, NULL);
            CFStringAppend(messages, curHeader);
            CFStringAppend(messages, CFSTR(": "));
            CFStringAppend(messages, curValueA);
            CFStringAppend(messages, lineEnd);
            if(CFStringCompareWithOptions (curHeader, CFSTR("subject"), CFRangeMake(0, 7), kCFCompareCaseInsensitive)
                == 0)
                isSubject = true;
        }
    }
    CFRelease(pA);

    if(! isSubject)	{
        CFStringAppend(messages, CFSTR("Subject: "));
        CFStringAppend(messages, lineEnd);
        CFStringAppend(messages, lineEnd);
        CFStringAppend(messages, lineEnd);
    }


    CFStringRef tempFileName = CFSTR("temp.mailtemplate");	//作成するファイル名をCFStringで用意
    CFIndex length = CFStringGetLength(tempFileName); 		//CFStringの長さ
    UniChar chars[256]; 					//CFStringのコンテンツをコピーする配列を用意
    CFStringGetCharacters(tempFileName, CFRangeMake(0, length), chars);	//配列にUnicode文字列をコピー
        //CFStringのドキュメントでは、mallocでメモリを確保してコピーしているが、これだと後でfreeが必要でちょい面倒
        //ローカル変数の配列なら、プロシージャ終了時にクリアされるから楽ということ
        //CFStringのストレージアクセスのAPIはいつもNULLを返すので、最初からこの方法で取り出すのが懸命だろう
    
    FSRef tempFileRef;
    er = FSMakeFSRefUnicode (&omePrefRef, length, chars, kTextEncodingUnknown, &tempFileRef);
        //ファイルのFSRefを得る
    if ( er == fnfErr )	//ファイルが存在しない場合
        er = FSCreateFileUnicode (&omePrefRef, length, chars, kFSCatInfoNone, kFSCatInfoNone, &tempFileRef, NULL);
            //ファイルを作成する
    else if ( er != noErr )	{ 
        printf("#OME_Core Error: Position=901, Code=%d \r", er);	goto CleanUp;
    }
    HFSUniStr255 dataForkName;
    er = FSGetDataForkName (&dataForkName);	//データフォーク名を取り出して
    er =  FSOpenFork (&tempFileRef, dataForkName.length, dataForkName.unicode, fsWrPerm, &outFile);
        //データフォークを開く
            if ( er != noErr )	{ printf("#OME_Core Error: Position=902, Code=%d \r", er);	goto CleanUp; }

//    er = SetEOF(outFile, 0);	//ファイルの中身を空にする
    er = FSSetForkSize(outFile, fsFromStart, 0);	//ファイルの中身を空にする
            if ( er != noErr )	{ printf("#OME_Core Error: Position=903, Code=%d \r", er);	goto CleanUp; }

    UInt8	bytes[1000];
    long	fLen;
    CFStringGetBytes (messages, CFRangeMake(0, CFStringGetLength(messages)), CFStringGetSystemEncoding(),
                        '?', false, bytes, 1000, &fLen);	//書き込むデータのバイト配列を得る
//    er = FSWrite(outFile, &fLen, bytes);	//ファイルに書き込む
    er = FSWriteFork(outFile,  fsFromStart, 0, fLen, bytes, NULL);	//ファイルに書き込む
            if ( er != noErr )	{ printf("#OME_Core Error: Position=904, Code=%d \r", er);	goto CleanUp; }
//    er = FSClose(outFile);
    er = FSCloseFork(outFile);
            if ( er != noErr )	{ printf("#OME_Core Error: Position=905, Code=%d \r", er);	goto CleanUp; }

    CFMutableStringRef cmdLine = CFStringCreateMutableCopy(NULL, 0, CFSTR("java "));
    CFStringAppend( cmdLine, CFSTR("-cp ") );
    CFStringAppend( cmdLine, CFSTR("/Applications/OME_Applications/tools/OME_lib.jar:/System/Library/Java ") );
    CFStringAppend( cmdLine, CFSTR("OME.mailwriter.OME_MailWriter ") );
    CFStringAppend( cmdLine, CFSTR("TEMPLATE ") );
    UInt8 path[1024];
    UInt32 pathSize = 1024;
    er = FSRefMakePath ( &tempFileRef, path, pathSize);
    CFStringAppendCString( cmdLine, (char*)path, 0);
    CFStringGetBytes (cmdLine, CFRangeMake(0, CFStringGetLength(cmdLine)), CFStringGetSystemEncoding(),
                        '?', false, bytes, 1000, &fLen);	//書き込むデータのバイト配列を得る
    bytes[fLen] = 0;
    system((char*)bytes);
/*    
    writerlaunchInfoTextReader.itemRefs = &tempFileRef;
    er = LSOpenFromRefSpec(&writerlaunchInfoTextReader, nil);
*/
CleanUp:

//    LSTerm();
    QuitApplicationEventLoop();

    return er;
}
