#!/bin/csh

set DevDir=$0:h
set DateStr=`date +%Y%m%d`

cd ${DevDir}
rm -rf OME-Installer.pkg

rm -rf InstallRoot-main
mkdir InstallRoot-main
cd InstallRoot-main
set InstallRoot=`pwd`

mkdir Applications
cd Applications
mkdir OME_Applications.localized
cd OME_Applications.localized
set OMEAppDir=`pwd`
mkdir .localized
cd .localized
echo '"OME_Applications"="OME-オープンメール環境";' > ja.strings
echo '"OME_Applications"="OME-Open Mail Environment";' > en.strings

cd ${InstallRoot}
mkdir Library
cd Library
mkdir Frameworks
mkdir Spotlight
mkdir QuickLook

#cd ${DevDir}/../OME_JavaCore
#xcodebuild -alltargets clean
#xcodebuild -target OME_lib -configuration Deployment

cd ${DevDir}/../OME_JavaCore2
xcodebuild -alltargets clean
xcodebuild -target OME_JavaCore2 -configuration Release

cd ${DevDir}/../cocoaomenu
xcodebuild -alltargets -project CocoaOMEnu3.xcodeproj clean
xcodebuild -target CocoaOMEnu -configuration Release -project CocoaOMEnu3.xcodeproj
cp -Rf build/Release/CocoaOMEnu.app ${OMEAppDir}

cd ${DevDir}/../OME_CoreApp
xcodebuild -alltargets clean
xcodebuild -target OME_Core -configuration Deployment
cp -Rf build/Deployment/OME_Core.app ${OMEAppDir}

cd ${DevDir}/../browser-xcos
xcodebuild -alltargets clean
xcodebuild -target OME_BrowserXCoS -configuration Deployment
cp -Rf build/Deployment/OME_BrowserXCoS.app ${OMEAppDir}

#cd ${DevDir}/../utilities/OpenAllUnreadMessage
#xcodebuild -alltargets clean
#xcodebuild -target OpenAllUnreadMessage -configuration Deployment
#cp -Rf build/Deployment/OpenAllUnreadMessage.app ${OMEAppDir}

cd ${DevDir}/../utilities/OpenAllUnreadMessage2
xcodebuild -alltargets clean
xcodebuild -target OpenAllUnreadMessage2 -configuration Release
cp -Rf build/Release/OpenAllUnreadMessage.app ${OMEAppDir}

cd ${DevDir}/../utilities/SetupEditor
xcodebuild -alltargets clean
xcodebuild -target SetupEditor -configuration Deployment
cp -Rf build/Deployment/SetupEditor.app ${OMEAppDir}

cd ${DevDir}/../utilities/OMEchpasswd
xcodebuild -alltargets clean
xcodebuild -target OMEchpasswd -configuration Deployment
cp -Rf build/Deployment/OMEchpasswd.app ${OMEAppDir}

cd ${DevDir}/../OME_Apps
xcodebuild -alltargets clean
xcodebuild -target OME_DownloadMails_ObjC -configuration Release
xcodebuild -target OME_SendMail_ObjC -configuration Release
cp -Rf build/Release/OME_DownloadMails.app ${OMEAppDir}
cp -Rf build/Release/OME_SendMail.app ${OMEAppDir}

cd ${DevDir}/../OME_Tools
xcodebuild -alltargets clean
xcodebuild -target kcpassword -configuration Deployment
xcodebuild -target resolvalias -configuration Deployment
xcodebuild -target makenewalias -configuration Deployment
xcodebuild -target mergeres -configuration Deployment
xcodebuild -target updatefinder -configuration Deployment

cd ${DevDir}/../utilities/InBoxNotify
xcodebuild -alltargets clean
xcodebuild -target InBoxNotify -configuration Deployment

cd ${DevDir}/../OME_Framework
xcodebuild -alltargets clean
xcodebuild -target OME -configuration Release
cp -Rf build/Release/OME.framework ${InstallRoot}/Library/Frameworks

cd ${DevDir}/../OMEMailViewer
xcodebuild -alltargets clean
xcodebuild -target OMEMailViewer -configuration Deployment
cp -Rf build/Deployment/OMEMailViewer.app ${OMEAppDir}

cd ${DevDir}/../MetadataImporter
xcodebuild -alltargets clean
xcodebuild -target OME -configuration Release
cp -Rf build/Release/OME.mdimporter ${InstallRoot}/Library/Spotlight

cd ${DevDir}/../Cocoa/OMESetUp
xcodebuild -alltargets clean
xcodebuild -target OMESetUp -configuration Release
cp -Rf build/Release/OMESetUp.app ${OMEAppDir}

cd ${DevDir}/../OME_Apps
xcodebuild -alltargets clean
xcodebuild -target OME_DownloadMails_ObjC -configuration Release
xcodebuild -target OME_SendMail_ObjC -configuration Release
xcodebuild -target OME_MailWriter_ObjC -configuration Release
cp -Rf build/Release/OME_DownloadMails.app ${OMEAppDir}
cp -Rf build/Release/OME_SendMail.app ${OMEAppDir}
cp -Rf build/Release/OME_MailWriter.app ${OMEAppDir}

cd ${DevDir}/../OME_QuickLook_Plug-In
xcodebuild -alltargets clean
xcodebuild -target OME_QuickLook_Plug-In -configuration Release
cp -Rf build/Release/OME_QuickLook_Plug-In.qlgenerator ${InstallRoot}/Library/QuickLook

#cd ${DevDir}/InstallRoot-main/Library/Frameworks
#tar cfz OME.framework.tgz OME.framework
#rm -rf OME.framework

cd ${DevDir}
set packagemaker_path=/Developer/Applications/Utilities/PackageMaker.app/Contents/MacOS/PackageMaker
$packagemaker_path --doc OME-Installer.pmdoc --out OME-Installer.pkg -v

cd ${DevDir}
rm -rf OME-${DateStr} OME-${DateStr}.dmg
mkdir OME-${DateStr}
cp -Rf OME-Installer.pkg OME-${DateStr}
hdiutil create -srcFolder OME-${DateStr} OME-${DateStr}.dmg

#cd ${DevDir}/../OME_JavaCore
#rm -rf javadocs
#mkdir javadocs
#javadoc -private -J-Duser.language=en -header 'OME_Java_Sources' \
#	-d javadocs -sourcepath src -docencoding "UTF-8" -encoding "UTF-8" \
#		OME OME.textformatter OME.mailformatinfo OME.downloadmails \
#		OME.sendmail OME.messagemaker OME.mailwriter OME.listingdeamon
