package com.barber;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BarberShop {
    //queue kéne, peek..., timeout-os blocking
    private final BlockingQueue<Person> waitingCostumers;
    private final List<Integer> elapsedTimeOfWaiting;
    private final List<Integer> costumersServedEachDay;
    private final static int MAX_NUMBER_OF_PPL_IN_WAITING_ROOM = 5;
    private final static int DAYS_TO_SIMULATE = 2;
    private final int SHOP_OPENING_TIME;
    private final int SHOP_CLOSING_TIME;
    private final int oneHourAsMsInProg;
    private int clock;
    private int simulatedDays;
    private int notServedDuringClose;
    private int notServedDuringOpen;

    public BarberShop(int oneHourAsMsInProg){
        waitingCostumers = new ArrayBlockingQueue<Person>(MAX_NUMBER_OF_PPL_IN_WAITING_ROOM);
        notServedDuringOpen = 0;
        notServedDuringClose = 0;
        clock = 0;
        simulatedDays = 0;
        SHOP_OPENING_TIME = oneHourAsMsInProg * 9;
        SHOP_CLOSING_TIME = oneHourAsMsInProg * 17;
        this.oneHourAsMsInProg = oneHourAsMsInProg;
        elapsedTimeOfWaiting = new ArrayList<Integer>();
        costumersServedEachDay = InitServedArray();
    }

    private List<Integer> InitServedArray() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i = 0; i < DAYS_TO_SIMULATE; ++i){
            arrayList.add(0);
        }
        return arrayList;
    }

    public void MainProcess() throws InterruptedException
    {
        //executorral
        Thread[] barbers = new Thread[2];
        barbers[0] = new Thread(new Barber(true, this));
        barbers[1] = new Thread(new Barber(false, this));
        barbers[0].start();
        barbers[1].start();

        //clock osztály? signal küldés időközönként
        while(simulatedDays < DAYS_TO_SIMULATE){
            int servedToday = 0;
            System.out.println("\nDay " + (simulatedDays +1) + " has started.");

            servedToday = SimulateDay(servedToday);

            System.out.println("");
            costumersServedEachDay.set(simulatedDays, servedToday);
            System.out.println("\nDay " + (simulatedDays +1) + " has ended.");
            Thread.sleep(1000);
            simulatedDays++;
            clock = 0;
        }

        System.out.println("\n\n\nResults:\nCostumers served: " + GetAllCostumersServed());
        System.out.println("Costumers not served because barbershop was closed: " + notServedDuringClose);
        System.out.println("Costumers not served because barbershop was full: " + notServedDuringOpen);
        System.out.println("Average wait time is " + elapsedTimeOfWaiting.stream().mapToDouble(d -> d).average().orElse(0.0));
        System.out.println("Costumers served each day: ");
        for (int i = 0 ; i < DAYS_TO_SIMULATE; i++){
            System.out.println("\tOn day " + (i+1) + ", this many people were served: " + costumersServedEachDay.get(i));
        }
    }

    private int SimulateDay(int servedToday) throws InterruptedException {
        int hoursInDay = 9600;
        while(clock <= hoursInDay) {
            if ((int)(Math.random() * 100) < 99) {
                Person person = RandomDataGenerator.GenerateRandomPerson(clock);
                if(SHOP_OPENING_TIME <= clock && clock <= SHOP_CLOSING_TIME) {
                    if (!IfPlaceAddPerson(person)) {
                        notServedDuringOpen++;
                    }else{
                        servedToday++;
                    }
                }else{
                    notServedDuringClose++;
                }
            }
            System.out.print("\rTime: " + clock);
            Thread.sleep(oneHourAsMsInProg /12);
            clock += oneHourAsMsInProg /12;
        }
        return servedToday;
    }

    private int GetAllCostumersServed(){
        int sum = 0;
        for(int n : costumersServedEachDay){
            sum += n;
        }
        return sum;
    }

    private Boolean IfPlaceAddPerson(Person person){
        if(waitingCostumers.remainingCapacity() > 0){
            waitingCostumers.add(person);
            return true;
        }
        return false;
    }

    //ez mehetne a barbe-be
    //inkább blocking queue-nak
    public synchronized Person GetNextCostumer(Barber barber) {
        if(waitingCostumers.size() == 0)
            return null;

        Person nextOne = waitingCostumers.peek();

        if(barber.doesBeardTrim()){
            if(nextOne.doesWantBeardTrim()){
                return removeAndGetNextOne();
            }else {
                return removeAndGetNextOne();
            }
        }

        if(nextOne.doesWantBeardTrim() && !barber.doesBeardTrim()){
            return null;
        }

        //nullpointer exception cucc
        return removeAndGetNextOne();
    }

    private Person removeAndGetNextOne() {
        Person nextOne = waitingCostumers.remove();
        nextOne.SetWaitEnded(clock);
        addAverageTimeToList(nextOne);
        return nextOne;
    }

    private void addAverageTimeToList(Person person){
        elapsedTimeOfWaiting.add(person.GetWaitEnded() - person.GetWaitStarted());
    }

    public int getDaysToSimulate(){
        return DAYS_TO_SIMULATE;
    }

    public int getSimulatedDays(){
        return simulatedDays;
    }
}
