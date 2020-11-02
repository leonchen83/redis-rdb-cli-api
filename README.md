# redis-rdb-cli-api

This project used as API in project [redis-rdb-cli](https://github.com/leonchen83/redis-rdb-cli).


# Usage

## 1. Create a maven project with pom.xml

```java  

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.your.company</groupId>
    <artifactId>your-sink-service</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.moilioncircle</groupId>
            <artifactId>redis-rdb-cli-api</artifactId>
            <version>1.0.0</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.moilioncircle</groupId>
            <artifactId>redis-replicator</artifactId>
            <version>3.4.0</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.25</version>
            <scope>provided</scope>
        </dependency>
        
        <!-- 
        <dependency>
            other dependencies
        </dependency>
        -->
        
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>${maven.compiler.source}</source>
                    <target>${maven.compiler.target}</target>
                    <encoding>${project.build.sourceEncoding}</encoding>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```

## 2. Implement sink service

* create `YourSinkService` implement `SinkService`  

```java  

public class YourSinkService implements SinkService {

    @Override
    public String sink() {
        return "your-sink-service";
    }

    @Override
    public void init(File config) throws IOException {
        // parse your external sink config
    }

    @Override
    public void onEvent(Replicator replicator, Event event) {
        // your sink business
    }
}

```

* register this service using Java SPI  

```java  
# create com.moilioncircle.redis.rdb.cli.api.sink.SinkService file in src/main/resources/META-INF/services/

|-src
|____main
| |____resources
| | |____META-INF
| | | |____services
| | | | |____com.moilioncircle.redis.rdb.cli.api.sink.SinkService

# add following content in com.moilioncircle.redis.rdb.cli.api.sink.SinkService

your.package.YourSinkService

```

## 3. Implement your formatter

* create `YourFormatter` extend `AbstractFormatter`  

```java  

public class YourFormatter extends AbstractFormatter {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Event applyString(RedisInputStream in, int version, byte[] key, int type, ContextKeyValuePair context) throws IOException {
        BaseRdbParser parser = new BaseRdbParser(in);
        byte[] val = parser.rdbLoadEncodedStringObject().first();
        OutputStreams.write(key, getOutputStream());
        OutputStreams.write(val, getOutputStream());
        OutputStreams.write("\n".getBytes(), getOutputStream());
        return context.valueOf(new DummyKeyValuePair());
    }
}

```

* register this formatter using Java SPI  

```java  
# create com.moilioncircle.redis.rdb.cli.api.format.Formatter file in src/main/resources/META-INF/services/

|-src
|____main
| |____resources
| | |____META-INF
| | | |____services
| | | | |____com.moilioncircle.redis.rdb.cli.api.format.Formatter

# add following content in com.moilioncircle.redis.rdb.cli.api.format.Formatter

your.package.YourFormatter

```

## 4. Package and deploy

```java  

mvn clean install

cp ./target/your-sink-service-1.0.0-jar-with-dependencies.jar /path/to/redis-rdb-cli/lib
```
## 5. Run

* run your sink service

```java  

ret -s redis://127.0.0.1:6379 -c config.conf -n your-sink-service
```

* run your formatter

```java  

rct -f test -s redis://127.0.0.1:6379 -o ./out.csv -t string -d 0 -e json
```

## 6. Debug sink service

```java  

    public static void main(String[] args) throws Exception {
        Replicator replicator = new RedisReplicator("redis://127.0.0.1:6379");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Replicators.closeQuietly(replicator);
        }));
        replicator.addExceptionListener((rep, tx, e) -> {
            throw new RuntimeException(tx.getMessage(), tx);
        });
        SinkService sink = new YourSinkService();
        sink.init(new File("/path/to/your-sink.conf"));
        replicator.addEventListener(new AsyncEventListener(sink, replicator, 4, Executors.defaultThreadFactory()));
        replicator.open();
    }

```
 