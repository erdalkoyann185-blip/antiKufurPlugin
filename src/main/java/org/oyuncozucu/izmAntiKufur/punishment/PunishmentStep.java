package org.oyuncozucu.izmAntiKufur.punishment;

import java.util.List;

public record PunishmentStep(int violations, List<String> actions, int muteSeconds, List<String> commands) {
}
