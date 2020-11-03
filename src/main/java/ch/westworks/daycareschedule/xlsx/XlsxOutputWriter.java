package ch.westworks.daycareschedule.xlsx;

import ch.westworks.daycareschedule.Input;
import ch.westworks.daycareschedule.Solution;
import ch.westworks.daycareschedule.model.Child;
import ch.westworks.daycareschedule.model.Day;
import ch.westworks.daycareschedule.model.Family;
import ch.westworks.daycareschedule.model.Group;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class XlsxOutputWriter {

    public void writeOutput(File file, Input input, Solution solution) throws IOException {
        final Workbook workbook = new XSSFWorkbook();
        final CreationHelper creationHelper = workbook.getCreationHelper();

        final Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 10);
        final CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.cloneStyleFrom(headerCellStyle);
        final short dateFormat = creationHelper.createDataFormat().getFormat("dd.mm");
        dateCellStyle.setDataFormat(dateFormat);

        final CellStyle assignedAndRequested = workbook.createCellStyle();
        assignedAndRequested.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        assignedAndRequested.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final CellStyle assignedNotRequested = workbook.createCellStyle();
        assignedNotRequested.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        assignedNotRequested.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final CellStyle requestedNotAssigned = workbook.createCellStyle();
        requestedNotAssigned.setFillForegroundColor(IndexedColors.RED1.getIndex());
        requestedNotAssigned.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final List<Day> days = new ArrayList<>(input.getDays());

        final List<Group> groups = input.getGroups();
        for (Group group : groups) {
            final Sheet sheet = workbook.createSheet(group.getName());

            createHeaderRow(sheet, headerCellStyle, dateCellStyle, days);

            final List<Child> children = group.getChildren();
            for (int c = 0; c < children.size(); c++) {
                final Child child = children.get(c);

                final Row childRow = sheet.createRow(c + 1);
                final Cell childCell = childRow.createCell(0);
                childCell.setCellValue(child.getFirstname());

                for (int d = 0; d < days.size(); d++) {
                    final Cell dateCell = childRow.createCell(d + 1);
                    final Day day = days.get(d);
                    boolean requested = child.getRequestedDays().contains(day);
                    boolean assigned = solution.hasDay(child, day);
                    if (assigned) {
                        dateCell.setCellValue("X");
                        if (requested) {
                            dateCell.setCellStyle(assignedAndRequested);
                        } else {
                            dateCell.setCellStyle(assignedNotRequested);
                        }
                    } else if (requested) {
                        dateCell.setCellStyle(requestedNotAssigned);
                    }
                }
            }

            for (int i = 0; i < days.size() + 1; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        write(workbook, file);
    }

    private void createHeaderRow(Sheet sheet, CellStyle headerCellStyle, CellStyle dateCellStyle, List<Day> days) {
        final Row headerRow = sheet.createRow(0);
        final Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Kind");
        headerCell.setCellStyle(headerCellStyle);

        for (int d = 0; d < days.size(); d++) {
            final Cell dateCell = headerRow.createCell(d + 1);
            dateCell.setCellValue(days.get(d).getDate());
            dateCell.setCellStyle(dateCellStyle);
        }
    }

    private void write(Workbook workbook, File file) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}