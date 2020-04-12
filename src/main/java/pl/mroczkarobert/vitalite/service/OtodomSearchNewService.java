package pl.mroczkarobert.vitalite.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mroczkarobert.vitalite.UrlRepository;
import pl.mroczkarobert.vitalite.common.Kind;
import pl.mroczkarobert.vitalite.common.Url;

import java.io.IOException;
import java.util.Iterator;

@Service
public class OtodomSearchNewService {

    private static final Logger LOG = LoggerFactory.getLogger(OtodomSearchNewService.class);

    @Autowired
    private UrlRepository urlRepository;

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
                urlRepository.save(new Url(urlWithoutHash, Kind.OTODOM));
            }
        }
    }
}
