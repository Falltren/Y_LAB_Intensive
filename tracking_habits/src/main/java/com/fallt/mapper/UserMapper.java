package com.fallt.mapper;

import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.domain.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "isBlocked", constant = "false")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "role", expression = "java(com.fallt.domain.entity.enums.Role.ROLE_USER)")
    User toEntity(UpsertUserRequest request);

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpsertUserRequest request, @MappingTarget User user);

}
