package OME.messagemaker;

import javax.mail.Part;

import OME.Logging;

/**
 *
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author Masayuki Nii
 * @version $Revision: 1.14 $
 */
public abstract class PartProcessor implements Runnable {

    // TODO: @throws 

    /**
     * @throws MessageException
     */
    public abstract void preProcess() throws Exception;

    /**
     * @throws MessageException
     */
    public abstract void processMessage() throws Exception;

    /**
     * @throws Exception 
     */
    public abstract void afterProcess() throws Exception;

    private Part mailSource;

    public MessageMaker parentMM;

    /**
     * @param messageSource 
     */
    public void setMailSource(Part messageSource) {
        mailSource = messageSource;
    }

    /**
     * @return 
     */
    public Part getMailSource() {
        return mailSource;
    }

    /**
     * @param parent 
     */
    public void setMessageMaker(MessageMaker parent) {
        parentMM = parent;
    }

    /**
     * @param key 
     * @return 
     */
    public Object getProperty(String key) {
        return parentMM.getPropertyHashMap().get(key);
    }

    /**
     * @param key 
     * @param value 
     */
    public void setProperty(String key, Object value) {
        parentMM.getPropertyHashMap().put(key, value);
    }

    /**
     */
    public void run() {
        try {
            processMessage();
        } catch (MessageException e) {
            Logging.writeErrorMessage(174, e, "Exception in processMessage method. Continue to process this message.");
        } catch (Exception e) {
            Logging.writeErrorMessage(175, e, "Exception in processMessage method. Stop to process this message "
                    + "and will proceed to the next message.");
            parentMM.setCatchedException(e);
        }
    }
}
