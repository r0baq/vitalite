package pl.mroczkarobert.vitalite;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FlatRepository extends CrudRepository<Flat, Long> {
    Flat findFirstByEstateIndexAndKindOrderByIdDesc(String estateIndex, Kind kind);
    Flat findByEstateIndexAndActionAndKind(String estateIndex, Action action, Kind kind);
    List<Flat> findByKind(Kind kind);
}
