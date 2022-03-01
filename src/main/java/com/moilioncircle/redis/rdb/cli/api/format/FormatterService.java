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

import static com.moilioncircle.redis.replicator.Constants.QUICKLIST_NODE_CONTAINER_PACKED;
import static com.moilioncircle.redis.replicator.Constants.QUICKLIST_NODE_CONTAINER_PLAIN;
import static com.moilioncircle.redis.replicator.Constants.RDB_LOAD_NONE;
import static com.moilioncircle.redis.replicator.Constants.STREAM_ITEM_FLAG_DELETED;
import static com.moilioncircle.redis.replicator.Constants.STREAM_ITEM_FLAG_SAMEFIELDS;
import static com.moilioncircle.redis.replicator.rdb.BaseRdbParser.StringHelper.listPackEntry;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.moilioncircle.redis.rdb.cli.api.format.escape.Escaper;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.io.RedisInputStream;
import com.moilioncircle.redis.replicator.rdb.BaseRdbParser;
import com.moilioncircle.redis.replicator.rdb.DefaultRdbValueVisitor;
import com.moilioncircle.redis.replicator.rdb.datatype.ContextKeyValuePair;
import com.moilioncircle.redis.replicator.rdb.datatype.Stream;
import com.moilioncircle.redis.replicator.util.ByteArray;
import com.moilioncircle.redis.replicator.util.Strings;

/**
 * @author Baoyi Chen
 */
