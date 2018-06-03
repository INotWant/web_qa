import org.junit.Test;
import qa.QA_System;
import qa.evidence.internet.extration.CblockExtration;
import qa.evidence.internet.search.BaiDuSearch;
import qa.segmentation.Jieba;
import qa.segmentation.Segmenter;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TestConcurrent {

    // Result :: 尽管使用的是单例模式，但由于调用的 Jieba.sentenceProcess() 方法是线程安全的。所以，它也是线程安全的。
    @Test
    public void testJieba() throws InterruptedException {
        Segmenter segmenter = Jieba.getInstance();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(2);
        Thread thread1 = new Thread(() -> {
            try {
                startLatch.await();
                List<String> words = segmenter.segment("习近平在慰问电中表示，惊悉贵国克麦罗沃市发生火灾，造成重大人员伤亡和财产损失。我谨代表中国政府和中国人民，并以我个人的名义，对所有遇难者表示沉痛的哀悼，向受伤者和遇难者家属致以深切的同情和诚挚的慰问。");
                System.out.println(words);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                startLatch.await();
                List<String> words = segmenter.segment("习近平在慰问电中表示，惊悉贵国克麦罗沃市发生火灾，造成重大人员伤亡和财产损失。我谨代表中国政府和中国人民，并以我个人的名义，对所有遇难者表示沉痛的哀悼");
                System.out.println(words);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });
        thread1.start();
        thread2.start();
        startLatch.countDown();
        endLatch.await();
    }


    // Result :: 由于栈封闭、线程池的使用即使是单例模式，但它也是线程安全的。
    @Test
    public void testBaiDuSearch() throws InterruptedException {
        BaiDuSearch baiDuSearch = new BaiDuSearch(3);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(2);
        Thread thread1 = new Thread(() -> {
            try {
                startLatch.await();
                List<String> evidences = baiDuSearch.getEvidences("西游记的作者是谁？", CblockExtration.create());
                System.out.println(evidences);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                startLatch.await();
                List<String> evidences = baiDuSearch.getEvidences("苹果的创始人是谁？", CblockExtration.create());
                System.out.println(evidences);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });
        thread1.start();
        thread2.start();
        startLatch.countDown();
        endLatch.await();
    }

    // Error :: 因为文件的共享
    // 已经修正
    @Test
    public void testQASystem() throws InterruptedException {
        BaiDuSearch baiDuSearch = new BaiDuSearch(3);
        Segmenter segmenter = Jieba.getInstance();
        QA_System qa_system = QA_System.getInstance(baiDuSearch, segmenter);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(2);
        Thread thread1 = new Thread(() -> {
            try {
                startLatch.await();
                List<String> answer = qa_system.answer("姚明的妻子是谁？", CblockExtration.create());
                System.out.println(answer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                startLatch.await();
                List<String> answer = qa_system.answer("苹果现任的CEO是谁？", CblockExtration.create());
                System.out.println(answer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });
        thread1.start();
        thread2.start();
        startLatch.countDown();
        endLatch.await();
    }

}
