package me.limeglass.skore;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import me.limeglass.skore.utils.ScoreboardManager;

public class Skore extends JavaPlugin {

	private static Skore instance;
	private SkriptAddon addon;
	private Metrics metrics;

	public void onEnable() {
		instance = this;
		addon = Skript.registerAddon(this);
		Bukkit.getPluginManager().registerEvents(new ScoreboardManager(), this);
//		metrics = new Metrics(this, 1234);
//		metrics.addCustomChart(new SimplePie("skriptVersion", () ->
//			Skript.getInstance().getDescription().getVersion()
//		));
	}

	public SkriptAddon getAddonInstance() {
		return addon;
	}

	public static Skore getInstance() {
		return instance;
	}

	public Metrics getMetrics() {
		return metrics;
	}

}
