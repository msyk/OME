// _Finish_EOArchive_English.java
// Generated by EnterpriseObjects palette at 2005\u5e747\u670813\u65e5\u6c34\u66dc\u65e5 20\u664239\u520640\u79d2Asia/Tokyo

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

public class _Finish_EOArchive_English extends com.webobjects.eoapplication.EOArchive {
    com.webobjects.eointerface.swing.EOFrame _eoFrame0;
    com.webobjects.eointerface.swing.EOTextField _nsTextField0, _nsTextField1, _nsTextField2;
    com.webobjects.eointerface.swing.EOView _nsBox0, _nsBox1;
    javax.swing.JButton _nsButton0, _nsButton1;
    javax.swing.JPanel _nsView0;

    public _Finish_EOArchive_English(Object owner, NSDisposableRegistry registry) {
        super(owner, registry);
    }

    protected void _construct() {
        Object owner = _owner();
        EOArchive._ObjectInstantiationDelegate delegate = (owner instanceof EOArchive._ObjectInstantiationDelegate) ? (EOArchive._ObjectInstantiationDelegate)owner : null;
        Object replacement;

        super._construct();

        _nsButton1 = (javax.swing.JButton)_registered(new javax.swing.JButton("Download"), "NSButton111");
        _nsTextField2 = (com.webobjects.eointerface.swing.EOTextField)_registered(new com.webobjects.eointerface.swing.EOTextField(), "NSTextField212");
        _nsTextField1 = (com.webobjects.eointerface.swing.EOTextField)_registered(new com.webobjects.eointerface.swing.EOTextField(), "NSTextField211");
        _nsBox1 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSView");
        _nsBox0 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSBox1");
        _nsButton0 = (javax.swing.JButton)_registered(new javax.swing.JButton("Close"), "NSButton");
        _nsTextField0 = (com.webobjects.eointerface.swing.EOTextField)_registered(new com.webobjects.eointerface.swing.EOTextField(), "NSTextField");

        if ((delegate != null) && ((replacement = delegate.objectForOutletPath(this, "window")) != null)) {
            _eoFrame0 = (replacement == EOArchive._ObjectInstantiationDelegate.NullObject) ? null : (com.webobjects.eointerface.swing.EOFrame)replacement;
            _replacedObjects.setObjectForKey(replacement, "_eoFrame0");
        } else {
            _eoFrame0 = (com.webobjects.eointerface.swing.EOFrame)_registered(new com.webobjects.eointerface.swing.EOFrame(), "Panel1");
        }

        _nsView0 = (JPanel)_eoFrame0.getContentPane();
    }

    protected void _awaken() {
        super._awaken();

        if (_replacedObjects.objectForKey("_eoFrame0") == null) {
            _connect(_owner(), _eoFrame0, "window");
        }

        _nsButton1.addActionListener((com.webobjects.eointerface.swing.EOControlActionAdapter)_registered(new com.webobjects.eointerface.swing.EOControlActionAdapter(_owner(), "downloadMails", _nsButton1), ""));
    }

    protected void _init() {
        super._init();
        _setFontForComponent(_nsButton1, "Lucida Grande", 13, Font.PLAIN);
        _nsButton1.setMargin(new Insets(0, 2, 0, 2));
        _setFontForComponent(_nsTextField2, "Lucida Grande", 13, Font.PLAIN);
        _nsTextField2.setEditable(false);
        _nsTextField2.setOpaque(false);
        _nsTextField2.setText("Download the mails\n");
        _nsTextField2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        _nsTextField2.setSelectable(false);
        _nsTextField2.setEnabled(true);
        _nsTextField2.setBorder(null);
        _setFontForComponent(_nsTextField1, "Lucida Grande", 13, Font.PLAIN);
        _nsTextField1.setEditable(false);
        _nsTextField1.setOpaque(false);
        _nsTextField1.setText("You can do the following process.\n");
        _nsTextField1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        _nsTextField1.setSelectable(false);
        _nsTextField1.setEnabled(true);
        _nsTextField1.setBorder(null);
        if (!(_nsBox0.getLayout() instanceof EOViewLayout)) { _nsBox0.setLayout(new EOViewLayout()); }
        _nsBox1.setSize(397, 1);
        _nsBox1.setLocation(2, 2);
        ((EOViewLayout)_nsBox0.getLayout()).setAutosizingMask(_nsBox1, EOViewLayout.MinYMargin);
        _nsBox0.add(_nsBox1);
        _nsBox0.setBorder(new com.webobjects.eointerface.swing._EODefaultBorder("", true, "Lucida Grande", 13, Font.PLAIN));
        _nsButton0.addActionListener((com.webobjects.eointerface.swing.EOControlActionAdapter)_registered(new com.webobjects.eointerface.swing.EOControlActionAdapter(null, "terminate", _nsButton0), ""));
        _setFontForComponent(_nsButton0, "Lucida Grande", 13, Font.PLAIN);
        _nsButton0.setMargin(new Insets(0, 2, 0, 2));
        _setFontForComponent(_nsTextField0, "Hiragino Kaku Gothic Pro", 18, Font.PLAIN + Font.BOLD);
        _nsTextField0.setEditable(false);
        _nsTextField0.setOpaque(false);
        _nsTextField0.setText("Finishing the OME Configrations");
        _nsTextField0.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        _nsTextField0.setSelectable(false);
        _nsTextField0.setEnabled(true);
        _nsTextField0.setBorder(null);
        if (!(_nsView0.getLayout() instanceof EOViewLayout)) { _nsView0.setLayout(new EOViewLayout()); }
        _nsTextField0.setSize(320, 22);
        _nsTextField0.setLocation(11, 14);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsTextField0, EOViewLayout.MaxXMargin | EOViewLayout.MaxYMargin);
        _nsView0.add(_nsTextField0);
        _nsButton0.setSize(77, 26);
        _nsButton0.setLocation(337, 12);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsButton0, EOViewLayout.MaxXMargin | EOViewLayout.MaxYMargin);
        _nsView0.add(_nsButton0);
        _nsBox0.setSize(401, 5);
        _nsBox0.setLocation(9, 46);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsBox0, EOViewLayout.MinYMargin);
        _nsView0.add(_nsBox0);
        _nsTextField1.setSize(278, 24);
        _nsTextField1.setLocation(7, 57);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsTextField1, EOViewLayout.MaxXMargin | EOViewLayout.MaxYMargin);
        _nsView0.add(_nsTextField1);
        _nsTextField2.setSize(161, 20);
        _nsTextField2.setLocation(41, 97);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsTextField2, EOViewLayout.MaxXMargin | EOViewLayout.MaxYMargin);
        _nsView0.add(_nsTextField2);
        _nsButton1.setSize(111, 26);
        _nsButton1.setLocation(217, 94);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsButton1, EOViewLayout.MaxXMargin | EOViewLayout.MaxYMargin);
        _nsView0.add(_nsButton1);

        if (_replacedObjects.objectForKey("_eoFrame0") == null) {
            _nsView0.setSize(423, 155);
            _eoFrame0.setTitle("Complete the OME Configrations");
            _eoFrame0.setLocation(62, 356);
            _eoFrame0.setSize(423, 155);
        }
    }
}
