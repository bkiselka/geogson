/*
 * Copyright 2013 Filippo De Luca - me@filippodeluca.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.filosganga.geogson.model;

import static com.github.filosganga.geogson.model.LinearGeometry.toLinearRingFn;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.skip;
import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;

import com.github.filosganga.geogson.model.positions.AreaPositions;
import com.github.filosganga.geogson.model.positions.LinearPositions;
import com.google.common.collect.ImmutableList;

/**
 * @author Filippo De Luca - me@filippodeluca.com
 */
public class Polygon extends MultiLineString {

    public Polygon(AreaPositions positions) {
        super(checkPositions(positions));
    }

    private static AreaPositions checkPositions(AreaPositions src) {

        checkArgument(src.size() >= 1);

        for (LinearPositions child : src.children()) {
            checkArgument(child.isClosed());
        }

        return src;

    }

    public static Polygon of(LinearRing perimeter, LinearRing... holes) {
        return Polygon.of(perimeter, asList(holes));
    }

    public static Polygon of(LinearRing perimeter, Iterable<LinearRing> holes) {

        AreaPositions positions = new AreaPositions(ImmutableList.<LinearPositions>builder()
                .add(perimeter.positions())
                .addAll(transform(holes, positionsFn(LinearPositions.class)))
                .build());

        return new Polygon(positions);
    }

    @Override
    public Type type() {
        return Type.POLYGON;
    }

    public Iterable<LinearRing> linearRings() {
        return transform(lineStrings(), toLinearRingFn());
    }

    public LinearRing perimeter() {
        return getFirst(linearRings(), null);
    }

    public Iterable<LinearRing> holes() {
        return skip(linearRings(), 1);
    }
}
