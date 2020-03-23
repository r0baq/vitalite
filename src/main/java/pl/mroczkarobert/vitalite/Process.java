package pl.mroczkarobert.vitalite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Clock;
import java.util.Iterator;

@Component
public class Process {

    private static final Logger log = LoggerFactory.getLogger(Process.class);

    @Autowired
    private FlatRepository repo;

    @PostConstruct
    public void init() throws IOException {
        log.info("Start");

        for (int i = 1; i <= 4 ; i++) {
            log.info("Page " + i);
            Document doc = Jsoup.connect("https://www.bi-polska.pl/inwestycja/vitalite/lista-lokali/page:" + i).get();
            Iterator<Element> iterator = doc.select("div.estate-details-list-toggle").iterator();

            while (iterator.hasNext()) {
                Element element = iterator.next();
                Integer estateIndex = Integer.valueOf(element.attr("data-estate-index"));
                String content = element.toString();
                log.info("Estate " + estateIndex);

                Flat flat = repo.findFirstByEstateIndexOrderByIdDesc(estateIndex);
                if (flat != null) {
                    log.info("Found");
                    if (content.equals(flat.getContent())) {
                        log.info("No changes");

                    } else {
                        log.info("Changed!");
                        repo.save(new Flat(content, estateIndex));
                    }

                } else {
                    log.info("New!\n" + content);
                    repo.save(new Flat(content, estateIndex));
                }
            }
        }

        log.info("End");
    }
}
