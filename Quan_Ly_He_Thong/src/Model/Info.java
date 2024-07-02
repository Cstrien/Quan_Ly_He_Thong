
package Model;

public class Info {
    private String info;

    // Constructor không tham số
    public Info() {
    }

    // Constructor có tham số
    public Info(String info) {
        this.info = info;
    }

    // Getter và Setter
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return info;
    }
}


