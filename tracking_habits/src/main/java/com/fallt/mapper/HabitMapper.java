package com.fallt.mapper;

import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.response.HabitExecutionResponse;
import com.fallt.domain.dto.response.HabitResponse;
import com.fallt.domain.entity.Habit;
import com.fallt.domain.entity.HabitExecution;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HabitMapper {

    HabitMapper INSTANCE = getMapper(HabitMapper.class);

    @Mapping(target = "createAt", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "executionRate", expression = "java(com.fallt.domain.entity.enums.ExecutionRate.valueOf(request.getRate()))")
    Habit toEntity(UpsertHabitRequest request);

    HabitResponse toResponse(Habit habit);

    List<HabitResponse> toResponseList(List<Habit> habits);

    HabitExecutionResponse toExecutionResponse(HabitExecution execution);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateHabitFromDto(UpsertHabitRequest request, @MappingTarget Habit habit);
}
