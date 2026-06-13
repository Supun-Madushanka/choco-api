package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.ShiftRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.ShiftUpdateRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.ShiftResponse;

import java.util.List;

public interface ShiftService {

    List<ShiftResponse> getAllShifts();

    List<ShiftResponse> getActiveShifts();

    ShiftResponse getShiftById(Long id);

    ShiftResponse createShift(ShiftRequest request);

    ShiftResponse updateShift(Long id, ShiftUpdateRequest request);

    void deleteShift(Long id);
}
