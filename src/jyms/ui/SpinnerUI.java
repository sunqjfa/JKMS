package jyms.ui;



import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


public class SpinnerUI extends BasicSpinnerUI{
	/** Creates and returns a UI delegate for the specified component
            * @return  
        */
	public static ComponentUI createUI(JComponent c){
		return new SpinnerUI();
	}


	/** Replaces the old editor of the associated JSpinner with the 
	 *  specified new editor
            * @param oldEditor
            * @param newEditor
	 */
        @Override
	protected void replaceEditor(JComponent oldEditor, JComponent newEditor){
		spinner.remove(oldEditor);
		spinner.add(newEditor, "Editor");
	}

}
