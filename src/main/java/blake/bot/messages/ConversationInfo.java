package blake.bot.messages;

import blake.bot.utility.DatedObject;
import blake.bot.utility.Utility;
import ddejonge.bandana.negoProtocol.BasicDeal;
import ddejonge.bandana.negoProtocol.DMZ;
import ddejonge.bandana.negoProtocol.DiplomacyProposal;
import ddejonge.bandana.negoProtocol.OrderCommitment;
import ddejonge.negoServer.Message;
import es.csic.iiia.fabregues.dip.board.Power;

import java.util.*;
import java.util.stream.Collectors;

public class ConversationInfo {
    private static final List<String> powerNameList = Arrays.asList("ENG", "FRA", "ITA", "GER", "AUS", "RUS", "TUR");
    private final Message message;
    private final DiplomacyProposal proposal;
    private final HashMap<String, List<OrderCommitment>> commitmentsByPower;
    private final HashMap<String, List<DMZ>> DMZsByPower;
    private final DatedObject expiryDate;
    private final DatedObject startDate;
    private final Map<String, ConversationStatus> participants;
    private boolean isConfirmed = false;

    public ConversationInfo(Message message) {
//        System.out.println("CREATING NEW CONVO INFO");
        this.message = message;
//        System.out.println("GETTING CONTENT");
        this.proposal = (DiplomacyProposal) message.getContent();
//        System.out.println("GETTING DEAL");
        BasicDeal deal = (BasicDeal) proposal.getProposedDeal();
        this.commitmentsByPower = new HashMap<>();
        this.DMZsByPower = new HashMap<>();
        participants = new HashMap<>();
//        System.out.println("Adding all Participants");
        proposal.getParticipants().forEach(participant -> participants.put(participant, ConversationStatus.UNDECLARED));
//        System.out.println("SETTING PROPOSER");
        participants.put(message.getSender(), ConversationStatus.PROPOSER);
//        System.out.println(this.participants);
//        System.out.println("JOINING DATES");
        final List<DatedObject> dates = Utility.Lists.append(
                (List<DatedObject>) Utility.Lists.mapList(deal.getOrderCommitments(), DatedObject::new),
                Utility.Lists.mapList(deal.getDemilitarizedZones(), DatedObject::new)
        );
//        System.out.println("GETTING LAST DATE");
        this.expiryDate = dates.stream().max(Comparator.naturalOrder()).orElse(null);
//        System.out.println("GETTING START DATE");
        this.startDate = dates.stream().min(Comparator.naturalOrder()).orElse(null);
    }

    public List<OrderCommitment> getCommitmentsByPowerName(final String name) {
        return commitmentsByPower.putIfAbsent(
                name,
                ((BasicDeal) proposal.getProposedDeal()).getOrderCommitments().stream()
                        .filter(orderCommitment -> orderCommitment.getOrder().getPower().getName().equals(name))
                        .collect(Collectors.toList())
        );
    }

    public List<DMZ> getDMZsByPowerName(final String name) {
        return this.DMZsByPower.putIfAbsent(
                name,
                ((BasicDeal) proposal.getProposedDeal()).getDemilitarizedZones().stream()
                        .filter(dmz -> Utility.Lists.mapList(dmz.getPowers(), Power::getName).contains(name))
                        .collect(Collectors.toList())
        );
    }

    public DatedObject getExpiryDate() {
        return expiryDate;
    }

    public DatedObject getStartDate() {
        return startDate;
    }

    public boolean markAcceptance(String sender) {
        if (this.participants.get(sender) != null) {
            this.participants.put(sender, this.isConfirmed ? ConversationStatus.CONFIRMED : ConversationStatus.ACCEPTED);
            return true;
        } else {
            return false;
        }
    }

    public boolean markRejection(String sender) {
        final ConversationStatus conversationStatus = this.participants.get(sender);
        if (conversationStatus != null && !EnumSet.of(ConversationStatus.ACCEPTED, ConversationStatus.CONFIRMED).contains(conversationStatus)) {
            this.participants.put(sender, ConversationStatus.REJECTED);
            return true;
        } else {
            return false;
        }
    }

    public boolean markConfirmation() {
        this.participants.forEach((key, value) -> {
            if (value == ConversationStatus.ACCEPTED) {
                this.participants.put(key, ConversationStatus.CONFIRMED);
            }
        });
        this.isConfirmed = true;
        return true;
    }

    public Message getMessage() {
        return this.message;
    }

    public Map<String, ConversationStatus> getParticipants() {
        return this.participants;
    }
}
