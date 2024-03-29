package OME.messagemaker;

import java.io.*;
import java.util.*;

import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

import OME.*;

/**
 メールのパートを展開してファイルに保存する機能を呼び出すクラス。<p>
 
 受信したソースから、ファイルやデータベース保存など、メッセージをストレージする機能をこのクラスで組み込む。
 実際の処理は、PartProcessorインタフェースをインプリメントしたクラスが行う。<p>
 
 PartProcessorインタフェースをインプリメントしたクラス（たとえば、DBSaverを定義しておく。
 そして、初期設定等で、あるメールソースを処理するのがDBSaverだと決定したとすると、このMassageMakerクラスの内部で
 DBSaverのインスタンスを作って、そのクラスのprocessMessageメソッドを呼び出す。<p>
 ここで、メールソースを処理するクラスは複数を選択できるものとし、たとえば、ファイル保存とバックアップ
 といった感じでクラスをそれぞれ作っておく。<p>
 
 実際の処理を行うには、以下のようなプログラムになるものとする。<p>
 
 Part originalMail; //処理するメール<br>
 MessageMaker mMaker = MessageMaker.prepareMessageMaker();<br>
 mMaker.process(originalMail);<br><p>
 
 processingの引数は、ファイル、ストリーム、パートのいずれでもよいが、いずれか1つを呼び出すだけでいい。<p>
 
 MessageMakerクラスでは、プロパティ（キー=文字列、値=オブジェクト）の保存をサポートする。
 実際のプロパティ設定や取得は、PartProcessor側にメソッドを定義しているが、PartProcessor間での
 データのやりとりを行うために、プロパティをサポートしている。<p>
 
 MessageMaker側でサポートしているプロパティは以下の通り。これらのプロパティは、自動的に設定されるので、
 PartProcessorの派生クラス内では、getPropertyメソッドを用いて値を参照する事ができる。
 <table border=2>
 <tr><td>キー</td><td>値</td><td>説明</td></tr>
 <tr><td>"EntryOfProcess"</td><td>"File", "InputStream", "Part"</td>
 <td>prosessingメソッドを呼び出したときの引数のタイプ</td></tr>
 <tr><td>"OriginalSourceFile"</td><td>Fileオブジェクト</td>
 <td>"EntryOfProcess"が"File"のとき、引数に指定をしたファイルへの参照</td></tr>
 <tr><td>StoringFolder</td><td>Fileオブジェクト</td>
 <td>Moving_Info.txtに基づいて判断された保存するべきフォルダ</td></tr>
 <tr><td>FiringProcess</td><td>String</td>
 <td>Moving_Info.txtに基づいて判断された起動プロセス</td></tr>
 <tr><td>MovingOptions</td><td>String</td>
 <td>Moving_Info.txtに基づいて判断されたオプション欄の情報。
 この情報を適用してから、各PartProcessorの処理に入る。また、ここでPartProcessorの組み合わせを変更可能</td></tr>
 </table>

 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 *
 *    :
 * 2004/1/4:新居:メールのバックアップファイルから復元する機能を追加。
 * 2004/1/14:新居:MessageMakerクラスをベースにしたものに大改造。
 * 2004/1/15:新居:エラー処理の見直し。エラーがあったときにソース（かねうちさん作成）を残すプログラムをMailProcessorから移動
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>

 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * $Revision: 1.16 $
 */

public class MessageMaker {

    private static String loggerNameSpace = "net.msyk.ome.messagemaker";

    /** このクラスで保持するプロパティ */
    private HashMap<String,Object> ownProperties;

    /** デフォルトのコンストラクタを無効にする */
    private MessageMaker() {}

    /**	パートを処理するクラスを用意する
     @return 生成されたMessageMakerのインスタンス
     */
    public static MessageMaker prepareMessageMaker() {
        MessageMaker mySelf = new MessageMaker();

        mySelf.setOwnProperties(new HashMap<String,Object>());
        return mySelf;
    }

    /**	コマンドラインで利用するときに起動時に実行されるメソッド。デバッグ用
     @param args コマンドライン引数に、オリジナルメールソースのファイルのパスを指定。1つだけ
     */
    public static void main(String args[]) {
        MessageMaker mm = MessageMaker.prepareMessageMaker();
		mm.process( new File( args[0] ) );
        System.exit(0);
    }

    /** プロパティを管理しているHashMapオブジェクトを得る
     @return HashMapオブジェクト
     */
    public HashMap<String,Object> getPropertyHashMap() {
        return getOwnProperties();
    }

    /** プロパティを取得する
     @param key プロパティのキー
     @return プロパティの値
     */
    public Object getProperty(String key) {
        return getOwnProperties().get(key);
    }

