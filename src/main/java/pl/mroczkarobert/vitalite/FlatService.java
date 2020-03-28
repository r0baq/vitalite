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
                    LOG.info("Deleted!");
                    repo.save(new Flat(flat.getContent(), index, flat.getPhone(), flat.getSummaryTop(), Action.DELETE, state.kind));
                    state.anyChange = true;
                }
            }
        }
    }

    public void checkEstate(String content, String estateIndex, String phone, String summaryTop, State state) {
        LOG.info("Estate " + estateIndex);
        state.processed.add(estateIndex);

        Flat flat = repo.findFirstByEstateIndexAndKindOrderByIdDesc(estateIndex, state.kind);
        if (flat != null) {
            LOG.info("Found");
            if (
                content.equals(flat.getContent())
                && ((phone == null && flat.getPhone() == null) || phone.equals(flat.getPhone()))
                && ((summaryTop == null && flat.getSummaryTop() == null) || summaryTop.equals(flat.getSummaryTop()))
            ) {
                LOG.info("No changes");

            } else {
                LOG.info("Changed!");
                repo.save(new Flat(content, estateIndex, phone, summaryTop, Action.EDIT, state.kind));
                state.anyChange = true;
            }

        } else {
            LOG.info("New!");
            repo.save(new Flat(content, estateIndex, phone, summaryTop, Action.NEW, state.kind));
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
