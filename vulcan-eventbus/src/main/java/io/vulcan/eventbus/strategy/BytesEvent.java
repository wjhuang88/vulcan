package io.vulcan.eventbus.strategy;

public class BytesEvent {
    private byte[] data;
    private String router;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }
}
