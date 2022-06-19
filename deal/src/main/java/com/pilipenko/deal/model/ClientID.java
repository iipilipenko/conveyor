package com.pilipenko.deal.model;

import com.sun.org.apache.xml.internal.serializer.SerializerTrace;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@RequiredArgsConstructor
@Embeddable
@EqualsAndHashCode
public class ClientID implements Serializable {
    private Integer series;
    private Integer number;
}
