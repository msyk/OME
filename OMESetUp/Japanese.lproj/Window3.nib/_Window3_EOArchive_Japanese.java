// _Window3_EOArchive_Japanese.java
// Generated by EnterpriseObjects palette at 2005\u5e747\u670823\u65e5\u571f\u66dc\u65e5 0\u664207\u520621\u79d2Asia/Tokyo

import com.webobjects.eoapplication.*;
import com.webobjects.eocontrol.*;
import com.webobjects.eointerface.*;
import com.webobjects.eointerface.swing.*;
import com.webobjects.foundation.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;

public class _Window3_EOArchive_Japanese extends com.webobjects.eoapplication.EOArchive {
    IBHelpConnector _iBHelpConnector0;
    com.webobjects.eointerface.swing.EOFrame _eoFrame0;
    com.webobjects.eointerface.swing.EOTextArea _nsTextView0;
    com.webobjects.eointerface.swing.EOTextField _nsTextField0, _nsTextField1;
    com.webobjects.eointerface.swing.EOView _nsBox0, _nsBox1, _nsBox2, _nsBox3, _nsBox4, _nsBox5;
    javax.swing.JButton _nsButton0, _nsButton1, _nsButton2;
    javax.swing.JCheckBox _nsButton3;
    javax.swing.JPanel _nsView0;

    public _Window3_EOArchive_Japanese(Object owner, NSDisposableRegistry registry) {
        super(owner, registry);
    }

