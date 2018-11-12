package OME.mailformatinfo;

/**
<p>返信メールを作成する場合、引用部分の最初に記述する文字列（ここでは「コメント」）を定義する。</p>
<p>コメントを言語ごとに用意できるようにするための仕組みを用意している。このクラスは、言語に対応した
クラスが用意されていないときのための文字列の生成で、Eudora形式に準じたコメントを生成している。もし、
言語に対応したクラスを用意する場合は、このクラスと同じパッケージのCommentatorIntarfaceクラスを
インプリメントし、かつクラス名が「Commentator_ロケール名」とする。実際にコメントを生成するのは、
OME_PreferencesクラスのgetCommentLineメソッドとなる。こちらでは、OME_Preferencesに
ReplyComment_ロケール名.txtというファイルがあるかを探して、ない場合には、このパッケージで定義した
CommentatorInterfaceを継承したクラスを探す。つまり、テキストファイルの定義が優先される。
 * <hr>
 * <h2>OME履歴情報</h2>
 * <pre>
 * 2002/2/13:新居:ファイルを作成
 * 2004/3/22:新居:やっと履歴を書いた。
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * </pre>
*/
public class Commentator implements CommentatorInterface	{
    public String getComment(	//戻り値はコメント文字列
			String subject,	//引数には元メールの情報が渡される
			String from,
			String date,
			String messageId,
			String to)	{
        String fromName = from;
        if((from.indexOf("<")>0) && (from.indexOf(">")>0))	{
            fromName = from.substring(0, from.indexOf("<")).trim();
        }
        return	"At " + date + ", " + fromName + " wrote:";
    }
}
