package org.oyuncozucu.izmAntiKufur.filter;

import java.util.Set;

public record FilterResult(boolean blocked, String sanitizedMessage, Set<String> matches) {
}
