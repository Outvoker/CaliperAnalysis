package org.fudan.blockchain;

import java.io.IOException;

public class Main {
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
