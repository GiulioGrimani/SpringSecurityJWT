package com.config;

//import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.filter.JwtAuthFilter;
import com.service.UserInfoDetailsService;
import com.service.UserInfoDetailsService2;

/*
 * Classe di configurazione.
 * Nota bene: in securityFilterChain configuriamo la catena di filtri attraverso
 * la quale passera' la request del client
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	// Dependency Injection del nostro filtro custom
	@Autowired
	private JwtAuthFilter authFilter;

	/*
	 * Quando facciamo l'autenticazione dell'utente tramite il recupero delle sue
	 * credenziali dal DB, allora abbiamo bisogno di definire due beans:
	 * UserDetailService e PasswordEncoder
	 */

	/*
	 * Recupera le credenziali dal DB e le wrappa in un oggetto di tipo UserDetails
	 * arricchito di metodi per la gestione dell'utenza
	 */
	@Bean
	UserDetailsService userDetailsService() {
		return new UserInfoDetailsService();
	}

	/*
	 * Codifica la password dell'utente sul DB
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * Simulo un secondo modo di autenticarsi (passando sempre dal recupero delle
	 * delle credenziali sul DB, ma controllando i campi username e password)
	 * Probabilmente ha piu' senso parlare di "altro modo di autenticarsi" quando
	 * usiamo il protocollo OAuth2 (social login)
	 * 
	 * https://www.marcobehler.com/guides/spring-security#
	 * _authentication_with_spring_security
	 */
	@Bean
	UserDetailsService userDetailsService2() {
		return new UserInfoDetailsService2();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		/*
		 * auth.requestMatchers(endpoint1, endpoint2, endpoint3, ...).permitAll();
		 * consente a tutti, anche a client non autenticati, di usare i servizi
		 * associati agli endpoint
		 * 
		 * {qualcosa da capire meglio} successivamente e' possibile definire in base al
		 * ruolo e/o autorizzazione dove l'utente puo' accedere (ruolo per gli endpoint)
		 * e cosa puo' fare (autorizzazione per le dml)
		 * 
		 * auth.anyRequest().authenticated(); significa che tutti gli altri endpoint
		 * sono usabili solo da client autenticati
		 * 
		 * Viene disabilitata la configurazione per gli attacchi CSRF (Cross Site
		 * Request Forgery): e' quasi superflua nel caso in cui il nostro backend
		 * implementi Spring Security con JWT
		 * (https://www.baeldung.com/csrf-stateless-rest-api)
		 * 
		 * Vengono definite le cors (Cross-Origin Resource Sharing) dove vengono
		 * specificati gli endpoint che possono fare da client e quindi inoltrare una
		 * request: i cosiddetti Origin (l'origine della request). In una request,
		 * l'informazione sull'Origin e' contenuta nell'header alla voce "Origin". Per
		 * simulare da Postman un client con una determinata Origin che fa una request,
		 * va aggiunta agli header la relativa coppia chiave-valore, ad esempio: Origin
		 * - https://www.google.com. Abbiamo due modi per implementare le cors: passare
		 * per Spring Security o mettere l'annotazione @CrossOrigin nei vari metodi dei
		 * Controller. Se passiamo per Spring Security, le annotazioni sui controller
		 * vengono sovrascritte dalla configurazione di Spring Security. Con Spring
		 * Security, abbiamo nei filtri cors(withDefaults()) che va a caricare le
		 * configurazioni impostate nel metodo corsConfigurationSource, dove tra le
		 * tante cose vengono specificati la lista delle Origin consentite e dei metodi
		 * http consentiti nelle request
		 * 
		 * La sessione di ogni request e' senza stato, questo significa che ogni request
		 * e' indipendente dalle altre. Il fatto che un utente risulti loggato dipende
		 * quindi dal token (dal suo expire time)
		 * 
		 * httpBasic fornisce un'autenticazione di base con username (o email, a seconda
		 * del principal scelto - vedi metodi di authenticationManager) da usare ad
		 * esempio sul browser (NB: nell'header viene restituito un token messo dopo il
		 * bearer che e' la codifica in base 64 di principal + password). Da non usare a
		 * meno che tu non voglia farti fottere le credenziali
		 * 
		 * Poi c'e' l'authenticationManager OPPURE (OR ESCLUSIVO) authenticationProvider
		 * qui decidiamo come gestire l'autenticazione. Se abbiamo piu' di un modo per
		 * farlo, conviene usare l'authenticationManager (che possiamo usare anche se
		 * abbiamo un solo modo). Vengono qui usati i bean userDetailService e
		 * passwordEncoder
		 * 
		 * addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
		 * inserisce il nostro filtro custom JwtAuthFilter authFilter prima del filtro
		 * specificato come secondo parametro.
		 * (https://www.marcobehler.com/images/filterchain-1a.png) In questo filtro
		 * viene autenticato l'utente
		 * 
		 * Infine, tutti questi controlli sulla request si buildano, quindi alla fine
		 * abbiamo .build()
		 */

		return http.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/api/v1/addUser", "/api/v1/getToken").permitAll();
//			auth.requestMatchers("/api/v1/whereAmI").hasAuthority("DIO");
//			auth.requestMatchers("/api/v1/isRunning").hasAuthority("CEO");
			auth.anyRequest().authenticated();
		}).csrf(csrf -> csrf.disable()).cors(withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.httpBasic(it -> {
				})
				// Se ho un solo AutenticationProvider posso usare questa...
//				.authenticationProvider(authenticationProvider())
				// ...oppure l'authenticationManager dove nel bean specifico un solo provider
				.authenticationManager(authenticationManager())
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowCredentials(false);
		configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin",
				"Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin", "Cache-Control",
				"Content-Type", "Authorization"));
		configuration.setAllowedMethods(Arrays.asList("DELETE", "GET", "POST", "PATCH", "PUT"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	/*
	 * Questo Autentication Provider, per l'autenticazione, usa due bean:
	 * userDetailsService e passwordEncoder
	 */
	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	/*
	 * Questo Autentication Provider dovrebbe simulare un altro modo di connettersi
	 * al DB (usando ad esempio OAuth 2 dei social login), ma per ora e'
	 * semplicemente una copia del primo che usa anziche' il nome, l'email
	 */
	@Bean
	AuthenticationProvider authenticationProvider2() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService2());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	// Posso usare questo manager se ho un solo authenticationProvider
//	@Bean
//	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//		return config.getAuthenticationManager();
//	}

	/*
	 * Quando abbiamo piu' di un authentication provider conviene usare un
	 * authentication manger che provera' ad eseguire l'autenticazione con tutti i
	 * provider messi nella lista
	 */
	@Bean
	AuthenticationManager authenticationManager() throws Exception {
		return new ProviderManager(List.of(authenticationProvider(), authenticationProvider2()));
	}

}