package com.devcivil.alarm_app.alarmserver.model;

import java.util.Date;
import java.util.Objects;

public class AlarmFor14Days {

    private String idMainAlarm;

    private String name;

    private Date alarmBe;

    private TurnOffType alarmTurnOffType;

    private Snooze snooze;

    private RingType ringType;

    private Boolean isActive;

    public AlarmFor14Days() {
    }

    public AlarmFor14Days(String idMainAlarm, String name, TurnOffType alarmTurnOffType, Snooze snooze, Boolean isActive) {
        this.idMainAlarm = idMainAlarm;
        this.name = name;
        this.alarmTurnOffType = alarmTurnOffType;
        this.snooze = snooze;
        this.isActive = isActive;
    }

    public AlarmFor14Days(String idMainAlarm, String name, TurnOffType alarmTurnOffType, Snooze snooze, Boolean isActive, Date alarmBe, RingType ringType) {
        this.idMainAlarm = idMainAlarm;
        this.name = name;
        this.alarmBe = alarmBe;
        this.alarmTurnOffType = alarmTurnOffType;
        this.snooze = snooze;
        this.ringType = ringType;
        this.isActive = isActive;
    }

    public String getIdMainAlarm() {
        return idMainAlarm;
    }

    public void setIdMainAlarm(String idMainAlarm) {
        this.idMainAlarm = idMainAlarm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAlarmBe() {
        return alarmBe;
    }

    public void setAlarmBe(Date alarmBe) {
        this.alarmBe = alarmBe;
    }

    public TurnOffType getAlarmTurnOffType() {
        return alarmTurnOffType;
    }

    public void setAlarmTurnOffType(TurnOffType alarmTurnOffType) {
        this.alarmTurnOffType = alarmTurnOffType;
    }

    public Snooze getSnooze() {
        return snooze;
    }

    public void setSnooze(Snooze snooze) {
        this.snooze = snooze;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public RingType getRingType() {
        return ringType;
    }

    public void setRingType(RingType ringType) {
        this.ringType = ringType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmFor14Days alarm = (AlarmFor14Days) o;
        return Objects.equals(idMainAlarm, alarm.idMainAlarm) &&
                Objects.equals(name, alarm.name) &&
                Objects.equals(alarmBe, alarm.alarmBe) &&
                alarmTurnOffType == alarm.alarmTurnOffType &&
                snooze == alarm.snooze &&
                ringType == alarm.ringType &&
                Objects.equals(isActive, alarm.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMainAlarm, name, alarmBe, alarmTurnOffType, snooze, ringType, isActive);
    }

    @Override
    public String toString() {
        return "AlarmFor14Days{" +
                "idMainAlarm='" + idMainAlarm + '\'' +
                ", name='" + name + '\'' +
                ", alarmBe=" + alarmBe +
                ", alarmTurnOffType=" + alarmTurnOffType +
                ", snooze=" + snooze +
                ", ringType=" + ringType +
                ", isActive=" + isActive +
                '}';
    }
}
