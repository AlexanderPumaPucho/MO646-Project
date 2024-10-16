package activity;

import org.junit.Before;
import org.junit.Test;

import activity.SmartEnergyManagementSystem.EnergyManagementResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                "Processor", 1
            ),
            LocalDateTime.of(2024, 10, 7, 10, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            new ArrayList<>()
        );

        assertEquals(result.energySavingMode, true);
        assertEquals(result.deviceStatus.get("Processor"), true);
        assertEquals(result.temperatureRegulationActive, false);
        assertEquals(result.deviceStatus.get("Heating"), false);
        assertEquals(result.deviceStatus.get("Cooling"), false);
        assertTrue(result.totalEnergyUsed == 25);
    }

    @Test
    public void shouldBeAbleToActivateEnergySavingModeIfPriceExceedsThresholdTurningOffLowPriorityDevices(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.25,
            0.20,
            Map.of(
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 10, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            new ArrayList<>()
        );

        assertEquals(result.energySavingMode, true);
        assertEquals(result.deviceStatus.get("Lights"), false);
        assertEquals(result.deviceStatus.get("Appliances"), false);
        assertEquals(result.temperatureRegulationActive, false);
        assertEquals(result.deviceStatus.get("Heating"), false);
        assertEquals(result.deviceStatus.get("Cooling"), false);
        assertTrue(result.totalEnergyUsed == 25);
    }

    @Test
    public void shouldNotActivateEnergySavingModeIfPriceDoesNotExceedsThreshold(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Processor", 1,
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 10, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            new ArrayList<>()
        );

        assertEquals(result.energySavingMode, false);
        assertEquals(result.deviceStatus.get("Processor"), true);
        assertEquals(result.deviceStatus.get("Lights"), true);
        assertEquals(result.deviceStatus.get("Appliances"), true);
        assertEquals(result.temperatureRegulationActive, false);
        assertEquals(result.deviceStatus.get("Heating"), false);
        assertEquals(result.deviceStatus.get("Cooling"), false);
        assertTrue(result.totalEnergyUsed == 25);
    }

    @Test
    public void shouldTurnOnNightModeAndTurnOffDevicesOtherThanSecurityAndRefrigerator(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 23, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            new ArrayList<>()
        );

        assertEquals(result.energySavingMode, false);
        assertEquals(result.deviceStatus.get("Lights"), false);
        assertEquals(result.deviceStatus.get("Appliances"), false);
        assertEquals(result.temperatureRegulationActive, false);
        assertEquals(result.deviceStatus.get("Heating"), false);
        assertEquals(result.deviceStatus.get("Cooling"), false);
        assertTrue(result.totalEnergyUsed == 25);
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
            new ArrayList<>()
        );

        assertEquals(result.energySavingMode, false);
        assertEquals(result.deviceStatus.get("Security"), true);
        assertEquals(result.deviceStatus.get("Refrigerator"), true);
        assertEquals(result.temperatureRegulationActive, false);
        assertEquals(result.deviceStatus.get("Heating"), false);
        assertEquals(result.deviceStatus.get("Cooling"), false);
        assertTrue(result.totalEnergyUsed == 25);
    }

    @Test
    public void shouldTurnHeatingSystemOnIfCurrentTemperatureIsBelowMinimum(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 10, 30),
            19,
            new double[]{20.0, 24.0},
            30,
            25,
            new ArrayList<>()
        );

        assertEquals(result.energySavingMode, false);
        assertEquals(result.deviceStatus.get("Lights"), true);
        assertEquals(result.deviceStatus.get("Appliances"), true);
        assertEquals(result.temperatureRegulationActive, true);
        assertEquals(result.deviceStatus.get("Heating"), true);
        assertTrue(result.totalEnergyUsed == 25);
    }

    @Test
    public void shouldTurnCoolingystemOnIfCurrentTemperatureIsAboveMaximum(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 10, 30),
            25,
            new double[]{20.0, 24.0},
            30,
            25,
            new ArrayList<>()
        );

        assertEquals(result.energySavingMode, false);
        assertEquals(result.deviceStatus.get("Lights"), true);
        assertEquals(result.deviceStatus.get("Appliances"), true);
        assertEquals(result.temperatureRegulationActive, true);
        assertEquals(result.deviceStatus.get("Cooling"), true);
        assertTrue(result.totalEnergyUsed == 25);
    }

    // loop infinito
    @Test
    public void shouldKeepHighPriorityDevicesOnIfDailyEnergyUsageIsGreaterThanOrEqualTheLimit(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Processor", 1,
                "Lights", 2
            ),
            LocalDateTime.of(2024, 10, 7, 10, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            30,
            new ArrayList<>()
        );

        assertEquals(result.energySavingMode, false);
        assertEquals(result.deviceStatus.get("Processor"), true);
        assertEquals(result.temperatureRegulationActive, false);
        assertEquals(result.deviceStatus.get("Heating"), false);
        assertEquals(result.deviceStatus.get("Cooling"), false);
        assertTrue(result.totalEnergyUsed == 29);
    }

    @Test
    public void shouldTurnLowPriorityDevicesOffIfDailyEnergyUsageIsGreaterThanOrEqualTheLimit(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 10, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            35,
            new ArrayList<>()
        );

        assertEquals(result.energySavingMode, false);
        assertEquals(result.deviceStatus.get("Lights"), false);
        assertEquals(result.deviceStatus.get("Appliances"), false);
        assertEquals(result.temperatureRegulationActive, false);
        assertEquals(result.deviceStatus.get("Heating"), false);
        assertEquals(result.deviceStatus.get("Cooling"), false);
        assertTrue(result.totalEnergyUsed == 33);
    }

    @Test
    public void shouldTurnDeviceOnIfCurrentTimeMatchesScheduleTime(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Processor", 1,
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 10, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            List.of(
                new SmartEnergyManagementSystem.DeviceSchedule("Oven", LocalDateTime.of(2024, 10, 7, 10, 30)),
                new SmartEnergyManagementSystem.DeviceSchedule("Another", LocalDateTime.now().plusHours(1))
            )
        );

        assertEquals(result.energySavingMode, false);
        assertEquals(result.deviceStatus.get("Processor"), true);
        assertEquals(result.deviceStatus.get("Lights"), true);
        assertEquals(result.deviceStatus.get("Appliances"), true);
        assertEquals(result.temperatureRegulationActive, false);
        assertEquals(result.deviceStatus.get("Heating"), false);
        assertEquals(result.deviceStatus.get("Cooling"), false);
        assertTrue(result.totalEnergyUsed == 25);
        assertEquals(result.deviceStatus.get("Oven"), true);
        assertFalse(result.deviceStatus.containsKey("Another"));
    }

    @Test
    public void shouldKeepTheSameStateIfCurrentTimeDoesNotMatchScheduleTime(){
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
            0.15,
            0.20,
            Map.of(
                "Processor", 1,
                "Lights", 2,
                "Appliances", 3
            ),
            LocalDateTime.of(2024, 10, 7, 10, 30),
            21.5,
            new double[]{20.0, 24.0},
            30,
            25,
            List.of(
                new SmartEnergyManagementSystem.DeviceSchedule("Oven", LocalDateTime.of(2024, 10, 7, 10, 31))
            )
        );

        assertEquals(result.energySavingMode, false);
        assertEquals(result.deviceStatus.get("Processor"), true);
        assertEquals(result.deviceStatus.get("Lights"), true);
        assertEquals(result.deviceStatus.get("Appliances"), true);
        assertEquals(result.temperatureRegulationActive, false);
        assertEquals(result.deviceStatus.get("Heating"), false);
        assertEquals(result.deviceStatus.get("Cooling"), false);
        assertTrue(result.totalEnergyUsed == 25);
        assertFalse(result.deviceStatus.containsKey("Oven"));
    }

    @Test
    public void shouldNotActivateEnergySavingModeWhenPriceEqualsThreshold() {
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
                0.20,
                0.20,
                Map.of(
                        "Processor", 1,
                        "Lights", 2
                ),
                LocalDateTime.of(2024, 10, 7, 10, 30),
                21.5,
                new double[]{20.0, 24.0},
                30,
                25,
                new ArrayList<>()
        );

        assertFalse(result.energySavingMode);
        assertTrue(result.deviceStatus.get("Processor"));
        assertTrue(result.deviceStatus.get("Lights"));
    }

    @Test
    public void shouldActivateNightModeAtExactly23Hours() {
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
                0.15,
                0.20,
                Map.of(
                        "Lights", 2,
                        "Appliances", 3,
                        "Security", 1
                ),
                LocalDateTime.of(2024, 10, 7, 23, 0),
                21.5,
                new double[]{20.0, 24.0},
                30,
                25,
                new ArrayList<>()
        );

        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
        assertTrue(result.deviceStatus.get("Security"));
    }

    @Test
    public void shouldNotActivateNightModeAtExactly6Hours() {
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
                0.15,
                0.20,
                Map.of(
                        "Lights", 2,
                        "Appliances", 3,
                        "Security", 1
                ),
                LocalDateTime.of(2024, 10, 7, 6, 0),
                21.5,
                new double[]{20.0, 24.0},
                30,
                25,
                new ArrayList<>()
        );

        assertTrue(result.deviceStatus.get("Lights"));
        assertTrue(result.deviceStatus.get("Appliances"));
        assertTrue(result.deviceStatus.get("Security"));
    }

    @Test
    public void shouldNotTurnHeatingOnWhenTemperatureEqualsMinimum() {
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
                0.15,
                0.20,
                Map.of(
                        "Lights", 2,
                        "Appliances", 3
                ),
                LocalDateTime.of(2024, 10, 7, 10, 30),
                20.0,
                new double[]{20.0, 24.0},
                30,
                25,
                new ArrayList<>()
        );

        assertFalse(result.temperatureRegulationActive);
        assertFalse(result.deviceStatus.get("Heating"));
    }

    @Test
    public void shouldNotTurnCoolingOnWhenTemperatureEqualsMaximum() {
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
                0.15,
                0.20,
                Map.of(
                        "Lights", 2,
                        "Appliances", 3
                ),
                LocalDateTime.of(2024, 10, 7, 10, 30),
                24.0,
                new double[]{20.0, 24.0},
                30,
                25,
                new ArrayList<>()
        );

        assertFalse(result.temperatureRegulationActive);
        assertFalse(result.deviceStatus.get("Cooling"));
    }

    @Test
    public void shouldReduceEnergyUsageWhenTotalEnergyUsedEqualsLimit() {
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
                0.15,
                0.20,
                Map.of(
                        "Processor", 1,
                        "Lights", 2,
                        "Appliances", 3
                ),
                LocalDateTime.of(2024, 10, 7, 10, 30),
                21.5,
                new double[]{20.0, 24.0},
                30,
                30,
                new ArrayList<>()
        );

        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
        assertTrue(result.deviceStatus.get("Processor"));
        assertTrue(result.totalEnergyUsed < 30);
    }

    @Test
    public void shouldNotTurnDeviceOnWhenScheduledTimeDoesNotMatch() {
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
                0.15,
                0.20,
                new HashMap<>(),
                LocalDateTime.of(2024, 10, 7, 10, 30),
                21.5,
                new double[]{20.0, 24.0},
                30,
                25,
                List.of(
                        new SmartEnergyManagementSystem.DeviceSchedule("Oven", LocalDateTime.of(2024, 10, 7, 10, 31))
                )
        );

        assertFalse(result.deviceStatus.containsKey("Oven"));
    }

    @Test
    public void shouldTurnDeviceOnWhenScheduledTimeMatches() {
        EnergyManagementResult result = energyManagementSystem.manageEnergy(
                0.15,
                0.20,
                new HashMap<>(),
                LocalDateTime.of(2024, 10, 7, 10, 30),
                21.5,
                new double[]{20.0, 24.0},
                30,
                25,
                List.of(
                        new SmartEnergyManagementSystem.DeviceSchedule("Oven", LocalDateTime.of(2024, 10, 7, 10, 30))
                )
        );

        assertTrue(result.deviceStatus.get("Oven"));
    }

}
