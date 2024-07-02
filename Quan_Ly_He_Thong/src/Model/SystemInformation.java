
package Model;



public class SystemInformation {
    private String osName;
    private String osVersion;
    private long totalMemory;

    // Constructor không tham số
    public SystemInformation() {
    }

    // Constructor có tham số
    public SystemInformation(String osName, String osVersion, long totalMemory) {
        this.osName = osName;
        this.osVersion = osVersion;
        this.totalMemory = totalMemory;
    }

    // Getters và Setters
    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }
    @Override
    public String toString() {
        return "OS Name: " + osName + "\nOS Version: " + osVersion + "\nTotal Memory: " + totalMemory;
    }
}
