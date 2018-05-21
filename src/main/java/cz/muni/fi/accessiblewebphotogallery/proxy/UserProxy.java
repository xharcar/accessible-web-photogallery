package cz.muni.fi.accessiblewebphotogallery.proxy;


import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;

import javax.validation.constraints.*;
import java.util.Objects;

/**
 * Simple stupid proxy class for UserDto; basically a UserDto w/o the password hash and salt.
 */
public class UserProxy {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 64)
    @Pattern(regexp = "[\\w]+")
    private String loginName;

    @NotBlank
    @Size(max = 128)
    @Pattern(regexp = "[\\p{Print}]+")
    private String screenName;

    @NotBlank
    @Email
    @Size(max = 128)
    private String email;

    @NotNull
    private AccountState accountState;

    @Size(max = 1536)
    @Pattern(regexp = "[\\p{Print}]+")
    private String bio;

    public UserProxy() {
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
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof UserProxy)) return false;
        UserProxy userProxy = (UserProxy) o;
        return Objects.equals(loginName, userProxy.loginName) &&
                Objects.equals(email, userProxy.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginName, email);
    }

    @Override
    public String toString() {
        return "UserProxy{" +
                "loginName='" + loginName + '\'' +
                ", screenName='" + screenName + '\'' +
                ", email='" + email + '\'' +
                ", accountState=" + accountState +
                ", bio='" + bio + '\'' +
                '}';
    }
}
