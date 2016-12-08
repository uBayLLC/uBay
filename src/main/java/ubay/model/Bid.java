package ubay.model;

import lombok.*;
import ubay.model.Account;

@Data
@RequiredArgsConstructor
public class Bid {

    @NonNull private Account account;
    @NonNull private int amount;

}