package com.novigosolutions.certiscisco_pcsbr.expandable;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Cage extends ExpandableGroup<Items> {

    public Cage(String title, List<Items> items) {
        super(title, items);
    }
}
