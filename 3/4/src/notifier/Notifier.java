package notifier;

import interactor.reader.Reader;

import java.nio.channels.AsynchronousFileChannel;

public class Notifier extends Thread {
    @Override
    public void run() {
        AsynchronousFileChannel asynchronousSourceFileChannel = Reader.getAsynchronousSourceFileChannel();
        synchronized (asynchronousSourceFileChannel) {
            asynchronousSourceFileChannel.notify();
        }
    }
}
