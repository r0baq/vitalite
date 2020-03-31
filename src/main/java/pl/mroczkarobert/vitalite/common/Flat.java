package pl.mroczkarobert.vitalite.common;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

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
    private String agent;
    private String agency;
    private LocalDate updateDate;
    private LocalDate publicationDate;

    public Flat() {}

    public Flat(Kind kind, String url) {
        this.kind = kind;
        this.url = url;
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
        this.agent = flat.agent;
        this.agency = flat.agency;
        this.updateDate = flat.updateDate;
        this.publicationDate = flat.publicationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flat flat = (Flat) o;
        return content.equals(flat.content) &&
                Objects.equals(phone, flat.phone) &&
                ((price == flat.price) || (price != null && price.compareTo(flat.price) == 0)) &&
                ((priceM2 == flat.priceM2) || (priceM2 != null && priceM2.compareTo(flat.priceM2) == 0)) &&
                ((livingArea == flat.livingArea) || (livingArea != null && livingArea.compareTo(flat.livingArea) == 0)) &&
                Objects.equals(agent, flat.agent) &&
                Objects.equals(agency, flat.agency) &&
                Objects.equals(updateDate, flat.updateDate) &&
                Objects.equals(publicationDate, flat.publicationDate);
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

    public String getAgent() {
        return agent;
    }

    public String getAgency() {
        return agency;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setEstateIndex(String estateIndex) {
        this.estateIndex = estateIndex;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setPriceM2(BigDecimal priceM2) {
        this.priceM2 = priceM2;
    }

    public void setLivingArea(BigDecimal livingArea) {
        this.livingArea = livingArea;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }
}
