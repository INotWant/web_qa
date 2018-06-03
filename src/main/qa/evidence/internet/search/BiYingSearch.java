package qa.evidence.internet.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import qa.evidence.internet.extration.Extration;
import qa.label.SafeType;
import qa.label.ThreadSafe;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe({SafeType.ENTRUST, SafeType.STACK, SafeType.CONSTANTNESS})
@Deprecated
public class BiYingSearch implements Search {

    private final int customConfigId;

    private static Map<Integer, BiYingSearch> searchs = new ConcurrentHashMap<>();

    private BiYingSearch(int customConfigId) {
        this.customConfigId = customConfigId;
    }

    public static BiYingSearch getInstance(int customConfigId) {
        // method 2
        searchs.putIfAbsent(customConfigId, new BiYingSearch(customConfigId));
        return searchs.get(customConfigId);

        /* method 1
        if (searchs.get(customConfigId) == null) {
            synchronized (BiYingSearch.class) {
                if (searchs.get(customConfigId) != null)
                    searchs.put(customConfigId, new BiYingSearch(customConfigId));
            }
        }
        return searchs.get(customConfigId);
        // */
    }

    @Override
    public List<String> getEvidences(String query) {
        List<String> evidences = new ArrayList<>();
        try {
            // 1, initialize BiYing evidence.internet.search API
            String host = "https://api.cognitive.microsoft.com";
            String path = "/bingcustomsearch/v7.0/evidence.internet.search";
            String customConfigId = String.valueOf(this.customConfigId);
            URL url = new URL(host + path + "?q=" + URLEncoder.encode(query, "UTF-8") + "&CustomConfig=" + customConfigId);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            String subscriptionKey = "fdc204d7371448e898b697a58ffca710";
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
            // 2, receive JSON body
            InputStream stream = connection.getInputStream();
            String jsonStr = new Scanner(stream).useDelimiter("\\A").next();
            JsonParser parser = new JsonParser();
            // 3, json parse
            JsonObject json = parser.parse(jsonStr).getAsJsonObject();
            JsonElement webPagesElement = json.get("webPages");
            JsonElement valueElement = webPagesElement.getAsJsonObject().get("value");
            JsonArray array = valueElement.getAsJsonArray();
            for (JsonElement anArray : array) {
                String evidence = anArray.getAsJsonObject().get("snippet").toString();
                // delete ""
                evidence = evidence.substring(1, evidence.length() - 1);
                // delete  ... if evidence haves
                if (evidence.substring(evidence.length() - 4, evidence.length()).equals(" ..."))
                    evidence = evidence.substring(0, evidence.length() - 4);
                evidences.add(evidence);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return evidences;
    }

    @Override
    public List<String> getEvidences(String query, Extration extration) {
        throw new UnsupportedOperationException("don't need to extrative");
    }
}
