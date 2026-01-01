package fn10.bedrockr.utils.typeAdapters;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class XplateAPIDateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(format.format(src));
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return format.parse(json.getAsString());
        } catch (ParseException e) {
            e.printStackTrace();
            return Date.from(Instant.EPOCH);
        }
    }

}
