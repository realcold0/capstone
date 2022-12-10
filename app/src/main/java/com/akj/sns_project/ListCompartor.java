package com.akj.sns_project;

import java.util.Comparator;
import java.util.Date;

public class ListCompartor implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Date testString1 = ((ReplyInfo)o1).getCreatedAt();
        Date testString2 = ((ReplyInfo)o2).getCreatedAt();


        return testString2.compareTo(testString1);
    }
}
