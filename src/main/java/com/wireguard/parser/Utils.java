package com.wireguard.parser;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final String BETWEEN_DATA_SPLITTER = "\t";
    private static final String BASE64_REGEX = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";

    public static ArrayList<String> trimAll(List<String> args){
        ArrayList<String> trimmedArgs = new ArrayList<>();
        for (String arg : args) {
            trimmedArgs.add(arg.trim());
        }
        return trimmedArgs;
    }

    public static List<String> splitLine(String arg){
        return List.of(arg.split(BETWEEN_DATA_SPLITTER));
    }

    public static String replaceOffsWithZero(String arg){
        return arg.replaceAll("off", "0");
    }

    public static Boolean isBase64(String arg){
        return arg.matches(BASE64_REGEX);
    }

    public static void throwIfNotBase64(String arg){
        if(!isBase64(arg)){
            throw new NotABase64Exception(arg);
        }
    }

}
