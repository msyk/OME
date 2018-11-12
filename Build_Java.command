#!/bin/sh

OME_Root=~/Documents/OME_Lion
OME_Java_Root="${OME_Root}/OME_JavaSrc"
Maven_Path=~/Documents/OME_Lion/apache-maven-3.3.3/bin/mvn
Generated_Lib="${OME_Java_Root}/target/OME-8.0.jar"

cd "${OME_Java_Root}"

DateStr=`date +%Y/%m/%d`

cat > src/main/java/OME/sendmail/BuildDate.java <<END
package OME.sendmail;
public class BuildDate {
    public static String ofString()	{
        return "$DateStr";
    }
}
END

${Maven_Path} clean
${Maven_Path} validate
${Maven_Path} compile
${Maven_Path} package

cp ${Generated_Lib} /Library/Frameworks/OME.framework/Resources/Java/
cp ${OME_Java_Root}/lib/mail.jar /Library/Frameworks/OME.framework/Resources/Java/
cp ${OME_Java_Root}/lib/activation.jar /Library/Frameworks/OME.framework/Resources/Java/
cp ${OME_Java_Root}/lib/pop3.jar /Library/Frameworks/OME.framework/Resources/Java/
