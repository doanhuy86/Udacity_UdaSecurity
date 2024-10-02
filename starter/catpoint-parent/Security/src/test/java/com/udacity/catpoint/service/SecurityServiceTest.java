package com.udacity.catpoint.service;

import com.udacity.catpoint.Image.ImageService;
import com.udacity.catpoint.data.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    @Mock
    private ImageService imageService;
    @Mock
    private SecurityRepository securityRepository;
    private SecurityService securityService;
    private Sensor sensor;
    @Mock
    private BufferedImage image;

    @BeforeEach
    public void init() {
        securityService = new SecurityService(securityRepository, imageService);
        sensor = new Sensor(UUID.randomUUID().toString(), SensorType.DOOR);
    }
    //Test1
    @Test
    public void alarmArmed_sensorActivated_changeAlarmStatusPending() {
        //alarm is armed
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        //sensor become activated
        securityService.changeSensorActivationStatus(sensor, true);
        //put the system into pending alarm status
        verify(securityRepository).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }
    //Test2
    @Test
    public void alarmArmed_sensorActivated_pendingAlarm_changeAlarmStatusAlarm() {
        //alarm is armed
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        //system is pending
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        //sensor become activated
        securityService.changeSensorActivationStatus(sensor, true);
        //then put the system into alarm status
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }
    //Test3
    @Test
    public void alarmPending_sensorInactive_changeAlarmStatusNoAlarm(){
        //system is pending
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        //alarm is armed
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        //sensor become Inactive
        securityService.changeSensorActivationStatus(sensor, true);
        securityService.changeSensorActivationStatus(sensor, false);
        //then put the system into alarm status
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }
    //Test4
    @ParameterizedTest
    @EnumSource(value = AlarmStatus.class, names = {"NO_ALARM", "PENDING_ALARM", "ALARM"})
    public void alarmActive_changeSensorState_alarmStateRemain() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor, true);
        //securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository, times(0)).setAlarmStatus(any(AlarmStatus.class));
    }
    //Test5
    @Test
    public void sensorActivatedWhileActive_alarmPending_changeAlarmState() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        //activated
        securityService.changeSensorActivationStatus(sensor, true);
        sensor.setActive(true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    //Test6
    @ParameterizedTest
    @EnumSource(value = AlarmStatus.class, names = {"NO_ALARM", "PENDING_ALARM", "ALARM"})
    public void sensorDeactivatedWhileInactive_noChangeAlarmState() {
        sensor.setActive(false);
        //deactivated
        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository, times(0)).setAlarmStatus(any(AlarmStatus.class));
    }
    //Test7
    @Test
    public void catDetected_alarmArmed_changeAlarmStatusAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(any(), ArgumentMatchers.anyFloat())).thenReturn(true);
        securityService.processImage(image);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }
    //Test8
    @Test
    public void catNotDetected_sensorNotActivated_changeAlarmStatusNoAlarm() {
        sensor.setActive(false);
        when(imageService.imageContainsCat(any(), ArgumentMatchers.anyFloat())).thenReturn(false);
        securityService.processImage(image);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }
    //Test9
    @Test
    public void systemDisarm_changeAlarmStatusNoAlarm() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }
    //Test10
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_AWAY", "ARMED_HOME"})
    public void systemArmed_changeSensorInactive(ArmingStatus status) {
        Set<Sensor> sensors = new HashSet<>();
        sensors.add(sensor);
        when(securityRepository.getSensors()).thenReturn(sensors);
        securityService.setArmingStatus(status);
        securityService.getSensors().forEach(
                s -> Assertions.assertFalse(s.getActive()));
    }
    //Test11
    @Test
    public void systemArmed_catDetected_changeAlarmStatusAlarm() {
        //when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(any(), ArgumentMatchers.anyFloat())).thenReturn(true);
        securityService.processImage(mock(BufferedImage.class));
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    //fix line116
//    @Test
//    public void alarmAlarm_sensorInactive_changeAlarmStatusPendingAlarm() {
//        //system is pending
//        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
//        //alarm is armed
//        //when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
//        //sensor become Inactive
//        securityService.changeSensorActivationStatus(sensor, true);
//        securityService.changeSensorActivationStatus(sensor, false);
//        //then put the system into alarm status
//        verify(securityRepository).setAlarmStatus(AlarmStatus.PENDING_ALARM);
//    }
}
