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
			if (columns.isEmpty()) {
				columns = stats.columns;
				setColumns(stats.columns);
			}
			
			// Write a line of scores for every player
			for (String entry : stats.entries) {
				List<String> scoreList = new ArrayList<>();
				scoreList.add(entry);
				columns.forEach(column -> scoreList.add(
						column.equalsIgnoreCase("Player")
								? entry
								: (String) stats.scores.get(column).get(entry)));
				writer.write(String.join(",", scoreList) + "\n");
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
	
	private void setColumns(List<String> columns) throws IOException {
		columns.add(0, "Player");
		writer.write(String.join(",", columns) + "\n");
	}
	
	private boolean ensureFileExists() {
		try {
			// Create new file if it did not yet exist
			file.createNewFile();
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
