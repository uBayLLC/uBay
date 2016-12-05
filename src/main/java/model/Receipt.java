package model;

import lombok.*;

@Data
@RequiredArgsConstructor
public class Receipt {

    @NonNull private int card;
    @NonNull private Auction auction;

}
