package com.redhat.emergency.response.responder.simulator.tracing;

import java.util.Iterator;
import java.util.Map;

import com.redhat.emergency.response.responder.simulator.model.ResponderLocation;
import io.opentracing.propagation.TextMap;

public class ResponderLocationInjectAdapter implements TextMap {

    private final ResponderLocation responderLocation;

    public ResponderLocationInjectAdapter(ResponderLocation responderLocation) {
        this.responderLocation = responderLocation;
    }


    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("iterator should never be used with Tracer.inject()");
    }

    @Override
    public void put(String key, String value) {
        if (responderLocation != null) {
            responderLocation.getSpanContextMap().put(key, value);
        }
    }
}
