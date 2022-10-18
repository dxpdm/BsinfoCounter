package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import com.opencsv.CSVWriter;

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
    final JTable table = new JTable(new DefaultTableModel(new String[][]{}, columnNames));
    final ArrayList<CustomerEntry> data = new ArrayList<CustomerEntry>();

    public Counter() {
        super("Zähler");
        init();
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
        exportButton.addActionListener(e -> exportList(data));
        final var exitButton = new Button("exit");
        exitButton.setMaximumSize(new Dimension(100, 50));

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

    private void addRow(CustomerEntry entry, JTable t) {
        ((DefaultTableModel)t.getModel()).addRow(new Object[]{ entry.customerNm(), entry.houseNm(), entry.apartmentNm(), entry.counterState(), entry.counterType(), entry.counterNum(), entry.date(), entry.counterSwitch(), entry.comment() });
    }

    private void exportList(ArrayList<CustomerEntry> tab) {
        File file = new File("export.csv");

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file));
            String[] header = { "customerNm", "houseNm", "apartmentNm", "counterState", "counterType", "counterNum", "date" , "counterSwitch", "comment" };
            writer.writeNext(header);
            tab.forEach(row -> {
                for (String s : new String[] { String.valueOf(row.customerNm()), String.valueOf(row.houseNm()), String.valueOf(row.apartmentNm()), String.valueOf(row.counterState()), row.counterType(), String.valueOf(row.counterNum()), String.valueOf(row.date()), String.valueOf(row.counterSwitch()), row.comment() }) {
                    writer.writeNext(new String[]{s});
                }
            });
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) {
        new Counter();
    }
}