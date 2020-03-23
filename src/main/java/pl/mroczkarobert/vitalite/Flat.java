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

    private Integer estateIndex;

    private Action action;

    public Flat() {}

    public Flat(String content, Integer estateIndex, Action action) {
        this.content = content;
        this.estateIndex = estateIndex;
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public Integer getEstateIndex() {
        return estateIndex;
    }
}
