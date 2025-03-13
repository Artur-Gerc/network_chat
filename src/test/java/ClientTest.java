import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.network_chat.Client;
import ru.network_chat.Server;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientTest {
    Client client;
    int port;

    @BeforeEach
    public void setUp() {
        client = new Client();
        port = Server.loadSetting();
    }

    @Test
    public void setNameTest() throws IOException {
        // arrange
        String testUserName = "Test name";
        Client.setUserName(testUserName);
        // act
        String actual = Client.getUserName();
        // assert
        Assertions.assertEquals(testUserName, actual);
    }

    @Test
    public void loadSettingTest() {
        // arrange
        int expected = 8080;
        // act
        int actual = Client.loadSetting();
        // assert
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void clientExitTest() throws IOException, InterruptedException {
        // arrange
        Socket mockSocket = Mockito.mock(Socket.class);
        BufferedReader readerMock = Mockito.mock(BufferedReader.class);
        BufferedWriter writerMock = Mockito.mock(BufferedWriter.class);
        Scanner scannerMock = Mockito.mock(Scanner.class);

        Mockito.when(mockSocket.getOutputStream()).thenReturn(Mockito.mock(OutputStream.class));
        Mockito.when(mockSocket.getInputStream()).thenReturn(Mockito.mock(InputStream.class));

        Mockito.when(scannerMock.nextLine()).thenReturn("exit");

        // act
        Client.startClient(writerMock, readerMock, scannerMock);

        // assert
        Mockito.verify(writerMock, Mockito.times(2)).write("exit");
        Assertions.assertTrue(Server.getClientHandlerList().isEmpty());
    }

}

