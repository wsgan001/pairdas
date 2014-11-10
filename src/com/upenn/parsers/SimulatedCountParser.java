package com.upenn.parsers;

import com.sun.xml.internal.ws.api.client.SelectOptimalEncodingFeature;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by chengjia on 11/9/14.
 */
public class SimulatedCountParser {
    String fileName = null;

    public SimulatedCountParser(String fileName) {
        this.fileName = fileName;
    }

    public SimulatedCounts ReadAllCounts(){
        SimulatedCounts allCounts = new SimulatedCounts();
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = in.readLine()) != null){
                allCounts.addLine(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allCounts;
    }
}