/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.Native;
import com.sun.jna.examples.win32.W32API;


public interface Kernel32 extends W32API
{

    Kernel32 INSTANCE = (Kernel32)Native.loadLibrary("Kernel32", Kernel32.class, DEFAULT_OPTIONS);
}