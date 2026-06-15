package com.ceylonechocolate.chocolate_factory_api.controller;

import com.ceylonechocolate.chocolate_factory_api.dto.request.AttendanceRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ApiResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.AttendanceResponse;
import com.ceylonechocolate.chocolate_factory_api.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // every logged in employee
    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Checked in successfully",
                        attendanceService.checkIn(userDetails.getUsername()))
        );
    }

    @PutMapping("/check-out")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Checked out successfully",
                        attendanceService.checkOut(userDetails.getUsername()))
        );
    }

    @GetMapping("/my/today")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getMyTodayAttendance(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Today's attendance fetched",
                        attendanceService.getMyTodayAttendance(
                                userDetails.getUsername()))
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getMyAttendance(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
                ApiResponse.success("Attendance fetched successfully",
                        attendanceService.getMyAttendance(
                                userDetails.getUsername(), startDate, endDate))
        );
    }

    // HR — view & manage all
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getByEmployee(
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(
                ApiResponse.success("Attendance fetched successfully",
                        attendanceService.getAttendanceByEmployee(employeeId))
        );
    }

    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(
                ApiResponse.success("Attendance fetched successfully",
                        attendanceService.getAttendanceByDate(date))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER','HR_OFFICER')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ApiResponse.success("Attendance updated successfully",
                        attendanceService.updateAttendance(
                                id, request, userDetails.getUsername()))
        );
    }

    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> createForEmployee(
            @PathVariable Long employeeId,
            @Valid @RequestBody AttendanceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Attendance record created successfully",
                        attendanceService.createAttendanceForEmployee(
                                employeeId, request, userDetails.getUsername()))
        );
    }
}