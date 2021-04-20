package org.fudan.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class Monitor {
    final String resultDirectoryName = "result";
    SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddhhmmss");

    String inputPath;
    String outputPath;
    ResultAnalysis resultAnalysis;
    File resultDirectory;
    File monitorDirectory;

    long duration;
    long startTime;
    long endTime;

    Monitor(String inputPath, String outputPath) throws IOException {
        this(inputPath, outputPath, true, -1);
    }

    Monitor(String inputPath, String outputPath, boolean append) throws IOException {
        this(inputPath, outputPath, append, -1);
    }

    Monitor(String inputPath, String outputPath, long duration) throws IOException {
        this(inputPath, outputPath, true, duration);
    }

    /**
     * 初始化监视器
     * @param inputPath     监听路径（目录）
     * @param outputPath    输出分析结果路径（目录）
     * @param append        是否追加写（false为覆盖写）
     * @param duration      监听时常
     * @throws IOException  IO异常
     */
    Monitor(String inputPath, String outputPath, boolean append, long duration) throws IOException {
        log.info("Monitor Init ...");
        log.info("inputPath: {}, outputPath: {}, append: {}, duration: {} (s)", inputPath, outputPath, append, duration);

        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.duration = duration;
        monitorDirectory = new File(inputPath);
        resultDirectory = new File(outputPath + File.separator + resultDirectoryName);
        resultAnalysis = new ResultAnalysis(resultDirectory.getAbsolutePath(), outputPath, append);
        FileUtils.forceMkdir(resultDirectory);
    }

    /**
     * 开始监听inputPath目录
     * @throws IOException
     */
    void start() throws IOException {
        log.info("Start Monitoring ...");
        this.startTime = System.currentTimeMillis();
        if(duration <= 0) this.endTime = Long.MAX_VALUE;
        else endTime = startTime + duration * 1000;
        while(System.currentTimeMillis() <= endTime){
            String[] list = monitorDirectory.list();
            if(list == null) throw new IOException("Not a Directory");
            if(list.length > 0){
                for(String fileName : list){
                    if(!fileName.endsWith(".html")) continue;
                    File file = new File(inputPath + File.separator + fileName);
                    moveFileAndAnalysis(file);
                }
            }
        }
        log.info("Time is over.");
    }

    /**
     * 关闭监听
     */
    public void close(){
        resultAnalysis.close();
    }

    private void moveFileAndAnalysis(File source)
            throws IOException {
        File dest = new File(resultDirectory.getAbsolutePath() + File.separator + ft.format(new Date()) + "-" + source.getName());
        FileUtils.moveFile(source, dest);
        resultAnalysis.analysis(dest.getName());
    }

    public static void main(String[] args) {
        String inputPath = "D:\\university\\blockchain\\CaliperAnalysis\\result";
        String outputPath = "D:\\university\\blockchain\\CaliperAnalysis\\analysis";
        try {
            Monitor monitor = new Monitor(inputPath, outputPath, true, 100000);
            monitor.start();
            monitor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
