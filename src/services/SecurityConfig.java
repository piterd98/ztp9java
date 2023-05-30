@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/library/order/{orderId}/return").hasRole("USER")
                .anyExchange().permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password("{bcrypt}$2a$10$OfXaO5S8KriZmlyprYCYMOfTT5vWnFsJrXLPtRqyJ.9j9RYgQ3ZPu") // Replace with your own bcrypt-encoded password
                .roles("USER")
                .build();

        return new MapReactiveUserDetailsService(user);
    }
}