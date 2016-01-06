package com.example.ideanote.hideoradio;

import android.content.Context;

import com.example.ideanote.hideoradio.presentation.internal.di.ApplicationComponent;

public class Injector {
    private static final String INJECTOR_SERVICE = "com.example.ideanote.hideoradio.injector";

    @SuppressWarnings({"ResourceType", "WrongConstant"}) // INJECTOR_SERVICEのWarningを抑制
    public static ApplicationComponent obtain(Context context) {
        return (ApplicationComponent) context.getSystemService(INJECTOR_SERVICE);
    }

    public static boolean matchesService(String name) {
        return INJECTOR_SERVICE.equals(name);
    }

    private Injector() {
        throw new AssertionError("No instance.");
    }
}
