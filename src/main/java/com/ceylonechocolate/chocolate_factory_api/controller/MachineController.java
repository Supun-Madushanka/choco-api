package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.MachineRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.MachineResponse;
import com.ceylonechocolate.chocolate_factory_api.service.MachineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<MachineResponse>>>
    getAllMachines() {
        return ResponseEntity.ok(
                ApiResponse.success("Machines fetched successfully",
                        machineService.getAllMachines())
        );
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<List<MachineResponse>>>
    getMachinesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(
                ApiResponse.success("Machines fetched successfully",
                        machineService.getMachinesByStatus(status))
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER'," +
            "'PRODUCTION_SUPERVISOR')")
    public ResponseEntity<ApiResponse<MachineResponse>> getMachineById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Machine fetched successfully",
                        machineService.getMachineById(id))
        );
    }

    @GetMapping("/next-code")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<String>> getNextMachineCode() {
        return ResponseEntity.ok(
                ApiResponse.success("Next machine code generated",
                        machineService.getNextMachineCode())
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<MachineResponse>> createMachine(
            @Valid @RequestBody MachineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Machine created successfully",
                        machineService.createMachine(request))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<MachineResponse>> updateMachine(
            @PathVariable Long id,
            @Valid @RequestBody MachineRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Machine updated successfully",
                        machineService.updateMachine(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','PRODUCTION_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteMachine(
            @PathVariable Long id) {
        machineService.deleteMachine(id);
        return ResponseEntity.ok(
                ApiResponse.success("Machine deleted successfully")
        );
    }
}