package cottontex.graphdep.offlinedatadao;

import com.fasterxml.jackson.core.type.TypeReference;
import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.models.TimeProcessingOffline;

import java.util.List;
import java.util.Optional;

public class TimeProcessingJsonDao extends BaseJsonDao<TimeProcessingOffline> {
    public TimeProcessingJsonDao() {
        super(JsonPaths.TIME_PROCESSING_JSON, new TypeReference<List<TimeProcessingOffline>>() {});
    }

    @Override
    public Optional<TimeProcessingOffline> findById(int id) {
        return findAll().stream().filter(tp -> tp.getId() == id).findFirst();
    }

    @Override
    public void update(TimeProcessingOffline entity) {
        List<TimeProcessingOffline> timeProcessings = findAll();
        for (int i = 0; i < timeProcessings.size(); i++) {
            if (timeProcessings.get(i).getId() == entity.getId()) {
                timeProcessings.set(i, entity);
                break;
            }
        }
        writeToFile(timeProcessings);
    }

    @Override
    public void delete(int id) {
        List<TimeProcessingOffline> timeProcessings = findAll();
        timeProcessings.removeIf(tp -> tp.getId() == id);
        writeToFile(timeProcessings);
    }
}