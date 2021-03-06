package com.pb.dp.enums;

import com.pb.dp.enums.ResponseStatus;

public enum Relationship {

    SELF(1,"Self"),
    SPOUSE(2,"Spouse"),
    DAUGHTER(3,"Daughter"),
    SON(4,"Son"),
    WIFE(5,"Wife"),
    HUSBAND(6,"Husband");

    private final int relationId;
    private final String relation;

    Relationship(int relationId, String relation) {
        this.relationId = relationId;
        this.relation = relation;
    }

    public int getRelationId() {
        return relationId;
    }

    public String getRelation() {
        return relation;
    }
}
