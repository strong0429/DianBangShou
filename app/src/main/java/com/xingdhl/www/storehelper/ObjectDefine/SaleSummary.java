package com.xingdhl.www.storehelper.ObjectDefine;

import java.io.Serializable;

public class SaleSummary implements Serializable {
    private double daySum, dayRise;
    private double weekSum, weekRise;
    private double monthSum, monthRise;

    private double dayProfit, dayProfitRise;
    private double weekProfit, weekProfitRise;
    private double monthProfit, monthProfitRise;

    public SaleSummary(){}

    public double getDaySum() {
        return daySum;
    }

    public void setDaySum(double daySum) {
        this.daySum = daySum;
    }

    public double getDayRise() {
        return dayRise;
    }

    public void setDayRise(double dayRise) {
        this.dayRise = dayRise;
    }

    public double getWeekSum() {
        return weekSum;
    }

    public void setWeekSum(double weekSum) {
        this.weekSum = weekSum;
    }

    public double getWeekRise() {
        return weekRise;
    }

    public void setWeekRise(double weekRise) {
        this.weekRise = weekRise;
    }

    public double getMonthSum() {
        return monthSum;
    }

    public void setMonthSum(double monthSum) {
        this.monthSum = monthSum;
    }

    public double getMonthRise() {
        return monthRise;
    }

    public void setMonthRise(double monthRise) {
        this.monthRise = monthRise;
    }

    public double getDayProfit() {
        return dayProfit;
    }

    public void setDayProfit(double dayProfit) {
        this.dayProfit = dayProfit;
    }

    public double getDayProfitRise() {
        return dayProfitRise;
    }

    public void setDayProfitRise(double dayProfitRise) {
        this.dayProfitRise = dayProfitRise;
    }

    public double getWeekProfit() {
        return weekProfit;
    }

    public void setWeekProfit(double weekProfit) {
        this.weekProfit = weekProfit;
    }

    public double getWeekProfitRise() {
        return weekProfitRise;
    }

    public void setWeekProfitRise(double weekProfitRise) {
        this.weekProfitRise = weekProfitRise;
    }

    public double getMonthProfit() {
        return monthProfit;
    }

    public void setMonthProfit(double monthProfit) {
        this.monthProfit = monthProfit;
    }

    public double getMonthProfitRise() {
        return monthProfitRise;
    }

    public void setMonthProfitRise(double monthProfitRise) {
        this.monthProfitRise = monthProfitRise;
    }
}
