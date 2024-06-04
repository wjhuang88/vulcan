package zone.hwj.vulcan.net;

import io.vertx.junit5.VertxTestContext;
import zone.hwj.vulcan.net.echo.EchoHandler;
import zone.hwj.vulcan.net.impl.SocketServer;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class SocketServerTest {

    private final static int PORT = 8080;

    private static SocketServer closeHandler;

    @BeforeAll
    static void beforeAll() {
        System.out.println("服务端开始启动；" + Thread.currentThread());
        closeHandler = SocketServer.serve(PORT, new EchoHandler());
        System.out.println("服务端启动完成；" + Thread.currentThread());
    }

    @AfterAll
    static void afterAll() throws InterruptedException {
        System.out.println("客户端完成发送数据，开始关闭服务端；" + Thread.currentThread());
        closeHandler.close();
        System.out.println("服务端已关闭；" + Thread.currentThread());
    }

    @Test
    void start() throws Throwable {
        VertxTestContext testContext = new VertxTestContext();

        new Thread(() -> {
            try(Socket socket = new Socket("127.0.0.1", PORT);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                String testStr = "test str 来点中文";
                System.out.println("客户端开始发送数据；" + Thread.currentThread());
                writer.write(testStr);
                writer.newLine();
                writer.flush();

                System.out.println("客户端开始读取数据；" + Thread.currentThread());
                String readStr = reader.readLine();
                System.out.println(readStr + "；" + Thread.currentThread());

                assertEquals(EchoHandler.APEX + testStr, readStr);

                System.out.println("客户端完成读取数据；" + Thread.currentThread());

                testContext.completeNow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Assertions.assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
        if (testContext.failed()) {
            throw testContext.causeOfFailure();
        }
    }
}