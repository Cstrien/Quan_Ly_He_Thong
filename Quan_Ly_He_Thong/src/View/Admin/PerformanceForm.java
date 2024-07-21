package View.Admin;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceForm extends JFrame {
    private static final int MAX_DATA_POINTS = 50; // Maximum number of data points on the chart
    private XYSeries cpuSeries;
    private XYSeries ramSeries;
    private XYSeries diskSeries;
    private PrintWriter out;
    private BufferedReader in;
    private AtomicInteger currentIndex;
    private JFreeChart chart; // Declare the chart instance at class level
    private volatile boolean running; // Add a flag to control the thread

    public PerformanceForm(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
        this.currentIndex = new AtomicInteger(0);
        this.running = true; // Initialize the flag
        initializeUI();
        startListening();
    }

    private void initializeUI() {
        setTitle("System Performance");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cpuSeries = new XYSeries("CPU Usage (%)");
        ramSeries = new XYSeries("RAM Usage (%)");
        diskSeries = new XYSeries("Disk Usage (%)");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(cpuSeries);
        dataset.addSeries(ramSeries);
        dataset.addSeries(diskSeries);

        chart = ChartFactory.createXYLineChart(
                "System Performance",
                "Time (seconds)",
                "Usage (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        // Customizing line colors and shapes
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.RED);   // CPU Usage - Red
        renderer.setSeriesPaint(1, Color.BLUE);  // RAM Usage - Blue
        renderer.setSeriesPaint(2, Color.GREEN); // Disk Usage - Green
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);

        // Add a window listener to stop the thread when the window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
            }
        });
    }

    private void startListening() {
        new Thread(() -> {
            try {
                String response;
                while (running && (response = in.readLine()) != null) {
                    final String finalResponse = response; // Declare response as final
                    EventQueue.invokeLater(() -> updateChart(finalResponse));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateChart(String data) {
        int index = currentIndex.getAndIncrement();
        if (data.startsWith("CPU_INFO:")) {
            double cpuUsage = extractUsage(data);
            cpuSeries.add(index, cpuUsage);
        } else if (data.startsWith("RAM_INFO:")) {
            double ramUsage = extractUsage(data);
            ramSeries.add(index, ramUsage);
        } else if (data.startsWith("DISK_INFO:")) {
            double diskUsage = extractUsage(data);
            diskSeries.add(index, diskUsage);
        }

        // Limit the number of data points on the chart
        if (index >= MAX_DATA_POINTS) {
            cpuSeries.remove(0);
            ramSeries.remove(0);
            diskSeries.remove(0);
        }

        // Automatically scroll the chart
        scrollChart();
    }

    private void scrollChart() {
        // Get the display range of the chart
        XYPlot plot = chart.getXYPlot();
        double lastX = plot.getDataset().getXValue(0, plot.getDataset().getItemCount(0) - 1);

        // Update the display range on the x-axis of the chart
        plot.getDomainAxis().setRange(lastX - MAX_DATA_POINTS + 1, lastX);
    }

    private double extractUsage(String data) {
        String[] parts = data.split(":");
        if (parts.length > 1) {
            try {
                return Double.parseDouble(parts[1].trim().replace("%", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PrintWriter dummyOut = new PrintWriter(System.out); // Replace with your actual PrintWriter
            BufferedReader dummyIn = new BufferedReader(new java.io.StringReader("CPU_INFO:20%\nRAM_INFO:30%\nDISK_INFO:40%\n")); // Replace with your actual BufferedReader
            PerformanceForm form = new PerformanceForm(dummyOut, dummyIn);
            form.setVisible(true);
        });
    }
}
