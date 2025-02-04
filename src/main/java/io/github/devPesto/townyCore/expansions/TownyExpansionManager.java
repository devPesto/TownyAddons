package io.github.devPesto.townyCore.expansions;

import io.github.devPesto.townyCore.TownyCore;
import io.github.devPesto.townyCore.config.Config;
import io.github.devPesto.townyCore.config.impl.PluginNodes;
import io.github.devPesto.townyCore.expansions.impl.MinerKitExpansion;
import io.github.devPesto.townyCore.expansions.impl.OldCombatSoundsExpansion;
import io.github.devPesto.townyCore.expansions.impl.SiegeRallyExpansion;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static io.github.devPesto.townyCore.expansions.TownyExpansion.MissingDependencyException;

public class TownyExpansionManager {
	private final Map<String, TownyExpansion> expansionMap;
	private final TownyCore plugin;
	private final Logger logger;
	private final Config config;

	public TownyExpansionManager(TownyCore plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		this.config = plugin.getConfiguration();
		this.expansionMap = loadExpansions();
	}

	public Map<String, TownyExpansion> getExpansions() {
		return expansionMap;
	}

	public void registerExpansions() {
		logger.info("================ [TownyCore] ================");
		expansionMap.values().forEach(expansion -> {
			try {
				expansion.register(plugin);
				logger.info("Successfully registered module: " + expansion.getName());
			} catch (MissingDependencyException e) {
				logger.severe(e.getMessage());
			}
		});
		logger.info("-------------------------------------------");
	}

	public boolean isApolloRequired() {
		return expansionMap.values()
				.stream()
				.flatMap(e -> e.getDependencies().stream())
				.anyMatch(d -> d.equalsIgnoreCase("apollo-bukkit"));
	}

	private Map<String, TownyExpansion> loadExpansions() {
		// Miner Kit
		Map<String, TownyExpansion> map = new HashMap<>();
		if (config.getBoolean(PluginNodes.EXPANSION_ENABLE_MINER_KIT))
			map.put("MinerKit", new MinerKitExpansion());

		// OldCombatSounds
		if (config.getBoolean(PluginNodes.EXPANSION_ENABLE_OC_SOUNDS))
			map.put("OldCombatSounds", new OldCombatSoundsExpansion());

		// Rallies
		if (config.getBoolean(PluginNodes.EXPANSION_ENABLE_RALLIES))
			map.put("SiegeRally", new SiegeRallyExpansion());

		return map;
	}
}
