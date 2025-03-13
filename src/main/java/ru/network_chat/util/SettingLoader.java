package ru.network_chat.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class SettingLoader {

    public static int loadPort(String path){
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String loadedSettings = reader.readLine();
            int index = loadedSettings.indexOf("port=");
            return Integer.parseInt(loadedSettings.substring(index + 5));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
