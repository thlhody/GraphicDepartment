package cottontex.graphdep.models;


import java.time.LocalDate;

public record HolidaySaveResult(boolean success, LocalDate savedDate, String message, boolean duplicate) {

}