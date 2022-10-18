package org.example;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

class LocalDateSerializer implements JsonSerializer< LocalDate > {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public JsonElement serialize(LocalDate localDate, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(localDate));
    }
}

class LocalDateDeserializer implements JsonDeserializer < LocalDate > {
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDate.parse(json.getAsString(),
                DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMAN));
    }
}

public class Counter extends JFrame {
    private static final String[] columnNames = new String[]{
            "Kundennummer",
            "Hausnummer",
            "Wohnungsnummer",
            "Zählerstand",
            "Zählerart",
            "Zählernummer",
            "Ablesedatum",
            "Zählerwechsel",
            "Kommentar",
    };
    private static final String savedDataPath = "res/savedData.json";
    final JTable table = new JTable(new DefaultTableModel(new String[][]{}, columnNames));
    ArrayList<CustomerEntry> data = new ArrayList<>();

    public Counter() {
        super("Zähler");
        load();
        init();
    }

    public static void main(String[] args) {
        new Counter();
    }

    private void init() {
        final var con = getContentPane();
        con.setLayout(new BorderLayout());

        con.add(new JScrollPane(table), BorderLayout.CENTER);

        table.setGridColor(Color.LIGHT_GRAY);
        table.setEnabled(false);

        final var sideButtons = new JPanel();
        sideButtons.setLayout(new BoxLayout(sideButtons, BoxLayout.Y_AXIS));

        final var addButton = new Button("hinzufügen");
        addButton.setMaximumSize(new Dimension(100, 50));
        addButton.addActionListener(e -> addElement(showInputPopup()));
        final var exportButton = new Button("export");
        exportButton.setMaximumSize(new Dimension(100, 50));
        final var exitButton = new Button("exit");
        exitButton.setMaximumSize(new Dimension(100, 50));
        exitButton.addActionListener(e -> exit());

        sideButtons.add(addButton);
        sideButtons.add(exportButton);
        sideButtons.add(exitButton);

        con.add(sideButtons, BorderLayout.EAST);

        setSize(1400, 600);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void addElement(Optional<CustomerEntry> entry) {
        if (entry.isPresent()) {
            data.add(entry.get());
            addRow(entry.get(), table);
        }
    }

    private void addElement(CustomerEntry entry) {
        data.add(entry);
        addRow(entry, table);
    }

    private void addRow(CustomerEntry entry, JTable t) {
        ((DefaultTableModel) t.getModel()).addRow(new Object[]{entry.customerNm, entry.houseNm, entry.apartmentNm, entry.counterState, entry.counterType, entry.counterNum, entry.date, entry.counterSwitch, entry.comment});
    }

    private Optional<CustomerEntry> showInputPopup() {
        final var inputPopup = new JPanel();
        inputPopup.setLayout(new GridLayout(9, 0));

        final var customerNmInput = new JTextField("");
        final var houseNmInput = new JTextField("");
        final var apartmentNmInput = new JTextField("");
        final var consumptionInput = new JTextField("");
        final var counterNmInput = new JTextField("");
        final var dateInput = new JTextField("");
        final var counterSwitchInput = new JComboBox<>(new String[]{"Nein", "Ja"});
        final var commentInput = new JTextField("");

        inputPopup.add(new JLabel(columnNames[0]));
        inputPopup.add(customerNmInput);
        inputPopup.add(new JLabel(columnNames[1]));
        inputPopup.add(houseNmInput);
        inputPopup.add(new JLabel(columnNames[2]));
        inputPopup.add(apartmentNmInput);
        inputPopup.add(new JLabel(columnNames[3]));
        inputPopup.add(consumptionInput);
        inputPopup.add(new JLabel(columnNames[4]));
        inputPopup.add(new JLabel("Wasser"));
        inputPopup.add(new JLabel(columnNames[5]));
        inputPopup.add(counterNmInput);
        inputPopup.add(new JLabel(columnNames[6]));
        inputPopup.add(dateInput);
        inputPopup.add(new JLabel(columnNames[7]));
        inputPopup.add(counterSwitchInput);
        inputPopup.add(new JLabel(columnNames[8]));
        inputPopup.add(commentInput);

        final var result = JOptionPane.showConfirmDialog(this, inputPopup, "Daten einfügen",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION)
            return Optional.of(
                    CustomerEntry.of(customerNmInput, houseNmInput, apartmentNmInput, consumptionInput, "Wasser", counterNmInput, dateInput, counterSwitchInput, commentInput)
            );
        else
            return Optional.empty();
    }

    private void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .setDateFormat("dd.MM.yyyy")
                .create();

        try (Reader reader = new FileReader(savedDataPath)) {
            var type = new TypeToken<ArrayList<CustomerEntry>>(){}.getType();

            data = gson.fromJson(reader, type);

            for (var d : data)
                addRow(d, table);

        } catch (IOException e) {
            System.out.println("Couldn't load file at location");
        }

    }

    private void save() {
        try {
            var writer = new FileWriter(savedDataPath);
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .setDateFormat("dd.MM.yyyy")
                    .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                    .create();

            gson.toJson(data, writer);

            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't read file at location " + savedDataPath);
        }
    }

    private void exit() {
        save();
        System.exit(0);
    }
}