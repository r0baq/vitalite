package pl.mroczkarobert.vitalite.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mroczkarobert.vitalite.UrlRepository;
import pl.mroczkarobert.vitalite.common.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.regex.Pattern;

@Service
public class MorizonService {

    private static final Logger LOG = LoggerFactory.getLogger(MorizonService.class);
    private static final Pattern MORIZON_ID = Pattern.compile("morizon-\\S*");
    private static final Pattern VIEWS_COUNT = Pattern.compile("Liczba wyświetleń: \\d{1,10}");

    @Autowired
    private FlatService service;
    @Autowired
    private UrlRepository urlRepository;

    public boolean check() throws IOException {
        State state = new State(Kind.MORIZON);
        service.startReport(state);

        for (Url url : urlRepository.findByStatusAndKind(Status.ACTIVE, state.kind)) {
            checkMorizonEstate(url, state);
        }

        service.checkDelete(state);
        service.endReport(state);
        return state.anyChange;
    }

    private void checkMorizonEstate(Url url, State state) throws IOException {

        Document doc = Jsoup.connect(url.getUrl()).get();
        LOG.debug(doc.toString());

        Element phone = doc.select("span.phone.hidden").first();

        if (phone == null) {
            LOG.info("Gone! " + url.getUrl());
            url.setStatus(Status.INACTIVE);
            urlRepository.save(url);
            return;
        }

        Flat flat = new Flat(state.kind, url.getUrl());

        flat.setPhone(phone.text());
        flat.setPrice(service.getDetail(doc, "li.paramIconPrice > em"));
        flat.setPriceM2(service.getDetail(doc, "li.paramIconPriceM2 > em"));
        flat.setLivingArea(service.getDetail(doc, "li.paramIconLivingArea > em"));

        String details = doc.select("section.propertyDetails").first().toString();
        flat.setEstateIndex(service.find(details, MORIZON_ID));

        details = clearViewsCount(details);
        details = replaceToday(details);
        details = replaceYesterday(details);

        flat.setUpdateDate(getDate(doc, "Zaktualizowano"));
        flat.setPublicationDate(getDate(doc, "Opublikowano"));

        flat.setContent(details);

        flat.setAgent(doc.select("div.agentName").first().text());
        flat.setAgency(doc.select("div.companyName").first().text());

        service.checkEstate(flat, state);
    }

    private LocalDate getDate(Document doc, String contains) {
        String date = doc.select("th:contains(" + contains + ")").first().parent().select("td").first().html();
        date = replaceToday(date);
        date = replaceYesterday(date);
        return LocalDate.parse(date, DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
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
