package ch.westworks.daycareschedule;

import ch.westworks.daycareschedule.model.Day;
import ch.westworks.daycareschedule.model.Group;

import java.util.List;
import java.util.SortedSet;

public class Input {
    private SortedSet<Day> days;
    private List<Group> groups;

    public Input(SortedSet<Day> days, List<Group> groups) {
        this.days = days;
        this.groups = groups;
    }

    public SortedSet<Day> getDays() {
        return days;
    }

    public List<Group>  getGroups() {
        return groups;
    }
}
