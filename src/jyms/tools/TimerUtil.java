/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author John
 */
public class TimerUtil {
    
    /**
	 * 定时器内部类:    TimeReminder
         * 函数描述:        安排在指定延迟后执行指定的任务
    */
    public static class TimeReminder {
        Timer timer;
        //MilliSeconds
        public TimeReminder(int MilliSeconds) {
            timer = new Timer();
            timer.schedule(new RemindTask(), MilliSeconds*1000);
        }

        class RemindTask extends TimerTask {
            @Override
            public void run() {
                System.out.println("Time's up!");
                timer.cancel(); //Terminate the timer thread
            }
        }
        //用法
        //public static void main(String args[]) {
        //    System.out.println("About to schedule task.");
        //    new TimeReminder(5);
        //    System.out.println("Task scheduled.");
        //}
    }
}
