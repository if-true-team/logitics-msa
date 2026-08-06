package com.iftrue.hub.presentation;

import com.iftrue.hub.application.HubService;
import com.iftrue.hub.application.dto.HubCreateRequestDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import com.iftrue.hub.domain.Hub;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HubController.class)
@DisplayName("[Controller] 허브 컨트롤러 테스트")
class HubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HubService hubService;

    @Test
    @DisplayName("허브 생성 요청이 성공하면 생성된 허브를 반환한다")
    void createHubSuccess() throws Exception {
        UUID hubId = UUID.randomUUID();
        given(hubService.createHub(any(HubCreateRequestDto.class)))
                .willReturn(sampleResponse(hubId));

        String requestBody = """
                {
                  "name": "서울특별시 센터",
                  "address": "서울특별시 송파구 송파대로 55",
                  "latitude": 10.555555,
                  "longitude": 120.333333
                }
                """;

        mockMvc.perform(post("/api/v1/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.code").value("success"))
                .andExpect(jsonPath("$.data.id").value(hubId.toString()))
                .andExpect(jsonPath("$.data.name").value("서울특별시 센터"));
    }

    @Test
    @DisplayName("허브 이름이 누락될 시 H-006 에러를 반환한다")
    void createHubValidationFail() throws Exception {
        String invalidBody = """
                {
                  "address": "서울특별시 송파구 송파대로 55",
                  "latitude": 10.555555,
                  "longitude": 120.333333
                }
                """;

        mockMvc.perform(post("/api/v1/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("H-006"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("존재하지 않는 허브 조회 시 H-001 에러를 반환한다")
    void getHubNotFound() throws Exception {
        UUID hubId = UUID.randomUUID();
        given(hubService.getHub(hubId))
                .willThrow(new BusinessException(ErrorCode.HUB_NOT_FOUND));

        mockMvc.perform(get("/api/v1/hubs/{hubId}", hubId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("H-001"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PathVariable의 허브 id가 UUID 형식이 아니면 H-006 에러를 반환한다")
    void getHubInvalidId() throws Exception {
        mockMvc.perform(get("/api/v1/hubs/{hubId}", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("H-006"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.hubId").exists());
    }

    private HubResponseDto sampleResponse(UUID id) {
        Hub hub = Hub.create(
                "서울특별시 센터",
                "서울특별시 송파구 송파대로 55",
                new BigDecimal("10.555555"),
                new BigDecimal("120.333333"));
        ReflectionTestUtils.setField(hub, "id", id);
        return HubResponseDto.from(hub);
    }
}
