package cottontex.graphdep.controllers.user;

import cottontex.graphdep.controllers.BaseController;
import cottontex.graphdep.database.queries.UserTimeTableHandler;
import cottontex.graphdep.models.WorkHourEntry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.List;

public class UserMonthlyTimeController extends BaseController {

    @FXML private TableView<WorkHourEntry> workHoursTable;
    @FXML private TableColumn<WorkHourEntry, String> dateColumn;
    @FXML private TableColumn<WorkHourEntry, String> startTimeColumn;
    @FXML private TableColumn<WorkHourEntry, Integer> breaksColumn;
    @FXML private TableColumn<WorkHourEntry, String> breaksTimeColumn;
    @FXML private TableColumn<WorkHourEntry, String> endTimeColumn;
    @FXML private TableColumn<WorkHourEntry, String> totalWorkedTimeColumn;


    @Setter
    private Integer userID;
    private UserTimeTableHandler timeTableHandler = new UserTimeTableHandler();

    public void loadUserMonthlyData() {
        if (userID == null) {
            showAlert("Error", "User ID is not set.");
            return;
        }

        List<WorkHourEntry> workHours = timeTableHandler.getUserMonthlyWorkHours(userID);

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        breaksColumn.setCellValueFactory(new PropertyValueFactory<>("breaks"));
        breaksTimeColumn.setCellValueFactory(new PropertyValueFactory<>("breaksTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        totalWorkedTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalWorkedTime"));

        workHoursTable.setItems(FXCollections.observableArrayList(workHours));
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) workHoursTable.getScene().getWindow();
        stage.close();
    }

}