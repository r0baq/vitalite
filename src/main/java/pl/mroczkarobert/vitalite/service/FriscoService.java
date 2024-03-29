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
public class FriscoService {

    private static final Logger LOG = LoggerFactory.getLogger(FriscoService.class);

    public void findNew() throws IOException {
//        String baseUrl = "https://www.frisco.pl/c,2/cat,piekarnia-cukiernia/stn,searchResults";
//        String baseUrl = "https://www.frisco.pl/c,4354/cat,piekarnia-cukiernia-chleb-pszenny/stn,searchResults";
//        String baseUrl = "https://www.frisco.pl/c,4355/cat,piekarnia-cukiernia-chleb-razowy-i-zytni/stn,searchResults";
        String baseUrl = "https://www.frisco.pl/c,117/cat,slodycze-i-przekaski-slodycze-slodycze-czekoladowe/stn,searchResults";
        LOG.info("baseUrl");

        Document searchPage = Jsoup.connect(baseUrl).get();
        int count = 0;

        for (Element script : searchPage.select("script")) {
            String html = script.html();

            int current = 0;
            int pos;
            do {
//                pos = html.indexOf("^80", current + 1);
//                pos = html.indexOf("^7N", current);
                pos = html.indexOf("^7O", current);
//                if (pos < 0) {
//                    pos = html.indexOf("^7V", current + 1);
//                }
                if (pos > 0) {
                    LOG.info(html.substring(pos, pos + 30));
                    current = pos + 1;
                    count++;
                }

            } while (pos > 0);
        }

        LOG.info("End 1 " + count);

//        LOG.info(searchPage.toString());

//        int count2 = 0;
//        for (Element element : searchPage.select("div.product-box_desc")) {
//            String title = element.select("a").attr("title");
//            String priceNum = element.select("span.price_num").first().text();
//            String priceDecimals = element.select("span.price_decimals").first().text();
//            LOG.info(title);
//            LOG.info(priceNum + "," + priceDecimals);
//            //LOG.info(element.toString());
//            count2++;
//        }
//
//        LOG.info("End 2 " + count2);
    }
}
