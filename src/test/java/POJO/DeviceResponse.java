package POJO;

public class DeviceResponse {
    private String id;
    private String name;
    private String createdAt;
    private DeviceData data;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public DeviceData getData() {
        return data;
    }

    public void setData(DeviceData data) {
        this.data = data;
    }
}
