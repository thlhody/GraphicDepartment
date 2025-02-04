package cottontex.graphdep.offlinedatadao;

import com.fasterxml.jackson.core.type.TypeReference;
import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.models.UserOffline;

import java.util.List;
import java.util.Optional;

public class UsersJsonDao extends BaseJsonDao<UserOffline> {
    public UsersJsonDao() {
        super(JsonPaths.USERS_JSON, new TypeReference<List<UserOffline>>() {});
    }

    @Override
    public Optional<UserOffline> findById(int id) {
        return findAll().stream().filter(user -> user.getUserId() == id).findFirst();
    }

    @Override
    public void update(UserOffline entity) {
        List<UserOffline> users = findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == entity.getUserId()) {
                users.set(i, entity);
                break;
            }
        }
        writeToFile(users);
    }

    @Override
    public void delete(int id) {
        List<UserOffline> users = findAll();
        users.removeIf(user -> user.getUserId() == id);
        writeToFile(users);
    }
}