package tf.fox.gymlocked;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("gymlocked")
public interface GymlockedConfig extends Config
{
	@ConfigItem(
			keyName = "availableXp",
			name = "Available XP",
			description = "Current available XP left."
	)
	default int availableXp() { return 10000; }

	@ConfigItem(
			keyName = "xpUnlocked",
			name = "XP Unlocked Total",
			description = "Sum of all XP ever unlocked via the plugin."
	)
	default int xpUnlocked() { return 0; }

	@ConfigItem(
			keyName = "lastTotalXp",
			name = "Last Total XP",
			description = "Total account XP recorded on last client shutdown."
	)
	default int lastTotalXp() { return 0; }

	@ConfigItem(
			keyName = "personalNotes",
			name = "Personal Notes",
			description = "Personal notes for tracking your progress."
	)
	default String personalNotes() { return ""; }

	@ConfigItem(
			keyName = "globalModifier",
			name = "Global Modifier",
			description = "Global modifier value for XP calculations (-5 to +5)."
	)
	default int globalModifier() { return 0; }
}
