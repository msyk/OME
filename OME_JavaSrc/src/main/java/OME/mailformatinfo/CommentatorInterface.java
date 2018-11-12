package OME.mailformatinfo;
/**
<p>返信メールを作成する場合、引用部分の最初に記述する文字列（ここでは「コメント」）を生成する
クラスのインタフェースを定義する。</p>
<p>コメントを言語ごとに用意できるようにするための仕組みを用意している。このクラスは、言語に対応した
クラスが用意されていないときのための文字列の生成で、Eudora形式に準じたコメントを生成している。もし、
言語に対応したクラスを用意する場合は、このクラスと同じパッケージのCommentatorIntarfaceクラスを
インプリメントし、かつクラス名が「Commentator_ロケール名」とする。実際にコメントを生成するのは、
OME_Preferencesクラスのメソッドとなる。
 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 * 2002/2/13:新居:ファイルを作成
 * 2004/3/22:新居:やっと履歴を書いた。
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
*/

public interface CommentatorInterface {

	/**
	コメントの文字列を作成して戻す。
	@param subject メールの件名
	@param from メールの送信者名
	@param date メールの送信日時
	@param messageID メールのメッセージID
	@param to メールの宛先
	@return 引数で得られた情報から、コメントの文字列を作成して戻す
	*/
	public String getComment(	//戻り値はコメント文字列
			String subject,	//引数には元メールの情報が渡される
			String from,
			String date,
			String messageId,
			String to);
}
