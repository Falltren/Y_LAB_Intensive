package com.fallt.out;

import java.util.Collection;

public class ConsoleOutput {

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printCollection(Collection<?> collection) {
        collection.forEach(System.out::println);
    }
}

