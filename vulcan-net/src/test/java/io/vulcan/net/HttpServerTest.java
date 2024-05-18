package io.vulcan.net;

import static org.junit.jupiter.api.Assertions.*;

import io.vulcan.net.impl.HttpServer;
import org.junit.jupiter.api.Test;

class HttpServerTest {

    @Test
    void runServer() {
        HttpServer server = HttpServer.serve(8888);
    }
}