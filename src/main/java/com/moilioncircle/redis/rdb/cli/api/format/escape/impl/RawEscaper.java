/*
 * Copyright 2016-2017 Leon Chen
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

package com.moilioncircle.redis.rdb.cli.api.format.escape.impl;

import java.io.OutputStream;

import com.moilioncircle.redis.rdb.cli.api.format.escape.Escaper;
import com.moilioncircle.redis.rdb.cli.api.support.OutputStreams;


/**
 * @author Baoyi Chen
 */
public class RawEscaper implements Escaper {

    @Override
    public void encode(int b, OutputStream out) {
        OutputStreams.write(b & 0xFF, out);
    }
    
    @Override
    public void encode(byte[] bytes, int off, int len, OutputStream out) {
        if (bytes == null) return;
        OutputStreams.write(bytes, off, len, out);
    }
}
