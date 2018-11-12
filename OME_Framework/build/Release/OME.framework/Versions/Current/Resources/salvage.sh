#!/bin/sh

# salvage.sh
# OME_Framework
#
# Created by Masayuki Nii on 09/05/03.
# Copyright 2009 __MyCompanyName__. All rights reserved.

export PATH=/bin:/usr/bin:/usr/local/bin:${PATH}
PREFPATH="${HOME}"/Library/Preferences/OME_Preferences
JAVA=/usr/bin/java
TOUCH=/usr/bin/touch
RM=/bin/rm
PERL=/usr/bin/perl
MAILWRITER=OME.mailwriter.OME_MailWriter
DOWNLOAD=OME.downloadmails.OME_DownloadMails
SENDMAIL=OME.sendmail.OME_SendMail
UNREAD=OME.UnreadUtility
PROCESS=OME.messagemaker.MailProcessor

if [ -d "${PREFPATH}.localized" ]; then
    OMEPREF="${PREFPATH}.localized"
elif [ -d "${PREFPATH}" ]; then
    OMEPREF="${PREFPATH}"
else
    exit 1;
fi

OMEAPPPATH=`${PERL} -ne'if(/OMEApplicationsPath=/){s/OMEApplicationsPath="(.+)"/$1/;print;exit;}' "${OMEPREF}"/Behavior_Info.txt`
if [ ! -d "${OMEAPPPATH}" ]; then
    [ -d /Library/Frameworks/OME.framework/Versions/Current/Resources ] || exit 1;
    OMEAPPPATH="/Library/Frameworks/OME.framework/Versions/Current/Resources"
fi
if [ "${OMEAPPPATH}" = "/Library/Frameworks/OME.framework/Versions/Current/Resources" ]; then
    CLASSPATH="${OMEAPPPATH}"/Java/OME_lib.jar
    RESOLVALIAS="${OMEAPPPATH}"/resolvalias
else
    CLASSPATH="${OMEAPPPATH}"/tools/OME_lib.jar
    RESOLVALIAS="${OMEAPPPATH}"/tools/resolvalias
fi
OMETEMPPATH=`${PERL} -ne'if(/TempFolderPath=/){s/TempFolderPath="(.+)"/$1/;print;exit;}' "${OMEPREF}"/Behavior_Info.txt`
if [ ! -d "${OMETEMPPATH}" ]; then
    [ -d /Library/Frameworks/OME.framework/Versions/Current/Resources ] || exit 1;
	OMEROOT=`${RESOLVALIAS} "${OMEPREF}"/OME_Root`
    OMETEMPPATH="${OMEROOT}"/temp
fi

if [ -f /var/mail/${USER} ]; then
	cp /var/mail/${USER} "${OMETEMPPATH}"/originals
	echo '' > /var/mail/${USER}
	${JAVA} -Djava.awt.headless=true -cp "${CLASSPATH}" "${PROCESS}"
fi