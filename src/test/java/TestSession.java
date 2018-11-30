import org.eclipse.jetty.websocket.api.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

public class TestSession implements Session {
    @Override
    public void close() {

    }

    @Override
    public void close(CloseStatus closeStatus) {

    }

    @Override
    public void close(int i, String s) {

    }

    @Override
    public void disconnect() throws IOException {

    }

    @Override
    public long getIdleTimeout() {
        return 0;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public WebSocketPolicy getPolicy() {
        return null;
    }

    @Override
    public String getProtocolVersion() {
        return null;
    }

    @Override
    public RemoteEndpoint getRemote() {
        return new RemoteEndpoint() {
            @Override
            public void sendBytes(ByteBuffer byteBuffer) throws IOException {

            }

            @Override
            public Future<Void> sendBytesByFuture(ByteBuffer byteBuffer) {
                return null;
            }

            @Override
            public void sendBytes(ByteBuffer byteBuffer, WriteCallback writeCallback) {

            }

            @Override
            public void sendPartialBytes(ByteBuffer byteBuffer, boolean b) throws IOException {

            }

            @Override
            public void sendPartialString(String s, boolean b) throws IOException {

            }

            @Override
            public void sendPing(ByteBuffer byteBuffer) throws IOException {

            }

            @Override
            public void sendPong(ByteBuffer byteBuffer) throws IOException {

            }

            @Override
            public void sendString(String s) throws IOException {
                System.out.println(s);
            }

            @Override
            public Future<Void> sendStringByFuture(String s) {
                return null;
            }

            @Override
            public void sendString(String s, WriteCallback writeCallback) {

            }

            @Override
            public BatchMode getBatchMode() {
                return null;
            }

            @Override
            public void setBatchMode(BatchMode batchMode) {

            }

            @Override
            public InetSocketAddress getInetSocketAddress() {
                return null;
            }

            @Override
            public void flush() throws IOException {

            }
        };
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public UpgradeRequest getUpgradeRequest() {
        return null;
    }

    @Override
    public UpgradeResponse getUpgradeResponse() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public void setIdleTimeout(long l) {

    }

    @Override
    public SuspendToken suspend() {
        return null;
    }
}
