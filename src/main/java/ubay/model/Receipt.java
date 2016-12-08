package ubay.model;

import lombok.*;
import ubay.model.Auction;

@Data
@RequiredArgsConstructor
public class Receipt {

    @NonNull private int card;
    @NonNull private Auction auction;

}
