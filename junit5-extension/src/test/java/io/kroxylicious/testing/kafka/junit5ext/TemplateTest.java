/*
 * Copyright Kroxylicious Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.kroxylicious.testing.kafka.junit5ext;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.apache.kafka.clients.admin.Admin;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import io.kroxylicious.testing.kafka.api.KafkaCluster;
import io.kroxylicious.testing.kafka.common.BrokerCluster;
import io.kroxylicious.testing.kafka.common.BrokerConfig;
import io.kroxylicious.testing.kafka.common.KRaftCluster;
import io.kroxylicious.testing.kafka.common.Version;
import io.kroxylicious.testing.kafka.testcontainers.TestcontainersKafkaCluster;

import static io.kroxylicious.testing.kafka.common.ConstraintUtils.brokerCluster;
import static io.kroxylicious.testing.kafka.common.ConstraintUtils.brokerConfig;
import static io.kroxylicious.testing.kafka.common.ConstraintUtils.kraftCluster;
import static io.kroxylicious.testing.kafka.common.ConstraintUtils.version;
import static io.kroxylicious.testing.kafka.common.ConstraintUtils.zooKeeperCluster;

@ExtendWith(KafkaClusterExtension.class)
public class TemplateTest {

    static Stream<BrokerCluster> clusterSizes() {
        return Stream.of(
                brokerCluster(1),
                brokerCluster(3));
    }

    @TestTemplate
    public void testMultipleClusterSizes(
                                         @DimensionMethodSource("clusterSizes") KafkaCluster cluster) {
    }

    @TestTemplate
    public void testMultipleClusterSizesWithAdminParameters(@DimensionMethodSource("clusterSizes") KafkaCluster cluster,
                                                            Admin admin) {
    }

    static Stream<BrokerConfig> compression() {
        return Stream.of(
                brokerConfig("compression.type", "zstd"),
                brokerConfig("compression.type", "snappy"));
    }

    @TestTemplate
    public void testCartesianProduct(@DimensionMethodSource("clusterSizes") @DimensionMethodSource("compression") KafkaCluster cluster,
                                     Admin admin)
             {
    }

    static Stream<List<Annotation>> tuples() {
        return Stream.of(
                List.of(brokerCluster(1), kraftCluster(1)),
                List.of(brokerCluster(3), kraftCluster(1)),
                List.of(brokerCluster(3), zooKeeperCluster()));
    }

    private static Stream<Version> versions() {
        return Stream.of(
                version("latest")
        // TODO: waiting for new versions support in ozangunalp repo https://github.com/ozangunalp/kafka-native/issues/21
        // version("3.3.1"),
        // version("3.2.1")
        );
    }

    @TestTemplate
    @ExtendWith(KafkaClusterExtension.class)
    public void testVersions(@DimensionMethodSource("versions") @KRaftCluster TestcontainersKafkaCluster cluster)
            throws Exception {
    }

}
