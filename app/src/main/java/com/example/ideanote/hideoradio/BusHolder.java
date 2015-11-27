package com.example.ideanote.hideoradio;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class BusHolder {

    private static Bus instance = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance() {
        return instance;
    }
}
