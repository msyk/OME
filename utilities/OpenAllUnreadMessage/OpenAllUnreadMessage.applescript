on launched theObject
	try
		tell application "Finder"
			set prefFolder to folder "Preferences" of folder "Library" of home
			set omePref to getFolderReference(prefFolder, "OME_Preferences") of me
			set behaviorFile to file "Behavior_Info.txt" of omePref
			--set behaviorPath to (URL of behaviorFile)
			--set behaviorPath to (characters 17 thru -1 of behaviorPath) as string
			set fileRef to open for access (behaviorFile as alias)
			set behaviorInfos to read fileRef to (get eof fileRef) using delimiter {ASCII character 10, ASCII character 13}
			close access fileRef
		end tell
		--set behaviorInfos to (call method "getFileContentsAsArray:" of class "FileReader" with parameters {behaviorPath})
		
		
		set isTempSet to false
		repeat with eachItem in behaviorInfos
			if eachItem starts with "TempFolderPath=" then
				set isTempSet to true
				set tempFolderPath to POSIX file ((characters 17 thru -2 of eachItem) as string)
				set tempFolder to alias (tempFolderPath as string)
			end if
		end repeat
		if isTempSet is false then
			tell application "Finder"
				set omeRoot to original item of file "OME_Root" of omePref
				set tempFolder to getFolderReference(omeRoot, "temp") of me
			end tell
		end if
		set isAppSet to false
		repeat with eachItem in behaviorInfos
			if eachItem starts with "OMEApplicationsPath=" then
				set isAppSet to true
				tell application "Finder"
					set param to (characters 22 thru -2 of eachItem) as string
					set appFolder to folder (param as POSIX file)
					log (param)
					--set appFolder to folder (my convertPOSIXtoMac((characters 22 thru -2 of eachItem) as string))
				end tell
			end if
		end repeat
		if isAppSet is false then
			tell application "Finder"
				set appFolder to folder "OME_Applications.localized" of folder "Applications" of startup disk
			end tell
		end if
		set isLimitToOpen to false
		repeat with eachItem in behaviorInfos
			if eachItem starts with "LimitToOpen=" then
				set isLimitToOpen to true
				set limitToOpen to ((characters 14 thru -2 of eachItem) as string) as number
			end if
		end repeat
		if isLimitToOpen is false then
			set limitToOpen to 10
		end if
		
		set omeRootAltList to {"OME_Root_Alt", "OME_Root_Alt1", "OME_Root_Alt2", "OME_Root_Alt3", "OME_Root_Alt4"}
		tell application "Finder"
			set omeCore to file "OME_Core.app" of appFolder
			set unreadFolder to folder "unreadAliases" of tempFolder
			set unreadFiles to files of unreadFolder
			set filesModDates to {}
			repeat with oneFile in unreadFiles
				set end of filesModDates to modification date of oneFile
			end repeat
			repeat with oneFolderName in omeRootAltList
				try
					if exists (item (oneFolderName as string) of omePref) then
						log ((oneFolderName as string) & " recognized.")
						set altFolder to original item of (item (oneFolderName as string) of omePref)
						set moreUnreadFiles to unreadFiles & (files of folder "unreadAliases" of folder "temp" of altFolder)
						set unreadFiles to unreadFiles & moreUnreadFiles
						repeat with oneFile in moreUnreadFiles
							set end of filesModDates to modification date of oneFile
						end repeat
					end if
				end try
			end repeat
			set countFiles to length of unreadFiles
		end tell
		
		set extractCount to countFiles - 1
		if limitToOpen is less than countFiles then set extractCount to limitToOpen
		repeat with i from 1 to extractCount
			set minIndex to i
			repeat with j from i + 1 to countFiles
				if (item j of filesModDates) as date < (item minIndex of filesModDates) as date then
					set minIndex to j
				end if
			end repeat
			set temp to item minIndex of filesModDates
			set item minIndex of filesModDates to item i of filesModDates
			set item i of filesModDates to temp
			set temp to item minIndex of unreadFiles
			set item minIndex of unreadFiles to item i of unreadFiles
			set item i of unreadFiles to temp
		end repeat
		if limitToOpen is less than countFiles then
			set unreadFiles to (items 1 thru limitToOpen of unreadFiles)
		end if
		
		tell application "Finder"
			open reverse of unreadFiles using omeCore
		end tell
		
		set appName to "GrowlHelperApp"
		try
			tell application "Finder"
				set growlProc to process appName
				set helperAppFile to file of growlProc
			end tell
			tell application appName to quit
			do shell script "sleep 2"
			tell application "Finder" to open helperAppFile
		on error x
			log ("Error in closing Growl windows:" & x)
		end try
		
	on error msg number erNum
		display dialog ("OME:Error Number and Message" & erNum & return & msg) as string with icon 0 buttons {"OK"} giving up after 15
		log ("OME:Error Number and Message" & erNum & "/" & msg) as string
	end try
	
	quit
end launched

on getFolderReference(parent, fName)
	tell application "Finder"
		try
			return folder fName of parent
		on error num
			return folder (fName & ".localized") of parent
		end try
	end tell
end getFolderReference
(*
on convertPOSIXtoMac(str)
	return (str as POSIX file) as string
end convertPOSIXtoMac
*)

on getHomeFolder()
	tell application "Finder"
		set classOfHome to (class of home) as string
		if classOfHome is "item" then
			set userName to (do shell script "printenv USER" as string)
			set homeFolder to item userName of folder "Users" of startup disk
		else
			set homeFolder to home
		end if
		return original item of homeFolder
	end tell
end getHomeFolder