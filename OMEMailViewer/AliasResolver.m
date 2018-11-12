/*
 *  $Id: AliasResolver.c,v 1.3 2005/09/03 13:42:12 wakimoto Exp $
 *
 *  Created by Masayuki Nii on Sun Jan 27 2002.
 */

#import "AliasResolver.h"

//#define finish if(er != noErr){printf("ERROR!");goto procEnd;}

@implementation AliasResolver

+ (NSString *)resolve: (NSString *)path
{
    NSFileManager *fm = [NSFileManager defaultManager];
    NSURL *resolvedURL = nil;
    NSError *error;
 
    if ([fm fileExistsAtPath: path])  {
        
        resolvedURL = [NSURL URLByResolvingAliasFileAtURL: [NSURL fileURLWithPath: path]
                                                  options: 0
                                                    error: &error];
        if(error)   {
            NSLog(@"ERROR: %@", error);
        }
    }
    return resolvedURL.path;
}

@end

/*
OSStatus AliasResolver(const char *sourceCStr, char *resolvedCStr)
{
    OSStatus er = 0;
    FSRef targetRef;
    Boolean b1, b2;
    FSCatalogInfoBitmap infoBit;
    FSCatalogInfo catInfo;
 
    NSString *sourcePath = [NSString stringWit]
	er = FSPathMakeRef ((unsigned char *)sourceCStr, &targetRef, NULL);
	infoBit = kFSCatInfoFinderInfo;
	er = FSGetCatalogInfo (&targetRef, infoBit, &catInfo, NULL, NULL, NULL); finish;
	
	// This part depends on endian.
	UInt16 finderFlag;
	if ( CFByteOrderGetCurrent() == CFByteOrderLittleEndian )
		finderFlag = catInfo.finderInfo[9] * 256 + catInfo.finderInfo[8];
	else
		finderFlag = catInfo.finderInfo[8] * 256 + catInfo.finderInfo[9];
		
	if (( finderFlag & kIsAlias ) == 0)   {   //引数のファイルはエイリアスではない
		resolvedCStr = resolvedCStr;  //引数のパスをそのまま戻す
	}
	else    {   //引数のファイルはエイリアス
		er = FSResolveAliasFile (&targetRef, TRUE, &b1, &b2);    finish;
		er = FSRefMakePath (&targetRef, (unsigned char *)resolvedCStr, 1024);      finish;
	}
procEnd:
    return er;
}
*/
/*
int AliasResolver ( char *resultPath, const char *aliasPath )
{
    OSStatus er;
    FSRef targetRef;
    FSSpec targetSpec;
    Boolean b1, b2;
    FSCatalogInfoBitmap infoBit;
    FSCatalogInfo catInfo;
    UInt16 finderFlag;

    er = noErr;
    if ( aliasPath [ 0 ] != 0 ) {
        er = FSPathMakeRef ( (unsigned char *)aliasPath, &targetRef, NULL );
            finish;
        infoBit = kFSCatInfoFinderInfo;
        er = FSGetCatalogInfo ( &targetRef, infoBit,
             &catInfo, NULL, &targetSpec, NULL ); finish;
        finderFlag = catInfo.finderInfo[8] * 256 + catInfo.finderInfo[9];
        if ( ( finderFlag & kIsAlias) == 0 ) { 
            strcpy ( resultPath, aliasPath );
        } else { 
//            er = ResolveAliasFile ( &targetSpec, TRUE, &b1, &b2 ); finish;
//            er = FSpMakeFSRef ( &targetSpec, &targetRef ); finish;
//            er = FSRefMakePath ( &targetRef, (unsigned char *)resultPath, 1024 ); finish;
			er = FSResolveAliasFile (&targetRef, TRUE, &b1, &b2);    finish;
            resultPath = (char*)NewPtr(1024);
            er = FSRefMakePath (&targetRef,(unsigned char *) resultPath, 1024);      finish;
        }
    }
procEnd:
    return er;
}
*/
