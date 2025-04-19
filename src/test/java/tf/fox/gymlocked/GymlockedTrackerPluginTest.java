package tf.fox.gymlocked;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GymlockedTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GymlockedPlugin.class);
		RuneLite.main(args);
	}
}