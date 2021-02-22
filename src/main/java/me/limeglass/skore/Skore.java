package me.limeglass.skore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jdt.annotation.Nullable;

import com.comphenix.protocol.utility.MinecraftVersion;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import me.limeglass.skore.elements.Register;
import me.limeglass.skore.utils.ScoreboardManager;
import me.limeglass.skore.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class Skore extends JavaPlugin {

	private static Map<String, FileConfiguration> files = new HashMap<String, FileConfiguration>();
	private String packageName = "me.limeglass.skore";
	private static String prefix = "&8[&6Skore&8] &e";
	private static String nameplate = "[Skore] ";
	private static Skore instance;
	private SkriptAddon addon;
	private Metrics metrics;

	public void onEnable() {
		if (!MinecraftVersion.atOrAbove(MinecraftVersion.AQUATIC_UPDATE)) {
			getPluginLoader().disablePlugin(this);
			Bukkit.getLogger().info(nameplate + " version 2.0.0+ of Skore will only run on 1.13+ currently. Please update your server or use the older 1.X Skore series.");
			return;
		}
		addon = Skript.registerAddon(this);
		instance = this;
		saveDefaultConfig();
		File config = new File(getDataFolder(), "config.yml");
		if (!Objects.equals(getDescription().getVersion(), getConfig().getString("version"))) {
			consoleMessage("&dNew update found! Updating files now...");
			if (config.exists()) new SpigotConfigSaver(this).execute();
		}
		for (String name : Arrays.asList("config", "syntax")) { //replace config with future files here
			File file = new File(getDataFolder(), name + ".yml");
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				saveResource(file.getName(), false);
			}
			FileConfiguration configuration = new YamlConfiguration();
			try {
				configuration.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
			files.put(name, configuration);
		}
		Bukkit.getPluginManager().registerEvents(new ScoreboardManager(), this);
		metrics = new Metrics(this);
		Register.metrics(metrics);
		if (!getConfig().getBoolean("DisableRegisteredInfo", false)) Bukkit.getLogger().info(nameplate + "has been enabled!");
	}

	public SkriptAddon getAddonInstance() {
		return addon;
	}

	public static String getNameplate() {
		return nameplate;
	}

	public static Skore getInstance() {
		return instance;
	}

	public static String getPrefix() {
		return prefix;
	}

	public String getPackageName() {
		return packageName;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	//Grabs a FileConfiguration of a defined name. The name can't contain .yml in it.
	public FileConfiguration getConfiguration(String file) {
		return (files.containsKey(file)) ? files.get(file) : null;
	}

	public static void save(String configuration) {
		try {
			File configurationFile = new File(instance.getDataFolder(), configuration + ".yml");
			instance.getConfiguration(configuration).save(configurationFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void debugMessage(@Nullable String... messages) {
		if (instance.getConfig().getBoolean("debug")) {
			for (String text : messages) consoleMessage("&b" + text);
		}
	}

	public static void infoMessage(@Nullable String... messages) {
		if (messages != null && messages.length > 0) {
			for (String text : messages) Bukkit.getLogger().info(getNameplate() + text);
		} else {
			Bukkit.getLogger().info("");
		}
	}

	public static void consoleMessage(@Nullable String... messages) {
		if (instance.getConfig().getBoolean("DisableConsoleMessages", false)) return;
		if (messages != null && messages.length > 0) {
			for (String text : messages) {
				if (instance.getConfig().getBoolean("DisableConsoleColour", false)) infoMessage(ChatColor.stripColor(Utils.cc(text)));
				else Bukkit.getConsoleSender().sendMessage(Utils.cc(prefix + text));
			}
		} else {
			Bukkit.getLogger().info("");
		}
	}

}