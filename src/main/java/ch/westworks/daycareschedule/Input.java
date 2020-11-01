package ch.westworks.daycareschedule;

import ch.westworks.daycareschedule.model.Day;
import ch.westworks.daycareschedule.model.Group;

import java.util.List;

public class Input {
    private List<Day> days;
    private List<Group> groups;

    public Input(List<Day> days, List<Group> groups) {
        this.days = days;
        this.groups = groups;
    }

    public List<Day> getDays() {
        return days;
    }

    public List<Group>  getGroups() {
        return groups;
    }
}
