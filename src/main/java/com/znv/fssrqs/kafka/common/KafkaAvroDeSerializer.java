package com.znv.fssrqs.kafka.common;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class KafkaAvroDeSerializer implements Deserializer<Object> {
    boolean isKey = false;

    public KafkaAvroDeSerializer() {
    }

    private ByteBuffer getByteBuffer(byte[] payload) {
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        return buffer;
    }

    public void configure(Map<String, ?> configs, boolean isKey) {
        this.isKey = isKey;
    }

    public Object deserialize(String topic, byte[] data) {
        Object result = null;

        try {
            ByteBuffer buffer = this.getByteBuffer(data);
            Schema schema = null;
            if (this.isKey) {
                schema = ZnvSchema.CharSequence.schema();
            } else {
                schema = ZnvSchema.Map.schema();
            }

            SpecificDatumReader<Schema> reader = new SpecificDatumReader(schema);
            Decoder decoder = DecoderFactory.get().binaryDecoder(buffer.array(), (BinaryDecoder)null);
            Object object = reader.read(null, decoder);
            if (schema.getType().equals(Type.STRING)) {
                object = object.toString();
            }

            result = object;
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        return result;
    }

    public void close() {
    }
}