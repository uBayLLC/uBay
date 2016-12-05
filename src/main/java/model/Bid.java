package model;

import lombok.*;

@Data
@RequiredArgsConstructor
public class Bid {

    @NonNull private Account account;
    @NonNull private int amount;

}