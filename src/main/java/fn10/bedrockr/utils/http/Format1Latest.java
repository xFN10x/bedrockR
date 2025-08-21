package fn10.bedrockr.utils.http;

import com.google.gson.annotations.SerializedName;

public class Format1Latest {

    @SerializedName("valid")
    public Boolean ShouldUse;

    @SerializedName("format")
    public Integer Format;

    @SerializedName("currentVersion")
    public Integer LatestVersion;

    @SerializedName("message")
    public String Message;

    @SerializedName("currentStringVersion")
    public String CurrentStringVersion;

}
