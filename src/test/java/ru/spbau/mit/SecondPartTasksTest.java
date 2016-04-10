package ru.spbau.mit;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SecondPartTasksTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testFindQuotes() throws IOException {
        String pattern = "world";
        List<String> expected = Arrays.asList(
                "Hello world!",
                "Actually, world is a lie too.",
                "Previous line is not blank, but world is.",
                "World, world, world,...",
                "Hello world, again."

        );

        Map<File, List<String>> files = ImmutableMap.<File, List<String>>builder()
                .put(tmpFolder.newFile("a.txt"), Arrays.asList(
                        expected.get(0),
                        "Oh my darling",
                        "Cake is a lie",
                        expected.get(1)
                ))
                .put(tmpFolder.newFile("b.txt"), Arrays.asList(
                        "This line is blank",
                        expected.get(2),
                        "Oh my God",
                        "Oh my Java"
                ))
                .put(tmpFolder.newFile("c.txt"), Collections.emptyList())
                .put(tmpFolder.newFile("d.txt"), Arrays.asList(
                        expected.get(3),
                        "Nothing to do here",
                        expected.get(4)
                ))
                .build();

        for (File f : files.keySet()) {
            Files.write(f.toPath(), files.get(f));
        }

        List<String> actual = SecondPartTasks.findQuotes(
                files.keySet().stream().map(File::getAbsolutePath).sorted().collect(Collectors.toList()), pattern);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPiDividedBy4() {
        Assert.assertEquals(Math.PI / 4, SecondPartTasks.piDividedBy4(), 0.01);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> printers = ImmutableMap.<String, List<String>>builder()
                .put("Pushkin", Arrays.asList("1", "1", "1"))
                .put("Sartre", Arrays.asList("12", "12", "12"))
                .put("Kafka", Arrays.asList("123", "1234"))
                .build();
        Assert.assertEquals("Kafka", SecondPartTasks.findPrinter(printers));
    }

    @Test
    public void testCalculateGlobalOrder() {
        List<Map<String, Integer>> orders = Arrays.asList(
                ImmutableMap.<String, Integer>builder()
                        .put("apple", 1).put("water", 2).put("bible", 3).build(),
                ImmutableMap.<String, Integer>builder()
                        .put("apple", 2).put("water", 3).put("bible", 4).build(),
                ImmutableMap.<String, Integer>builder()
                        .put("apple", 3).put("water", 4).put("bible", 5).build()
        );

        Map<String, Integer> expected = ImmutableMap.<String, Integer>builder()
                .put("apple", 6).put("water", 9).put("bible", 12).build();


        Map<String, Integer> actual = SecondPartTasks.calculateGlobalOrder(orders);

        actual.keySet().stream().forEach(
                s -> assertEquals(expected.get(s), actual.get(s))
        );
    }
}
