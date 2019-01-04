/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.W32API;


//windows user32接口,user32.dll in system32 folder, 在设置遮挡区域,移动侦测区域等情况下使用
public interface USER32 extends W32API
{

    USER32 INSTANCE = (USER32) Native.loadLibrary("user32", USER32.class, DEFAULT_OPTIONS);

    public static final int BF_LEFT = 0x0001;
    public static final int BF_TOP = 0x0002;
    public static final int BF_RIGHT = 0x0004;
    public static final int BF_BOTTOM = 0x0008;
    public static final int BDR_SUNKENOUTER = 0x0002;
    public static final int BF_RECT = (BF_LEFT | BF_TOP | BF_RIGHT | BF_BOTTOM);

    public boolean DrawEdge(HDC hdc, com.sun.jna.examples.win32.GDI32.RECT qrc, int edge, int grfFlags);

    public int FillRect(HDC hDC, com.sun.jna.examples.win32.GDI32.RECT lprc, HANDLE hbr);
    public HANDLE LoadImage(HINSTANCE hnstnc, String filename, int i, int i1, int i2, int i3);
    public int DrawText (HDC hdc, String text, int nCount, Pointer Rect, int wFormat);// 设备描述表句柄,将要绘制的字符串 ,字符串的长度 ,指向矩形结构RECT的指针 , 正文的绘制选项 
    
}
