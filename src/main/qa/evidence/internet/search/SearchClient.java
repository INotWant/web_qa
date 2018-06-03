package qa.evidence.internet.search;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import qa.evidence.internet.extration.Extration;
import qa.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 该类一个实例对应一个请求。
 *
 * @author jie
 */

public abstract class SearchClient implements Search {

    private static final long RESPONCE_TIME = 300;
    private static final int MAX_TOTAL_CONN = 300;
    private static final int MAX_PER_ROUTE = 50;

    private PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpClient;
    private Executor executor;
    // the number of evidences
    private final int num;

    public SearchClient(int num, List<Header> headers) {
        this.num = num;
        connectionManager.setMaxTotal(MAX_TOTAL_CONN);
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        httpClient = HttpClients.custom()
                .setDefaultHeaders(headers)
                .setConnectionManager(connectionManager)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)) // 不重试
                .build();
        executor = Executors.newFixedThreadPool(num);
    }

    public abstract List<String> getUrls(String query);

    @Override
    public List<String> getEvidences(String query, Extration extration) throws InterruptedException {
        List<String> evidences = new ArrayList<>();

        List<String> urls = getUrls(query);

        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        for (String url : urls) {
            HttpGet httpGet = new HttpGet(url);
            Callable<String> callable = () -> {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    byte[] bytes = IOUtils.toByteArray(entity.getContent());

                    // 1, get charset
                    String charSet = "UTF-8";
                    if (entity.getContentType() != null) {
                        charSet = Utils.detectCharset(entity.getContentType().getValue(), bytes);
                        if (charSet == null)
                            charSet = "UTF-8";
                    }

                    // 2, get html and extration
                    String html = new String(bytes, charSet);
                    return extration.extration(html);
                }
                return null;
            };
            completionService.submit(callable);
        }
        // 3, save to list
        for (int i = 0; i < urls.size(); i++) {
            try {
                // get a future, and it represents a completed task.
                Future<String> future = completionService.poll(RESPONCE_TIME, TimeUnit.MILLISECONDS);
                if (future != null) {
                    String content = future.get();
                    if (content != null && content.length() > 0)
                        evidences.add(content);
                }
            } catch (InterruptedException e) {
                close();
                throw new InterruptedException(e.toString());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return evidences;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void close() {
        if (httpClient != null)
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (connectionManager != null)
            connectionManager.close();
    }

    public int getNum() {
        return num;
    }
}
