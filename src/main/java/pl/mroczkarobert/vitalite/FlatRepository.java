package pl.mroczkarobert.vitalite;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FlatRepository extends CrudRepository<Flat, Long> {
    Flat findFirstByEstateIndexOrderByIdDesc(Integer estateIndex);
    Flat findByEstateIndexAndAction(Integer estateIndex, Action action);
}
