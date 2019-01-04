/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.ui;

import java.awt.Component;

/**
 *
 * @author John
 */
public class UIUtils {
    static boolean isLeftToRight(Component c)
    {
            return c.getComponentOrientation().isLeftToRight();
    }
}
