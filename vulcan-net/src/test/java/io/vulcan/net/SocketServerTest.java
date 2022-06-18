package io.vulcan.net;

import io.vulcan.net.echo.EchoHandler;
import io.vulcan.net.impl.SocketServer;
import io.vulcan.worker.WorkerPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class SocketServerTest {

    private static CloseHandler closeHandler;

    @BeforeAll
    static void beforeAll() {
        int port = 8080;
        System.out.println("服务端开始启动；" + Thread.currentThread());
        closeHandler = new SocketServer(port, WorkerPool.getDefault()).startAsync(new EchoHandler());
        System.out.println("服务端启动完成；" + Thread.currentThread());
    }

    @AfterAll
    static void afterAll() throws InterruptedException {
        System.out.println("客户端完成发送数据，开始关闭服务端；" + Thread.currentThread());
        closeHandler.close();
        System.out.println("服务端已关闭；" + Thread.currentThread());
    }

    @Test
    void start() throws IOException {
        try(Socket socket = new Socket("127.0.0.1", 8080);
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

            assertEquals(testStr, readStr);

            System.out.println("客户端完成读取数据；" + Thread.currentThread());
        }
    }
}