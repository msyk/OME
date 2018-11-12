/*

kctool by msyk(msyk@mac.com)
2001/12/17

キーチェーンからパスワードを取り出す：
    kcpassword get ServerName AccountName [Protocol]
キーチェーンにパスワードを設定する：
    kcpassword set ServerName AccountName Password [Protocol]

ServerName	サーバ名
AccountName アカウント名
Password パスワード
Protocol プロトコルを指定するがMac OSのOSタイプ形式の4バイトデータを指定する
    （省略すると任意のプロトコル）

結果コードが0なら取り出しや設定がうまく行われたことを示す。そうでなければエラー。

パスワードの取り出しでは、取り出し結果が標準出力に書き出されるが、改行は出力されないので、
出力結果がそのままパスワードである。

パスワードの設定では、すでに指定するサーバ、アカウントのアイテムがあれば、それを
削除して、新たなパスワードを設定する。

ポート、認証タイプについては、「任意」となっている。また、プロトコルを指定しないと
「任意」となるようにしている。

OMEでは、/Applications/OME_Applications/toolsに自動的にインストールされるものとする。

ref.

Keychain Manager:
http://developer.apple.com/techpubs/macosx/Carbon/securityservices/keychainmanager/keychainmanager.html

Keychain Managerは近々にDeprecatedになるだろうということで、こっちのフレームワークを使う(2006/12/31)
Keychain Services Reference:
http://developer.apple.com/documentation/Security/Reference/keychainservices/index.html

*/
/* main.c */

#include <Carbon/Carbon.h>
#include <Security/Security.h>

int main(int argc, char *argv[])
{
    OSStatus er;
	int returnValue = 0;
	Handle passwordData = NewHandle(100);
    SecProtocolType protocol;
    UInt16 port;
    SecAuthenticationType authType;
    UInt32 actualLength;
    SecKeychainItemRef itemRef;

    if(argc < 2)	{
        printf("Usage:\nkcpassword get ServerName AccountName [Protocol]\nkcpassword set ServerName AccountName Password [Protocol]\n");
		returnValue = 9;
		goto EndOfProcess;
    }

    if(strcmp(argv[1], "get") == 0)	{
        if((argc < 4) || (argc >5))	{
            printf("Invalid Parameter. If you specify 'get' as verb, you have to set ServerName, UserName(, Protocol).\n");
			returnValue = 2;
			goto EndOfProcess;
        }

        if(argc == 5)
			if ( CFByteOrderLittleEndian == CFByteOrderGetCurrent() )
				protocol = argv[4][0]*256*256*256 + argv[4][1]*256*256 + argv[4][2]*256 + argv[4][3];
			else
				protocol = argv[4][3]*256*256*256 + argv[4][2]*256*256 + argv[4][1]*256 + argv[4][0];
		else
            protocol = kAnyProtocol;

		er = SecKeychainFindInternetPassword( 
					NULL, strlen(argv[2]), argv[2], 0, NULL, strlen(argv[3]), argv[3], 0, NULL,
					0, 0, kSecAuthenticationTypeDefault, &actualLength, (void**)passwordData, NULL);
        if(er != noErr)
            printf("Error, Can't get password: error number=%d\n", (int)er);
        else	{
            (*passwordData)[actualLength] = 0;
            printf((char*)*passwordData);
        }
    }
    else if(strcmp(argv[1], "set") == 0)	{
        if((argc < 5) || (argc > 6))	{
            printf("Invalid Parameter. If you specify 'set' as verb, you have to set ServerName, UserName, Password(, Protocol).\n");
			returnValue = 2;
			goto EndOfProcess;
        }
        port = kAnyPort;
        authType = kAnyAuthType;
        if(argc == 6)
			if ( CFByteOrderLittleEndian == CFByteOrderGetCurrent() )
				protocol = argv[5][0]*256*256*256 + argv[5][1]*256*256 + argv[5][2]*256 + argv[5][3];
			else
				protocol = argv[5][3]*256*256*256 + argv[5][2]*256*256 + argv[5][1]*256 + argv[5][0];
		else
			protocol = kAnyProtocol;

		er = SecKeychainFindInternetPassword( 
					NULL, strlen(argv[2]), argv[2], 0, NULL, strlen(argv[3]), argv[3], 0, NULL,
					0, protocol, kSecAuthenticationTypeDefault, &actualLength, (void**)passwordData, &itemRef);

		if(er == noErr)	{
			er = SecKeychainItemDelete(itemRef);
            if(er != noErr)	{
                printf("Error in deleting item.\n");
                returnValue = 6;
				goto EndOfProcess;
            }
        }
		er = SecKeychainAddInternetPassword( 
					NULL, strlen(argv[2]), argv[2], 0, NULL, strlen(argv[3]), argv[3], 0, NULL,
					0, protocol, kSecAuthenticationTypeDefault, strlen(argv[4]), argv[4], NULL);
        
        if(er != noErr)	{
            printf("Error, Can't add password: error number=%d\n", (int)er);
			returnValue = 1;
		}
    }
    else	{
        printf("First parameter accepts only 'get' or 'set', and You must set it !\n");
        returnValue = 4;
    }
	
EndOfProcess:	
	return returnValue;
}