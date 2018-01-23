/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sdap.ningester.testjobs;

import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TestUtils {

    public static <T> void assertEqualsEventually(T expected, Supplier<T> actualSupplier, int secondsToWait) throws InterruptedException {
        int n = 0;
        int iterations = 10 * secondsToWait;
        int sleepTimeMillis = 100;

        T suppliedValue = actualSupplier.get();
        while (!suppliedValue.equals(expected) && n++ < iterations) {
            Thread.sleep(sleepTimeMillis);
            suppliedValue = actualSupplier.get();
        }
        assertThat("Did not equal after waiting " + (iterations * sleepTimeMillis / 1000) + " seconds.", suppliedValue, is(expected));
    }
}
