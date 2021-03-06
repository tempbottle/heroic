/*
 * Copyright (c) 2015 Spotify AB.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.heroic.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@EqualsAndHashCode
@RequiredArgsConstructor
@JsonSerialize(using = ValueOptionalLimit.Serializer.class)
class ValueOptionalLimit implements OptionalLimit {
    private final long limit;

    @Override
    public boolean isGreater(final long size) {
        return size > limit;
    }

    @Override
    public boolean isGreaterOrEqual(final int size) {
        return size >= limit;
    }

    @Override
    public boolean isZero() {
        return limit == 0;
    }

    @Override
    public Optional<Integer> asInteger() {
        if (limit > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                "Limit is larger than maximum possible integer (" + limit + ")");
        }

        return Optional.of((int) limit);
    }

    @Override
    public Optional<Long> asLong() {
        return Optional.of(limit);
    }

    @Override
    public int asMaxInteger(final int maxValue) {
        if (limit > Integer.MAX_VALUE) {
            return maxValue;
        }

        return Math.min((int) limit, maxValue);
    }

    @Override
    public <T> List<T> limitList(final List<T> input) {
        if (limit > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                "Limit is larger than maximum possible integer (" + limit + ")");
        }

        final int l = (int) limit;

        if (input.size() < l) {
            return input;
        }

        return input.subList(0, l);
    }

    @Override
    public <T> Stream<T> limitStream(final Stream<T> stream) {
        return stream.limit(limit);
    }

    @Override
    public void ifPresent(final Consumer<Long> consumer) {
        consumer.accept(limit);
    }

    @Override
    public OptionalLimit add(final long size) {
        return new ValueOptionalLimit(limit + size);
    }

    @Override
    public String toString() {
        return "[" + limit + "]";
    }

    static class Serializer extends JsonSerializer<ValueOptionalLimit> {
        @Override
        public void serialize(
            final ValueOptionalLimit in, final JsonGenerator g, final SerializerProvider p
        ) throws IOException {
            g.writeNumber(in.limit);
        }
    }
}
