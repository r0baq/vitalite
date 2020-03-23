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

@Component
public class Process {

    @Autowired
    private FlatRepository repo;

    @PostConstruct
    public void init() throws IOException {
        System.out.println("Start");

//        Document doc = Jsoup.connect("https://www.bi-polska.pl/inwestycja/vitalite/lista-lokali").get();
//        doc.select("div.estate-details-list-toggle").forEach(System.out::println);

        repo.save(new Flat("RMR", 1));

        System.out.println("End");
    }
}
