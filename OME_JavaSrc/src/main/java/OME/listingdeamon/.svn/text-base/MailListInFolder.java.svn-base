package OME.listingdeamon;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.*;
import java.util.*;

import javax.mail.internet.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

import OME.*;

/**
 特定のフォルダにあるメールの一覧を管理するクラス<p>
 フォルダへの参照を引数に持つコンストラクタを使って、フォルダの内容一覧を作成する。
 toFileメソッドでその結果をフォルダに書き戻す…というのが基本的な使い方。

 *
 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 *
 * 2003/11/10:新居:とりあえず、クラスの簡単な仕様を考え、ファイルを作った。
 * 2004/3/16:新居:フォルダを指定して、そのフォルダ内にファイル一覧のXMLファイルを生成できるようにした。
 * 2004/3/17:新居:既存のXMLファイルを読み込み、フォルダの現状と突き合わせてアップデートできるようにした。
 * 2004/3/22:新居:バグ修正。スタイルシートファイルをtoolsにおいてもOKにした。
 * 2009/6/28:新居:OME_JavaCore2へ移動
 */

public class MailListInFolder extends LinkedList {

    final private String infoFileName = "contents.xml";

    final private String headerStartSign = "\n<!-- Real Mail Headers -->\n";

    /**　引数なしのコンストラクタは外部から利用できないようにする */
    private MailListInFolder() {};

    /** 特定のフォルダのメール一覧をオブジェクトとして用意する。<p>
     一覧ファイルがない場合は、そのフォルダのメールをスキャンして、一覧ファイルを作成する。
     一覧ファイルがある場合は、そのファイルを単に読み込んでオブジェクトを作る。
     つまり、一覧ファイルの強制再作成は、一覧ファイルを削除してなんかさせればいいということになる。
     @param targetFolder メール一覧を作成するフォルダへの参照
     */
    public MailListInFolder(File targetFolder) {
        this(targetFolder, false);
    }

