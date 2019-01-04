/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.examples.win32.W32API;
import static com.sun.jna.examples.win32.W32API.DEFAULT_OPTIONS;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFOHEADER;


//windows gdi接口,gdi32.dll in system32 folder, 在设置遮挡区域,移动侦测区域等情况下使用
public interface GDI32 extends W32API{
    
    GDI32 INSTANCE = (GDI32) Native.loadLibrary("gdi32", GDI32.class, DEFAULT_OPTIONS);

    public static final int TRANSPARENT = 1;
    public static final int PS_SOLID = 0;///* 画笔画出的是实线  */
    public static final int PS_DASH = 1;/* 画笔画出的是虚线（nWidth必须是1）   */
    public static final int PS_DOT = 2; /* 画笔画出的是点线（nWidth必须是1）   */
    public static final int PS_DASHDOT = 3 ; /* 画笔画出的是点划线（nWidth必须是1）  */
    public static final int PS_DASHDOTDOT = 4;/* 画笔画出的是点-点-划线（nWidth必须是1） */
    
    public static class Size extends Structure {
	public static Size Empty = new Size();
        public int Height ;//获取或设置此 Size 结构的垂直分量。
        public boolean  IsEmpty ;//测试此 Size 结构的宽度和高度是否为 0。
        public int  Width ;//获取或设置此 Size 结构的水平分量。

    }
    public int SetBkMode(HDC hdc, int i);

    public HANDLE CreateSolidBrush(int icolor);//刷子是用来填充颜色的，不是用来画东西的，不是画笔
    /**
	 * 函数:      CreatePen
         * 函数描述:  用指定的样式、宽度和颜色创建一个画笔 
         *            注解： 一旦不再需要画笔，记得用DeleteObject函数将其删除
         * 参数：       @param nPenStyle ，指定画笔样式，可以是下述常数之一 
         *                              PS_SOLID 画笔画出的是实线
         *                              PS_DASH 画笔画出的是虚线（nWidth必须是1） 
         *                              PS_DOT 画笔画出的是点线（nWidth必须是1） 
         *                              PS_DASHDOT 画笔画出的是点划线（nWidth必须是1） 
         *                              PS_DASHDOTDOT 画笔画出的是点-点-划线（nWidth必须是1） 
         *                              PS_NULL 画笔不能画图 
         *                              PS_INSIDEFRAME 画笔在由椭圆、矩形、圆角矩形、饼图以及弦等生成的封闭对象框中画图。如指定的准确RGB颜色不存在，就进行抖动处理
     * @param nPenStyle
         *              @param nWidth 以逻辑单位表示的画笔的宽度 
         *              @param crColor 画笔的RGB颜色 
         * @return int 如函数执行成功，就返回指向新画笔的一个句柄；否则返回零 
    */
    public HANDLE CreatePen(int nPenStyle, int nWidth, int crColor);
    public HANDLE SelectObject(HDC hdc, HANDLE hObject);
    public int DeleteObject(HANDLE hObject);//Long，非零表示成功，零表示失败
    public HBITMAP CreateDIBitmap(HDC hdc, BITMAPINFOHEADER b, int i, BITMAPINFO btmpnf, int i1);
    public int SetTextColor(HDC hdc, int Color);//设置颜色，成功返回文本色的前一个RGB颜色设定，如0x0000FF、0xFF0000、0x00FF00
    public boolean TextOut(
                    HDC hdc, // 设备描述表句柄
                    int nXStart, // 字符串的开始位置 x坐标
                    int nYStart, // 字符串的开始位置 y坐标
                    String lpString, // 字符串
                    int cbString //字串中要描绘的字符数量
                    );
    /**
	 * 函数:      GetTextExtentPoint32
         * 函数描述:  判断一个字串的大小（范围）。在Win32环境中，最好使用GetTextExtentPoint32，它提供了更精确的计算结果
         *            注解：这个函数不会将剪切区考虑在内，但却考虑到了由SetTextCharacterExtra函数设置的任何额外空间（间距）
         * 参数：     @param hDc ，指向一个设备场景的句柄 
     * @param hDc
         *             @param lpsz String，欲度量其范围（extent）的一个字串 
         *             @param cbString int，lpsz字串的长度 
         *             @param lpSize SIZE，这个结构用于装载字串范围的高度和宽度信息 
         * @return int 非零表示成功，零表示失败 。会设置GetLastError 
    */
    public int GetTextExtentPoint32  (HDC hDc, String lpsz, int cbString, Pointer lpSize);
    public int Rectangle(HDC hdc, int X1, int Y1, int X2, int Y2);//用当前选定的画笔描绘矩形，并用当前选定的刷子进行填充
    /**
	 * 函数:      LineTo
         * 函数描述:  用当前画笔画一条线，从当前位置连到一个指定的点。这个函数调用完毕，当前位置变成x,y点
         *            注解： 如重复调用这个函数和一个几何画笔，从而创建一系列线段，那么除非在一个路径的场景中调用，否则不会认为这些线段已结合到一起
         * 参数：     @param hdc ，指向一个设备场景的句柄 
     * @param hdc
         *             @param x,y int，采用逻辑坐标表示的新画笔位置
     * @param y
         * @return int 非零表示成功，零表示失败 
    */
    public int LineTo (HDC hdc, int x, int y);//用当前画笔画一条线，从当前位置连到一个指定的点。这个函数调用完毕，当前位置变成x,y点
    /**
	 * 函数:      MoveToEx
         * 函数描述:  为指定的设备场景指定一个新的当前画笔位置。前一个位置保存在point中 
         *            注解：    在一个路径分支中描绘的时候，这个函数会创建一个新的子路径
         * 参数：     @param hdc ，指向一个设备场景的句柄 
     * @param hdc
         *             @param x,y int，采用逻辑坐标表示的新画笔位置
     * @param y 
     * @param point 
         * @return int 非零表示成功，零表示失败 
    */
    public int MoveToEx(HDC hdc, int x, int y, POINT point);
    
