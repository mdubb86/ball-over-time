package com.meridian.ball.model;

import java.time.LocalDate;
import java.time.Month;

public enum Season {
    _1980(Month.OCTOBER, 10),
    _1981(Month.OCTOBER, 30),
    _1982(Month.OCTOBER, 29),
    _1983(Month.OCTOBER, 28),
    _1984(Month.OCTOBER, 26),
    _1985(Month.OCTOBER, 25),
    _1986(Month.OCTOBER, 31),
    _1987(Month.NOVEMBER, 6),
    _1988(Month.NOVEMBER, 4),
    _1989(Month.NOVEMBER, 3),
    _1990(Month.NOVEMBER, 2),
    _1991(Month.NOVEMBER, 1),
    _1992(Month.NOVEMBER, 6),
    _1993(Month.NOVEMBER, 5),
    _1994(Month.NOVEMBER, 4),
    _1995(Month.NOVEMBER, 3),
    _1996(Month.NOVEMBER, 1),
    _1997(Month.OCTOBER, 31),
    _1998(Month.FEBRUARY, 5),
    _1999(Month.NOVEMBER, 2),
    _2000(Month.OCTOBER, 31),
    _2001(Month.OCTOBER, 30),
    _2002(Month.OCTOBER, 29),
    _2003(Month.OCTOBER, 28),
    _2004(Month.NOVEMBER, 2),
    _2005(Month.NOVEMBER, 1),
    _2006(Month.OCTOBER, 31),
    _2007(Month.OCTOBER, 30),
    _2008(Month.OCTOBER, 28),
    _2009(Month.OCTOBER, 27),
    _2010(Month.OCTOBER, 26),
    _2011(Month.DECEMBER, 25),
    _2012(Month.OCTOBER, 30),
    _2013(Month.OCTOBER, 29),
    _2014(Month.OCTOBER, 28),
    _2015(Month.OCTOBER, 27),
    _2016(Month.OCTOBER, 25),
    _2017(Month.OCTOBER, 20),
    _2018(Month.OCTOBER, 16),
    _2019(Month.OCTOBER, 16);

    private final LocalDate startDate;

    private Season(Month month, int dayOfMonth) {
        startDate = LocalDate.of(Integer.parseInt(name().substring(1)), month.getValue(), dayOfMonth);
    }
    
    public LocalDate getStartDate() {
        return this.startDate;
    }
}
