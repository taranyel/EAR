package joinMe.db.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Address extends AbstractEntity {
    @Basic(optional = false)
    @Column(name = "city", nullable = false)
    private String city;

    @Basic(optional = false)
    @Column(name = "street", nullable = false)
    private String street;

    @Basic(optional = false)
    @Column(name = "number", nullable = false)
    private String number;

    @Basic(optional = false)
    @Column(name = "post_index", nullable = false)
    private String postIndex;

    @Basic(optional = false)
    @Column(name = "country", nullable = false)
    private String country;

    @OneToMany
    @JoinColumn(name = "address_id")
    private List<User> residents;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public List<User> getResidents() {
        return residents;
    }

    public void setResidents(List<User> residents) {
        this.residents = residents;
    }

    public String getPostIndex() {
        return postIndex;
    }

    public void setPostIndex(String postIndex) {
        this.postIndex = postIndex;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}