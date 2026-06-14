package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.AttendanceRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.AttendanceResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Attendance;
import com.ceylonechocolate.chocolate_factory_api.entity.Employee;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.AttendanceRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.EmployeeRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    // Considered "late" if check-in after this time
    private static final LocalTime LATE_THRESHOLD = LocalTime.of(9, 15);

    @Override
    @Transactional
    public AttendanceResponse checkIn(String email) {

        Employee employee = getEmployeeByUserEmail(email);
        User user = getUserByEmail(email);

        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByEmployeeIdAndWorkDate(
                employee.getId(), today)) {
            throw new IllegalArgumentException(
                    "You have already checked in today"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Attendance.AttendanceStatus status =
                now.toLocalTime().isAfter(LATE_THRESHOLD)
                        ? Attendance.AttendanceStatus.LATE
                        : Attendance.AttendanceStatus.PRESENT;

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .workDate(today)
                .checkIn(now)
                .status(status)
                .markedBy(user)
                .build();

        attendanceRepository.save(attendance);
        log.info("Employee {} checked in at {}",
                employee.getEmployeeNo(), now);

        return mapToResponse(attendance);
    }

    @Override
    @Transactional
    public AttendanceResponse checkOut(String email) {

        Employee employee = getEmployeeByUserEmail(email);
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndWorkDate(employee.getId(), today)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "You have not checked in today"
                        )
                );

        if (attendance.getCheckOut() != null) {
            throw new IllegalArgumentException(
                    "You have already checked out today"
            );
        }

        attendance.setCheckOut(LocalDateTime.now());
        attendanceRepository.save(attendance);

        log.info("Employee {} checked out at {}",
                employee.getEmployeeNo(), attendance.getCheckOut());

        return mapToResponse(attendance);
    }

    @Override
    public AttendanceResponse getMyTodayAttendance(String email) {
        Employee employee = getEmployeeByUserEmail(email);
        LocalDate today = LocalDate.now();

        return attendanceRepository
                .findByEmployeeIdAndWorkDate(employee.getId(), today)
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Override
    public List<AttendanceResponse> getMyAttendance(
            String email, LocalDate startDate, LocalDate endDate) {

        Employee employee = getEmployeeByUserEmail(email);

        return attendanceRepository
                .findByEmployeeIdAndWorkDateBetweenOrderByWorkDateDesc(
                        employee.getId(), startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceByEmployee(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByWorkDate(date)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(
            Long id, AttendanceRequest request, String updatedByEmail) {

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Attendance record not found")
                );

        if (request.getCheckIn() != null) {
            attendance.setCheckIn(request.getCheckIn());
        }
        if (request.getCheckOut() != null) {
            attendance.setCheckOut(request.getCheckOut());
        }
        if (request.getStatus() != null) {
            attendance.setStatus(
                    Attendance.AttendanceStatus.valueOf(request.getStatus())
            );
        }
        if (request.getNote() != null) {
            attendance.setNote(request.getNote());
        }

        User updatedBy = getUserByEmail(updatedByEmail);
        attendance.setMarkedBy(updatedBy);

        attendanceRepository.save(attendance);
        log.info("Attendance updated for employee: {}",
                attendance.getEmployee().getEmployeeNo());

        return mapToResponse(attendance);
    }

    private Employee getEmployeeByUserEmail(String email) {
        User user = getUserByEmail(email);
        return employeeRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No employee profile found for this account"
                        )
                );
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );
    }

    private AttendanceResponse mapToResponse(Attendance a) {
        return AttendanceResponse.builder()
                .id(a.getId())
                .employeeId(a.getEmployee().getId())
                .employeeNo(a.getEmployee().getEmployeeNo())
                .employeeName(a.getEmployee().getFullName())
                .workDate(a.getWorkDate())
                .checkIn(a.getCheckIn())
                .checkOut(a.getCheckOut())
                .status(a.getStatus().name())
                .markedByName(a.getMarkedBy().getFullName())
                .note(a.getNote())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}