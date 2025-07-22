package Workeando20.erp.seguridad.security;

import Workeando20.erp.seguridad.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public String generarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getCorreo())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }

    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public Date extraerExpiration(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    public boolean esTokenValido(String token, Usuario usuario) {
        final String correo = extraerCorreo(token);
        return correo.equals(usuario.getCorreo()) && !extraerExpiration(token).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = parseToken(token);
        return resolver.apply(claims);
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
