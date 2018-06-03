package qa.evidence.internet.extration;

import qa.label.ThreadSafe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static qa.label.SafeType.ENTRUST;

/**
 * based on: http://code.google.com/p/cx-extractor/ (哈尔滨工业大学信息检索研究中心 陈鑫)
 *
 * @author jie
 */

@ThreadSafe(ENTRUST)
public class CblockExtration implements Extration {

    private static final ConcurrentHashMap<Integer, CblockExtration> map = new ConcurrentHashMap<>();
    /*
    行块长度阈值
    当待抽取的网页正文中遇到成块的新闻标题未剔除时，只要增大此阈值即可
    阈值增大，准确率提升，召回率下降；值变小，噪声会大，但可以保证抽到只有一句话的正文
    */
    private final int threshold;

    private CblockExtration(int threshold) {
        this.threshold = threshold;
    }

    public static CblockExtration create(int threshold) {
        map.putIfAbsent(threshold, new CblockExtration(threshold));
        return map.get(threshold);
    }

    public static CblockExtration create() {
        return create(86);
    }

    @Override
    public String extration(String html) {
        StringBuilder resultSB = new StringBuilder();
        int blocksWidth = 3;

        // 1, remvoe unrelated content
        html = preProcess(html);

        // 2, get content
        List<String> lines = Arrays.asList(html.split("\n"));
        List<Integer> indexDistribution = new ArrayList<>();

        for (int i = 0; i < lines.size() - blocksWidth; i++) {
            int wordsNum = 0;
            for (int j = i; j < i + blocksWidth; j++) {
                // “\\st+” 匹配空白字符
                lines.set(j, lines.get(j).replaceAll("\\s+", ""));
                wordsNum += lines.get(j).length();
            }
            indexDistribution.add(wordsNum);
        }

        int start = -1;
        int end = -1;
        boolean boolstart = false, boolend = false;

        for (int i = 0; i < indexDistribution.size() - 1; i++) {
            if (indexDistribution.get(i) > threshold && !boolstart) {
                if (indexDistribution.get(i + 1) != 0
                        || indexDistribution.get(i + 2) != 0
                        || indexDistribution.get(i + 3) != 0) {
                    boolstart = true;
                    start = i;
                    continue;
                }
            }
            if (boolstart) {
                if (indexDistribution.get(i) == 0
                        || indexDistribution.get(i + 1) == 0) {
                    end = i;
                    boolend = true;
                }
            }
            if (boolend) {
                StringBuilder tmp = new StringBuilder();
                for (int ii = start; ii <= end; ii++) {
                    if (lines.get(ii).length() < 5) continue;
                    tmp.append(lines.get(ii)).append("\n");
                }
                String str = tmp.toString();
                //System.out.println(str);
                if (str.contains("Copyright") || str.contains("版权所有"))
                    continue;
                resultSB.append(str);
                boolstart = boolend = false;
            }
        }

        return resultSB.toString().replaceAll("\n", "");
    }

    private String preProcess(String html) {
        html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
        html = html.replaceAll("(?is)<!--.*?-->", "");                // remove html comment
        html = html.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove javascript
        html = html.replaceAll("(?is)<style.*?>.*?</style>", "");   // remove css
        html = html.replaceAll("&.{2,5};|&#.{2,5};", " ");            // remove special char
        return html.replaceAll("(?is)<.*?>", "");
    }
}
