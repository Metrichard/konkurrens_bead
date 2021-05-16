package com.barber;

public class Barber implements Runnable {
    private final BarberShop _barberShop;
    private final Boolean _doesBeardTrim;

    //barbershop-ból singletont
    public Barber(Boolean doesBeardTrim, BarberShop barberShop){
        _doesBeardTrim = doesBeardTrim;
        _barberShop = barberShop;
    }

    public void run()
    {
        //lekérdezi a queue-t és blokkol
        while(_barberShop.getSimulatedDays() < _barberShop.getDaysToSimulate()) {
            Person person = _barberShop.GetNextCostumer(this);
            if (person == null) {
                waitUntil(10);
            } else {
                //WriteAction(person);
                int CutTime = (int) (Math.random() * 180) + 20;
                waitUntil(CutTime);
            }
        }
    }

    private void WriteAction(Person person) {
        if (_doesBeardTrim)
            System.out.println("\nNow working on " + person.GetName() + " hair and beard.");
        else
            System.out.println("\nNow working on " + person.GetName() + " hair.");
    }

    private void waitUntil(int time){
        try {
            Thread.sleep(time);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Boolean doesBeardTrim() {
        return _doesBeardTrim;
    }
}
