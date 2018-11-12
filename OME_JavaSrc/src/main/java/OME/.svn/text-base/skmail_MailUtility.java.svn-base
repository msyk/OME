package OME;

import java.io.UnsupportedEncodingException;

import javax.mail.*;
import javax.mail.internet.*;

//
//  skmail_MailUtility.java
//  OME_JavaProject
//
//  Created by êVãè âÎçs on Wed Jan 15 2003.
//  Copyright (c) 2003 __MyCompanyName__. All rights reserved.
//
/*


 */

public class skmail_MailUtility {

    //////////////////////////////////////////////////////////////////////////
    /**
     * This method set Content-Disposition: with RFC2231 encoding.
     * It is required JavaMail1.2.
     */
    public static void setFileName(Part part, String filename, String charset, String lang) throws MessagingException {
        // Set the Content-Disposition "filename" parameter
        ContentDisposition disposition;
        String[] strings = part.getHeader("Content-Disposition");
        if (strings == null || strings.length < 1) {
            disposition = new ContentDisposition(Part.ATTACHMENT);
        } else {
            disposition = new ContentDisposition(strings[0]);
            disposition.getParameterList().remove("filename");
        }

        part.setHeader("Content-Disposition", disposition.toString()
                + encodeParameter("filename", filename, charset, lang));

        ContentType cType;
        strings = part.getHeader("Content-Type");
        if (strings == null || strings.length < 1) {
            cType = new ContentType(part.getDataHandler().getContentType());
        } else {
            cType = new ContentType(strings[0]);
        }

        try {
            // I want to public the MimeUtility#doEncode()!!!
            String mimeString = MimeUtility.encodeWord(filename, charset, "B");
            // cut <CRLF>...
            StringBuffer sb = new StringBuffer();
            int i;
            while ((i = mimeString.indexOf('\r')) != -1) {
                sb.append(mimeString.substring(0, i));
                mimeString = mimeString.substring(i + 2);
            }
            sb.append(mimeString);

            cType.setParameter("name", new String(sb));
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Encoding error", e);
        }
        part.setHeader("Content-Type", cType.toString());
    }

    /**
     * This method encodes the parameter.
     * <P>
     * But most MUA cannot decode the encoded parameters by this method.<BR>
     * I recommend using the "Content-Type:"'s name parameter both.
     * </P>
     */
    /**
     * @param name 
     * @param value 
     * @param encoding 
     * @param lang 
     * @return 
     *
     */
    public static String encodeParameter(String name, String value, String encoding, String lang) {
        StringBuffer result = new StringBuffer();
        StringBuffer encodedPart = new StringBuffer();

        boolean needWriteCES = !isAllAscii(value);
        boolean CESWasWritten = false;
        boolean encoded;
        boolean needFolding = false;
        int sequenceNo = 0;
        int column;

        while (value.length() > 0) {
            // index of boundary of ascii/non ascii
            int lastIndex;
            boolean isAscii = value.charAt(0) < 0x80;
            for (lastIndex = 1; lastIndex < value.length(); lastIndex++) {
                if (value.charAt(lastIndex) < 0x80) {
                    if (!isAscii) break;
                } else {
                    if (isAscii) break;
                }
            }
            if (lastIndex != value.length()) needFolding = true;

            RETRY: while (true) {
                encodedPart.setLength(0);
                String target = value.substring(0, lastIndex);

                byte[] bytes;
                try {
                    if (isAscii) {
                        bytes = target.getBytes("us-ascii");
                    } else {
                        bytes = target.getBytes(encoding);
                    }
                } catch (UnsupportedEncodingException e) {
                    bytes = target.getBytes(); // use default encoding
                    encoding = MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
                }

                encoded = false;
                // It is not strict.
                column = name.length() + 7; // size of " " and "*nn*=" and ";"

                for (int i = 0; i < bytes.length; i++) {
                    if ((bytes[i] >= '0' && bytes[i] <= '9') || (bytes[i] >= 'A' && bytes[i] <= 'Z')
                            || (bytes[i] >= 'a' && bytes[i] <= 'z') || bytes[i] == '$' || bytes[i] == '.'
                            || bytes[i] == '!') {
                        encodedPart.append((char) bytes[i]);
                        column++;
                    } else {
                        encoded = true;
                        encodedPart.append('%');
                        String hex = Integer.toString(bytes[i] & 0xff, 16);
                        if (hex.length() == 1) {
                            encodedPart.append('0');
                        }
                        encodedPart.append(hex);
                        column += 3;
                    }
                    if (column > 76) {
                        needFolding = true;
                        lastIndex /= 2;
                        continue RETRY;
                    }
                }

                result.append(";\r\n ").append(name);
                if (needFolding) {
                    result.append('*').append(sequenceNo);
                    sequenceNo++;
                }
                if (!CESWasWritten && needWriteCES) {
                    result.append("*=");
                    CESWasWritten = true;
                    result.append(encoding).append('\'');
                    if (lang != null) result.append(lang);
                    result.append('\'');
                } else if (encoded) {
                    result.append("*=");
                } else {
                    result.append('=');
                }
                result.append(new String(encodedPart));
                value = value.substring(lastIndex);
                break;
            }
        }
        return new String(result);
    }

