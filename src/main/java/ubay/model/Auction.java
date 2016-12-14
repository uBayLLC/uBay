package ubay.model;

import lombok.*;

import java.util.Date;

@Data
@RequiredArgsConstructor
public class Auction {

    @NonNull private Item item;
    @NonNull private int startingPrice;
    @NonNull private Date endDateTime;
    @NonNull private int buyOutPrice;
    @NonNull private Bid currentBid;

}
