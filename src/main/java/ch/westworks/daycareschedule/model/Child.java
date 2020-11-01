package ch.westworks.daycareschedule.model;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class Child {
    private final String firstname;
    private final String lastname;
    private final Set<Day> requestedDays = new TreeSet<>();
    private final Family family;

    public Child(String firstname, String lastname, Family family) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.family = family;
    }


    public Child(String firstname, String lastname) {
        this(firstname, lastname, null);
    }

    public Child addDay(Day day) {
        requestedDays.add(day);
        return this;
    }

    public Set<Day> getRequestedDays() {
        return requestedDays;
    }

    public String getFirstname() {
        return firstname;
    }

    public Optional<Family> getFamily() {
        return Optional.ofNullable(family);
    }
}
