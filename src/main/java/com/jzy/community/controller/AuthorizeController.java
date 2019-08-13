package com.jzy.community.controller;

import com.jzy.community.dto.AccessTokenDTO;
import com.jzy.community.dto.GithubUser;
import com.jzy.community.mapper.UserMapper;
import com.jzy.community.model.User;
import com.jzy.community.provider.GitHubProvider;
import com.jzy.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author jzy
 * @create 2019-08-06-15:40
 */
@Controller
public class AuthorizeController {
    @Autowired
    private GitHubProvider gitHubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.Client.secret}")
    private String clientSecret;

    @Value("${github.Redirect.uri}")
    private String redirectUri;



    @Autowired
    private UserService userService;


    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setState(state);
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = gitHubProvider.getUser(accessToken);
        System.out.println(githubUser.getName());
        System.out.println(githubUser.getAvatarUrl());
        if (githubUser != null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatarUrl());
            if(user.getAccountId() == null){
                return "redirect:/";
            }
            userService.creatOrUpdate(user);
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(864000);
            response.addCookie(cookie);
            return "redirect:/";
        }else{
            return "redirect:/";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        //清除seeion
        request.getSession().removeAttribute("user");
        //移除cookie
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
