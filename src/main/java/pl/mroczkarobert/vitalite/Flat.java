package pl.mroczkarobert.vitalite;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Flat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    private Date createdDate;

    @Column(length = 2000)
    private String content;

    private String estateIndex;

    private Action action;

    private Kind kind;

    public Flat() {}

    public Flat(String content, String estateIndex, Action action, Kind kind) {
        this.content = content;
        this.estateIndex = estateIndex;
        this.action = action;
        this.kind = kind;
    }

    public String getContent() {
        return content;
    }

    public String getEstateIndex() {
        return estateIndex;
    }
}
