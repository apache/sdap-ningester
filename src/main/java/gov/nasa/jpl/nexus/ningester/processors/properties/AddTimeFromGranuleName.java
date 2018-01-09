/*****************************************************************************
 * Copyright (c) 2018 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.processors.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Component("addTimeFromGranuleNameProperties")
public class AddTimeFromGranuleName {

    private String regex;
    private String dateFormat;

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
