-- OMEchpasswd.applescript
-- OMEchpasswd

--  Created by Koji Mochizuki on Sun Dec 29 2002.
--  Copyright (c) 2002 Koji Mochizuki. All rights reserved.

-- タスク切り替えボタン用のテキストを保存する
global regist_popup_msg
global show_popup_msg

on clicked theObject
	tell window "OME chpasswd"
		-- 必要なパラメータを取得する。取得はテキストで。
		set sname to string value of text field "servername"
		set uname to string value of text field "username"
		set passwd to string value of text field "password"
		
		-- 登録か表示かを選択する
		tell popup button "cmd"
			set cmdName to title of current menu item
			if cmdName is regist_popup_msg then
				set selector to "set"
			else if cmdName is show_popup_msg then
				set selector to "get"
			end if
		end tell
		
		--- 実行コマンドを作る。
		set parameter to selector & " " & sname & " " & uname & " " & passwd
		set cmdStr to "/Library/Frameworks/OME.framework/Resources/kcpassword" & " " & parameter
	end tell
	
	--- コマンド実行時の各メッセージ（ローカライズ版）を取得
	set registerr_msg to localized string "registerr_key" from table "Localized"
	set geterr_msg to localized string "geterr_key" from table "Localized"
	set registok_msg to localized string "regist_ok_key" from table "Localized"
	set passwd_msg to localized string "passwd_key" from table "Localized"
	
	-- コマンドを実行する。
	if selector is "set" then
		try
			do shell script cmdStr
		on error
			display alert registerr_msg attached to window "OME chpasswd"
			return
		end try
		display dialog registok_msg
	else if selector is "get" then
		try
			do shell script cmdStr
		on error
			display alert geterr_msg attached to window "OME chpasswd"
			return
		end try
		set passwd to result
		display alert passwd_msg & passwd as text attached to window "OME chpasswd"
	end if
end clicked

on awake from nib theObject
	--- ローカライズメッセージを取得する
	set exec_btn_msg to localized string "execute_key" from table "Localized"
	set regist_popup_msg to localized string "regist_key" from table "Localized"
	set show_popup_msg to localized string "show_key" from table "Localized"
	
	---　各ボタン、メニューにローカライズメッセージを適用する。
	set title of button "execute" of window "OME chpasswd" to exec_btn_msg
	tell popup button "cmd" of window "OME chpasswd"
		set title of menu item "set" to regist_popup_msg
		set title of menu item "get" to show_popup_msg
	end tell
end awake from nib
