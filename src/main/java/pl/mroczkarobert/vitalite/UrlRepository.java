package pl.mroczkarobert.vitalite;

import org.springframework.data.repository.CrudRepository;
import pl.mroczkarobert.vitalite.common.Flat;
import pl.mroczkarobert.vitalite.common.Kind;
import pl.mroczkarobert.vitalite.common.Status;
import pl.mroczkarobert.vitalite.common.Url;

import java.util.List;

public interface UrlRepository extends CrudRepository<Url, Long> {
    List<Url> findByStatusAndKind(Status status, Kind kind);
}
