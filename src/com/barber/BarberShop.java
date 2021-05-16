package com.barber;

import java.util.*;

public class BarberShop {
    //queue kéne, peek..., timeout-os blocking
    private final List<Person> _waitingCostumers = Collections.synchronizedList(new ArrayList<>());
    private final List<Integer> elapsedTimeOfWaiting;
    private final List<Integer> costumersServedEachDay;
    private final int MAX_NUMBER_OF_PPL_IN_WAITING_ROOM = 5;
    private final int SHOP_OPENING_TIME;
    private final int SHOP_CLOSING_TIME;
    private static int DAYS_TO_SIMULATE = 5;
    private final int _oneHourAsMsInProg;
    private int _clock;
    private int _simulatedDays;
    private int _notServedDuringClose;
    private int notServedDuringOpen;

    public BarberShop(int oneHourAsMsInProg){
        notServedDuringOpen = 0;
        _notServedDuringClose = 0;
        _clock = 0;
        _simulatedDays = 0;
        SHOP_OPENING_TIME = oneHourAsMsInProg * 9;
        SHOP_CLOSING_TIME = oneHourAsMsInProg * 17;
        _oneHourAsMsInProg = oneHourAsMsInProg;
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
        while(_simulatedDays < DAYS_TO_SIMULATE){
            int servedToday = 0;
            System.out.println("\nDay " + (_simulatedDays +1) + " has started.");

            servedToday = SimulateDay(servedToday);

            System.out.println("");
            costumersServedEachDay.set(_simulatedDays, servedToday);
            System.out.println("\nDay " + (_simulatedDays +1) + " has ended.");
            Thread.sleep(2000);
            _simulatedDays++;
            _clock = 0;
        }

        System.out.println("\n\n\nResults:\nCostumers served: " + GetAllCostumersServed());
        System.out.println("Costumers not served because barbershop was closed: " + _notServedDuringClose);
        System.out.println("Costumers not served because barbershop was full: " + notServedDuringOpen);
        System.out.println("Average wait time is " + elapsedTimeOfWaiting.stream().mapToDouble(d -> d).average().orElse(0.0));
        System.out.println("Costumers served each day: ");
        for (int i = 0 ; i < DAYS_TO_SIMULATE; i++){
            System.out.println("\tOn day " + (i+1) + ", this many people were served: " + costumersServedEachDay.get(i));
        }
    }

    private int SimulateDay(int servedToday) throws InterruptedException {
        int hoursInDay = 9600;
        while(_clock <= hoursInDay) {
            if ((int)(Math.random() * 100) < 99) {
                Person person = RandomDataGenerator.GenerateRandomPerson(_clock);
                if(SHOP_OPENING_TIME <= _clock && _clock <= SHOP_CLOSING_TIME) {
                    if (!IfPlaceAddPerson(person)) {
                        notServedDuringOpen++;
                    }else{
                        servedToday++;
                    }
                }else{
                    _notServedDuringClose++;
                }
            }
            System.out.print("\rTime: " + _clock);
            Thread.sleep(5);
            _clock += 5;
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
        if(GetFreeSpaces() > 0){
            _waitingCostumers.add(person);
            return true;
        }
        return false;
    }

    private int GetFreeSpaces(){
        return MAX_NUMBER_OF_PPL_IN_WAITING_ROOM - _waitingCostumers.size();
    }

    //ez mehetne a barbe-be
    //inkább blocking queue-nak
    public synchronized Person GetNextCostumer(Barber barber) {
        if(_waitingCostumers.size() == 0)
            return null;

        Person nextOne = _waitingCostumers.get(0);

        if(barber.doesBeardTrim()){
            if(nextOne.doesWantBeardTrim()){
                _waitingCostumers.remove(nextOne);
            }else {
                _waitingCostumers.remove(nextOne);
            }
        }

        if(nextOne.doesWantBeardTrim() && !barber.doesBeardTrim()){
            return null;
        }

        //nullpointer exception cucc
        _waitingCostumers.remove(nextOne);
        nextOne.SetWaitEnded(_clock);
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
        return _simulatedDays;
    }
}
