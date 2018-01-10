/*
 ****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.testjobs;

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
