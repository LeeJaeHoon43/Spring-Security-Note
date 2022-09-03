package com.example.note.config;

import com.example.note.filter.StopwatchFilter;
import com.example.note.filter.TesterAuthenticationFilter;
import com.example.note.jwt.JwtAuthenticationFilter;
import com.example.note.jwt.JwtAuthorizationFilter;
import com.example.note.user.User;
import com.example.note.user.UserRepository;
import com.example.note.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // stop watch filter
        http.addFilterBefore(new StopwatchFilter(), WebAsyncManagerIntegrationFilter.class);
        // tester authentication filter
        http.addFilterBefore(new TesterAuthenticationFilter(this.authenticationManager()), UsernamePasswordAuthenticationFilter.class);
        // basic authentication filter disable
        http.httpBasic().disable(); // basic authentication filter 비활성화.
        // csrf
        http.csrf().disable();
        // rememberMeAuthenticationFilter
        http.rememberMe().disable();
        // stateless
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // jwt filter
        http.addFilterBefore(
                new JwtAuthenticationFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class
        ).addFilterBefore(
                new JwtAuthorizationFilter(userRepository),
                BasicAuthenticationFilter.class
        );
        // authorization : 인가를 설정
        http.authorizeRequests()
                // /와 /home은 모두에게 허용
                .antMatchers("/", "/home", "/signup").permitAll()
                // hello 페이지는 USER 롤을 가진 유저에게만 허용
                .antMatchers("/note").hasRole("USER") // 유저 권한인 경우만 해당
                .antMatchers("/admin").hasRole("ADMIN") // 관리자 권한인 경우만 해당
                .antMatchers(HttpMethod.POST, "/notice").hasRole("ADMIN") // 관리자 권한인 경우만 해당
                .antMatchers(HttpMethod.DELETE, "/notice").hasRole("ADMIN") // 관리자 권한인 경우만 해당
                .anyRequest().authenticated(); // 인증이 되었는지 검증
        // login : 폼 로그인의 로그인 페이지를 지정하고 로그인에 성공했을 때 이동하는 URL을 지정.
        http.formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll(); // 모두 허용
        // logout : 로그아웃 URL을 지정하고 로그아웃에 성공했을 때 이동하는 URL을 지정한다.
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/");
    }

    @Override
    public void configure(WebSecurity web) {
        // 정적 리소스 spring security 대상에서 제외
        // web.ignoring().antMatchers("/images/**", "/css/**"); // 아래 코드와 같은 코드입니다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * UserDetailsService 구현
     *
     * @return UserDetailsService
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException(username);
            }
            return user;
        };
    }
}
