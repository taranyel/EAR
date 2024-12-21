package joinMe.db.entity;

public enum TripStatus {
    ACTIVE("ACTIVE"), CLOSED("CLOSED");

    private final String status;

    TripStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
