package com.barber;

import java.awt.*;
import java.util.*;

public class RandomDataGenerator {
    private static final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

    private static final java.util.Random rand = new java.util.Random();
    private static final Set<String> identifiers = new HashSet<String>();

    private static String RandomIdentifier() {
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

    public static Person GenerateRandomPerson(int currentTime){
        return new Person(RandomIdentifier(), rand.nextInt(100) < 20, currentTime);
    }
}
