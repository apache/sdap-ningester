/*
 *****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors;

public class AddTimeToSectionSpec {

    private String timeVar = "time";
    private Integer timeLen;
    private String absolutefilepath;

    public AddTimeToSectionSpec(Integer timeLen, String absolutefilepath) {
        this.timeLen = timeLen;
        this.absolutefilepath = absolutefilepath;
    }

    public void setTimeVar(String timeVar) {
        this.timeVar = timeVar;
    }

    public String process(String sectionSpec) {
        StringBuilder newSectionSpec = new StringBuilder();
        for (int i = 0; i < this.timeLen; i++) {
            newSectionSpec.append(this.timeVar).append(":")
                    .append(i).append(":")
                    .append(i + 1).append(",")
                    .append(sectionSpec).append(";");
        }

        newSectionSpec.append("file://").append(this.absolutefilepath);

        return newSectionSpec.toString();
    }

}
