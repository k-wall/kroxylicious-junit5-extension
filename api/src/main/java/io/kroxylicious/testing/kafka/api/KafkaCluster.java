/*
 * Copyright Kroxylicious Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

package io.kroxylicious.testing.kafka.api;

import java.util.Map;
import java.util.function.Predicate;

/**
 * A KafkaCluster, from which is it possible to create/connect clients.
 * Clients can be created using {@link #getKafkaClientConfiguration()} or
 * {@link #getKafkaClientConfiguration(String, String)}.
 * <br/>
 * The cluster needs to be {@link #start() start}ed prior to use,
 * and should be {@link #close() close}d after use.
 */
public interface KafkaCluster extends AutoCloseable {
    /**
     * starts the cluster.
     */
    void start();

    /**
     * adds a new broker to the cluster. Once this method returns the caller is guaranteed that the new broker will
     * be incorporated into the cluster.  In kraft mode, the broker added will always from the role 'broker'.
     *
     * @return kafka <code>node.id</code> of the created broker.
     */
    int addBroker();

    /**
     * removes broker identified by the given <code>node.id</code> from the cluster.  Once this method returns the
     * caller is guaranteed that the broker has stopped and is no longer part of the cluster.
     * <p>
     * in kraft mode, it is not permitted to remove a broker that is the controller mode.
     *
     * @param nodeId node to be removed from the cluster
     * @throws UnsupportedOperationException the <code>node.id</code> identifies a kraft controller
     * @throws IllegalArgumentException      the node identified by <code>node.id</code> does not exist.
     */
    void removeBroker(int nodeId) throws UnsupportedOperationException, IllegalArgumentException;

    /**
     * Restarts the broker(s) identified by the supplied predicate.  Once this method returns the
     * caller is guaranteed that the broker(s) have been restarted and have been reincorporated into the
     * cluster.
     * <br/>
     * The caller specifies a <code>terminationStyle</code>.  If it is set {@link TerminationStyle#ABRUPT}, the kafka
     * broker(s) will be abruptly killed, rather than undergoing a graceful shutdown. This may be useful to test cases
     * wishing to explore networking edge cases.  The implementation may ignore this parameter if the
     * implementation is unable to support the requested style of termination. In this case, the implementation
     * is free to use an alternative termination style instead.
     * <br/>
     * The caller may provide an <code>onBrokersStopped</code> runnable.  If not null, the runnable will be invoked,
     * exactly once, when all the requested brokers are stopped. The restart of the brokers will not commence until
     * this method returns.  Behaviour is not defined if the callable throws unchecked exceptions.  Furthermore, it
     * must not attempt to mutate the cluster (that is, make calls to methods such as {@link KafkaCluster#addBroker()}).
     * There is no guarantee made about the thread used to execute the callback.
     *
     * @param nodeIdPredicate  predicate that returns true if the node identified by the given nodeId should be restarted
     * @param terminationStyle the style of termination used to shut down the broker(s).
     * @param onBrokersStopped callback to invoked when all brokers are stopped, before restart commences. may be null.
     */
    void restartBrokers(Predicate<Integer> nodeIdPredicate, TerminationStyle terminationStyle, Runnable onBrokersStopped);

    /**
     * stops the cluster.
     */
    @Override
    void close() throws Exception;

    /**
     * Gets the number of brokers expected in the cluster
     * @return the size of the cluster.
     */
    int getNumOfBrokers();

    /**
     * Gets the bootstrap servers for this cluster
     * @return bootstrap servers
     */
    String getBootstrapServers();

    /**
     * Gets the cluster id
     * @return The cluster id for KRaft-based clusters, otherwise null;
     */
    String getClusterId();

    /**
     * Gets the kafka configuration for making connections to this cluster as required by the
     * {@code org.apache.kafka.clients.admin.AdminClient}, {@code org.apache.kafka.clients.producer.Producer} etc.
     * Details such the bootstrap and SASL configuration are provided automatically.
     * The returned map is guaranteed to be mutable and is unique to the caller.
     *
     * @return mutable configuration map
     */
    Map<String, Object> getKafkaClientConfiguration();

    /**
     * Gets the kafka configuration for making connections to this cluster as required by the
     * {@code org.apache.kafka.clients.admin.AdminClient}, {@code org.apache.kafka.clients.producer.Producer} etc.
     * Details such the bootstrap and SASL configuration are provided automatically.
     * The returned map is guaranteed to be mutable and is unique to the caller.
     *
     * @param user The user
     * @param password The password
     * @return mutable configuration map
     */
    Map<String, Object> getKafkaClientConfiguration(String user, String password);
}
