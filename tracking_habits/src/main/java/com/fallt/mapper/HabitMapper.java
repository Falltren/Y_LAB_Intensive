package com.fallt.mapper;

import com.fallt.dto.request.UpsertHabitRequest;
import com.fallt.dto.response.HabitExecutionResponse;
import com.fallt.dto.response.HabitResponse;
import com.fallt.entity.Habit;
import com.fallt.entity.HabitExecution;
import org.mapstruct.*;

import java.util.List;

import static org.mapstruct.factory.Mappers.getMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HabitMapper {

    HabitMapper INSTANCE = getMapper(HabitMapper.class);

    @Mapping(target = "createAt", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "executionRate", expression = "java(com.fallt.entity.ExecutionRate.valueOf(request.getRate()))")
    Habit toEntity(UpsertHabitRequest request);

    HabitResponse toResponse(Habit habit);

    List<HabitResponse> toResponseList(List<Habit> habits);

    HabitExecutionResponse toExecutionResponse(HabitExecution execution);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateHabitFromDto(UpsertHabitRequest request, @MappingTarget Habit habit);
}
