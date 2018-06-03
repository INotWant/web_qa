package qa.evidence.internet.search;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import qa.evidence.internet.extration.CblockExtration;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BaiDuSearch extends SearchClient implements Search {

    private static final String QUERY_PARAMETER = "s?wd=";
    private static final String HOST = "https://www.baidu.com/";
    private static List<Header> headers = new ArrayList<>();

    static {
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36"));
    }

    public BaiDuSearch(int num) {
        super(num, headers);
    }

    @Override
    public List<String> getUrls(String query) {
        List<String> urls = new ArrayList<>();
        try {
            HttpGet httpGet = new HttpGet(HOST + QUERY_PARAMETER + URLEncoder.encode(query, "UTF-8"));
            CloseableHttpResponse response = this.getHttpClient().execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                byte[] bytes = IOUtils.toByteArray(entity.getContent());
                String html = new String(bytes);
                Document document = Jsoup.parse(html);
                XPathEvaluator xPathEvaluator = Xsoup.compile("//h3//a//@href");
                List<String> list = xPathEvaluator.evaluate(document).list();
                int count = 0;
                for (int i = 0; i < list.size(); i++) {
                    urls.add(list.get(i));
                    if (++count == this.getNum())
                        break;
                }
                return urls;
            }
        } catch (UnsupportedEncodingException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }

    @Override
    public List<String> getEvidences(String query) {
        throw new UnsupportedOperationException("need to extrative!");
    }


    public static void main(String[] args) throws IOException {

        // https://www.baidu.com/s?wd=%E4%B8%96%E7%95%8C%E4%B8%8A%E6%9C%80%E9%95%BF%E7%9A%84%E6%B2%B3%E6%B5%81

        /*
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://www.baidu.com/s?wd=%E4%B8%96%E7%95%8C%E4%B8%8A%E6%9C%80%E9%95%BF%E7%9A%84%E6%B2%B3%E6%B5%81");
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1) {
                    System.out.println(EntityUtils.toString(entity));
                } else {

                }
            }
        } finally {
            response.close();
        }
        */

        /*
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        clientBuilder.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");

        SocketConfig.Builder socketBuilder = SocketConfig.custom();
        socketBuilder.setSoKeepAlive(true);
        socketBuilder.setSoTimeout(50000);
        SocketConfig socketConfig = socketBuilder.build();
        clientBuilder.setDefaultSocketConfig(socketConfig);
        CloseableHttpClient httpclient = clientBuilder.build();
        HttpGet httpget = new HttpGet("https://www.baidu.com/s?wd=%E4%B8%96%E7%95%8C%E4%B8%8A%E6%9C%80%E9%95%BF%E7%9A%84%E6%B2%B3%E6%B5%81");
        CloseableHttpResponse response = httpclient.execute(httpget);
        StringBuilder sb = new StringBuilder();
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream in = entity.getContent();
                byte[] bytes = new byte[1024];
                int num;
                while ((num = in.read(bytes, 0, 1024)) > 0) {
                    sb.append(new String(bytes, 0, num));
                }
            }
        } finally {
            response.close();
        }
        String html = sb.toString();
        Document document = Jsoup.parse(html);
        XPathEvaluator xPathEvaluator = Xsoup.compile("//h3//a//@href");
        List<String> list = xPathEvaluator.evaluate(document).list();
        for (String url : list) {
            httpget = new HttpGet(url);
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity, "GBK");
//                String output = TextExtract.parse(content);
                System.out.println("URL :: " + url);
                System.out.println("============ 正文 ============");
//                System.out.println(output);
            }

        }

        // */

        /*
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://www.baidu.com/s?wd=%E4%B8%96%E7%95%8C%E4%B8%8A%E6%9C%80%E9%95%BF%E7%9A%84%E6%B2%B3%E6%B5%81");
        BasicHeader header = new BasicHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/63.0.3239.84 Chrome/63.0.3239.84 Safari/537.36");
        httpget.setHeader(header);
        CloseableHttpResponse response = httpclient.execute(httpget);
        try {

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1) {
                    System.out.println(EntityUtils.toString(entity));
                } else {

                }
            }
        } finally {
            response.close();
        }
        */

        BaiDuSearch search = new BaiDuSearch(5);
        try {
            List<String> evidences = search.getEvidences("姚明的妻子", CblockExtration.create());
            for (String evidence : evidences)
                System.out.println(evidence);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
