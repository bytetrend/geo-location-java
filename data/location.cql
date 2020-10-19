CREATE KEYSPACE IF NOT EXISTS sanbox WITH durable_writes = true
AND replication = {
	'class' : 'NetworkTopologyStrategy',
	'datacenter1' : 2
};

DROP TABLE IF EXISTS sandbox.location;

CREATE TABLE sandbox.location (
sequence int,
latitude float,
longitude float,
name text,
address text,
city text,
state text,
postalcode text,
country text,
website text,
category text,
PRIMARY KEY (sequence, longitude, latitude)
) WITH CLUSTERING ORDER BY (longitude ASC, latitude ASC)
AND bloom_filter_fp_chance = 0.01
AND caching = {'keys': 'ALL','rows_per_partition': 'NONE'}
AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
AND crc_check_chance = 0.1
AND default_time_to_live = 0
AND gc_grace_seconds = 864000
AND max_index_interval = 2048
AND memtable_flush_period_in_ms = 0
AND min_index_interval = 128
AND read_repair_chance = 0.0
AND speculative_retry = '99PERCENTILE';
