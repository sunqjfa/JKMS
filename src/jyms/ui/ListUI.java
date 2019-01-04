
package jyms.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;


public class ListUI extends BasicListUI {

    
	public static ComponentUI createUI(JComponent c) {
		return new ListUI();
	}


//	protected void installDefaults(){
//		super.installDefaults();
//                
//	}
}
