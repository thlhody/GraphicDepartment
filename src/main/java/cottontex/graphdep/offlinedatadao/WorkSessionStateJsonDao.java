package cottontex.graphdep.offlinedatadao;

import com.fasterxml.jackson.core.type.TypeReference;
import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.models.WorkSessionStateOffline;

import java.util.List;
import java.util.Optional;

public class WorkSessionStateJsonDao extends BaseJsonDao<WorkSessionStateOffline> {
    public WorkSessionStateJsonDao() {
        super(JsonPaths.WORK_SESSION_STATE_JSON, new TypeReference<List<WorkSessionStateOffline>>() {});
    }

    @Override
    public Optional<WorkSessionStateOffline> findById(int id) {
        return findAll().stream().filter(wss -> wss.getId() == id).findFirst();
    }

    @Override
    public void update(WorkSessionStateOffline entity) {
        List<WorkSessionStateOffline> workSessionStates = findAll();
        for (int i = 0; i < workSessionStates.size(); i++) {
            if (workSessionStates.get(i).getId() == entity.getId()) {
                workSessionStates.set(i, entity);
                break;
            }
        }
        writeToFile(workSessionStates);
    }

    @Override
    public void delete(int id) {
        List<WorkSessionStateOffline> workSessionStates = findAll();
        workSessionStates.removeIf(wss -> wss.getId() == id);
        writeToFile(workSessionStates);
    }
}