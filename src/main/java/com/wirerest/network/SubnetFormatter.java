package com.wirerest.network;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SubnetFormatter implements Formatter<Subnet> {


    @Override
    public Subnet parse(String text, Locale locale) {
        return Subnet.valueOf(text);
    }

    @Override
    public String print(Subnet object, Locale locale) {
        return object.toString();
    }
}
