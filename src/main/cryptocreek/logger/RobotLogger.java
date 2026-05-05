
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.datalog.*;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import java.util.HashMap;
import java.util.Map;

/**
 * Logs robot data simultaneously to NetworkTables 4 and a .wpilog file.
 * Compatible with AdvantageScope, Glass, and Elastic.
 * The .wpilog file is saved to USB if available, otherwise roboRIO storage.
 */
public class RobotLogger {

    private final NetworkTable table;
    private final DataLog log;

    private final Map<String, DoubleLogEntry> doubleEntries = new HashMap<>();
    private final Map<String, BooleanLogEntry> booleanEntries = new HashMap<>();
    private final Map<String, StringLogEntry> stringEntries = new HashMap<>();

    /**
     * Creates a logger for a subsystem.
     * 
     * @param subsystemName used as the table name e.g. "Shooter"
     */
    public RobotLogger(String subsystemName) {
        DataLogManager.start();
        DriverStation.startDataLog(DataLogManager.getLog());
        this.log = DataLogManager.getLog();
        this.table = NetworkTableInstance.getDefault().getTable(subsystemName);
    }

    /** Logs a double value. */
    public void log(String key, double value) {
        table.getEntry(key).setDouble(value);
        doubleEntries
                .computeIfAbsent(key, k -> new DoubleLogEntry(log, "/" + table.getPath() + "/" + k))
                .append(value);
    }

    /** Logs a boolean value. */
    public void log(String key, boolean value) {
        table.getEntry(key).setBoolean(value);
        booleanEntries
                .computeIfAbsent(key, k -> new BooleanLogEntry(log, "/" + table.getPath() + "/" + k))
                .append(value);
    }

    /** Logs a string value. */
    public void log(String key, String value) {
        table.getEntry(key).setString(value);
        stringEntries
                .computeIfAbsent(key, k -> new StringLogEntry(log, "/" + table.getPath() + "/" + k))
                .append(value);
    }

    /** Logs a timestamped event visible on the AdvantageScope timeline. */
    public void logEvent(String event) {
        DataLogManager.log("[" + table.getPath() + "] " + event);
    }
}