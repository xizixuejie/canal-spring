package io.xzxj.canal.core.model;

import org.apache.commons.collections4.keyvalue.MultiKey;

import java.util.Objects;


public class ListenerKey extends MultiKey<String> {
    private final String destination;
    private final String schemaName;

    private final String topic;
    private final String partition;

    public ListenerKey(String destination, String schemaName, String topic, String partition) {
        super(destination, schemaName, topic, partition);
        this.destination = destination;
        this.schemaName = schemaName;
        this.topic = topic;
        this.partition = partition;
    }

    public String getDestination() {
        return destination;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTopic() {
        return topic;
    }

    public String getPartition() {
        return partition;
    }

    public static ListenerKey empty() {
        return new Builder().build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ListenerKey that = (ListenerKey) o;

        if (!Objects.equals(destination, that.destination)) return false;
        if (!Objects.equals(schemaName, that.schemaName)) return false;
        if (!Objects.equals(topic, that.topic)) return false;
        return Objects.equals(partition, that.partition);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        result = 31 * result + (schemaName != null ? schemaName.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (partition != null ? partition.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private String destination;
        private String schemaName;
        private String topic;
        private String partition;

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder schemaName(String schemaName) {
            this.schemaName = schemaName;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder partition(String partition) {
            this.partition = partition;
            return this;
        }

        public Builder merge(Builder other) {
            // Merge logic based on non-null properties of other
            if (other.destination != null) {
                this.destination = other.destination;
            }
            if (other.topic != null) {
                this.topic = other.topic;
            }
            if (other.partition != null) {
                this.partition = other.partition;
            }
            if (other.schemaName != null) {
                this.schemaName = other.schemaName;
            }
            return this;
        }

        public ListenerKey build() {
            return new ListenerKey(destination, schemaName, topic, partition);
        }
    }


}
