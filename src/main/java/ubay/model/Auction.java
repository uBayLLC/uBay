package ubay.model;

import lombok.*;

@Data
@RequiredArgsConstructor
public class Auction {

    @NonNull private Item item;
    @NonNull private int startingPrice;
    @NonNull private int buyOutPrice;
    @NonNull private Bid currentBid;

}
