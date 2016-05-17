package ru.spbau.mit.forkjoin;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Egor Gorbunov
 * @since 17.05.16
 */
public class ForkJoinSummer {
    private static ExecutorService es = Executors.newCachedThreadPool();

    private static class SumTask extends RecursiveTask<String> {
        private Path path;
        public SumTask(Path path) {
            this.path = path;
        }

        @Override
        protected String compute() {
            StringBuilder sb = new StringBuilder();
            if (Files.isDirectory(path)) {
                List<SumTask> tasks = new ArrayList<>();
                List<Path> paths;
                try {
                     paths = Files.list(path).collect(Collectors.toList());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (Path p : paths) {
                    SumTask task = new SumTask(p);
                    task.fork();
                    tasks.add(task);
                }
                sb.append(path.toString());
                for (SumTask t : tasks) {
                    sb.append(t.join());
                }
                return sb.toString();
            } else {
                InputStream is;
                try {
                    is = new BufferedInputStream(Files.newInputStream(path));
                    return DigestUtils.md5Hex(is);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static String md5Sum(Path path) {
        return new ForkJoinPool().invoke(new SumTask(path));
    }
}
