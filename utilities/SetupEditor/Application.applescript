
property targetApp : ""
property theWindow : ""

on open dragedItems
	tell application "Finder"
		set oneItem to item 1 of dragedItems
		set fType to file type of oneItem
		set appName to name of oneItem
	end tell
	if fType is "APPL" then
		set targetApp to oneItem
		set string value of text field "msg" of theWindow to ""
		set string value of text field "appName" of theWindow to appName
		set enabled of button "setButton" of theWindow to true
	else
		set enabled of button "setButton" of theWindow to false
	end if
end open

on clicked theObject
	set buttonName to name of theObject
	set theWindow to window of theObject
	if buttonName is "selectButton" then
		set apps to choose file with prompt "アプリケーションを指定します。" of type "APPL"
		set targetApp to apps
		tell application "Finder" to set appName to name of apps
		set string value of text field "appName" of theWindow to appName
		set enabled of button "setButton" of theWindow to true
	else if buttonName is "cancelButton" then
		quit
	else if buttonName is "selall" then
		set mrVal to state of button "mailreader" of theWindow
		set mwVal to state of button "mailwriter" of theWindow
		set hrVal to state of button "htmlreader" of theWindow
		if (mrVal is 1) and (mwVal is 1) and (hrVal is 1) then
			set state of button "mailreader" of theWindow to 0
			set state of button "mailwriter" of theWindow to 0
			set state of button "htmlreader" of theWindow to 0
			set title of button "selall" of theWindow to "すべて設定"
		else
			set state of button "mailreader" of theWindow to 1
			set state of button "mailwriter" of theWindow to 1
			set state of button "htmlreader" of theWindow to 1
			set title of button "selall" of theWindow to "すべて解除"
		end if
	else if buttonName is "setButton" then
		set fName to ""
		if state of button "mailreader" of theWindow is 1 then
			makeAliasToApp("Mail_Reader") of me
		end if
		if state of button "mailwriter" of theWindow is 1 then
			makeAliasToApp("Mail_Writer") of me
		end if
		if state of button "htmlreader" of theWindow is 1 then
			makeAliasToApp("HTML_Reader") of me
		end if
		quit
	end if
end clicked

on makeAliasToApp(aliasName)
	tell application "Finder"
		set OMEPrefFolder to my getFolderReference(folder "Preferences" of folder "Library" of home, "OME_Preferences")
		if exists file aliasName of OMEPrefFolder then delete file aliasName of OMEPrefFolder
		make new alias file at OMEPrefFolder to targetApp with properties {name:aliasName}
	end tell
	
end makeAliasToApp

on getFolderReference(parent, fName)
	tell application "Finder"
		try
			return folder fName of parent
		on error num
			return folder (fName & ".localized") of parent
		end try
	end tell
end getFolderReference

on idle theObject
	(*Add your script here.*)
end idle

on opened theObject
	set theWindow to theObject
end opened

on became main theObject
	set mrVal to state of button "mailreader" of theObject
	set mwVal to state of button "mailwriter" of theObject
	set hrVal to state of button "htmlreader" of theObject
	if (mrVal is 1) and (mwVal is 1) and (hrVal is 1) then
		set title of button "selall" of theWindow to "すべて解除"
	else
		set title of button "selall" of theWindow to "すべて設定"
	end if
end became main

