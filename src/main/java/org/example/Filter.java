package org.example;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import static org.example.CustomerEntry.formatter;

class Filter {
    LocalDate dateStart;
    LocalDate dateEnd;
    String customerNum;
    String houseNum;
    String apartmentNum;
    String counterType;

    public Filter(LocalDate dateStart, LocalDate dateEnd, String customerNum, String houseNum, String apartmentNum, String counterType) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.customerNum = customerNum;
        this.houseNum = houseNum;
        this.apartmentNum = apartmentNum;
        this.counterType = counterType;
    }

    public boolean fulfills(CustomerEntry entry) {
        boolean result = true;

        if (!(dateStart == null || dateEnd == null))
            result &= entry.date.isAfter(dateStart) && entry.date.isBefore(dateEnd);
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

    public static Filter from(Date dateStart, Date dateEnd, JTextField customerNum, JTextField houseNum, JTextField apartmentNum, String counterType) {
        return new Filter(toLocalDate(dateStart), toLocalDate(dateEnd), customerNum.getText(), houseNum.getText(),
                apartmentNum.getText(), counterType);
    }

    static private LocalDate toLocalDate(Date date) {
        if (date != null)
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        else
            return null;
    }
}
