package ru.network_chat;

import ru.network_chat.util.MessageSender;
import ru.network_chat.util.SettingLoader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final String SETTINGS = "src/main/resources/settings.txt";
    private static final int PORT = loadSetting();
    private static CopyOnWriteArrayList<ClientHandler> clientHandlerList = new CopyOnWriteArrayList<>();
    private static LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    private static final String LOG_FILE = "src/main/resources/log.txt";

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlerList.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int loadSetting() {
        return SettingLoader.loadPort(SETTINGS);
    }

    public static List<ClientHandler> getClientHandlerList() {
        return clientHandlerList;
    }

    public static void sendMessage(String message, ClientHandler sender) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("[" + localDateTime + "] " + sender.getUserName() + ": " + message);

        logMessage(LOG_FILE, sb.toString());

        for (ClientHandler client : clientHandlerList) {
            if (client != sender) {
                client.send(sb.toString());
            }
        }
    }

    public static void logMessage(String LOG_FILE, String message) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(message);
            writer.newLine();
            writer.flush();
        }
    }
}


class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;
    private MessageSender messageSender;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        userName = reader.readLine();
        messageSender = new MessageSender(writer);
    }

    @Override
    public void run() {
        String text;
        while (true) {
            try {
                text = reader.readLine();
                if (text == null || text.equals("exit")) {
                    messageSender.sendMessage(text);
                    Thread.sleep(1000);
                    Server.sendMessage("Пользователь " + userName + " покинул чат.", this); // Уведомление о выходе
                    downClient();
                    break;
                }

                Server.sendMessage(text, this);
            } catch (IOException | InterruptedException e) {
//                System.err.println("Ошибка при чтении сообщения от клиента: " + e.getMessage());
                Server.getClientHandlerList().remove(this);
                try {
                    downClient();
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            }
        }
    }

    public void downClient() throws IOException, InterruptedException {
        if (!socket.isClosed()) {
            reader.close();
            writer.close();
            socket.close();
        }

        Server.getClientHandlerList().remove(this);
    }

    public void send(String message) throws IOException {
        messageSender.sendMessage(message);
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(socket, that.socket) && Objects.equals(reader, that.reader) && Objects.equals(writer, that.writer) && Objects.equals(userName, that.userName);
    }
}

