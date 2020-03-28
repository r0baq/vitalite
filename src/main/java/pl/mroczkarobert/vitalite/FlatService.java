package pl.mroczkarobert.vitalite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mroczkarobert.vitalite.common.Action;
import pl.mroczkarobert.vitalite.common.Flat;
import pl.mroczkarobert.vitalite.common.State;

@Service
public class FlatService {

    private static final Logger LOG = LoggerFactory.getLogger(FlatService.class);

    @Autowired
    private FlatRepository repo;

    public void checkDelete(State state) {
        for(Flat flat : repo.findByKind(state.kind)) {
            String index = flat.getEstateIndex();
            if (!state.processed.contains(index)) {
                if (repo.findByEstateIndexAndActionAndKind(index, Action.DELETE, state.kind) == null) {
                    LOG.info("Deleted!\n" + flat.getContent());
                    repo.save(new Flat(flat.getContent(), index, flat.getPhone(), Action.DELETE, state.kind));
                    state.anyChange = true;
                }
            }
        }
    }

    public void checkEstate(String content, String estateIndex, String phone, State state) {
        LOG.info("Estate " + estateIndex);
        state.processed.add(estateIndex);

        Flat flat = repo.findFirstByEstateIndexAndKindOrderByIdDesc(estateIndex, state.kind);
        if (flat != null) {
            LOG.info("Found");
            if (content.equals(flat.getContent())) {
                LOG.info("No changes");

            } else {
                LOG.info("Changed!\n " + content);
                repo.save(new Flat(content, estateIndex, phone, Action.EDIT, state.kind));
                state.anyChange = true;
            }

        } else {
            LOG.info("New!\n" + content);
            repo.save(new Flat(content, estateIndex, phone, Action.NEW, state.kind));
            state.anyChange = true;
        }
    }

    public void startReport(State state) {
        LOG.info("Start check: " + state.kind);
    }

    public void endReport(State state) {
        if (state.anyChange) {
            LOG.error("There were changes in " + state.kind + "!");

        } else {
            LOG.warn("No changes in " + state.kind);
        }

        LOG.info("End check: " + state.kind);
    }
}
