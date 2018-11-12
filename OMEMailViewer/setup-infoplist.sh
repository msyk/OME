#!/bin/sh

DateStr=`date +%Y/%m/%d`
DateStr=`echo -n $DateStr`
ThisYear=`date +%Y`
ThisYear=`echo -n $ThisYear`

fileList=`find . -name "InfoPlist-Seed.strings"`

for target in $fileList
do
	genFile=${target%-*}
	sed -e "s%@@VERSION@@%$DateStr%g" $target\
	|sed -e "s%@@THISYEAR@@%$ThisYear%g" \
	|sed -e s%@@COMPANY@@%OME\(Open\ Mail\ Environment:http:\/\/msyk.net\/ome\)%g > $genFile.strings

done

