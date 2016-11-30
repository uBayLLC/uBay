package model;

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

}
