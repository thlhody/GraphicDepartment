package cottontex.graphdep.offlinedatadao;

import com.fasterxml.jackson.core.type.TypeReference;
import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.models.WorkIntervalOffline;

import java.util.List;
import java.util.Optional;

public class WorkIntervalJsonDao extends BaseJsonDao<WorkIntervalOffline> {
    public WorkIntervalJsonDao() {
        super(JsonPaths.WORK_INTERVAL_JSON, new TypeReference<List<WorkIntervalOffline>>() {});
    }

    @Override
    public Optional<WorkIntervalOffline> findById(int id) {
        return findAll().stream().filter(wi -> wi.getId() == id).findFirst();
    }

    @Override
    public void update(WorkIntervalOffline entity) {
        List<WorkIntervalOffline> workIntervals = findAll();
        for (int i = 0; i < workIntervals.size(); i++) {
            if (workIntervals.get(i).getId() == entity.getId()) {
                workIntervals.set(i, entity);
                break;
            }
        }
        writeToFile(workIntervals);
    }

    @Override
    public void delete(int id) {
        List<WorkIntervalOffline> workIntervals = findAll();
        workIntervals.removeIf(wi -> wi.getId() == id);
        writeToFile(workIntervals);
    }
}