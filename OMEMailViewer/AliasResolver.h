/*
 *  $Id: AliasResolver.h,v 1.2 2005/09/03 13:42:12 wakimoto Exp $
 *
 *  Created by Masayuki Nii on Sun Jan 27 2002.
 */

#import <Foundation/Foundation.h>

@interface AliasResolver : NSObject
{
}

//+(OSStatus) resolve ( const char *resultPath, char *aliasPath );

+ (NSString *)resolve: (NSString *)path;

@end