CREATE TABLE IF NOT EXISTS `user`
(
    `id`          VARCHAR(255) NOT NULL,
    `name`        VARCHAR(255) NOT NULL,
    `type`        VARCHAR(255) DEFAULT NULL,
    `avatar`      TEXT         DEFAULT NULL,
    `email`       VARCHAR(255) DEFAULT NULL,
    `badge`       TEXT         DEFAULT NULL,
    `login_time`  timestamp(3) DEFAULT NULL,
    `create_time` timestamp(3) NOT NULL,
    `update_time` timestamp(3) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `group`
(
    `id`          VARCHAR(255) NOT NULL,
    `name`        VARCHAR(255) NOT NULL,
    `avatar`      TEXT DEFAULT NULL,
    `create_time` timestamp(3) NOT NULL,
    `update_time` timestamp(3) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `chat_list`
(
    `id`           VARCHAR(255) NOT NULL,
    `user_id`      VARCHAR(255) NOT NULL,
    `target_id`    VARCHAR(255) NOT NULL,
    `target_info`  TEXT         NOT NULL,
    `unread_count` INT          DEFAULT 0,
    `last_message` TEXT         DEFAULT NULL,
    `type`         VARCHAR(255) DEFAULT NULL,
    `create_time`  timestamp(3) NOT NULL,
    `update_time`  timestamp(3) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `message`
(
    `id`            VARCHAR(255) NOT NULL,
    `from_id`       VARCHAR(255) NOT NULL,
    `to_id`         VARCHAR(255) NOT NULL,
    `from_info`     TEXT         NOT NULL,
    `message`       TEXT         DEFAULT NULL,
    `reference_msg` TEXT         DEFAULT NULL,
    `at_user`       TEXT         DEFAULT NULL,
    `is_show_time`  TINYINT(1) DEFAULT 0,
    `type`          VARCHAR(255) DEFAULT NULL,
    `source`        VARCHAR(255) DEFAULT NULL,
    `create_time`   timestamp(3) NOT NULL,
    `update_time`   timestamp(3) NOT NULL,
    PRIMARY KEY (`id`),
    INDEX           `idx_message_from_id_to_id` (`from_id`, `to_id`)
);

CREATE TABLE IF NOT EXISTS `notify`
(
    `id`             VARCHAR(255) NOT NULL,
    `notify_title`   VARCHAR(255) DEFAULT NULL,
    `notify_content` TEXT         DEFAULT NULL,
    `type`           VARCHAR(255) DEFAULT NULL,
    `create_time`    timestamp(3) NOT NULL,
    `update_time`    timestamp(3) NOT NULL,
    PRIMARY KEY (`id`)
);