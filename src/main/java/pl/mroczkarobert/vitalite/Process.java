package pl.mroczkarobert.vitalite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Iterator;

@Component
public class Process {

    private static final Logger log = LoggerFactory.getLogger(Process.class);

    @Autowired
    private FlatRepository repo;

    @PostConstruct
    public void init() throws IOException {
        boolean changedVitalite = checkVitalite();
        boolean changedOutlet = checkOutlet();

        if (changedVitalite || changedOutlet) {
            log.error("There were changes!");

        } else {
            log.warn("No changes at all.");
        }
    }

    private boolean checkOutlet() throws IOException {
        State state = new State(Kind.OUTLET);
        startReport(state);

        Document doc = Jsoup.connect("https://www.bi-polska.pl/oferta/outlet").get();
        Element table = doc.select("#outlet-table").first();

        Iterator<Element> iterator = table.select("tbody > tr").iterator();
        String investment = null;

        while (iterator.hasNext()) {
            Element element = iterator.next();

            if (!StringUtils.isEmpty(element.id())) {
                checkEstate(element.toString(), element.id(), state);

            } else {
                investment = element.select("td div a").text();
                log.info("Investment " + investment);
            }
        }

        checkDelete(state);
        endReport(state);
        return state.anyChange;
    }

    private boolean checkVitalite() throws IOException {
        State state = new State(Kind.VITALITE);
        startReport(state);

        for (int i = 1; i <= 4 ; i++) {
            log.info("Page " + i);
            Document doc = Jsoup.connect("https://www.bi-polska.pl/inwestycja/vitalite/lista-lokali/page:" + i).get();
            Iterator<Element> iterator = doc.select("div.estate-details-list-toggle").iterator();

            while (iterator.hasNext()) {
                Element element = iterator.next();
                checkEstate(element.toString(), element.attr("data-estate-index"), state);
            }
        }

        checkDelete(state);
        endReport(state);
        return state.anyChange;
    }

    private void checkDelete(State state) {
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

    private void checkEstate(String content, String estateIndex, State state) {
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

    private void startReport(State state) {
        log.info("Start check: " + state.kind);
    }

    private void endReport(State state) {
        if (state.anyChange) {
            log.error("There were changes in " + state.kind + "!");

        } else {
            log.warn("No changes in " + state.kind);
        }

        log.info("End check: " + state.kind);
    }
}
