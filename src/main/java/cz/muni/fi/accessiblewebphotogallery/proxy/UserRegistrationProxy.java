package cz.muni.fi.accessiblewebphotogallery.proxy;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Simple stupid proxy class for registering a user
 */
public class UserRegistrationProxy {

    @NotBlank
    @Size(min = 2, max = 64)
    @Pattern(regexp = "[\\w]+")
    private String loginName;

    @Email
    @NotBlank
    @Size(max = 128)
    private String email;

    @NotBlank
    @Size(min = 8, max = 64)
    @Pattern(regexp = "[\\p{Graph}]+")
    // nobody really uses anything else anyway, and better make sure nobody breaks
    // anything by inserting control characters or whatever
    private String password;

    public UserRegistrationProxy(){}

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
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
        if (!(o instanceof UserRegistrationProxy)) return false;
        UserRegistrationProxy that = (UserRegistrationProxy) o;
        return Objects.equals(loginName, that.loginName) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loginName, email);
    }

    @Override
    public String toString() {
        return "UserRegistrationProxy{" +
                "loginName='" + loginName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
