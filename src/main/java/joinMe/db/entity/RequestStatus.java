package joinMe.db.entity;

public enum RequestStatus {
    APPROVED("APPROVED"), REJECTED("REJECTED"), IN_PROGRESS("IN_PROGRESS");

    private final String status;

    RequestStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
