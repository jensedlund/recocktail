package cocktail.restful_web;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import cocktail.snippet.SnippetInfo;

/**
 * @version 1.0
 * @since 09/05/16
 */
public class SnippetSetDeserializer implements JsonDeserializer<SnippetInfo> {

  @Override
  public SnippetInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {

    JsonElement snippetCollection = json.getAsJsonObject().get("snippetCollection");
    System.out.println(snippetCollection);
    return new Gson().fromJson(snippetCollection, SnippetInfo.class);
  }
}
