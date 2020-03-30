package pl.mroczkarobert.vitalite.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.mroczkarobert.vitalite.Process;
import pl.mroczkarobert.vitalite.common.Flat;
import pl.mroczkarobert.vitalite.common.Kind;
import pl.mroczkarobert.vitalite.common.State;

import java.io.IOException;
import java.util.Iterator;

@Service
public class VitaliteService {

    private static final Logger LOG = LoggerFactory.getLogger(VitaliteService.class);

    @Autowired
    private FlatService service;

    public boolean checkOutlet() throws IOException {
        State state = new State(Kind.OUTLET);
        service.startReport(state);

        Document doc = Jsoup.connect("https://www.bi-polska.pl/oferta/outlet").get();
        Element table = doc.select("#outlet-table").first();

        Iterator<Element> iterator = table.select("tbody > tr").iterator();
        String investment = null;

        while (iterator.hasNext()) {
            Element element = iterator.next();

            if (!StringUtils.isEmpty(element.id())) {

                Flat flat = new Flat(state.kind, null);
                flat.setContent(element.toString());
                flat.setEstateIndex(element.id());

                service.checkEstate(flat, state);

            } else {
                investment = element.select("td div a").text();
                LOG.info("Investment " + investment);
            }
        }

        service.checkDelete(state);
        service.endReport(state);
        return state.anyChange;
    }

    public boolean checkVitalite() throws IOException {
        State state = new State(Kind.VITALITE);
        service.startReport(state);

        for (int i = 1; i <= 4 ; i++) {
            LOG.info("Page " + i);
            Document doc = Jsoup.connect("https://www.bi-polska.pl/inwestycja/vitalite/lista-lokali/page:" + i).get();
            Iterator<Element> iterator = doc.select("div.estate-details-list-toggle").iterator();

            while (iterator.hasNext()) {
                Element element = iterator.next();

                Flat flat = new Flat(state.kind, null);
                flat.setContent(element.toString());
                flat.setEstateIndex(element.attr("data-estate-index"));

                service.checkEstate(flat, state);
            }
        }

        service.checkDelete(state);
        service.endReport(state);
        return state.anyChange;
    }
}
