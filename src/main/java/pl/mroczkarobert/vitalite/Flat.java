package pl.mroczkarobert.vitalite;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Flat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String content;
    private Integer page;

    public Flat(String content, Integer page) {
        this.content = content;
        this.page = page;
    }
}
