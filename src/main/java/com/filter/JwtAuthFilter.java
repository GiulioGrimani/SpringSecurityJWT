package com.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.service.JwtService;
import com.service.UserInfoDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Definizione filtro personalizzato dove andiamo ad autenticare un utente.
 * L'autenticazione avviene grossomodo controllando che l'header della request
 * contenga un token valido. Se e' valido (e quindi e' associato ad uno user)
 * viene recuperato il relativo userDetail (un record sul DB che rappresenta le credenziali
 * dell'utente).
 * Dal momento che stiamo eseguendo un filtro all'interno di una catena di filtri, dobbiamo controllare
 * che l'utente non sia stato gia' autenticato da un altro filtro, ovvero che il suo contesto sia vuoto.
 * 
 * Essendo il server stateless, non esiste una sessione che contenga uno stato, quindi il contesto viene
 * ricreato per ogni request. Pertanto controlliamo che sia vuoto solo perche' un altro filtro potrebbe
 * averlo riempito
 * 
 * Passati questi controlli, viene creato un token per un contesto che autorizza il client.
 * Questa classe si appoggia ai metodi definiti nel service JwtService.
 * 
 */

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserInfoDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;

		// Controlla che l'utente che si connette abbia un token inserito
		// nell'header della request per estrapolare token e username
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = jwtService.extractUsername(token);
		}

		// se esiste questo username e non e' gia' stato autenticato da altri filtri...
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// ...recupera le credenziali dell'utente e...
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			// se ha un token valido, ne genera un altro che associa al contesto
			// dell'utente, che ora e' autorizzato in base alle sue autorizzazioni a usare
			// il server
			if (jwtService.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		// infine, continua con la catena di filtri eseguendo il prossimo
		filterChain.doFilter(request, response);
	}

}
