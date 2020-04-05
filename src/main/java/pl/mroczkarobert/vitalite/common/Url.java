package pl.mroczkarobert.vitalite.common;

import javax.persistence.*;

@Entity
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String url;
    private Status status;
    private Kind kind;

    public Url() {
    }

    public Url(String url, Kind kind) {
        this.url = url;
        this.kind = kind;
        this.status = Status.ACTIVE;
    }

    public String getUrl() {
        return url;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
