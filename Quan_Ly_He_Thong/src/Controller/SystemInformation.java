
package Controller;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.List;

public class SystemInformation {

    private SystemInfo si;
    private HardwareAbstractionLayer hal;
    private OperatingSystem os;

    public SystemInformation() {
        si = new SystemInfo();
        hal = si.getHardware();
        os = si.getOperatingSystem();
    }

    public String getCpuInfo() {
        CentralProcessor processor = hal.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // Sleep for 1 second to calculate load over time
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        long[] ticks = processor.getSystemCpuLoadTicks();
        long totalCpu = 0;
        for (int i = 0; i < ticks.length; i++) {
            totalCpu += ticks[i] - prevTicks[i];
        }
        double cpuLoad = 1.0 - (ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()]) * 1.0 / totalCpu;
        return String.format("CPU Load: %.2f%%", cpuLoad * 100);
    }

    public String getMemoryInfo() {
        GlobalMemory memory = hal.getMemory();
        return String.format("Memory: %.2f GB / %.2f GB", memory.getAvailable() / 1e9, memory.getTotal() / 1e9);
    }

    public String getDiskInfo() {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        StringBuilder diskInfo = new StringBuilder();
        for (OSFileStore fs : fileStores) {
            diskInfo.append(String.format("%s: %.2f GB / %.2f GB\n", fs.getName(), fs.getUsableSpace() / 1e9, fs.getTotalSpace() / 1e9));
        }
        return diskInfo.toString();
    }

    public String getNetworkInfo() {
        List<NetworkIF> networkIFs = hal.getNetworkIFs();
        StringBuilder networkInfo = new StringBuilder();
        for (NetworkIF net : networkIFs) {
            networkInfo.append(String.format("%s: %s\n", net.getName(), net.getDisplayName()));
        }
        return networkInfo.toString();
    }
}
