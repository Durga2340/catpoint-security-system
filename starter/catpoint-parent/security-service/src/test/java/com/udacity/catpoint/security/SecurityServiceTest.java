package com.udacity.catpoint.security;

import com.udacity.catpoint.data.AlarmStatus;
import com.udacity.catpoint.data.ArmingStatus;
import com.udacity.catpoint.data.SecurityRepository;
import com.udacity.catpoint.data.Sensor;
import com.udacity.catpoint.data.SensorType;
import com.udacity.catpoint.service.ImageService;
import java.awt.image.BufferedImage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {


    @Mock
    private SecurityRepository securityRepository;

    @Mock
    private ImageService imageService;

    // Requirement 1
    @Test
    void armedSensorActivated_setsPendingAlarm() {

        Sensor sensor = new Sensor("Door", SensorType.DOOR);

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_HOME);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.NO_ALARM);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    // Requirement 2
    @Test
    void armedSensorActivated_whenPendingAlarm_setsAlarm() {

        Sensor sensor = new Sensor("Door", SensorType.DOOR);

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_HOME);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.ALARM);
    }

    // Requirement 3
    @Test
    void pendingAlarm_allSensorsInactive_setsNoAlarm() {

        Sensor sensor = new Sensor("Door", SensorType.DOOR);
        sensor.setActive(true);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    // Requirement 4
    @Test
    void alarmState_sensorChange_doesNothing() {

        Sensor sensor = new Sensor("Door", SensorType.DOOR);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.ALARM);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, never())
                .setAlarmStatus(any());
    }

    // Requirement 5
    @Test
    void activeSensorActivatedAgain_pendingAlarm_setsAlarm() {

        Sensor sensor = new Sensor("Door", SensorType.DOOR);
        sensor.setActive(true);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.ALARM);
    }

    // Requirement 6
    @Test
    void inactiveSensorDeactivatedAgain_noChange() {

        Sensor sensor = new Sensor("Door", SensorType.DOOR);
        sensor.setActive(false);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository, never())
                .setAlarmStatus(any());
    }

    // Requirement 7
    @Test
    void catDetected_armedHome_setsAlarm() {

        BufferedImage image =
                new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_HOME);

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(true);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.processImage(image);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.ALARM);
    }

    // Requirement 8
    @Test
    void noCatDetected_setsNoAlarm() {

        BufferedImage image =
                new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(false);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.processImage(image);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.NO_ALARM);
    }


    // Requirement 9
    @Test
    void disarmed_setsNoAlarm() {

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.setArmingStatus(ArmingStatus.DISARMED);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    // Requirement 10
    @Test
    void armingSystem_resetsSensors() {

        Sensor sensor = new Sensor("Door", SensorType.DOOR);
        sensor.setActive(true);

        java.util.Set<Sensor> sensors = new java.util.HashSet<>();
        sensors.add(sensor);

        when(securityRepository.getSensors())
                .thenReturn(sensors);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        assertFalse(sensor.getActive());
    }

    // Requirement 11
    @Test
    void armedHomeWhenCatAlreadyDetected_setsAlarm() {

        BufferedImage image =
                new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(true);

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.ARMED_HOME);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.processImage(image);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.ALARM);
    }
    // Parameterized test
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class,
            names = {"ARMED_HOME", "ARMED_AWAY"})
    void sensorActivated_setsPendingAlarm_forArmedStates(
            ArmingStatus status) {

        Sensor sensor =
                new Sensor("Door", SensorType.DOOR);

        when(securityRepository.getArmingStatus())
                .thenReturn(status);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.NO_ALARM);

        SecurityService securityService =
                new SecurityService(securityRepository, imageService);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

//   spy test
    @Test
    void spyExample() {

        Sensor sensor =
                spy(new Sensor("Door", SensorType.DOOR));

        sensor.setActive(true);

        verify(sensor).setActive(true);
    }
}
