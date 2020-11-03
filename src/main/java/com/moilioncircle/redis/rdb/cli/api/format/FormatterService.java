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
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.datatype.ContextKeyValuePair;

/**
 * @author Baoyi Chen
 */
public interface FormatterService {

    String format();
    
    Escaper getEscaper();
    
    Properties getProperties();

    OutputStream getOutputStream();
    
    void setEscaper(Escaper escaper);

    void setProperties(Properties properties);
    
    void setOutputStream(OutputStream outputStream);

    /*
     *
     */
    default void applyStart(RedisInputStream in) throws IOException {
    }

    default void applyEnd(RedisInputStream in, int version, long checksum) throws IOException {
    }

    default void applyRedisProperty(RedisInputStream in, int version, String key, String value) throws IOException {
    }

    /*
     *
     */
    default Event applyString(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applySet(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyZSet(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyZSet2(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyHash(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyHashZipMap(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyListZipList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applySetIntSet(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyZSetZipList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyHashZipList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyListQuickList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyModule(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyModule2(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }

    default Event applyStreamListPacks(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        throw new UnsupportedOperationException("must implement this method.");
    }
}
