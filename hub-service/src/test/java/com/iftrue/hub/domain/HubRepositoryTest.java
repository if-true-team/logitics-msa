package com.iftrue.hub.domain;

import com.iftrue.hub.global.config.AuditorAwareImpl;
import com.iftrue.hub.global.config.JpaAuditingConfig;
import com.iftrue.hub.global.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfig.class, AuditorAwareImpl.class, CurrentUserProvider.class})
@ActiveProfiles("test")
@DisplayName("[Repository] 허브 레포지토리 테스트")
class HubRepositoryTest {

    @Autowired
    private HubRepository hubRepository;

    @Test
    @DisplayName("삭제되지 않은 허브는 id로 조회된다")
    void findHub() {
        Hub hub = hubRepository.save(hub("서울특별시 센터"));

        Optional<Hub> found = hubRepository.findByIdAndDeletedAtIsNull(hub.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("서울특별시 센터");
    }

    @Test
    @DisplayName("soft deleted된 허브는 조회되지 않는다")
    void findHubSoftDeleted() {
        Hub hub = hub("부산광역시 센터");
        hub.softDelete(UUID.randomUUID());
        Hub saved = hubRepository.save(hub);

        Optional<Hub> found = hubRepository.findByIdAndDeletedAtIsNull(saved.getId());

        assertThat(found).isEmpty();
    }

    private Hub hub(String name) {
        return Hub.create(
                name,
                "테스트 주소",
                new BigDecimal("37.563600"),
                new BigDecimal("126.982500"));
    }
}
