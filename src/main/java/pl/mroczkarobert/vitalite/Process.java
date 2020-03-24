package pl.mroczkarobert.vitalite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Clock;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Component
public class Process {

    private static final Logger log = LoggerFactory.getLogger(Process.class);

    @Autowired
    private FlatRepository repo;

    @PostConstruct
    public void init() throws IOException {
        checkVitalite();
        //checkOutlet();
    }

    private void checkOutlet() throws IOException {
        log.info("Start check Outlet");
        Set<Integer> processed = new HashSet<>();
        boolean anyChange = false;

        Document doc = Jsoup.connect("https://www.bi-polska.pl/oferta/outlet").get();
        Element table = doc.select("#outlet-table").first();

        Iterator<Element> iterator = table.select("tbody > tr").iterator();

        while (iterator.hasNext()) {
            Element element = iterator.next();
            System.out.println("RMR id=" + element.id());
            if (!StringUtils.isEmpty(element.id())) {
                System.out.println("OFERTA!");
            }
            System.out.println(element);
        }

        log.info("End check Outlet");
    }

    private void checkVitalite() throws IOException {
        log.info("Start check Vitalite");
        Set<Integer> processed = new HashSet<>();
        boolean anyChange = false;

        for (int i = 1; i <= 4 ; i++) {
            log.info("Page " + i);
            Document doc = Jsoup.connect("https://www.bi-polska.pl/inwestycja/vitalite/lista-lokali/page:" + i).get();
            Iterator<Element> iterator = doc.select("div.estate-details-list-toggle").iterator();

            while (iterator.hasNext()) {
                Element element = iterator.next();
                Integer estateIndex = Integer.valueOf(element.attr("data-estate-index"));
                String content = element.toString();
                log.info("Estate " + estateIndex);
                processed.add(estateIndex);

                Flat flat = repo.findFirstByEstateIndexOrderByIdDesc(estateIndex);
                if (flat != null) {
                    log.info("Found");
                    if (content.equals(flat.getContent())) {
                        log.info("No changes");

                    } else {
                        log.info("Changed!\n " + content);
                        repo.save(new Flat(content, estateIndex, Action.EDIT));
                        anyChange = true;
                    }

                } else {
                    log.info("New!\n" + content);
                    repo.save(new Flat(content, estateIndex, Action.NEW));
                    anyChange = true;
                }
            }
        }

        for(Flat flat : repo.findAll()) {
            Integer index = flat.getEstateIndex();
            if (!processed.contains(index)) {
                if (repo.findByEstateIndexAndAction(index, Action.DELETE) == null) {
                    log.info("Deleted!\n" + flat.getContent());
                    repo.save(new Flat(flat.getContent(), index, Action.DELETE));
                }
            }
        }

        if (anyChange) {
            log.error("There were changes!");

        } else {
            log.warn("No changes at all.");
        }
        log.info("End check Vitalite");
    }
}
