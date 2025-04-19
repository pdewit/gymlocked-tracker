package tf.fox.gymlocked;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

public class GymlockedOverlay extends Overlay
{
    @Inject private GymlockedPlugin plugin;
    @Inject private Client client;

    public GymlockedOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        if (!plugin.shouldGrey())
        {
            return null;
        }

        int width  = client.getCanvasWidth();
        int height = client.getCanvasHeight();
        g.setComposite(AlphaComposite.SrcOver.derive(0.75f));
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, width, height);
        return null;
    }
}