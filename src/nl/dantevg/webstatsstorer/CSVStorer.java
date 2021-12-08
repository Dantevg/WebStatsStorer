package nl.dantevg.webstatsstorer;

import nl.dantevg.webstats.StatData;
import nl.dantevg.webstats.Stats;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CSVStorer {
	private final WebStatsStorer plugin;
	private final String filename;
	private final File file;
	private FileWriter writer;
	
	public CSVStorer(WebStatsStorer plugin, String filename) {
		this.plugin = plugin;
		this.filename = filename;
		this.file = new File(plugin.getDataFolder(), filename);
		
		ensureFileExists();
	}
	
	public void store() {
		try {
			// Create new file if it was deleted after plugin enable
			if (!ensureFileExists()) return;
			
			List<String> columns = getColumns();
			StatData.Stats stats = Stats.getStats();
			writer = new FileWriter(file);
			if (columns.size() == 0) setColumns(stats.scores.keySet());
			
			for (String column : stats.scores.keySet()) {
				Map<String, Object> scores = stats.scores.get(column);
				// TODO: write scores
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getColumns() throws IOException {
		List<String> columns = new ArrayList<>();
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter(",");
		
		while (scanner.hasNext()) columns.add(scanner.next());
		
		scanner.close();
		return columns;
	}
	
	private void setColumns(Set<String> columns) throws IOException {
		writer.write(String.join(",", columns));
	}
	
	private boolean ensureFileExists() {
		try {
			file.createNewFile(); // Create new file if it did not yet exist
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if (!file.isFile()) {
			// File does exist and is not a file (a directory)
			plugin.getLogger().severe(filename + " exists and is a directory. Please remove");
			return false;
		}
		return true;
	}
	
}
