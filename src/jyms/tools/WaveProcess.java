/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;

import java.applet.AudioClip;
import java.io.FileNotFoundException;
import java.net.URL;
import javax.swing.JApplet;
import jyms.data.TxtLogger;
import sun.audio.*; 

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


/**
 * 声音处理类
 * @author John
 */
public class WaveProcess {
    
        private static final String sFileName = "--->>WaveProcess.java";
    
        public static class WaveFilePlay implements Runnable {

            private static WaveFilePlay INSTANCE = null ;
            private static boolean ifPlaying = false;//是否正在播放
            private static final int PLAY_SECONDS = 5;//默认报警声音为5秒
            private int iSeconds = PLAY_SECONDS;
            //private static TimeReminder timeReminder;//安排在指定延迟后执行指定的任务的定时器
            //URL Url = this.getClass().getClassLoader().getResource("/jyms/wav/alarm.wav");
            private URL waveUrl;
            private AudioClip audioClipWave;

            //默认情况下获取wave文件
            private WaveFilePlay(){
                try{
                    waveUrl = new File(jyms.CommonParas.SysPath + "wav" + File.separator + "alarm.wav").toURI().toURL();//this.getClass().getResource("/jyms/wav/alarm.wav");
                    audioClipWave = JApplet.newAudioClip(waveUrl);//TxtLogger.append(sFileName, "WaveFilePlay2()",waveUrl.toString()); 
                }catch (Exception e){
                    TxtLogger.append(sFileName, "WaveFilePlay2()","系统在WaveFilePlay构造函数中，出现错误"
                                + "\r\n                       参数waveUrl:" + waveUrl.toString()
                                 + "\r\n                       Exception:" + e.toString());
                }
            }
            private WaveFilePlay(int Seconds){
                this();
                this.iSeconds = Seconds;
            }
            
            //利用文件路径获取wave文件
            private WaveFilePlay(String PathOfWaveFile, int Seconds){
                setParas( PathOfWaveFile, Seconds);
            }
            private WaveFilePlay(String PathOfWaveFile){
                this(PathOfWaveFile, PLAY_SECONDS);
            }
            
            //利用URL获取wave文件
            private WaveFilePlay(URL Url,int Seconds){
                setParas( Url,Seconds);
            }
            private WaveFilePlay(URL Url){ 
                this(Url, PLAY_SECONDS);
            }
            
            //使用实现接口 Runnable 的对象创建一个线程时，启动该线程将导致在独立执行的线程中调用对象的 run 方法。
            @Override
            public void run() {
                
                try {
                    TimeReminder timeReminder = new TimeReminder(iSeconds);
                    audioClipWave.loop();
                    ifPlaying = true;
                    /*AudioClip 的易用性远不如 android 的播放类，这个操作会坚决的阻塞主线程。主线程会被被卡死，声音卡顿，线程停滞
                    下面的语句可以避免这个问题
                    */
                    Thread.sleep(iSeconds * 1000);
                    //System.out.println("Alarm bing end!");
                } catch (Exception e){
                    TxtLogger.append(sFileName, "PlayWaveFile.run()","播放声音文件时，出现错误"
                                    + "\r\n                       参数:" + this.toString()
                                     + "\r\n                       Exception:" + e.toString());
                }

            }

            ////默认情况下获取WaveFilePlay对象
            public static WaveFilePlay getWaveFilePlay() {
                try{
                    
                    if(INSTANCE == null)  {
                        INSTANCE = new WaveFilePlay();
                    }

                }catch (Exception e){
                    TxtLogger.append(sFileName, "getWaveFilePlay()","系统在获得WaveFilePlay对象的过程中，出现错误"
                                 + "\r\n                       Exception:" + e.toString());
                }
                return INSTANCE ;
            }
            
            public static WaveFilePlay getWaveFilePlay(int Seconds) {
                if(INSTANCE == null)  {
                    INSTANCE = new WaveFilePlay( Seconds);
                }else{
                    INSTANCE.iSeconds = Seconds;
                }
                return INSTANCE ;
            }
            
            
            //利用URL获取WaveFilePlay对象
            public static WaveFilePlay getWaveFilePlay(URL Url,int Seconds) {
                try{
                    if(INSTANCE == null)  {
                        INSTANCE = new WaveFilePlay( Url, Seconds);
                    }else{
                        INSTANCE.setParas( Url,Seconds);
                    }
                }catch (Exception e){
                    TxtLogger.append(sFileName, "getWaveFilePlay()","系统在获得WaveFilePlay对象的过程中，出现错误"
                                + "\r\n                       参数Url:" + Url.toString()
                                + "\r\n                       参数Seconds:" + Seconds
                                + "\r\n                       Exception:" + e.toString());
                    INSTANCE = new WaveFilePlay( Seconds);
                }
                return INSTANCE ;
            }
            public static WaveFilePlay getWaveFilePlay(URL Url) {
                return getWaveFilePlay( Url, PLAY_SECONDS) ;
            }
            
            //利用文件路径获取WaveFilePlay对象
            public static WaveFilePlay getWaveFilePlay(String PathOfWaveFile, int Seconds) {
                try{
                    if(INSTANCE == null)  {
                        INSTANCE = new WaveFilePlay( PathOfWaveFile, Seconds);
                    }else{
                        INSTANCE.setParas( PathOfWaveFile, Seconds);
                    }
                }catch (Exception e){
                    TxtLogger.append(sFileName, "getWaveFilePlay()","系统在获得WaveFilePlay对象的过程中，出现错误"
                                + "\r\n                       参数PathOfWaveFile:" + PathOfWaveFile
                                + "\r\n                       参数Seconds:" + Seconds
                                + "\r\n                       Exception:" + e.toString());
                    INSTANCE = new WaveFilePlay( Seconds);
                }
                return INSTANCE ;
            }
            public static WaveFilePlay getWaveFilePlay(String PathOfWaveFile) {
                return getWaveFilePlay( PathOfWaveFile, PLAY_SECONDS) ;
            }


