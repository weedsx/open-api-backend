-- 接口信息表
create table if not exists open_api.`interface_info`
(
    id
    bigint
    auto_increment
    comment
    '主键'
    primary
    key,
    name
    varchar
(
    256
) not null comment '接口名称',
    description varchar
(
    256
) null comment '描述',
    url varchar
(
    256
) not null comment 'url地址',
    request_params text null comment '请求参数',
    request_header text null comment '请求头',
    response_header text null comment '响应头',
    method varchar
(
    256
) not null comment '请求类型',
    status tinyint default 0 not null comment '接口状态（0-关闭，1-开启）',
    creator_id bigint not null comment '创建人id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
    )
    comment '接口信息';

insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.brendon-konopelski.org', 'www.marty-wilderman.com', 'POST', 1, 9487722);
insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.bianca-marquardt.net', 'www.ernest-torphy.org', 'GET', 0, 14213174);
insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.mario-kemmer.co', 'www.ashleigh-smitham.com', 'DELETE', 1, 835607);
insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.marquis-herman.biz', 'www.edgar-marks.biz', 'GET', 0, 803662897);
insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.elois-dare.name', 'www.angle-bergstrom.org', 'GET', 1, 11609554);
insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.lucinda-harris.info', 'www.tom-ward.info', 'DELETE', 0, 443759397);
insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.janay-marvin.com', 'www.calvin-bruen.co', 'POST', 0, 426105);
insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.jarred-hackett.biz', 'www.val-mertz.net', 'DELETE', 1, 46189);
insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.corey-bins.org', 'www.camila-hirthe.org', 'POST', 0, 8340361);
insert into open_api.`interface_info` (`name`, `url`, `method`, `status`, `creator_id`)
values ('www.ivy-wehner.co', 'www.stanford-stracke.org', 'POST', 0, 60086013);

-- 用户表
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    access_key   varchar(512)                           not null comment 'accessKey',
    secret_key   varchar(512)                           not null comment 'secretKey',
    unionId      varchar(256) null comment '微信开放平台id',
    mpOpenId     varchar(256) null comment '公众号openId',
    userName     varchar(256) null comment '用户昵称',
    userAvatar   varchar(1024) null comment '用户头像',
    userProfile  varchar(512) null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;

create index idx_unionId
    on user (unionId);

-- 用户接口关系表
create table user_interface_count
(
    id           bigint auto_increment comment '主键'
        primary key,
    user_id      bigint                             not null comment '用户id',
    interface_id bigint                             not null comment '接口id',
    invoke_count int      default 0                 not null comment '调用次数',
    left_count   int      default 0                 not null comment '剩余调用次数',
    status       tinyint  default 0                 not null comment '状态（0-正常，1-禁用）',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted   tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
) comment '用户接口关系表';
