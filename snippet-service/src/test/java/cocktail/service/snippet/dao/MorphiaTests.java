package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;
import cocktail.service.snippet.entity.TagCloudEntry;
import cocktail.service.snippet.entity.User;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.mongodb.morphia.aggregation.Accumulator.accumulator;
import static org.mongodb.morphia.aggregation.Group.grouping;
import static org.mongodb.morphia.aggregation.Group.push;
import static org.mongodb.morphia.aggregation.Group.sum;

/**
 * Created by jerikss3 on 2017-05-23.
 */

public class MorphiaTests {

  private static SnippetInfo snippetInfo;
  private static User user = new User(ObjectId.get(),"Testuser");

  public static SnippetInfo populateSnippetInfo() {

    List<String> tagList = new ArrayList<>();
    tagList.add("gull");
    tagList.add("wolf");
    tagList.add("bear");
    tagList.add("larch");

    snippetInfo = new SnippetInfo(
        null,
        1,
        tagList,
        5,
        1.1,
        1.2,
        LocalDateTime.now(),
        LocalDate.now(),
        null
        );
    return snippetInfo;
  }

  public static void main(String... args) {
    final Morphia morphia = new Morphia();

    // tell Morphia where to find your classes
    // can be called multiple times with different packages or classes
    morphia.mapPackage("cocktail.service.snippet.entity");

    // create the Datastore connecting to the default port on the local host
    final Datastore datastore = morphia.createDatastore(new MongoClient(), "morphia_example");
    datastore.getDB().dropDatabase();
    datastore.ensureIndexes();

    datastore.save(user);

    populateSnippetInfo();
    datastore.save(snippetInfo);
    System.out.println(snippetInfo);

    populateSnippetInfo();
    datastore.save(snippetInfo);
    System.out.println(snippetInfo);

    System.out.println("Save ok");

    Query<SnippetInfo> snippetInfoQuery = datastore.find(SnippetInfo.class);
    System.out.println("SnippetInfo Query ok");
    Query<User> userQuery = datastore.find(User.class);
    System.out.println("User Query ok");

    final List<User> users = userQuery.asList();
    for ( User s : users) {
      System.out.println(s);
    }

    final List<SnippetInfo> snippetInfos = snippetInfoQuery.asList();
    for (SnippetInfo s : snippetInfos) {
      System.out.println(s);
    }



//
//    Iterator<TagCloudEntry> aggregate = datastore.createAggregation(SnippetInfo.class)
//        .unwind("tags")
//        .group("tags", grouping("count", accumulator("$sum", 1)))
//        .out("tags", TagCloudEntry.class);
//
//    while (aggregate.hasNext()) {
//      System.out.println(aggregate.next());
//    }

//    db.snippets.aggregate([
//        { $unwind : "$tags" },
//        { $group : { _id : "$tags" , number : { $sum : 1 } } },
//        {$out : "tags"}
//        ])
//

  }
}

