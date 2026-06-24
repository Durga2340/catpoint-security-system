package com.udacity.catpoint.security;

import com.udacity.catpoint.data.*;
import com.udacity.catpoint.service.ImageService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityServiceIntegrationTest {

    @Test
    void armedSensorActivated_setsPendingAlarm() {

        FakeSecurityRepository repository =
                new FakeSecurityRepository();

        ImageService imageService =
                mock(ImageService.class);

        SecurityService service =
                new SecurityService(repository, imageService);

        Sensor sensor =
                new Sensor("Door", SensorType.DOOR);

        repository.addSensor(sensor);

        service.setArmingStatus(ArmingStatus.ARMED_HOME);
        service.changeSensorActivationStatus(sensor, true);

        assertEquals(
                AlarmStatus.PENDING_ALARM,
                repository.getAlarmStatus());
    }

    @Test
    void pendingAlarm_sensorActivated_setsAlarm() {

        FakeSecurityRepository repository =
                new FakeSecurityRepository();

        ImageService imageService =
                mock(ImageService.class);

        SecurityService service =
                new SecurityService(repository, imageService);

        Sensor sensor =
                new Sensor("Door", SensorType.DOOR);

        repository.addSensor(sensor);

        service.setArmingStatus(ArmingStatus.ARMED_HOME);

        service.changeSensorActivationStatus(sensor, true);
        service.changeSensorActivationStatus(sensor, true);

        assertEquals(
                AlarmStatus.ALARM,
                repository.getAlarmStatus());
    }

    @Test
    void disarmingSystem_setsNoAlarm() {

        FakeSecurityRepository repository =
                new FakeSecurityRepository();

        ImageService imageService =
                mock(ImageService.class);

        SecurityService service =
                new SecurityService(repository, imageService);

        repository.setAlarmStatus(AlarmStatus.ALARM);

        service.setArmingStatus(ArmingStatus.DISARMED);

        assertEquals(
                AlarmStatus.NO_ALARM,
                repository.getAlarmStatus());
    }

    @Test
    void armingSystem_resetsSensors() {

        FakeSecurityRepository repository =
                new FakeSecurityRepository();

        ImageService imageService =
                mock(ImageService.class);

        SecurityService service =
                new SecurityService(repository, imageService);

        Sensor sensor =
                new Sensor("Door", SensorType.DOOR);

        sensor.setActive(true);

        repository.addSensor(sensor);

        service.setArmingStatus(ArmingStatus.ARMED_HOME);

        assertFalse(sensor.getActive());
    }

    @Test
    void activeSensorActivatedAgain_setsAlarm() {

        FakeSecurityRepository repository =
                new FakeSecurityRepository();

        ImageService imageService =
                mock(ImageService.class);

        SecurityService service =
                new SecurityService(repository, imageService);

        Sensor sensor =
                new Sensor("Door", SensorType.DOOR);

        repository.addSensor(sensor);

        repository.setAlarmStatus(
                AlarmStatus.PENDING_ALARM);

        sensor.setActive(true);

        service.changeSensorActivationStatus(sensor, true);

        assertEquals(
                AlarmStatus.ALARM,
                repository.getAlarmStatus());
    }
}