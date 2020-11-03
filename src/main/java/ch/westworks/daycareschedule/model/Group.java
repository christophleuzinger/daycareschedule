package ch.westworks.daycareschedule.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Group {
    private final String name;
    private final List<Child> children = new ArrayList<>();
    private final List<Place> places;

    public Group(String name, int capacity) {
        this.name = name;
        this.places = IntStream.range(0, capacity).mapToObj(i -> new Place()).collect(Collectors.toList());
    }

    public Group addChild(Child child) {
        children.add(child);
        return this;
    }

    public List<Child> getChildren() {
        return children;
    }

    public List<Place> getPlaces() {
        return places.subList(0, Math.min(places.size(), children.size()));
    }

    public String getName() {
        return name;
    }
}
