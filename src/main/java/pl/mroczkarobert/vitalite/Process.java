package pl.mroczkarobert.vitalite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.mroczkarobert.vitalite.common.Action;
import pl.mroczkarobert.vitalite.common.Flat;
import pl.mroczkarobert.vitalite.common.Kind;
import pl.mroczkarobert.vitalite.common.State;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Iterator;

@Component
public class Process {

    private static final Logger log = LoggerFactory.getLogger(Process.class);

    @Autowired
    private FlatService service;

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
        service.startReport(state);

        Document doc = Jsoup.connect("https://www.bi-polska.pl/oferta/outlet").get();
        Element table = doc.select("#outlet-table").first();

        Iterator<Element> iterator = table.select("tbody > tr").iterator();
        String investment = null;

        while (iterator.hasNext()) {
            Element element = iterator.next();

            if (!StringUtils.isEmpty(element.id())) {
                service.checkEstate(element.toString(), element.id(), state);

            } else {
                investment = element.select("td div a").text();
                log.info("Investment " + investment);
            }
        }

        service.checkDelete(state);
        service.endReport(state);
        return state.anyChange;
    }

    private boolean checkVitalite() throws IOException {
        State state = new State(Kind.VITALITE);
        service.startReport(state);

        for (int i = 1; i <= 4 ; i++) {
            log.info("Page " + i);
            Document doc = Jsoup.connect("https://www.bi-polska.pl/inwestycja/vitalite/lista-lokali/page:" + i).get();
            Iterator<Element> iterator = doc.select("div.estate-details-list-toggle").iterator();

            while (iterator.hasNext()) {
                Element element = iterator.next();
                service.checkEstate(element.toString(), element.attr("data-estate-index"), state);
            }
        }

        service.checkDelete(state);
        service.endReport(state);
        return state.anyChange;
    }
}
