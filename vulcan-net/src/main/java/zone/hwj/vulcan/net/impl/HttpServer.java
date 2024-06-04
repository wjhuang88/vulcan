package zone.hwj.vulcan.net.impl;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import zone.hwj.vulcan.net.CloseHandler;
import zone.hwj.vulcan.worker.WorkerPool;

public class HttpServer implements CloseHandler {

    private final SocketServer socketServer;

    private static final ChannelHandler[] DEFAULT_HANDLERS = {
            new HttpServerCodec(),
            new HttpContentCompressor(),
            new ChunkedWriteHandler(),
            new HttpServerKeepAliveHandler(),
            new HttpServerExpectContinueHandler(),
            new HttpServerHandler()
    };

    private HttpServer(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    @Override
    public ChannelFuture closeAsync() {
        return socketServer.closeAsync();
    }

    public static HttpServer serve(int port, WorkerPool pool) {
        return new HttpServer(SocketServer.serve(port, pool, DEFAULT_HANDLERS));
    }

    public static HttpServer serve(int port) {
        return serve(port, WorkerPool.getDefault());
    }


    public static void main(String[] args) {
        HttpServer server = HttpServer.serve(8888);
    }
}
