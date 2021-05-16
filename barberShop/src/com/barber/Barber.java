package com.barber;

public class Barber implements Runnable {
    private final BarberShop barberShop;
    private final Boolean doesBeardTrim;

    public Barber(Boolean doesBeardTrim){
        this.doesBeardTrim = doesBeardTrim;
        this.barberShop = BarberShop.getBarberShopObject();
    }

    public void run()
    {
        //lekérdezi a queue-t és blokkol
        while(barberShop.getSimulatedDays() < barberShop.getDaysToSimulate()) {
            Person person = barberShop.getNextCostumer(this);
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
        if (doesBeardTrim)
            System.out.println("\nNow working on " + person.getName() + " hair and beard.");
        else
            System.out.println("\nNow working on " + person.getName() + " hair.");
    }

    private void waitUntil(int time){
        try {
            Thread.sleep(time);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Boolean doesBeardTrim() {
        return doesBeardTrim;
    }
}
