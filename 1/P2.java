import java.util.Arrays;
import java.util.Scanner;

public class P2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt(), kSum = 0, index = 0;
        int[] k = new int[n];
        for (int i = 0; i < n; i++) {
            k[i] = scanner.nextInt();
            kSum += k[i];
        }
        int[] numbers = new int[kSum];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k[i]; j++) {
                numbers[index] = scanner.nextInt();
                index += 1;
            }
        Arrays.sort(numbers);
        int firstIndex = 0, lastIndex = 0;
        while (firstIndex < kSum) {
            while (lastIndex < kSum && numbers[firstIndex] == numbers[lastIndex])
                lastIndex++;
            if (lastIndex - firstIndex == n) {
                System.out.print(numbers[firstIndex]);
                System.out.print(' ');
            }
            firstIndex++;
        }
        System.out.println();
        scanner.close();
    }
}