package ua.hodik.gym.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table(name = "Tokens")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokensBlackList {
    @Id
    private String body;
    private Date expiration;
}
