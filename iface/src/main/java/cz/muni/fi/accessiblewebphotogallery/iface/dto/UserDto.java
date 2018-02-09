package cz.muni.fi.accessiblewebphotogallery.iface.dto;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;

import java.util.Arrays;
import java.util.Objects;

public class UserDto {

    private Long id;

    private String loginName;

    private String screenName;

    private String email;

    private byte[] passHash;

    private byte[] passSalt;

    private AccountState accState;

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

    public byte[] getPassHash() {
        return passHash;
    }

    public void setPassHash(byte[] passHash) {
        this.passHash = passHash;
    }

    public byte[] getPassSalt() {
        return passSalt;
    }

    public void setPassSalt(byte[] passSalt) {
        this.passSalt = passSalt;
    }

    public AccountState getAccState() {
        return accState;
    }

    public void setAccState(AccountState accState) {
        this.accState = accState;
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
        if (!(o instanceof UserDto)) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(loginName, userDto.loginName) &&
                Objects.equals(screenName, userDto.screenName) &&
                Objects.equals(email, userDto.email) &&
                Arrays.equals(passHash, userDto.passHash) &&
                Arrays.equals(passSalt, userDto.passSalt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginName, screenName, email, passHash, passSalt);
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", email='" + email + '\'' +
                ", passHash=" + Arrays.toString(passHash) +
                ", passSalt=" + Arrays.toString(passSalt) +
                ", accState=" + accState +
                '}';
    }
}
