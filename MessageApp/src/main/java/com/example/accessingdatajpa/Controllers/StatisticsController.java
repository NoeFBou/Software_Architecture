package com.example.accessingdatajpa.Controllers;

import com.example.accessingdatajpa.Services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Statistics", description = "Endpoints for retrieving statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * Get messages statistics
     * Ex : GET http://localhost:8080/api/stats/messages
     */
    @Operation(summary = "Get message statistics", description = "Retrieves statistics on messages")
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessageStatistics() {
        return ResponseEntity.ok(statisticsService.getMessageStatistics());
    }

    /**
     * Get persons statistics
     * Ex : GET http://localhost:8080/api/stats/persons
     */
    @Operation(summary = "Get person statistics", description = "Retrieves statistics on users")
    @GetMapping("/persons")
    public ResponseEntity<Map<String, Object>> getPersonStatistics() {
        return ResponseEntity.ok(statisticsService.getPersonStatistics());
    }

    /**
     * Get queues statistics
     * Ex : GET http://localhost:8080/api/stats/queues
     */
    @Operation(summary = "Get queue statistics", description = "Retrieves statistics on queues")
    @GetMapping("/queues")
    public ResponseEntity<Map<String, Object>> getQueueStatistics() {
        return ResponseEntity.ok(statisticsService.getQueueStatistics());
    }

    /**
     * Get topics statistics
     * Ex : GET http://localhost:8080/api/stats/topics
     */
    @Operation(summary = "Get topic statistics", description = "Retrieves statistics on topics")
    @GetMapping("/topics")
    public ResponseEntity<Map<String, Object>> getTopicStatistics() {
        return ResponseEntity.ok(statisticsService.getTopicStatistics());
    }
}
