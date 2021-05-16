package com.barber;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Person {
    private static final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
    private static final Random rand = new Random();
    private static final Set<String> identifiers = new HashSet<>();

    private final String name;
    private final Boolean wantBeardTrim;
    private final int waitStarted;
    private int waitEnded;

    //factory for person
    private Person(String name, Boolean wantsBeard, int waitStarted){
        this.name = name;
        wantBeardTrim = wantsBeard;
        this.waitStarted = waitStarted;
    }

    public static Person getNewPerson(int currentTime){
        return new Person(randomIdentifier(), Math.random() * 100 < 20, currentTime);
    }

    public String getName() {
        return name;
    }

    public Boolean doesWantBeardTrim() {
        return wantBeardTrim;
    }

    public void setWaitEnded(int waitEnded) {
        this.waitEnded = waitEnded;
    }

    public int getFullWaitTime(){
        return (waitEnded - waitStarted);
    }

    private static String randomIdentifier() {
        StringBuilder builder = new StringBuilder();
        while (builder.toString().length() == 0) {
            int length = rand.nextInt(5) + 5;
            for (int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if (identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }
}
