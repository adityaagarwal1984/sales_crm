package com.prosperr.crm.sales.domain;

public enum OpportunityStatus {
    L1,
    L2,
    L3,
    WON,
    LOST;

    public boolean isOpen() { return this == L1 || this == L2 || this == L3; }
}
