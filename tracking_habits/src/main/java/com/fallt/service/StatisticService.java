package com.fallt.service;

import com.fallt.dto.request.ReportRequest;
import com.fallt.dto.response.ExecutionDto;
import com.fallt.dto.response.HabitProgress;

import java.util.List;

/**
 * Класс для расчета различной статистики по выполнению привычек пользователем
 */
public interface StatisticService {

    /**
     * Получение общей статистики по выполнению привычки пользователем, включая название привычки,
     * % успешного выполнения и серии выполнения
     *
     * @param userEmail Электронный адрес пользователя
     * @param request   Объект, содержащий данные о названии привычки, а также дате начала и окончания отчетного периода
     * @return Прогресс выполнения пользователем привычки
     */
    HabitProgress getHabitProgress(String userEmail, ReportRequest request);

    /**
     * Расчет % успешного выполнения привычки за указанный период
     *
     * @param userEmail Электронный адрес пользователя
     * @param request   Объект, содержащий данные о названии привычки, а также дате начала и окончания отчетного периода
     * @return % успешного выполнения привычки
     */
    int getSuccessHabitRate(String userEmail, ReportRequest request);

    /**
     * Расчет серии выполнения привычки
     *
     * @param userEmail Электронный адрес пользователя
     * @param request   Объект, содержащий данные о названии привычки, а также дате начала и окончания отчетного периода
     * @return Список с данными по выполнению привычки за указанны период
     */
    List<ExecutionDto> getHabitStreak(String userEmail, ReportRequest request);

}
