package io.vulcan.net.impl;

import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.vulcan.net.CloseHandler;
import io.vulcan.worker.WorkerPool;

public class HttpServer extends SocketServer {

    public HttpServer(int port) {
        this(port, WorkerPool.getDefault());
    }

    public HttpServer(int port, WorkerPool pool) {
        super(port, pool);
    }

    public CloseHandler start() {
        return super.start(new HttpServerCodec(), new HttpServerExpectContinueHandler(), new HttpServerHandler());
    }

    public static void main(String[] args) throws InterruptedException {
        HttpServer server = new HttpServer(8888);
        CloseHandler start = server.start();
    }
}
