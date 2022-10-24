package org.example;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

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

    static public CustomerEntry of(JComponent[] components) {
        return new CustomerEntry(
                Integer.parseInt(((IntegerTextField) components[0]).getText()),
                Integer.parseInt(((IntegerTextField) components[1]).getText()),
                Integer.parseInt(((IntegerTextField) components[2]).getText()),
                (int)Double.parseDouble(((DoubleTextField) components[3]).getText()),
                ((JLabel) components[4]).getText(),
                Integer.parseInt(((IntegerTextField) components[5]).getText()),
                toLocalDate(((JDateChooser) components[6]).getDate()),
                String.valueOf(((JComboBox<String>) components[7]).getSelectedItem()).equals("Ja"),
                ((JTextField) components[8]).getText());
    }

    static private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n",
                customerNm, houseNm, apartmentNm, counterState, counterType, counterNum, date, counterSwitch, comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerEntry that)) return false;
        return customerNm == that.customerNm && houseNm == that.houseNm && apartmentNm == that.apartmentNm && counterState == that.counterState && counterNum == that.counterNum && counterSwitch == that.counterSwitch && counterType.equals(that.counterType) && date.equals(that.date) && comment.equals(that.comment);
    }
}