    /** 特定のフォルダのメール一覧をオブジェクトとして用意する。
     @param targetFolder メール一覧を作成するフォルダへの参照
     @param forceUpdate trueなら既存のxml一覧ファイルを無視して存在するメールからリストを構築。
     falseなら、既存のxmlファイルを探して存在すればその情報をもとにリストを作成
     */
    public MailListInFolder(File targetFolder, boolean forceUpdate) {
        thisFolder = targetFolder;

        dateSortedSet = new TreeSet(new OneMailDateComparator());
        subjectSortedSet = new TreeSet(new OneMailSubjectComparator());
        fromSortedSet = new TreeSet(new OneMailFromComparator());
        threadSortedList = new ArrayList();

        File listingDataFile = new File(thisFolder, infoFileName);
        File[] itemList = thisFolder.listFiles(new MailItemFilter());
        List filesList = new ArrayList(Arrays.asList(itemList));

        System.out.println("# Detect : " + itemList.length + " files");

        System.out.println("# Checkpoint 1: " + (new Date().getTime()) + "ms");

        if ((!forceUpdate) && listingDataFile.exists()) {
            Document dom = null;
            int processingCounter = 0;
            try {
                dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(listingDataFile);
                System.out.println("# Checkpoint Z: " + (new Date().getTime()) + "ms");
                Element rootElement = dom.getDocumentElement();
                NodeList mailNodeList = rootElement.getElementsByTagName("mail-info");
                for (int i = 0; i < mailNodeList.getLength(); i++) {
                    Element targetNode = (Element) (mailNodeList.item(i));
                    String fromThisNode = "", dateThisNode = "", subjectThisNode = "", messageIDThisNode = "", parentIDThisNode = "", fileNameThisNode = "";
                    try {
                        fromThisNode = targetNode.getElementsByTagName("from").item(0).getFirstChild().getNodeValue();
                    } catch (Exception e) { /* do nothing */}
                    try {
                        dateThisNode = targetNode.getElementsByTagName("date").item(0).getFirstChild().getNodeValue();
                    } catch (Exception e) { /* do nothing */}
                    try {
                        subjectThisNode = targetNode.getElementsByTagName("subject").item(0).getFirstChild()
                                .getNodeValue();
                    } catch (Exception e) { /* do nothing */}
                    try {
                        messageIDThisNode = targetNode.getElementsByTagName("messageID").item(0).getFirstChild()
                                .getNodeValue();
                    } catch (Exception e) { /* do nothing */}
                    try {
                        parentIDThisNode = targetNode.getElementsByTagName("parentID").item(0).getFirstChild()
                                .getNodeValue();
                    } catch (Exception e) { /* do nothing */}
                    try {
                        fileNameThisNode = targetNode.getElementsByTagName("file-name").item(0).getFirstChild()
                                .getNodeValue();
                    } catch (Exception e) { /* do nothing */}

                    String fileNameThisNodeWOExt = fileNameThisNode.substring(0, fileNameThisNode.lastIndexOf("."));
                    for (int j = 0; j < filesList.size(); j++) {
                        String thisFileName = ((File) (filesList.get(j))).getName();
                        if (fileNameThisNodeWOExt.equals(thisFileName.substring(0, thisFileName.lastIndexOf(".")))) {
                            addMail(fromThisNode, dateThisNode, subjectThisNode, messageIDThisNode, parentIDThisNode,
                                    (File) (filesList.get(j)));
                            filesList.remove(j);
                            break;
                        }
                    }
                    processingCounter = i;
                }
            } catch (javax.xml.parsers.ParserConfigurationException pcEx) {
                //parse()
                pcEx.printStackTrace();
            } catch (org.xml.sax.SAXException saxEx) {
                //parse()
                saxEx.printStackTrace();
            } catch (java.io.IOException ioEx) {
                //parse()
                ioEx.printStackTrace();
            }
            System.out.println("# Update from contents.xml file : " + processingCounter + " items");
            System.out.println("# Checkpoint 2: " + (new Date().getTime()) + "ms");
        }
        int processingCounter = 0;
        for (int i = 0; i < filesList.size(); i++) {
            addMail((File) (filesList.get(i)));
            processingCounter = i;
        }
        System.out.println("# Detect new mail files : " + processingCounter + " items");
        System.out.println("# Checkpoint 3: " + (new Date().getTime()) + "ms");

        for (Iterator i = dateSortedSet.iterator(); i.hasNext();)
            setThreadSortedList((OneMail) (i.next()));
    }

    /** このクラスが管理しているフォルダへの参照 */
    private File thisFolder;

    /* フォルダ情報の内部表現 */
    /** 日付でソートされた集合 */
    private Set dateSortedSet = null;

    /** 件名でソートされた集合 */
    private Set subjectSortedSet = null;

    /** 送信者でソートされた集合 */
    private Set fromSortedSet = null;

    /** スレッド順に並べられたリスト */
    private List threadSortedList = null;

    /** 日付でソートされた集合dateSortedSetのDOM表現 */
    private Document dateSortedSetDOM = null;

    /** 件名でソートされた集合subjectSortedSetのDOM表現 */
    private Document subjectSortedSetDOM = null;

    /** 送信者でソートされた集合fromSortedSetのDOM表現 */
    private Document fromSortedSetDOM = null;

    /** スレッド順に並べられたリストthreadSortedListDOMのDOM表現 */
    private Document threadSortedListDOM = null;

