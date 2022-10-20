package org.example;

import javax.swing.*;
import java.time.LocalDate;
import java.time.Period;

import static org.example.CustomerEntry.formatter;

class Filter {
    String dateStart;
    String dateEnd;
    String customerNum;
    String houseNum;
    String apartmentNum;
    String counterType;

    public Filter(String dateStart, String dateEnd, String customerNum, String houseNum, String apartmentNum, String counterType) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.customerNum = customerNum;
        this.houseNum = houseNum;
        this.apartmentNum = apartmentNum;
        this.counterType = counterType;
    }

    public boolean fulfills(CustomerEntry entry) {
        boolean result = true;

        if (!(dateStart.equals("") && dateEnd.equals("")))
            result &= entry.date.isAfter(LocalDate.parse(dateStart, formatter)) && entry.date.isBefore(LocalDate.parse(dateEnd, formatter));
        if (!customerNum.equals(""))
            result &= Integer.parseInt(customerNum) == entry.customerNm;
        if (!houseNum.equals(""))
            result &= Integer.parseInt(houseNum) == entry.houseNm;
        if (!apartmentNum.equals(""))
            result &= Integer.parseInt(apartmentNum) == entry.apartmentNm;
        // Immer "Wasser"
        /* if (!counterType.equals(""))
            result &= counterType == entry.counterType; */

        return result;
    }

    public static Filter from(JTextField dateStart, JTextField dateEnd, JTextField customerNum, JTextField houseNum, JTextField apartmentNum, String counterType) {
        return new Filter(dateStart.getText(), dateEnd.getText(), customerNum.getText(), houseNum.getText(),
                apartmentNum.getText(), counterType);
    }
}
