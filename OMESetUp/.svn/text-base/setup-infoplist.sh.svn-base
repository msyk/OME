#!/bin/sh

DateStr=`date +%Y/%m/%d`
DateStr=`echo -n $DateStr`
ThisYear=`date +%Y`
ThisYear=`echo -n $ThisYear`

fileList=`find . -name "InfoPlist-Seed.strings"`

for target in $fileList
do
	genFile=${target%-*}
	iconv -f UTF-16 -t UTF-8 $target \
	|sed -e "s%@@VERSION@@%$DateStr%g" \
	|sed -e "s%@@THISYEAR@@%$ThisYear%g" \
	|sed -e s%@@COMPANY@@%OME\(Open\ Mail\ Environment:http:\/\/mac-ome.jp\)%g \
	|iconv -f UTF-8 -t UTF-16 > $genFile.strings

done

