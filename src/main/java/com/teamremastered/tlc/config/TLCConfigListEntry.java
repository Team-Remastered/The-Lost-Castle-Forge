package com.teamremastered.tlc.config;

import com.google.common.collect.Lists;
import com.teamremastered.tlc.TheLostCastle;

import java.util.ArrayList;

public class TLCConfigListEntry extends TLCConfigGenericEntry<String> {
    public TLCConfigListEntry(String id, String comment, ArrayList<String> default_value) {
        super(id, comment, getStringFromList(default_value));
    }

    public TLCConfigListEntry(String id, String comment, String default_value) {
        super(id, comment, default_value);
    }

    public ArrayList<String> getList() {
        // Gets the raw string value
        String str = this.getRaw();

        // If the string is too small or isn't formatted properly, return a default value and send an error message
        if (str.length() < 2 || str.charAt(0) != '[' || str.charAt(str.length() - 1) != ']') {
            TheLostCastle.LOGGER.error(String.format("Invalid value for list: %s", str));
            str = this.DEFAULT_VALUE;
        }
        return Lists.newArrayList(str.substring(1, str.length() - 1).split(",\\s*"));
    }

    public void set(ArrayList<String> value) {
        this.set(getStringFromList(value));
    }

    private static String getStringFromList(ArrayList<String> lst) {
        return "[" + String.join(", ", lst) + "]";
    }
}

