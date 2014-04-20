package os.milestone3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class LogFile {

	private String directory;
	private String filename;
	private boolean append;
	private final String DEFAULT_APP_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/fuel_defender/";
	
	/**========================================================================
	 * public LogFile
	 * ------------------------------------------------------------------------
	 */
	public LogFile(String filename, boolean append) {
		this.directory = DEFAULT_APP_DIRECTORY;
		this.filename = this.directory + filename;
		this.append = append;
		
		// Create directory, if needed
		File logDirectory = new File(this.directory);
		logDirectory.mkdirs();
	}
	
	/**========================================================================
	 * public LogFile
	 * ------------------------------------------------------------------------
	 */
	public LogFile(String directory, String filename, boolean append) {
		this.directory = Environment.getExternalStorageDirectory().getPath() + directory;
		this.filename = this.directory + filename;
		this.append = append;
		
		// Create directory, if needed
		File logDirectory = new File(this.directory);
		logDirectory.mkdirs();
	}
	
	/**========================================================================
	 * public void write()
	 * ------------------------------------------------------------------------
	 */
	public void write(String s) {
		
		File logFile = new File(filename);

		try
		{
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, append));
			
			buf.write(s);
			buf.newLine();
			
			buf.close();
			buf = null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**========================================================================
	 * public void delete()
	 * ------------------------------------------------------------------------
	 */
	public boolean delete() {
		File logFile = new File(filename);
		return logFile.delete();
	}
}
