@file:Suppress("NoWildcardImports", "WildcardImport", "SpreadOperator")

package es.unizar.webeng.lab3

import org.springframework.context.annotation.*
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.*
import org.springframework.security.config.annotation.web.builders.*
import org.springframework.security.config.annotation.web.configuration.*
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    @Throws(Exception::class)
    fun httpSecurity(http: HttpSecurity): SecurityFilterChain? {
        http.authorizeRequests()
            // Invocations for POST and PUT have to have the role ADMIN
            // Invocation for DELETE has to have the role USER or ADMIN
            .antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/**").hasAnyRole("USER", "ADMIN")
            .and()
            .httpBasic()
            .and()

        // In any other case (GET), we can call the APIs even if we are not logged in
        http.csrf().disable()
        return http.build()
    }
}
