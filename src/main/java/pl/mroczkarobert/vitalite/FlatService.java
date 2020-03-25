package pl.mroczkarobert.vitalite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.mroczkarobert.vitalite.common.Action;
import pl.mroczkarobert.vitalite.common.Flat;
import pl.mroczkarobert.vitalite.common.State;

@Service
public class FlatService {

    private static final Logger log = LoggerFactory.getLogger(FlatService.class);

    @Autowired
    private FlatRepository repo;

    public void checkDelete(State state) {
        for(Flat flat : repo.findByKind(state.kind)) {
            String index = flat.getEstateIndex();
            if (!state.processed.contains(index)) {
                if (repo.findByEstateIndexAndActionAndKind(index, Action.DELETE, state.kind) == null) {
                    log.info("Deleted!\n" + flat.getContent());
                    repo.save(new Flat(flat.getContent(), index, Action.DELETE, state.kind));
                    state.anyChange = true;
                }
            }
        }
    }

    public void checkEstate(String content, String estateIndex, State state) {
        log.info("Estate " + estateIndex);
        state.processed.add(estateIndex);

        Flat flat = repo.findFirstByEstateIndexAndKindOrderByIdDesc(estateIndex, state.kind);
        if (flat != null) {
            log.info("Found");
            if (content.equals(flat.getContent())) {
                log.info("No changes");

            } else {
                log.info("Changed!\n " + content);
                repo.save(new Flat(content, estateIndex, Action.EDIT, state.kind));
                state.anyChange = true;
            }

        } else {
            log.info("New!\n" + content);
            repo.save(new Flat(content, estateIndex, Action.NEW, state.kind));
            state.anyChange = true;
        }
    }

    public void startReport(State state) {
        log.info("Start check: " + state.kind);
    }

    public void endReport(State state) {
        if (state.anyChange) {
            log.error("There were changes in " + state.kind + "!");

        } else {
            log.warn("No changes in " + state.kind);
        }

        log.info("End check: " + state.kind);
    }
}
