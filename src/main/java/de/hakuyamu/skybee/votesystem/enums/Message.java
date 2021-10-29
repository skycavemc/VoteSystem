package de.hakuyamu.skybee.votesystem.enums;

import de.hakuyamu.skybee.votesystem.models.FormattableString;

public enum Message {

    DATABASE_ERROR("&cIm Datenbanksystem liegt ein Fehler vor."),
    PLAYER_NOT_FOUND("&cDer Spieler %player konnte nicht gefunden werden."),
    INVALID_NUMBER("&c%number ist keine gültige Zahl."),

    // vote rewards
    VOTE_DEFAULT("&b&k!!!&r &3%player &7hat gevoted und &610 &ePollen &7erhalten! &3/vote"),
    VOTE_LUCK("&b&k!!!&r &3%player &7hat Glück und erhält &615 &ePollen&7! &3/vote"),
    VOTE_ZIEL_REACHED("&3%player &7hat das persönliche Ziel &6%votes Votes &7erreicht und erhält %reward&7!"),
    VOTE_EVENT_REACHED("&aDas &2%number. VoteEvent Ziel &avon &d%votes Votes &awurde erreicht! &8(&7/vote event&8)"),
    VOTE_EVENT_REWARD("&eJeder Spieler erhält %reward!"),
    VOTE_BROADCAST("&aVergiss nicht zu Voten: &2/vote&a! &eDu hast bereits &6%votes Votes."),
    VOTE_EVENT_BROADCAST("&aDas &2Vote&dEvent &aläuft noch bis Sonntag, 23:59 Uhr! &8(&7/vote event&8)"),

    // vote command
    VOTE_WRONG_ARGS("&cUngültiges Argument. Siehe /vote help"),
    VOTE_INFO1("&8» &7Pro Vote &610 Pollen"),
    VOTE_INFO2("&8» &2+ &aPersönliche Ziele &8(&7/vote ziel&8)"),
    VOTE_INFO3("&8» &2+ &a20% Chance auf &615 Pollen"),

    // vote help
    VOTE_HELP("&a/vote\n&8» &7Zeigt dir die Vote Links an"),
    VOTE_HELP_EVENT("&a/vote event\n&8» &7Zeigt dir den Fortschritt des VoteEvents an"),
    VOTE_HELP_ZIEL("&a/vote ziel\n&8» &7Zeigt dir deine persönlichen VoteZiele an"),

    // vote event subcommand
    VOTE_EVENT_STATUS_ACTIVE("&8» &6Status: &aaktiv, &6Votes: &a%votes\n" +
            "&8» &6Nächstes: &e%next, &6Gestartet: &e%started"),
    VOTE_EVENT_STATUS_INACTIVE("&8» &6Status: &cabgeschlossen, &6Gestartet: &c%started, &6Geendet: &c%ended\n"),
    VOTE_EVENT_LINE_NOT("&7%numeral. &c%votes Votes &8= &c%name"),
    VOTE_EVENT_LINE_DONE("&7%numeral. &a%votes Votes &8= &a%name &2✔ &7&o%date"),

    // vote ziel subcommand
    VOTE_ZIEL_STATUS("&8» &6Deine Votes: &a%votes, &6Nächstes Ziel: &e%next"),
    VOTE_ZIEL_LINE_NOT("&7%numeral. &c%votes Votes &8= &c%name"),
    VOTE_ZIEL_LINE_DONE("&7%numeral. &a%votes Votes &8= &a%name &2✔"),

    // vote count subcommand
    VOTE_COUNT("&aDu hast momentan &6%votes Votes&a."),
    VOTE_COUNT_OTHER("&6%player &ahat momentan &6%votes Votes&a."),

    // vote top subcommand
    VOTE_TOP_HEADER_FOOTER("&8>&7--------&8[ &a%page&2/&a%amount &8]&7--------&8<"),
    VOTE_TOP_ENTRY("&6%rank. &a%player: &e%votes Votes"),

    // voteadmin command
    VADMIN_WRONG_ARGS("&cUngültiges Argument. Siehe /voteadmin help"),

    // voteadmin help
    VADMIN_HELP_START("&a/vadmin start\n&8» &7Startet das Vote Event"),
    VADMIN_HELP_STOP("&a/vadmin stop\n&8» &7Stoppt das Vote Event"),
    VADMIN_HELP_CLEAR("&a/vadmin clear\n&8» &7Setzt alle Daten zurück"),
    VADMIN_HELP_FAKE("&a/vadmin fake\n&8» &7Führt einen Fake Vote aus"),

    // voteadmin start subcommand
    VADMIN_START_FIRST("&e&lDas &2&lVote&d&lEvent &e&lwurde gestartet!"),
    VADMIN_START_ALREADY("&cDas Vote Event läuft bereits."),
    VADMIN_START_WEEK("&eDiese Woche wird übersprungen."),

    // voteadmin stop subcommand
    VADMIN_STOP_SUCCESS("&aDu hast das Vote Event gestoppt."),
    VADMIN_STOP_NOT("&cDas Vote Event läuft derzeit nicht."),

    // voteadmin clear subcommand
    VADMIN_CLEAR("&eWarteschlange und Votes geleert."),

    // voteadmin fake subcommand
    VADMIN_FAKE("&e/vadmin fake <Spieler>"),
    VADMIN_FAKE_EXE("&aFühre Fake Vote für %name aus..."),
    ;

    private final String string;

    Message(String string) {
        this.string = string;
    }

    public FormattableString getString() {
        return new FormattableString(string);
    }

}
