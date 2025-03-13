package ru.network_chat;

import ru.network_chat.util.MessageSender;
import ru.network_chat.util.SettingLoader;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientTwo {
    private static final String SETTINGS = "src/main/resources/settings.txt";
    private static String serverAddress = "localhost";
    private static int port;
    private static volatile boolean running = true;
    private static String userName;


    public static void main(String[] args) throws IOException {
        setPort(loadSetting());
        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             Scanner scanner = new Scanner(System.in)) {

            startClient(writer, reader, scanner);
        }
    }


    public static void startClient(BufferedWriter writer, BufferedReader reader, Scanner scanner) throws IOException {
        MessageSender messageSender = new MessageSender(writer);

        System.out.println("Введите имя:");
        setUserName(scanner.nextLine());
        messageSender.sendMessage(userName);

        Thread readThread = new Thread(() -> {
            while (running) {
                try {
                    String messageFromOtherUsers = reader.readLine();
                    if (messageFromOtherUsers == null || messageFromOtherUsers.equals("exit")) {
                        System.out.println("Вы покинули чат. Возвращайтесь к нам еще!");
                        break;
                    }
                    System.out.println(messageFromOtherUsers);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        readThread.start();

        while (true) {
            String userMessage = scanner.nextLine();
            if (userMessage.equals("exit")) {
                running = false; // Останавливаем поток чтения
                messageSender.sendMessage(userMessage);
                break;
            }
            messageSender.sendMessage(userMessage);
        }

    }

    public static int loadSetting() {
        return SettingLoader.loadPort(SETTINGS);
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        ClientTwo.userName = userName;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        ClientTwo.port = port;
    }
}
