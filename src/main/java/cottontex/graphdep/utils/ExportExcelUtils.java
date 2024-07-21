package cottontex.graphdep.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class ExportExcelUtils {

    public static void exportToExcel(Map<String, Map<Integer, String>> data, int year, int month, String filePath) throws IOException {
        int daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Work Schedule");
            createHeaderRow(sheet, daysInMonth);
            createDataRows(sheet, data, daysInMonth);
            autoSizeColumns(sheet, daysInMonth);

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    private static void createHeaderRow(Sheet sheet, int daysInMonth) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Username");
        for (int day = 1; day <= daysInMonth; day++) {
            headerRow.createCell(day).setCellValue(day);
        }
        headerRow.createCell(daysInMonth + 1).setCellValue("Total");
    }

    private static void createDataRows(Sheet sheet, Map<String, Map<Integer, String>> data, int daysInMonth) {
        int rowNum = 1;
        for (Map.Entry<String, Map<Integer, String>> entry : data.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());

            int totalMinutes = 0;
            for (int day = 1; day <= daysInMonth; day++) {
                String dailyTotal = entry.getValue().getOrDefault(day, "");
                if (dailyTotal != null && !dailyTotal.isEmpty() && !dailyTotal.equals("00:00")) {
                    row.createCell(day).setCellValue(dailyTotal);
                    totalMinutes += DateTimeUtils.calculateMinutes(dailyTotal);
                }
                // If dailyTotal is null, empty, or "00:00", we don't create a cell, leaving it blank
            }

            row.createCell(daysInMonth + 1).setCellValue(DateTimeUtils.formatTotalTime(totalMinutes));
        }
    }

    private static void autoSizeColumns(Sheet sheet, int daysInMonth) {
        for (int i = 0; i <= daysInMonth + 1; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}