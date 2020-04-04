package pl.mroczkarobert.vitalite.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mroczkarobert.vitalite.common.Flat;
import pl.mroczkarobert.vitalite.common.Kind;
import pl.mroczkarobert.vitalite.common.State;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

@Service
public class OtodomService {

    private static final Logger LOG = LoggerFactory.getLogger(MorizonService.class);

    private static final String OTODOM_ID_PREFIX = "Nr oferty w biurze nieruchomości: ";
    private static final Pattern OTODOM_ID = Pattern.compile(OTODOM_ID_PREFIX + "\\S*");

    private static final String UPDATE_DAYS_PREFIX = "Data aktualizacji: ";
    private static final Pattern UPDATE_DAYS = Pattern.compile(UPDATE_DAYS_PREFIX + "\\d*");

    private static final String PUBLICATION_DAYS_PREFIX = "Data dodania: ";
    private static final Pattern PUBLICATION_DAYS = Pattern.compile(PUBLICATION_DAYS_PREFIX + "\\d*");

    @Autowired
    private FlatService service;

    public boolean check() throws IOException {
        State state = new State(Kind.OTODOM);
        service.startReport(state);

        checkEstate("https://www.otodom.pl/oferta/rabaty-duze-system-30-70-zostanwdomu-ID45g2q.html#4856509129", state);

        service.checkDelete(state);
        service.endReport(state);
        return state.anyChange;
    }

    private void checkEstate(String url, State state) throws IOException {

        Document doc = Jsoup.connect(url).get();
        LOG.debug(doc.toString());

        Flat flat = new Flat(state.kind, url);

        flat.setPhone(doc.select("strong.css-n1vsi7").first().text());
        flat.setAgent(doc.select("div.css-1rg48tw").first().ownText());
        flat.setAgency(doc.select("li.css-1uzc6ks > strong").first().text());
        flat.setPrice(service.getDetail(doc, "div.css-1vr19r7"));
        flat.setPriceM2(service.getDetail(doc, "div.css-zdpt2t"));
        flat.setLivingArea(service.getDetail(doc, "div.css-1ci0qpi > ul > li > strong"));
        flat.setContent(doc.select("section.section-description").first().toString());

        String estateIndexDiv = doc.select("div.css-kos6vh").first().text();
        flat.setEstateIndex(service.find(estateIndexDiv, OTODOM_ID).replace(OTODOM_ID_PREFIX, ""));

        String daysDiv = doc.select("div.css-lh1bxu").first().text();

        String updateDays = service.find(daysDiv, UPDATE_DAYS).replace(UPDATE_DAYS_PREFIX, "");
        flat.setUpdateDate(LocalDate.now().minusDays(Integer.valueOf(updateDays)));

        String publicationDays = service.find(daysDiv, PUBLICATION_DAYS).replace(PUBLICATION_DAYS_PREFIX, "");
        flat.setPublicationDate(LocalDate.now().minusDays(Integer.valueOf(publicationDays)));

        service.checkEstate(flat, state);
    }
}
