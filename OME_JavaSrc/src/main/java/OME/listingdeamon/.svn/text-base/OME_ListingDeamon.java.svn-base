package OME.listingdeamon;

import java.io.File;
import java.util.Date;

//  OME_ListingDeamon.java
//  OME_JavaProject
//
// * 2009/6/28:新居:OME_JavaCore2へ移動
//  Created by Masayuki Nii on Sun Jul 13 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

public class OME_ListingDeamon {

    public static void main(String argv[]) {
        long start, end;

        System.out.println("# Target directory: " + argv[0]);
        start = new Date().getTime();
        System.out.println("# checkpoint 0: " + start);
        MailListInFolder obj = new MailListInFolder(new File(argv[0]), false);
        end = new Date().getTime();
        System.out.println("# Constructing the object: " + (end - start) + "ms");

        start = new Date().getTime();
        obj.toFile();
        obj.toHTMLFile();
        end = new Date().getTime();
        System.out.println("# Make files : " + (end - start) + "ms");

        //		System.out.println(obj.toString());
    }
}
