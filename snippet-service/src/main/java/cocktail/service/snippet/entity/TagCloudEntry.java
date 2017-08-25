package cocktail.service.snippet.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mongodb.morphia.annotations.Id;

@Setter @Getter @ToString
public class TagCloudEntry {
  @Id
  private String _id;
  private int count;
}
