package qa.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /**
     * exec bash script
     */
    public static void execBashScript(String shellPath) {
        // get name representing the running Java virtual machine.
//        String name = ManagementFactory.getRuntimeMXBean().getName();
//        String pid = name.split("@")[0];

        try {
//            System.out.println("Starting to exec{ " + shellPath + " }. PID is: " + pid);
            Process process;
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", shellPath);
            pb.environment();
            process = pb.start();
            process.waitFor();
//            System.out.println("----- End -----");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * exec cmd
     */
    public static List<String> execBashCmd(String... cmds) {
        List<String> resultList = new ArrayList<>();
        System.out.println(Arrays.toString(cmds));
        try {
            Process process;
            ProcessBuilder pb = new ProcessBuilder(cmds);
            pb.environment();
            process = pb.start();
            process.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                resultList.add(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }


    // source: https://github.com/code4craft/webmagic/tree/master/webmagic-core/src/main/java/us/codecraft/webmagic/utils
    public static String detectCharset(String contentType, byte[] contentBytes) throws IOException {
        String charset;
        // charset
        // 1、encoding in http header Content-Type
        charset = getCharset(contentType);
        if (StringUtils.isNotBlank(contentType) && StringUtils.isNotBlank(charset)) {
            return charset;
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset);
        // 2、charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.contains("charset")) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                }
                // 2.2、html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        return charset;
    }

    // source: https://github.com/code4craft/webmagic/tree/master/webmagic-core/src/main/java/us/codecraft/webmagic/utils
    public static String getCharset(String contentType) {
        Pattern patternForCharset = Pattern.compile("charset\\s*=\\s*['\"]*([^\\s;'\"]*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternForCharset.matcher(contentType);
        if (matcher.find()) {
            String charset = matcher.group(1);
            if (Charset.isSupported(charset)) {
                return charset;
            }
        }
        return null;
    }


    /**
     * (for baidu word embedding)
     * create word-embedding.index for RandomAccessFile.
     * this is a word-embedding index.
     */
    public static void createBaiduWordEmbeddingIndex() {
        try (BufferedReader reader1 = new BufferedReader(new FileReader("data/word/wordvecs.txt"));
             BufferedReader reader2 = new BufferedReader(new FileReader("data/word/wordvecs.vcb"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("data/word/word-embedding.index"))) {
            String embedding;   // embedding
            String word;   // word
            long pos = 0;
            while ((word = reader2.readLine()) != null) {
                embedding = reader1.readLine();
                int length = embedding.getBytes().length;
                writer.write(word + "\t" + pos + "\t" + length + "\n");
                pos += (length + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * similar  createBaiduWordEmbeddingIndex ,but this for a word embedding of facebook.
     */
    public static void createFacebookWordEmbeddingIndex() {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/word/wiki.zh.vec"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("data/word/word-embedding-facebook.index"))) {
            String line = reader.readLine();
            long startP = 0;
            startP += (line.getBytes().length + 1);
            while ((line = reader.readLine()) != null) {
                String word = line.split(" ")[0];
                int length = line.getBytes().length;
                writer.write(word + "\t" + startP + "\t" + length + "\n");
                startP += (length + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * create q-e.index for RandomAccessFile.
     * this is a q-e index.
     */
    public static void createQEIndex(String readFileName, String writeFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(readFileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(writeFileName))) {
            long pos = 0;
            String question = reader.readLine();
            writer.write(question + "\t" + pos + "\n");
            pos += (question.getBytes().length + 1);
            boolean b = false;
            while ((question = reader.readLine()) != null) {
                int length = question.getBytes().length;
                if (b) {
                    writer.write(question + "\t" + pos + "\n");
                    b = false;
                }
                if ("".equals(question))
                    b = true;
                pos += (length + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get train_idf
     */
    public static void createIdf(String readFileName, String writeFileName) {
        long textCount = 1;
        Map<String, Integer> wordMap = new HashMap<>();
        Map<String, Boolean> textMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(readFileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(writeFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if ("".equals(line)) {
                    for (Map.Entry<String, Boolean> entry : textMap.entrySet()) {
                        String word = entry.getKey();
                        // 排除不可见字符
                        if (word.length() == 0 || word.charAt(0) < 32)
                            continue;
                        wordMap.merge(word, 1, (a, b) -> a + b);
                    }
                    if (textMap.size() > 0)
                        ++textCount;
                    textMap.clear();
                } else {
                    String[] split = line.split(" ");
                    for (int i = 0; i < split.length; i++) {
                        String word = split[i];
                        textMap.putIfAbsent(word, true);
                    }
                }
            }
            for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
                String word = entry.getKey();
                double count = entry.getValue();
                writer.write(word + "\t" + Math.log(textCount / count) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Map<String, Double> getIdf(String idfFileName) {
        Map<String, Double> idfMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(idfFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\t");
                idfMap.put(split[0], Double.parseDouble(split[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return idfMap;
    }

    public static void creatTfIdf(String idfFileName, String readFileName, String writeFileName) {
        // initialize idfMap
        Map<String, Double> idfMap = getIdf(idfFileName);
        Map<String, Integer> textMap = new HashMap<>();
        int textCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(readFileName));
             BufferedWriter writer = new BufferedWriter(new FileWriter(writeFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if ("".equals(line)) {
                    for (Map.Entry<String, Integer> entry : textMap.entrySet()) {
                        String word = entry.getKey();
                        double count = entry.getValue();
                        writer.write(word + " " + count / textCount * idfMap.get(word) + "\t");
                    }
                    writer.write("\n");
                    textCount = 0;
                    textMap.clear();
                } else {
                    String[] split = line.split(" ");
                    for (int i = 0; i < split.length; i++) {
                        String word = split[i];
                        if (word.length() == 0 || word.charAt(0) < 32)
                            continue;
                        ++textCount;
                        textMap.merge(word, 1, (a, b) -> a + b);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
