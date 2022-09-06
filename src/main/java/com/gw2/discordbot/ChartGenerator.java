package com.gw2.discordbot;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;

import kotlin.Pair;

public class ChartGenerator {
    
    static PieChart chart;

    public static File generateChart(String title, List<Pair<String, Integer>> pairs) {
        
        chart = new PieChartBuilder().width(800).height(600).title(title).build();

        chart.getStyler().setChartTitleFont(new Font(Font.SERIF, Font.BOLD, 30));
        chart.getStyler().setLegendFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
        chart.getStyler().setLabelsFont(new Font(Font.SERIF, Font.PLAIN, 22));

        for(Pair<String, Integer> pair : pairs) {
            chart.addSeries(String.format("%-15s - %-5s DPS", pair.getFirst().length() > 13 ? pair.getFirst().substring(0, 13) + "." : pair.getFirst(), String.valueOf(pair.getSecond())), pair.getSecond());
        }

        try {
            BitmapEncoder.saveBitmap(chart, "./bitmapsave", BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File("bitmapsave.png");

        chart = null;

        return file;
    }
}