    /** check if contains only ascii characters in text. */
    public static boolean isAllAscii(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 0x7f) { // non-ascii
            return false; }
        }
        return true;
    }

    //////////////////////////////////////////////////////////////////////////
    /**
     * This method decode the RFC2231 encoded filename parameter
     * instead of Part#getFileName().
     */
    public static String getFileName(Part part) throws MessagingException {
        String[] disposition = part.getHeader("Content-Disposition");
        // A patch by YOSI (Thanx)
        // http://www.sk-jp.com/cgibin/treebbs.cgi?kako=1&all=227&s=227
        String filename;
        if (disposition == null || disposition.length < 1
                || (filename = getParameter(disposition[0], "filename")) == null) {
            filename = part.getFileName();
            if (filename != null) { return decodeParameterSpciallyJapanese(filename); }
            return null;
        }
        return filename;
    }

    static class Encoding {

        String encoding = "us-ascii";

        String lang = "";
    }

    /**
     * This method decodes the parameter
     * which be encoded (folded) by RFC2231 method.
     * <P>
     * The parameter's order should be considered.
     * </P>
     */
    /**
     * @param header 
     * @param name 
     * @return 
	*/
    public static String getParameter(String header, String name) throws ParseException {
        if (header == null) return null;
        header = decodeParameterSpciallyJapanese(header);

        HeaderTokenizer tokenizer = new HeaderTokenizer(header, HeaderTokenizer.MIME, true);
        HeaderTokenizer.Token token;
        StringBuffer sb = new StringBuffer();
        // It is specified in first encoded-part.
        Encoding encoding = new Encoding();

        String n;
        String v;

        try {
            while (true) {
                token = tokenizer.next();
                if (token.getType() == token.EOF) break;
                if (token.getType() != ';') continue;

                token = tokenizer.next();
                checkType(token);
                n = token.getValue();

                token = tokenizer.next();
                if (token.getType() != '=') { throw new ParseException("Illegal token : " + token.getValue()); }

                token = tokenizer.next();
                checkType(token);
//                v = token.getValue();

/* ファイル名に / がある場合に、そこでトークンが区切れるという問題が発生している。
これは、JavaMailのHeaderTokenizerの問題だろうけど、結果的にここで対処しないとどうしようもない。
あまり前後と違うプログラムだが、ともかくQuick Patch!
msyk 2007/7/8
*/
				v = "";
				while ( token.getType() != token.EOF )	{
					v = v + token.getValue();
					if ( tokenizer.peek().getType() == ';')	break;
					token = tokenizer.next();
					//	checkType(token);
				}

/* End of patch! */

                if (n.equalsIgnoreCase(name)) { 
                // It is not divided and is not encoded.
                return v; }

                int index = name.length();

                if (!n.startsWith(name) || n.charAt(index) != '*') {
                    // another parameter
                    continue;
                }
                // be folded, or be encoded
                int lastIndex = n.length() - 1;
                if (n.charAt(lastIndex) == '*') {
                    // http://www.sk-jp.com/cgibin/treebbs.cgi?all=399&s=399
                    if (index == lastIndex || n.charAt(index + 1) == '0') {
                        // decode as initial-section
                        sb.append(decodeRFC2231(v, encoding, true));
                    } else {
                        // decode as other-sections
                        sb.append(decodeRFC2231(v, encoding, false));
                    }
                } else {
                    sb.append(v);
                }
                if (index == lastIndex) {
                    // not folding
                    break;
                }
            }
            if (sb.length() == 0) return null;
            return new String(sb);
        } catch (UnsupportedEncodingException e) {
            throw new ParseException(e.toString());
        }
    }

    private static String decodeRFC2231(String s, Encoding encoding, boolean isInitialSection) throws ParseException,
            UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        int i = 0;

        if (isInitialSection) {
            int work = s.indexOf('\'');
            if (work > 0) {
                encoding.encoding = s.substring(0, work);
                work++;
                i = s.indexOf('\'', work);
                if (i < 0) { throw new ParseException("lang tag area was missing."); }
                encoding.lang = s.substring(work, i);
                i++;
            }
        }

        try {
            for (; i < s.length(); i++) {
                if (s.charAt(i) == '%') {
                    sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                    i += 2;
                    continue;
                }
                sb.append(s.charAt(i));
            }
            return new String(new String(sb).getBytes("ISO-8859-1"), encoding.encoding);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException(s + " :: this string were not decoded.");
        }
    }

    private static String decodeParameterSpciallyJapanese(String s) throws ParseException {
        try {
            // decode by character encoding.
            // if string are all ASCII, it is not translated.
            s = new String(s.getBytes("ISO-8859-1"), "JISAutoDetect");
            // decode by RFC2047.
            // if string doesn't contain encoded-word, it is not translated.
            return decodeText(s);
        } catch (UnsupportedEncodingException e) {}
        throw new ParseException("Unsupported Encoding");
    }

    private static void checkType(HeaderTokenizer.Token token) throws ParseException {
        int t = token.getType();
        if (t != HeaderTokenizer.Token.ATOM && t != HeaderTokenizer.Token.QUOTEDSTRING) { throw new ParseException(
                "Illegal token : " + token.getValue()); }
    }

    //////////////////////////////////////////////////////////////////////////
    /**
     * @param source encoded text
     * @return decoded text
     */
    public static String decodeText(String source) throws ParseException {
        if (source == null) return null;

        // specially for Japanese
        if (source.indexOf('\u001b') >= 0) {
            // ISO-2022-JP
            try {
                return new String(source.getBytes("ISO-8859-1"), "ISO-2022-JP");
            } catch (UnsupportedEncodingException e) {
                throw new InternalError();
            }
        }

        // first, decode by JavaMail.
        int startIndex;
        int endIndex = 0;
        String encodedWord;
        StringBuffer buf = new StringBuffer();

        while (true) {
            startIndex = source.indexOf("=?", endIndex);
            if (startIndex == -1) {
                buf.append(source.substring(endIndex));
                break;
            } else if (startIndex > endIndex) {
                String work = source.substring(endIndex, startIndex);
                if (indexOfNonLWSP(work, 0, false) > -1) {
                    buf.append(work);
                }
            }
            // skip "=?..?..?"
            // because In the case of "Q" encoding,
            // it exists that a word is the case of "=?..?Q?=1B...?=".
            endIndex = source.indexOf('?', startIndex + 2);
            if (endIndex == -1) {
                buf.append(source.substring(startIndex));
                break;
            }
            endIndex = source.indexOf('?', endIndex + 1);
            if (endIndex == -1) {
                buf.append(source.substring(startIndex));
                break;
            }
            endIndex = source.indexOf("?=", endIndex + 1);
            if (endIndex == -1) {
                buf.append(source.substring(startIndex));
                break;
            }
            endIndex += 2;

            encodedWord = source.substring(startIndex, endIndex);
            try {
                buf.append(MimeUtility.decodeWord(encodedWord));
            } catch (UnsupportedEncodingException ex) {
                buf.append(encodedWord);
            }
        }
        String decodedText = new String(buf);

        if (decodedText.indexOf('\u001b') >= 0) {
            try {
                return new String(decodedText.getBytes("ISO-8859-1"), "ISO-2022-JP");
            } catch (UnsupportedEncodingException e) {
                throw new InternalError();
            }
        }
        return decodedText;
    }

    /**
     * @param source 
     * @param startIndex 
     * @param decrease 
     * @return
     */
    public static int indexOfNonLWSP(String source, int startIndex, boolean decrease) {
        char c;
        int inc = 1;
        if (decrease) inc = -1;

        for (int i = startIndex; i >= 0 && i < source.length(); i += inc) {
            c = source.charAt(i);
            if (!isLWSP(c)) { return i; }
        }
        return -1;
    }

    public static boolean isLWSP(char c) {
        return c == '\r' || c == '\n' || c == ' ' || c == '\t';
    }

}
