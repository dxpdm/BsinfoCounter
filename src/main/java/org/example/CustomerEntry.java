package org.example;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*public record CustomerEntry(int customerNm, int houseNm, int apartmentNm, int counterState,
                            String counterType, int counterNum, String date, boolean counterSwitch, String comment) */
public class CustomerEntry {
    int customerNm;
    int houseNm;
    int apartmentNm;
    int counterState;
    String counterType;
    int counterNum;
    LocalDate date;
    boolean counterSwitch;
    String comment;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public CustomerEntry(int customerNm, int houseNm, int apartmentNm, int counterState, String counterType, int counterNum, LocalDate date, boolean counterSwitch, String comment) {
        this.customerNm = customerNm;
        this.houseNm = houseNm;
        this.apartmentNm = apartmentNm;
        this.counterState = counterState;
        this.counterType = counterType;
        this.counterNum = counterNum;
        this.date = date;
        this.counterSwitch = counterSwitch;
        this.comment = comment;
    }

    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n", customerNm, houseNm, apartmentNm, counterState, counterType, counterNum, date, counterSwitch, counterNum);
    }

    static public CustomerEntry of(JTextField arg1, JTextField arg2, JTextField arg3, JTextField arg4, String arg5,
                                   JTextField arg6, JTextField arg7, JComboBox<String> arg8, JTextField arg9) {
        return new CustomerEntry(Integer.parseInt(arg1.getText()), Integer.parseInt(arg2.getText()), Integer.parseInt(arg3.getText()), (int)Double.parseDouble(arg4.getText()),
                arg5, Integer.parseInt(arg6.getText()), LocalDate.parse(arg7.getText(), formatter), String.valueOf(arg8.getSelectedItem()).equals("Ja"), arg9.getText());
    }
}