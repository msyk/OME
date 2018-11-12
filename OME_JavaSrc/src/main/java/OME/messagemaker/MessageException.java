package OME.messagemaker;

/**
 *

 * 2009/6/28:新居:OME_JavaCore2へ移動
 @author Masayuki Nii msyk@msyk.net
 $Revision: 1.7 $
 */

public class MessageException extends Exception {

    /**
     * @param message 
     */
    public MessageException() {}

    /**
     *
     * @param message 
     */
    public MessageException(String message) {
        super(message);
    }

    /**
     *
     * @param cause 
     */
    public MessageException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param message 
     * @param cause
     */
    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
