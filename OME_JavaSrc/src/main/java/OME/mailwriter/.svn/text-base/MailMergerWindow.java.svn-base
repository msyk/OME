package OME.mailwriter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import OME.Logging;

//import com.apple.cocoa.application.*;
//import com.apple.cocoa.foundation.NSAutoreleasePool;

//import com.borland.jbcl.layout.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * 2009/6/28:新居:OME_JavaCore2へ移動
 * @author unascribed
 * @version 1.0
 */

public class MailMergerWindow extends JFrame {

    JPanel contentPane;

    JRadioButton format1 = new JRadioButton();

    JRadioButton format2 = new JRadioButton();

    JTextField tempFilePath = new JTextField();

    JTextField insertFilePath = new JTextField();

    JButton jButton1 = new JButton();

    JButton jButton2 = new JButton();

    JButton jButton3 = new JButton();

    JButton jButton4 = new JButton();

    JLabel jLabel1 = new JLabel();

    JLabel jLabel2 = new JLabel();

    JLabel jLabel3 = new JLabel();

    JButton jButton5 = new JButton();

    ButtonGroup buttonGroup1 = new ButtonGroup();

    JButton jButton6 = new JButton();

    JCheckBox jCheckBox1 = new JCheckBox();

    //Construct the frame
    public MailMergerWindow() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Component initialization
    private void jbInit() throws Exception {
        //setIconImage(Toolkit.getDefaultToolkit().createImage(Frame1.class.getResource("[Your Icon]")));
        contentPane = (JPanel) this.getContentPane();
        //    format1.setFont(new java.awt.Font("Osaka", 0, 14));
        format1.setText("フォーマット1：差し込みファイルから1メールを作成");
        format1.setBounds(new Rectangle(76, 10, 394, 25));
        contentPane.setLayout(null);
        this.setResizable(false);
        this.setSize(new Dimension(519, 279));
        this.setTitle("OME_差し込みメール作成");
        //    format2.setFont(new java.awt.Font("Osaka", 0, 14));
        format2.setSelected(true);
        format2.setText("フォーマット2：差し込みファイル1行につきメール1通");
        format2.setBounds(new Rectangle(76, 36, 394, 29));
        //    tempFilePath.setFont(new java.awt.Font("Osaka", 0, 14));
        tempFilePath.setText("");
        tempFilePath.setBounds(new Rectangle(161, 84, 337, 27));
        //    insertFilePath.setFont(new java.awt.Font("Osaka", 0, 14));
        insertFilePath.setText("");
        insertFilePath.setBounds(new Rectangle(161, 144, 338, 29));
        jButton1.setBounds(new Rectangle(229, 213, 175, 36));
        //    jButton1.setFont(new java.awt.Font("Osaka", 0, 14));
        jButton1.setText("送信ファイル作成");
        jButton1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
        jButton2.setBounds(new Rectangle(411, 211, 87, 35));
        //    jButton2.setFont(new java.awt.Font("Osaka", 0, 14));
        jButton2.setText("終了");
        jButton2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton2_actionPerformed(e);
            }
        });
        jButton3.setBounds(new Rectangle(421, 112, 77, 27));
        //    jButton3.setFont(new java.awt.Font("Osaka", 0, 14));
        jButton3.setText("参照...");
        jButton3.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton3_actionPerformed(e);
            }
        });
        jButton4.setBounds(new Rectangle(424, 173, 74, 29));
        //    jButton4.setFont(new java.awt.Font("Osaka", 0, 14));
        jButton4.setText("参照...");
        jButton4.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton4_actionPerformed(e);
            }
        });
        //    jLabel1.setFont(new java.awt.Font("Osaka", 0, 14));
        jLabel1.setText("処理形態");
        jLabel1.setBounds(new Rectangle(9, 9, 69, 26));
        //    jLabel2.setFont(new java.awt.Font("Osaka", 0, 14));
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText("テンプレートファイル");
        jLabel2.setBounds(new Rectangle(2, 82, 148, 25));
        //    jLabel3.setFont(new java.awt.Font("Osaka", 0, 14));
        jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel3.setText("差し込みファイル");
        jLabel3.setBounds(new Rectangle(1, 143, 150, 31));
        jButton5.setBounds(new Rectangle(6, 203, 181, 35));
        //    jButton5.setFont(new java.awt.Font("Osaka", 0, 14));
        jButton5.setText("メール送信");
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton5_actionPerformed(e);
            }
        });
        jButton6.setText("ドキュメント参照-Web");
        //   jButton6.setEnabled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                jButton6_actionPerformed(e);
            }
        });
        //    jButton6.setFont(new java.awt.Font("Osaka", 0, 14));
        jButton6.setBounds(new Rectangle(5, 237, 182, 35));
        contentPane.setActionMap(null);
        jCheckBox1.setText("1行目は無視する（フォーマット2）");
        jCheckBox1.setBounds(new Rectangle(163, 178, 253, 20));
        //    jCheckBox1.setFont(new java.awt.Font("Osaka", 0, 14));
        contentPane.add(jLabel1, null);
        contentPane.add(format1, null);
        contentPane.add(format2, null);
        contentPane.add(tempFilePath, null);
        contentPane.add(jButton3, null);
        contentPane.add(insertFilePath, null);
        contentPane.add(jLabel2, null);
        contentPane.add(jLabel3, null);
        contentPane.add(jButton4, null);
        contentPane.add(jButton2, null);
        contentPane.add(jButton1, null);
        contentPane.add(jButton5, null);
        contentPane.add(jButton6, null);
        contentPane.add(jCheckBox1, null);
        buttonGroup1.add(format1);
        buttonGroup1.add(format2);

        this.setSize(520, 320);
    }

    //Overridden so we can exit when window is closed
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

    void jButton3_actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            tempFilePath.setText(chooser.getSelectedFile().getPath());
        }
    }

    void jButton4_actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            insertFilePath.setText(chooser.getSelectedFile().getPath());
        }
    }

    void jButton2_actionPerformed(ActionEvent e) {
        System.exit(0);
    }

    void jButton1_actionPerformed(ActionEvent e) {
        File templateFile = new File(tempFilePath.getText());
        InputStream insertStream = null;
        try {
            insertStream = new FileInputStream(new File(insertFilePath.getText()));
        } catch (Exception ex) {
            Logging.writeMessage(ex.getMessage());
        }
        if (format1.isSelected()) {
            (new MailMerger()).processingFormat1(templateFile, insertStream);
        } else if (format2.isSelected()) {
            (new MailMerger()).processingFormat2(templateFile, insertStream, jCheckBox1.isSelected());
        }
        try {
            insertStream.close();
        } catch (Exception ex) {
            Logging.writeMessage(ex.getMessage());
        }
    }

    void jButton5_actionPerformed(ActionEvent e) {

    }

    void jButton6_actionPerformed(ActionEvent e) {
/*        NSWorkspace ws = NSWorkspace.sharedWorkspace();
        try {
            ws.openURL(new java.net.URL("http://mac-ome.jp/site/manual/d00/d01mailmerger.html"));
        } catch (Exception ex) {

        }
        int myPool = NSAutoreleasePool.push();
        NSSound snd = NSSound.soundNamed("Frog");
        snd.play();

        NSAutoreleasePool.pop(myPool);
*/    }
}