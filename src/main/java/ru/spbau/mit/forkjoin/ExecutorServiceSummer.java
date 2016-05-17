package ru.spbau.mit.forkjoin;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Egor Gorbunov
 * @since 17.05.16
 */
public class ExecutorServiceSummer {
    private static ExecutorService es = Executors.newCachedThreadPool();

    public static String md5Sum(Path path) throws IOException, ExecutionException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        if (Files.isDirectory(path)) {
            List<Future<String>> fs = new ArrayList<>();
            List<Path> paths = Files.list(path).collect(Collectors.toList());
            for (Path p : paths) {
                fs.add(es.submit(() -> {
                    try {
                        return md5Sum(p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
            sb.append(path.toString());
            for (Future<String> f : fs) {
                sb.append(f.get());
            }
            return sb.toString();
        } else {
            InputStream is = new BufferedInputStream(Files.newInputStream(path));
            return DigestUtils.md5Hex(is);
        }
    }
}

