package org.example;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class CustomerEntry {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    int customerNm;
    int houseNm;
    int apartmentNm;
    int counterState;
    String counterType;
    int counterNum;
    LocalDate date;
    boolean counterSwitch;
    String comment;

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
                                   JTextField arg6, JDateChooser arg7, JComboBox<String> arg8, JTextField arg9) {
        return new CustomerEntry(Integer.parseInt(arg1.getText()), Integer.parseInt(arg2.getText()), Integer.parseInt(arg3.getText()), (int) Double.parseDouble(arg4.getText()),
                arg5, Integer.parseInt(arg6.getText()), toLocalDate(arg7.getDate()), String.valueOf(arg8.getSelectedItem()).equals("Ja"), arg9.getText());
    }

    static private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerEntry that)) return false;
        return customerNm == that.customerNm && houseNm == that.houseNm && apartmentNm == that.apartmentNm && counterState == that.counterState && counterNum == that.counterNum && counterSwitch == that.counterSwitch && counterType.equals(that.counterType) && date.equals(that.date) && comment.equals(that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerNm, houseNm, apartmentNm, counterState, counterType, counterNum, date, counterSwitch, comment);
    }
}