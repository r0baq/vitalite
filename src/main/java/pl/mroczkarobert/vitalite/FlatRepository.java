package pl.mroczkarobert.vitalite;

import org.springframework.data.repository.CrudRepository;
import pl.mroczkarobert.vitalite.common.Action;
import pl.mroczkarobert.vitalite.common.Flat;
import pl.mroczkarobert.vitalite.common.Kind;

import java.util.List;

public interface FlatRepository extends CrudRepository<Flat, Long> {
    Flat findFirstByEstateIndexAndKindOrderByIdDesc(String estateIndex, Kind kind);
    Flat findByEstateIndexAndActionAndKind(String estateIndex, Action action, Kind kind);
    List<Flat> findByKindOrderByIdDesc(Kind kind);
}
