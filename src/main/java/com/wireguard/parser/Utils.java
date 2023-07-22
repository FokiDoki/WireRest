package com.wireguard.parser;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static ArrayList<String> trimAll(List<String> args) {
        ArrayList<String> trimmedArgs = new ArrayList<>();
        for (String arg : args) {
            trimmedArgs.add(arg.trim());
        }
        return trimmedArgs;
    }

}
