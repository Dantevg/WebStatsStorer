package nl.dantevg.webstatsstorer;

import nl.dantevg.webstats.StatData;
import nl.dantevg.webstats.Stats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;

public class CSVStorer {
	private final @NotNull WebStatsStorer plugin;
	private final @NotNull String filename;
	private final @NotNull File file;
	private @Nullable FileWriter writer;
	
	public CSVStorer(@NotNull WebStatsStorer plugin, @NotNull String filename) {
		this.plugin = plugin;
		this.filename = filename;
		file = new File(plugin.getDataFolder(), filename);
		plugin.getDataFolder().mkdirs(); // Create plugin folder if not existing
		
		ensureFileExists();
	}
	
	public boolean store() {
		try {
			// Create new file if it was deleted after plugin enable
			if (!ensureFileExists()) return false;
			
			writer = new FileWriter(file, true);
			StatData.Stats stats = Stats.getStats();
			List<String> columns = getColumns(stats);
			
			// Write a line of scores for every player
			for (String entry : stats.entries) {
				List<String> scoreList = new ArrayList<>();
				boolean hasScores = false;
				for (String column : columns) {
					if (column.equalsIgnoreCase("player")) {
						// Player's name
						scoreList.add(entry);
					} else if (column.equalsIgnoreCase("timestamp")) {
						// Number of seconds since unix epoch, in UTC+0 timezone
						scoreList.add(Instant.now().getEpochSecond() + "");
					} else if (column.equalsIgnoreCase("date")) {
						// Date in YYYY-MM-DD format, local timezone
						scoreList.add(LocalDate.now().toString());
					} else if (stats.scores.contains(column, entry)) {
						scoreList.add(stats.scores.get(column, entry));
						hasScores = true;
					} else {
						// Add empty score so the columns stay aligned
						scoreList.add("");
					}
				}
				
				if (hasScores) writer.append(String.join(",", scoreList)).append("\n");
			}
			
			writer.close();
			writer = null;
			return true;
		} catch (IOException e) {
			plugin.getLogger().log(Level.WARNING, "Could not write scores to file " + file.getPath(), e);
		}
		return false;
	}
	
	private @NotNull List<String> readColumns() throws IOException {
		List<String> columns = new ArrayList<>();
		
		String line;
		try (Scanner lineScanner = new Scanner(file)) {
			line = lineScanner.nextLine();
		} catch (NoSuchElementException e) {
			return columns;
		}
		
		Scanner columnScanner = new Scanner(line);
		columnScanner.useDelimiter(",");
		while (columnScanner.hasNext()) columns.add(columnScanner.next());
		columnScanner.close();
		
		return columns;
	}
	
	private @NotNull List<String> getColumns(StatData.@NotNull Stats stats) throws IOException {
		// Try to read columns from file
		List<String> columns = readColumns();
		if (!columns.isEmpty()) return columns;
		
		// No columns present in file (file is empty), get columns and write
		columns = (stats.columns != null)
				? stats.columns
				: new ArrayList<>(stats.scores.rowKeySet());
		
		// Add initial columns and write to empty file
		if (!columns.contains("Player")) columns.add(0, "Player");
		if (!columns.contains("Date")) columns.add(0, "Date");
		writeColumns(columns);
		
		return columns;
	}
	
	private void writeColumns(@NotNull List<String> columns) throws IOException {
		writer.append(String.join(",", columns)).append("\n");
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
