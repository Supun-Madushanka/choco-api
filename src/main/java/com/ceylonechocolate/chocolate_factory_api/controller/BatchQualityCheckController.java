package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.BatchQualityCheckRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.BatchQualityCheckResponse;
import com.ceylonechocolate.chocolate_factory_api.service.BatchQualityCheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/production-batches/{batchId}/quality-checks")
@RequiredArgsConstructor
public class BatchQualityCheckController {

    private final BatchQualityCheckService qualityCheckService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'QC_MANAGER','QC_CONTROLLER')")
    public ResponseEntity<ApiResponse<List<BatchQualityCheckResponse>>>
    getChecksByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(
                ApiResponse.success("Quality checks fetched successfully",
                        qualityCheckService.getChecksByBatch(batchId))
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','QC_MANAGER','QC_CONTROLLER')")
    public ResponseEntity<ApiResponse<BatchQualityCheckResponse>>
    addCheck(@PathVariable Long batchId,
             @Valid @RequestBody BatchQualityCheckRequest request,
             @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Quality check added successfully",
                        qualityCheckService.addCheck(
                                batchId, request, userDetails.getUsername()))
        );
    }
}