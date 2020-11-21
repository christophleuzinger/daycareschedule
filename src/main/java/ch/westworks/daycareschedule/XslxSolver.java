
package ch.westworks.daycareschedule;

import ch.westworks.daycareschedule.xlsx.XlsxInputReader;
import ch.westworks.daycareschedule.xlsx.XlsxOutputWriter;

import java.io.File;
import java.awt.*;

import static java.awt.Desktop.getDesktop;

public class XslxSolver{

	public static void main(String[] args){
		Frame f= new Frame();
		f.setVisible(true);

		FileDialog fd = new FileDialog(f, "Solver Input", FileDialog.LOAD);
		fd.setFile("*.xlsx");
		fd.setVisible(true);

		assert args[0].contains(".xlsx");
		try{
			final File file = new File(fd.getDirectory() + fd.getFile());

			final Input input = new XlsxInputReader().readInput(file);
			final Solution solution = new Solver(input).solve();

			final File output = File.createTempFile(file.getName().replace(".xlsx","solution"), ".xlsx", new File("."));
			new XlsxOutputWriter().writeOutput(output, input, solution);
			getDesktop().open(output);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		f.dispose();
	}
}
