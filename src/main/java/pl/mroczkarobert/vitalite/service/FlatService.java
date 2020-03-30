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

    public void checkEstate(String url, String content, String estateIndex, String phone, BigDecimal price, BigDecimal priceM2, BigDecimal livingArea, String agent, String agency, State state) {
        LOG.info("Estate " + estateIndex);
        state.processed.add(estateIndex);

        Flat flat = repo.findFirstByEstateIndexAndKindOrderByIdDesc(estateIndex, state.kind);
        if (flat != null) {
            LOG.info("Found");
            if (notChanged(flat, content, phone, price, priceM2, livingArea, agent, agency)) {
                LOG.info("No changes");

            } else {
                LOG.info("Changed!");
                repo.save(new Flat(url, content, estateIndex, phone, price, priceM2, livingArea, agent, agency, Action.EDIT, state.kind));
                state.anyChange = true;
            }

        } else {
            LOG.info("New!");
            repo.save(new Flat(url, content, estateIndex, phone, price, priceM2, livingArea, agent, agency, Action.NEW, state.kind));
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
