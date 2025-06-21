package fn10.bedrockr.addons.source;

import java.awt.Component;
import java.io.File;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface ElementSource {
    final GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.setPrettyPrinting().create();

    final Integer Type = 0; // 0 for BP, 1 for RP
    

    String getJSONString();

    Object getClassFromJSON(String jsonString);

    File buildJSONFile(Component doingThis,String workspace);

    Class getSerilizedClass();

    Object getSerilized();
}
