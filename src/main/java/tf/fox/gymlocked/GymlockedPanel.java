package tf.fox.gymlocked;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.util.SwingUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Slf4j
@Singleton
public class GymlockedPanel extends PluginPanel
{
    private static final Dimension BTN_SIZE   = new Dimension(80, 26);
    private static final String    TITLE_TEXT = "Gymlocked";

    private final JLabel availableXpValue  = new JLabel("0");
    private final JLabel totalUnlockedValue = new JLabel("0");
    private final FlatTextField xpInputField = new FlatTextField();

    private static final int[] QUICK_AMOUNTS = {1_000, 10_000};

    private GymlockedPlugin plugin;

    public void init(GymlockedPlugin plugin) {
        this.plugin = plugin;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        add(buildHeader(), BorderLayout.NORTH);
        add(wrapScrollable(buildBody()), BorderLayout.CENTER);
        refresh();
    }

    private JComponent buildHeader()
    {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ColorScheme.DARK_GRAY_COLOR);
        header.setBorder(new EmptyBorder(8, 10, 6, 10));

        JLabel title = new JLabel(TITLE_TEXT);
        title.setFont(FontManager.getRunescapeBoldFont());
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);
        return header;
    }

    private JPanel buildBody()
    {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(ColorScheme.DARK_GRAY_COLOR);
        body.setBorder(new EmptyBorder(8, 10, 10, 10));

        body.add(statsRow("Available XP:",  availableXpValue));
        body.add(Box.createVerticalStrut(4));
        body.add(statsRow("Total Unlocked:", totalUnlockedValue));
        body.add(Box.createVerticalStrut(12));

        body.add(quickAddBar());
        body.add(Box.createVerticalStrut(8));

        xpInputField.setBorder(new EmptyBorder(5, 7, 5, 7));
        xpInputField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        xpInputField.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        body.add(xpInputField);

        body.add(Box.createVerticalStrut(8));
        body.add(buttonBar());

        body.add(Box.createVerticalGlue());
        return body;
    }

    private JPanel statsRow(String key, JLabel valueLabel)
    {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JLabel keyLabel = new JLabel(key);
        keyLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        keyLabel.setFont(FontManager.getRunescapeFont());

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(FontManager.getRunescapeSmallFont());

        row.add(keyLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private JPanel quickAddBar()
    {
        JPanel bar = new JPanel(new GridLayout(1, 0, 6, 0));
        bar.setBackground(ColorScheme.DARK_GRAY_COLOR);

        for (int amt : QUICK_AMOUNTS)
        {
            JButton btn = new JButton("+" + (amt / 1_000) + "k");
            styleButton(btn);
            btn.setPreferredSize(new Dimension(60, 24));
            btn.setFont(FontManager.getRunescapeSmallFont());
            btn.addActionListener(e -> plugin.addUnlockedXp(amt));
            bar.add(btn);
        }
        return bar;
    }

    private JPanel buttonBar()
    {
        JPanel bar = new JPanel(new GridLayout(1, 0, 6, 0));
        bar.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JButton add = new JButton("Add XP");
        styleButton(add);
        add.addActionListener(e -> addXp());

        JButton reset = new JButton("Reset");
        styleButton(reset);
        reset.addActionListener(e -> resetCounters());

        bar.add(add);
        bar.add(reset);
        return bar;
    }

    private void styleButton(JButton btn)
    {
        SwingUtil.removeButtonDecorations(btn);
        btn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(BTN_SIZE);
    }

    private JScrollPane wrapScrollable(JComponent c)
    {
        JScrollPane sp = new JScrollPane(c,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setViewportBorder(null);
        return sp;
    }

    private void addXp()
    {
        String input = xpInputField.getText().replace(",", "").trim();
        if (input.isEmpty())
        {
            return;
        }

        try
        {
            int xp = Integer.parseInt(input);
            plugin.addUnlockedXp(xp);
            xpInputField.setText("");
        }
        catch (NumberFormatException ignored) { }
    }

    private void resetCounters()
    {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset all counters?",
                "Reset confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION)
        {
            plugin.resetCounters();
        }
    }

    public void refresh()
    {
        availableXpValue.setText(format(plugin.getAvailableXp()));
        totalUnlockedValue.setText(format(plugin.getXpUnlockedTotal()));

        availableXpValue.setForeground(plugin.getAvailableXp() < 0
                ? ColorScheme.PROGRESS_ERROR_COLOR
                : Color.WHITE);
    }

    private static String format(int xp)
    {
        return String.format("%,d", xp);
    }
}
