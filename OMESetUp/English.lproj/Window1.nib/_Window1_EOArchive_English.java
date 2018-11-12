// _Window1_EOArchive_English.java
// Generated by EnterpriseObjects palette at 2005\u5e747\u670823\u65e5\u571f\u66dc\u65e5 13\u664237\u520628\u79d2Asia/Tokyo

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

public class _Window1_EOArchive_English extends com.webobjects.eoapplication.EOArchive {
    com.webobjects.eointerface.swing.EOFrame _eoFrame0;
    com.webobjects.eointerface.swing.EOTextArea _nsTextView0;
    com.webobjects.eointerface.swing.EOTextField _nsTextField0;
    com.webobjects.eointerface.swing.EOView _nsBox0, _nsBox1;
    javax.swing.JButton _nsButton0, _nsButton1;
    javax.swing.JPanel _nsView0;

    public _Window1_EOArchive_English(Object owner, NSDisposableRegistry registry) {
        super(owner, registry);
    }

    protected void _construct() {
        Object owner = _owner();
        EOArchive._ObjectInstantiationDelegate delegate = (owner instanceof EOArchive._ObjectInstantiationDelegate) ? (EOArchive._ObjectInstantiationDelegate)owner : null;
        Object replacement;

        super._construct();

        _nsButton1 = (javax.swing.JButton)_registered(new javax.swing.JButton("Quit without changing"), "NSButton2");
        _nsBox1 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSView");
        _nsBox0 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSBox");
        _nsTextView0 = (com.webobjects.eointerface.swing.EOTextArea)_registered(new com.webobjects.eointerface.swing.EOTextArea(), "NSTextView");
        _nsTextField0 = (com.webobjects.eointerface.swing.EOTextField)_registered(new com.webobjects.eointerface.swing.EOTextField(), "NSTextField");
        _nsButton0 = (javax.swing.JButton)_registered(new javax.swing.JButton("Next"), "NSButton");

        if ((delegate != null) && ((replacement = delegate.objectForOutletPath(this, "window")) != null)) {
            _eoFrame0 = (replacement == EOArchive._ObjectInstantiationDelegate.NullObject) ? null : (com.webobjects.eointerface.swing.EOFrame)replacement;
            _replacedObjects.setObjectForKey(replacement, "_eoFrame0");
        } else {
            _eoFrame0 = (com.webobjects.eointerface.swing.EOFrame)_registered(new com.webobjects.eointerface.swing.EOFrame(), "Window1");
        }

        _nsView0 = (JPanel)_eoFrame0.getContentPane();
    }

    protected void _awaken() {
        super._awaken();

        if (_replacedObjects.objectForKey("_eoFrame0") == null) {
            _connect(_owner(), _eoFrame0, "window");
        }

        _nsButton0.addActionListener((com.webobjects.eointerface.swing.EOControlActionAdapter)_registered(new com.webobjects.eointerface.swing.EOControlActionAdapter(_owner(), "nextPage", _nsButton0), ""));
    }

    protected void _init() {
        super._init();
        _nsButton1.addActionListener((com.webobjects.eointerface.swing.EOControlActionAdapter)_registered(new com.webobjects.eointerface.swing.EOControlActionAdapter(null, "terminate", _nsButton1), ""));
        _setFontForComponent(_nsButton1, "Lucida Grande", 11, Font.PLAIN);
        _nsButton1.setMargin(new Insets(0, 2, 0, 2));
        if (!(_nsBox0.getLayout() instanceof EOViewLayout)) { _nsBox0.setLayout(new EOViewLayout()); }
        _nsBox1.setSize(527, 1);
        _nsBox1.setLocation(2, 2);
        ((EOViewLayout)_nsBox0.getLayout()).setAutosizingMask(_nsBox1, EOViewLayout.MinYMargin);
        _nsBox0.add(_nsBox1);
        _nsBox0.setBorder(new com.webobjects.eointerface.swing._EODefaultBorder("", true, "Lucida Grande", 13, Font.PLAIN));
        _nsTextView0.setEditable(true);
        _nsTextView0.setOpaque(true);
        _nsTextView0.setText("Let's start to configure OME (Open Mail Environment).\nYou can do this in several ways, but we recommend to use this application first. It is the easiest and surest way.\n\nThis configuration application will make a folder named \"OME_Preferences\" in the Preferences folder in the Library folder. And it will make some configuration files containing the information for sending and receiving mails. You can change the configuration by editing these files by your favorite text editor etc.. The detail of these files are described in the OME manuals.");
        _setFontForComponent(_nsTextView0.textArea(), "Hiragino Kaku Gothic Pro", 12, Font.PLAIN);
        _setFontForComponent(_nsTextField0, "Lucida Grande", 18, Font.PLAIN);
        _nsTextField0.setEditable(false);
        _nsTextField0.setOpaque(false);
        _nsTextField0.setText("About OME Configuration.");
        _nsTextField0.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        _nsTextField0.setSelectable(false);
        _nsTextField0.setEnabled(true);
        _nsTextField0.setBorder(null);
        _setFontForComponent(_nsButton0, "Lucida Grande", 13, Font.PLAIN);
        _nsButton0.setMargin(new Insets(0, 2, 0, 2));
        if (!(_nsView0.getLayout() instanceof EOViewLayout)) { _nsView0.setLayout(new EOViewLayout()); }
        _nsButton0.setSize(77, 26);
        _nsButton0.setLocation(431, 354);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsButton0, EOViewLayout.MinYMargin);
        _nsView0.add(_nsButton0);
        _nsTextField0.setSize(446, 22);
        _nsTextField0.setLocation(14, 14);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsTextField0, EOViewLayout.MinYMargin);
        _nsView0.add(_nsTextField0);
        _nsTextView0.setSize(491, 283);
        _nsTextView0.setLocation(13, 53);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsTextView0, EOViewLayout.MinYMargin);
        _nsView0.add(_nsTextView0);
        _nsBox0.setSize(531, 5);
        _nsBox0.setLocation(-7, 42);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsBox0, EOViewLayout.MinYMargin);
        _nsView0.add(_nsBox0);
        _nsButton1.setSize(153, 22);
        _nsButton1.setLocation(13, 357);
        ((EOViewLayout)_nsView0.getLayout()).setAutosizingMask(_nsButton1, EOViewLayout.MinYMargin);
        _nsView0.add(_nsButton1);

        if (_replacedObjects.objectForKey("_eoFrame0") == null) {
            _nsView0.setSize(517, 390);
            _eoFrame0.setTitle("OME Configuration 1/4");
            _eoFrame0.setLocation(25, 308);
            _eoFrame0.setSize(517, 390);
        }
    }
}