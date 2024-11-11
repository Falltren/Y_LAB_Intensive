package com.fallt.service;

import com.fallt.domain.dto.request.ReportRequest;
import com.fallt.domain.dto.response.ExecutionDto;
import com.fallt.domain.dto.response.HabitProgress;

import java.util.List;

/**
 * Интерфейс, предназначенный для расчета различной статистики по выполнению привычек пользователем
 */
public interface StatisticService {

    /**
     * Получение общей статистики по выполнению привычки пользователем, включая название привычки,
     * % успешного выполнения и серии выполнения
     *
     * @param request   Объект, содержащий данные об идентификаторе привычки, а также дате начала и окончания отчетного периода
     * @return Прогресс выполнения пользователем привычки
     */
    HabitProgress getHabitProgress(ReportRequest request);

    /**
     * Расчет % успешного выполнения привычки за указанный период
     *
     * @param request   Объект, содержащий данные об идентификаторе привычки, а также дате начала и окончания отчетного периода
     * @return % успешного выполнения привычки
     */
    int getSuccessHabitRate(ReportRequest request);

    /**
     * Расчет серии выполнения привычки
     *
     * @param request   Объект, содержащий данные об идентификаторе привычки, а также дате начала и окончания отчетного периода
     * @return Список с данными по выполнению привычки за указанный период
     */
    List<ExecutionDto> getHabitStreak(ReportRequest request);

}
