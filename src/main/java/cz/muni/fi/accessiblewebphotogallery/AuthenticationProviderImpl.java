package cz.muni.fi.accessiblewebphotogallery;

import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private UserFacade userFacade;

    @Autowired
    public AuthenticationProviderImpl(UserFacade userFacade){
        this.userFacade = userFacade;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String identifier = authentication.getName();
        String password = (String)authentication.getCredentials();
        Pair<Boolean,Optional<UserDto>> authenticationResult = userFacade.authenticateUser(identifier,password);
        if(!authenticationResult.getFirst()){
            if(identifier.contains("@")){
                throw new BadCredentialsException("Invalid email or password.");
            }else{
                throw new BadCredentialsException("Invalid username or password.");
            }
        }
        if(!authenticationResult.getSecond().isPresent()){
            throw new AuthenticationServiceException("Authentication succeeded but returned an invalid value.");
        }
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("ROLE_"+authenticationResult.getSecond().get().getAccountState());
        return new UsernamePasswordAuthenticationToken(identifier,password,authorityList);
    }

    public static void logInUser(UserDto userDto, String password){
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList("ROLE_"+ userDto.getAccountState());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDto.getEmail(),password,authorityList);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void logout(HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request,null,authentication);
    }

    public static String getLoggedInUserIdentifier(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            return null;
        }
        return authentication.getName();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
