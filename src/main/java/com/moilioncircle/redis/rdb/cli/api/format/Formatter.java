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

package com.moilioncircle.redis.rdb.cli.api.format;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.moilioncircle.redis.rdb.cli.api.format.escape.Escaper;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.ContextKeyValuePair;

/**
 * @author Baoyi Chen
 */
public interface Formatter {

    String getName();
    
    Escaper getEscaper();

    Properties getProperties();

    OutputStream getOutputStream();

    void setEscaper(Escaper escaper);
    
    void setProperties(Properties properties);
    
    void setOutputStream(OutputStream outputStream);

    default Event applyString(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyList(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applySet(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyZSet(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyZSet2(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyHash(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyHashZipMap(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyListZipList(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applySetIntSet(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyZSetZipList(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyHashZipList(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyListQuickList(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyModule(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyModule2(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyStreamListPacks(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
}
