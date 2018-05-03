package cz.muni.fi.accessiblewebphotogallery.web.pto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

public class UserRegistrationPto {

    @Length(min = 2, max = 64)
    @Pattern(regexp = "[\\w]+")
    private String loginName;

    private String screenName;

    @Email
    @NotBlank
    private String email;

    @Length(min = 8, max = 64) // 64 characters ought to be enough for everyone
    private String password;

    public UserRegistrationPto() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (this == o) return true;
        if (!(o instanceof UserRegistrationPto)) return false;
        UserRegistrationPto that = (UserRegistrationPto) o;
        return Objects.equals(loginName, that.loginName) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginName, email);
    }

    @Override
    public String toString() {
        return "UserRegistrationPto{" +
                "loginName='" + loginName + '\'' +
                ", screenName='" + screenName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
