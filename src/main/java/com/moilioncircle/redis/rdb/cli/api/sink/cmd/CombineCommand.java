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
import com.moilioncircle.redis.replicator.cmd.RedisCodec;
import com.moilioncircle.redis.replicator.cmd.impl.AbstractCommand;
import com.moilioncircle.redis.replicator.cmd.impl.DefaultCommand;

/**
 * @author Baoyi Chen
 */
public class CombineCommand extends AbstractCommand {
    private Command parsedCommand;
    private DefaultCommand defaultCommand;
    private static RedisCodec CODEC = new RedisCodec();

    public CombineCommand() {
        
    }

    public CombineCommand(DefaultCommand defaultCommand, Command parsedCommand) {
        this.defaultCommand = defaultCommand;
        this.parsedCommand = parsedCommand;
    }

    public DefaultCommand getDefaultCommand() {
        return defaultCommand;
    }

    public void setDefaultCommand(DefaultCommand defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    public Command getParsedCommand() {
        return parsedCommand;
    }

    public void setParsedCommand(Command parsedCommand) {
        this.parsedCommand = parsedCommand;
    }
    
    @Override
    public String toString() {
        return toString(this.defaultCommand);
    }
    
    public static String toString(DefaultCommand defaultCommand) {
        StringBuilder builder = new StringBuilder();
        builder.append(new String(defaultCommand.getCommand()));
        for (byte[] arg : defaultCommand.getArgs()) {
            builder.append(" ");
            builder.append(new String(CODEC.encode(arg)));
        }
        return builder.toString();
    }
}
