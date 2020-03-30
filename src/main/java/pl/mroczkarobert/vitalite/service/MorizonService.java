package pl.mroczkarobert.vitalite.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mroczkarobert.vitalite.Process;
import pl.mroczkarobert.vitalite.common.Kind;
import pl.mroczkarobert.vitalite.common.State;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MorizonService {

    private static final Logger LOG = LoggerFactory.getLogger(MorizonService.class);
    private static final Pattern MORIZON_ID = Pattern.compile("morizon-\\S*");
    private static final Pattern VIEWS_COUNT = Pattern.compile("Liczba wyświetleń: \\d{1,10}");

    @Autowired
    private FlatService service;

    public boolean check() throws IOException {
        State state = new State(Kind.MORIZON);
        service.startReport(state);

        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-61m2-mzn2035929730", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-zygmunta-vogla-62m2-mzn2035999835", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-61m2-mzn2036084941", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-61m2-mzn2036056741", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-62m2-mzn2035995336", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-61m2-mzn2035995345", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-71m2-mzn2035995348", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-61m2-mzn2036086103", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-zygmunta-vogla-62m2-mzn2035746454", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-glebowa-61m2-mzn2035987241", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-hektarowa-62m2-mzn2035990677", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-glebowa-62m2-mzn2035987242", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-glebowa-62m2-mzn2035987245", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-syta-61m2-mzn2033717168", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-syta-61m2-mzn2033718261", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-62m2-mzn2033668086", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-56m2-mzn2035995346", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-syta-61m2-mzn2035404431", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-syta-56m2-mzn2035404430", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-syta-62m2-mzn2035404432", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-62m2-mzn2033654864", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-wilanow-zawady-62m2-mzn2035536687", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-61m2-mzn2035437185", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-syta-61m2-mzn2033578390", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-wilanow-zawady-56m2-mzn2035417695", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-wilanow-zawady-62m2-mzn2034760968", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-wilanow-zawady-62m2-mzn2035417696", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-wilanow-zawady-62m2-mzn2035417689", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-wilanow-zawady-61m2-mzn2033528456", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-wilanow-zawady-71m2-mzn2035417693", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-61m2-mzn2033646418", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-wilanow-zawady-61m2-mzn2033528463", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-wilanow-zawady-61m2-mzn2034760969", state);
        checkMorizonEstate("https://www.morizon.pl/oferta/sprzedaz-mieszkanie-warszawa-wilanow-tuzinowa-61m2-mzn2035262522", state);

        service.checkDelete(state);
        service.endReport(state);
        return state.anyChange;
    }

    private void checkMorizonEstate(String url, State state) throws IOException {

        Document doc = Jsoup.connect(url).get();
        LOG.debug(doc.toString());

        String phone = doc.select("span.phone.hidden").first().text();
        BigDecimal price = service.getDetail(doc, "li.paramIconPrice > em");
        BigDecimal priceM2 = service.getDetail(doc, "li.paramIconPriceM2 > em");
        BigDecimal livingArea = service.getDetail(doc, "li.paramIconLivingArea > em");

        String details = doc.select("section.propertyDetails").first().toString();
        String estateIndex = service.find(details, MORIZON_ID);
        details = clearViewsCount(details);
        details = replaceToday(details);
        details = replaceYesterday(details);

        service.checkEstate(url, details, estateIndex, phone, price, priceM2, livingArea, null, null, state);
    }

    private String replaceToday(String details) {
        String today =  LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        return details.replace("<strong>dzisiaj</strong>", today);
    }

    private String replaceYesterday(String details) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdateString = yesterday.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        return details.replace("<strong>wczoraj</strong>", yesterdateString);
    }

    private String clearViewsCount(String details) {
        String viewsCount = service.find(details, VIEWS_COUNT);
        return details.replace(viewsCount, "Liczba wyświetleń: xxx");
    }
}