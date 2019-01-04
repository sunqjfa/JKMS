/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.synth.SynthTreeUI;
import jyms.tools.TreeUtil.BlackNodeTreeCellRenderer;
import jyms.tools.TreeUtil.CustomNodeTreeCellRenderer;

/**
 *
 * @author John
 */
public class TreeUI extends SynthTreeUI {
    public static ComponentUI createUI(JComponent x) {
        return new TreeUI();
    }
    
    protected void installDefaults() {
        super.installDefaults();
        tree.setCellRenderer(new BlackNodeTreeCellRenderer());
    }
}
