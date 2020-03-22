package pl.mroczkarobert.vitalite;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Clock;

@Component
public class Process {

    @PostConstruct
    public void init() throws IOException {
        System.out.println("Start");

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet("https://www.bi-polska.pl/inwestycja/vitalite/lista-lokali"));
        System.out.println(EntityUtils.toString(response.getEntity()));

        System.out.println("End");
    }
}
