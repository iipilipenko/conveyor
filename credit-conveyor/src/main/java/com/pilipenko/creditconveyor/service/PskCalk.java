package com.pilipenko.creditconveyor.service;

public class PskCalk {
    private double rate;
    private double amount;
    private double term;

    public PskCalk(double rate, double amount, double term) {
        this.rate = rate;
        this.amount = amount;
        this.term = term;
    }

    public double getMonthlyPayment () {
        double Pc = this.term/(100*12);
        return this.amount*Pc/(1-Math.pow((1+Pc),term));
    }
}
