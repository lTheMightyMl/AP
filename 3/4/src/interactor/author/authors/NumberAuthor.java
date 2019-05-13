package interactor.author.authors;

import interactor.author.Author;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;

public class NumberAuthor extends Author {

    public NumberAuthor(String fileName) {
        super(fileName);
    }

    @Override
    public void run() {
        threadLocalRandom = ThreadLocalRandom.current();
        while (shouldWrite) {
            countCharacters();
            writeToFile(getDigitsByteBuffer(threadLocalRandom.nextInt(100) + 1));
        }
    }

    private ByteBuffer getDigitsByteBuffer(int number) {
        Deque<Byte> digits = new ArrayDeque<>();
        while (number > 0) {
            digits.push((byte) (number % 10));
            number /= 10;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(digits.size());
        for (int i = 0; i < digits.size(); i++) {
            byteBuffer.put((byte) (digits.pop() + '0'));
        }
        return byteBuffer;
    }
}
