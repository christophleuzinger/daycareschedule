
package ch.westworks.daycareschedule;

import ch.westworks.daycareschedule.xlsx.XlsxInputReader;
import ch.westworks.daycareschedule.xlsx.XlsxOutputWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

import static java.awt.Desktop.getDesktop;

public class XlsxSolver {

    public static final String XLSX = "xlsx";
    public static final String XLSX_SUFFIX = "." + XLSX;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel", XLSX));
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.showOpenDialog(null);
            final File file = fileChooser.getSelectedFile();
            if (file != null) {
                final Input input = new XlsxInputReader().readInput(file);
                final Solution solution = new Solver(input).solve();
                final File output = File.createTempFile(file.getName().replace(XLSX_SUFFIX, "_Solution_"), XLSX_SUFFIX, file.getParentFile());
                new XlsxOutputWriter().writeOutput(output, input, solution);
                getDesktop().open(output);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": " + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }
}
