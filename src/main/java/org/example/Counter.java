package org.example;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

import static org.example.CustomerEntry.formatter;

class NumberTextField extends JTextField {
    NumberTextField(String text) {
        super(text);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                setEditable(e.getKeyChar() >= '0' && e.getKeyChar() <= '9'
                        || e.getKeyChar() == '.' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE);
            }
        });
    }
}

class LocalDateSerializer implements JsonSerializer<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public JsonElement serialize(LocalDate localDate, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(localDate));
    }
}

class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
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
    private static final String exportedDataPath = "res/exportedData.csv";
    private final TableModel myTableModel = new DefaultTableModel(new String[][]{}, columnNames) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    final JTable table = new JTable(myTableModel);
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
        table.setAutoCreateRowSorter(true);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() % 2 == 0) {
                    int rowIndex = table.getSelectedRow();
                    var input = showInputPopup(
                            Integer.toString((int) table.getValueAt(rowIndex, 0)),
                            Integer.toString((int) table.getValueAt(rowIndex, 1)),
                            Integer.toString((int) table.getValueAt(rowIndex, 2)),
                            Integer.toString((int) table.getValueAt(rowIndex, 3)),
                            Integer.toString((int) table.getValueAt(rowIndex, 5)),
                            ((LocalDate) table.getValueAt(rowIndex, 6)).format(formatter),
                            table.getValueAt(rowIndex, 7).equals("Ja"),
                            (String) table.getValueAt(rowIndex, 8)

                    );

                    if (input.isPresent()) {
                        table.setValueAt(input.get().customerNm, rowIndex, 0);
                        table.setValueAt(input.get().houseNm, rowIndex, 1);
                        table.setValueAt(input.get().apartmentNm, rowIndex, 2);
                        table.setValueAt(input.get().counterState, rowIndex, 3);
                        table.setValueAt(input.get().counterType, rowIndex, 4);
                        table.setValueAt(input.get().counterNum, rowIndex, 5);
                        table.setValueAt(input.get().date, rowIndex, 6);
                        table.setValueAt(input.get().counterSwitch, rowIndex, 7);
                        table.setValueAt(input.get().comment, rowIndex, 8);
                    }
                }
            }
        });

        final var sideButtons = new JPanel();
        sideButtons.setLayout(new BoxLayout(sideButtons, BoxLayout.Y_AXIS));

        final var addButton = new Button("hinzufügen");
        addButton.setMaximumSize(new Dimension(100, 50));
        addButton.addActionListener(e -> addElement(showInputPopup()));
        final var deleteButton = new Button("entfernen");
        deleteButton.setMaximumSize(new Dimension(100, 50));
        deleteButton.addActionListener(e -> deleteSelectedElement());
        final var exportButton = new Button("export");
        exportButton.setMaximumSize(new Dimension(100, 50));
        exportButton.addActionListener(e -> showExportPopup());
        final var exitButton = new Button("exit");
        exitButton.setMaximumSize(new Dimension(100, 50));
        exitButton.addActionListener(e -> exit());

        sideButtons.add(addButton);
        sideButtons.add(deleteButton);
        sideButtons.add(exportButton);
        sideButtons.add(exitButton);

        con.add(sideButtons, BorderLayout.EAST);

        setSize(1400, 600);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void deleteSelectedElement() {
        deleteRow(table.getSelectedRow(), table);
    }

    private void addElement(Optional<CustomerEntry> entry) {
        if (entry.isPresent()) {
            data.add(entry.get());
            addRow(entry.get(), table);
        }
    }

    private void addRow(CustomerEntry entry, JTable t) {
        ((DefaultTableModel) t.getModel()).addRow(new Object[]{entry.customerNm, entry.houseNm, entry.apartmentNm, entry.counterState, entry.counterType, entry.counterNum, entry.date, entry.counterSwitch, entry.comment});
    }

    private void deleteRow(int index, JTable t) {
        ((DefaultTableModel) t.getModel()).removeRow(index);
    }

    private Optional<CustomerEntry> showInputPopup() {
        final var inputPopup = new JPanel();
        inputPopup.setLayout(new GridLayout(9, 0));

        final var customerNmInput = new NumberTextField("");
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

    private Optional<CustomerEntry> showInputPopup(String customerNum, String houseNum, String apartmentNum, String consumption,
                                                   String counterNum, String date, boolean isTrue, String comment) {
        final var inputPopup = new JPanel();
        inputPopup.setLayout(new GridLayout(9, 0));

        final var customerNmInput = new JTextField(customerNum);
        final var houseNmInput = new JTextField(houseNum);
        final var apartmentNmInput = new JTextField(apartmentNum);
        final var consumptionInput = new JTextField(consumption);
        final var counterNmInput = new JTextField(counterNum);
        final var dateInput = new JTextField(date);
        final var counterSwitchInput = new JComboBox<>(isTrue ? new String[]{"Ja", "Nein"} : new String[]{"Nein", "Ja"});
        final var commentInput = new JTextField(comment);

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

    private void showExportPopup() {
        final var exportPopup = new JPanel();
        exportPopup.setLayout(new GridLayout(5, 0));

        final var dateStartInput = new JTextField("01.01.2000");
        final var dateEndInput = new JTextField("01.01.2000");
        final var customerNumInput = new JTextField();
        final var houseNumInput = new JTextField();
        final var apartmentNumInput = new JTextField();

        final var datePanel = new JPanel();
        datePanel.setLayout(new FlowLayout());
        datePanel.add(dateStartInput);
        datePanel.add(new JLabel(" - "));
        datePanel.add(dateEndInput);

        //exportPopup.add(new JLabel("Filter fürs Exportieren")); // .setFont(new Font("", Font.BOLD, 15))
        exportPopup.add(new JLabel("Datum: "));
        exportPopup.add(datePanel);
        exportPopup.add(new JLabel("Kundennummer: "));
        exportPopup.add(customerNumInput);
        exportPopup.add(new JLabel("Hausnummer: "));
        exportPopup.add(houseNumInput);
        exportPopup.add(new JLabel("Wohnungsnummer: "));
        exportPopup.add(apartmentNumInput);
        exportPopup.add(new JLabel("Zählerart: "));
        exportPopup.add(new JLabel("Wasser"));
        /*
        Datum der Ablesung von – bis
        - Kundennummer
        - Hausnummer
        - Wohnungsnummer
        - Zählerart
        */

        final var result = JOptionPane.showConfirmDialog(this, exportPopup, "Daten exportieren",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION)
            exportData(Filter.from(dateStartInput, dateEndInput, customerNumInput, houseNumInput, apartmentNumInput, "Wasser"));
    }

    private void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .setDateFormat("dd.MM.yyyy")
                .create();

        try (Reader reader = new FileReader(savedDataPath)) {
            var type = new TypeToken<ArrayList<CustomerEntry>>() {
            }.getType();

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

    private void exportData(Filter filter) {
        try {
            final var writer = new PrintWriter(exportedDataPath);
            writer.print("");
            writer.close();

            final var csvWriter = new BufferedWriter(new FileWriter(exportedDataPath, StandardCharsets.UTF_8,true));
            data.forEach(entry -> {
                try {
                    if (filter.fulfills(entry))
                        csvWriter.append(entry.toString());
                } catch (IOException e) {
                    System.out.println("couldn't add more entries");
                }
            });
            csvWriter.close();

        } catch (IOException e) {
            System.out.println("Couldn't open path \"res/TODO-Entries.csv\" to save the TODO entries, exception:\n" + e.getMessage());
        }
    }

    private void exit() {
        save();
        System.exit(0);
    }
}