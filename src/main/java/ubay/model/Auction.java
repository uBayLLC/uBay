package ubay.model;

import lombok.*;

import java.util.*;
import java.text.*;
import java.text.FieldPosition;
import java.text.ParsePosition;



@Data
@RequiredArgsConstructor
public class Auction {

    @NonNull private Item item;
    @NonNull private int startingPrice;
    @NonNull private Date endDateTime;
    @NonNull private int buyOutPrice;
    @NonNull private Bid currentBid;


    public String getEndDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = dateFormat.format(this.getEndDateTime());
        return formattedDate; }

    public String getEndTime() {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedTime = timeFormat.format(this.getEndDateTime());
        return formattedTime; }
}

