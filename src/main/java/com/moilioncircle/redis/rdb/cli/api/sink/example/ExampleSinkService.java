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

package com.moilioncircle.redis.rdb.cli.api.sink.example;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moilioncircle.redis.rdb.cli.api.sink.SinkService;
import com.moilioncircle.redis.rdb.cli.api.sink.cmd.ClosedCommand;
import com.moilioncircle.redis.rdb.cli.api.sink.cmd.ClosingCommand;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.impl.PingCommand;
import com.moilioncircle.redis.replicator.cmd.impl.ReplConfCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SelectCommand;
import com.moilioncircle.redis.replicator.event.Event;
import com.moilioncircle.redis.replicator.event.PostCommandSyncEvent;
import com.moilioncircle.redis.replicator.event.PostRdbSyncEvent;
import com.moilioncircle.redis.replicator.event.PreCommandSyncEvent;
import com.moilioncircle.redis.replicator.event.PreRdbSyncEvent;
import com.moilioncircle.redis.replicator.rdb.datatype.KeyValuePair;

/**
 * @author Baoyi Chen
 */
public class ExampleSinkService implements SinkService {

    private static final Logger logger = LoggerFactory.getLogger(ExampleSinkService.class);

    private AtomicLong rdb = new AtomicLong(0L);
    private AtomicLong aof = new AtomicLong(0L);

    @Override
    public String sink() {
        return "example";
    }

    @Override
    public void init(File config) throws IOException {

    }

    @Override
    public void onEvent(Replicator replicator, Event event) {
        if (event instanceof PreRdbSyncEvent) {
            rdb.set(0);
            aof.set(0);
        }
        if (event instanceof KeyValuePair) {
            rdb.incrementAndGet();
        }

        if (event instanceof PostRdbSyncEvent ||
                event instanceof PreCommandSyncEvent ||
                event instanceof PostCommandSyncEvent ||
                //
                event instanceof PingCommand ||
                event instanceof SelectCommand ||
                event instanceof ReplConfCommand ||
                event instanceof ClosingCommand ||
                event instanceof ClosedCommand) {

            if (event instanceof PreCommandSyncEvent) {
                logger.info("rdb count {}", rdb.get());
            }

            if (event instanceof PingCommand) {
                logger.info("aof count {}", aof.get());
            }

            if (event instanceof ClosedCommand) {
                logger.info("rdb count {}, aof count {}", rdb.get(), aof.get());
            }
        } else if (event instanceof Command) {
            aof.incrementAndGet();
        }
    }
}
