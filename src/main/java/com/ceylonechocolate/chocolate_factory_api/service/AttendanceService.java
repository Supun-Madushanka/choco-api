package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.AttendanceRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.AttendanceResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    // Self mark
    AttendanceResponse checkIn(String email);

    AttendanceResponse checkOut(String email);

    AttendanceResponse getMyTodayAttendance(String email);

    List<AttendanceResponse> getMyAttendance(
            String email, LocalDate startDate, LocalDate endDate);

    // HR
    List<AttendanceResponse> getAttendanceByEmployee(Long employeeId);

    List<AttendanceResponse> getAttendanceByDate(LocalDate date);

    AttendanceResponse updateAttendance(Long id, AttendanceRequest request,
                                        String updatedByEmail);
}