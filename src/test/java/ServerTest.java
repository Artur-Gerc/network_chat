import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.network_chat.Server;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ServerTest {
    Server server;
    int port;
//    Client client1;
//    Client2 client2;

    @BeforeEach
    public void setUp() throws InterruptedException {
        server = new Server();
        port = Server.loadSetting();
//        client1 = new Client();
//        client2 = new Client2();

        Thread serverThread = new Thread(() -> {
            try {
                Server.main(new String[]{});
            } catch (Exception e) {

            }
        });
        serverThread.start();

        Thread.sleep(500);
    }

    @Test
    public void loadSettingTest() {
        // arrange
        int expected = 8080;
        // act
        int actual = Server.loadSetting();
        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void connectionOneClient() throws IOException, InterruptedException {
        // arrange
        int port = Server.loadSetting();
        String address = "localhost";
        try (Socket socket = new Socket(address, port);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            writer.write("Test user");
            writer.newLine();
            writer.flush();
        }

        // Даём время серверу принять соединение
        Thread.sleep(500);

        int expected = 1;
        // act
        int actual = Server.getClientHandlerList().size();
        // assert
        assertEquals(expected, actual);

    }

    /**
     * Тестирует подключение нескольких клиентов.
     * Создаются несколько сокетов, каждый из которых отправляет имя сразу после подключения.
     * Затем для каждого клиента отправляется команда "exit". После чего список клиентских обработчиков должен оказаться пустым.
     */
    @Test
    public void testMultipleClientConnections() throws Exception {
        final int numClients = 3;
        List<Socket> sockets = new ArrayList<>();

        try {
            // Подключаем несколько клиентов
            for (int i = 0; i < numClients; i++) {
                Socket socket = new Socket("localhost", port);
                sockets.add(socket);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                writer.write("Client" + i);
                writer.newLine();
                writer.flush();
            }

            // Даем время серверу принять все подключения
            Thread.sleep(1500);

            Assertions.assertEquals(numClients, Server.getClientHandlerList().size(),
                    "Должно быть подключено " + numClients + " клиентов");

        } finally {
            // Закрываем все сокеты
            for (Socket socket : sockets) {
                if (!socket.isClosed()) {
                    socket.close();
                }
            }
        }
    }

    @Test
    public void exitClient() throws IOException {
        // arrange
        int clientCount = 3;
        List<Socket> sockets = new ArrayList<>();
        List<BufferedWriter> writers = new ArrayList<>();

        try {
            for (int i = 0; i < clientCount; i++) {
                Socket socket = new Socket("localhost", port);
                sockets.add(socket);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writers.add(writer);

                writer.write("Client " + i);
                writer.newLine();
                writer.flush();
            }

            // Даем время серверу принять все подключения
            Thread.sleep(700);

            for (BufferedWriter w : writers) {
                w.write("exit");
                w.newLine();
                w.flush();
            }

            // Дадим время на обработку отключений
            Thread.sleep(2000);

            int expected = 0;

            //act
            int actual = Server.getClientHandlerList().size();

            // assert
            Assertions.assertEquals(expected, actual);


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            for (Socket s : sockets) {
                if (!s.isClosed()) {
                    s.close();
                }
            }
        }
    }

    @Test
    public void sendMessageTest() {
        try (Socket socket1 = new Socket("localhost", port);
             BufferedReader r1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
             BufferedWriter w1 = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));

             Socket socket2 = new Socket("localhost", port);
             BufferedReader r2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
             BufferedWriter w2 = new BufferedWriter(new OutputStreamWriter(socket2.getOutputStream()))) {

            LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            String w1Name = "Client 1";
            String w2Name = "Client 2";

            w1.write(w1Name);
            w1.newLine();
            w1.flush();

            w2.write(w2Name);
            w2.newLine();
            w2.flush();

            String message = "Hello";

            w1.write(message);
            w1.newLine();
            w1.flush();

            StringBuilder sb = new StringBuilder();
            sb.append("[" + localDateTime + "] " + w1Name + ": " + message);
            String expected = sb.toString();

            String actual = r2.readLine();

            Assertions.assertEquals(expected, actual);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void logMessageTest() throws IOException {
        // arrange
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        String testLogFile = "src/test/resources/testLog.txt";
        Server.logMessage(testLogFile, "Hello");

//        StringBuilder sb = new StringBuilder();
//        sb.append("[" + localDateTime + "] " + ": " + "Hello");
//        String expected = sb.toString();

        String expected = "Hello";
        String actual;
        try (BufferedReader reader = new BufferedReader(new FileReader(testLogFile))) {
            actual = reader.readLine();
            Assertions.assertEquals(expected, actual);
        }
    }
}
