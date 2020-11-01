package ch.westworks.daycareschedule.model;

import java.time.LocalDate;

public class Day implements Comparable<Day> {
    private final LocalDate date;

    public Day(LocalDate date) {
        this.date = date;
    }

    @Override
    public int compareTo(Day o) {
        return date.compareTo(o.date);
    }
}
