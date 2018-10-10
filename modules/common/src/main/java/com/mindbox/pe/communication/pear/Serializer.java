package com.mindbox.pe.communication.pear;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

public abstract class Serializer<T extends Serializer<?>> implements Serializable {

    private static final long serialVersionUID = -6478981413383738586L;
    private static final Logger LOG = Logger.getLogger(Serializer.class);

    public Serializer() {
        LOG.trace("Common()");
    }

    // deserialize

    public static Serializer<?> deserialize(ByteArrayInputStream input) throws Exception {
        ObjectInputStream stream = new ObjectInputStream(input);
        Serializer<?> result = (Serializer<?>) deserialize(stream);
        stream.close();
        return result;
    }

    public static Serializer<?> deserialize(InputStream input) throws Exception {
        ObjectInputStream stream = new ObjectInputStream(input);
        Serializer<?> result = (Serializer<?>) deserialize(stream);
        stream.close();
        return result;
    }

    public static Serializer<?> deserialize(ObjectInputStream input) throws Exception {
        Serializer<?> result = (Serializer<?>) input.readObject();
        return result;
    }

    // serialize

    public void serialize(ByteArrayOutputStream output) throws Exception {
        ObjectOutputStream stream = new ObjectOutputStream(output);
        serialize(stream);
        stream.close();
    }

    public void serialize(OutputStream output) throws Exception {
        ObjectOutputStream stream = new ObjectOutputStream(output);
        serialize(stream);
        stream.close();
    }

    public void serialize(ObjectOutputStream output) throws Exception {
        output.writeObject(this);
    }
}
