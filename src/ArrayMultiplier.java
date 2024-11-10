import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class ArrayMultiplier {
    private static final int ARRAY_MIN = -100;
    private static final int ARRAY_MAX = 100;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //Приклад масиву з діапазоном [-100; 100]
        int[] numbers = new int[ARRAY_MAX - ARRAY_MIN + 1];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = ARRAY_MIN + i;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть множник: ");
        int multiplier = scanner.nextInt();

        //розподіл масиву та створення задач
        int numberOfParts = 5;  // Кількість потоків
        ExecutorService executor = Executors.newFixedThreadPool(numberOfParts);
        List<Future<List<Integer>>> futures = new ArrayList<>();
        int partSize = numbers.length / numberOfParts;

        for (int i = 0; i < numberOfParts; i++) {
            int start = i * partSize;
            int end = (i == numberOfParts - 1) ? numbers.length : start + partSize;
            int[] part = new int[end - start];
            System.arraycopy(numbers, start, part, 0, end - start);

            //Відправлення задачі для цієї частини масиву
            futures.add(executor.submit(new MultiplierTask(part, multiplier)));
        }

        //Збір результатів у CopyOnWriteArrayList
        CopyOnWriteArrayList<Integer> resultList = new CopyOnWriteArrayList<>();
        for (Future<List<Integer>> future : futures) {
            resultList.addAll(future.get());
        }

        System.out.println("Результат: " + resultList);
        //Завершення виконання екзек'ютора
        executor.shutdown();
    }

    //Callable задача для множення частини масиву
    public static class MultiplierTask implements Callable<List<Integer>> {
        private final int[] part;
        private final int multiplier;

        public MultiplierTask(int[] part, int multiplier) {
            this.part = part;
            this.multiplier = multiplier;
        }

        @Override
        public List<Integer> call() {
            List<Integer> result = new ArrayList<>();
            for (int value : part) {
                result.add(value * multiplier);
            }
            return result;
        }
    }
}
