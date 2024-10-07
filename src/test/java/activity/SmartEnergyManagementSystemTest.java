package activity;

import org.junit.Before;
import org.junit.Test;

import activity.SmartEnergyManagementSystem.EnergyManagementResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SmartEnergyManagementSystemTest {
    private SmartEnergyManagementSystem energyManagementSystem;

    @Before
    public void setUp(){
        energyManagementSystem = new SmartEnergyManagementSystem();
    }

    @Test
    public void shouldBeAbleToActivateEnergySavingModeIfPriceExceedsThresholdKeepingHighPriorityDevicesOn(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.25,
            0.20,
            Map.of(
                "Heating", 1,
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.now(),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            null
        );
    }

    @Test
    public void shouldBeAbleToActivateEnergySavingModeIfPriceExceedsThresholdTurningOffLowPriorityDevices(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.25,
            0.20,
            Map.of(
                "Heating", 1,
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.now(),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            null
        );
    }

    @Test
    public void shouldNotActivateEnergySavingModeIfPriceDoesNotExceedsThreshold(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Heating", 1,
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 23, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            null
        );
    }

    @Test
    public void shouldTurnOnNightModeForDevicesOtherThanSecurityAndRefrigerator(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Security", 1,
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 23, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            null
        );
    }

    @Test
    public void shouldKeepSecurityAndRefrigeratorDevicesOnDuringNight(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Security", 1,
                "Refrigerator", 1
            ),
            LocalDateTime.of(2024, 10, 7, 23, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            null
        );
    }

    @Test
    public void shouldTurnHeatingSystemOnIfCurrentTemperatureIsBelowMinimum(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Heating", 1,
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 23, 30),
            19,
            new double[]{20.0, 24.0},
            30,
            25,
            null
        );
    }

    @Test
    public void shouldTurnCoolingystemOnIfCurrentTemperatureIsAboveMaximum(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Heating", 1,
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 23, 30),
            25,
            new double[]{20.0, 24.0},
            30,
            25,
            null
        );
    }
}
