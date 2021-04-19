package org.fudan.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
public class ResultAnalysis {
    //csv headers
//    final String[] HEADERS = { "Name", "Succ", "Fail", "Send Rate (TPS)", "Max Latency (s)", "Min Latency (s)", "Avg Latency (s)", "Throughput (TPS)", "Benchmark", "Benchmark config", "SUT", "Time"};
    final String[] HEADERS = { "Name", "Succ", "Fail", "Send Rate (TPS)", "Max Latency (s)", "Min Latency (s)", "Avg Latency (s)", "Throughput (TPS)", "Time"};
    //out put file name
    final String outputFileName = "analysisResult.csv";

    String inputPath;
    String outputPath;
    FileWriter out;
    CSVPrinter printer;

    ResultAnalysis(String inputPath, String outputPath) throws IOException {
        this(inputPath, outputPath, true);
    }

    ResultAnalysis(String inputPath, String outputPath, boolean append) throws IOException {
        log.info("Analysis Init ...");
        log.info("inputPath: {}, outputPath: {}, append: {}", inputPath, outputPath, append);

        this.inputPath = inputPath;
        this.outputPath = outputPath;

        String outputFilePath = outputPath + File.separator + outputFileName;
        // create output file
        File file = new File(outputFilePath);
        if(file.exists() && append) {
            log.info("File {} exists, appending ...", file.getAbsolutePath());
            out = new FileWriter(outputFilePath, true);
            printer = new CSVPrinter(out, CSVFormat.DEFAULT);
        }
        else {
            if(file.exists()) log.info("File {} exists, overwriting ...", file.getAbsolutePath());
            else log.info("File not exists, creating ... {}", file.getAbsolutePath());
            out = new FileWriter(outputFilePath);
            printer = new CSVPrinter(out, CSVFormat.DEFAULT
                    .withHeader(HEADERS));
            printer.flush();
        }
    }

    public void analysis(String name){
        log.info("Analysis {} ...", name);
        File file = new File(inputPath + File.separator + name);
        long time = file.lastModified();
        try {
            //get summary
            Document doc = Jsoup.parse(file, "UTF-8");
            Elements elements = doc.getElementById("benchmarksummary").getElementsByTag("table").last().children().last().children().last().children();
            List<String> list = elements.eachText();

//            //get benchmark
//            String benchmark = doc.getElementById("samplecc test").getElementsByTag("pre").text();
//            list.add(benchmark);
//
//            //get benchmark config
//            String benchmarkInfo = doc.getElementById("benchmarkInfo").text();
//            list.add(benchmarkInfo);
//
//            //get SUT
//            String SUT = doc.getElementById("sutdetails").text();
//            list.add(SUT);

            //add result file create time
            list.add(new Date(time).toString());

            //write a record into csv
            printer.printRecord(list);
            printer.flush();

            //log
            log.info("{}", list);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void close() {
        log.info("Result Analysis over ...");
        try {
            printer.close(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputPath = "D:\\university\\blockchain\\experiment\\result";
        String outputPath = "D:\\university\\blockchain\\experiment\\analysis";
        ResultAnalysis resultAnalysis = null;
        try {
            resultAnalysis = new ResultAnalysis(inputPath, outputPath);
            resultAnalysis.analysis("1.html");
            resultAnalysis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
