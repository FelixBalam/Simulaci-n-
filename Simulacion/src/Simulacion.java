import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Simulacion extends JFrame {
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private File selectedFile;
    private JLabel meanLabel, runsLabel, zLabel, independenceLabel, stepsLabel, intermediateLabel;

    public Simulacion() {
        setTitle("Prueba de Corridas Arriba y Abajo de la Media");
        setSize(900, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 250));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel para selección de archivo
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
        filePanel.setBorder(BorderFactory.createTitledBorder("Seleccione el archivo .txt"));
        filePanel.setBackground(new Color(240, 240, 250));
        JLabel fileLabel = new JLabel("Elija un archivo con números separados por saltos de línea:");
        fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fileLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton fileButton = new JButton("Cargar archivo");
        styleButton(fileButton);
        filePanel.add(fileLabel);
        filePanel.add(Box.createVerticalStrut(10));
        filePanel.add(fileButton);

        // Tabla para mostrar los datos y análisis
        JPanel resultPanel = new JPanel(new BorderLayout(5, 5));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Resultados del Análisis"));
        tableModel = new DefaultTableModel(new Object[]{"Número", "Media", "Posición respecto a la Media"}, 0);
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 13));
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        resultTable.getTableHeader().setBackground(new Color(70, 130, 180));
        resultTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de estadísticas y resultados
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(7, 1, 10, 10));  // Reducido a 7 filas
        statsPanel.setBorder(BorderFactory.createTitledBorder("Estadísticas de Corridas"));
        statsPanel.setBackground(new Color(240, 240, 250));

        meanLabel = new JLabel("Media: ");
        runsLabel = new JLabel("Número de Corridas: ");
        zLabel = new JLabel("Valor Z: ");
        independenceLabel = new JLabel("Resultado: ");
        stepsLabel = new JLabel("Pasos del Cálculo: ");
        intermediateLabel = new JLabel("Operaciones Intermedias: ");
        
        styleLabel(meanLabel);
        styleLabel(runsLabel);
        styleLabel(zLabel);
        styleLabel(independenceLabel);
        styleLabel(stepsLabel);
        styleLabel(intermediateLabel);

        statsPanel.add(meanLabel);
        statsPanel.add(runsLabel);
        statsPanel.add(zLabel);
        statsPanel.add(independenceLabel);
        statsPanel.add(stepsLabel);
        statsPanel.add(intermediateLabel);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton analyzeButton = new JButton("Analizar");
        JButton clearButton = new JButton("Limpiar");
        styleButton(analyzeButton);
        styleButton(clearButton);
        buttonPanel.add(analyzeButton);
        buttonPanel.add(clearButton);

        // Agregar secciones al panel principal
        mainPanel.add(filePanel, BorderLayout.NORTH);
        mainPanel.add(resultPanel, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Acción para cargar el archivo
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(Simulacion.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    //JOptionPane.showMessageDialog(Simulacion.this, "Archivo seleccionado: " + selectedFile.getName());
                    fileLabel.setText("Archivo seleccionado: " + selectedFile.getName()); //Cambia el texto de la etiqueta en vez de mostrar un popup
                }
            }
        });

        // Acción para analizar los datos
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    analyzeDataFromFile();
                } else {
                    JOptionPane.showMessageDialog(Simulacion.this, 
                                                  "Por favor, seleccione un archivo antes de analizar.", 
                                                  "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción para limpiar la tabla y resultados
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
                meanLabel.setText("Media: ");
                runsLabel.setText("Número de Corridas: ");
                zLabel.setText("Valor Z: ");
                independenceLabel.setText("Resultado: ");
                stepsLabel.setText("Pasos del Cálculo: ");
                intermediateLabel.setText("Operaciones Intermedias: ");
                selectedFile = null;
            }
        });
    }

    // Método para dar estilo a los botones
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    // Método para dar estilo a las etiquetas
    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(0, 51, 102));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // Método para analizar los datos del archivo
    private void analyzeDataFromFile() {
        ArrayList<Double> data = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(selectedFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    data.add(Double.parseDouble(line));
                }
            }
            
            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El archivo está vacío o no contiene números válidos.", 
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double sum = data.stream().mapToDouble(Double::doubleValue).sum();
            //double mean = sum / data.size(); //La media no se calcula, siempre es 0.5
            double mean = 0.5;

            tableModel.setRowCount(0);
            ArrayList<String> runs = new ArrayList<>();

            for (double num : data) {
                String position = (num >= mean) ? "1" : "0"; //Arriba se representa con 1, abajo con 0
                tableModel.addRow(new Object[]{num, mean, position});
                runs.add(position);
            }

            //System.out.println(runs.size());
            calculateRunsTest(runs, mean);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Archivo no encontrado.", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El archivo contiene datos no válidos. Asegúrese de que sean números separados por saltos de línea.", 
                                          "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para calcular las corridas y realizar los análisis
    private void calculateRunsTest(ArrayList<String> runs, double mean) {
        int numCorridas = 1; //numCorridas debe empezar cómo 1
        int countUp = 0;
        int countDown = 0;
        int n = runs.size(); //extraer n

        // Calcular las corridas y contar las veces que hay una transición entre "Arriba" y "Abajo"
        if (runs.get(0) == "1") {
            countUp++;
        } else {
            countDown++;
        }
        for (int i = 1; i < runs.size(); i++) {
            if (runs.get(i) == "1") {
                countUp++;
            } else {
                countDown++;
            }
            if (!runs.get(i).equals(runs.get(i - 1))) {
                numCorridas++;
            }
        }
        System.out.println(String.format("n={%d}, c1={%d}, c0={%d}",n, countUp, countDown));

        // Calcular los parámetros para la fórmula de Z
        double expected = ((2 * countUp * countDown) / (double) n) + 0.5;
        double variance = (2 * countUp * countDown * ((2 * countUp * countDown) - n)) //corrección en la fórmula
                          / (double) (n * n * (n - 1));
        double standardDeviation = Math.sqrt(variance);
        double zValue = (numCorridas - expected) / standardDeviation;

        // Mostrar los resultados
        runsLabel.setText("Número de Corridas: " + numCorridas);
        meanLabel.setText("Media: " + mean);
        zLabel.setText("Valor Z: " + zValue);
        intermediateLabel.setText("Operaciones Intermedias: \nC0 = " + numCorridas + " \nMu = " + expected 
                                  + " \nσ^2 = " + variance);

        if (Math.abs(zValue) <= 1.96) {
            independenceLabel.setText("Resultado: Los números son independientes.");
        } else {
            independenceLabel.setText("Resultado: Los números no son independientes.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Simulacion().setVisible(true);
            }
        });
    }
}
