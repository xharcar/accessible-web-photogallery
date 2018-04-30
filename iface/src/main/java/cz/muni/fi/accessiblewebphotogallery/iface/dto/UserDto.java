package cz.muni.fi.accessiblewebphotogallery.iface.dto;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;

import java.util.Arrays;
import java.util.Objects;

public class UserDto {

    private Long id;

    private String loginName;

    private String screenName;

    private String email;

    private byte[] passwordHash;

    private byte[] passwordSalt;

    private AccountState accountState;

    private String bio;

    public UserDto() {}

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

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(byte[] passwordSalt) {
        this.passwordSalt = passwordSalt;
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
        if(o == null) return false;
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(loginName, userDto.loginName) &&
                Objects.equals(screenName, userDto.screenName) &&
                Objects.equals(email, userDto.email) &&
                Arrays.equals(passwordHash, userDto.passwordHash) &&
                Arrays.equals(passwordSalt, userDto.passwordSalt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginName, screenName, email, passwordHash, passwordSalt);
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash=" + Arrays.toString(passwordHash) +
                ", passwordSalt=" + Arrays.toString(passwordSalt) +
                ", accountState=" + accountState +
                '}';
    }
}
