package blake.bot.suppliers.strategies;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ddejonge.bandana.negoProtocol.OrderCommitment;
import es.csic.iiia.fabregues.dip.board.Game;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MTOOrderBuilder.class, name = "MTO"),
        @JsonSubTypes.Type(value = HLDOrderBuilder.class, name = "HLD"),
        @JsonSubTypes.Type(value = BLDOrderBuilder.class, name = "BLD"),
        @JsonSubTypes.Type(value = SUPMTOOrderBuilder.class, name = "SUPMTO")
}
)
public interface OrderBuilder {
    OrderCommitment construct(Game game);

    String getPower();
}
