package cz.muni.fi.accessiblewebphotogallery.web.pto;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class UserPto {

    private Long id;

    @NotBlank
    private String loginName;

    @NotBlank
    private String screenName;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private AccountState accountState;

    private String bio;

    public UserPto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof UserPto)) return false;
        UserPto userPto = (UserPto) o;
        return Objects.equals(loginName, userPto.loginName) &&
                Objects.equals(email, userPto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginName, email);
    }

    @Override
    public String toString() {
        return "AlbumPto{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", screenName='" + screenName + '\'' +
                ", email='" + email + '\'' +
                ", accountState=" + accountState +
                ", bio='" + bio + '\'' +
                '}';
    }
}
