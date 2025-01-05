package joinMe.db.entity;

import lombok.Getter;

@Getter
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
