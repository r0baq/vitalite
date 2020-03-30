package pl.mroczkarobert.vitalite.service;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mroczkarobert.vitalite.FlatRepository;
import pl.mroczkarobert.vitalite.common.Action;
import pl.mroczkarobert.vitalite.common.Flat;
import pl.mroczkarobert.vitalite.common.State;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    repo.save(new Flat(flat, Action.DELETE));
                    state.anyChange = true;
                }
            }
        }
    }

    public void checkEstate(Flat newFlat, State state) {
        String index = newFlat.getEstateIndex();
        LOG.info("Estate " + index);
        state.processed.add(index);

        Flat flat = repo.findFirstByEstateIndexAndKindOrderByIdDesc(index, state.kind);
        if (flat != null) {
            LOG.info("Found");
            if (flat.equals(newFlat)) {
                LOG.info("No changes");

            } else {
                LOG.info("Changed!");
                newFlat.setAction(Action.EDIT);
                repo.save(newFlat);
                state.anyChange = true;
            }

        } else {
            LOG.info("New!");
            newFlat.setAction(Action.NEW);
            repo.save(newFlat);
            state.anyChange = true;
        }
    }

    private boolean notChanged(Flat flat, String content, String phone, BigDecimal price, BigDecimal priceM2, BigDecimal livingArea, String agent, String agency) {
        return
            content.equals(flat.getContent())
            && ((phone == null && flat.getPhone() == null) || phone.equals(flat.getPhone()))
            && ((price == null && flat.getPrice() == null) || price.compareTo(flat.getPrice()) == 0)
            && ((priceM2 == null && flat.getPriceM2() == null) || priceM2.compareTo(flat.getPriceM2()) == 0)
            && ((livingArea == null && flat.getLivingArea() == null) || livingArea.compareTo(flat.getLivingArea()) == 0)
            && ((agent == null && flat.getAgent() == null) || agent.compareTo(flat.getAgent()) == 0)
            && ((agency == null && flat.getAgency() == null) || agency.compareTo(flat.getAgency()) == 0);
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

    public BigDecimal getDetail(Document doc, String query) {
        String emTag = doc.select(query).first().text();
        String value = emTag.replaceAll("[^0-9,]+","").replace(",", ".");
        return new BigDecimal(value);
    }

    public String find(String details, Pattern pattern) {
        Matcher matcher = pattern.matcher(details);
        if (matcher.find()) {
            return matcher.group(0);

        } else {
            throw new RuntimeException("Nie znaleziono w ofercie: " + pattern);
        }
    }
}
