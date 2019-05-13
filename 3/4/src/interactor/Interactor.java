package interactor;

import interactor.reader.Reader;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.FileSystems;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class Interactor extends Thread {
    protected static ArrayList<Reader> readers = new ArrayList<>();
    protected AsynchronousFileChannel asynchronousDestinationFileChannel;

    protected Interactor(String fileName) {
        try {
            asynchronousDestinationFileChannel = AsynchronousFileChannel.open(FileSystems.getDefault().getPath(fileName)
                    , StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException ignored) {}
    }
}
