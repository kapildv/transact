package com.algo.transact.home.smart_home.beans;

import java.util.ArrayList;

/**
 * Created by patilsp on 10/14/2017.
 */

public class Room {
    int id;
    String name;
    ArrayList<Peripheral> peripherals =new ArrayList<>();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Peripheral> getPeripherals() {
        return peripherals;
    }
}
