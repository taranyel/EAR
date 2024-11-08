package joinMe.db.entity;

public enum AccountStatus {
    ACTIVE("ACTIVE"), BLOCKED("BLOCKED");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
