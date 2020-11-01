package ch.westworks.daycareschedule;

import ch.westworks.daycareschedule.model.*;
import com.google.ortools.Loader;
import com.google.ortools.sat.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solver {

    private final List<Day> days;
    private final List<Group> groups;
    private final List<Child> children;
    private final List<Place> places;
    private final Set<Family> families;

    public Solver(Input input) {
        days = input.getDays();
        groups = input.getGroups();
        children = groups.stream().flatMap(group -> group.getChildren().stream()).collect(Collectors.toUnmodifiableList());
        places = groups.stream().flatMap(group -> group.getPlaces().stream()).collect(Collectors.toUnmodifiableList());
        families = children.stream().map(Child::getFamily).filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Solution solve() {
        final int numDays = days.size();
        final int numChildren = children.size();
        final int numPlaces = places.size();
        final IntVar[][][] variables = new IntVar[numDays][numChildren][numPlaces];

        Loader.loadNativeLibraries();
        final CpModel model = new CpModel();

        for (int d = 0; d < numDays; d++) {
            for (int c = 0; c < numChildren; c++) {
                for (int p = 0; p < numPlaces; p++) {
                    variables[d][c][p] = model.newBoolVar("day_child_place=" + d + "_" + c + "_" + p);
                }
            }
        }

        // each child gets one place at the most per day
        for (Day day : days) {
            for (Group group : groups) {
                for (Child child : group.getChildren()) {
                    int d = id(day);
                    int c = id(child);
                    model.addLessOrEqual(LinearExpr.sum(variables[d][c]), model.newConstant(1));
                }
            }
        }

        for (Day day : days) {
            int d = id(day);
            for (Group group : groups) {
                for (Place place : group.getPlaces()) {
                    int p = id(place);

                    // each place must be assigned to a child of its group per day
                    final IntVar[] v = group.getChildren().stream().map(child -> {
                        int c = id(child);
                        return variables[d][c][p];
                    }).toArray(IntVar[]::new);
                    model.addEquality(LinearExpr.sum(v), model.newConstant(1));

                    // the group's places may not be assigned to a child from another group
                    final Set<Integer> groupChildIds = group.getChildren().stream().map(this::id).collect(Collectors.toSet());
                    IntVar[] otherChildren = IntStream.range(0, numChildren)
                            .filter(c -> !groupChildIds.contains(c))
                            .mapToObj(c -> variables[d][c][p]).toArray(IntVar[]::new);
                    model.addEquality(LinearExpr.sum(otherChildren), model.newConstant(0));
                }
            }
        }

        for (Group group : groups) {
            final List<Child> groupChildren = group.getChildren();
            final List<Place> groupPlaces = group.getPlaces();
            double avgDaysPerChild = ((double) groupPlaces.size() * days.size()) / groupChildren.size();
            final int minDaysPerChild = (int) Math.floor(avgDaysPerChild);
            final int maxDaysPerChild = (int) Math.ceil(avgDaysPerChild);

            for (Child child : groupChildren) {
                final List<IntVar> assignments = new ArrayList<>(days.size() * groupPlaces.size());
                for (Day day : days) {
                    for (Place place : groupPlaces) {
                        assignments.add(variables[id(day)][id(child)][id(place)]);
                    }
                }
                model.addGreaterOrEqual(LinearExpr.sum(assignments.toArray(IntVar[]::new)), model.newConstant(minDaysPerChild));
                model.addLessOrEqual(LinearExpr.sum(assignments.toArray(IntVar[]::new)), model.newConstant(maxDaysPerChild));
            }
        }

        // Make sure members of the same family are assigned to the same days
        final Map<Family, Set<Child>> familyMembers = children.stream()
                .filter(child -> child.getFamily().isPresent())
                .collect(Collectors.toMap(child -> child.getFamily().get(), Collections::singleton,
                        (c1, c2) -> Stream.concat(c1.stream(), c2.stream()).collect(Collectors.toUnmodifiableSet())));
        final Map<Child, Group> groupMembers = new HashMap<>();
        for (Group group : groups) {
            for (Child child : children) {
                groupMembers.put(child, group);
            }
        }
        for (Day day : days) {
            familyMembers.forEach((family, members) ->
                    {
                        if (members.size() > 1) {
                            final Child[] children = members.toArray(new Child[0]);
                            for (int m = 0; m< children.length-1; m++) {

                                model.addEquality(
                                        LinearExpr.sum(variables[id(day)][id(children[m])]),
                                        LinearExpr.sum(variables[id(day)][id(children[m+1])]));
                            }
                        }
                    }
                    );
        }

        // Add the requests for specific days as a soft constraint
        final CpSolver solver = new CpSolver();
        final List<IntVar> requestObjective = new LinkedList<>();
        for (Day day : days) {
            for (Group group : groups) {
                for (Child child : group.getChildren()) {
                    if (child.getRequestedDays().contains(day)) {
                        for (Place place : group.getPlaces()) {
                            int d = id(day);
                            int c = id(child);
                            int p = id(place);
                            requestObjective.add(variables[d][c][p]);
                        }
                    }
                }
            }
        }

        int[] coeffs = new int[requestObjective.size()];
        Arrays.fill(coeffs, 1);
        model.maximize(LinearExpr.scalProd(requestObjective.toArray(new IntVar[0]), coeffs));

        // Finally, compute the solution
        solver.solve(model);

        System.out.println("Statistics");
        System.out.println("  - Number of requests met = " + ((int)solver.response().getObjectiveValue()) + " out of " + children.stream().flatMap(child -> child.getRequestedDays().stream()).count());
        System.out.println("  - wall time       : " + solver.wallTime() + " s");

        // Construct the solution model
        final Map<Child, Set<Day>> assignments = new HashMap<>();
        for (Child child : children) {
            for (Day day : days) {
                boolean hasPlace = Arrays.stream(variables[id(day)][id(child)])
                        .anyMatch(v -> solver.value(v) == 1);
                if (hasPlace) {
                    final Set<Day> assignedDays = assignments.computeIfAbsent(child, (c) -> new HashSet<>());
                    assignedDays.add(day);
                }
            }
        }
        return new Solution(assignments);
    }

    private int id(Place place) {
        return places.indexOf(place);
    }

    private int id(Child child) {
        return children.indexOf(child);
    }

    private int id(Day day) {
        return days.indexOf(day);
    }
}
