/**
 *    Copyright 2012 MegaFon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ru.histone.resourceloaders;

import ru.histone.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class StreamResource implements Resource {
    private InputStream stream;
    private String baseHref;

    public StreamResource(InputStream stream, String baseHref) {
        this.stream = stream;
        this.baseHref = baseHref;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return stream;
    }

    @Override
    public String getBaseHref() {
        return baseHref;
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(stream);
    }
}