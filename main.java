import java.util.concurrent.*;
import java.util.*;

public class ParallelSum {

    // RecursiveTask to compute sum using parallel stuffington
    static class SumTask extends RecursiveTask<Long> {
	// declare vars
        private static final int THRESHOLD = 10_000;
        private final int[] numbers;
        private final int start;
        private final int end;

        public SumTask(int[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += numbers[i];
                }
                return sum;
            } else {
                int mid = (start + end) / 2;
                SumTask leftTask = new SumTask(numbers, start, mid);
                SumTask rightTask = new SumTask(numbers, mid, end);
                leftTask.fork(); // Start left in parallel
                long rightResult = rightTask.compute(); // Compute right directly
                long leftResult = leftTask.join(); // Wait for left to complete
                return leftResult + rightResult;
            }
        }
    }

    public static void main(String[] args) {
        int size = 1_000_000;
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i + 1;
        }

        ForkJoinPool pool = new ForkJoinPool(); // Uses available processors
        SumTask task = new SumTask(numbers, 0, numbers.length);

        long result = pool.invoke(task);

        System.out.println("Total Sum: " + result);
    }
}
