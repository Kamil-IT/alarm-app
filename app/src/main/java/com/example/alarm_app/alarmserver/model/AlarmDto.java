package com.example.alarm_app.alarmserver.model;

import java.util.List;
import java.util.Objects;

public class AlarmDto {

    private String id;

    private String name;
    private String description;

    private String userId;

    private Time time;

    private Long timeCreateInMillis;

    private String ringType;

    /**
     * Only not null if ringType = costume
     */
    private String ringName;


    private String alarmFrequencyType;

    private Boolean isActive;

    /**
     * Only not empty if alarmFrequencyType = costume
     */
    private List<Date> alarmFrequencyCostume;


    private String alarmTurnOffType;

    public AlarmDto() {
    }

    public AlarmDto(String id, String name, String description, String userId, Time time, Long timeCreateInMillis, String ringType, String ringName, String alarmFrequencyType, Boolean isActive, List<Date> alarmFrequencyCostume, String alarmTurnOffType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.time = time;
        this.timeCreateInMillis = timeCreateInMillis;
        this.ringType = ringType;
        this.ringName = ringName;
        this.alarmFrequencyType = alarmFrequencyType;
        this.isActive = isActive;
        this.alarmFrequencyCostume = alarmFrequencyCostume;
        this.alarmTurnOffType = alarmTurnOffType;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getRingType() {
        return ringType;
    }

    public void setRingType(String ringType) {
        this.ringType = ringType;
    }

    public String getRingName() {
        return ringName;
    }

    public void setRingName(String ringName) {
        this.ringName = ringName;
    }

    public String getAlarmFrequencyType() {
        return alarmFrequencyType;
    }

    public void setAlarmFrequencyType(String alarmFrequencyType) {
        this.alarmFrequencyType = alarmFrequencyType;
    }

    public List<Date> getAlarmFrequencyCostume() {
        return alarmFrequencyCostume;
    }

    public void setAlarmFrequencyCostume(List<Date> alarmFrequencyCostume) {
        this.alarmFrequencyCostume = alarmFrequencyCostume;
    }

    public String getAlarmTurnOffType() {
        return alarmTurnOffType;
    }

    public void setAlarmTurnOffType(String alarmTurnOffType) {
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
                Objects.equals(userId, alarmDto.userId) &&
                Objects.equals(time, alarmDto.time) &&
                Objects.equals(timeCreateInMillis, alarmDto.timeCreateInMillis) &&
                Objects.equals(ringType, alarmDto.ringType) &&
                Objects.equals(ringName, alarmDto.ringName) &&
                Objects.equals(alarmFrequencyType, alarmDto.alarmFrequencyType) &&
                Objects.equals(alarmFrequencyCostume, alarmDto.alarmFrequencyCostume) &&
                Objects.equals(alarmTurnOffType, alarmDto.alarmTurnOffType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, userId, time, timeCreateInMillis, ringType, ringName, alarmFrequencyType, alarmFrequencyCostume, alarmTurnOffType);
    }
}
