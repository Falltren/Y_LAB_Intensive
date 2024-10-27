package com.fallt.aop.audit;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.AuthService.login(..))")
    public void loginUser() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.UserService.saveUser(..))")
    public void registerUser() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.UserService.get*(..))")
    public void getUser() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.UserService.updateUser(..))")
    public void updateUser() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.UserService.deleteUser(..))")
    public void deleteUser() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.HabitService.saveHabit(..))")
    public void createHabit() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.HabitService.updateHabit(..))")
    public void updateHabit() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.HabitService.deleteHabit(..))")
    public void deleteHabit() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.HabitService.getAllHabits(..))")
    public void getAllHabits() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.HabitService.confirmHabit(..))")
    public void confirmHabit() {
    }

    @Pointcut("within(@com.fallt.aop.audit.Auditable *) && execution(* com.fallt.service.StatisticService.getHabitProgress(..))")
    public void getReport() {
    }


}
