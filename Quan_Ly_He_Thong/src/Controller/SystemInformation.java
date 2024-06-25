
package Controller;

import java.util.HashMap;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.List;
import java.util.Map;
import oshi.hardware.CentralProcessor.PhysicalProcessor;

public class SystemInformation {

    private SystemInfo si;
    private HardwareAbstractionLayer hal;
    private OperatingSystem os;

    public SystemInformation() {
        si = new SystemInfo();
        hal = si.getHardware();
        os = si.getOperatingSystem();
    }

   public String getOSName() {
        return os.getFamily();
    }

    

    public String getMemoryInfo() {
        GlobalMemory memory = hal.getMemory();
        return String.format("Total Memory: %.2f GB", memory.getTotal() / 1e9);
    }
    
}
