package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.dto.CardDTO;
import com.sheshapay.sheshapay.enums.HistoryType;
import com.sheshapay.sheshapay.exception.FormException;
import com.sheshapay.sheshapay.form.CardForm;
import com.sheshapay.sheshapay.model.Card;
import com.sheshapay.sheshapay.model.Profile;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.CardRepo;
import com.sheshapay.sheshapay.repo.ProfileRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class CardService {

    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long expiration = 1000L * 60 * 60 * 24 * 365 * 2;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private CardRepo cardRepo;

    @Autowired
    private HistoryService historyService;

    /**
     * Generate a token that encodes the card info + owner details.
     */
    public String generateToken(CardForm cardForm) throws FormException {
        cardForm.validate();//validate card first
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claim("cardNo", cardForm.getCardNo())
                .claim("expiry", cardForm.getExpiry())
                .claim("brand", cardForm.getBrand())
                .claim("cvv", cardForm.getCvv())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the card info back from a token into a DTO.
     */
    public CardDTO extractCardFromToken(String token) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new CardDTO(
                "",
                claims.get("cardNo", String.class),
                claims.get("expiry", String.class),
                claims.get("brand", String.class),
                claims.get("cvv", String.class)

        );
    }

    /**
     * Registers and stores the card token for a given user.
     */
    public void registerToken(String username, CardForm cardForm) throws FormException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found for user: " + username));

        cardForm.setFirstName(profile.getFirstName());
        cardForm.setLastName(profile.getLastName());

        Card dbCard = cardRepo.findByUser(user).orElse(null);
        if (dbCard != null) {
            cardRepo.delete(dbCard);
        }else{
            dbCard = new Card();
        }
        dbCard.setUser(user);
        dbCard.setToken(generateToken(cardForm));
        dbCard.setBrand(cardForm.getBrand());
        dbCard.setExpiryDate(cardForm.getExpiry());
        String cardNo = cardForm.getCardNo();
        int length = cardNo.length();
        dbCard.setCardNumber(cardNo.substring(length - 4, length));
        cardRepo.save(dbCard);

        historyService.recordActivity(user , HistoryType.CARD , "updated card information");
    }

    public CardDTO getTokenForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Card token = cardRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("No card token found for user"));

        CardDTO dto =  extractCardFromToken(token.getToken());
        dto.setUsername(user.getUsername());
        int length = dto.getCardNumber().length();
        dto.setCardNumber(dto.getCardNumber().substring(length - 4, length));
        return dto;
    }

    public Card getCard(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Card card = cardRepo.findByUser(user).orElseThrow(() -> new IllegalArgumentException("No card token found for user"));
        return card;
    }

}
