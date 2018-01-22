/*****************************************************************************
 * Copyright (c) 2017 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.jpl.nexus.ningester.http;

import org.apache.sdap.nexusproto.NexusTile;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStream;

public class NexusTileConverter extends AbstractHttpMessageConverter<NexusTile> {

    @Override
    protected boolean supports(Class<?> clazz) {
        return NexusTile.class.isAssignableFrom(clazz);
    }

    @Override
    protected NexusTile readInternal(Class<? extends NexusTile> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        return NexusTile.parseFrom(inputMessage.getBody());
    }

    @Override
    protected void writeInternal(NexusTile nexusTile, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            OutputStream outputStream = outputMessage.getBody();
            nexusTile.writeTo(outputStream);
            outputStream.close();
        } catch (Exception ignored) {
        }
    }
}