    /**
        创建由多个多边形构成的区域。每个多边形都应是封闭的 
        返回值 
        Long，执行成功则为创建区域的句柄，失败则为零 
        参数表 
        参数 类型及说明 
        lpPoint POINTAPI，nCount个POINTAPI结构中的第一个POINTAPI结构 
        lpPolyCounts Long，长整数阵列的第一个入口。每个入口包含构成一个封闭多边形的点数。lpPoint阵列组成了一系列多边形，每个多边形在lpPolyCounts中有一个入口 
        nCount Long，多边形的点数 
        nPolyFillMode Long，描述多边形填充模式。可为ALTERNATE 或 WINDING常数。参考SetPolyFillMode函数对多边形填充模式的解释 
        注解 
        不用时一定要用DeleteObject函数删除该区域
     * @param points
     * @param lpPolyCounts
     * @param nCount
     * @param nPolyFillMode
     * @return 
     */
    public HRGN CreatePolyPolygonRgn(POINT[] points, int[] lpPolyCounts , int nCount , int nPolyFillMode);
   
    /**
	 * 函数:      CreatePolygonRgn
         * 函数描述:  创建一个由一系列点围成的区域。windows在需要时自动将最后点与第一点相连以封闭多边形 
         *            注解：    不用时一定要用DeleteObject函数删除该区域
         * 参数：     @param points，nCount个POINTAPI结构中的第一个POINTAPI结构 
         *             @param points
         *             @param nCount             多边形的点数 
         *             @param nPolyFillMode      描述多边形填充模式。可为ALTERNATE 或 WINDING常数。参考SetPolyFillMode函数对多边形填充模式的解释 
         * @return 执行成功为创建的区域句柄，失败则为0 
    */
    public HRGN CreatePolygonRgn(POINT[] points, int nCount , int nPolyFillMode);
    public HANDLE CreatePatternBrush(HANDLE hbmp);

}