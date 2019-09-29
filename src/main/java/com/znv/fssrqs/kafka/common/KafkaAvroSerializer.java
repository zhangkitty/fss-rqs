package com.znv.fssrqs.kafka.common;


import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class KafkaAvroSerializer implements Serializer<Object> {
    private String encoding = "UTF8";
    protected static final byte MAGIC_BYTE = 0;
    protected static final int IDSIZE = 4;
    private final EncoderFactory encoderFactory = EncoderFactory.get();

    public KafkaAvroSerializer() {
    }

    public void configure(Map<String, ?> configs, boolean isKey) {
        String propertyName = isKey ? "key.serializer.encoding" : "value.serializer.encoding";
        Object encodingValue = configs.get(propertyName);
        if (encodingValue == null) {
            encodingValue = configs.get("serializer.encoding");
        }

        if (encodingValue != null && encodingValue instanceof String) {
            this.encoding = (String) encodingValue;
        }

    }

    public byte[] serialize(String topic, Object record) {
        Schema schema = null;
        if (record == null) {
            return null;
        } else {
            try {
                schema = ZnvSchema.getSchema(record);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                if (record instanceof byte[]) {
                    out.write((byte[]) ((byte[]) record));
                } else {
                    BinaryEncoder encoder = this.encoderFactory.directBinaryEncoder(out, (BinaryEncoder) null);
                    DatumWriter<Object> writer = null;
                    if (record instanceof SpecificRecord) {
                        writer = new SpecificDatumWriter(schema);
                    } else {
                        writer = new GenericDatumWriter(schema);
                    }

                    ((DatumWriter) writer).write(record, encoder);
                    encoder.flush();
                }

                byte[] bytes = out.toByteArray();
                out.close();
                return bytes;
            } catch (Exception var8) {
                throw new SerializationException("Error serializing Avro message", var8);
            }
        }
    }

    public void close() {
    }
}