package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

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
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    final JTable table = new JTable(new DefaultTableModel(new String[][]{}, columnNames) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    });
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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        table.setGridColor(Color.LIGHT_GRAY);
        table.setAutoCreateRowSorter(true);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() % 2 == 0) {
                    int rowIndex = table.getSelectedRow();
                    var input = showInputPopup(getCustomerEntryFromTable(rowIndex));

                    if (input.isPresent()) {
                        data.set(data.indexOf(getCustomerEntryFromTable(rowIndex)), input.get());

                        for (int i = 0; i < table.getColumnCount(); i++) {
                            try {
                                Field[] members = CustomerEntry.class.getDeclaredFields();
                                table.setValueAt(members[i].get(input.get()), rowIndex, i);
                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            }
        });

        final var sideButtons = new JPanel();
        sideButtons.setLayout(new BoxLayout(sideButtons, BoxLayout.Y_AXIS));

        sideButtons.add(makeSideButton("hinzufügen", () -> addElement(showInputPopup())));
        sideButtons.add(makeSideButton("entfernen", this::deleteSelectedElement));
        sideButtons.add(makeSideButton("export", this::showExportPopup));
        sideButtons.add(makeSideButton("exit", this::exit));

        con.add(sideButtons, BorderLayout.EAST);

        setSize(1400, 600);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private JButton makeSideButton(String label, Runnable callback) {
        final var button = new JButton(label);
        button.setMaximumSize(new Dimension(100, 50));
        button.addActionListener(e -> callback.run());

        return button;
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

    private void deleteSelectedElement() {
        int rowIndex = table.getSelectedRow();

        data.remove(getCustomerEntryFromTable(rowIndex));
        deleteRow(rowIndex, table);
    }

    private void deleteRow(int index, JTable t) {
        ((DefaultTableModel) t.getModel()).removeRow(index);
    }

    private Optional<CustomerEntry> showInputPopup() {
        return showInputPopup("", "", "", "", "", "", false, "");
    }

    private Optional<CustomerEntry> showInputPopup(CustomerEntry entry) {
        return showInputPopup(
                Integer.toString(entry.customerNm),
                Integer.toString(entry.houseNm),
                Integer.toString(entry.apartmentNm),
                Integer.toString(entry.counterState),
                Integer.toString(entry.counterNum),
                entry.date.format(formatter),
                entry.counterSwitch,
                entry.comment
        );
    }

    private Optional<CustomerEntry> showInputPopup(String customerNum, String houseNum, String apartmentNum, String counterState,
                                                   String counterNum, String date, boolean isTrue, String comment) {
        final var inputPopup = new JPanel();
        inputPopup.setLayout(new GridLayout(9, 0));

        final var calender = Calendar.getInstance();
        try {
            if (!date.equals(""))
                calender.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        calender.add(Calendar.YEAR, 0);

        final var inputArray = new JComponent[9];
        inputArray[0] = new IntegerTextField(customerNum);
        inputArray[1] = new IntegerTextField(houseNum);
        inputArray[2] = new IntegerTextField(apartmentNum);
        inputArray[3] = new DoubleTextField(counterState);
        inputArray[4] = new JLabel("Wasser");
        inputArray[5] = new IntegerTextField(counterNum);
        inputArray[6] = new JDateChooser(calender.getTime());
        inputArray[7] = new JComboBox<>(isTrue ? new String[]{"Ja", "Nein"} : new String[]{"Nein", "Ja"});
        inputArray[8] = new JTextField(comment);

        for (int i = 0; i < inputArray.length; i++) {
            inputPopup.add(new JLabel(columnNames[i]));
            inputPopup.add(inputArray[i]);
        }

        final var result = JOptionPane.showConfirmDialog(this, inputPopup, "Daten einfügen",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION)
            return Optional.of(CustomerEntry.of(inputArray));
        else
            return Optional.empty();
    }

    private void showExportPopup() {
        final var exportPopup = new JPanel();
        exportPopup.setLayout(new BorderLayout());

        final var inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 0));

        final var labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(5, 0));

        final var calender = Calendar.getInstance();
        calender.add(Calendar.YEAR, 0);

        final var dateStartInput = new JDateChooser();
        dateStartInput.setPreferredSize(new Dimension(115, 20));
        final var dateEndInput = new JDateChooser();
        dateEndInput.setPreferredSize(new Dimension(115, 20));
        final var customerNumInput = new JTextField();
        final var houseNumInput = new JTextField();
        final var apartmentNumInput = new JTextField();

        final var datePanel = new JPanel();
        datePanel.setLayout(new FlowLayout());
        datePanel.add(dateStartInput);
        datePanel.add(new JLabel(" - "));
        datePanel.add(dateEndInput);

        labelPanel.add(new JLabel("Datum: "));
        labelPanel.add(new JLabel("Kundennummer: "));
        labelPanel.add(new JLabel("Hausnummer: "));
        labelPanel.add(new JLabel("Wohnungsnummer: "));
        labelPanel.add(new JLabel("Zählerart: "));

        inputPanel.add(datePanel);
        inputPanel.add(customerNumInput);
        inputPanel.add(houseNumInput);
        inputPanel.add(apartmentNumInput);
        inputPanel.add(new JLabel("Wasser"));

        exportPopup.add(labelPanel, BorderLayout.WEST);
        exportPopup.add(inputPanel, BorderLayout.CENTER);

        final var result = JOptionPane.showConfirmDialog(this, exportPopup, "Daten exportieren",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION)
            exportData(Filter.of(dateStartInput.getDate(), dateEndInput.getDate(),
                    customerNumInput, houseNumInput, apartmentNumInput, "Wasser"));
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

            final var csvWriter = new BufferedWriter(new FileWriter(exportedDataPath, StandardCharsets.UTF_8, true));
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

    private CustomerEntry getCustomerEntryFromTable(int index) {
        return new CustomerEntry(
                (int) table.getValueAt(index, 0),
                (int) table.getValueAt(index, 1),
                (int) table.getValueAt(index, 2),
                (int) table.getValueAt(index, 3),
                "Wasser",
                (int) table.getValueAt(index, 5),
                (LocalDate) table.getValueAt(index, 6),
                (boolean) table.getValueAt(index, 7),
                (String) table.getValueAt(index, 8)
        );
    }

    private void exit() {
        save();
        System.exit(0);
    }
}