package ubay.model;

import lombok.*;

@Data
@RequiredArgsConstructor
public class Account {

    @NonNull private String firstname;
    @NonNull private String lastname;
    @NonNull private String email;
    @NonNull private String password;
    @NonNull private String address;
    private int card = 0;

    private static Account loggedInUser = null;

    public static Account getLoggedInUser() {
        if (loggedInUser == null) {
            loggedInUser = new Account(); }
        return loggedInUser;
    }

    private Account() {
        firstname = null;
        lastname = null;
        email = null;
        password = null;
        address = null;
        card = 0; }
}







