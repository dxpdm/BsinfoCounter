package org.example;

import java.util.Date;

public record CustomerEntry(int customerNm, int houseNm, int apartmentNm, String counterType, int counterNm, Date date,
                            boolean counterSwitch, String comment) {
}
