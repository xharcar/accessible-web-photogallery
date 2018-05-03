package cz.muni.fi.accessiblewebphotogallery.web;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public class AuthenticationProviderImpl implements AuthenticationProvider {

    @Inject
    private UserFacade userFacade;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String identifier = authentication.getName();
        String password = (String) authentication.getCredentials();
        Pair<Boolean,Optional<UserDto>> authResult = userFacade.authenticateUser(identifier,password);
        if(!authResult.getFirst()){
            throw new BadCredentialsException("Invalid username/email or password.");
        }
        if(!authResult.getSecond().isPresent()){
            throw new AuthenticationServiceException("Authentication succeeded but returned an invalid value.");
        }
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_"+authResult.getSecond().get().getAccountState());
        return new UsernamePasswordAuthenticationToken(identifier, password, authorities);
    }

    public static void logInUser(UserDto user, String password){
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + user.getAccountState());

        Authentication auth =
                new UsernamePasswordAuthenticationToken(user.getEmail(), password, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void logout(HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, null, auth);
    }

    public static String getLoggedUserLoginId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
