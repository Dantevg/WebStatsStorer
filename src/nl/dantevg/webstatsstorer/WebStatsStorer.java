package nl.dantevg.webstatsstorer;

import nl.dantevg.webstats.StatData;
import nl.dantevg.webstats.Stats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class WebStatsStorer extends JavaPlugin implements CommandExecutor {
	public static final String FILENAME = "stats.csv";
	private final File file = new File(getDataFolder(), FILENAME);
	
	@Override
	public void onEnable() {
		getCommand("storestats").setExecutor(this);
		try {
			file.createNewFile(); // Create new file if it did not yet exist
		} catch (IOException e) {
			e.printStackTrace();
			getPluginLoader().disablePlugin(this);
			return;
		}
		if (!file.isFile()) {
			// File does exist and is not a file (a directory)
			getLogger().severe(FILENAME + " file exists and is a directory. Please remove");
			getPluginLoader().disablePlugin(this);
			return;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("storestats")) {
			getLogger().info("Storing statistics");
			storeStats();
		}
		return true;
	}
	
	private void storeStats() {
		try {
			file.createNewFile(); // Create new file if it was deleted after plugin enable
			List<String> filter = getHeader();
			StatData.Stats stats = Stats.getStats();
			FileWriter writer = new FileWriter(file);
			if(filter.size() == 0) initFile(writer, stats.scores.keySet());
			for(String column : stats.scores.keySet()){
				Map<String, Object> scores = stats.scores.get(column);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getHeader() throws IOException {
		List<String> columns = new ArrayList<>();
		Scanner scanner = new Scanner(file);
		scanner.useDelimiter(",");
		
		while (scanner.hasNext()) columns.add(scanner.next());
		
		scanner.close();
		return columns;
	}
	
	private void initFile(FileWriter writer, Set<String> columns) throws IOException {
		writer.write(String.join(",", columns));
	}
	
}
