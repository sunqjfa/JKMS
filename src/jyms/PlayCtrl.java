/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms;
//播放库函数声明,PlayCtrl.dll

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.win32.StdCallLibrary;
import java.io.File;

public interface PlayCtrl extends StdCallLibrary{
    
    //PlayCtrl INSTANCE = (PlayCtrl) Native.loadLibrary("..\\lib\\PlayCtrl", PlayCtrl.class);
    PlayCtrl INSTANCE = (PlayCtrl) Native.loadLibrary(System.getProperty("user.dir")+File.separator+"lib\\PlayCtrl", PlayCtrl.class);

    public static final int STREAME_REALTIME = 0;
    public static final int STREAME_FILE = 1;

    boolean PlayM4_GetPort(NativeLongByReference nPort);
    boolean PlayM4_OpenStream(NativeLong nPort, ByteByReference pFileHeadBuf, int nSize, int nBufPoolSize);
    boolean PlayM4_InputData(NativeLong nPort, ByteByReference pBuf, int nSize);
    boolean PlayM4_CloseStream(NativeLong nPort);
    boolean PlayM4_SetStreamOpenMode(NativeLong nPort, int nMode);
    boolean PlayM4_Play(NativeLong nPort, W32API.HWND hWnd);
    boolean PlayM4_Stop(NativeLong nPort);
    boolean PlayM4_SetSecretKey(NativeLong nPort, NativeLong lKeyType, String pSecretKey, NativeLong lKeyLen);
    //为了测试新添加的函数
    boolean PlayM4_OpenFile(NativeLong nPort,String sFileName);
    boolean PlayM4_CloseFile(NativeLong nPort);
    boolean PlayM4_Pause(NativeLong nPort, int nPause);
    boolean PlayM4_Slow(NativeLong  nPort);
    boolean PlayM4_Fast(NativeLong  nPort);

} 