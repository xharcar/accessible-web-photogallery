package cz.muni.fi.accessiblewebphotogallery.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

/**
 * Registration token entity for confirming user registrations.
 */
@Entity
public class RegistrationToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String email;

    @Column
    private String token;

    public RegistrationToken() {
    }

    public RegistrationToken(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistrationToken)) return false;
        RegistrationToken that = (RegistrationToken) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, token);
    }

    @Override
    public String toString() {
        return "RegistrationToken{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
