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

package com.moilioncircle.redis.rdb.cli.api.sink.parser;

import java.io.File;
import java.io.IOException;

import com.moilioncircle.redis.rdb.cli.api.sink.ParserService;
import com.moilioncircle.redis.replicator.Replicator;
import com.moilioncircle.redis.replicator.rdb.RdbVisitor;
import com.moilioncircle.redis.replicator.rdb.dump.DumpRdbVisitor;

/**
 * @author Baoyi Chen
 */
public class DumpParserService implements ParserService {
    
    @Override
    public String parser() {
        return "dump";
    }

    @Override
    public void init(File config) throws IOException {

    }

    @Override
    public Replicator wrap(Replicator replicator) {
        return replicator;
    }

    @Override
    public RdbVisitor getRdbVisitor(Replicator replicator) {
        return new DumpRdbVisitor(replicator);
    }
}
