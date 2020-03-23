package pl.mroczkarobert.vitalite;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Clock;
import java.util.Iterator;

@Component
public class Process {

    @Autowired
    private FlatRepository repo;

    @PostConstruct
    public void init() throws IOException {
        System.out.println("Start");

        for (int i = 1; i <=4 ; i++) {
            Document doc = Jsoup.connect("https://www.bi-polska.pl/inwestycja/vitalite/lista-lokali/page:" + i).get();
            Iterator<Element> iterator = doc.select("div.estate-details-list-toggle").iterator();

            while (iterator.hasNext()) {
                Element element = iterator.next();
                Integer estateIndex = Integer.valueOf(element.attr("data-estate-index"));
                repo.save(new Flat(element.toString(), estateIndex));
            }
        }

        System.out.println("End");
    }
}
