package OME.mailformatinfo;
//
//  Commentator_ja_JP.java
//  OME_JavaProject
//
// * 2009/6/28:新居:OME_JavaCore2へ移動

//  Created by 新居 雅行 on Wed Feb 13 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//
import java.text.*;
import java.util.*;
import javax.mail.internet.MailDateFormat;

public class Commentator_ja_JP implements CommentatorInterface{
    public String getComment(	//戻り値はコメント文字列
			String subject,	//引数には元メールの情報が渡される
			String from,
			String date,
			String messageId,
			String to)	{

        String nextLine = System.getProperty("line.separator");
        Date msgDate = new MailDateFormat().parse( date, new ParsePosition ( 0 ));
        return	from + " さんが、" 
            + ( new SimpleDateFormat("yyyy/M/d H:mm:ss").format(msgDate) )
            + "に送られた" + nextLine + "　　---“" + subject + "”によりますと";
    }
}
