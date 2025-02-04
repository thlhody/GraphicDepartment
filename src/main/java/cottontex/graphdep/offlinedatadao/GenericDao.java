package cottontex.graphdep.offlinedatadao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T> {
    List<T> findAll();
    Optional<T> findById(int id);
    void save(T entity);
    void update(T entity);
    void delete(int id);
}