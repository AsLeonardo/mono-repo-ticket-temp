package com.ticket.ticket.infrastructure.json;

import com.ticket.ticket.domain.event.ID;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext; // ← faltava esse import
import tools.jackson.databind.deser.std.StdDeserializer;
import java.util.UUID;

public class IDDeserializer extends StdDeserializer<ID> {

  public IDDeserializer() {
    super(ID.class);
  }

  @Override
  public ID deserialize(JsonParser p, DeserializationContext ctx) { // ← sem IOException
    String value = p.getString();
    return new ID(UUID.fromString(value));
  }
}
