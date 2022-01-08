package nl.dantevg.webstatsstorer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class WebStatsStorer extends JavaPlugin implements CommandExecutor {
	public static final String FILENAME = "stats.csv";
	private CSVStorer storer;
	
	@Override
	public void onEnable() {
		getCommand("storestats").setExecutor(this);
		storer = new CSVStorer(this, FILENAME);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		getLogger().info("Storing statistics");
		if (storer.store()) {
			sender.sendMessage("Storing stats finished");
		} else {
			sender.sendMessage("Could not store stats, check console");
		}
		return true;
	}
	
}
