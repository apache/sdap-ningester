/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

public class AddTimeToSectionSpec {

    private String timeVar;

    public AddTimeToSectionSpec(String timeVar){
        this.timeVar = timeVar;
    }

    public String process(String sectionSpec){
        return sectionSpec;
    }


}
