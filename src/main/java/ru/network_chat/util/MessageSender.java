package ru.network_chat.util;

import java.io.BufferedWriter;
import java.io.IOException;

public class MessageSender {
    private BufferedWriter writer;

    public MessageSender(BufferedWriter writer) {
        this.writer = writer;
    }

    public void sendMessage(String message) throws IOException {
        if(message != null){
            writer.write(message);
            writer.newLine();
            writer.flush();
        }
    }
}

