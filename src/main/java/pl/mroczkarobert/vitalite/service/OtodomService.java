package pl.mroczkarobert.vitalite.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mroczkarobert.vitalite.common.Kind;
import pl.mroczkarobert.vitalite.common.State;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
public class OtodomService {

    private static final Logger LOG = LoggerFactory.getLogger(MorizonService.class);

    private static final String OTODOM_ID_PREFIX = "Nr oferty w biurze nieruchomości: ";
    private static final Pattern OTODOM_ID = Pattern.compile(OTODOM_ID_PREFIX + "\\S*");

    private static final String PHONE_CODE_PREFIX = "phoneCode\":\"";
    private static final Pattern PHONE_CODE = Pattern.compile(PHONE_CODE_PREFIX + "[0-9-]*");

    @Autowired
    private FlatService service;

    //TODO url do bazy danych
    //TODO abstrakcyjny serwis
    public boolean check() throws IOException {
        State state = new State(Kind.OTODOM);
        service.startReport(state);

        checkEstate("https://www.otodom.pl/oferta/rabaty-duze-system-30-70-zostanwdomu-ID45g2q.html#4856509129", state);

//        service.checkDelete(state);
        service.endReport(state);
        return state.anyChange;
    }

    //TODO data aktualizacji
    //TODO numer telefonu
    private void checkEstate(String url, State state) throws IOException {

        Connection.Response res = Jsoup.connect(url).execute();
        Document doc = res.parse();
        LOG.debug(doc.toString());

        LOG.info("RMR04: " + res.cookies());

        String serverAppState = doc.select("#server-app-state").first().toString();
        String phoneCode = service.find(serverAppState, PHONE_CODE).replace(PHONE_CODE_PREFIX, "");
        String phoneCodeResult = Jsoup.connect("https://www.otodom.pl/frontera/api/item/owner/phone/" + phoneCode).cookies(res.cookies()).get().toString();
        LOG.info("RMR03: " + phoneCodeResult);

//        BigDecimal price = service.getDetail(doc, "div.css-1vr19r7");
//        BigDecimal priceM2 = service.getDetail(doc, "div.css-zdpt2t");
//        BigDecimal livingArea = service.getDetail(doc, "div.css-1ci0qpi > ul > li > strong");
//
//        String details = doc.select("section.section-description").first().toString();
//
//        String estateIndexDiv = doc.select("div.css-kos6vh").first().text();
//        String estateIndex = service.find(estateIndexDiv, OTODOM_ID).replace(OTODOM_ID_PREFIX, "");
//
//        service.checkEstate(url, details, estateIndex, phone, price, priceM2, livingArea, state);
    }
}
