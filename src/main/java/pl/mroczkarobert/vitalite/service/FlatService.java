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
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FlatService {

    private static final Logger LOG = LoggerFactory.getLogger(FlatService.class);

    @Autowired
    private FlatRepository repo;

    public void checkDelete(State state) {
        Set<String> processedInDelete = new HashSet<>();

        for(Flat flat : repo.findByKindOrderByIdDesc(state.kind)) {
            String index = flat.getEstateIndex();

            if (!processedInDelete.contains(index)) {
                processedInDelete.add(index);

                if (!state.processed.contains(index)) {
                    if (repo.findByEstateIndexAndActionAndKind(index, Action.DELETE, state.kind) == null) {
                        LOG.info("Deleted!");
                        repo.save(new Flat(flat, Action.DELETE));
                        state.anyChange = true;
                    }
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
                newFlat.setComment(buildComment(flat, newFlat));
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

    private String buildComment(Flat flat, Flat newFlat) {
        StringBuilder builder = new StringBuilder();

        if (!flat.contentEquals(newFlat)) {
            builder.append("Zmiana w treści. ");
        }
        if (!flat.phoneEquals(newFlat)) {
            builder.append("Zmiana numeru telefonu. ");
        }
        if (!flat.priceEquals(newFlat)) {
            builder.append("Zmiana ceny. ");
        }
        if (!flat.priceM2Equals(newFlat)) {
            builder.append("Zmiana ceny za metr. ");
        }
        if (!flat.livingAreaEquals(newFlat)) {
            builder.append("Zmiana metrażu. ");
        }
        if (!flat.agentEquals(newFlat)) {
            builder.append("Zmiana agenta. ");
        }
        if (!flat.agencyEquals(newFlat)) {
            builder.append("Zmiana agencji. ");
        }
        if (!flat.updateDateEquals(newFlat)) {
            builder.append("Zmiana daty aktualizacji. ");
        }

        return builder.toString();
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

    public String findOrNull(String details, Pattern pattern) {
        Matcher matcher = pattern.matcher(details);
        if (matcher.find()) {
            return matcher.group(0);

        } else {
            return null;
        }
    }

    public boolean findBoolean(String details, Pattern pattern) {
        return findOrNull(details, pattern) != null;
    }
}
