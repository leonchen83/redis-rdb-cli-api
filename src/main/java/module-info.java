import com.moilioncircle.redis.rdb.cli.api.sink.ParserService;
import com.moilioncircle.redis.rdb.cli.api.sink.SinkService;
import com.moilioncircle.redis.rdb.cli.api.sink.example.ExampleSinkService;
import com.moilioncircle.redis.rdb.cli.api.sink.parser.DefaultParserService;
import com.moilioncircle.redis.rdb.cli.api.sink.parser.DumpParserService;

module com.moilioncircle.redis.rdb.cli.api {
    requires org.slf4j;
    requires com.moilioncircle.redis.replicator;

    exports com.moilioncircle.redis.rdb.cli.api.format;
    exports com.moilioncircle.redis.rdb.cli.api.format.escape;
    exports com.moilioncircle.redis.rdb.cli.api.format.escape.impl;
    exports com.moilioncircle.redis.rdb.cli.api.sink;
    exports com.moilioncircle.redis.rdb.cli.api.sink.cmd;
    exports com.moilioncircle.redis.rdb.cli.api.sink.example;
    exports com.moilioncircle.redis.rdb.cli.api.sink.listener;
    exports com.moilioncircle.redis.rdb.cli.api.sink.parser;
    exports com.moilioncircle.redis.rdb.cli.api.support;

    provides SinkService with ExampleSinkService;
    provides ParserService with DefaultParserService, DumpParserService;
}