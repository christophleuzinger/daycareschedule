package ch.westworks.daycareschedule;

import ch.westworks.daycareschedule.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProofOfConcept {
    @Test
    public void test() {
        List<Day> days = IntStream.range(0, 10)
                .mapToObj(d -> new Day(LocalDate.now().plusDays(d)))
                .collect(Collectors.toUnmodifiableList());

        final Family mustermanns = new Family("Mustermann");

        final Group paradiesvoegel = new Group("Paradiesvögel", 5)
                .addChild(new Child("Moritz", "Mustermann", mustermanns)
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(5))
                        .addDay(days.get(6))
                        .addDay(days.get(7))
                )
                .addChild(new Child("PBird2", "PBird2")
                        .addDay(days.get(1))
                        .addDay(days.get(2))
                        .addDay(days.get(3))
                        .addDay(days.get(6))
                        .addDay(days.get(7)))
                .addChild(new Child("PBird3", "PBird3")
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(3))
                        .addDay(days.get(6))
                        .addDay(days.get(8)))
                .addChild(new Child("PBird4", "PBird4")
                        .addDay(days.get(2))
                        .addDay(days.get(4))
                        .addDay(days.get(6))
                        .addDay(days.get(8))
                        .addDay(days.get(9)))
                .addChild(new Child("PBird5", "PBird5")
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(2))
                        .addDay(days.get(4))
                        .addDay(days.get(5)))
                .addChild(new Child("PBird6", "PBird6")
                        .addDay(days.get(0))
                        .addDay(days.get(3))
                        .addDay(days.get(5))
                        .addDay(days.get(7))
                        .addDay(days.get(9)))
                .addChild(new Child("PBird7", "PBird7")
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(4))
                        .addDay(days.get(5))
                        .addDay(days.get(7)))
                .addChild(new Child("PBird8", "PBird8")
                        .addDay(days.get(4))
                        .addDay(days.get(5))
                        .addDay(days.get(6))
                        .addDay(days.get(8))
                        .addDay(days.get(9)))
                .addChild(new Child("PBird9", "PBird9")
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(5))
                        .addDay(days.get(6))
                        .addDay(days.get(7)));


        final Group pandas = new Group("Pandabären", 5)
                .addChild(new Child("Max", "Mustermann", mustermanns)
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(5))
                        .addDay(days.get(6))
                        .addDay(days.get(7))
                )
                .addChild(new Child("Panda2", "Panda2")
                        .addDay(days.get(1))
                        .addDay(days.get(2))
                        .addDay(days.get(3))
                        .addDay(days.get(6))
                        .addDay(days.get(7)))
                .addChild(new Child("Panda3", "Panda3")
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(3))
                        .addDay(days.get(6))
                        .addDay(days.get(8)))
                .addChild(new Child("Panda4", "Panda4")
                        .addDay(days.get(2))
                        .addDay(days.get(4))
                        .addDay(days.get(6))
                        .addDay(days.get(8))
                        .addDay(days.get(9)))
                .addChild(new Child("Panda5", "Panda5")
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(2))
                        .addDay(days.get(4))
                        .addDay(days.get(5)))
                .addChild(new Child("Panda6", "Panda6")
                        .addDay(days.get(0))
                        .addDay(days.get(3))
                        .addDay(days.get(5))
                        .addDay(days.get(7))
                        .addDay(days.get(9)))
                .addChild(new Child("Panda7", "Panda7")
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(4))
                        .addDay(days.get(5))
                        .addDay(days.get(7)))
                .addChild(new Child("Panda8", "Panda8")
                        .addDay(days.get(4))
                        .addDay(days.get(5))
                        .addDay(days.get(6))
                        .addDay(days.get(8))
                        .addDay(days.get(9)))
                .addChild(new Child("Panda9", "Panda9")
                        .addDay(days.get(0))
                        .addDay(days.get(1))
                        .addDay(days.get(5))
                        .addDay(days.get(6))
                        .addDay(days.get(7)));

        final Input input = new Input(new TreeSet<>(days), Arrays.asList(paradiesvoegel, pandas));
        final Solution solution = new Solver(input).solve();
        print(input, solution);
    }

    private void print(Input input, Solution solution) {
        final SortedSet<Day> days = input.getDays();
        System.out.println("|Child   |" + " |".repeat(days.size()));
        input.getGroups().forEach(group ->
                group.getChildren().forEach(
                        child ->
                        {
                            System.out.print("|" + String.format("%-8s", child.getFirstname()));
                            days.forEach(day -> {
                                        boolean hasPlace = solution.hasDay(child, day);
                                        if (hasPlace) {
                                            if (child.getRequestedDays().contains(day)) {
                                                System.out.print("|X");
                                            } else {
                                                System.out.print("|x");
                                            }
                                        } else {
                                            if (child.getRequestedDays().contains(day)) {
                                                System.out.print("|o");
                                            } else {
                                                System.out.print("| ");
                                            }
                                        }
                                    }
                            );
                            System.out.println("|");
                        }
                ));
    }
}
