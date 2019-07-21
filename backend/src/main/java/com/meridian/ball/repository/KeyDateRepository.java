package com.meridian.ball.repository;

import static jooq.Tables.KEY_DATE;

import java.time.LocalDate;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jooq.tables.pojos.KeyDate;
import jooq.tables.records.KeyDateRecord;

@Repository
public class KeyDateRepository { 
    
    private final DSLContext create;
    
    @Autowired
    public KeyDateRepository(DSLContext create) {
        this.create = create;
    }
    
    public LocalDate fetchLast() {
        return create.select().from(KEY_DATE).where(KEY_DATE.KEY.eq("last")).fetchOne().into(KeyDate.class).getDate();
    }
    
    public void storeLast(LocalDate date) {
        KeyDateRecord record = create.newRecord(KEY_DATE, new KeyDate("last", date));
        record.store();
    }
}
