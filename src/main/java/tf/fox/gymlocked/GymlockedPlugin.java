package tf.fox.gymlocked;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "Gymlocked Tracker",
	description = "Tracks XP gained and be able to manually set available XP for special XP locked accounts.",
	tags = {"xp", "tracker", "gymlocked", "snowflake"}
)
public class GymlockedPlugin extends Plugin
{
	@Getter @Setter private int availableXp;
	@Getter @Setter private int xpUnlockedTotal;
	@Getter @Setter private int lastTotalXp;

	@Inject private Client client;
	@Inject private GymlockedConfig config;
	@Inject private ConfigManager configManager;
	@Inject private GymlockedOverlay greyOverlay;
	@Inject private GymlockedInfoOverlay infoOverlay;
	@Inject private OverlayManager overlayManager;
	@Inject private ClientToolbar clientToolbar;

	private GymlockedPanel panel;
	private NavigationButton navigationButton;
	private boolean fetchXp = false;

	@Provides
	GymlockedConfig provideConfig(ConfigManager cm)
	{
		return cm.getConfig(GymlockedConfig.class);
	}

	@Override
	protected void startUp() {
		availableXp     = config.availableXp();
		xpUnlockedTotal = config.xpUnlocked();
		lastTotalXp     = config.lastTotalXp();


		panel = injector.getInstance(GymlockedPanel.class);
		panel.init(this);

		navigationButton = NavigationButton
				.builder()
				.tooltip("Gymlocked")
				.icon(ImageUtil.loadImageResource(getClass(), "/gym_icon.png"))
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navigationButton);
		overlayManager.add(greyOverlay);
		overlayManager.add(infoOverlay);
	}

	private int calculateTotalXp()
	{
		return Arrays.stream(Skill.values()).mapToInt(client::getSkillExperience).sum();
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navigationButton);
		overlayManager.remove(greyOverlay);
		overlayManager.remove(infoOverlay);
		persist();
	}

	void persist()
	{
		configManager.setConfiguration("gymlocked", "availableXp", availableXp);
		configManager.setConfiguration("gymlocked", "xpUnlocked", xpUnlockedTotal);
		configManager.setConfiguration("gymlocked", "lastTotalXp", lastTotalXp);
	}

	public void addUnlockedXp(int delta)
	{
		xpUnlockedTotal += delta;
		availableXp     += delta;
		persist();
		panel.refresh();
	}

	public void resetCounters()
	{
		availableXp = 0;
		xpUnlockedTotal = 0;
		lastTotalXp = calculateTotalXp();
		persist();
		panel.refresh();
	}

    public boolean shouldGrey()
	{
		return availableXp < 0;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			// xp is not available until after login is finished, so fetch it on the next gametick
			fetchXp = true;
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if(fetchXp) {
			int currentTotal = calculateTotalXp();

			if (lastTotalXp == 0) {
				lastTotalXp = currentTotal;
				persist();
			} else {
				int offlineGain = currentTotal - lastTotalXp;
				if (offlineGain > 0) {
					availableXp -= offlineGain;
				}
				lastTotalXp = currentTotal;
				persist();
			}

			fetchXp = false;
		}

		int currentTotal = calculateTotalXp();
		int gained = currentTotal - lastTotalXp;
		if (gained > 0)
		{
			availableXp -= gained;
			lastTotalXp  = currentTotal;
			persist();
			panel.refresh();
		}
		else if (gained < 0) // should never happen but handle de‑leveling, etc.
		{
			lastTotalXp = currentTotal;
			persist();
		}
	}
}
