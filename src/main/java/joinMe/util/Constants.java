package joinMe.util;

import joinMe.db.entity.AccountStatus;
import joinMe.db.entity.RequestStatus;
import joinMe.db.entity.Role;
import joinMe.db.entity.TripStatus;

public final class Constants {

    /**
     * Default user role.
     */
    public static final Role DEFAULT_ROLE = Role.USER;

    public static final RequestStatus DEFAULT_REQUEST_STATUS = RequestStatus.IN_PROGRESS;

    public static final AccountStatus DEFAULT_ACCOUNT_STATUS = AccountStatus.ACTIVE;

    public static final TripStatus DEFAULT_TRIP_STATUS = TripStatus.ACTIVE;

    public static final String USERNAME_PARAM = "username";

    private Constants() {
        throw new AssertionError();
    }
}