    /** プロパティを設定する
     @param key プロパティのキー
     @param object   プロパティの値
     */
    public void setProperty(String key, Object object) {
        getOwnProperties().put(key, object);
    }

    /**	実際にメールの処理を行う。ダウンロードしたメールソースのファイルを処理対象にするが、
     エラーがあった場合には、そのメールソースファイルと同じ内容のファイルを、保存先のフォルダに作成する。
     @param mailSource 処理するメール
     */
    public void process(File mailFile) {
        getOwnProperties().put("EntryOfProcess", "File");
        getOwnProperties().put("OriginalSourceFile", mailFile);

        try {
            FileInputStream fileInput = new FileInputStream(mailFile);
            process(fileInput);
            fileInput.close();
        } catch (Exception e) {
            Logging.writeMessage(e.getMessage());
            e.printStackTrace();
            createBackup(mailFile);
        }
    }

    /**	実際にメールの処理を行う
     @param mailSource 処理するメール
     */
    public void process(InputStream mailStream) throws Exception {
        if (!getOwnProperties().containsKey("EntryOfProcess")) getOwnProperties().put("EntryOfProcess", "InputStream");

        try {
            process(new MimeBodyPart(mailStream));
        } catch (Exception e) {
            throw e;
        }
    }

    /**	実際にメールの処理を行う
     @param mailSource 処理するメール
     */
    public void process(Part mailSource) throws Exception {
    	Logging.setupLogger(loggerNameSpace);
    	
    	instanciateClasses = new Vector<Object>();

        if (!getOwnProperties().containsKey("EntryOfProcess")) {
        	getOwnProperties().put("EntryOfProcess", "Part");
        }

        //メールの保存ディレクトリをMoving_Info.txtファイルから求める
        MovingInfo movingInfo = MovingInfo.getInstance();
        //Logging.writeMessage("#####" + movingInfo.toString());
        movingInfo.inspectMessage(mailSource); //メールの振り分け先などを求める
        getOwnProperties().put("StoringFolder", movingInfo.getStoringFolder());
        Logging.writeMessage("#####" + movingInfo.getStoringFolder());
        getOwnProperties().put("FiringProcess", movingInfo.getFiringProcess());
        String movingOptions = movingInfo.getMovingOptions();
        getOwnProperties().put("MovingOptions", movingOptions);

        //Moving_Info.txtで今回の条件にあったメールの処理に対するオプション設定があれば、
        //それを、OMEPreferencesに適用するが、現在の設定をスタックに保存しておく
        boolean isChangePrefs = (movingOptions != null && movingOptions.length() > 0);
        if (isChangePrefs) {
            OMEPreferences.push();
            OMEPreferences.getInstance().parsePreferences(movingOptions);
        }

        List<String> classes = OMEPreferences.getInstance().getMessageMakerClasses();
        //パート処理クラスのコレクションを得る
        ListIterator<String> listIt = classes.listIterator(); //順番に処理
        while (listIt.hasNext()) {
            String className = "OME.messagemaker." + listIt.next();
            try {
                instanciateClasses.add(Class.forName(className).newInstance());
                //指定したクラスのインスタンスを生成してリストに入れておく
            } catch (Exception cnfe) {
                Logging.writeErrorMessage(171, cnfe, "Class doesnt exists in MessageMakerClaasses parameter.");
                //生成に失敗する場合はいずれにしてもそのクラスは使えない。正しい指定のクラスだけで
                //処理を続行させるため、例外はメッセージを出すだけで無視でいいだろう。
            }
        }

        //インスタンス化したパート処理クラスのそれぞれについて、順番にpreProcessメソッドを実行する
        ListIterator<Object> instClass = instanciateClasses.listIterator();
        while (instClass.hasNext()) {
            PartProcessor pp = (PartProcessor) (instClass.next());
            pp.setMailSource(mailSource); //処理するメールのデータへの参照を各インスタンスに設定
            pp.setMessageMaker(this); //パート処理クラスからMessageMakerを参照できるようにする
            try {
                pp.preProcess();
            } catch (MessageException e) {
                //この例外では現在のクラスでの処理は終了するが、次のクラスの処理を行う。
                Logging.writeErrorMessage(172, e, "Exception in preProcess method. Continue to process this message.");
            } catch (Exception e) {
                //そのほかの例外の場合は、そのメールに対する処理一切キャンセルする。
                Logging.writeErrorMessage(173, e, "Exception in preProcess method. Stop to process this message "
                        + "and will proceed to the next message.");
                throw e;
            }
        }

        //インスタンス化したパート処理クラスのそれぞれについて、processMessageメソッドを並列的に実行する
        Vector<Thread> threadArray = new Vector<Thread>();
        instClass = instanciateClasses.listIterator();
        while (instClass.hasNext()) {
            PartProcessor pp = (PartProcessor) instClass.next();
            Thread curThread = new Thread(pp, pp.getClass().getName());
            threadArray.add(curThread);
            curThread.start();
        }
        do {
        	ListIterator<Thread> instThread = threadArray.listIterator();
            while (instThread.hasNext()) {
                try {
                    Thread targetThread = (Thread) instThread.next();
                    targetThread.join(100);
                } catch (InterruptedException ignored) {
                    //この例外は無視する
                } catch (Exception e) {
                    //そのほかの例外の場合は、そのメールに対する処理一切キャンセルする。
                    e.printStackTrace();
                    throw e;
                }
            }
        } while (!isAllThreadDead(threadArray));

        //MessageExceptionのキャッチは、PartProcessorのstartメソッド側で行っている
        if (catchedException != null) { //スレッド処理中に例外が発生したら
        throw catchedException; }

        //インスタンス化したパート処理クラスのそれぞれについて、順番にafterProcessメソッドを実行する
        instClass = instanciateClasses.listIterator();
        while (instClass.hasNext())
            try {
                ((PartProcessor) instClass.next()).afterProcess();
            } catch (MessageException e) {
                //この例外では現在のクラスでの処理は終了するが、次のクラスの処理を行う。
                Logging.writeErrorMessage(176, e,
                                "Exception in afterProcess method. Continue to process this message.");
            } catch (Exception e) {
                //そのほかの例外の場合は、そのメールに対する処理一切キャンセルする。
                Logging.writeErrorMessage(177, e, "Exception in afterProcess method. Stop to process this message "
                        + "and will proceed to the next message.");
                throw e;
            }
        SerialCodeGenerator.getInstance().updateSerialFile();
        if (isChangePrefs) OMEPreferences.pop();
    }

