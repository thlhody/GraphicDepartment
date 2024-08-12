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
            CellStyle borderedStyle = createBorderedStyle(workbook);
            createHeaderRow(sheet, daysInMonth, borderedStyle);
            createDataRows(sheet, data, daysInMonth, borderedStyle);
            autoSizeColumns(sheet, daysInMonth);

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    private static void createHeaderRow(Sheet sheet, int daysInMonth, CellStyle borderedStyle) {
        Row headerRow = sheet.createRow(0);
        Cell usernameCell = headerRow.createCell(0);
        usernameCell.setCellValue("Nume");
        usernameCell.setCellStyle(borderedStyle);

        for (int day = 1; day <= daysInMonth; day++) {
            Cell dayCell = headerRow.createCell(day);
            dayCell.setCellValue(day);
            dayCell.setCellStyle(borderedStyle);
        }

        Cell totalCell = headerRow.createCell(daysInMonth + 1);
        totalCell.setCellValue("Total");
        totalCell.setCellStyle(borderedStyle);
    }

    private static void createDataRows(Sheet sheet, Map<String, Map<Integer, String>> data, int daysInMonth, CellStyle borderedStyle) {
        int rowNum = 1;
        for (Map.Entry<String, Map<Integer, String>> entry : data.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell nameCell = row.createCell(0);
            nameCell.setCellValue(entry.getKey());
            nameCell.setCellStyle(borderedStyle);

            int totalHours = 0;
            for (int day = 1; day <= daysInMonth; day++) {
                String dailyTotal = entry.getValue().getOrDefault(day, "");
                Cell cell = row.createCell(day);
                cell.setCellStyle(borderedStyle);

                if (dailyTotal != null && !dailyTotal.isEmpty() && !dailyTotal.equals("00:00")) {
                    if (dailyTotal.equals("CM") || dailyTotal.equals("SN") || dailyTotal.equals("CO")) {
                        cell.setCellValue(dailyTotal);
                    } else {
                        try {
                            int roundedHours = DateTimeUtils.roundDownHours(dailyTotal);
                            cell.setCellValue(roundedHours);
                            totalHours += roundedHours;
                        } catch (Exception e) {
                            LoggerUtility.error("Failed to calculate hours from time: " + dailyTotal, e);
                            cell.setCellValue(dailyTotal);  // Fallback to original value
                        }
                    }
                }
                // If dailyTotal is empty, null, or "00:00", we don't set any value, leaving the cell truly empty
            }

            Cell totalCell = row.createCell(daysInMonth + 1);
            totalCell.setCellValue(totalHours);
            totalCell.setCellStyle(borderedStyle);
        }
    }

    private static void autoSizeColumns(Sheet sheet, int daysInMonth) {
        for (int i = 0; i <= daysInMonth + 1; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static CellStyle createBorderedStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }
}