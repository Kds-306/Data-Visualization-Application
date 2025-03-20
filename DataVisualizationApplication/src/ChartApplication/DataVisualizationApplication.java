package ChartApplication; // Ensure this matches your directory structure

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DataVisualizationApplication extends JFrame {

    private static final long serialVersionUID = 1L;
    private DefaultCategoryDataset barDataset;
    private XYSeries lineSeries;
    private DefaultPieDataset pieDataset;
    private JTable dataTable;
    private JComboBox<String> chartTypeComboBox;

    public DataVisualizationApplication(String title) {
        super(title);
        barDataset = new DefaultCategoryDataset();
        lineSeries = new XYSeries("Values");
        pieDataset = new DefaultPieDataset();
        createUI();
    }

    private void createUI() {
        // Create a table for data entry
        String[] columnNames = {"Category", "Value"}; // Only one category per row
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        dataTable = new JTable(model);
        dataTable.setRowHeight(30); // Set row height to ensure visibility
        JScrollPane scrollPane = new JScrollPane(dataTable);
        
        // Set preferred size for the scroll pane to show at least 10 rows
        scrollPane.setPreferredSize(new Dimension(800, 300)); // Adjust height as needed

        // Create buttons for adding and removing data
        JButton addButton = new JButton("Add Data");
        JButton addRowButton = new JButton("Add Row");
        JButton removeRowButton = new JButton("Remove Row");

        // Create a combo box for chart type selection
        String[] chartTypes = {"Bar Chart", "Line Chart", "Pie Chart"};
        chartTypeComboBox = new JComboBox<>(chartTypes);

        // Add action listener to the button to add data
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear previous datasets
                barDataset.clear();
                lineSeries.clear();
                pieDataset.clear();

                for (int i = 0; i < model.getRowCount(); i++) {
                    String category = (String) model.getValueAt(i, 0); // Single category
                    String valueStr = (String) model.getValueAt(i, 1);
                    
                    // Check if the category or value is empty
                    if (category == null || category.trim().isEmpty() || valueStr == null || valueStr.trim().isEmpty()) {
                        continue; // Skip empty rows
                    }

                    try {
                        double value = Double.parseDouble(valueStr);
                        // Add to bar dataset
                        barDataset.addValue(value, "Values", category);
                        
                        // Use (i + 1) as the X value for the line series
                        lineSeries.add(i + 1, value); // X value is the index + 1
                        
                        // Add to pie dataset
                        pieDataset.setValue(category, value);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(DataVisualizationApplication.this,
                                "Invalid value in row " + (i + 1) + ". Please enter a valid number.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                showChart(); // Show the updated chart
            }
        });

        // Add action listener to the button to add a new row
        addRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow(new Object[]{"", ""}); // Add an empty row
            }
        });

        // Add action listener to the button to remove the selected row
        removeRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = dataTable.getSelectedRow();
                if (selectedRow != -1) {
                    model.removeRow(selectedRow); // Remove the selected row
                } else {
                    JOptionPane.showMessageDialog(DataVisualizationApplication.this,
                            "Please select a row to remove.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(scrollPane, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.SOUTH);
        inputPanel.add(chartTypeComboBox, BorderLayout.NORTH);
        inputPanel.add(addRowButton, BorderLayout.EAST);
        inputPanel.add(removeRowButton, BorderLayout.WEST);

        // Set up the main frame
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void showChart() {
        String selectedChartType = (String) chartTypeComboBox.getSelectedItem();
        JFreeChart chart;

        switch (selectedChartType) {
            case "Bar Chart":
                chart = createBarChart(barDataset);
                break;
            case "Line Chart":
                chart = createLineChart();
                break;
            case "Pie Chart":
                chart = createPieChart();
                break;
            default:
                return;
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        setContentPane(chartPanel);
        revalidate(); // Refresh the frame to show the new chart
    }

    private JFreeChart createBarChart(CategoryDataset dataset) {
        return ChartFactory.createBarChart(
                "Bar Chart",
                "Category",
                "Value",
                dataset
        );
    }

    private JFreeChart createLineChart() {
        XYSeriesCollection dataset = new XYSeriesCollection(lineSeries);
        return ChartFactory.createXYLineChart(
                "Line Chart",
                "X",
                "Value",
                dataset
        );
    }

    private JFreeChart createPieChart() {
        JFreeChart chart = ChartFactory.createPieChart(
                "Pie Chart",
                pieDataset,
                true, // include legend
                true, // include tooltips
                false // include URLs
        );

        // Set custom label generator for pie chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({1})")); // Show category and value

        return chart;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataVisualizationApplication app = new DataVisualizationApplication("Chart Example Application");
            app.setVisible(true);
        });
    }
}