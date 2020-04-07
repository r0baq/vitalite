package pl.mroczkarobert.vitalite.service;

import org.apache.commons.lang3.StringUtils;
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
import java.util.Iterator;
import java.util.regex.Pattern;

@Service
public class OtodomService {

    private static final Logger LOG = LoggerFactory.getLogger(MorizonService.class);
    private static final Kind KIND = Kind.OTODOM;

    private static final String AGENCY_ID_PREFIX = "Nr oferty w biurze nieruchomości: ";
    private static final Pattern AGENCY_ID = Pattern.compile(AGENCY_ID_PREFIX + "\\S*");

    private static final String OTODOM_ID_PREFIX = "Nr oferty w Otodom: ";
    private static final Pattern OTODOM_ID = Pattern.compile(OTODOM_ID_PREFIX + "\\S*");

    private static final String UPDATE_DAYS_PREFIX = "Data aktualizacji: ";
    private static final Pattern UPDATE_DAYS = Pattern.compile(UPDATE_DAYS_PREFIX + "\\d{1,2}");

    private static final String PUBLICATION_PREFIX = "Data dodania: ";

    private static final String PUBLICATION_DAYS_SUFFIX = " dni temu";
    private static final Pattern PUBLICATION_DAYS = Pattern.compile(PUBLICATION_PREFIX + "\\d*" + PUBLICATION_DAYS_SUFFIX);

    private static final Pattern PUBLICATION_MONTHS = Pattern.compile(PUBLICATION_PREFIX + "\\d* miesi.c. temu");

    @Autowired
    private FlatService service;
    @Autowired
    private UrlRepository urlRepository;

    public boolean check() throws IOException {
        State state = new State(KIND);
        service.startReport(state);

        for (Url url : urlRepository.findByStatusAndKind(Status.ACTIVE, KIND)) {
            checkEstate(url, state);
        }

        service.checkDelete(state);
        service.endReport(state);
        return state.anyChange;
    }

    private void checkEstate(Url url, State state) throws IOException {
        LOG.info("Checking {}", url.getUrl());

        Document doc = Jsoup.connect(url.getUrl()).get();
        LOG.debug(doc.toString());

        Element phone = doc.select("strong.css-n1vsi7").first();

        if (phone == null) {
            LOG.info("Gone! " + url.getUrl());
            url.setStatus(Status.INACTIVE);
            urlRepository.save(url);
            return;
        }

        Flat flat = new Flat(state.kind, url.getUrl());

        flat.setPhone(phone.text());
        flat.setAgent(doc.select("div.css-1rg48tw").first().ownText());
        flat.setPrice(service.getDetail(doc, "div.css-1vr19r7"));
        flat.setPriceM2(service.getDetail(doc, "div.css-zdpt2t"));
        flat.setLivingArea(service.getDetail(doc, "div.css-1ci0qpi > ul > li > strong"));
        flat.setContent(doc.select("section.section-description").first().toString());

        Element agency = doc.select("li.css-1uzc6ks > strong").first();
        if (agency != null) {
            flat.setAgency(agency.text());
        }

        String estateIndexDiv = doc.select("div.css-kos6vh").first().text();
        String agencyId = service.findOrNull(estateIndexDiv, AGENCY_ID);
        if (agencyId != null) {
            flat.setEstateIndex(agencyId.replace(AGENCY_ID_PREFIX, ""));

        } else {
            String otodomId = service.find(estateIndexDiv, OTODOM_ID);
            flat.setEstateIndex(otodomId.replace(OTODOM_ID_PREFIX, "otodom-"));
        }

        String daysDiv = doc.select("div.css-lh1bxu").first().text();

        String updateDays = service.findOrNull(daysDiv, UPDATE_DAYS);
        if (updateDays != null) {
            flat.setUpdateDate(LocalDate.now().minusDays(Integer.valueOf(updateDays.replace(UPDATE_DAYS_PREFIX, ""))));

        } else {
            flat.setUpdateDate(LocalDate.now());
        }

        flat.setPublicationDate(getPublicationDays(daysDiv));
        fillLocation(flat);

        service.checkEstate(flat, state);
    }

    private void fillLocation(Flat flat) {
        if (StringUtils.containsIgnoreCase(flat.getContent(), "spa") || StringUtils.containsIgnoreCase(flat.getContent(), "syta")) {
            flat.setLocation("Vitalite");

        } else {
            flat.setLocation("Wilanów");
        }
    }

    private LocalDate getPublicationDays(String daysDiv) {

        String publicationDays = service.findOrNull(daysDiv, PUBLICATION_DAYS);

        if (StringUtils.isEmpty(publicationDays)) {
            String publicationMonths = service.findOrNull(daysDiv, PUBLICATION_MONTHS);

            if (StringUtils.isEmpty(publicationMonths)) {
                return LocalDate.now().minusDays(30);

            } else {
                return LocalDate.now().minusMonths(Integer.valueOf(publicationMonths.replaceAll("\\D+","")));
            }

        } else {
            return LocalDate.now().minusDays(Integer.valueOf(publicationDays.replace(PUBLICATION_PREFIX, "").replace(PUBLICATION_DAYS_SUFFIX, "")));
        }
    }

    public void findNew() throws IOException {
        LOG.info("Looking for new flats");

        String baseUrl =
            "https://www.otodom.pl/sprzedaz/mieszkanie/warszawa/wilanow/" +
                "?search[filter_enum_rooms_num][0]=3&search[filter_enum_market]=primary&search[filter_float_building_floors_num:to]=3&search[region_id]=7&search[subregion_id]=197" +
                "&search[city_id]=26&search[district_id]=50";

        Document firstPage = Jsoup.connect(baseUrl).get();
        LOG.debug(firstPage.toString());
        saveAllNew(firstPage);

        Document secondPage = Jsoup.connect(baseUrl + "&page=2").get();
        LOG.debug(secondPage.toString());
        saveAllNew(secondPage);

        LOG.info("Looking for new flats ended");
    }

    private void saveAllNew(Document doc) {
        Iterator<Element> iterator = doc.select("article").iterator();
        while (iterator.hasNext()) {

            Element article = iterator.next();
            String url = article.attr("data-url");
            int hashIndex = url.indexOf("#");
            String urlWithoutHash = url.substring(0, hashIndex);

            if (urlRepository.findByUrl(urlWithoutHash) == null) {
                LOG.info("New offer found {}", url);
                urlRepository.save(new Url(urlWithoutHash, KIND));
            }
        }
    }
}