    protected void _construct() {
        Object owner = _owner();
        EOArchive._ObjectInstantiationDelegate delegate = (owner instanceof EOArchive._ObjectInstantiationDelegate) ? (EOArchive._ObjectInstantiationDelegate)owner : null;
        Object replacement;

        super._construct();

        _nsBox5 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "");
        _nsBox4 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "");
        _nsTextField1 = (com.webobjects.eointerface.swing.EOTextField)_registered(new com.webobjects.eointerface.swing.EOTextField(), "NSTextField");
        _nsBox3 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSView");
        _nsBox2 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSBox2");
        _nsTextField0 = (com.webobjects.eointerface.swing.EOTextField)_registered(new com.webobjects.eointerface.swing.EOTextField(), "NSTextField1");
        _nsButton2 = (javax.swing.JButton)_registered(new javax.swing.JButton("\u4f55\u3082\u3057\u306a\u3044\u3067\u7d42\u4e86"), "NSButton4");
        _iBHelpConnector0 = (IBHelpConnector)_registered(new IBHelpConnector(), "");
        _nsBox1 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSView");
        _nsBox0 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSBox");
        _nsButton1 = (javax.swing.JButton)_registered(new javax.swing.JButton("\u6b21\u3078"), "NSButton2");

        if ((delegate != null) && ((replacement = delegate.objectForOutletPath(this, "sign")) != null)) {
            _nsTextView0 = (replacement == EOArchive._ObjectInstantiationDelegate.NullObject) ? null : (com.webobjects.eointerface.swing.EOTextArea)replacement;
            _replacedObjects.setObjectForKey(replacement, "_nsTextView0");
        } else {
            _nsTextView0 = (com.webobjects.eointerface.swing.EOTextArea)_registered(new com.webobjects.eointerface.swing.EOTextArea(), "NSTextView");
        }

        if ((delegate != null) && ((replacement = delegate.objectForOutletPath(this, "isSaveOnServer")) != null)) {
            _nsButton3 = (replacement == EOArchive._ObjectInstantiationDelegate.NullObject) ? null : (javax.swing.JCheckBox)replacement;
            _replacedObjects.setObjectForKey(replacement, "_nsButton3");
        } else {
            _nsButton3 = (javax.swing.JCheckBox)_registered(new javax.swing.JCheckBox("\u53d7\u4fe1\u30e1\u30fc\u30eb\u3092\u30b5\u30fc\u30d0\u306b\u6b8b\u3059"), "NSButton");
        }

        _nsButton0 = (javax.swing.JButton)_registered(new javax.swing.JButton("\u524d\u3078"), "NSButton3");

        if ((delegate != null) && ((replacement = delegate.objectForOutletPath(this, "window")) != null)) {
            _eoFrame0 = (replacement == EOArchive._ObjectInstantiationDelegate.NullObject) ? null : (com.webobjects.eointerface.swing.EOFrame)replacement;
            _replacedObjects.setObjectForKey(replacement, "_eoFrame0");
        } else {
            _eoFrame0 = (com.webobjects.eointerface.swing.EOFrame)_registered(new com.webobjects.eointerface.swing.EOFrame(), "Window3");
        }

        _nsView0 = (JPanel)_eoFrame0.getContentPane();
    }

    protected void _awaken() {
        super._awaken();
        _nsButton1.addActionListener((com.webobjects.eointerface.swing.EOControlActionAdapter)_registered(new com.webobjects.eointerface.swing.EOControlActionAdapter(_owner(), "showWindow4", _nsButton1), ""));

        if (_replacedObjects.objectForKey("_eoFrame0") == null) {
            _connect(_owner(), _eoFrame0, "window");
        }

        if (_replacedObjects.objectForKey("_nsTextView0") == null) {
            _connect(_owner(), _nsTextView0, "sign");
        }

        if (_replacedObjects.objectForKey("_nsButton3") == null) {
            _connect(_owner(), _nsButton3, "isSaveOnServer");
        }

        _nsButton0.addActionListener((com.webobjects.eointerface.swing.EOControlActionAdapter)_registered(new com.webobjects.eointerface.swing.EOControlActionAdapter(_owner(), "showWindow2", _nsButton0), ""));
    }

    protected void _init() {
        super._init();
        if (!(_nsBox5.getLayout() instanceof EOViewLayout)) { _nsBox5.setLayout(new EOViewLayout()); }
        _nsTextView0.setSize(460, 112);
        _nsTextView0.setLocation(14, 11);
        ((EOViewLayout)_nsBox5.getLayout()).setAutosizingMask(_nsTextView0, EOViewLayout.MinYMargin);
        _nsBox5.add(_nsTextView0);
        if (!(_nsBox4.getLayout() instanceof EOViewLayout)) { _nsBox4.setLayout(new EOViewLayout()); }
        _nsBox5.setSize(488, 137);
        _nsBox5.setLocation(2, 18);
        ((EOViewLayout)_nsBox4.getLayout()).setAutosizingMask(_nsBox5, EOViewLayout.MinYMargin);
        _nsBox4.add(_nsBox5);
        _nsBox4.setBorder(new com.webobjects.eointerface.swing._EODefaultBorder("\u7f72\u540d", true, "Lucida Grande", 13, Font.PLAIN));
        _setFontForComponent(_nsTextField1, "Lucida Grande", 18, Font.PLAIN);
        _nsTextField1.setEditable(false);
        _nsTextField1.setOpaque(false);
        _nsTextField1.setText("\u30e1\u30fc\u30eb\u53d6\u308a\u51fa\u3057\u306e\u5404\u7a2e\u8a2d\u5b9a\u3068\u7f72\u540d");
        _nsTextField1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        _nsTextField1.setSelectable(false);
        _nsTextField1.setEnabled(true);
        _nsTextField1.setBorder(null);
        if (!(_nsBox2.getLayout() instanceof EOViewLayout)) { _nsBox2.setLayout(new EOViewLayout()); }
        _nsBox3.setSize(527, 1);
        _nsBox3.setLocation(2, 2);
        ((EOViewLayout)_nsBox2.getLayout()).setAutosizingMask(_nsBox3, EOViewLayout.MinYMargin);
        _nsBox2.add(_nsBox3);
        _nsBox2.setBorder(new com.webobjects.eointerface.swing._EODefaultBorder("", true, "Lucida Grande", 13, Font.PLAIN));
        _setFontForComponent(_nsTextField0, "Lucida Grande", 11, Font.PLAIN);
        _nsTextField0.setEditable(false);
        _nsTextField0.setOpaque(false);
        _nsTextField0.setText("\u30c6\u30b9\u30c8\u7684\u306b\u5229\u7528\u3059\u308b\u5834\u5408\u306f\u3001\u3053\u306e\u30c1\u30a7\u30c3\u30af\u3092\u5165\u308c\u3066\u304f\u3060\u3055\u3044\u3002\u305d\u3046\u3059\u308c\u3070\u3001\u30b5\u30fc\u30d0\u306b\u3042\u308b\u30e1\u30fc\u30eb\u306f\u524a\u9664\u3055\u308c\u305a\u3001\u305d\u308c\u307e\u3067\u4f7f\u3063\u3066\u3044\u305f\u30e1\u30fc\u30eb\u30bd\u30d5\u30c8\u3067\u6539\u3081\u3066\u30e1\u30fc\u30eb\u306e\u53d6\u308a\u8fbc\u307f\u304c\u3067\u304d\u307e\u3059");
        _nsTextField0.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        _nsTextField0.setSelectable(false);
        _nsTextField0.setEnabled(true);
        _nsTextField0.setBorder(null);
        _nsButton2.addActionListener((com.webobjects.eointerface.swing.EOControlActionAdapter)_registered(new com.webobjects.eointerface.swing.EOControlActionAdapter(null, "terminate", _nsButton2), ""));
        _setFontForComponent(_nsButton2, "Lucida Grande", 11, Font.PLAIN);
        _nsButton2.setMargin(new Insets(0, 2, 0, 2));
        if (!(_nsBox0.getLayout() instanceof EOViewLayout)) { _nsBox0.setLayout(new EOViewLayout()); }
        _nsBox1.setSize(482, 60);
        _nsBox1.setLocation(2, 18);
        ((EOViewLayout)_nsBox0.getLayout()).setAutosizingMask(_nsBox1, EOViewLayout.MinYMargin);
        _nsBox0.add(_nsBox1);
        _nsBox0.setBorder(new com.webobjects.eointerface.swing._EODefaultBorder("\u5404\u7a2e\u8a2d\u5b9a", true, "Lucida Grande", 13, Font.PLAIN));
        _setFontForComponent(_nsButton1, "Lucida Grande", 13, Font.PLAIN);
        _nsButton1.setMargin(new Insets(0, 2, 0, 2));

        if (_replacedObjects.objectForKey("_nsTextView0") == null) {
            _nsTextView0.setEditable(true);
            _nsTextView0.setOpaque(true);
            _nsTextView0.setText("");
        }

        if (_replacedObjects.objectForKey("_nsButton3") == null) {
            _setFontForComponent(_nsButton3, "Lucida Grande", 13, Font.PLAIN);
        }

        _setFontForComponent(_nsButton0, "Lucida Grande", 13, Font.PLAIN);
        _nsButton0.setMargin(new Insets(0, 2, 0, 2));
        if (!(_nsView0.getLayout() instanceof EOViewLayout)) { _nsView0.setLayout(new EOViewLayout()); }
        _nsButton0.setSize(77, 26);
        _nsButton0.setLocation(347, 366);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsButton0, EOViewLayout.MinYMargin);
        _nsView0.add(_nsButton0);
        _nsBox0.setSize(486, 80);
        _nsBox0.setLocation(18, 64);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsBox0, EOViewLayout.MinYMargin);
        _nsView0.add(_nsBox0);
        _nsButton2.setSize(137, 22);
        _nsButton2.setLocation(13, 367);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsButton2, EOViewLayout.MinYMargin);
        _nsView0.add(_nsButton2);
        _nsButton3.setSize(178, 17);
        _nsButton3.setLocation(40, 103);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsButton3, EOViewLayout.MinYMargin);
        _nsView0.add(_nsButton3);
        _nsTextField0.setSize(272, 56);
        _nsTextField0.setLocation(228, 83);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsTextField0, EOViewLayout.MinYMargin);
        _nsView0.add(_nsTextField0);
        _nsButton1.setSize(77, 26);
        _nsButton1.setLocation(431, 366);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsButton1, EOViewLayout.MinYMargin);
        _nsView0.add(_nsButton1);
        _nsBox2.setSize(531, 5);
        _nsBox2.setLocation(-7, 54);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsBox2, EOViewLayout.MinYMargin);
        _nsView0.add(_nsBox2);
        _nsTextField1.setSize(446, 22);
        _nsTextField1.setLocation(14, 26);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsTextField1, EOViewLayout.MinYMargin);
        _nsView0.add(_nsTextField1);
        _nsBox4.setSize(492, 157);
        _nsBox4.setLocation(12, 162);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsBox4, EOViewLayout.MinYMargin);
        _nsView0.add(_nsBox4);

        if (_replacedObjects.objectForKey("_eoFrame0") == null) {
            _nsView0.setSize(517, 390);
            _eoFrame0.setTitle("\u5404\u7a2e\u8a2d\u5b9a\u3068\u7f72\u540d 3/4");
            _eoFrame0.setLocation(232, 232);
            _eoFrame0.setSize(517, 390);
        }
    }
}
