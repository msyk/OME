package OME;

import java.io.*;

/**
 * コマンド実行を行うクラス
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii（新居雅行） msyk@msyk.net
 * @version 10.1
 */

public class CommandExecuter {

	/** 指定したコマンドを実行するためのオブジェクトを生成する。実際の実行はdoCommandメソッドが呼び出されたとき。
	 *	@param cmd	コマンドの文字列をString配列で指定する。各パラメータを配列の要素としていれておく。シェル展開などはしないので、ベタで指定が無難
	 */

	public CommandExecuter(String cmd[]) {
		com2 = cmd;
	}

	/** 指定したコマンドを実行するためのオブジェクトを生成する。実際の実行はdoCommandメソッドが呼び出されたとき。
	 *	@param cmd	コマンドの文字列をObject配列で指定する。各要素をtoStringメソッドで文字列に変換し、コマンドのパラメータとして与える。
	 */

	public CommandExecuter(Object cmd[]) {

		com2 = new String[cmd.length];
		for (int i = 0; i < cmd.length; i++)
			com2[i] = cmd[i].toString();

	}

	/** 指定したコマンドを実行するためのオブジェクトを生成する。実際の実行はdoCommandメソッドが呼び出されたとき。
	 *	@param cmd	コマンドの文字列をString配列で指定する。コマンドではスペースで区切る各パラメータを配列に入れる。
		シェル展開などはしないので、ベタで指定が無難
	 */

	public CommandExecuter(String cmd) {
		com1 = cmd;
	}

	private String com1 = null;

	private String com2[] = null;

	public int returnValue = 0;

	/** コマンドを同期で実行する
	 *	@return	コマンドから出力された標準出力の結果
	 */
	public String doCommand() {
		ByteArrayOutputStream outSt = new ByteArrayOutputStream();
		returnValue = doCommand(false, outSt);
		String strOut = "";
		try {
			strOut = new String(outSt.toByteArray(), "UTF-8");
		} catch (Exception e) {
			Logging.writeMessage(e.getMessage());
		}
		try {
			outSt.close();
		} catch (Exception e) {
			Logging.writeMessage(e.getMessage());
		}
		return strOut;
	}

	/** コマンドを実行する
	 *	@param	async	非同期で実行するならtrue
	 *	@param	out	コマンドによって実行されたプロセスからの標準出力をこのストリームに書き出す（同期の場合のみサポート）
	 *	@return	コマンドの戻り値（非同期の場合は常に0、プロセスが起動していない場合には-1）
	 */
	public int doCommand(boolean async, OutputStream out) {
		int returnValue = 0;
		Process commandProcess = null;
		try {
			if (com1 != null){
				//Logging.writeMessage("Command: " + com1.toString());
				commandProcess = Runtime.getRuntime().exec(com1);
			}
			else if (com2 != null) {
//				StringBuffer sb = new StringBuffer("");
//				for( int i=0 ; i<com2.length; i++){
//					if (i > 0){
//						sb.append(", ");
//					}
//					sb.append(com2[i]);
//				}
//				Logging.writeMessage("Command: " + sb.toString());
				commandProcess = Runtime.getRuntime().exec(com2);
			}
			if (async) return returnValue;

			InputStream inSt = commandProcess.getInputStream();
			byte buffer[] = new byte[10000];
			WatchTimer timer = new WatchTimer(Thread.currentThread());
			timer.start();
			boolean processLive = true;
			while (processLive) {
				try {
					commandProcess.waitFor();
					processLive = false;
				} catch (Exception e) {
					int cnt = inSt.read(buffer, 0, 1024);
					if (cnt > 0) out.write(buffer, 0, cnt);
				}
			}
			int cnt = inSt.read(buffer, 0, 1024);
			if (cnt > 0) out.write(buffer, 0, cnt);
			timer.noMoreTimer();
		} catch (Exception e) {
			Logging.followingMessageIsImportant();
			Logging.writeMessage("%% OME Error 122 %% Error in Command executing: " + e.getMessage());
			if (com1 != null)
				Logging.writeMessage("   Command: " + com1);
			else if (com2 != null) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < com2.length; i++) {
					sb.append(com2[i]);
					sb.append(" ");
				}
				Logging.writeMessage("   Command: " + sb.toString());
			}
		}
		if (commandProcess == null) return -1;
		try {
			commandProcess.getOutputStream().close();
			commandProcess.getInputStream().close();
			commandProcess.getErrorStream().close();
		} catch (Exception e) {
			Logging.writeMessage("%% OME Error 123 %% Error when close stream: " + e.getMessage());
		}
		return commandProcess.exitValue();
	}

	/** コマンドを実行する。同期の場合、コマンドによって実行されたプロセスからの標準出力をOME.Loggingメソッドに渡す
	 *	@param	async	非同期で実行するならtrue
	 *	@return	コマンドの戻り値（非同期の場合は常に0、プロセスが起動していない場合には-1）
	 */
	public int doCommandWithLogging(boolean async) {
		int returnValue = 0;
		Process commandProcess = null;
		try {
			if (com1 != null)
				commandProcess = Runtime.getRuntime().exec(com1);
			else if (com2 != null) commandProcess = Runtime.getRuntime().exec(com2);
			if (async) return returnValue;

			InputStream inSt = commandProcess.getInputStream();
			byte buffer[] = new byte[10000];
			WatchTimer t = new WatchTimer(Thread.currentThread());
			t.start();
			boolean processLive = true;
			while (processLive) {
				try {
					commandProcess.waitFor();
					processLive = false;
				} catch (Exception e) {
					int cnt = inSt.read(buffer, 0, 1024);
					if (cnt > 0) OME.Logging.writeMessage(new String(buffer, 0, cnt));
				}
			}
			int cnt = inSt.read(buffer, 0, 1024);
			if (cnt > 0) OME.Logging.writeMessage(new String(buffer, 0, cnt));
			t.noMoreTimer();
		} catch (Exception e) {
			Logging.followingMessageIsImportant();
			Logging.writeMessage("%% OME Error 122 %% Error in Command executing: " + e.getMessage());
			if (com1 != null)
				Logging.writeMessage("   Command: " + com1);
			else if (com2 != null) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < com2.length; i++) {
					sb.append(com2[i]);
					sb.append(" ");
				}
				Logging.writeMessage("   Command: " + sb.toString());
			}
		}
		if (commandProcess == null) return -1;
		try {
			commandProcess.getOutputStream().close();
			commandProcess.getInputStream().close();
			commandProcess.getErrorStream().close();
		} catch (Exception e) {
			Logging.writeMessage("%% OME Error 123 %% Error when close stream: " + e.getMessage());
		}
		return commandProcess.exitValue();
	}

	private class WatchTimer extends Thread {

		boolean isNoMore = false;

		Thread receiverThread = null;

		public WatchTimer(Thread receiver) {
			receiverThread = receiver;
		}

		public void run() {
			while (isNoMore == false) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					Logging.writeMessage(e.getMessage());
				}
				receiverThread.interrupt();
			}
		}

		public void noMoreTimer() {
			isNoMore = true;
		}
	}

}