            public void startPlay(){
                if (!ifPlaying) 
                    this.run();
            }
            public void stopPlay(){
                audioClipWave.stop();
                ifPlaying = false;
            }
            public void playOnce(){
                audioClipWave.play();
            }
 
            /**
             * @return the ifPlaying
             */
            public boolean isIfPlaying() {
                return ifPlaying;
            }
            
            @Override
            public String toString(){
                return "jyms.tools.WaveProcess.WaveFilePlay[ Url=" + waveUrl.toString() + "; iSeconds = " + iSeconds +" ]";
            }
            
            
            
            private void setParas(URL Url,int Seconds){
                try{
                    if (ifPlaying) return;
                    iSeconds = Seconds;
                    waveUrl = Url;
                    audioClipWave = JApplet.newAudioClip(Url);
                }catch (Exception e){
                    TxtLogger.append(sFileName, "setParas()","系统在WaveFilePlay构造函数中，出现错误"
                                + "\r\n                       参数waveUrl:" + waveUrl.toString()
                                 + "\r\n                       Exception:" + e.toString());
                }
            }
            private void setParas(URL Url){
                setParas(Url, PLAY_SECONDS);
            }
            
            private void setParas(String PathOfWaveFile, int Seconds){
                try{
                    if (ifPlaying) return;
                    iSeconds = Seconds;
                    waveUrl = new File(PathOfWaveFile).toURI().toURL();//this.getClass().getResource("/jyms/wav/alarm.wav");
                    audioClipWave = JApplet.newAudioClip(waveUrl);//TxtLogger.append(sFileName, "WaveFilePlay2()",waveUrl.toString()); 
                }catch (Exception e){
                    TxtLogger.append(sFileName, "setParas()","系统在WaveFilePlay构造函数中，出现错误"
                                + "\r\n                       参数PathOfWaveFile:" + PathOfWaveFile
                                 + "\r\n                       Exception:" + e.toString());
                }
            }
            private void setParas(String PathOfWaveFile){
                setParas( PathOfWaveFile, PLAY_SECONDS);
            }
            /**
             * 定时器内部类:    TimeReminder
             * 函数描述:        安排在指定延迟后执行指定的任务
            */
            private class TimeReminder {
                    Timer timer;
                    //MilliSeconds
                    public TimeReminder(int Seconds) {
                        timer = new Timer();
                        timer.schedule(new RemindTask(), Seconds*1000);
                    }

                    class RemindTask extends TimerTask {
                        @Override
                        public void run() {
                            //System.out.println("Time's up!");
                            stopPlay();
                            timer.cancel(); //Terminate the timer thread
                            
                        }
                    }
            }

        } 
    
/*----------------------------------------------------------------------------------------------------*/    
        /** 
        * 
        * @author wuhuiwen 
        * 播放音频文件，产生音效 
        */ 
        public class MusicPlay { 
            private AudioStream  as; //单次播放声音用 
            ContinuousAudioDataStream cas;//循环播放声音 
            // 构造函数 
            public MusicPlay(URL url) 
            { 
                try { 
                    //打开一个声音文件流作为输入 
                    as = new AudioStream (url.openStream()); 
                } catch (FileNotFoundException e) { 
                    // TODO Auto-generated catch block 
                    e.printStackTrace(); 
                } catch (IOException e) { 
                    // TODO Auto-generated catch block 
                    e.printStackTrace(); 
                } 
            } 
            // 一次播放 开始 
            public void start() 
            { 
                if( as==null ){ 
                    System.out.println("AudioStream object is not created!"); 
                    return; 
                }else{ 
                    AudioPlayer.player.start (as); 
                } 
            } 
            // 一次播放 停止 
            public void stop() 
            { 
                if( as==null ){ 
                    System.out.println("AudioStream object is not created!"); 
                    return; 
                }else{ 
                    AudioPlayer.player.stop(as); 
                }        
            } 
            // 循环播放 开始 
            public void continuousStart() 
            { 
                // Create AudioData source. 
                AudioData data = null; 
                try { 
                    data = as.getData(); 
                } catch (IOException e) { 
                    // TODO Auto-generated catch block 
                    e.printStackTrace(); 
                } 

                // Create ContinuousAudioDataStream. 
                cas = new ContinuousAudioDataStream (data); 

                // Play audio. 
                AudioPlayer.player.start(cas); 
            } 
            // 循环播放 停止 
            public void continuousStop() 
            { 
                if(cas != null) 
                { 
                    AudioPlayer.player.stop (cas); 
                }    
            } 

        }


        //播放声音的类
        public class PlaySounds extends Thread {

            private String filename;
            public PlaySounds(String wavfile) {
               filename = System.getProperty("user.dir")+wavfile;
            }
            public void run() {

                File soundFile = new File(filename);

                AudioInputStream audioInputStream = null;
                try {
                    audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return;
                }

                AudioFormat format = audioInputStream.getFormat();
                SourceDataLine auline = null;
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                try {
                    auline = (SourceDataLine) AudioSystem.getLine(info);
                    auline.open(format);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                auline.start();
                int nBytesRead = 0;
                //这是缓冲
                byte[] abData = new byte[512];
                try {
                    while (nBytesRead != -1) {
                        nBytesRead = audioInputStream.read(abData, 0, abData.length);
                        if (nBytesRead >= 0)
                        auline.write(abData, 0, nBytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } finally {
                    auline.drain();
                    auline.close();
                }
            } 
        }

}
