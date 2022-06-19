package io.vulcan.net.impl;

import io.vulcan.worker.WorkerPool;

public class HttpServer extends SocketServer {

    public HttpServer(int port, WorkerPool pool) {
        super(port, pool);
    }
}
