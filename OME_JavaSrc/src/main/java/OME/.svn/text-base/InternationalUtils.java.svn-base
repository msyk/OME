package OME;

import java.io.File;
import java.util.*;

/** 国際化の機能を実現するためのユーティリティ集。リソースバンドルの一連の機能だと、データとしてStringしか扱えないので、
 同じようなメカニズムで、クラス名とそのロード、およびファイルを扱えるようにした。
 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 * 2002/2/14:新居:ファイルを作成
 * 2004/3/23:新居:やっと履歴を書いた。プロジェクト内のグループを移動した。パッケージはそのままにした。
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
 */
public class InternationalUtils {

    /**	リソースバンドルに従い、与えたクラス名とロケール名を持つクラスをロードし、インスタンス化する
     @param className ロードするクラス名
     @param loc　対象となるロケール
     @return クラスをロードしてインスタンス化した参照を戻す。ロードできないときはnullを戻す。
     */
    public static Object getClassInstance(String className, Locale loc) {
        Object returnObj = null;
        Iterator nameList = makeResourceNameSeries(className, loc, "");
        while (nameList.hasNext()) {
            String resClassName = (String) nameList.next();
            //Logging.writeMessage("Checking: "+resClassName);
            try {
                returnObj = Class.forName(resClassName).newInstance();
                if (returnObj != null) return returnObj;
            } catch (Exception e) {
                //	Nothing to do ...
                //                e.printStackTrace();
                //                System.out.println(e.getMessage());
            }
        }
        return returnObj;
    }

    /**	リソースバンドルに従い、与えたファイル名とロケール名を持つファイルが存在するかを探し、存在すればそのFileオブジェクトを戻す
     @param searchDir 検索するディレクトリ
     @param fileName ファイル名
     @param exteinsion 拡張子
     @param loc　対象となるロケール
     @return ファイルへの参照。ファイルがないときはnullを戻す。
     */
    public static File getExistsFile(File searchDir, String fileName, String extention, Locale loc) {
        File existsFile = null;
        Iterator nameList = makeResourceNameSeries(fileName, loc, extention);
        while (nameList.hasNext()) {
            String fName = (String) nameList.next();
            //Logging.writeMessage("Checking: "+fName);
            existsFile = new File(searchDir, fName);
            if (existsFile.exists()) return existsFile;
        }
        return null;
    }

    /** Localeを示す文字列から、Localeオブジェクトを作成する
     *	@param locString	Localeを示す文字列（たとえば、ja_JP_Mac）
     *	@return	Localeオブジェクト
     */
    public static Locale createLocaleFromString(String locString) {
        Locale genLocale = null;
        StringTokenizer tokenParam = new StringTokenizer(locString, "_");
        String c = null, r = null, v = null;
        try {
            c = tokenParam.nextToken();
            r = tokenParam.nextToken();
            v = tokenParam.nextToken();
        } catch (Exception e) { /* Do Nothing */}
        if ((c != null) && (r != null)) if (v == null)
            genLocale = new Locale(c, r);
        else
            genLocale = new Locale(c, r, v);
        return genLocale;
    }

    private static Iterator makeResourceNameSeries(String resName, Locale loc, String suffix) {
        String lang = loc.getLanguage();
        String region = loc.getCountry();
        String vari = loc.getVariant();

        List nameList = new ArrayList();
        if (!vari.equals("")) nameList.add(resName + "_" + lang + "_" + region + "_" + vari + suffix);
        if (!loc.equals("")) nameList.add(resName + "_" + lang + "_" + region + suffix);
        if (!lang.equals("")) nameList.add(resName + "_" + lang + suffix);
        nameList.add(resName + suffix);
        return nameList.iterator();
    }
}
