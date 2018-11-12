//
//  FileReader.java
//  FileReader
//
//  Created by êVãè âÎçs on Thu Jan 16 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//

import java.io.*;
import java.util.*;
import com.apple.cocoa.foundation.*;

public class FileReader {

    static public String getFileContents(String targetFilePath)	{
    
        File calFile = new File(targetFilePath);
        
        byte buffer[] = new byte[(int)calFile.length()];
        try	{
            FileInputStream inSt = new FileInputStream(calFile);
            inSt.read(buffer);
            inSt.close();

            return new String(buffer, "Shift_JIS");

        }
        catch (Exception e)	{
            return "Error: " + e.getMessage();
        }
    
    }

    static public NSArray getFileContentsAsRecord(String targetFilePath)	{
    
        File calFile = new File(targetFilePath);
        
        byte buffer[] = new byte[(int)calFile.length()];
        try	{
            FileInputStream inSt = new FileInputStream(calFile);
            inSt.read(buffer);
            inSt.close();
        }
        catch (Exception e)	{
            return null;
        }
        
        NSMutableArray fData = new NSMutableArray();
        NSMutableArray keys = null, values = null;
        boolean isCollect = false;
        
        StringTokenizer eachLine;
        try{
            eachLine = new StringTokenizer(new String(buffer, "utf-8"), "\n");
        }
        catch (Exception e)	{
            return null;
        }
        
        StringBuffer aLine = new StringBuffer();
        boolean isCollLine = false;
        while ( eachLine.hasMoreTokens() )	{
            String thisToken = eachLine.nextToken();
            
            if ( ! isCollLine && ! thisToken.startsWith(" ") )	{
                aLine.append(thisToken);
                isCollLine = true;
            }
            else if ( isCollLine && thisToken.startsWith(" ") )	{
                aLine.append(thisToken.trim());
                isCollLine = true;
            }
            else if ( isCollLine && ! thisToken.startsWith(" ") )	{
                String colLine = aLine.toString();
                if ( colLine.equals("BEGIN:VEVENT") )	{
                    isCollect = true;
                    keys = new NSMutableArray();
                    values  = new NSMutableArray();
                }
                else if ( colLine.equals("END:VEVENT") )	{
                    isCollect = false;
                    int arrayCount = keys.count();
                    String thisKeys[] = new String[arrayCount];
                    String thisValues[] = new String[arrayCount];
                    keys.getObjects(thisKeys);
                    values.getObjects(thisValues);
                    NSDictionary recordInFile = new NSDictionary(thisValues, thisKeys);
                    fData.addObject(recordInFile);
                }
                else if ( isCollect )	{
                    int colonPos = colLine.indexOf(":");
                    keys.addObject(colLine.substring(0, colonPos));
                    values.addObject(colLine.substring(colonPos+1));
                }
                isCollLine = true;
                aLine = new StringBuffer(thisToken);
            }
        }
        return fData;
    }
    static public NSArray getFileContentsAsArray(String targetFilePath)	{
    
        File calFile = new File(targetFilePath);
        
        byte buffer[] = new byte[(int)calFile.length()];
        try	{
            FileInputStream inSt = new FileInputStream(calFile);
            inSt.read(buffer);
            inSt.close();
        }
        catch (Exception e)	{
            return null;
        }
        
        NSMutableArray fData = new NSMutableArray();        
        StringTokenizer eachLine;
        try{
            eachLine = new StringTokenizer(new String(buffer, "Shift_JIS"), "\n");
        }
        catch (Exception e)	{
            return null;
        }
        
        while ( eachLine.hasMoreTokens() )	{
            String thisToken = eachLine.nextToken();
            fData.addObject(thisToken);
        }
        return fData;
    }

    static public int writeTo(String f, String contents)	{
        int rVal=0;
        try	{
            FileOutputStream outSt = new FileOutputStream(new File(f));
            outSt.write(contents.getBytes("SJIS"));
            outSt.close();
        }
        catch (Exception e)	{
            return -1;
        }
        return rVal;
    
    }

}
