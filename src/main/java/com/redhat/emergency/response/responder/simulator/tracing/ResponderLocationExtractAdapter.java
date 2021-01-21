package com.redhat.emergency.response.responder.simulator.tracing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.redhat.emergency.response.responder.simulator.model.ResponderLocation;
import io.opentracing.propagation.TextMap;

public class ResponderLocationExtractAdapter implements TextMap {

    private final Map<String, String> spanContextMap;

    public ResponderLocationExtractAdapter(ResponderLocation responderLocation) {
        if (responderLocation != null) {
            this.spanContextMap = responderLocation.getSpanContextMap();
        } else {
            this.spanContextMap = new HashMap<>();
        }
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return spanContextMap.entrySet().iterator();
    }

    @Override
    public void put(String key, String value) {
        throw new UnsupportedOperationException(
                "HeadersMapExtractAdapter should only be used with Tracer.extract()");
    }
}
