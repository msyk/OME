package OME.mailformatinfo;
//
//  TextConverter.java
//  OME_JavaProject
//
// * 2009/6/28:新居:OME_JavaCore2へ移動
//  Created by 新居 雅行 on Mon Feb 11 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//

public interface TextConverter {

    public String convert(String source);
    public String headerTextUnifier(String source);
	public String FileNameUnifier(String source);
    
}
