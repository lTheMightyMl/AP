import java.util.Scanner;

public class P1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int k = scanner.nextInt();
        int nCopy = n, max = 2 * n + m;
        for(int i = m; i <= max; i += 2) {
            String pattern = "";
            for(int j = 0; j < nCopy; j++)
                pattern += ' ';
            for(int j = 0; j < i; j++)
                pattern += '*';
            for(int j = 0; j < nCopy; j++)
                pattern += ' ';
            for(int j = 0; j < k; j++)
                System.out.print(pattern);
            nCopy -= 1;
            System.out.println();
        }
        nCopy = 1;
        max -= 2;
        for(int i = max; i >= m; i -= 2) {
            String pattern = "";
            for(int j = 0; j < nCopy; j++)
                pattern += ' ';
            for(int j = 0; j < i; j++)
                pattern += '*';
            for(int j = 0; j < nCopy; j++)
                pattern += ' ';
            for(int j = 0; j < k; j++)
                System.out.print(pattern);
            nCopy += 1;
            System.out.println();
        }
        return; 
    }
}