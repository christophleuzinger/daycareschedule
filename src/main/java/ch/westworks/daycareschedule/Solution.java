package ch.westworks.daycareschedule;

import ch.westworks.daycareschedule.model.Child;
import ch.westworks.daycareschedule.model.Day;

import java.util.Map;
import java.util.Set;

public class Solution {
    private final Map<Child, Set<Day>> assignments;

    public Solution(Map<Child, Set<Day>> assignments) {
        this.assignments = assignments;
    }

    public boolean hasDay(Child child, Day day) {
        return assignments.get(child).contains(day);
    }
}
