package ru.spbau.mit.forkjoin;


import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Egor Gorbunov
 * @since 17.05.16
 */
public class SingleThreadSummer {
    public static String md5Sum(Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
        md5sumRec(path, sb);
        return sb.toString();
    }

    private static void md5sumRec(Path path, StringBuilder sb) throws IOException {
        if (Files.isDirectory(path)) {
            sb.append(path.toString());
            List<Path> paths = Files.list(path).collect(Collectors.toList());
            for (Path p : paths) {
                md5sumRec(p, sb);
            }
        } else {
            InputStream is = new BufferedInputStream(Files.newInputStream(path));
            sb.append(DigestUtils.md5Hex(is));
        }
    }
}
