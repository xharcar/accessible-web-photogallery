package cz.muni.fi.accessiblewebphotogallery.persistence.entity;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "USERS")
public class UserEntity {
// TBD: record user DoB to filter NSFW/otherwise unwanted pictures Y/N
    // login name and screen name- allow changing screen name and keep login name static?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false) // nickname should be a unique handle
    private String nickname;

    @Column(unique = true,nullable = false) // no multiple accounts
    private String email;

    @Column(nullable = false)
    private byte[] hash;

    @Column(nullable = false)
    private byte[] salt;

    @Column(nullable = false)
    private AccountState accountState;

    public UserEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof UserEntity)) return false;

        UserEntity that = (UserEntity) o;

        if (!Objects.equals(nickname,that.nickname)) return false;
        return Objects.equals(email,that.email);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(nickname);
        result = 31 * result + Objects.hashCode(email);
        return result;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", hash=" + Arrays.toString(hash) +
                ", salt=" + Arrays.toString(salt) +
                ", accountState=" + accountState +
                '}';
    }
}
