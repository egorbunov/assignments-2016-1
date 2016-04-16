package ru.spbau.mit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths.stream().map(
                p -> {
                    try {
                        return Files.lines(Paths.get(p));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(Function.identity()).filter(s -> s.contains(sequence)).collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать,
    // какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        final long n = 500000;
        final double r = 0.5;
        final Random rand = new Random();
        return Stream.generate(() -> new double[]{rand.nextDouble(), rand.nextDouble()}).limit(n)
                .filter(p -> (p[0] - r) * (p[0] - r) + (p[1] - r) * (p[1] - r) < r * r).count() / (double) n;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions.keySet().stream()
                .max(Comparator.comparing(name -> compositions.get(name).stream().mapToInt(String::length).sum()))
                .orElse("");
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders.stream().map(Map::entrySet).flatMap(Set::stream)
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
    }
}
