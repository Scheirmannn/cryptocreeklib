package logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.datalog.*;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import java.util.HashMap;
import java.util.Map;

/**
 * Logs robot data simultaneously to NetworkTables 4 and a .wpilog file.
 *
 * <p>
 * Compatible with AdvantageScope, Glass, and Elastic dashboards.
 * The .wpilog file is automatically saved to USB if available,
 * otherwise falls back to roboRIO onboard storage.
 *
 * <p>
 * Usage example:
 * 
 * <pre>{@code
 * RobotLogger logger = new RobotLogger("Shooter");
 * logger.log("velocity", shooter.getVelocity());
 * logger.log("atSpeed", shooter.atSpeed());
 * logger.logEvent("Shot fired");
 * }</pre>
 */
public class RobotLogger {

    private final NetworkTable table;
    private final DataLog log;
    private final String subsystemName;

    private final Map<String, DoubleLogEntry> doubleEntries = new HashMap<>();
    private final Map<String, BooleanLogEntry> booleanEntries = new HashMap<>();
    private final Map<String, StringLogEntry> stringEntries = new HashMap<>();

    /**
     * Creates a logger for a subsystem.
     *
     * @param subsystemName the name of the subsystem, used as the NetworkTable
     *                      name and log prefix (e.g. "Shooter", "Drivetrain")
     */
    public RobotLogger(String subsystemName) {
        this.subsystemName = subsystemName;
        DataLogManager.start();
        DriverStation.startDataLog(DataLogManager.getLog());
        this.log = DataLogManager.getLog();
        this.table = NetworkTableInstance.getDefault().getTable(subsystemName);
    }

    /**
     * Logs a double value to NetworkTables and the .wpilog file.
     *
     * @param key   the name of the value (e.g. "velocity", "temperature")
     * @param value the double value to log
     */
    public void log(String key, double value) {
        table.getEntry(key).setDouble(value);
        doubleEntries
                .computeIfAbsent(key, k -> new DoubleLogEntry(log, "/" + subsystemName + "/" + k))
                .append(value);
    }

    /**
     * Logs a boolean value to NetworkTables and the .wpilog file.
     *
     * @param key   the name of the value (e.g. "atSpeed", "limitSwitch")
     * @param value the boolean value to log
     */
    public void log(String key, boolean value) {
        table.getEntry(key).setBoolean(value);
        booleanEntries
                .computeIfAbsent(key, k -> new BooleanLogEntry(log, "/" + subsystemName + "/" + k))
                .append(value);
    }

    /**
     * Logs a string value to NetworkTables and the .wpilog file.
     *
     * @param key   the name of the value (e.g. "state", "mode")
     * @param value the string value to log
     */
    public void log(String key, String value) {
        table.getEntry(key).setString(value);
        stringEntries
                .computeIfAbsent(key, k -> new StringLogEntry(log, "/" + subsystemName + "/" + k))
                .append(value);
    }

    /**
     * Logs a timestamped event visible on the AdvantageScope timeline.
     * Useful for marking when actions occur, such as shooting or intaking.
     *
     * @param event a short description of the event (e.g. "Shot fired", "Intake
     *              deployed")
     */
    public void logEvent(String event) {
        DataLogManager.log("[" + subsystemName + "] " + event);
    }
}