package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.response.RoleResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Role;
import com.ceylonechocolate.chocolate_factory_api.repository.RoleRepository;
import com.ceylonechocolate.chocolate_factory_api.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleResponse> getRolesByLevel(String level) {
        return roleRepository.findAll()
                .stream()
                .filter(role -> role.getLevel().name()
                        .equalsIgnoreCase(level))
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }

    private RoleResponse mapToRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .displayName(role.getDisplayName())
                .level(role.getLevel().name())
                .description(role.getDescription())
                .build();
    }
}