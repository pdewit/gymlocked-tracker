package tf.fox.gymlocked;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;

public class GymlockedInfoOverlay extends OverlayPanel
{
    @Inject private GymlockedPlugin plugin;

    private final static String AVAILABLE_XP = "Available XP:";
    private final static String XP_UNLOCKED = "XP Unlocked:";

    public GymlockedInfoOverlay()
    {
        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        String availableXp = String.format("%,d", plugin.getAvailableXp());
        String xpUnlocked = String.format("%,d", plugin.getXpUnlockedTotal());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(AVAILABLE_XP)
                .leftColor(Color.WHITE)
                .right(availableXp)
                .rightColor(plugin.getAvailableXp() < 0 ? Color.RED : Color.WHITE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(XP_UNLOCKED)
                .leftColor(Color.WHITE)
                .right(xpUnlocked)
                .rightColor(plugin.getXpUnlockedTotal() < 0 ? Color.RED : Color.WHITE)
                .build());

        panelComponent.setPreferredSize(new Dimension(getLongestStringWidth(new String[] { AVAILABLE_XP + " " + availableXp, XP_UNLOCKED + " " + xpUnlocked }, g) + 25, 0));

        return super.render(g);
    }

    private int getLongestStringWidth(String[] strings, Graphics2D graphics) {
        int longest = 0;
        for(String i: strings) {
            int currentItemWidth = graphics.getFontMetrics().stringWidth(i);
            if(currentItemWidth > longest) {
                longest = currentItemWidth;
            }
        }
        return longest;
    }
}