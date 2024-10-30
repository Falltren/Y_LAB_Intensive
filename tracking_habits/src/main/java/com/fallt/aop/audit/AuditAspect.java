package com.fallt.aop.audit;

import com.fallt.dto.request.HabitConfirmRequest;
import com.fallt.dto.request.LoginRequest;
import com.fallt.dto.request.UpsertUserRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {

    private final AuditWriter writer;


    public AuditAspect() {
        writer = new AuditWriter();
    }

    @Before("Pointcuts.loginUser()")
    public void beforeLogin(JoinPoint joinPoint) {
        LoginRequest parameter = (LoginRequest) joinPoint.getArgs()[0];
        String userEmail = parameter.getEmail();
        Audit audit = new Audit("Пользователь " + userEmail, "Вход пользователя", LocalDateTime.now());
        writer.write(audit);
    }

    @Before("Pointcuts.registerUser()")
    public void beforeRegister(JoinPoint joinPoint) {
        UpsertUserRequest parameter = (UpsertUserRequest) joinPoint.getArgs()[0];
        String userEmail = parameter.getEmail();
        Audit audit = new Audit("Используемый email " + userEmail, "Регистрация пользователя", LocalDateTime.now());
        writer.write(audit);
    }

    @Before("Pointcuts.getUser()")
    public void beforeGetMethods(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Audit audit = null;
        if (methodName.contains("getAll")) {
            audit = new Audit("", "Получение данных о пользователях", LocalDateTime.now());
        }
        if (methodName.contains("getUser")) {
            String email = (String) joinPoint.getArgs()[0];
            audit = new Audit("Аргумент метода: " + email, "Получение пользователя по email", LocalDateTime.now());
        }
        writer.write(audit);
    }

    @Before("Pointcuts.updateUser()")
    public void beforeUpdateMethod(JoinPoint joinPoint) {
        String userEmail = (String) joinPoint.getArgs()[0];
        Audit audit = new Audit("Используемы email " + userEmail, "Обновление данных о пользователе", LocalDateTime.now());
        writer.write(audit);
    }

    @Before("Pointcuts.deleteUser()")
    public void beforeDeleteMethod(JoinPoint joinPoint){
        String userEmail = (String) joinPoint.getArgs()[0];
        Audit audit = new Audit("Используемый email: " + userEmail, "Удаление пользователя", LocalDateTime.now());
        writer.write(audit);
    }

    @Before("Pointcuts.createHabit()")
    public void beforeCreateHabit(JoinPoint joinPoint){
        String userEmail = (String) joinPoint.getArgs()[0];
        Audit audit = new Audit("Пользователь с email: " + userEmail, "Создание привычки", LocalDateTime.now());
        writer.write(audit);
    }

    @Before("Pointcuts.updateHabit()")
    public void beforeUpdateHabit(JoinPoint joinPoint){
        String userEmail = (String) joinPoint.getArgs()[0];
        String title = (String) joinPoint.getArgs()[1];
        Audit audit = new Audit("Пользователь с email: " + userEmail, "Обновление привычки с названием: " + title, LocalDateTime.now());
        writer.write(audit);
    }

    @Before("Pointcuts.deleteHabit()")
    public void beforeDeleteHabit(JoinPoint joinPoint){
        String userEmail = (String) joinPoint.getArgs()[0];
        String title = (String) joinPoint.getArgs()[1];
        Audit audit = new Audit("Пользователь с email: " + userEmail, "Удаление привычки с названием: " + title, LocalDateTime.now());
        writer.write(audit);
    }

    @Before("Pointcuts.getAllHabits()")
    public void beforeGetAllHabits(JoinPoint joinPoint){
        String userEmail = (String) joinPoint.getArgs()[0];
        Audit audit = new Audit("Пользователь с email: " + userEmail, "Получение всех привычек", LocalDateTime.now());
        writer.write(audit);
    }

    @Before("Pointcuts.confirmHabit()")
    public void beforeConfirmHabit(JoinPoint joinPoint){
        String userEmail = (String) joinPoint.getArgs()[0];
        HabitConfirmRequest request = (HabitConfirmRequest) joinPoint.getArgs()[1];
        Audit audit = new Audit("Пользователь с email: " + userEmail, "Подтверждение выполнения привычки: " + request.getTitle(), LocalDateTime.now());
        writer.write(audit);
    }

    @Before("Pointcuts.getReport()")
    public void beforeGetReport(JoinPoint joinPoint){
        String userEmail = (String) joinPoint.getArgs()[0];
        Audit audit = new Audit("Пользователь с email: " + userEmail, "Получение полного отчета по привычкам", LocalDateTime.now());
        writer.write(audit);
    }

}
