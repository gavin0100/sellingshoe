package com.data.filtro.authentication.oauth;

import com.data.filtro.Util.Utility;
import com.data.filtro.authentication.oauth.user.OAuth2UserInfo;
import com.data.filtro.authentication.oauth.user.OAuth2UserInfoFactory;
import com.data.filtro.exception.OAuth2AuthenticationProcessingException;
import com.data.filtro.model.Provider;
import com.data.filtro.model.User;
import com.data.filtro.repository.UserRepository;
import com.data.filtro.service.MailSenderService;
import com.data.filtro.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService{
    private final UserRepository userRepository;

    private final UserService userService;

    private final MailSenderService mailSenderService;

    public CustomOAuth2UserService(UserRepository userRepository, UserService userService, MailSenderService mailSenderService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailSenderService = mailSenderService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByEmail(oAuth2UserInfo.getEmail()));
        User user = null;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equals(Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        } else {
            String username = "";
            if (Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()) == Provider.google){
                username = oAuth2User.getAttribute("family_name").toString() + oAuth2User.getAttribute("given_name").toString();
            } else {
                username = oAuth2User.getAttribute("name").toString();
            }

            String accountName = oAuth2User.getAttribute("email").toString();
            String email = oAuth2User.getAttribute("email").toString();
            String newPassword = Utility.getRandomString();
            userService.registerUserViaOauth(username, accountName, email, newPassword, newPassword, Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
            String to = email;
            String from = "voduc0100@gmail.com";
            String host = "smtp.gmail.com";
            String subject = "SHOP BÁN GIÀY FOUR LEAVES SHOE - MẬT KHẨU CHO TÀI KHOẢN!";
            mailSenderService.sendEmailGetPassword(to, from, host, subject, newPassword );
            user = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }
}
