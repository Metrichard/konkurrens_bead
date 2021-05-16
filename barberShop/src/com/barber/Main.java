package com.barber;

public class Main {
    public static void main(String[] args) {
        int oneHourAsMsInProg;
        if(args.length == 2)
            oneHourAsMsInProg = Integer.parseInt(args[1]);
        else
            oneHourAsMsInProg = 400;

        BarberShop barberShop = BarberShop.createBarberShopObject(oneHourAsMsInProg);
        try {
            barberShop.mainProcess();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
