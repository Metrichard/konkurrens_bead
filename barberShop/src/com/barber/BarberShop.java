package com.barber;

import java.util.*;
import java.util.concurrent.*;

public class BarberShop {
    private final BlockingQueue<Person> waitingCostumers;
    private final List<Integer> elapsedTimeOfWaiting;
    private final List<Integer> costumersServedEachDay;
    private final static int MAX_NUMBER_OF_PPL_IN_WAITING_ROOM = 5;
    private final static int DAYS_TO_SIMULATE = 2;
    private final int SHOP_OPENING_TIME;
    private final int SHOP_CLOSING_TIME;
    private final int REPRESENTATION_OF_AN_HOUR;
    private int clock;
    private int simulatedDays;
    private int notServedDuringClose;
    private int notServedDuringOpen;

    // Singleton declaration
    private static BarberShop barberShop;

    /**
     * Creating a single instance of the barber shop class, making it singleton
     * @param oneHourAsMsInProg The amount of microseconds that represent an hour in the program
     * @return either the already made instance of the BarberShop class or a newly made instance of it
     */
    public static BarberShop createBarberShopObject(int oneHourAsMsInProg){
        if(barberShop == null){
            barberShop = new BarberShop(oneHourAsMsInProg);
        }
        return barberShop;
    }

    /**
     * Gets the one and only instance of this class
     * @return The already made instance of the BarberShop class
     */
    public static BarberShop getBarberShopObject(){
        return barberShop;
    }

    private BarberShop(int oneHourAsMsInProg){
        // Final or static data
        SHOP_OPENING_TIME = oneHourAsMsInProg * 9;
        SHOP_CLOSING_TIME = oneHourAsMsInProg * 17;
        REPRESENTATION_OF_AN_HOUR = oneHourAsMsInProg;
        // Program solving tools
        waitingCostumers = new ArrayBlockingQueue<>(MAX_NUMBER_OF_PPL_IN_WAITING_ROOM);
        clock = 0;
        simulatedDays = 0;
        // Variables for the answers
        notServedDuringOpen = 0;
        notServedDuringClose = 0;
        elapsedTimeOfWaiting = new ArrayList<>();
        costumersServedEachDay = initServedArray();
    }

    private List<Integer> initServedArray() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i = 0; i < DAYS_TO_SIMULATE; ++i){
            arrayList.add(0);
        }
        return arrayList;
    }

    public void mainProcess() throws InterruptedException
    {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.execute(new Barber(true));
        executor.execute(new Barber(false));
        ScheduledThreadPoolExecutor scheduledExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        scheduledExecutor.scheduleWithFixedDelay(new Clock( REPRESENTATION_OF_AN_HOUR*24, REPRESENTATION_OF_AN_HOUR),0,REPRESENTATION_OF_AN_HOUR*24, TimeUnit.MILLISECONDS);// Putting the clock on the wall

        //clock osztály? signal küldés időközönként
        while(simulatedDays < DAYS_TO_SIMULATE){
            int servedToday = 0;
            System.out.println("\nDay " + (simulatedDays +1) + " has started.");

            servedToday = simulateDay(servedToday);

            System.out.println();
            costumersServedEachDay.set(simulatedDays, servedToday);
            System.out.println("\nDay " + (simulatedDays +1) + " has ended.");
            Thread.sleep(1000);
            simulatedDays++;
            clock = 0;
        }
        executor.shutdown();
        writeEndResultStatistics();
    }

    private void writeEndResultStatistics() {
        System.out.println("\n\n\nResults:\nCostumers served: " + getAllCostumersServed());
        System.out.println("Costumers not served because barbershop was closed: " + notServedDuringClose);
        System.out.println("Costumers not served because barbershop was full: " + notServedDuringOpen);
        System.out.println("Average wait time is " + elapsedTimeOfWaiting.stream().mapToDouble(d -> d).average().orElse(0.0));
        System.out.println("Costumers served each day: ");
        for (int i = 0 ; i < DAYS_TO_SIMULATE; i++){
            System.out.println("\tOn day " + (i+1) + ", this many people were served: " + costumersServedEachDay.get(i));
        }
    }

    private int simulateDay(int servedToday) throws InterruptedException {
        int hoursInDay = REPRESENTATION_OF_AN_HOUR * 24;
        while(!Clock.hasDayEnded()) {
            if ((int)(Math.random() * 100) < 99) {
                Person person = Person.getNewPerson(clock);
                if(Clock.hasWorkStarted()) {
                    if (!ifPlaceAddPerson(person)) {
                        notServedDuringOpen++;
                    }else{
                        servedToday++;
                    }
                }else{
                    notServedDuringClose++;
                }
            }

            System.out.print("\rTime: " + clock);
            Thread.sleep(REPRESENTATION_OF_AN_HOUR /8);
            clock += REPRESENTATION_OF_AN_HOUR /8;
        }
        return servedToday;
    }

    private int getAllCostumersServed(){
        int sum = 0;
        for(int n : costumersServedEachDay){
            sum += n;
        }
        return sum;
    }

    private Boolean ifPlaceAddPerson(Person person){
        if(waitingCostumers.remainingCapacity() > 0){
            waitingCostumers.add(person);
            return true;
        }
        return false;
    }

    //ez mehetne a barbe-be
    public synchronized Person getNextCostumer(Barber barber) {
        if(waitingCostumers.isEmpty())
            return null;

        Person nextOne = waitingCostumers.peek();

        if(barber.doesBeardTrim() && nextOne.doesWantBeardTrim()){
            return removeAndGetNextOne();
        }

        if(!barber.doesBeardTrim() && nextOne.doesWantBeardTrim()){
            return null;
        }

        return removeAndGetNextOne();
    }

    private Person removeAndGetNextOne() {
        Person nextOne = waitingCostumers.remove();
        nextOne.setWaitEnded(clock);
        addAverageTimeToList(nextOne);
        return nextOne;
    }

    private void addAverageTimeToList(Person person){
        elapsedTimeOfWaiting.add(person.getFullWaitTime());
    }

    public int getDaysToSimulate(){
        return DAYS_TO_SIMULATE;
    }

    public int getSimulatedDays(){
        return simulatedDays;
    }

    public Boolean isSimulationRunning(){
        return DAYS_TO_SIMULATE != simulatedDays;
    }
}
