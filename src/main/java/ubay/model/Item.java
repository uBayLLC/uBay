package ubay.model;

import lombok.*;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Item {

    @NonNull private String name;
    @NonNull private String description;
    @NonNull private String photoUrl;
    @NonNull private String seller;
    @NonNull private List<Tag> tags;

}