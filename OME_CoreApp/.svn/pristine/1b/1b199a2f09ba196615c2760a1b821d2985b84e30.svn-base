/* main.h */

#include <Carbon/Carbon.h>

//#define DEBUG_MSG

//Prototypes
int main(void);
void updateOMERootShortcut(void);
pascal OSErr HandleOpenEvent(const AppleEvent *theAppleEvent, AppleEvent *reply, UInt32 theRefCon);
pascal OSErr HandleQuitEvent(const AppleEvent *theAppleEvent, AppleEvent *reply, UInt32 theRefCon);
pascal OSErr HandleOpenLocation(const AppleEvent *theAppleEvent, AppleEvent *reply, UInt32 theRefCon);
pascal void TimerAction(EventLoopTimerRef inTimer, EventLoopIdleTimerMessage inState, void *inUserData);

Boolean isSameFileName( HFSUniStr255 * f1, HFSUniStr255 *f2 );
Boolean CheckSuffixOfFileName( HFSUniStr255 * fn, char * suffix);
char * GetCStringPtrFromFileName(  HFSUniStr255 * fn );
Boolean isAlphaNumeric(char c);
unsigned char getCharFromHex(char d1, char d2);
OSStatus FSMakeFSRefCFString(FSRef *parentFolder, CFStringRef itemName, FSRef *targetRef);
OSStatus MakeOMEPrefrerencesFSRef(FSRef *omePrefRef);
OSStatus MakeOMERootFSRef(FSRef *omeRootRef);
OSStatus MakeOMETempFSRef(FSRef *omeTempRef);
OSStatus MakeOMEUnreadAliasFSRef(FSRef *omeUnreadAliasRef);
OSStatus MakeOMERootAltFSRef(FSRef *omeRootRef);
OSStatus MakeOMETempAltFSRef(FSRef *omeTempRef);
OSStatus MakeOMEUnreadAliasAltFSRef(FSRef *omeUnreadAliasRef);

void AliasListInitialize();
void AliasListFinalize();
void RefreshAliasList();
void DeleteAlias( FSRef * targetMailFile );
void Listing();
OSStatus MakeLaunchInfoFromAlias(
			LSLaunchFSRefSpec * launchInfo,
			CFStringRef aliasName,
			char * altanateAppPath);
OSStatus MakeLaunchInfoFromAlias3Alts(
			LSLaunchFSRefSpec * launchInfo,
			CFStringRef aliasName,
			char * altanateAppPath1,
			char * altanateAppPath2,
			char * altanateAppPath3);