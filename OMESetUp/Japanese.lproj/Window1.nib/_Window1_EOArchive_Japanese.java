// _Window1_EOArchive_Japanese.java
// Generated by EnterpriseObjects palette at 2005\u5e747\u670823\u65e5\u571f\u66dc\u65e5 13\u664240\u520628\u79d2Asia/Tokyo

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

public class _Window1_EOArchive_Japanese extends com.webobjects.eoapplication.EOArchive {
    com.webobjects.eointerface.swing.EOFrame _eoFrame0;
    com.webobjects.eointerface.swing.EOTextArea _nsTextView0;
    com.webobjects.eointerface.swing.EOTextField _nsTextField0;
    com.webobjects.eointerface.swing.EOView _nsBox0, _nsBox1;
    javax.swing.JButton _nsButton0, _nsButton1;
    javax.swing.JPanel _nsView0;

    public _Window1_EOArchive_Japanese(Object owner, NSDisposableRegistry registry) {
        super(owner, registry);
    }

    protected void _construct() {
        Object owner = _owner();
        EOArchive._ObjectInstantiationDelegate delegate = (owner instanceof EOArchive._ObjectInstantiationDelegate) ? (EOArchive._ObjectInstantiationDelegate)owner : null;
        Object replacement;

        super._construct();

        _nsButton1 = (javax.swing.JButton)_registered(new javax.swing.JButton("\u4f55\u3082\u3057\u306a\u3044\u3067\u7d42\u4e86"), "NSButton2");
        _nsBox1 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSView");
        _nsBox0 = (com.webobjects.eointerface.swing.EOView)_registered(new com.webobjects.eointerface.swing.EOView(), "NSBox");
        _nsTextView0 = (com.webobjects.eointerface.swing.EOTextArea)_registered(new com.webobjects.eointerface.swing.EOTextArea(), "NSTextView");
        _nsTextField0 = (com.webobjects.eointerface.swing.EOTextField)_registered(new com.webobjects.eointerface.swing.EOTextField(), "NSTextField");
        _nsButton0 = (javax.swing.JButton)_registered(new javax.swing.JButton("\u6b21\u3078"), "NSButton");

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
        _nsTextView0.setText("OME\uff08Open Mail Environment\uff09\u306e\u8a2d\u5b9a\u3092\u884c\u3044\u307e\u3059\u3002\n\nOME\u306f\u4ee5\u4e0b\u306b\u7d9a\u304f\u8a2d\u5b9a\u3092\u884c\u3063\u3066\u521d\u3081\u3066\u5229\u7528\u3059\u308b\u3053\u3068\u304c\u3067\u304d\u307e\u3059\u304c\u3001\u8a2d\u5b9a\u3092\u884c\u3046\u65b9\u6cd5\u306f\u3001\u3053\u306e\u30bd\u30d5\u30c8\u3092\u4f7f\u3046\u3060\u3051\u3067\u306f\u3042\u308a\u307e\u305b\u3093\u3002\u3057\u304b\u3057\u306a\u304c\u3089\u3001\u307e\u305a\u6700\u521d\u306e\u8a2d\u5b9a\u3092\u3053\u306e\u30bd\u30d5\u30c8\u3092\u4f7f\u3063\u3066\u884c\u308f\u308c\u308b\u3053\u3068\u3092\u304a\u52e7\u3081\u3057\u307e\u3059\u3002\n\n\u8a2d\u5b9a\u7d50\u679c\u306f\u3001\u73fe\u5728\u306e\u30e6\u30fc\u30b6\u306e\u30db\u30fc\u30e0\u30d5\u30a9\u30eb\u30c0\u306b\u3042\u308bLibrary\u30d5\u30a9\u30eb\u30c0\u306ePreferences\u30d5\u30a9\u30eb\u30c0\u306b\u300cOME_Preferences\u300d\u30d5\u30a9\u30eb\u30c0\u3092\u4f5c\u308a\u3001\u305d\u306e\u4e2d\u306b\u898f\u5b9a\u306e\u30d5\u30a1\u30a4\u30eb\u3092\u4f5c\u6210\u3057\u3066\u884c\u3044\u307e\u3059\u3002\u8a2d\u5b9a\u5f8c\u306e\u7de8\u96c6\u306f\u3001\u305d\u306e\u30d5\u30a1\u30a4\u30eb\u3092\u30a8\u30c7\u30a3\u30bf\u306a\u3069\u3067\u958b\u3044\u3066\u5909\u66f4\u3059\u308b\u306a\u3069\u3057\u3066\u304f\u3060\u3055\u3044\u3002\u5909\u66f4\u65b9\u6cd5\u306f\u30de\u30cb\u30e5\u30a2\u30eb\u3092\u3054\u89a7\u304f\u3060\u3055\u3044\u3002");
        _setFontForComponent(_nsTextView0.textArea(), "Hiragino Kaku Gothic Pro", 12, Font.PLAIN);
        _setFontForComponent(_nsTextField0, "Lucida Grande", 18, Font.PLAIN);
        _nsTextField0.setEditable(false);
        _nsTextField0.setOpaque(false);
        _nsTextField0.setText("OME\u306e\u8a2d\u5b9a\u306b\u3064\u3044\u3066");
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
            _eoFrame0.setTitle("OME\u306e\u8a2d\u5b9a 1/4");
            _eoFrame0.setLocation(25, 308);
            _eoFrame0.setSize(517, 390);
        }
    }
}
