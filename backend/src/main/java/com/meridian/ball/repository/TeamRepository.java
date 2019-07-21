package com.meridian.ball.repository;

import static jooq.Tables.TEAM;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jooq.tables.pojos.Team;
import jooq.tables.records.TeamRecord;

@Repository
public class TeamRepository {
    
    private final DSLContext create;
    
    @Autowired
    public TeamRepository(DSLContext context) {
        this.create = context;
    }
    
    public Team fetchOne(long teamId) {
        return create.select().from(TEAM).fetchAny().into(Team.class);
    }
    
    public boolean exists(String teamId) {
        return 1 == create.select(DSL.count()).where(TEAM.TEAM_ID.eq(teamId)).fetchOne(0, int.class);
    }
    
    public String store(Team team) {
        TeamRecord record = create.newRecord(TEAM, team);
        record.store();
        return team.getTeamId();
    }
}
