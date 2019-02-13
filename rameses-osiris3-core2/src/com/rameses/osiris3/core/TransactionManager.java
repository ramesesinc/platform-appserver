package com.rameses.osiris3.core;



public interface TransactionManager {
    void commit();
    void rollback();
    void close();
}