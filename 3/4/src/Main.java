import interactor.author.Author;
import interactor.author.authors.NumberAuthor;
import interactor.author.authors.SpecialCharactersAuthor;
import interactor.author.authors.UpperCaseLetterAuthor;
import interactor.reader.Reader;

public class Main {
    private static final int RUNNING_TIME = 30 * 1000;
    private static final String DATAFILE_NAME = "data.txt";

    public static void main(String[] args) {
        Reader.setAsynchronousSourceFileChannel(DATAFILE_NAME);
        Reader[] readers = new Reader[3];
        for (int i = 0; i < readers.length; i++) {
            readers[i] = new Reader((i + 1) + ".txt");
            readers[i].setShouldRead(true);
        }
        Author[] authors = {
                new NumberAuthor(DATAFILE_NAME),
                new SpecialCharactersAuthor(DATAFILE_NAME),
                new UpperCaseLetterAuthor(DATAFILE_NAME)
        };
        for (Author author : authors)
            author.setShouldWrite(true);
        for (Reader reader : readers)
            reader.start();
        for (Author author : authors)
            author.start();
        try {
            Thread.sleep(RUNNING_TIME);
        } catch (InterruptedException ignored) {}
        for (Author author : authors)
            author.setShouldWrite(false);
        for (Reader reader : readers)
            reader.setShouldRead(false);
    }
}
