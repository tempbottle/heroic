package com.spotify.heroic.aggregation;

import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class GroupAggregationQuery implements AggregationQuery<GroupAggregation> {
    private final List<String> of;
    private final Aggregation each;

    @JsonCreator
    public GroupAggregationQuery(@JsonProperty("of") List<String> of,
            @JsonProperty("each") List<AggregationQuery<?>> each) {
        this.of = of;
        this.each = new ChainAggregation(ChainAggregation.convertQueries(each));
    }

    @Override
    public GroupAggregation build() {
        return new GroupAggregation(of, each);
    }
}