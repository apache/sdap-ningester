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


package org.apache.sdap.ningester.http;

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
