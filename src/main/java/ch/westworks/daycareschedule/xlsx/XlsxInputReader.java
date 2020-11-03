package ch.westworks.daycareschedule.xlsx;

import ch.westworks.daycareschedule.Input;
import ch.westworks.daycareschedule.model.Child;
import ch.westworks.daycareschedule.model.Day;
import ch.westworks.daycareschedule.model.Family;
import ch.westworks.daycareschedule.model.Group;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class XlsxInputReader {
    public Input readInput(File file) throws IOException {
        final Input input;
        try (var workbooks = WorkbookFactory.create(file)) {
            final Sheet index = workbooks.getSheetAt(0);
            final var groups = getGroups(index);
            final Map<String, Family> families = new HashMap<String, Family>();
            final SortedSet<Day> allDays = new TreeSet<Day>();

            for (Group group : groups) {
                final Sheet groupSheet = workbooks.getSheet(group.getName());
                if (groupSheet != null) processGroup(group, groupSheet, allDays, families);
            }
            groups.removeIf(group -> group.getChildren().isEmpty());

            input = new Input(allDays, groups);
        }
        return input;
    }

    private List<Group> getGroups(Sheet index) {
        final int lastRow = index.getLastRowNum();
        final List<Group> groups = new ArrayList<Group>(lastRow - 1);
        for (int r = 1; r <= lastRow; r++) {
            final Row row = index.getRow(r);
            final Cell groupNameCell = row.getCell(0);
            final String groupName = groupNameCell.getStringCellValue();
            final Cell groupCapacityCell = row.getCell(1);
            final int groupCapacity = Math.toIntExact(Math.round(groupCapacityCell.getNumericCellValue()));
            groups.add(new Group(groupName, groupCapacity));
        }
        return groups;
    }

    private List<Day> getDays(Sheet groupSheet) {
        final Row header = groupSheet.getRow(0);
        final short lastCell = header.getLastCellNum();
        final List<Day> days = new ArrayList<Day>(lastCell - 3);
        for (int c = 2; c < lastCell; c++) {
            final Cell dateCell = header.getCell(c);
            final LocalDate date = dateCell.getLocalDateTimeCellValue().toLocalDate();
            days.add(new Day(date));
        }
        return days;
    }

    private void processGroup(Group group, Sheet groupSheet, SortedSet<Day> allDays, Map<String, Family> families) {
        final List<Day> days = getDays(groupSheet);
        allDays.addAll(days);

        final int lastRow = groupSheet.getLastRowNum();
        for (int r = 1; r <= lastRow; r++) {
            final Row row = groupSheet.getRow(r);
            final Family family = processFamily(families, row);
            final Cell childNameCell = row.getCell(0);
            final String childName = childNameCell.getStringCellValue();
            final Child child = new Child(childName, childName, family);
            group.addChild(child);
            processRequests(child, days, row);
        }
    }

    private Family processFamily(Map<String, Family> families, Row row) {
        final Cell familyCell = row.getCell(1);
        Family family = null;
        if (familyCell != null) {
            final String familyName = familyCell.getStringCellValue();
            if (familyName != null) {
                family = families.computeIfAbsent(familyName, Family::new);
            }
        }
        return family;
    }

    private void processRequests(Child child, List<Day> days, Row row) {
        for (int d = 0; d < days.size(); d++) {
            final Cell requestCell = row.getCell(2 + d);
            final String requestValue = requestCell.getStringCellValue();
            final boolean request = requestValue != null && !requestValue.isBlank();
            if (request) {
                child.addDay(days.get(d));
            }
        }
    }
}