PRAGMA journal_mode=WAL; PRAGMA synchronous=NORMAL; PRAGMA shared_cache=ON;

CREATE TABLE IF NOT EXISTS "user"
(
    "id"          TEXT    NOT NULL,
    "name"        TEXT    NOT NULL,
    "type"        TEXT DEFAULT NULL,
    "avatar"      TEXT DEFAULT NULL,
    "email"       TEXT DEFAULT NULL,
    "badge"       TEXT DEFAULT NULL,
    "login_time"  INTEGER NOT NULL,
    "create_time" INTEGER NOT NULL,
    "update_time" INTEGER NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "group"
(
    "id"          TEXT    NOT NULL,
    "name"        TEXT    NOT NULL,
    "avatar"      TEXT DEFAULT NULL,
    "create_time" INTEGER NOT NULL,
    "update_time" INTEGER NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "chat_list"
(
    "id"           TEXT    NOT NULL,
    "user_id"      TEXT    NOT NULL,
    "target_id"    TEXT    NOT NULL,
    "target_info"  TEXT    NOT NULL,
    "unread_count" INTEGER DEFAULT 0,
    "last_message" TEXT    DEFAULT NULL,
    "type"         TEXT    DEFAULT NULL,
    "create_time"  INTEGER NOT NULL,
    "update_time"  INTEGER NOT NULL,
    PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "message"
(
    "id"            TEXT    NOT NULL,
    "from_id"       TEXT    NOT NULL,
    "to_id"         TEXT    NOT NULL,
    "from_info"     TEXT    NOT NULL,
    "message"       TEXT    DEFAULT NULL,
    "reference_msg" TEXT    DEFAULT NULL,
    "at_user"       TEXT    DEFAULT NULL,
    "is_show_time"  BOOLEAN DEFAULT 0,
    "type"          TEXT    DEFAULT NULL,
    "source"        TEXT    DEFAULT NULL,
    "create_time"   INTEGER NOT NULL,
    "update_time"   INTEGER NOT NULL,
    PRIMARY KEY ("id")
);

CREATE INDEX IF NOT EXISTS idx_message_from_id_to_id ON message (from_id, to_id);


CREATE TABLE IF NOT EXISTS "notify"
(
    "id"             TEXT    NOT NULL,
    "notify_title"   TEXT DEFAULT NULL,
    "notify_content" TEXT DEFAULT NULL,
    "type"           TEXT DEFAULT NULL,
    "create_time"    INTEGER NOT NULL,
    "update_time"    INTEGER NOT NULL,
    PRIMARY KEY ("id")
);
