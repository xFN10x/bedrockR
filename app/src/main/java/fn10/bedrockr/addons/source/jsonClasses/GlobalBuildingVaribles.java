package fn10.bedrockr.addons.source.jsonClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.google.gson.GsonBuilder;

import fn10.bedrockr.Launcher;
import fn10.bedrockr.addons.source.interfaces.ElementFile;
import fn10.bedrockr.addons.source.interfaces.ElementSource;

/**
 * This is an ElementFile, meant to be passed to other Element Files, that holds
 * things like language texts, and textures. This should ALWAYS be built last.
 */

public class GlobalBuildingVaribles implements ElementFile {

    public List<String> Langs = new ArrayList<String>();
    public Map<String, String> EnglishTexts = new HashMap<String,String>();

    @Override
    public Class<? extends ElementSource> getSourceClass() {
        return null;
    }

    @Override
    public String getElementName() {
        return null;
    }

    @Override
    public void setDraft(Boolean draft) {
        return;
    }

    @Override
    public Boolean getDraft() {
        return null;
    }

    @Override
    public void build(String rootPath, WPFile workspaceFile, String rootResPackPath,
            GlobalBuildingVaribles globalResVaribles) throws IOException {
        //var gson = new GsonBuilder().setPrettyPrinting().create();

        Langs.add("en_US"); // currently, only english support

        // make lang file
        var langsFile = new File(rootResPackPath + File.separator + "texts" + File.separator + "languages.json");
        FileUtils.createParentDirectories(langsFile);
        Files.write(langsFile.toPath(), gson.toJson(Langs).getBytes(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);

        var englishFile = new File(rootResPackPath + File.separator + "texts" + File.separator + "en_US.lang");
        //dont need to make parents, other file is already in the same dir
        var englishLangStringBuilder = new StringBuilder("## Generated with bedrockR "+Launcher.VERSION+"\n");

        for (Entry<String,String> ent : EnglishTexts.entrySet()) {
            englishLangStringBuilder.append(ent.getKey()+"="+ent.getValue()+"\n");
        }

        Files.write(englishFile.toPath(), englishLangStringBuilder.toString().getBytes(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

}
