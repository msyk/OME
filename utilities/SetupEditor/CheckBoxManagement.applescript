
on clicked theObject
	set theWindow to window of theObject
	set mrVal to state of button "mailreader" of theWindow
	set mwVal to state of button "mailwriter" of theWindow
	set hrVal to state of button "htmlreader" of theWindow
	if (mrVal is 1) and (mwVal is 1) and (hrVal is 1) then
		set title of button "selall" of theWindow to "すべて解除"
	else
		set title of button "selall" of theWindow to "すべて設定"
	end if
end clicked
