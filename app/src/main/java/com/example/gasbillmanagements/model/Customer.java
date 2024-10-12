package com.example.gasbillmanagements.model;

public class Customer {
    public static int ID ;
    public static String NAME;
    public static String YYYYMM;
    public static String ADDRESS;
    public static int USED_NUM_GAS;
    public static int GAS_LEVEL_TYPE_ID;

    public Customer(int id, String name, String yyyymm, String address, int usedNumGas, int gasLevelTypeId) {
        this.ID = id;
        this.NAME = name;
        this.YYYYMM = yyyymm;
        this.ADDRESS = address;
        this.USED_NUM_GAS = usedNumGas;
        this.GAS_LEVEL_TYPE_ID = gasLevelTypeId;
    }

    public Customer() {

    }

    public static int getID() {
        return ID;
    }

    public static void setID(int ID) {
        Customer.ID = ID;
    }

    public static String getNAME() {
        return NAME;
    }

    public static void setNAME(String NAME) {
        Customer.NAME = NAME;
    }

    public static String getYYYYMM() {
        return YYYYMM;
    }

    public static void setYYYYMM(String YYYYMM) {
        Customer.YYYYMM = YYYYMM;
    }

    public static String getADDRESS() {
        return ADDRESS;
    }

    public static void setADDRESS(String ADDRESS) {
        Customer.ADDRESS = ADDRESS;
    }

    public static int getUsedNumGas() {
        return USED_NUM_GAS;
    }

    public static void setUsedNumGas(int usedNumGas) {
        USED_NUM_GAS = usedNumGas;
    }

    public static int getGasLevelTypeId() {
        return GAS_LEVEL_TYPE_ID;
    }

    public static void setGasLevelTypeId(int gasLevelTypeId) {
        GAS_LEVEL_TYPE_ID = gasLevelTypeId;
    }


}
