package logger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.datalog.*;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import java.util.HashMap;
import java.util.Map;

public class RobotLogger {

    private final NetworkTable table;
    private final DataLog log;
    private final String subsystemName;

    private final Map<String, DoubleLogEntry> doubleEntries = new HashMap<>();
    private final Map<String, BooleanLogEntry> booleanEntries = new HashMap<>();
    private final Map<String, StringLogEntry> stringEntries = new HashMap<>();

    public RobotLogger(String subsystemName) {
        this.subsystemName = subsystemName;
        DataLogManager.start();
        DriverStation.startDataLog(DataLogManager.getLog());
        this.log = DataLogManager.getLog();
        this.table = NetworkTableInstance.getDefault().getTable(subsystemName);
    }

    public void log(String key, double value) {
        table.getEntry(key).setDouble(value);
        doubleEntries
                .computeIfAbsent(key, k -> new DoubleLogEntry(log, "/" + subsystemName + "/" + k))
                .append(value);
    }

    public void log(String key, boolean value) {
        table.getEntry(key).setBoolean(value);
        booleanEntries
                .computeIfAbsent(key, k -> new BooleanLogEntry(log, "/" + subsystemName + "/" + k))
                .append(value);
    }

    public void log(String key, String value) {
        table.getEntry(key).setString(value);
        stringEntries
                .computeIfAbsent(key, k -> new StringLogEntry(log, "/" + subsystemName + "/" + k))
                .append(value);
    }

    public void logEvent(String event) {
        DataLogManager.log("[" + subsystemName + "] " + event);
    }
}