@SuppressWarnings({"unused", "resource"})
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
    default void onEvent(Replicator replicator, Event event) {
    }

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
    default Event applyFunction(Replicator replicator, RedisInputStream in, int version, int type) throws IOException {
        return new DefaultRdbValueVisitor(replicator).applyFunction(in, version);
    }
    
    default Event applyString(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        byte[] val = parser.rdbLoadEncodedStringObject().first();
        return context;
    }

    default Event applyList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            byte[] element = parser.rdbLoadEncodedStringObject().first();
            len--;
        }
        return context;
    }

    default Event applySet(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            byte[] element = parser.rdbLoadEncodedStringObject().first();
            len--;
        }
        return context;
    }

    default Event applyZSet(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            byte[] element = parser.rdbLoadEncodedStringObject().first();
            double score = parser.rdbLoadDoubleValue();
            len--;
        }
        return context;
    }

    default Event applyZSet2(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            byte[] element = parser.rdbLoadEncodedStringObject().first();
            double score = parser.rdbLoadBinaryDoubleValue();
            len--;
        }
        return context;
    }

    default Event applyHash(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        long len = parser.rdbLoadLen().len;
        while (len > 0) {
            byte[] field = parser.rdbLoadEncodedStringObject().first();
            byte[] value = parser.rdbLoadEncodedStringObject().first();
            len--;
        }
        return context;
    }

    default Event applyHashZipMap(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);

        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        BaseRdbParser.LenHelper.zmlen(stream); // zmlen
        while (true) {
            int zmEleLen = BaseRdbParser.LenHelper.zmElementLen(stream);
            if (zmEleLen == 255) {
                return context;
            }
            byte[] field = BaseRdbParser.StringHelper.bytes(stream, zmEleLen);
            zmEleLen = BaseRdbParser.LenHelper.zmElementLen(stream);
            if (zmEleLen == 255) {
                //value is null
                // handle <filed, null>
                return context;
            }
            int free = BaseRdbParser.LenHelper.free(stream);
            byte[] value = BaseRdbParser.StringHelper.bytes(stream, zmEleLen);
            BaseRdbParser.StringHelper.skip(stream, free);
            // handle <field, value>
        }
    }

    default Event applyListZipList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
        BaseRdbParser.LenHelper.zltail(stream); // zltail
        int zllen = BaseRdbParser.LenHelper.zllen(stream);
        for (int i = 0; i < zllen; i++) {
            byte[] element = BaseRdbParser.StringHelper.zipListEntry(stream);
            // handle element
        }
        int zlend = BaseRdbParser.LenHelper.zlend(stream);
        if (zlend != 255) {
            throw new AssertionError("zlend expect 255 but " + zlend);
        }
        return context;
    }

    default Event applySetIntSet(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        int encoding = BaseRdbParser.LenHelper.encoding(stream);
        long lenOfContent = BaseRdbParser.LenHelper.lenOfContent(stream);
        for (long i = 0; i < lenOfContent; i++) {
            switch (encoding) {
                case 2:
                    String element = String.valueOf(stream.readInt(2));
                    break;
                case 4:
                    element = String.valueOf(stream.readInt(4));
                    break;
                case 8:
                    element = String.valueOf(stream.readLong(8));
                    break;
                default:
                    throw new AssertionError("expect encoding [2,4,8] but:" + encoding);
            }
        }
        return context;
    }

    default Event applyZSetZipList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);

        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
        BaseRdbParser.LenHelper.zltail(stream); // zltail
        int zllen = BaseRdbParser.LenHelper.zllen(stream);
        while (zllen > 0) {
            byte[] element = BaseRdbParser.StringHelper.zipListEntry(stream);
            zllen--;
            double score = Double.valueOf(Strings.toString(BaseRdbParser.StringHelper.zipListEntry(stream)));
            zllen--;
        }
        int zlend = BaseRdbParser.LenHelper.zlend(stream);
        if (zlend != 255) {
            throw new AssertionError("zlend expect 255 but " + zlend);
        }
        return context;
    }
    
    default Event applyZSetListPack(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        RedisInputStream listPack = new RedisInputStream(parser.rdbLoadPlainStringObject());
        listPack.skip(4); // total-bytes
        int len = listPack.readInt(2);
        while (len > 0) {
            byte[] element = listPackEntry(listPack);
            len--;
            double score = Double.valueOf(Strings.toString(listPackEntry(listPack)));
            len--;
        }
        int lpend = listPack.read(); // lp-end
        if (lpend != 255) {
            throw new AssertionError("listpack expect 255 but " + lpend);
        }
        return context;
    }

    default Event applyHashZipList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);

        RedisInputStream stream = new RedisInputStream(parser.rdbLoadPlainStringObject());
        BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
        BaseRdbParser.LenHelper.zltail(stream); // zltail
        int zllen = BaseRdbParser.LenHelper.zllen(stream);
        while (zllen > 0) {
            byte[] field = BaseRdbParser.StringHelper.zipListEntry(stream);
            zllen--;
            byte[] value = BaseRdbParser.StringHelper.zipListEntry(stream);
            zllen--;
        }
        int zlend = BaseRdbParser.LenHelper.zlend(stream);
        if (zlend != 255) {
            throw new AssertionError("zlend expect 255 but " + zlend);
        }
        return context;
    }
    
    default Event applyHashListPack(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        RedisInputStream listPack = new RedisInputStream(parser.rdbLoadPlainStringObject());
        listPack.skip(4); // total-bytes
        int len = listPack.readInt(2);
        while (len > 0) {
            byte[] field = listPackEntry(listPack);
            len--;
            byte[] value = listPackEntry(listPack);
            len--;
        }
        int lpend = listPack.read(); // lp-end
        if (lpend != 255) {
            throw new AssertionError("listpack expect 255 but " + lpend);
        }
        return context;
    }

    default Event applyListQuickList(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);

        long len = parser.rdbLoadLen().len;
        for (long i = 0; i < len; i++) {
            RedisInputStream stream = new RedisInputStream(parser.rdbGenericLoadStringObject(RDB_LOAD_NONE));

            BaseRdbParser.LenHelper.zlbytes(stream); // zlbytes
            BaseRdbParser.LenHelper.zltail(stream); // zltail
            int zllen = BaseRdbParser.LenHelper.zllen(stream);
            for (int j = 0; j < zllen; j++) {
                byte[] element = BaseRdbParser.StringHelper.zipListEntry(stream);
            }
            int zlend = BaseRdbParser.LenHelper.zlend(stream);
            if (zlend != 255) {
                throw new AssertionError("zlend expect 255 but " + zlend);
            }
        }
        return context;
    }
    
    default Event applyListQuickList2(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        long len = parser.rdbLoadLen().len;
        for (long i = 0; i < len; i++) {
            long container = parser.rdbLoadLen().len;
            ByteArray bytes = parser.rdbLoadPlainStringObject();
            if (container == QUICKLIST_NODE_CONTAINER_PLAIN) {
                byte[] e = bytes.first();
            } else if (container == QUICKLIST_NODE_CONTAINER_PACKED) {
                RedisInputStream listPack = new RedisInputStream(bytes);
                listPack.skip(4); // total-bytes
                int innerLen = listPack.readInt(2);
                for (int j = 0; j < innerLen; j++) {
                    byte[] e = listPackEntry(listPack);
                }
                int lpend = listPack.read(); // lp-end
                if (lpend != 255) {
                    throw new AssertionError("listpack expect 255 but " + lpend);
                }
            } else {
                throw new UnsupportedOperationException(String.valueOf(container));
            }
        }
        return context;
    }

    default Event applyModule(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        new DefaultRdbValueVisitor(replicator).applyModule(in, version);
        return context;
    }

    default Event applyModule2(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        new DefaultRdbValueVisitor(replicator).applyModule2(in, version);
        return context;
    }

    default Event applyStreamListPacks(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);

        // Entries
        long listPacks = parser.rdbLoadLen().len;
        while (listPacks-- > 0) {
            RedisInputStream rawId = new RedisInputStream(parser.rdbLoadPlainStringObject());
            Stream.ID baseId = new Stream.ID(rawId.readLong(8, false), rawId.readLong(8, false));
            RedisInputStream listPack = new RedisInputStream(parser.rdbLoadPlainStringObject());
            listPack.skip(4); // total-bytes
            listPack.skip(2); // num-elements
            long count = Long.parseLong(Strings.toString(listPackEntry(listPack))); // count
            long deleted = Long.parseLong(Strings.toString(listPackEntry(listPack))); // deleted
            int numFields = Integer.parseInt(Strings.toString(listPackEntry(listPack))); // num-fields
            byte[][] tempFields = new byte[numFields][];
            for (int i = 0; i < numFields; i++) {
                tempFields[i] = listPackEntry(listPack);
            }
            listPackEntry(listPack); // 0

            long total = count + deleted;
            while (total-- > 0) {
                int flag = Integer.parseInt(Strings.toString(listPackEntry(listPack)));
                long ms = Long.parseLong(Strings.toString(listPackEntry(listPack)));
                long seq = Long.parseLong(Strings.toString(listPackEntry(listPack)));
                Stream.ID id = baseId.delta(ms, seq);
                boolean delete = (flag & STREAM_ITEM_FLAG_DELETED) != 0;
                if ((flag & STREAM_ITEM_FLAG_SAMEFIELDS) != 0) {
                    for (int i = 0; i < numFields; i++) {
                        byte[] value = listPackEntry(listPack);
                        byte[] field = tempFields[i];
                        // handle <field value>
                    }
                    
                    // handle entry <id, new Stream.Entry(id, delete, fields)>
                } else {
                    numFields = Integer.parseInt(Strings.toString(listPackEntry(listPack)));
                    for (int i = 0; i < numFields; i++) {
                        byte[] field = listPackEntry(listPack);
                        byte[] value = listPackEntry(listPack);
                        // handle <field value>
                    }
                    // handle entry <id, new Stream.Entry(id, delete, fields)>
                }
                listPackEntry(listPack); // lp-count
            }
            int lpend = listPack.read(); // lp-end
            if (lpend != 255) {
                throw new AssertionError("listpack expect 255 but " + lpend);
            }
        }

        long length = parser.rdbLoadLen().len;
        Stream.ID lastId = new Stream.ID(parser.rdbLoadLen().len, parser.rdbLoadLen().len);
        // handle <length, lastId>

        long groupCount = parser.rdbLoadLen().len;
        while (groupCount-- > 0) {
            byte[] groupName = parser.rdbLoadPlainStringObject().first();
            Stream.ID groupLastId = new Stream.ID(parser.rdbLoadLen().len, parser.rdbLoadLen().len);
            // handle <groupName , groupLastId>

            long globalPel = parser.rdbLoadLen().len;
            while (globalPel-- > 0) {
                Stream.ID rawId = new Stream.ID(in.readLong(8, false), in.readLong(8, false));
                long deliveryTime = parser.rdbLoadMillisecondTime();
                long deliveryCount = parser.rdbLoadLen().len;
                // handle group pending entry <rawId, new Stream.Nack(rawId, null, deliveryTime, deliveryCount)>
            }

            long consumerCount = parser.rdbLoadLen().len;
            while (consumerCount-- > 0) {
                byte[] consumerName = parser.rdbLoadPlainStringObject().first();
                long seenTime = parser.rdbLoadMillisecondTime();
                // handle <consumerName, seenTime>

                long pel = parser.rdbLoadLen().len;
                while (pel-- > 0) {
                    Stream.ID rawId = new Stream.ID(in.readLong(8, false), in.readLong(8, false));
                    // handle consumer pending entry <rawId>
                }
            }
        }
        return context;
    }
    
    default Event applyStreamListPacks2(Replicator replicator, RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        
        // Entries
        long listPacks = parser.rdbLoadLen().len;
        while (listPacks-- > 0) {
            RedisInputStream rawId = new RedisInputStream(parser.rdbLoadPlainStringObject());
            Stream.ID baseId = new Stream.ID(rawId.readLong(8, false), rawId.readLong(8, false));
            RedisInputStream listPack = new RedisInputStream(parser.rdbLoadPlainStringObject());
            listPack.skip(4); // total-bytes
            listPack.skip(2); // num-elements
            long count = Long.parseLong(Strings.toString(listPackEntry(listPack))); // count
            long deleted = Long.parseLong(Strings.toString(listPackEntry(listPack))); // deleted
            int numFields = Integer.parseInt(Strings.toString(listPackEntry(listPack))); // num-fields
            byte[][] tempFields = new byte[numFields][];
            for (int i = 0; i < numFields; i++) {
                tempFields[i] = listPackEntry(listPack);
            }
            listPackEntry(listPack); // 0
            
            long total = count + deleted;
            while (total-- > 0) {
                int flag = Integer.parseInt(Strings.toString(listPackEntry(listPack)));
                long ms = Long.parseLong(Strings.toString(listPackEntry(listPack)));
                long seq = Long.parseLong(Strings.toString(listPackEntry(listPack)));
                Stream.ID id = baseId.delta(ms, seq);
                boolean delete = (flag & STREAM_ITEM_FLAG_DELETED) != 0;
                if ((flag & STREAM_ITEM_FLAG_SAMEFIELDS) != 0) {
                    for (int i = 0; i < numFields; i++) {
                        byte[] value = listPackEntry(listPack);
                        byte[] field = tempFields[i];
                        // handle <field value>
                    }
                    
                    // handle entry <id, new Stream.Entry(id, delete, fields)>
                } else {
                    numFields = Integer.parseInt(Strings.toString(listPackEntry(listPack)));
                    for (int i = 0; i < numFields; i++) {
                        byte[] field = listPackEntry(listPack);
                        byte[] value = listPackEntry(listPack);
                        // handle <field value>
                    }
                    // handle entry <id, new Stream.Entry(id, delete, fields)>
                }
                listPackEntry(listPack); // lp-count
            }
            int lpend = listPack.read(); // lp-end
            if (lpend != 255) {
                throw new AssertionError("listpack expect 255 but " + lpend);
            }
        }
        
        long length = parser.rdbLoadLen().len;
        Stream.ID lastId = new Stream.ID(parser.rdbLoadLen().len, parser.rdbLoadLen().len);
        Stream.ID firstId = new Stream.ID(parser.rdbLoadLen().len, parser.rdbLoadLen().len);
        Stream.ID maxDeletedEntryId = new Stream.ID(parser.rdbLoadLen().len, parser.rdbLoadLen().len);
        long entriesAdded = parser.rdbLoadLen().len;
        // handle <length, lastId, firstId, maxDeletedEntryId, entriesAdded>
        long groupCount = parser.rdbLoadLen().len;
        while (groupCount-- > 0) {
            byte[] groupName = parser.rdbLoadPlainStringObject().first();
            Stream.ID groupLastId = new Stream.ID(parser.rdbLoadLen().len, parser.rdbLoadLen().len);
            long entriesRead = parser.rdbLoadLen().len;
            // handle <groupName, groupLastId, entriesRead>
            
            long globalPel = parser.rdbLoadLen().len;
            while (globalPel-- > 0) {
                Stream.ID rawId = new Stream.ID(in.readLong(8, false), in.readLong(8, false));
                long deliveryTime = parser.rdbLoadMillisecondTime();
                long deliveryCount = parser.rdbLoadLen().len;
                // handle group pending entry <rawId, new Stream.Nack(rawId, null, deliveryTime, deliveryCount)>
            }
            
            long consumerCount = parser.rdbLoadLen().len;
            while (consumerCount-- > 0) {
                byte[] consumerName = parser.rdbLoadPlainStringObject().first();
                long seenTime = parser.rdbLoadMillisecondTime();
                // handle <consumerName, seenTime>
                
                long pel = parser.rdbLoadLen().len;
                while (pel-- > 0) {
                    Stream.ID rawId = new Stream.ID(in.readLong(8, false), in.readLong(8, false));
                    // handle consumer pending entry <rawId>
                }
            }
        }
        return context;
    }
}
