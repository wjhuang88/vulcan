package zone.hwj.vulcan.net;

import zone.hwj.vulcan.net.impl.HttpServer;
import org.junit.jupiter.api.Test;

class HttpServerTest {

    @Test
    void runServer() {
        HttpServer server = HttpServer.serve(8888);
    }
}