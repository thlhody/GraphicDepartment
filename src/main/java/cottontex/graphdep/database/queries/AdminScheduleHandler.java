package cottontex.graphdep.database.queries;

import cottontex.graphdep.database.BaseDatabase;
import cottontex.graphdep.utils.LoggerUtility;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.YearMonth;
import java.util.*;



public class AdminScheduleHandler extends BaseDatabase {

    public Map<String, Map<Integer, String>> getMonthlyWorkData(int year, int month) {
        Map<String, Map<Integer, String>> result = new LinkedHashMap<>();
        String sql = "SELECT u.name, " +
                "DAY(wi.first_start_time) AS day_number, " +
                "TIME_FORMAT(SEC_TO_TIME(SUM(TIME_TO_SEC(wi.total_worked_time))), '%H:%i') AS daily_total " +
                "FROM users u " +
                "LEFT JOIN work_interval wi ON u.user_id = wi.user_id " +
                "WHERE MONTH(wi.first_start_time) = ? AND YEAR(wi.first_start_time) = ? " +
                "GROUP BY u.name, DAY(wi.first_start_time) " +
                "ORDER BY u.name, day_number";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int dayNumber = rs.getInt("day_number");
                    String dailyTotal = rs.getString("daily_total");
                    result.computeIfAbsent(name, k -> new HashMap<>()).put(dayNumber, dailyTotal);
                }
            }
        } catch (SQLException e) {
            LoggerUtility.error("Error fetching monthly work data", e);
        }
        return result;
    }

    public String formatMonthlyWorkData(int year, int month) {
        Map<String, Map<Integer, String>> data = getMonthlyWorkData(year, month);
        StringBuilder result = new StringBuilder();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        appendHeader(result, yearMonth, daysInMonth);
        appendDataRows(result, data, daysInMonth);

        return result.toString();
    }

    private void appendHeader(StringBuilder result, YearMonth yearMonth, int daysInMonth) {
        result.append(String.format("Work Schedule for %s %d%n", yearMonth.getMonth(), yearMonth.getYear()));
        result.append("Username/Days |");
        for (int day = 1; day <= daysInMonth; day++) {
            result.append(String.format(" %2d |", day));
        }
        result.append(" Total\n");
        result.append("-".repeat(14 + daysInMonth * 5 + 7)).append("\n");
    }

    private void appendDataRows(StringBuilder result, Map<String, Map<Integer, String>> data, int daysInMonth) {
        for (Map.Entry<String, Map<Integer, String>> entry : data.entrySet()) {
            String username = entry.getKey();
            Map<Integer, String> dailyData = entry.getValue();

            result.append(String.format("%-13s |", username));

            int totalHours = 0;
            for (int day = 1; day <= daysInMonth; day++) {
                String dailyTotal = dailyData.getOrDefault(day, "00:00");
                int hours = Integer.parseInt(dailyTotal.split(":")[0]);
                totalHours += hours;
                result.append(hours > 0 ? String.format(" %2d |", hours) : "    |");
            }

            result.append(String.format(" %3d%n", totalHours));
        }
    }

    public void exportToExcel(int year, int month, String filePath) {
        Map<String, Map<Integer, String>> data = getMonthlyWorkData(year, month);
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Work Schedule");
            createHeaderRow(sheet, daysInMonth);
            createDataRows(sheet, data, daysInMonth);
            autoSizeColumns(sheet, daysInMonth);

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            LoggerUtility.logAndThrow("Error exporting to Excel", e);
        }
    }

    private void createHeaderRow(Sheet sheet, int daysInMonth) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Username");
        for (int day = 1; day <= daysInMonth; day++) {
            headerRow.createCell(day).setCellValue(day);
        }
        headerRow.createCell(daysInMonth + 1).setCellValue("Total");
    }

    private void createDataRows(Sheet sheet, Map<String, Map<Integer, String>> data, int daysInMonth) {
        int rowNum = 1;
        for (Map.Entry<String, Map<Integer, String>> entry : data.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());

            int totalMinutes = 0;
            for (int day = 1; day <= daysInMonth; day++) {
                String dailyTotal = entry.getValue().getOrDefault(day, "00:00");
                row.createCell(day).setCellValue(dailyTotal);
                totalMinutes += calculateMinutes(dailyTotal);
            }

            row.createCell(daysInMonth + 1).setCellValue(formatTotalTime(totalMinutes));
        }
    }

    private int calculateMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private String formatTotalTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    private void autoSizeColumns(Sheet sheet, int daysInMonth) {
        for (int i = 0; i <= daysInMonth + 1; i++) {
            sheet.autoSizeColumn(i);
        }
    }

}