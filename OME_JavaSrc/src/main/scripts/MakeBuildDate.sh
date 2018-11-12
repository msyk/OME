DateStr=`date +%Y/%m/%d`

cat > src/main/java/OME/sendmail/BuildDate.java <<END
package OME.sendmail;
public class BuildDate {
    public static String ofString()	{
        return "$DateStr";
    }
}
END
