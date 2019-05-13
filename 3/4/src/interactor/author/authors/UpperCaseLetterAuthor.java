package interactor.author.authors;

import interactor.author.Author;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

public class UpperCaseLetterAuthor extends Author {
    public UpperCaseLetterAuthor(String fileName) {
        super(fileName);
    }

    @Override
    public void run() {
        threadLocalRandom = ThreadLocalRandom.current();
        while (shouldWrite) {
            countCharacters();
            writeToFile(ByteBuffer.allocate(1).put((byte) (char) threadLocalRandom.nextInt(65, 91)));
        }
    }
}
