package ru.spbau.mit.forkjoin;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

/**
 * @author Egor Gorbunov
 * @since 17.05.16
 */
public class Bench {
    private static void printResult(String name, String md5, long start, long end) {
        System.out.println(name);
        System.out.println("    md5: " + md5);
        System.out.println("    elapsed time: " + Long.toString(end - start) + " ms");
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args.length != 1) {
            System.out.println("USAGE: Bench path/to/dir");
            System.exit(1);
        }

        Path path = Paths.get(args[0].trim());
        long start;
        long end;
        String md5;

        System.out.println("Directory: " + path.toString());

        start = System.currentTimeMillis();
        md5 = SingleThreadSummer.md5Sum(path);
        end = System.currentTimeMillis();
        printResult(SingleThreadSummer.class.getSimpleName(), md5, start, end);

        start = System.currentTimeMillis();
        md5 = ExecutorServiceSummer.md5Sum(path);
        end = System.currentTimeMillis();
        printResult(ExecutorServiceSummer.class.getSimpleName(), md5, start, end);

        start = System.currentTimeMillis();
        md5 = ForkJoinSummer.md5Sum(path);
        end = System.currentTimeMillis();
        printResult(ForkJoinSummer.class.getSimpleName(), md5, start, end);
    }
}
