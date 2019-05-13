import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class P5 {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        StringProcessor stringProcessor = new StringProcessor(scanner.nextLine());
        while (stringProcessor.process(scanner.nextLine())) {
            stringProcessor.reset(scanner.nextLine());
        }
        scanner.close();
    }
}

class StringProcessor {
    public static final String CONST_REGEX = "(\\d+)";
    private static final int FWORD_LENGTH = 4;
    private static final String[] DIGIT = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
    };
    private ArrayList<String> args;
    private ArrayList<String> operators;
    private boolean isSWord = false;
    private StringBuilder currentString;
    private StringBuilder allStrings = new StringBuilder();
    private StringBuilder endString = new StringBuilder();
    private boolean operated;

    StringProcessor(String keywords) {
        this.reset(keywords);
    }

    void reset(String keywords) {
        this.args = new ArrayList<>();
        this.operators = new ArrayList<>();
        for (String token : keywords.split(",")) {
            String[] tokenSplit = token.split(":");
            this.operators.add(tokenSplit[0].trim().toLowerCase());
            this.args.add(tokenSplit[1].trim().toLowerCase());
        }
    }

    boolean process(String inputString) {
        this.currentString = new StringBuilder(inputString.toLowerCase());
        while (!this.endGame()) {
            this.isSWord = false;
            if (!this.operate()) {
                if (!this.toDigit()) {
                    this.allStrings.append(this.currentString);
                    System.out.println(this.currentString.toString().toLowerCase());
                    return true;
                }
            } else if (this.isSWord) {
                this.allStrings.append(this.currentString.toString().toLowerCase());
                return !this.endGame();
            }
        }
        return false;
    }

    private boolean endGame() {
        if (this.endString.toString().isEmpty())
            return false;
        StringBuilder temp = new StringBuilder(this.allStrings);
        temp.append(this.currentString);
        if (temp.indexOf(this.endString.toString()) != -1) {
            if (!this.isSWord)
                System.out.println(this.currentString.toString().toLowerCase());
            return true;
        }
        return false;
    }

    private boolean operate() {
        this.operated = false;
        this.isSWord = false;
        for (int i = 0; i < this.operators.size(); i++) {
            String operator = this.operators.get(i);
            String arg = this.args.get(i);
            if (operator.equalsIgnoreCase("cFWord"))
                this.cFWord(arg);
            else if (operator.equalsIgnoreCase("sWord"))
                this.sWord(arg);
            else if (operator.equalsIgnoreCase("cpy"))
                this.cpy(arg);
            else if (operator.equalsIgnoreCase("mul"))
                this.mul(arg);
            else if (operator.equalsIgnoreCase("add"))
                this.add(arg);
            if (this.operated)
                return true;
        }
        return false;
    }

    private void cFWord(String keyword) {
        int index = this.currentString.indexOf(keyword);
        this.operated = false;
        if (index != -1) {
            int first = index + keyword.length();
            this.endString = new StringBuilder(this.currentString.substring(first, first + FWORD_LENGTH));
            this.currentString.replace(index, first + FWORD_LENGTH, "");
            this.operated = true;
        }
    }

    private void sWord(String keywords) {
        this.operated = this.isSWord = false;
        String[] keywordsSplit = keywords.split(" ");
        String keyword = keywordsSplit[0].trim().toLowerCase();
        long toFind = Integer.parseInt(keywordsSplit[1].trim());
        StringBuilder temp = new StringBuilder(this.currentString);
        long found = 0;
        int index = temp.indexOf(keyword);
        while (index != -1 && found < toFind) {
            found++;
            temp.replace(index, index + keyword.length(), "");
            index = temp.indexOf(keyword);
        }
        if (found < toFind) {
            this.isSWord = false;
            return;
        }
        this.currentString = temp;
        System.out.println(this.currentString.toString().toLowerCase());
        this.operated = true;
        this.isSWord = true;
    }

    private void cpy(String keyword) {
        this.operated = false;
        int index = this.currentString.indexOf(keyword);
        if (index == -1)
            return;
        this.operated = true;
        int first = index + keyword.length();
        int end = first + keyword.length();
        if (Character.isDigit(this.currentString.charAt(first))) {
            int number = Character.getNumericValue(this.currentString.charAt(first));
            this.currentString.replace(index, first + 1, this.currentString.substring(first + 1, first + 1 + number));
        } else
            this.currentString.replace(index, first, this.currentString.substring(first, end));
    }

    private void mul(String keyword) {
        this.operated = false;
        StringBuilder regex = new StringBuilder(CONST_REGEX);
        regex.append(new StringBuilder(keyword));
        regex.append(CONST_REGEX);
        Pattern pattern = Pattern.compile(regex.toString());
        Matcher matcher = pattern.matcher(this.currentString);
        if (matcher.find()) {
            int a = Integer.valueOf(matcher.group(1)), b = Integer.valueOf(matcher.group(2));
            this.currentString.replace(matcher.start(), matcher.end(), String.valueOf(a * b));
            this.operated = true;
        }
    }

//    this.operated = false;
//    StringBuilder regex = new StringBuilder(CONST_REGEX);
//        regex.append(new StringBuilder(keyword));
//        regex.append(CONST_REGEX);
//    Pattern pattern = Pattern.compile(regex.toString());
//    Matcher matcher = pattern.matcher(this.currentString);
//        if (matcher.find()) {
//        int a = Integer.parseInt(matcher.group(1)), b = Integer.parseInt(matcher.group(2));
//        this.currentString.replace(matcher.start(), matcher.end(), Integer.toString(a * b));
//        this.operated = true;
//    }

    private void add(String keyword) {
        this.operated = false;
        StringBuilder regex = new StringBuilder(CONST_REGEX);
        regex.append(new StringBuilder(keyword));
        regex.append(CONST_REGEX);
        Pattern pattern = Pattern.compile(regex.toString());
        Matcher matcher = pattern.matcher(this.currentString);
        if (matcher.find()) {
            int a = Integer.valueOf(matcher.group(1)), b = Integer.valueOf(matcher.group(2));
            this.currentString.replace(matcher.start(), matcher.end(), String.valueOf(a + b));
            this.operated = true;
        }
    }

    private boolean toDigit() {
        int[] index = new int[20];
        boolean foundOne;
        int first;
        boolean ret = false;
        while (true) {
            first = 0;
            foundOne = false;
            for (int i = 0; i < 10; i++) {
                index[i] = this.currentString.indexOf(DIGIT[i]);
                if (index[i] != -1) {
                    foundOne = true;
                    if (index[first] == -1)
                        first = i;
                    else if (index[i] < index[first])
                        first = i;
                }
            }
            if (!foundOne)
                return ret;
            this.currentString.replace(index[first], index[first] + DIGIT[first].length(), Integer.toString(first));
            ret = true;
        }
    }
}