    /** 1つのメール情報データを、スレッド順のリストの適切な場所に入れる。<p>
     このメソッドは、In-Reply-ToのIDをMessage-Idとして持つメールを探すので、親メッセージが
     後から登録する事はできない。おそらく、メールの日付順にこのメソッドを呼び出せば、普通は問題なく
     スレッドのリストを組み立てることができると考えられる。
     
     @param currentMail メール情報データ
     */
    private void setThreadSortedList(OneMail currentMail) {
        if (threadSortedList.size() == 0) {
            currentMail.level = 1;
            threadSortedList.add(currentMail);
            return;
        }
        boolean isSet = false;
        if (currentMail.parentID.equals("")) { //In-Reply-Toがないならレベルは1
            currentMail.level = 1;
            for (int j = 0; j < threadSortedList.size(); j++) {
                OneMail currentListItem = (OneMail) threadSortedList.get(j);
                if (currentListItem.level != 1) continue;
                if (currentListItem.date.getTime() > currentMail.date.getTime()) {
                    threadSortedList.add(j, currentMail);
                    isSet = true;
                    break;
                }
            }
            if (!isSet) //リストにないのなら、末尾にセット
                    threadSortedList.add(threadSortedList.size(), currentMail);
        } else { //In-Reply-Toがある場合
            for (int j = 0; j < threadSortedList.size(); j++) { //既存のオブジェクトをすべて調べる
                OneMail currentListItem = (OneMail) threadSortedList.get(j); //現在のオブジェクト
                if (currentListItem.messageID.equals(currentMail.parentID)) { //Message-IDが一致する場合
                    int currentLevel = currentListItem.level; //そのレベルを入れて
                    currentMail.level = currentLevel + 1; //追加するメッセージのレベルは上記プラス1
                    for (int k = j + 1; k < threadSortedList.size(); k++, j++) {
                        //見つかったメッセージの下位のメッセージを調べるが、１レベルだけ下位のものだけを調べる
                        OneMail deepMail = (OneMail) threadSortedList.get(k); //現在のメッセージ
                        if (deepMail.level <= currentLevel) { //下位のレベルをすべて調べ終わった
                            threadSortedList.add(k, currentMail); //まだセットされていないなら、場所はそこでいい
                            isSet = true;
                            break;
                        }
                        if (deepMail.level > currentMail.level) //さらに下位のレベルなら無視
                                continue;
                        if (deepMail.date.getTime() > currentMail.date.getTime()) {
                            //追加するのと同一レベルでかつ、日付がより新しいものである場合
                            threadSortedList.add(k, currentMail); //そこにセットしてループを終了
                            isSet = true;
                            break;
                        }
                    }
                }
            }
            if (!isSet) {
                currentMail.level = 1;
                threadSortedList.add(threadSortedList.size(), currentMail);
            }
        }
    }

    /** フォルダから、メールのファイルあるいはフォルダだけを抜き出すためのフィルタクラス */
    private class MailItemFilter implements FilenameFilter {

        public boolean accept(File dir, String name) {
            if (name.endsWith(".mail")) return true;
            if (name.endsWith(".rply")) return true;
            if (name.endsWith(".ygm")) return true;
            if (name.endsWith(".html")) return true;
            if (name.endsWith(".htm")) return true;
            if (name.endsWith(".mpart")) return true;
            return false;
        }
    }

    /** メール情報データの集合で、日付順に並ばせるためのコンパレータ定義。
     日付が同一なら、件名、ファイル名の順序に調べる。従って、「同一である」とは判断されない。
     */
    private class OneMailDateComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            long t1 = ((OneMail) o1).date.getTime();
            long t2 = ((OneMail) o2).date.getTime();
            if (t1 < t2) return -1;
            if (t1 > t2) return 1;

            int compareResult = ((OneMail) o1).subject.compareToIgnoreCase(((OneMail) o2).subject);
            if (compareResult != 0) return compareResult;

