package com.fundflow.health;

import com.fundflow.common.ApiResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        jdbcTemplate.queryForObject("select 1", Integer.class);
        Map<String, Object> status = Map.of("ok", true, "database", "connected");
        return ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .message("System is healthy")
                .data(status)
                .build();
    }
}
