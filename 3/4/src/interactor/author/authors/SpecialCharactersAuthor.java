package interactor.author.authors;

import interactor.author.Author;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

public class SpecialCharactersAuthor extends Author {
    private static final char[] specialCharacters = {
            '!', '@', '#', '$', '%', '&', '*'
    };

    public SpecialCharactersAuthor(String fileName) {
        super(fileName);
    }

    @Override
    public void run() {
        threadLocalRandom = ThreadLocalRandom.current();
        while (shouldWrite) {
            countCharacters();
            writeToFile(ByteBuffer.allocate(1).put((byte) specialCharacters[threadLocalRandom.nextInt(specialCharacters.
                    length)]));
        }
    }
}
