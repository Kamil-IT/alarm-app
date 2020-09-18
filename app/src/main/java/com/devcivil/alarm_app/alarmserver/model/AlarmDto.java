package com.devcivil.alarm_app.alarmserver.model;

import java.util.List;
import java.util.Objects;

public class AlarmDto {

    private String id;

    private String name;
    private String description;

    private Time time;

    private Long timeCreateInMillis;

    private RingType ringType;

    private List<AlarmFrequencyType> alarmFrequencyType;

    private Boolean isActive;

    /**
     * Only not empty if alarmFrequencyType = costume
     */
    private List<Date> alarmFrequencyCostume;


    private TurnOffType alarmTurnOffType;

    private Snooze snooze;

    public AlarmDto() {
    }

    public AlarmDto(String id, String name, String description, Time time, Long timeCreateInMillis, RingType ringType, List<AlarmFrequencyType> alarmFrequencyType, Boolean isActive, List<Date> alarmFrequencyCostume, TurnOffType alarmTurnOffType, Snooze snooze) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = time;
        this.timeCreateInMillis = timeCreateInMillis;
        this.ringType = ringType;
        this.alarmFrequencyType = alarmFrequencyType;
        this.isActive = isActive;
        this.alarmFrequencyCostume = alarmFrequencyCostume;
        this.alarmTurnOffType = alarmTurnOffType;
        this.snooze = snooze;
    }

    public AlarmDto(String name, Time time, RingType ringType, List<AlarmFrequencyType> alarmFrequencyType, Boolean isActive, List<Date> alarmFrequencyCostume, TurnOffType alarmTurnOffType, Snooze snooze) {
        this.name = name;
        this.time = time;
        this.ringType = ringType;
        this.alarmFrequencyType = alarmFrequencyType;
        this.isActive = isActive;
        this.alarmFrequencyCostume = alarmFrequencyCostume;
        this.alarmTurnOffType = alarmTurnOffType;
        this.snooze = snooze;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Long getTimeCreateInMillis() {
        return timeCreateInMillis;
    }

    public void setTimeCreateInMillis(Long timeCreateInMillis) {
        this.timeCreateInMillis = timeCreateInMillis;
    }

    public RingType getRingType() {
        return ringType;
    }

    public void setRingType(RingType ringType) {
        this.ringType = ringType;
    }

    public List<AlarmFrequencyType> getAlarmFrequencyType() {
        return alarmFrequencyType;
    }

    public void setAlarmFrequencyType(List<AlarmFrequencyType> alarmFrequencyType) {
        this.alarmFrequencyType = alarmFrequencyType;
    }

    public Snooze getSnooze() {
        return snooze;
    }

    public void setSnooze(Snooze snooze) {
        this.snooze = snooze;
    }

    public List<Date> getAlarmFrequencyCostume() {
        return alarmFrequencyCostume;
    }

    public void setAlarmFrequencyCostume(List<Date> alarmFrequencyCostume) {
        this.alarmFrequencyCostume = alarmFrequencyCostume;
    }

    public TurnOffType getAlarmTurnOffType() {
        return alarmTurnOffType;
    }

    public void setAlarmTurnOffType(TurnOffType alarmTurnOffType) {
        this.alarmTurnOffType = alarmTurnOffType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmDto alarmDto = (AlarmDto) o;
        return Objects.equals(id, alarmDto.id) &&
                Objects.equals(name, alarmDto.name) &&
                Objects.equals(description, alarmDto.description) &&
                Objects.equals(time, alarmDto.time) &&
                Objects.equals(timeCreateInMillis, alarmDto.timeCreateInMillis) &&
                ringType == alarmDto.ringType &&
                Objects.equals(alarmFrequencyType, alarmDto.alarmFrequencyType) &&
                Objects.equals(isActive, alarmDto.isActive) &&
                Objects.equals(alarmFrequencyCostume, alarmDto.alarmFrequencyCostume) &&
                alarmTurnOffType == alarmDto.alarmTurnOffType &&
                snooze == alarmDto.snooze;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, time, timeCreateInMillis, ringType, alarmFrequencyType, isActive, alarmFrequencyCostume, alarmTurnOffType, snooze);
    }
}
