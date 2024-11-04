package ua.hodik.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.hodik.gym.model.TokensBlackList;

import java.util.Date;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokensBlackList, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM TokensBlackList t WHERE t.expiration < :date")
    void deleteByExpirationBefore(@Param("date") Date date);

}
