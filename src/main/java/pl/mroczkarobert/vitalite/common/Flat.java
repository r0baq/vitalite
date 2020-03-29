package pl.mroczkarobert.vitalite.common;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Flat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @CreationTimestamp
    private Date createdDate;
    private Action action;
    private Kind kind;
    private String comment;

    @Column(length = 10000)
    private String content;

    private String url;
    private String estateIndex;
    private String phone;
    private BigDecimal price;
    private BigDecimal priceM2;
    private BigDecimal livingArea;

    public Flat() {}

    public Flat(String url, String content, String estateIndex, String phone, BigDecimal price, BigDecimal priceM2, BigDecimal livingArea, Action action, Kind kind) {
        this.url = url;
        this.content = content;
        this.estateIndex = estateIndex;
        this.action = action;
        this.kind = kind;
        this.phone = phone;
        this.price = price;
        this.priceM2 = priceM2;
        this.livingArea = livingArea;
    }

    public Flat(Flat flat, Action action) {
        this.action = action;

        this.url = flat.url;
        this.content = flat.content;
        this.estateIndex = flat.estateIndex;
        this.kind = flat.kind;
        this.phone = flat.phone;
        this.price = flat.price;
        this.priceM2 = flat.priceM2;
        this.livingArea = flat.livingArea;
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

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getPriceM2() {
        return priceM2;
    }

    public BigDecimal getLivingArea() {
        return livingArea;
    }
}
