package cocktail.service.snippet.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Objects;

/**
 * Created by jerikss3 on 2017-05-23.
 */
@Entity("users")
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class User implements Comparable<User> {
  @Id
  private ObjectId id;

  private String name;

  public User(String name) {
    this.name = name;
  }


  @Override
  public int compareTo(User that) {
    if (this.id.compareTo(that.id) < 0) {
      return -1;
    } else if (this.id.compareTo(that.id) > 0) {
      return 1;
    }

    if (this.name.compareTo(that.name) < 0) {
      return -1;
    } else if (this.name.compareTo(that.name) > 0) {
      return 1;
    }
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id) &&
        Objects.equals(name, user.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}
