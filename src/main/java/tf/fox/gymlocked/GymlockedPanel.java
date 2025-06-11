package tf.fox.gymlocked;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.util.SwingUtil;

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

    private final FlatTextField cardioMinutesField = new FlatTextField();
    private final FlatTextField repsField = new FlatTextField();
    private final FlatTextField stepsField = new FlatTextField();
    private final FlatTextField proteinField = new FlatTextField();
    private final FlatTextField sleepField = new FlatTextField();
    private final JTextArea personalNotesArea = new JTextArea();

    private final JComboBox<String> globalModifier = new JComboBox<>();

    private GymlockedPlugin plugin;

    public void init(GymlockedPlugin plugin) {
        this.plugin = plugin;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        initializeModifierDropdowns(globalModifier);

        globalModifier.addActionListener(e -> {
            int selectedIndex = globalModifier.getSelectedIndex();
            int modifierValue = selectedIndex - 5;
            plugin.setGlobalModifier(modifierValue);
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(wrapScrollable(buildBody()), BorderLayout.CENTER);
        refresh();
    }

    private void initializeModifierDropdowns(JComboBox<String> dropdown) {
        for (int i = -5; i <= 5; i++) {
            double multiplier = 1.0 + (i * 0.1);
            dropdown.addItem(i + " (" + String.format("%.1fx", multiplier) + ")");
        }
        dropdown.setSelectedIndex(5); // default 1.0x

        dropdown.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        dropdown.setForeground(Color.WHITE);
        dropdown.setFont(FontManager.getRunescapeSmallFont());
        dropdown.setFocusable(false);
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

        JPanel modifierPanel = new JPanel(new BorderLayout());
        modifierPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JLabel modifierLabel = new JLabel("Global Modifier:");
        modifierLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        modifierLabel.setFont(FontManager.getRunescapeFont());
        modifierPanel.add(modifierLabel, BorderLayout.WEST);

        globalModifier.setPreferredSize(new Dimension(100, globalModifier.getPreferredSize().height));
        modifierPanel.add(globalModifier, BorderLayout.EAST);

        body.add(modifierPanel);
        body.add(Box.createVerticalStrut(12));

        body.add(createInputPanel("Cardio (min):", cardioMinutesField));
        body.add(Box.createVerticalStrut(8));

        body.add(createInputPanel("Reps:", repsField));
        body.add(Box.createVerticalStrut(8));

        body.add(createInputPanel("Steps:", stepsField));
        body.add(Box.createVerticalStrut(8));

        body.add(createInputPanel("Protein (g):", proteinField));
        body.add(Box.createVerticalStrut(8));

        body.add(createInputPanel("Sleep (hrs):", sleepField));
        body.add(Box.createVerticalStrut(8));

        body.add(createInputPanel("Manual XP:", xpInputField));
        body.add(Box.createVerticalStrut(8));

        body.add(buttonBar());
        body.add(Box.createVerticalStrut(12));

        JLabel notesLabel = new JLabel("Personal Notes:");
        notesLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        notesLabel.setFont(FontManager.getRunescapeFont());
        body.add(notesLabel);
        body.add(Box.createVerticalStrut(4));

        personalNotesArea.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        personalNotesArea.setForeground(Color.WHITE);
        personalNotesArea.setFont(FontManager.getRunescapeSmallFont());
        personalNotesArea.setLineWrap(true);
        personalNotesArea.setWrapStyleWord(true);
        personalNotesArea.setBorder(new EmptyBorder(5, 7, 5, 7));
        personalNotesArea.setRows(5);

        JScrollPane notesScrollPane = new JScrollPane(personalNotesArea);
        notesScrollPane.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        notesScrollPane.setBorder(null);
        body.add(notesScrollPane);

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
        int totalXpToAdd = 0;
        int totalLevel = plugin.calculateTotalLevel();

        int manualXp = parseInputField(xpInputField);
        if (manualXp > 0) {
            totalXpToAdd += manualXp;
        }

        int cardioValue = parseInputField(cardioMinutesField);
        if (cardioValue > 0) {
            totalXpToAdd += calculateXp(cardioValue, totalLevel, 1.66);
        }

        int repsValue = parseInputField(repsField);
        if (repsValue > 0) {
            totalXpToAdd += calculateXp(repsValue, totalLevel, 1.0);
        }

        int stepsValue = parseInputField(stepsField);
        if (stepsValue > 0) {
            totalXpToAdd += calculateXp(stepsValue, totalLevel, 0.0033);
        }

        int proteinValue = parseInputField(proteinField);
        if (proteinValue > 0) {
            totalXpToAdd += calculateXp(proteinValue, totalLevel, 0.33);
        }

        int sleepValue = parseInputField(sleepField);
        if (sleepValue > 0) {
            totalXpToAdd += calculateXp(sleepValue, totalLevel, 2.0);
        }

        plugin.setPersonalNotes(personalNotesArea.getText());

        if (totalXpToAdd > 0)
        {
            // save the current notes before adding XP (which triggers refresh)
            String currentNotes = personalNotesArea.getText();

            plugin.addUnlockedXp(totalXpToAdd);

            xpInputField.setText("");
            cardioMinutesField.setText("");
            repsField.setText("");
            stepsField.setText("");
            proteinField.setText("");
            sleepField.setText("");

            personalNotesArea.setText(currentNotes);
        }
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

        personalNotesArea.setText(plugin.getPersonalNotes());

        int modifierValue = plugin.getGlobalModifier();
        globalModifier.setSelectedIndex(modifierValue + 5);
    }

    private static String format(int xp)
    {
        return String.format("%,d", xp);
    }

    private JPanel createInputPanel(String labelText, FlatTextField inputField)
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel(labelText);
        label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        label.setFont(FontManager.getRunescapeFont());
        panel.add(label, c);

        c.gridx = 1;
        c.weightx = 1.0;
        c.insets = new Insets(0, 5, 0, 0);

        inputField.setBorder(new EmptyBorder(5, 7, 5, 7));
        inputField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        inputField.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        inputField.setPreferredSize(new Dimension(100, inputField.getPreferredSize().height));
        panel.add(inputField, c);

        return panel;
    }


    public String getPersonalNotes()
    {
        return personalNotesArea.getText();
    }

    public void setPersonalNotes(String notes)
    {
        personalNotesArea.setText(notes);
    }

    // Helper method to apply modifier to XP
    private int applyModifier(int xp, JComboBox<String> modifierDropdown)
    {
        int selectedIndex = modifierDropdown.getSelectedIndex();
        int modifierValue = selectedIndex - 5;
        double multiplier = 1.0 + (modifierValue * 0.1);

        return (int) Math.round(xp * multiplier);
    }

    private int parseInputField(FlatTextField field)
    {
        String input = field.getText().replace(",", "").trim();
        if (input.isEmpty())
        {
            return 0;
        }

        try
        {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException ignored)
        {
            return 0;
        }
    }

    private int calculateXp(int inputValue, int totalLevel, double multiplier)
    {
        if (inputValue <= 0)
        {
            return 0;
        }

        int xp = (int) Math.round(inputValue * multiplier * totalLevel);

        xp = applyModifier(xp, globalModifier);

        return xp;
    }
}
