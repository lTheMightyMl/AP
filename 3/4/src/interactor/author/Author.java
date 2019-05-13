package interactor.author;

import interactor.Interactor;
import notifier.Notifier;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Author extends Interactor {
    private static final int IDLE_TIME = 550;
    private static Integer numberOfCharacters = 0;
    protected ThreadLocalRandom threadLocalRandom;
    protected boolean shouldWrite = false;

    protected Author(String fileName) {
        super(fileName);
    }

    protected static void countCharacters() {
        synchronized (numberOfCharacters) {
            if (numberOfCharacters == 0) {
                try {
                    Thread.sleep(IDLE_TIME);
                } catch (InterruptedException ignored) {}
            }
            numberOfCharacters++;
            numberOfCharacters %= 5;
        }
    }

    public void setShouldWrite(boolean shouldWrite) {
        this.shouldWrite = shouldWrite;
    }

    protected void writeToFile(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        try {
            asynchronousDestinationFileChannel.write(byteBuffer, asynchronousDestinationFileChannel.size()).get();
            new Notifier().start();
        } catch (InterruptedException | ExecutionException | IOException ignored) {}
    }
}
