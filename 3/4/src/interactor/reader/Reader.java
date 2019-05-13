package interactor.reader;

import interactor.Interactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.FileSystems;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;

public class Reader extends Interactor {
    private static AsynchronousFileChannel asynchronousSourceFileChannel;
    private boolean shouldRead = false;
    private static Integer numberOfCharactersRead = 0;

    public static AsynchronousFileChannel getAsynchronousSourceFileChannel() {
        return asynchronousSourceFileChannel;
    }

    public Reader(String fileName) {
        super(fileName);
        readers.add(this);
    }

    public static void setAsynchronousSourceFileChannel(String fileName) {
        try {
            asynchronousSourceFileChannel = AsynchronousFileChannel.open(FileSystems.getDefault().
                            getPath(fileName), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.READ);
        } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        while (shouldRead) {
            if (interrupted())
                return;
            synchronized (asynchronousSourceFileChannel) {
                try {
                    while (numberOfCharactersRead >= asynchronousSourceFileChannel.size())
                        asynchronousSourceFileChannel.wait();
                } catch (InterruptedException | IOException e) {}
                ByteBuffer byteBuffer = ByteBuffer.allocate(1);
                synchronized (numberOfCharactersRead) {
                    try {
                        asynchronousSourceFileChannel.read(byteBuffer, numberOfCharactersRead++).get();
                        byteBuffer.flip();
                        asynchronousDestinationFileChannel.write(byteBuffer, asynchronousDestinationFileChannel.size()).get();
                    } catch (InterruptedException | ExecutionException | IOException ignored) {}
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
    }

    public void setShouldRead(boolean shouldRead) {
        this.shouldRead = shouldRead;
        interrupt();
    }
}
