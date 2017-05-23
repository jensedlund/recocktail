package cocktail.service.snippet.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by jerikss3 on 2017-05-23.
 */
@Entity("users")
@Data @AllArgsConstructor @NoArgsConstructor
public class User {
  @Id
//  @Setter(AccessLevel.NONE)
  private ObjectId id;

  private String name;

  public User(String name) {
    this.name = name;
  }
}