    /** 引数のスレッドのリストについて、すべての実行が終わっているかどうかをチェックする
     @param threadArray チェックするスレッドのリスト
     @return 全部処理が終わっている場合のみtrue
     */
    private boolean isAllThreadDead(Vector<Thread> threadArray) {
        ListIterator<Thread> listIt = threadArray.listIterator();
        while (listIt.hasNext()) {
            Thread targetThread = (Thread) listIt.next();
            if (targetThread.isAlive()) return false;
        }
        return true;
    }

    private Exception catchedException = null;

    public void setCatchedException(Exception e) {
        catchedException = e;
    }

    private static final String BACKUPFILE_BASE = "error-mail-original";

    private static final String BACKUPFILE_SUFFIX = ".txt";

    /**
     * 処理中に問題が起こったメールをエラーとしてバックアップする。
     *
     * @param mailSource 元ファイル。
     * @return バックアップファイル作成に成功したらtrue。
     */
    private boolean createBackup(File mailSource) {
        try {
			File backupFolder = (File) (getOwnProperties().get("StoringFolder"));
			if (backupFolder == null) {
				backupFolder = new File(OMEPreferences.getInstance().getOMETemp(), MovingInfo.DEFAULT_MAIL_FOLDER);
			}
			File backupFile = null;
			SerialCodeGenerator serialCodeGenerator = SerialCodeGenerator.getInstance();

			// determine unique backup file path
			do {
				String serial = serialCodeGenerator.getSerialCode();
				backupFile = new File(backupFolder, BACKUPFILE_BASE + serial + BACKUPFILE_SUFFIX);
			} while (backupFile.exists());

            Logging.writeMessage("Creating backup as " + backupFile);
            copyFile(mailSource, backupFile);
            return true;
        } catch (Exception ex) {
            Logging.writeMessage("Error during creating backup : " + ex.getMessage());
            return false;
        }
    }

    // general utility methods.

    /**
     * ファイルをコピーする。
     *
     * @param src コピー元ファイル。
     * @param dest コピー先ファイル。
     * @exception IOException コピー中にエラーが起こった場合。
     */
    private static void copyFile(File src, File dest) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);
            copyStream(in, out);
            out.close();
            in.close();
        } finally {
            // in, out ともに close() を最低 1 回は実行する。
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ignore) {}
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ignore) {}
        }
    }

    /**
     * copyStreamメソッドで使うバッファサイズ。
     */
    private static final int BUFFER_SIZE = 2048;

	private List<Object> instanciateClasses;

    /**
     * 入力ストリームを出力ストリームにコピーする。
     *
     * @param in コピー元のストリーム。
     * @param out コピー先のストリーム。
     * @exception IOException 読み込み、書き込みのどちらかでエラーが発生した時。
     */
    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        for (int len = in.read(buf); len != -1; len = in.read(buf)) {
            out.write(buf, 0, len);
        }
    }

	public HashMap<String,Object> getOwnProperties() {
		return ownProperties;
	}

	public void setOwnProperties(HashMap<String,Object> ownProperties) {
		this.ownProperties = ownProperties;
	}
}
