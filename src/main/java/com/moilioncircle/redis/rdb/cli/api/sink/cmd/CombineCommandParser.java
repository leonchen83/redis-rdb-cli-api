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

package com.moilioncircle.redis.rdb.cli.api.sink.cmd;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.CommandParser;
import com.moilioncircle.redis.replicator.cmd.parser.DefaultCommandParser;

/**
 * @author Baoyi Chen
 */
public class CombineCommandParser implements CommandParser<CombineCommand> {
    
    private CommandParser<?> parser2;
    private DefaultCommandParser parser1;
    
    public CombineCommandParser(CommandParser<? extends Command> parser) {
        this.parser2 = parser;
        this.parser1 = new DefaultCommandParser();
    }
    
    @Override
    public CombineCommand parse(Object[] command) {
        return new CombineCommand(parser1.parse(command), parser2.parse(command));
    }
}
