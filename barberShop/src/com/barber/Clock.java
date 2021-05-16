package com.barber;

import java.util.concurrent.atomic.AtomicBoolean;

public class Clock implements Runnable {
    private final int HOUR;

    private static AtomicBoolean workStarted = new AtomicBoolean();
    private static AtomicBoolean endOfDay = new AtomicBoolean();

    public Clock(int end, int hour){
        HOUR = hour;
        workStarted.set(false);
        endOfDay.set(true);
    }

    @Override
    public void run() {
        try {
            flipEndOfDay();
            wait(HOUR * 9);
            flipDayStarted();
            wait(HOUR*8);
            flipDayStarted();
            wait(HOUR*7);
            flipEndOfDay();
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

    private synchronized void flipEndOfDay(){
        endOfDay.set(!endOfDay.get());
    }

    private synchronized void flipDayStarted() {
        workStarted.set(!workStarted.get());
    }

    public static synchronized Boolean hasDayEnded(){
        return endOfDay.get();
    }

    public static synchronized Boolean hasWorkStarted(){
        return workStarted.get();
    }
}
