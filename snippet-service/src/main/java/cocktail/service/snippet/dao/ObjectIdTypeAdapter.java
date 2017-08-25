package cocktail.service.snippet.dao;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * Created by jerikss3 on 2017-08-21.
 */
public class ObjectIdTypeAdapter extends TypeAdapter<ObjectId> {
  @Override
  public void write(final JsonWriter out, final ObjectId value) throws IOException {
    out.value(value.toString());
  }

  @Override
  public ObjectId read(final JsonReader in) throws IOException {
    return new ObjectId(in.toString());
  }
}