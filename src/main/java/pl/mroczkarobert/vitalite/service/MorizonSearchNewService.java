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
public class MorizonSearchNewService {

    private static final Logger LOG = LoggerFactory.getLogger(MorizonSearchNewService.class);

    @Autowired
    private UrlRepository urlRepository;

    public void findNew() throws IOException {
        LOG.info("Looking for new flats");

        String baseUrl = "https://www.morizon.pl/mieszkania/rynek-pierwotny/warszawa/wilanow/?ps[number_of_rooms_from]=3&ps[number_of_rooms_to]=3&ps[number_of_floors_to]=2";

        Document firstPage = Jsoup.connect(baseUrl).get();
        LOG.info("First page");
        LOG.debug(firstPage.toString());
        saveAllNew(firstPage);

        LOG.info("Looking for new flats ended");
    }

    private void saveAllNew(Document doc) {
        Iterator<Element> iterator = doc.select("a.property-url").iterator();
        while (iterator.hasNext()) {

            Element link = iterator.next();
            String url = link.attr("href");

            if (urlRepository.findByUrl(url) == null) {
                LOG.info("New offer found {}", url);
                urlRepository.save(new Url(url, Kind.MORIZON));
            }
        }
    }
}