            compareResult = ((OneMail) o1).mailFile.getName().compareToIgnoreCase(((OneMail) o2).mailFile.getName());
            return compareResult;
        }
    }

    /** メール情報データの集合で、件名順に並ばせるためのコンパレータ定義
     件名が同一なら、日付、ファイル名の順序に調べる。従って、「同一である」とは判断されない。
     */
    private class OneMailSubjectComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            int compareResult = ((OneMail) o1).subject.compareToIgnoreCase(((OneMail) o2).subject);
            if (compareResult != 0) return compareResult;

            long t1 = ((OneMail) o1).date.getTime();
            long t2 = ((OneMail) o2).date.getTime();
            if (t1 < t2) return -1;
            if (t1 > t2) return 1;

            compareResult = ((OneMail) o1).mailFile.getName().compareToIgnoreCase(((OneMail) o2).mailFile.getName());
            return compareResult;
        }
    }

    /** メール情報データの集合で、送信者順に並ばせるためのコンパレータ定義
     送信者名が同一なら、日付、ファイル名の順序に調べる。従って、「同一である」とは判断されない。
     */
    private class OneMailFromComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            int compareResult = ((OneMail) o1).from.compareToIgnoreCase(((OneMail) o2).from);
            if (compareResult != 0) return compareResult;

            long t1 = ((OneMail) o1).date.getTime();
            long t2 = ((OneMail) o2).date.getTime();
            if (t1 < t2) return -1;
            if (t1 > t2) return 1;

            compareResult = ((OneMail) o1).mailFile.getName().compareToIgnoreCase(((OneMail) o2).mailFile.getName());
            return compareResult;
        }
    }

    /** メールファイルからメール情報データのオブジェクトを生成する。引数がフォルダなら、mpartフォルダと仮定して
     そのフォルダ内にある同名で拡張子が違うファイルを探し、それをメールファイルとして処理をする。
     @param mailFile メールファイルへの参照
     */
    private OneMail GetOneMailInfo(File mailFile) {
        File targetFile = mailFile;
        String[] eachLine = null;

        EndOfFileSearch: if (mailFile.isDirectory()) { //マルチパートメールの場合しかないと思われる
            String fileName = targetFile.getName().substring(0, targetFile.getName().length() - 6);
            targetFile = new File(mailFile, fileName + ".ygm");
            if (targetFile.exists()) break EndOfFileSearch;
            targetFile = new File(mailFile, fileName + ".mail");
            if (targetFile.exists()) break EndOfFileSearch;
            targetFile = new File(mailFile, fileName + ".rply");
            if (targetFile.exists()) break EndOfFileSearch;
            targetFile = new File(mailFile, fileName + ".html");
            if (targetFile.exists()) break EndOfFileSearch;
            targetFile = new File(mailFile, fileName + ".htm");
            if (targetFile.exists()) break EndOfFileSearch;

            System.out.println("#### This is not a mail: " + mailFile.getPath());
            return null;
        }
        try {
            RandomAccessFile file = new RandomAccessFile(targetFile, "r");
            FileChannel channel = file.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            byte[] byteBuffer = new byte[(int) channel.size()];
            buffer.get(byteBuffer);
            String fileContents = new String(byteBuffer);
            int headerStartPosition = fileContents.indexOf(headerStartSign);
            if (headerStartPosition < 0) { return null; }
            eachLine = fileContents.substring(headerStartPosition + headerStartSign.length()).split("[\\n\\r]");
            channel.close();
            file.close();
        } catch (java.io.FileNotFoundException fnfEx) {
            fnfEx.printStackTrace();
            return null;
        } catch (java.io.IOException ioEx) {
            ioEx.printStackTrace();
            return null;
        }

        OneMail newOneMailInfo = new OneMail();
        newOneMailInfo.mailFile = mailFile;
        int fieldSetCounter = 0;
        for (int i = 0; i < eachLine.length; i++) {
            String headerLine = eachLine[i];
            String topLine = eachLine[i].toLowerCase();
            for (int j = i + 1; (j < eachLine.length) && isContinuousHeaderLine(eachLine[j]); j++, i++)
                headerLine += eachLine[j];
            try {
                headerLine = MimeUtility.decodeText(headerLine);
            } catch (Exception e) {
                System.out.println("----Error in MIMEUtility.decodeText");
                System.out.println("----" + e.getMessage());
            }
            if (topLine.startsWith("from: ")) {
                newOneMailInfo.from = headerLine.substring(6);
                fieldSetCounter++;
            } else if (topLine.startsWith("message-id: ")) {
                newOneMailInfo.messageID = headerLine.substring(12).trim();
                fieldSetCounter++;
            } else if (topLine.startsWith("date: ")) {
                newOneMailInfo.date = new MailDateFormat().parse(headerLine, new ParsePosition(6));
                fieldSetCounter++;
            } else if (topLine.startsWith("subject: ")) {
                newOneMailInfo.subject = headerLine.substring(9);
                fieldSetCounter++;
            } else if (topLine.startsWith("in-reply-to: ")) {
                newOneMailInfo.parentID = headerLine.substring(13).trim();
                fieldSetCounter++;
            }
            if (fieldSetCounter >= 5) break;
        }
        return newOneMailInfo;
    }

    /** 文字列の先頭が空白かタブの場合はtrueを戻す。<p>正規表現でチェックしようとしたが、なぜかうまくいかないので、
     とりあえず関数を作って対処した。もちろん、複数行に分かれたヘッダの2行目以降かどうかを判断するための関数。
     @param s 調べる文字列
     @return 引数の文字列の先頭が空白かタブならtrue、そうでないならfalse
     */
    private boolean isContinuousHeaderLine(String s) {
        if (s.length() <= 0) return true;
        if (s.charAt(0) == ' ') return true;
        if (s.charAt(0) == '\t') return true;
        return false;
    }

    /** メールファイルを集合に追加する。
     @param mailFile メールファイルへの参照
     */
    public void addMail(File mailFile) {
        OneMail currentMail = GetOneMailInfo(mailFile);
        if (currentMail != null) {
            dateSortedSet.add(currentMail);
            subjectSortedSet.add(currentMail);
            fromSortedSet.add(currentMail);
        }
    }

    /** メールの情報が分かっている場合に、メール情報データのオブジェクトを生成し、それを集合に追加する。
     @param fromThisNode 送信者名
     @param dateThisNode　日付データ（epocからの秒数のlongを表現する文字列）
     @param subjectThisNode　件名
     @param messageIDThisNode　Message-Idの値
     @param parentIDThisNode 親メッセージ（In-Reply-Toの値）
     @param fileNameThisNode メールファイルへの参照
     */
    public void addMail(String fromThisNode, String dateThisNode, String subjectThisNode, String messageIDThisNode,
            String parentIDThisNode, File fileNameThisNode) {
        OneMail currentMail = new OneMail();
        currentMail.messageID = messageIDThisNode;
        currentMail.parentID = parentIDThisNode;
        currentMail.date = new Date(Long.parseLong(dateThisNode));
        currentMail.from = fromThisNode;
        currentMail.subject = subjectThisNode;
        currentMail.mailFile = fileNameThisNode;

        if (currentMail != null) {
            dateSortedSet.add(currentMail);
            subjectSortedSet.add(currentMail);
            fromSortedSet.add(currentMail);
        }
    }

    /** フォルダからファイルを削除した場合の、メール一覧のメンテナンスを行う
     */
    public void removeMail(File mailFile) {

    }

    /** メール情報をテキスト形式で得る
     */
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("\n======== Date Sorted List ========\n");
        for (Iterator i = dateSortedSet.iterator(); i.hasNext();) {
            OneMail targetMail = (OneMail) (i.next());
            str.append(targetMail.date.toString());
            str.append(" - ");
            str.append(targetMail.subject);
            str.append("\n");
        }
        str.append("\n======== Subject Sorted List ========\n");
        for (Iterator i = subjectSortedSet.iterator(); i.hasNext();) {
            OneMail targetMail = (OneMail) (i.next());
            str.append(targetMail.date.toString());
            str.append(" - ");
            str.append(targetMail.subject);
            str.append("\n");
        }
        str.append("\n======== From Sorted List ========\n");
        for (Iterator i = fromSortedSet.iterator(); i.hasNext();) {
            OneMail targetMail = (OneMail) (i.next());
            str.append(targetMail.from);
            str.append(" - ");
            str.append(targetMail.date.toString());
            str.append(" - ");
            str.append(targetMail.subject);
            str.append("\n");
        }
        str.append("\n======== Thread Sorted List ========\n");
        for (Iterator i = threadSortedList.iterator(); i.hasNext();) {
            OneMail targetMail = (OneMail) (i.next());
            str.append(targetMail.level);
            str.append(" - ");
            str.append(targetMail.from);
            str.append(" - ");
            str.append(targetMail.subject);
            str.append(" - ");
            str.append(targetMail.messageID);
            str.append(" -> ");
            str.append(targetMail.parentID);
            str.append("\n");
        }
        return str.toString();
    }

    /** 現在のメール一覧をファイルに保存する。保存するファイル名は固定。
     */
    public void toFile() {
        /*		makeDOMObject ( );
         toFileFromMailDOM( infoFileName, dateSortedSetDOM );
         toFileFromMailDOM( AppendToName( infoFileName, "-subject") , subjectSortedSetDOM );
         toFileFromMailDOM( AppendToName( infoFileName, "-from"), fromSortedSetDOM );
         toFileFromMailDOM( AppendToName( infoFileName, "-thread"), threadSortedListDOM );
         */dateSortedSetDOM = makeDOMObjectCommon(dateSortedSet);
        subjectSortedSetDOM = makeDOMObjectCommon(subjectSortedSet);
        fromSortedSetDOM = makeDOMObjectCommon(fromSortedSet);
        threadSortedListDOM = makeDOMObjectCommon(threadSortedList);
        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.transform(new DOMSource(dateSortedSetDOM), new StreamResult(new File(thisFolder, infoFileName)));
            tf.transform(new DOMSource(subjectSortedSetDOM), new StreamResult(new File(thisFolder, appendToName(
                    infoFileName, "-subject"))));
            tf.transform(new DOMSource(fromSortedSetDOM), new StreamResult(new File(thisFolder, appendToName(
                    infoFileName, "-from"))));
            tf.transform(new DOMSource(threadSortedListDOM), new StreamResult(new File(thisFolder, appendToName(
                    infoFileName, "-thread"))));
        } catch (javax.xml.transform.TransformerConfigurationException tcEx) {
            //newTransformer()
            tcEx.printStackTrace();
        } catch (javax.xml.transform.TransformerException tEx) { //transform() 
            tEx.printStackTrace();
        }
    }

    private String appendToName(String fileName, String appending) {
        int lastDotPosition = fileName.lastIndexOf(".");
        if (lastDotPosition < 0)
            return fileName + appending;
        else
            return fileName.substring(0, lastDotPosition) + appending + fileName.substring(lastDotPosition);
    }

    public void makeDOMObject() {
        dateSortedSetDOM = makeDOMObjectCommon(dateSortedSet);
        subjectSortedSetDOM = makeDOMObjectCommon(subjectSortedSet);
        fromSortedSetDOM = makeDOMObjectCommon(fromSortedSet);
        threadSortedListDOM = makeDOMObjectCommon(threadSortedList);
    }

    private Document makeDOMObjectCommon(Collection mailListSet) {
        Document dom = null;
        try {
            dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Node rootNode = dom.appendChild(dom.createElement("folder-info"));
            DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            int counter = 1;
            for (Iterator i = mailListSet.iterator(); i.hasNext();) {
                OneMail targetMail = (OneMail) (i.next());

                Element oneMailNode = dom.createElement("mail-info");
                oneMailNode.setAttribute("serial", new Integer(counter++).toString());
                rootNode.appendChild(oneMailNode);

                Element currentElement = dom.createElement("from");
                currentElement.appendChild(dom.createTextNode(targetMail.from));
                oneMailNode.appendChild(currentElement);

                currentElement = dom.createElement("subject");
                currentElement.appendChild(dom.createTextNode(targetMail.subject));
                oneMailNode.appendChild(currentElement);

                currentElement = dom.createElement("messageID");
                currentElement.appendChild(dom.createTextNode(targetMail.messageID));
                oneMailNode.appendChild(currentElement);

                currentElement = dom.createElement("parentID");
                currentElement.appendChild(dom.createTextNode(targetMail.parentID));
                oneMailNode.appendChild(currentElement);

                currentElement = dom.createElement("file-name");
                currentElement.appendChild(dom.createTextNode(targetMail.mailFile.getName()));
                oneMailNode.appendChild(currentElement);

                currentElement = dom.createElement("level");
                currentElement.appendChild(dom.createTextNode(Integer.toString(targetMail.level)));
                oneMailNode.appendChild(currentElement);

                currentElement = dom.createElement("date");
                currentElement.appendChild(dom.createTextNode(Long.toString(targetMail.date.getTime())));
                oneMailNode.appendChild(currentElement);

                currentElement = dom.createElement("date-string");
                currentElement.appendChild(dom.createTextNode(dateFormatter.format(targetMail.date)));
                oneMailNode.appendChild(currentElement);
            }
        } catch (ParserConfigurationException pcEx) {
            //newDocument()
            pcEx.printStackTrace();
        }
        return dom;
    }

    /*	
     private void toFileFromMailDOM( String fileName, Document dom )  {
     try {
     Transformer tf = TransformerFactory.newInstance().newTransformer();
     tf.transform(new DOMSource( dom ), new StreamResult( new File( thisFolder, fileName ) ) );
     }
     catch ( javax.xml.transform.TransformerConfigurationException tcEx ) {
     //newTransformer()
     }
     catch ( javax.xml.transform.TransformerException tEx )  {  //transform() 
     }
     
     }
     */
    /** 現在のメール一覧をファイルに保存する。保存するファイル名は固定。
     */
    public void toHTMLFile() {

        File filelistStylesheets[] = { new File(OMEPreferences.getInstance().getOMEPref(), "filelist.xslt"),
                new File(OMEPreferences.getInstance().getOMEToolsFolder(), "filelist.xslt")};

        File threadlistStylesheets[] = { new File(OMEPreferences.getInstance().getOMEPref(), "threadlist.xslt"),
                new File(OMEPreferences.getInstance().getOMEToolsFolder(), "threadlist.xslt")};

        StreamSource stylesheet = new StreamSource(MacFile.getInstance(filelistStylesheets));
        StreamSource stylesheetThread = new StreamSource(MacFile.getInstance(threadlistStylesheets));
        try {
            Templates styledTemplate = TransformerFactory.newInstance().newTemplates(stylesheet);
            Transformer transformer = styledTemplate.newTransformer();

            transformer.transform(new DOMSource(dateSortedSetDOM), new StreamResult(new File(thisFolder,
                    "datesorted.html")));
            transformer.transform(new DOMSource(subjectSortedSetDOM), new StreamResult(new File(thisFolder,
                    "subjectsorted.html")));
            transformer.transform(new DOMSource(fromSortedSetDOM), new StreamResult(new File(thisFolder,
                    "fromsorted.html")));

            styledTemplate = TransformerFactory.newInstance().newTemplates(stylesheetThread);
            transformer = styledTemplate.newTransformer();

            transformer.transform(new DOMSource(threadSortedListDOM), new StreamResult(new File(thisFolder,
                    "threadsorted.html")));
        } catch (javax.xml.transform.TransformerConfigurationException tcEx) {
            tcEx.printStackTrace();
        } catch (javax.xml.transform.TransformerException tEx) {
            tEx.printStackTrace();
        }
    }

    /** メール1つ1つを管理するクラス
     */
    private class OneMail {

        public String messageID = "";

        public String parentID = "";

        public Date date;

        public String from = "";

        public String subject = "";

        public File mailFile;

        public int level;
    }
}
