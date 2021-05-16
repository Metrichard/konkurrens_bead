package com.barber;

public class Main {
    public static void main(String[] args) {
        int oneHourAsMsInProg;
        if(args.length == 2)
            oneHourAsMsInProg = Integer.parseInt(args[1]);
        else
            oneHourAsMsInProg = 400;

        BarberShop barberShop = new BarberShop(oneHourAsMsInProg);
        try {
            barberShop.MainProcess();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
