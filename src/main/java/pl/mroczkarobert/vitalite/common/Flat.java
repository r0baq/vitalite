package pl.mroczkarobert.vitalite.common;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Flat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    private Date createdDate;

    @Column(length = 10000)
    private String content;

    @Column(length = 10000)
    private String summaryTop;

    private String estateIndex;
    private Action action;
    private Kind kind;
    private String phone;
    private String comment;

    public Flat() {}

    public Flat(String content, String estateIndex, String phone, String summaryTop, Action action, Kind kind) {
        this.content = content;
        this.estateIndex = estateIndex;
        this.action = action;
        this.kind = kind;
        this.phone = phone;
        this.summaryTop = summaryTop;
    }

    public String getContent() {
        return content;
    }

    public String getEstateIndex() {
        return estateIndex;
    }

    public String getPhone() {
        return phone;
    }

    public String getSummaryTop() {
        return summaryTop;
    }
}
