package bg.sofia.uni.fmi.mjt.foodanalyzer.server;

import bg.sofia.uni.fmi.mjt.foodanalyzer.Constants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientRequestHandlerTest {

    private static final String WRONG_COMMAND = "alabala";

    @Mock
    private Socket socket;
    @Mock
    private BufferedReader reader;
    @Mock
    private PrintWriter writer;

    private ClientRequestHandler handler;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        handler = new ClientRequestHandler(socket);
    }

    @Test
    public void testWrongCommand() throws IOException {

        when(reader.readLine()).thenReturn(WRONG_COMMAND).thenReturn(null);

        handler.processRequests(reader, writer);

        verify(writer).println(Constants.WRONG_COMMAND_MESSAGE);

    }

}
