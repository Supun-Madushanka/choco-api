package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ShiftRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.ShiftUpdateRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ShiftResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Shift;
import com.ceylonechocolate.chocolate_factory_api.repository.ShiftRepository;
import com.ceylonechocolate.chocolate_factory_api.service.ShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;

    @Override
    public List<ShiftResponse> getAllShifts() {
        return shiftRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShiftResponse> getActiveShifts() {
        return shiftRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ShiftResponse getShiftById(Long id) {
        return mapToResponse(findShiftById(id));
    }

    @Override
    @Transactional
    public ShiftResponse createShift(ShiftRequest request) {

        if (shiftRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Shift already exists with name: " + request.getName()
            );
        }

        Shift shift = Shift.builder()
                .name(request.getName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .type(Shift.ShiftType.valueOf(request.getType()))
                .description(request.getDescription())
                .isActive(request.getIsActive() != null
                        ? request.getIsActive() : true)
                .build();

        shiftRepository.save(shift);
        log.info("Shift created: {}", shift.getName());

        return mapToResponse(shift);
    }

    @Override
    @Transactional
    public ShiftResponse updateShift(Long id, ShiftUpdateRequest request) {

        Shift shift = findShiftById(id);

        if (!shift.getName().equals(request.getName()) &&
                shiftRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Shift already exists with name: " + request.getName()
            );
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            shift.setName(request.getName());
        }

        if (request.getStartTime() != null) {
            shift.setStartTime(request.getStartTime());
        }

        if (request.getEndTime() != null) {
            shift.setEndTime(request.getEndTime());
        }

        if (request.getType() != null) {
            shift.setType(Shift.ShiftType.valueOf(request.getType()));
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            shift.setDescription(request.getDescription());
        }

        if (request.getIsActive() != null) {
            shift.setIsActive(request.getIsActive());
        }

        shiftRepository.save(shift);
        log.info("Shift updated: {}", shift.getName());

        return mapToResponse(shift);
    }

    @Override
    @Transactional
    public void deleteShift(Long id) {
        Shift shift = findShiftById(id);
        shiftRepository.delete(shift);
        log.info("Shift deleted: {}", shift.getName());
    }

    private Shift findShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Shift not found with id: " + id
                        )
                );
    }

    private ShiftResponse mapToResponse(Shift shift) {
        return ShiftResponse.builder()
                .id(shift.getId())
                .name(shift.getName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .type(shift.getType().name())
                .description(shift.getDescription())
                .isActive(shift.getIsActive())
                .createdAt(shift.getCreatedAt())
                .build();
    }
}