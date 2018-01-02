/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

import org.junit.Assert;
import org.junit.Test;


public class TestAddTimeToSpatialSpec {


    @Test
    public void testStringPayload() {

        String testSpec = "test:0:1,script:3:4";

        AddTimeToSectionSpec processor = new AddTimeToSectionSpec(4, "afilepath");

        String expected = "time:0:1,test:0:1,script:3:4;" +
                "time:1:2,test:0:1,script:3:4;" +
                "time:2:3,test:0:1,script:3:4;" +
                "time:3:4,test:0:1,script:3:4;" +
                "file://afilepath";

        String result = processor.process(testSpec);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testTimeVarName() {

        String testSpec = "test:0:1,script:3:4";
        String timeVarName = "theTime";

        AddTimeToSectionSpec processor = new AddTimeToSectionSpec(4, "afilepath");
        processor.setTimeVar(timeVarName);

        String expected = "theTime:0:1,test:0:1,script:3:4;" +
                "theTime:1:2,test:0:1,script:3:4;" +
                "theTime:2:3,test:0:1,script:3:4;" +
                "theTime:3:4,test:0:1,script:3:4;" +
                "file://afilepath";

        String result = processor.process(testSpec);

        Assert.assertEquals(expected, result);
    }

}