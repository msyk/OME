package OME;

import OME.mailformatinfo.*;
import java.util.*;
//
//  OME_libTest.java
//  OME_lib
//
// * 2009/6/28:新居:OME_JavaCore2へ移動
//  Created by 新居 雅行 on Sun Jan 27 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//

public class OME_libTest {

    public static void main(String arg[])	{

TextConverter txConv=null;
String s = "[iMuON 56454] Re: =?ISO-2022-JP?B?GyRCSCE0WyUqJVUbKEI=?=\n\t=?ISO-2022-JP?B?KBskQkd6PyklRCUiITwbKEIp?=\n\t=?ISO-2022-JP?B?GyRCMj45cENOGyhC?=";
		try {
			txConv = (TextConverter) Class.forName(MailFormatInfo.getInstance().getMailConverter(Locale.getDefault())).newInstance();
		} catch (Exception e) {
			try {
				txConv = (TextConverter) Class.forName("OME.mailformatinfo.TextConverter_Default").newInstance();
			} catch (Exception e2) {				}
		}
System.out.println( txConv.headerTextUnifier(s));

/*		OME.messagemaker.GrowlNotify.sendDownloadedMessage( 1 );
		OME.messagemaker.GrowlNotify.sendUnreadMessage( 1 );
		OME.messagemaker.GrowlNotify.sendDownloadedMessage( 5 );
		OME.messagemaker.GrowlNotify.sendUnreadMessage( 5 );
		OME.messagemaker.GrowlNotify.sendSentMessage( );
*//*
		System.out.println( OMEPreferences.getInstance().getOMEApplicationsFolder());
		System.out.println( OMEPreferences.getInstance().getOMEFrameworkFolder());
*//*    
		MailFormatInfo mf = MailFormatInfo.getInstance();
		System.out.println(mf.getSendingBodyCode("ja_JP"));
		System.out.println(mf.getMailSourceCharset("ISO-2022-JP"));
		System.out.println(mf.getMailSourceCharset("iso-2022-jp"));

*/
//        MovingInfo.getInstance();
    
    /*
        StringBuffer b = new StringBuffer();
        String s = "さてさて日本の「パートナー」は、どの国だ。We must sing a song well.\n\n";
        LineDevider.convertAppend(b, s, 10 , "", false);
        LineDevider.convertAppend(b, s, 11 , "", false);
        LineDevider.convertAppend(b, s, 12 , "", false);
        LineDevider.convertAppend(b, s, 13 , "", false);
        LineDevider.convertAppend(b, s, 14 , "", false);
        LineDevider.convertAppend(b, s, 15 , "", false);
        LineDevider.convertAppend(b, s, 16 , "", false);
        LineDevider.convertAppend(b, s, 17 , "", false);
        LineDevider.convertAppend(b, s, 18 , "", false);
        LineDevider.convertAppend(b, s, 19 , "", false);
        LineDevider.convertAppend(b, s, 20 , "", false);
        LineDevider.convertAppend(b, s, 21 , "", false);
        LineDevider.convertAppend(b, s, 22 , "", false);
        LineDevider.convertAppend(b, s, 23 , "", false);
        LineDevider.convertAppend(b, s, 24 , "", false);
        LineDevider.convertAppend(b, s, 25 , "", false);
        LineDevider.convertAppend(b, s, 26 , "", false);
        LineDevider.convertAppend(b, s, 27 , "", false);
        LineDevider.convertAppend(b, s, 28 , "", false);
        LineDevider.convertAppend(b, s, 29 , "", false);
        try	{
        OutputStream outFile = new FileOutputStream(new File ("/Users/msyk/result.log"));
        outFile.write(b.toString().getBytes("Shift_JIS"));
        outFile.close();
        } catch ( Exception e ) 	{e.printStackTrace();}
    */
/*     Part originalMail=null;
    Vector messasingClasses = new Vector();
	messasingClasses.add("TestPartProcessor1");
	messasingClasses.add("TestPartProcessor2");
*/

//    MessageMaker mMaker = MessageMaker.prepareMessageMaker(messasingClasses);
// mMaker.process(originalMail);

    /*
        OMEPreferences omePref = OMEPreferences.getInstance();
    
        System.out.println(omePref);
        System.out.println(omePref.getMessageMakerClasses());

        OMEPreferences.push();
        omePref = OMEPreferences.getInstance();
        OMEPreferences.getInstance().parsePreferences("MessageMakerClasses={AAA,BBB,CCC}");
        System.out.println(omePref);
        System.out.println(omePref.getMessageMakerClasses());

        OMEPreferences.pop();
omePref = OMEPreferences.getInstance();
        System.out.println(omePref);
        System.out.println(omePref.getMessageMakerClasses());
        
    */
    
    
//        UnreadUtility.getInstance().removeHasRedAliases();
    
/*
        long a,b;

        File f = new File("/Users/msyk/Carbon-gcc3.p");
        long fDate = f.lastModified();
        File f2 = new File("/Users/msyk/a2");
        File f3 = new File("/Users/msyk/a3");
        try{
            System.out.println(f2.createNewFile());
            System.out.println(f2.setLastModified(fDate));
            System.out.println(f3.createNewFile());
            System.out.println(f3.setLastModified(fDate+120L*24L*60L*60L*1000L));
        }
        catch(Exception e){System.out.println(e.getMessage());}
        
        System.out.println(a=new InternetCalender("Thu, 01 Jan 2003 08:59:59 +0900").parseDateString());
        System.out.println(b=new InternetCalender("Thu, 01 Jan 2003 09:00:00 +0900").parseDateString());
        System.out.println(b-a);
*/
/*
        File f4 = new File("/Users/msyk/Kaoru Washitaki|Re:インスピレーションの原稿などは？|0ELH.mail");
        System.out.println(a=f4.lastModified());
        System.out.println(f4.exists());
        InternetCalender c = new InternetCalender("Thu, 09 Jan 2003 18:17:38 +0900");
        System.out.println(b=c.parseDateString());
        System.out.println(a-b);
        try{
        System.out.println(f3.createNewFile());
        System.out.println(f3.setLastModified(b));
        }
        catch(Exception e){System.out.println(e.getMessage());}
*/
/*
    File doc = new File("/Users/msyk/電子メール/temp/refByAliases/<3E1538C9.5020203@mtf.biglobe.ne.jp>.refref");
    File original[] = MacAlias.resolveMacAliases(doc);
    for(int i=0;i<original.length;i++)
        System.out.println("#####"+original[i].getPath());
*/
/* 
File doc = new File("/Users/msyk/電子メール/temp/testdata");
(new MailProcessor()).processDLEachFiles();
*/
/*   try	{
        MacFolder x = new MacFolder("/Users/msyk/test", true);
        System.out.println("#####"+x.getName());
        System.out.println("#####"+x.exists());
    }
    catch(Exception e)	{}
*/
//    OMEPreferences omePref = OMEPreferences.getInstance();
//    omePref.getOMERoot();  
//    File doc = new File("/Users/msyk/OS_Mail_Environment/InBox/\"Sun Microsyste|FORTE[tm] COMPILER COLLEC|2PPX.mpart");
//    VirusScan.check(doc);
//    File te = new File("/Applications/TextEdit.app");
//    File doc = new File("/Users/msyk/OS_Mail_Environment/書籍雑誌サイト関連/MacPower:MacPeople/江本　剛 <take-e@as|Developer Toolsの著者校です|242N.mpart/Developer Tools.txt.mime");
//    OpenFile.byStuffItExpander(doc);
    System.exit(0);
//       Logging.writeMessage( CharsetToLocale.getInstance().getSuitableLocale("US-ASCII"));
//        OMEPreferences omePref = OMEPreferences.getInstance();
//        Logging.writeMessage(omePref.getOMERoot().getPath());
//        MailFormatInfo mf = MailFormatInfo.getInstance();
//     Logging.writeMessage(mf.getSendingBodyCode());
//     Logging.writeMessage(mf.getMailSourceCharset("ISO-2022-JP"));

/*
        Object obj = InternationalUtils.getClassInstance(
            "OME.mailformatinfo.MailFormatInfo", new Locale("ab","CD","ef"));
        Logging.writeMessage(obj.getClass().getName());

            CommentatorInterface commObj 
                = (CommentatorInterface)InternationalUtils.getClassInstance(
                    "OME.mailformatinfo.Commentator", new Locale("ja","JP"));
            Logging.writeMessage(commObj.getComment("#", "#", "#", "#", "#"));
*/        

        /*
         StringBuffer b = new StringBuffer();
         String s = "さてさて日本の「パートナー」は、どの国だ。We must sing a song well.\n\n";
         LineDevider.convertAppend(b, s, 10 , "", false);
         LineDevider.convertAppend(b, s, 11 , "", false);
         LineDevider.convertAppend(b, s, 12 , "", false);
         LineDevider.convertAppend(b, s, 13 , "", false);
         LineDevider.convertAppend(b, s, 14 , "", false);
         LineDevider.convertAppend(b, s, 15 , "", false);
         LineDevider.convertAppend(b, s, 16 , "", false);
         LineDevider.convertAppend(b, s, 17 , "", false);
         LineDevider.convertAppend(b, s, 18 , "", false);
         LineDevider.convertAppend(b, s, 19 , "", false);
         LineDevider.convertAppend(b, s, 20 , "", false);
         LineDevider.convertAppend(b, s, 21 , "", false);
         LineDevider.convertAppend(b, s, 22 , "", false);
         LineDevider.convertAppend(b, s, 23 , "", false);
         LineDevider.convertAppend(b, s, 24 , "", false);
         LineDevider.convertAppend(b, s, 25 , "", false);
         LineDevider.convertAppend(b, s, 26 , "", false);
         LineDevider.convertAppend(b, s, 27 , "", false);
         LineDevider.convertAppend(b, s, 28 , "", false);
         LineDevider.convertAppend(b, s, 29 , "", false);
         try	{
         OutputStream outFile = new FileOutputStream(new File ("/Users/msyk/result.log"));
         outFile.write(b.toString().getBytes("Shift_JIS"));
         outFile.close();
         } catch ( Exception e ) 	{e.printStackTrace();}
         */
        /*     Part originalMail=null;
         Vector messasingClasses = new Vector();
         messasingClasses.add("TestPartProcessor1");
         messasingClasses.add("TestPartProcessor2");
         */

        //    MessageMaker mMaker = MessageMaker.prepareMessageMaker(messasingClasses);
        // mMaker.process(originalMail);
        /*
         OMEPreferences omePref = OMEPreferences.getInstance();
         
         System.out.println(omePref);
         System.out.println(omePref.getMessageMakerClasses());

         OMEPreferences.push();
         omePref = OMEPreferences.getInstance();
         OMEPreferences.getInstance().parsePreferences("MessageMakerClasses={AAA,BBB,CCC}");
         System.out.println(omePref);
         System.out.println(omePref.getMessageMakerClasses());

         OMEPreferences.pop();
         omePref = OMEPreferences.getInstance();
         System.out.println(omePref);
         System.out.println(omePref.getMessageMakerClasses());
         
         */

        //        UnreadUtility.getInstance().removeHasRedAliases();
        /*
         long a,b;

         File f = new File("/Users/msyk/Carbon-gcc3.p");
         long fDate = f.lastModified();
         File f2 = new File("/Users/msyk/a2");
         File f3 = new File("/Users/msyk/a3");
         try{
         System.out.println(f2.createNewFile());
         System.out.println(f2.setLastModified(fDate));
         System.out.println(f3.createNewFile());
         System.out.println(f3.setLastModified(fDate+120L*24L*60L*60L*1000L));
         }
         catch(Exception e){System.out.println(e.getMessage());}
         
         System.out.println(a=new InternetCalender("Thu, 01 Jan 2003 08:59:59 +0900").parseDateString());
         System.out.println(b=new InternetCalender("Thu, 01 Jan 2003 09:00:00 +0900").parseDateString());
         System.out.println(b-a);
         */
        /*
         File f4 = new File("/Users/msyk/Kaoru Washitaki|Re:インスピレーションの原稿などは？|0ELH.mail");
         System.out.println(a=f4.lastModified());
         System.out.println(f4.exists());
         InternetCalender c = new InternetCalender("Thu, 09 Jan 2003 18:17:38 +0900");
         System.out.println(b=c.parseDateString());
         System.out.println(a-b);
         try{
         System.out.println(f3.createNewFile());
         System.out.println(f3.setLastModified(b));
         }
         catch(Exception e){System.out.println(e.getMessage());}
         */
        /*
         File doc = new File("/Users/msyk/電子メール/temp/refByAliases/<3E1538C9.5020203@mtf.biglobe.ne.jp>.refref");
         File original[] = MacAlias.resolveMacAliases(doc);
         for(int i=0;i<original.length;i++)
         System.out.println("#####"+original[i].getPath());
         */
        /* 
         File doc = new File("/Users/msyk/電子メール/temp/testdata");
         (new MailProcessor()).processDLEachFiles();
         */
        /*   try	{
         MacFolder x = new MacFolder("/Users/msyk/test", true);
         System.out.println("#####"+x.getName());
         System.out.println("#####"+x.exists());
         }
         catch(Exception e)	{}
         */
        //    OMEPreferences omePref = OMEPreferences.getInstance();
        //    omePref.getOMERoot();  
        //    File doc = new File("/Users/msyk/OS_Mail_Environment/InBox/\"Sun Microsyste|FORTE[tm] COMPILER COLLEC|2PPX.mpart");
        //    VirusScan.check(doc);
        //    File te = new File("/Applications/TextEdit.app");
        //    File doc = new File("/Users/msyk/OS_Mail_Environment/書籍雑誌サイト関連/MacPower:MacPeople/江本　剛 <take-e@as|Developer Toolsの著者校です|242N.mpart/Developer Tools.txt.mime");
        //    OpenFile.byStuffItExpander(doc);
        System.exit(0);
        //       Logging.writeMessage( CharsetToLocale.getInstance().getSuitableLocale("US-ASCII"));
        //        OMEPreferences omePref = OMEPreferences.getInstance();
        //        Logging.writeMessage(omePref.getOMERoot().getPath());
        //        MailFormatInfo mf = MailFormatInfo.getInstance();
        //     Logging.writeMessage(mf.getSendingBodyCode());
        //     Logging.writeMessage(mf.getMailSourceCharset("ISO-2022-JP"));

        /*
         Object obj = InternationalUtils.getClassInstance(
         "OME.mailformatinfo.MailFormatInfo", new Locale("ab","CD","ef"));
         Logging.writeMessage(obj.getClass().getName());

         CommentatorInterface commObj 
         = (CommentatorInterface)InternationalUtils.getClassInstance(
         "OME.mailformatinfo.Commentator", new Locale("ja","JP"));
         Logging.writeMessage(commObj.getComment("#", "#", "#", "#", "#"));
         */
    }
}
