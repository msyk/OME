package OME.textformatter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import OME.*;

/**
メールの本文に記述したHTMLタグに合わせて、その情報をもとにテキストに展開し、軽くレイアウトした感じの
テキストメッセージを作成する。そのための基本機能を提供するクラス。

HTML展開をする場合、本文部分の文字列に対して、変数preSourceのソースを前に、変数postSourceの
ソースを後に付加し、XSLTを適用して変換をかける。
<h2>標準テンプレート</h2>
XSLTによるテンプレートを利用してHTML展開を行います。そのテンプレートのうち、以下の基準を満たすものを
「標準テンプレート」として、OME内では既定値として使われるようにします。
<ol>
<li>~/Library/Preferences/OME_Preferences内にあるHTMLExpanding.xslt
</li>/OME_Applications/tools内にあるHTMLExpanding.xslt
</ol>

<hr>
<h2>OME履歴情報</h2>
<pre>
2005/3/30:新居:着手しました
</pre>
 
@author Masayuki Nii（新居雅行） msyk@msyk.net
*/

public class HTMLExpander {

/**
	HTML展開をコマンドラインで呼び出すためのインタフェース。たとえば送信メールをエディタで編集中に
	HTML展開した結果を確認するような場合に利用する。
	
	基本的な使い方は、以下の通りで、メールの本文部分を標準入力で与え、HTML展開した結果は標準出力で得られる。
	encodingパラメータで指定したエンコードで標準入力を解釈する
		
	java -cp ___/OME_lib.jar java OME.textformatter.HTMLExpander [encoding]
*/
    public static void main(String args[]) {
		String encoding = "UTF-8";
		if ( args.length >= 1 )	encoding = args[0];
		try{
			new HTMLExpander().expandHTML(System.in, encoding, System.out);
		} catch (Exception e)	{ e.printStackTrace(); }
        System.exit(0);
    }
	
/**	XSLTファイルのデフォルトのファイル名
*/
	private String XSLTFileName = "HTMLExpanding.xslt";

/**	メールの本文の前に付加するXMLソース
*/
	private String preSource = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><body>";

/**	メールの本文の後ろに付加するXMLソース
*/
	private String postSource = "</body>";
	
/**	XSLTの内容を取り出すストリーム
*/
	private InputStream XSLTStream = null;
	
/**	デフォルト以外のXSLTファイルを指定する場合に利用する
	@param inStream	XSLTが含まれるストリーム
*/
	public void setXSLT( InputStream inStream )	{
		XSLTStream = inStream;
	}
/**	デフォルト以外のXSLTファイルを指定する場合に利用する。ファイルのエンコードはUTF-8のみをサポート？
	@param　inFile	XSLTファイル
*/
	public void setXSLT( File inFile ) throws Exception	{
		try	{
			XSLTStream = new FileInputStream( inFile );
		} catch ( Exception e )	{
			throw e;
		}
	}
/**	デフォルト以外のXSLT以外を使う場合に使用する。
	@param inString	XSLTのソースの文字列
	@param encoding	XSLTをストリーム化するときのエンコード（ソースに指定したエンコード）
*/
	public void setXSLT( String inString, String encoding ) throws Exception	{
		try	{
			XSLTStream = new ByteArrayInputStream( inString.getBytes( encoding ) );
		} catch ( Exception e )	{
			throw e;
		}
	}

/**	XSLTのストリームを戻す
	@return XSLTのストリーム（デフォルトの場合はnull）
*/
	private InputStream getXSLTStream()	{
		return XSLTStream;
	}

/**	引数に指定した文字列をトランスレートし、引数に指定したファイルに書き出す。
	setXSLTメソッドで指定したXSLTが適用されるが、指定がなければデフォルトのXSLTファイルが使われる
	@param inString	ソースとなるテキスト
	@param outFile	トランスレートした結果を書き出すファイル（存在すれば上書き）
*/
	public void expandHTML( String inString, File outFile ) throws Exception	{
		try	{
			expandHTML( inString, new FileOutputStream( outFile ) );
		} catch ( Exception e )	{
			throw e;
		}
	}

/**	引数に指定した文字列をトランスレートし、引数に指定した変数に設定する。
	setXSLTメソッドで指定したXSLTが適用されるが、指定がなければデフォルトのXSLTファイルが使われる
	@param inString	ソースとなるテキスト
	@return	トランスレートした結果
*/
	public String expandHTML( String inString ) throws Exception	{
		try	{
			ByteArrayOutputStream baOutSt = new ByteArrayOutputStream();
			expandHTML( inString, baOutSt );
			String outString = baOutSt.toString( "UTF-8" );
//			System.out.println( "####### Converted source\n"+outString+"\n\n");
			return outString;
		} catch ( Exception e )	{
			throw e;
		}
	}

/**	引数に指定した文字列をトランスレートし、引数に指定したファイルに書き出す。
	setXSLTメソッドで指定したXSLTが適用されるが、指定がなければデフォルトのXSLTファイルが使われる
	@param inString	ソースとなるテキスト
	@param outFile	トランスレートした結果を書き出すストリーム
*/
	public void expandHTML( String inString, OutputStream outStream ) throws Exception	{
		try	{
			if ( XSLTStream == null )	{
				OMEPreferences omePrefs = OMEPreferences.getInstance();
				File xsltFile = new File( omePrefs.getOMEPref(), XSLTFileName );
				if ( ! xsltFile.exists() )	{
					xsltFile = new File( omePrefs.getOMEToolsFolder(), XSLTFileName );
					if ( ! xsltFile.exists() )	{
						Logging.followingMessageIsImportant();
						Exception e = new Exception( "Missing XSLT file." );
						Logging.writeErrorMessage(671, e, 
							"Missing XSLT file.");			
						throw e;
					}
				}
				XSLTStream = new FileInputStream( xsltFile );
			
			}
			
//			System.out.println( "####### Processing source\n"+preSource + inString + postSource+"\n\n");
			
			try	{
			Transformer tr =TransformerFactory.newInstance()
							.newTemplates( new StreamSource( XSLTStream ) ).newTransformer();
			tr.transform( 
				new StreamSource( 
					new ByteArrayInputStream( (preSource + inString + postSource).getBytes( "UTF-8" ) ) ) ,
				new StreamResult( outStream ) );
			} catch ( Exception e )	{
				Logging.followingMessageIsImportant();
				Logging.writeErrorMessage(672, e, 
					"Error in the body part scripted by HTML or XSLT file.");			
				throw e;
			}
				
		} catch ( Exception e )	{
			throw e;
		}
	}
	
/**	引数に指定したストリームをトランスレートし、引数に指定したストリームに書き出す。
	setXSLTメソッドで指定したXSLTが適用されるが、指定がなければデフォルトのXSLTファイルが使われる
	@param inStream	ソースとなるストリーム
	@param encoding ここで指定したエンコードであると仮定して入力ストリームを解釈する
	@param outStream	トランスレートした結果を書き出すストリーム
*/
	public void expandHTML( InputStream inStream, String encoding, OutputStream outStream ) throws Exception	{
		try	{
			byte buffer[] = new byte[100000];
			int count = inStream.read(buffer);
			expandHTML ( new String( buffer, 0, count, encoding), outStream );
		} catch ( Exception e )	{
			throw e;
		}
	}

	
}
