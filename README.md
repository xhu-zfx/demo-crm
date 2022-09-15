# Crm开发文档

# 1. 技术架构

* 视图层(view): 展示数据, 跟用户交互
* 控制层(controller): 控制业务流程(接受请求, 接受参数, 封装参数; 根据不同的业务调用不同的业务层)
* 业务层(service): 处理业务流程
* 持久层(Dao/Mapper): 操作数据库
* 整合层: 维护类资源, 维护数据库资源

# 2. 物理模型设计(表结构、实体类)

* tbl_user  用户表
* tbl_dic_type  数据字典类型值
* tbl_dic_value  数据字典值
* tbl_activity  市场活动表
* tbl_activity_remark 市场活动备注表
* tbl_clue  线索表
* tbl_clue_remark  线索备注表
* tbl_clue_activity_relation  线索和市场活动关系表
* tbl_customer  客户表
* tbl_customer_remaek  客户备注表
* tbl_contacts  联系人表
* tbl_contacts_remark  联系人备注表
* tbl_contacts_activity_relation  联系人和市场活动的关系表
* tbl_tran  交易
* tbl_tran_remark  交易备注表
* tbl_tran_history  交易历史表
* tbl_task  任务表
  创建表

```sql
drop table if exists tbl_clue;

drop table if exists tbl_clue_activity_relation;

drop table if exists tbl_clue_remark;

drop table if exists tbl_contacts;

drop table if exists tbl_contacts_activity_relation;

drop table if exists tbl_contacts_remark;

drop table if exists tbl_customer;

drop table if exists tbl_customer_remark;

drop table if exists tbl_dept;

drop table if exists tbl_dictionary_type;

drop table if exists tbl_dictionary_value;

drop table if exists tbl_marketing_activities;

drop table if exists tbl_marketing_activities_remark;

drop table if exists tbl_permission;

drop table if exists tbl_role;

drop table if exists tbl_role_permission_relation;

drop table if exists tbl_task;

drop table if exists tbl_task_remark;

drop table if exists tbl_transaction;

drop table if exists tbl_transaction_history;

drop table if exists tbl_transaction_remark;

drop table if exists tbl_user;

drop table if exists tbl_user_role_relation;

/*==============================================================*/
/* Table: tbl_clue                                              */
/*==============================================================*/
create table tbl_clue
(
   id                   char(32) not null comment 'uuid，全部小写，32长度',
   owner                varchar(255) not null comment '线索所有者可以是当前登录的用户，也可以是其他用户。线索所有者转换为：客户所有者和联系人所有者',
   company              varchar(255) not null comment '公司转换为“客户名称”，客户名称在客户表和联系人表当中都有该字段',
   phone                varchar(255) comment '电话转换到客户表表中',
   website              varchar(255) comment '网站转换到客户表中',
   grade                varchar(255) comment '等级转换到客户表中，已获得,激活的,市场失败,项目取消,关闭',
   industry             varchar(255) comment '行业转换到客户表中，应用服务提供商,数据/电信/OEM,企业资源管理,政府/军队,大企业,管理软件提供商,MSP(管理服务提供商),网络设备(企业),非管理软件提供商,光网络,服务提供商,中小企业,存储设备,存储服务提供商,系统集成,无线企业',
   annualIncome         bigint(11) comment '年收入转换到客户表中',
   empNums              int(7) comment '员工数转换到客户表中',
   country              varchar(255) comment '地址转换到客户表，同时也转换到联系人表',
   province             varchar(255) comment '地址转换到客户表，同时也转换到联系人表',
   city                 varchar(255) comment '地址转换到客户表，同时也转换到联系人表',
   street               varchar(255) comment '地址转换到客户表，同时也转换到联系人表',
   zipcode              varchar(255) comment '地址转换到客户表，同时也转换到联系人表',
   description          varchar(255) comment '描述转换到客户表，同时也转换到联系人表',
   fullName             varchar(255) not null comment '转换到联系人表',
   appellation          varchar(255) comment '称呼转换到联系人表，''先生''''夫人''''女士''''博士''''教授''',
   source               varchar(255) comment '来源转换到联系人表，广告,推销电话,员工介绍,外部介绍,在线商场,合作伙伴,公开媒介,销售邮件,合作伙伴研讨会,内部研讨会,交易会,web下载,web调研,聊天',
   email                varchar(255) comment '邮箱转换到联系人表',
   mphone               varchar(255) comment '手机转换到联系人表',
   job                  varchar(255) comment '职位转换到联系人表',
   state                varchar(255) comment '状态转换不转换到任何表，试图联系,将来联系,已联系,虚假线索,丢失线索,未联系,需要条件',
   createBy             varchar(255) comment '创建线索时当前登录的用户，不转换',
   createTime           char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editBy               varchar(255) comment '修改线索时当前登录的用户，不转换',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   contactSummary       varchar(255),
   nextContactTime      char(19),
   primary key (id)
);

alter table tbl_clue comment '线索表，线索可以转换，一个线索可以被转换为：一个客户、一个联系人、一个交易';

/*==============================================================*/
/* Table: tbl_clue_activity_relation                            */
/*==============================================================*/
create table tbl_clue_activity_relation
(
   id                   char(32) not null comment 'uuid，32个长度，全部小写',
   clueId               char(32) comment '线索id是外键，引用线索表的主键',
   activityId           char(32) comment '市场活动id是外键，引用市场活动表的主键',
   primary key (id)
);

alter table tbl_clue_activity_relation comment '线索与市场活动的关系表，一个线索可以对应多个市场活动，一个市场活动也可以对应多个线索。所以线索和市场活动是一个多对多的关';

/*==============================================================*/
/* Table: tbl_clue_remark                                       */
/*==============================================================*/
create table tbl_clue_remark
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   notePerson           varchar(255) comment '第一次创建此备注信息的人被称为备注人',
   noteContent          varchar(255) comment '备注的详细内容',
   noteTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editPerson           varchar(255) comment '当修改标记为0时，修改人为null，当修改标记为1时，修改人有值，并且修改人是可变化的。只记录最后一次修改人。',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editFlag             int(1) comment '0表示该备注没有被修改过，1表示该备注被修改过',
   clueId               char(32) comment '一个线索下有多条备注信息，线索id为外键引用线索表的主键id',
   primary key (id)
);

alter table tbl_clue_remark comment '线索备注表';

/*==============================================================*/
/* Table: tbl_contacts                                          */
/*==============================================================*/
create table tbl_contacts
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   owner                varchar(255) not null comment '可能是从线索的所有者转换过来的，也可以不是从线索转换过来的。可以是当前保存联系人的登录用户，也可以是登录用户指定的其它用户为所有者。',
   source               varchar(255) comment '可能是线索中转换过来的，广告,推销电话,员工介绍,外部介绍,在线商场,合作伙伴,公开媒介,销售邮件,合作伙伴研讨会,内部研讨会,交易会,web下载,web调研,聊天',
   appellation          varchar(255) comment '可能是线索中转换过来的',
   fullName             varchar(255) not null comment '可能是线索中转换过来的',
   email                varchar(255) comment '可能是线索中转换过来的',
   job                  varchar(255) comment '可能是线索中转换过来的',
   mphone               varchar(255) comment '可能是线索中转换过来的',
   description          varchar(255) comment '可能是线索中转换过来的',
   country              varchar(255) comment '地址从线索中转换过来的，或者是新填写的',
   province             varchar(255) comment '地址从线索中转换过来的，或者是新填写的',
   city                 varchar(255) comment '地址从线索中转换过来的，或者是新填写的',
   street               varchar(255) comment '地址从线索中转换过来的，或者是新填写的',
   zipcode              varchar(255) comment '地址从线索中转换过来的，或者是新填写的',
   birth                char(10) comment '联系人的生日，新填写的。',
   customerId           char(32) comment '一个客户下有多个联系人，客户id是外键，引用客户表主键id',
   createBy             varchar(255) comment '保存联系人的时候，当前登录的用户为创建者',
   createTime           char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editBy               varchar(255) comment '修改联系人的时候，当前登录的用户为修改者。',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   contactSummary       varchar(255),
   nextContactTime      char(19),
   primary key (id)
);

alter table tbl_contacts comment '联系人表';

/*==============================================================*/
/* Table: tbl_contacts_activity_relation                        */
/*==============================================================*/
create table tbl_contacts_activity_relation
(
   id                   char(32) not null comment 'uuid，32个长度，全部小写',
   contactsId           char(32) comment '外键，引用联系人表中主键id',
   activityId           char(32) comment '外键，引用市场活动表中主键id',
   primary key (id)
);

alter table tbl_contacts_activity_relation comment '一个联系人对应多个市场活动，一个市场活动对应多个联系人，联系人和市场活动之间是多对多关系';

/*==============================================================*/
/* Table: tbl_contacts_remark                                   */
/*==============================================================*/
create table tbl_contacts_remark
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   notePerson           varchar(255) comment '第一次创建此备注信息的人被称为备注人',
   noteContent          varchar(255) comment '备注的详细内容',
   noteTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editPerson           varchar(255) comment '当修改标记为0时，修改人为null，当修改标记为1时，修改人有值，并且修改人是可变化的。只记录最后一次修改人。',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editFlag             int(1) comment '0表示该备注没有被修改过，1表示该备注被修改过',
   contactsId           char(32) comment '联系人id外键，引用联系人表主键id',
   primary key (id)
);

alter table tbl_contacts_remark comment '联系人备注表（可能是从线索备注转换过来的）';

/*==============================================================*/
/* Table: tbl_customer                                          */
/*==============================================================*/
create table tbl_customer
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   owner                varchar(255) not null comment '保存客户信息时当前登录的用户为该客户信息的所有者，当然也可以是指定的所有者，该所有者也可能是从线索所有者转换过来的。',
   name                 varchar(255) not null comment '公司名称作为客户名称，线索中的公司转换成客户名称',
   grade                varchar(255) comment '线索中的等级转换过来的，已获得,激活的,市场失败,项目取消,关闭',
   phone                varchar(255) comment '线索中的电话转换为客户的电话，一般指公司座机',
   website              varchar(255) comment '公司官网，线索的网站转换过来的',
   annualIncome         bigint(11) comment '线索中的年收入转换过来的，公司年收入',
   empNums              int(7) comment '线索中员工数转换过来的，公司员工数量',
   industry             varchar(255) comment '线索中的行业转换过来的，应用服务提供商,数据/电信/OEM,企业资源管理,政府,军队,大企业,管理软件提供商,MSP(管理服务提供商),网络设备(企业),非管理软件,供商,光网络,服务提供商,中小企业,存储设备,存储服务提供商,系统集成,无线企业',
   description          varchar(255) comment '可能是线索中的描述转换过来的',
   country              varchar(255) comment '地址从线索中转换过来，当然，也可能不是线索转换的',
   province             varchar(255) comment '地址从线索中转换过来，当然，也可能不是线索转换的',
   city                 varchar(255) comment '地址从线索中转换过来，当然，也可能不是线索转换的',
   street               varchar(255) comment '地址从线索中转换过来，当然，也可能不是线索转换的',
   zipcode              varchar(255) comment '地址从线索中转换过来，当然，也可能不是线索转换的',
   createBy             varchar(255) comment '保存客户信息时当前登录的用户',
   createTime           char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editBy               varchar(255) comment '修改客户信息时当前登录的用户',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   primary key (id)
);

alter table tbl_customer comment '客户表';

/*==============================================================*/
/* Table: tbl_customer_remark                                   */
/*==============================================================*/
create table tbl_customer_remark
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   notePerson           varchar(255) comment '第一次创建此备注信息的人被称为备注人',
   noteContent          varchar(255) comment '备注的详细内容',
   noteTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editPerson           varchar(255) comment '当修改标记为0时，修改人为null，当修改标记为1时，修改人有值，并且修改人是可变化的。只记录最后一次修改人。',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editFlag             int(1) comment '0表示该备注没有被修改过，1表示该备注被修改过',
   customerId           char(32) comment '一个客户下有多条备注信息，客户id为外键引用客户表的主键id',
   primary key (id)
);

alter table tbl_customer_remark comment '客户备注表（可能是从线索备注转换过来的）';

/*==============================================================*/
/* Table: tbl_dept                                              */
/*==============================================================*/
create table tbl_dept
(
   id                   char(32) not null,
   no                   varchar(4) not null comment '四位数字，具有唯一性，不能为空，可以0开始',
   name                 varchar(255),
   manager              varchar(255),
   description          varchar(255),
   phone                varchar(255),
   primary key (id)
);

alter table tbl_dept comment '部门表';

/*==============================================================*/
/* Table: tbl_dictionary_type                                   */
/*==============================================================*/
create table tbl_dictionary_type
(
   code                 varchar(255) not null comment '例如：sex、orgType',
   name                 varchar(255) comment '例如：性别、机构类型',
   description          varchar(255) comment '对该字典类型的一个描述',
   primary key (code)
);

alter table tbl_dictionary_type comment '字典类型表';

/*==============================================================*/
/* Table: tbl_dictionary_value                                  */
/*==============================================================*/
create table tbl_dictionary_value
(
   id                   char(32) not null,
   value                varchar(255) not null,
   text                 varchar(255),
   orderNo              bigint(11),
   typeCode             varchar(255) not null,
   primary key (id)
);

alter table tbl_dictionary_value comment '字典值表';

/*==============================================================*/
/* Table: tbl_marketing_activities                              */
/*==============================================================*/
create table tbl_marketing_activities
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   owner                varchar(255) not null comment '市场活动所有者可以是当前登录的用户，不过当前登录的用户也可以指派其它用户为该市场活动的所有者',
   type                 varchar(255) comment '会议,web研讨,交易会,公开媒介,合作伙伴,推举程序,广告,条幅广告,直接邮件,邮箱,电子市场,其它',
   name                 varchar(255) not null,
   state                varchar(255) comment '计划中,激活的,休眠,完成',
   startDate            char(10) comment '日期格式：2010-10-09',
   endDate              char(10) comment '日期格式：2010-10-09',
   budgetCost           bigint(11),
   actualCost           bigint(11),
   description          varchar(255) comment '对当前市场活动的描述',
   createBy             varchar(255) comment '保存市场活动的时候，将当前登录的用户作为市场活动创建者',
   createTime           char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editBy               varchar(255) comment '修改市场活动的时候，将当前登录的用户作为该市场活动的修改者',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   primary key (id)
);

alter table tbl_marketing_activities comment '市场活动表';

/*==============================================================*/
/* Table: tbl_marketing_activities_remark                       */
/*==============================================================*/
create table tbl_marketing_activities_remark
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   notePerson           varchar(255) comment '第一次创建此备注信息的人被称为备注人',
   noteContent          varchar(255) comment '备注的详细内容',
   noteTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editPerson           varchar(255) comment '当修改标记为0时，修改人为null，当修改标记为1时，修改人有值，并且修改人是可变化的。只记录最后一次修改人。',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editFlag             int(1) comment '0表示该备注没有被修改过，1表示该备注被修改过',
   marketingActivitiesId char(32) comment '一个市场活动下有多条备注信息，市场活动id为外键引用市场活动表的主键id',
   primary key (id)
);

alter table tbl_marketing_activities_remark comment '市场活动备注表';

/*==============================================================*/
/* Table: tbl_permission                                        */
/*==============================================================*/
create table tbl_permission
(
   id                   varchar(32) not null,
   pid                  varchar(32),
   name                 varchar(255),
   moduleUrl            varchar(255),
   operUrl              varchar(255),
   orderNo              bigint(10),
   primary key (id)
);

/*==============================================================*/
/* Table: tbl_role                                              */
/*==============================================================*/
create table tbl_role
(
   id                   varchar(32) not null,
   code                 varchar(255),
   name                 varchar(255),
   description          varchar(255),
   primary key (id)
);

/*==============================================================*/
/* Table: tbl_role_permission_relation                          */
/*==============================================================*/
create table tbl_role_permission_relation
(
   id                   varchar(32) not null,
   roleId               varchar(32),
   permissionId         varchar(32),
   primary key (id)
);

/*==============================================================*/
/* Table: tbl_task                                              */
/*==============================================================*/
create table tbl_task
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   owner                varchar(255) not null comment '当前登录用户，或者可指派',
   subject              varchar(255) not null comment '任务主题，例如：拜访客户',
   dueDate              char(10) comment '格式：2010-10-10',
   contactsId           char(32) comment '这次任务的主要联系人，一个联系人对应多个任务。联系人id是外键，引用联系人表的主键id',
   state                varchar(255) comment '未启动、推迟、进行中、完成、等待某人',
   priority             varchar(255) comment '高、最高、低、最低、常规',
   description          varchar(255) comment '任务描述信息',
   remindFlag           int(1) comment '0表示不添加提醒功能，1表示添加提醒功能，默认是不添加提醒功能',
   startTime            char(19) comment '格式：2010-10-10 10:20:30',
   repeatType           varchar(255) comment '每天、每周、每月、每年',
   adviceType           varchar(255) comment '邮件、弹窗',
   createBy             varchar(255) comment '保存任务时，登录的用户',
   createTime           char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editBy               varchar(255) comment '修改任务时，登录的用户',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   primary key (id)
);

alter table tbl_task comment '任务表';

/*==============================================================*/
/* Table: tbl_task_remark                                       */
/*==============================================================*/
create table tbl_task_remark
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   notePerson           varchar(255) comment '第一次创建此备注信息的人被称为备注人',
   noteContent          varchar(255) comment '备注的详细内容',
   noteTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editPerson           varchar(255) comment '当修改标记为0时，修改人为null，当修改标记为1时，修改人有值，并且修改人是可变化的。只记录最后一次修改人。',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editFlag             int(1) comment '0表示该备注没有被修改过，1表示该备注被修改过',
   taskId               char(32) comment '一个任务对应多个备注，任务id是外键，引用任务表的主键id',
   primary key (id)
);

alter table tbl_task_remark comment '任务备注表';

/*==============================================================*/
/* Table: tbl_transaction                                       */
/*==============================================================*/
create table tbl_transaction
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   owner                varchar(255) not null comment '交易所有者可能是从线索所有者转换过来的，也可能不是',
   amountOfMoney        bigint(11) comment '交易金额',
   name                 varchar(255) not null comment '名称规则：客户名称+xxx',
   expectedClosingDate  char(10) not null comment '成交日期10个长度，年月日即可2010-10-10',
   customerId           char(32) not null comment '一个客户对应多个交易，客户id是外键引用客户表的主键id',
   stage                varchar(255) not null comment '阶段是一个非常重要的字段，形象显示了交易的过程。资质审查,需求分析,价值建议, 确定决策者,提案/报价,谈判/复审,成交,丢失的线索,因竞争丢失关闭',
   type                 varchar(255) comment '已有业务、新业务',
   source               varchar(255) comment '来源可能是线索表中的来源转换过来的，也可能不是。广告,推销电话,员工介绍,外部介绍,在线商场,合作伙伴,公开媒介,销售邮件,合作伙伴研讨会,内部研讨会,交易会,web下载,web调研,聊天',
   activityId           char(32) comment '一个市场活动对应多个交易，市场活动id是外键。引用市场活动表的主键id',
   contactsId           char(32) comment '一个联系人对应多个交易，联系人id是外键，引用联系人表主键id',
   description          varchar(255) comment '对交易的描述，该描述可能来自线索转换',
   createBy             varchar(255) comment '保存交易的时候，当前登录的用户',
   createTime           char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editBy               varchar(255) comment '修改交易的时候，当前登录的用户',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   contactSummary       varchar(255),
   nextContactTime      char(19),
   primary key (id)
);

alter table tbl_transaction comment '交易表';

/*==============================================================*/
/* Table: tbl_transaction_history                               */
/*==============================================================*/
create table tbl_transaction_history
(
   id                   char(32) not null,
   stage                varchar(255),
   amountOfMoney        bigint(11),
   expectedClosingDate  char(10),
   editTime             char(19),
   editBy               varchar(255),
   transactionId        char(32),
   primary key (id)
);

/*==============================================================*/
/* Table: tbl_transaction_remark                                */
/*==============================================================*/
create table tbl_transaction_remark
(
   id                   char(32) not null comment '主键采用uuid自动生成策略，要求32位长度，全部小写',
   notePerson           varchar(255) comment '第一次创建此备注信息的人被称为备注人',
   noteContent          varchar(255) comment '备注的详细内容',
   noteTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editPerson           varchar(255) comment '当修改标记为0时，修改人为null，当修改标记为1时，修改人有值，并且修改人是变化的。只记录最后一次修改人。',
   editTime             char(19) comment '19个长度，精确到时分秒，例如：2010-10-10 10:10:10',
   editFlag             int(1) comment '0表示该备注没有被修改过，1表示该备注被修改过',
   transactionId        char(32) comment '一个交易对应多条备注信息，交易id是外键，引用交易表的主键id',
   primary key (id)
);

alter table tbl_transaction_remark comment '交易备注表（可能是从线索备注转换过来的）';

/*==============================================================*/
/* Table: tbl_user                                              */
/*==============================================================*/
create table tbl_user
(
   id                   varchar(32) not null,
   deptId               varchar(32),
   loginAct             varchar(255),
   name                 varchar(255),
   loginPwd             varchar(255),
   email                varchar(255),
   expireTime           char(19) comment '失效时间为空表示永不失效',
   lockStatus           char(1) comment '0表示锁定1表示启用',
   allowIps             varchar(255) comment '为空时表示不限制IP，多个IP地址之间使用半角逗号隔开',
   createBy             varchar(255),
   createTime           char(19),
   editBy               varchar(255),
   editTime             char(19),
   primary key (id)
);

/*==============================================================*/
/* Table: tbl_user_role_relation                                */
/*==============================================================*/
create table tbl_user_role_relation
(
   id                   varchar(32) not null,
   userId               varchar(32),
   roleId               varchar(32),
   primary key (id)
);

alter table tbl_clue_activity_relation add constraint FK_Reference_2 foreign key (activityId)
      references tbl_marketing_activities (id) on delete restrict on update restrict;

alter table tbl_clue_activity_relation add constraint FK_Reference_3 foreign key (clueId)
      references tbl_clue (id) on delete restrict on update restrict;

alter table tbl_clue_remark add constraint FK_Reference_4 foreign key (clueId)
      references tbl_clue (id) on delete restrict on update restrict;

alter table tbl_contacts add constraint FK_Reference_5 foreign key (customerId)
      references tbl_customer (id) on delete restrict on update restrict;

alter table tbl_contacts_activity_relation add constraint FK_Reference_14 foreign key (contactsId)
      references tbl_contacts (id) on delete restrict on update restrict;

alter table tbl_contacts_activity_relation add constraint FK_Reference_15 foreign key (activityId)
      references tbl_marketing_activities (id) on delete restrict on update restrict;

alter table tbl_contacts_remark add constraint FK_Reference_11 foreign key (contactsId)
      references tbl_contacts (id) on delete restrict on update restrict;

alter table tbl_customer_remark add constraint FK_Reference_10 foreign key (customerId)
      references tbl_customer (id) on delete restrict on update restrict;

alter table tbl_dictionary_value add constraint FK_Reference_16 foreign key (typeCode)
      references tbl_dictionary_type (code) on delete restrict on update restrict;

alter table tbl_marketing_activities_remark add constraint FK_Reference_1 foreign key (marketingActivitiesId)
      references tbl_marketing_activities (id) on delete restrict on update restrict;

alter table tbl_permission add constraint FK_Reference_18 foreign key (pid)
      references tbl_permission (id) on delete restrict on update restrict;

alter table tbl_role_permission_relation add constraint FK_Reference_19 foreign key (permissionId)
      references tbl_permission (id) on delete restrict on update restrict;

alter table tbl_role_permission_relation add constraint FK_Reference_20 foreign key (roleId)
      references tbl_role (id) on delete restrict on update restrict;

alter table tbl_task add constraint FK_Reference_9 foreign key (contactsId)
      references tbl_contacts (id) on delete restrict on update restrict;

alter table tbl_task_remark add constraint FK_Reference_13 foreign key (taskId)
      references tbl_task (id) on delete restrict on update restrict;

alter table tbl_transaction add constraint FK_Reference_6 foreign key (customerId)
      references tbl_customer (id) on delete restrict on update restrict;

alter table tbl_transaction add constraint FK_Reference_7 foreign key (contactsId)
      references tbl_contacts (id) on delete restrict on update restrict;

alter table tbl_transaction add constraint FK_Reference_8 foreign key (activityId)
      references tbl_marketing_activities (id) on delete restrict on update restrict;

alter table tbl_transaction_history add constraint FK_Reference_24 foreign key (transactionId)
      references tbl_transaction (id) on delete restrict on update restrict;

alter table tbl_transaction_remark add constraint FK_Reference_12 foreign key (transactionId)
      references tbl_transaction (id) on delete restrict on update restrict;

alter table tbl_user add constraint FK_Reference_23 foreign key (deptId)
      references tbl_dept (id) on delete restrict on update restrict;

alter table tbl_user_role_relation add constraint FK_Reference_21 foreign key (roleId)
      references tbl_role (id) on delete restrict on update restrict;

alter table tbl_user_role_relation add constraint FK_Reference_22 foreign key (userId)
      references tbl_user (id) on delete restrict on update restrict;

```

# 3. 开发环境

* 开发工具：Intellij IDEA 2022.1.1、JDK 版本：JDK1.8、数据库：mysql-5.7.27、字符集：UTF-8
* maven 导入依赖:

```java
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
<!--    MySQL 数据库驱动-->
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.46</version>
    </dependency>
<!--    Druid 连接池-->
    <dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>druid</artifactId>
      <version>1.1.1</version>
    </dependency>
<!--    Mybatis 依赖-->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.5.8</version>
    </dependency>
<!--    Spring 相关依赖-->
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>4.3.9.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-aop</artifactId>
      <version>4.3.9.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-core</artifactId>
      <version>4.3.9.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-beans</artifactId>
      <version>4.3.9.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-jdbc</artifactId>
      <version>4.3.9.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-tx</artifactId>
      <version>4.3.9.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-web</artifactId>
      <version>4.3.9.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-webmvc</artifactId>
      <version>4.3.9.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-oxm</artifactId>
      <version>4.3.9.RELEASE</version>
    </dependency>
<!--    Spring AOP 依赖-->
    <dependency>
      <groupId>org.aspectj</groupId>
      <artifactId>aspectjweaver</artifactId>
      <version>1.8.9</version>
    </dependency>
<!--    Mybatis 与 Spring 整合依赖-->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis-spring</artifactId>
      <version>1.3.0</version>
    </dependency>
<!--    添加项目对 jsp 的支持-->
    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>servlet-api</artifactId>
      <version>2.5</version>
    </dependency>
    <dependency>
      <groupId>javax.servlet.jsp.jstl</groupId>
      <artifactId>jstl-api</artifactId>
      <version>1.2</version>
    </dependency>
    <dependency>
      <groupId>org.apache.taglibs</groupId>
      <artifactId>taglibs-standard-spec</artifactId>
      <version>1.2.1</version>
    </dependency>
    <dependency>
      <groupId>org.apache.taglibs</groupId>
      <artifactId>taglibs-standard-impl</artifactId>
      <version>1.2.1</version>
    </dependency>
<!--    Jackson 插件依赖-->
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-core</artifactId>
      <version>2.7.3</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.7.3</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-annotations</artifactId>
      <version>2.7.3</version>
    </dependency>
<!--    poi 依赖-->
    <dependency>
      <groupId>org.apache.poi</groupId>
      <artifactId>poi</artifactId>
      <version>3.15</version>
    </dependency>
<!--    文件上传下载-->
    <dependency>
      <groupId>commons-fileupload</groupId>
      <artifactId>commons-fileupload</artifactId>
      <version>1.3.1</version>
    </dependency>
<!--    Log4j-->
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-api</artifactId>
      <version>2.3</version>
    </dependency>
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-core</artifactId>
      <version>2.3</version>
    </dependency>
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-jcl</artifactId>
      <version>2.3</version>
    </dependency>
  </dependencies>

```

* 添加配置文件


# 4. 业务功能开发

<br />![image](assets/image-20220802192829-odg968p.png "java-package")![image](assets/image-20220802192949-84oijlj.png "settings-package")![image](assets/image-20220802193146-id9no8a.png "workbench-package")![image](assets/image-20220802192911-gynjrj5.png "commons-package")

resources 文件夹一览![image](assets/image-20220727145010-4xuq7xp.png "resources-package")

webapp 文件夹一览![image](assets/image-20220727145130-gmo0gz3.png "webapp-package")

## 4.1.1 首页功能

用户访问项目首页，首先进入登录页面。

## 4.1.2 用户登录

用户在登录页面,输入用户名和密码,点击"登录"按钮或者回车,完成用户登录的功能.

    *用户名和密码不能为空

    *用户名或者密码错误,用户已过期,用户状态被锁定,ip 受限 都不能登录成功

    *登录成功之后,所有业务页面显示当前用户的名称

    *实现 10 天记住密码

    *登录成功之后,跳转到业务主页面

    *登录失败,页面不跳转,提示信息

## 4.1.3 安全退出

用户在任意的业务页面,点击"退出"按钮,弹出确认退出的模态窗口;用户在确认退出的模态窗口,点击"确定"按钮,完成安全退出的功能.

    *安全退出,清空 cookie,销毁 session

    *退出完成之后,跳转到首页

## 4.1.4 登录验证

登录验证.

    用户访问任何业务资源,都需要进行登录验证.

    *只有登录成功的用户才能访问业务资源

    *没有登录成功的用户访问业务资源,跳转到登录页面

## 4.1.5 数据字典类型维护

用户打开”系统设置”-->”数据字典表”，显示字典类型列表，完成数据字典类型的增删改查。

1、  查询数据字典类型

用户在业务主页面，点击”系统设置”，跳转到系统设置主页面；

用户在系统设置主页面，点击”数据字典表”，跳转到数据字典维护主页面；

在数据字典维护主页面，默认在工作区中显示数据字典类型列表。

2、创建数据字典类型：

   用户在数据字典类型主页面，点击”创建”按钮，跳转到创建页面；

用户在创建页面，填写表单，点击”保存”按钮，完成创建数据字典类型的功能.

*编码不能为空、不能重复

*创建成功之后，跳转数据字典类型主页面；

*创建失败，提示信息，页面不跳转。

3、修改数据字典类型

  用户在数据字典类型主页面，选择要修改的记录，点击”编辑”按钮，跳转到修改记录的页面；

  用户在修改记录的页面，填写表单，点击”更新”按钮，完成修改数据字典类型的功能。

   *每次必须修改一条记录,而且只能修改一条记录

   *数据字典类型的编码不能够修改

*修改成功之后，跳转到数据字典类型的主页面

*修改失败，提示信息，页面不跳转

4、删除数据字典类型

  用户在数据字典类型主页面，选择要删除的记录，点击”删除”按钮，弹出确认删除对话框，用户点击”确定”，完成删除数据字典类型的功能。

*每次至少删除一条

*可以批量删除

*删除成功之后，刷新数据字典类型列表

*删除失败，提示信息，列表不刷新

## 4.1.6 数据字典值维护

用户打开”系统设置”-->”数据字典表”-->”字典值”，显示字典值列表，完成数据字典值的增删改查。

1、  查询数据字典值

用户在数据字典主页面，点击”字典值”菜单，在工作区中显示数据字典值主页面；

在数据字典值主页面显示所有数据字典值的记录。

2、  创建数据字典值

用户在数据字典值主页面，点击”创建”按钮，跳转到创建数据字典值的页面；

用户在创建数据字典值的页面填写表单，点击”保存”按钮，完成创建数据字典值的功能。

*字典类型编码来自于数据库，并且不能为空

*字典值也不能为空

*创建成功之后，跳转到数据字典值主页面

*创建失败，提示信息，页面不跳转

3、修改数据字典值

用户在数据字典值主页面，选择要修改的记录，点击”编辑”按钮，跳转到修改页面；

用户在修改数据字典值页面，填写表单，点击”更新”按钮，完成修改数据字典值的功能。

*每次只能修改一条记录，而且必须修改一条

*”所属字典类型”字段不能修改

*”字典值”不能为空

*修改成功之后，跳转到数据字典值主页面

*修改失败，提示信息，页面不跳转

4、删除数据字典值

用户在数据字典值主页面，选择要删除的记录，点击”删除”按钮，弹出确认删除对话框,用户点击”确定”，完成删除数据字典值的功能。

*每次至少删除一条记录

*删除成功之后，跳转到数据字典值主页面

*删除失败，提示信息，页面不跳转

## 4.1.7 创建市场活动

创建市场活动.

    用户在市场活动主页面,点击"创建"按钮,弹出创建市场活动的模态窗口;

    用户在创建市场活动的模态窗口填写表单,点击"保存"按钮,完成创建市场活动的功能.

    *所有者是动态的(//在现实市场活动主页面时，就从数据库中查询出所有用户并且显示在创建的模态窗口中)

    *所有者和名称不能为空

    *如果开始日期和结束日期都不为空,则结束日期不能比开始日期小

    *成本只能为非负整数

    *创建成功之后,关闭模态窗口,刷新市场活动列，显示第一页数据，保持每页显示条数不变

    *创建失败,提示信息创建失败,模态窗口不关闭,市场活动列表也不刷新

## 4.1.8 查询市场活动

当市场活动主页面加载完成之后,显示所有数据的第一页;

用户在市场活动主页面填写查询条件,点击"查询"按钮,显示所有符合条件的数据的第一页，保持每页显示条数不变

实现翻页功能.

    *在市场活动主页面,显示市场活动列表和记录的总条数

    *默认每页显示条数:10

## 4.1.9 修改市场活动

    用户在市场活动主页面,选择要修改的市场活动,点击"修改"按钮,弹出修改市场活动的模态窗口;

    用户在修改市场活动的模态窗口填写表单,点击"更新"按钮,完成修改市场活动的功能.

    *每次能且只能修改一条市场活动

    *所有者
动态的

    *表单验证(同创建)

    *修改成功之后,关闭模态窗口,刷新市场活动列表,保持页号和每页显示条数都不变

    *修改失败,提示信息,模态窗口不关闭,列表也不刷新

## 4.1.10 删除市场活动

用户在市场活动主页面,选择要删除的市场活动,点击"删除"按钮,弹出确认窗口;

    用户点击"确定"按钮,完成删除市场活动的功能.

    *每次至少删除一条市场活动

*可以批量删除市场活动

    *删除成功之后,刷新市场活动列表,显示第一页数据,保持每页显示条数不变

    *删除失败,提示信息,列表不刷新

## 4.1.11 批量导出市场活动

    用户在市场活动主页面,点击"批量导出"按钮,把所有市场活动生成一个 excel 文件,弹出文件下载的对话框;

    用户选择要保存的目录,完成导出市场活动的功能.

    *导出成功之后,页面不刷新

## 4.1.12 选择导出市场活动

    用户在市场活动主页面,选择要导出的市场活动,点击"选择导出"按钮,把所有选择的数据生成一个 excel 文件,弹出文件下载的对话框;

    用户选择要保存的目录,完成选择导出市场活动的功能.

    *每次至少选择导出一条记录

    *导出成功之后,页面不刷新

## 4.1.13 导入市场活动

用户在市场活动主页面,点击"导入"按钮,弹出导入市场活动的模态窗口;

    用户在导入市场活动的模态窗口选择要上传的文件,点击"导入"按钮,完成导入市场活动的功能.

    *只支持.xls

    *文件大小不超过 5MB

    *导入成功之后,提示成功导入记录条数,关闭模态窗口,刷新市场活动列表,显示第一页数据,保持每页显示条数不变

    *导入失败,提示信息,模态窗口不关闭,列表也不刷新

## 4.1.14 查看市场活动明细

用户在市场活动主页面,点击市场活动名称超级链接,跳转到明细页面,完成查看市场活动明细的功能.

    *在市场活动明细页面,展示:

    -市场活动的基本信息

    -该市场活动下所有的备注信息

## 4.1.15 添加市场活动备注

用户在市场活动明细页面,输入备注内容,点击"保存"按钮,完成添加市场活动备注的功能.

    *备注内容不能为空

    *添加成功之后,清空输入框,刷新备注列表

    *添加失败,提示信息,输入框不清空,列表也不刷新

## 4.1.16 删除市场活动备注

用户在市场活动明细页面,点击"删除"市场活动备注的图标,完成删除市场活动备注的功能.

    *删除成功之后,刷新备注列表

    *删除失败,提示信息,备注列表不刷新

## 4.1.17 修改市场活动备注

用户在市场活动明细页面,点击"修改"市场活动备注的图标,弹出修改市场活动备注的模态窗口;

    用户在修改市场活动备注的模态窗口,填写表单,点击"更新"按钮,完成修改市场活动备注的功能.

    *备注内容不能为空

    *修改成功之后,关闭模态窗口,刷新备注列表

    *修改失败,提示信息,模态窗口不关闭,列表也不刷新

## 4.1.18 创建线索

用户在线索主页面，点击”创建”按钮，弹出创建线索的模态窗口；

用户在创建线索的模态窗口，填写表单，点击”保存”按钮，完成创建线索的功能。

*所有者、称呼、线索状态、线索来源
是动态

*表单验证

*创建成功之后，关闭模态窗口，刷新线索列表，显示第一页数据，保持每页显示条数不变

*创建失败，提示信息，模态窗口不关闭，列表也不刷新。

## 4.1.19 查询线索

当线索主页面加载完成之后,显示所有数据的第一页;

用户在线索主页面填写查询条件,点击"查询"按钮,显示所有符合条件的数据的第一页;

实现翻页功能.

    *在线索主页面,显示市场活动列表和记录的总条数

    *默认每页显示条数:10

## 4.1.20 查看线索明细

用户在线索主页面,点击线索名称(fullname 和 appellition)超级链接,跳转到线索明细页面,完成查看线索明细的功能.

    *在线索明细页面,展示:

    -线索的基本信息

    -线索的备注信息

    -跟该线索相关联的市场活动信息

## 4.1.21 线索关联市场活动

用户在线索明细页面,点击"关联市场活动"按钮,弹出线索关联市场活动的模态窗口;

用户在线索关联市场活动的模态窗口,输入搜索条件,每次键盘弹起,根据名称模糊查询市场活动,把所有符合条件的市场活动显示到列表中;用户选择要关联的市场活动,点击"关联"按钮,完成线索关联市场活动的功能.

    *每次至少关联一个市场活动

    *同一个市场活动只能跟同一个线索关联一次

    *关联成功之后,关闭模态窗口,刷新已经关联过的市场活动列表

    *关联失败,提示信息,模态窗口不关闭,已经关联过的市场活动列表也不刷新

## 4.1.22 解除线索关联市场活动

用户在线索明细页面,点击某一个"解除关联"按钮,弹出确认解除的窗口;

用户点击"确定"按钮,完成解除线索关联市场活动的功能.

    *解除成功之后,刷新已经关联的市场活动列表

    *解除失败,提示信息,列表也不刷新

## 4.1.23 线索转换

用户在线索明细页面,点击"转换"按钮,跳转到线索转换页面;

    用户在线索转换页面,如果需要创建创建交易,则填写交易表单数据,点击"转换"按钮,完成线索转换的功能.

    *在线索转换页面,展示:fullName,appellation,company,owner

    *市场活动源是可搜索的

    *数据转换:

    把线索中有关公司的信息转换到客户表中

    把线索中有关个人的信息转换到联系人表中

    把线索的备注信息转换到客户备注表中一份

    把线索的备注信息转换到联系人备注表中一份

    把线索和市场活动的关联关系转换到联系人和市场活动的关联关系表中

    如果需要创建交易,还要往交易表中添加一条记录

    如果需要创建交易,还要把线索的备注信息转换到交易备注表中一份

    删除线索的备注

    删除线索和市场活动的关联关系

    删除线索

    在一同个事务中完成.

    *转换成功之后,跳转到线索主页面

*转换失败,提示信息,页面不跳转

## 4.1.24 创建交易

用户在交易主页面，点击”创建”按钮，跳转到创建交易的页面；

用户在创建交易的页面填写表单，点击”保存”按钮，完成创建交易的功能。

*所有者、阶段、类型、来源
都是动态的

*市场活动源是可搜索的

*联系人也是可搜索的

*可能性是可配置的

*客户名称支持自动补全

*表单验证

*保存成功之后，跳转到交易主页面

*保存失败，提示信息，页面不跳转

## 4.1.25 查看交易明细

用户在交易主页面，点击交易名称超级链接，跳转到交易明细页面，完成查看交易明细的功能。

*显示交易的基本信息

*显示交易的备注信息

*显示交易的历史信息

*显示交易的阶段图标信息

## 4.1.26 修改交易阶段

用户在交易明细页面，点击交易阶段的图标，把交易当前的阶段修改为指定的阶段，完成修改交易阶段的功能。

*已经成交的交易不能修改阶段

*修改成功之后，更新：

--交易的图标信息

--交易的基本信息

--交易的历史信息

 *修改失败，提示信息，页面不更新

## 4.1.27 交易统计图表

用户点击”交易统计图表”菜单，显示交易统计图表页面，以销售漏斗图的形式显示交易表中各个阶段的记录数量，完成查看交易统计图表的功能。

# 5. 登录页面跳转首页具体实现流程

1. 由于设置了 welcome-file，所以运行 Tomcat 自动进入/

   ```java
     <welcome-file-list>
       <welcome-file>/</welcome-file>
     </welcome-file-list>

   ```
2. IndexController 映射/请求，并将其转入 WEB-INF/pages/index.jsp 页面

   ```java
   @Controller
   public class IndexController {
       @RequestMapping("/")
       public String index(){
           return "index";
       }
   }
   ```
3. 该 WEB-INF/pages/index.jsp 页面直接执行 toLogin.do 方法

   ```javascript
   <script type="text/javascript">
      document.location.href = "settings/qx/user/toLogin.do";
   </script>
   ```
4. 映射 toLogin.do 请求，返回登录界面视图

   ```java
       @RequestMapping("/settings/qx/user/toLogin.do")
       public String toLogin(){

           return "settings/qx/user/login";
       }

   ```
5. 登录界面，采用异步请求，发送给 login.do，携带账号、密码、是否记住密码，执行过后的回调函数：code 为 1 即登录成功后跳转，如果未成功则将后台返回的错误信息 message 渲染

   ```javascript
   				$.ajax({
   					url:'settings/qx/user/login.do',
   					data:{
   						loginAct:loginAct,
   						loginPwd:loginPwd,
   						isRemPwd:isRemPwd
   					},
   					type:'post',
   					dataType:'json',
   					success:function(data){
   						if(data.code=="1"){
   							window.location.href="workbench/index.do"
   						}else{
   							$("#msg").text(data.message);
   						}

   					}
   				});

   ```
6. 映射 login.do 请求，并返回一个类对象，存储登录成功与否信息，失败获取提示信息，用于前端提示

   ```java

       @Autowired
       private UserService userService;
       @RequestMapping("/settings/qx/user/login.do")
       public @ResponseBody Object login(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request){
           Map<String,Object> map=new HashMap<>();
           map.put("loginAct" , loginAct ) ;
           map.put("loginPwd" , loginPwd ) ;
           User user = userService.queryUserByActAndPwd(map);
   //        根据查询结果生成响应对象
           ReturnObject returnObject = new ReturnObject();
           if(user==null){
   //           用户名或密码错误 登陆失败
               returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
               returnObject.setMessage("用户名或密码错误");
           }else {
   //            检查是否超时
               String format = DateUtils.formateDateTime(new Date());
               if(format.compareTo(user.getExpireTime())>0){
   //                账号过期 登陆失败
                   returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                   returnObject.setMessage("账号过期");
               }else if("0".equals(user.getLockState())){
   //                状态被锁定 登陆失败
                   returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                   returnObject.setMessage("状态被锁定");
               }else if(!user.getAllowIps().contains(request.getRemoteAddr())){
   //                IP地址不属于 登陆失败
                   returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                   returnObject.setMessage("IP地址不属于");
               }else{
   //                登陆成功
                   returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
               }
           }
           return returnObject;


   ```

   该 Controller 层其中 @Autowird 自动装配了一个 UserService 对象，并调用了该对象的 queryUserByActAndPwd(map)方法，将保存有 loginAct 与 loginPwd 的对象 map 传入，

   Service 层有 UserService 接口与其实现类 UserServiceImpl，接口中定义了

   User queryUserByActAndPwd(Map<String,Object> map)方法，该方法需要返回实体类对象，实现类中自动装配了 UserMapper 对象，并调用其 selectUserByLoginActAndPwd()方法

   ```java
   @Autowired
       private UserMapper userMapper;

       @Override
       public User queryUserByActAndPwd(Map<String, Object> map) {
           return  userMapper.selectUserByLoginActAndPwd(map);
       }
   ```
7. Mapper 层使用 Mybatis 操作数据库

   ```sql
   <select id="selectUserByLoginActAndPwd" parameterType="map" resultMap="BaseResultMap">
       select
           <include refid="Base_Column_List"/>
       from
           tbl_user
       where
           login_act=#{loginAct} and login_pwd=#{loginPwd}
     </select>
   ```

   总结：Controller 层调 Service 层，Service 层调 Mapper 层

# 6. 登录界面优化

## 6.1 实现回车登录

键盘每个案件都有一个 keyCode，回车键的 keyCode 为 13，当检测到 keyCode 为 13 时执行登录按钮的单击事件，此单击事件已经定义过

```js
$(window).keydown(function(event){
   if(event.keyCode==13) {
      $("#loginBtn").click();
   }
});
```

## 6.2 实现记住密码

```java
//                如果需要记住密码 , 则将密码存入cookie
if ("true".equals(isRemPwd)){
                    Cookie cookieReAct = new Cookie("loginAct", user.getLoginAct());
                    cookieReAct.setMaxAge(10*24*60*60);
                    response.addCookie(cookieReAct);
                    Cookie cookieRePwd = new Cookie("loginPwd", user.getLoginPwd());
                    cookieRePwd.setMaxAge(10*24*60*60);
                    response.addCookie(cookieRePwd);
                }else {
                    Cookie cookieReAct = new Cookie("loginAct", "1");
                    cookieReAct.setMaxAge(0);
                    response.addCookie(cookieReAct);
                    Cookie cookieRePwd = new Cookie("loginPwd", "1");
                    cookieRePwd.setMaxAge(0);
                    response.addCookie(cookieRePwd);
                }
```

如果 isRemPwd 被选中，则将账号密码存入 Cookie，前台通过

```java
value="${cookie.loginAct.value}"
```

```java
value="${cookie.loginPwd.value}"
```

获取 cookie 值

# 7. 安全退出功能

安全退出即如果当前用户记住密码，则会取消记住密码并退出

为 index 页面右上角点击安全退出按钮绑定单击事件，单击后跳转到 settings/qx/user/logout.do

在 UserController 添加如下控制器，映射该请求

```java
@RequestMapping("/settings/qx/user/logout.do")
public String logout(HttpServletResponse response,HttpSession session){
    Cookie cookieDelAct = new Cookie("loginAct", "1");
    cookieDelAct.setMaxAge(0);
    response.addCookie(cookieDelAct);
    Cookie cookieDelPwd = new Cookie("loginPwd", "1");
    cookieDelPwd.setMaxAge(0);
    response.addCookie(cookieDelPwd);
    session.invalidate();
    return "redirect:/";
}
```

代码功能为将 Cookie 消除后重定向到首页

# 8. 登录验证功能

## 8.1 逻辑代码

设计一个拦截器将所有未登录用户而想要进入业务界面的重定向到首页

需要实现 HandlerInterceptor 接口，在返回值类型为 Boolean 的 preHandle 方法中写判断逻辑，返回 false 则拦截，true 则不拦截

获取被存入 session 的 user 对象，为空则拦截并重定向到首页

```java
@Override
public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
//        如果用户没有登录 , 则跳转到首页
HttpSession session=httpServletRequest.getSession();
        User user = (User) session.getAttribute(Contacts.SESSION_USER);
        if (user == null) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath());
            return false;
        }
        return true;
    }
```

## 8.2 拦截器相关配置

需要配置匹配拦截路径、排除拦截的路径、拦截器类

```xml
    <mvc:interceptors>
        <mvc:interceptor>
<!--            需要拦截的路径-->
	    <mvc:mapping path="/settings/**"/>
            <mvc:mapping path="/workbench/**"/>
<!--            需要排除拦截的路径-->
            <mvc:exclude-mapping path="/settings/qx/user/toLogin.do"/>
            <mvc:exclude-mapping path="/settings/qx/user/login.do"/>
<!--            拦截器类-->
            <bean class="com.bjpowernode.crm.settings.web.interceptor.LoginInterceptor"/>
        </mvc:interceptor>
    </mvc:interceptors>
```

# 9. 市场活动相关功能

> **模态窗口技术：**
>
> 模态窗口：模拟的窗口,本质上是 `<div>`，通过设置 z-index 大小来实现的;
> 初始时，z-index 初始参数是 <0，所以不显示；
> 需要显示时，z-index 值设置成 >0 即可。
>
> bootstrap 来控制 z-index 的大小。
> 控制模态窗口的显示与隐藏：
> 1)方式一：通过标签的属性 data-toggle="modal" data-target="模态窗口的 id"
> 2)方式二：通过 js 函数控制：
> 选择器(选中 div).modal("show");//显示选中的模态窗口
> 选择器(选中 div).modal("hide");//关闭选中的模态窗口
> 3)方式三：通过标签的属性 data-dismiss=""
> 点击添加了 data-dismiss=""属性的标签，自动关闭该标签所在的模态窗口。
> 模态窗口的意义：
> window.open("url","_blank");
> 模态窗口本质上就是原来页面中的一个 `<div>`，只有一个页面;所有的操作都是在同一个页面中完成。
>

## 9.1 进入市场活动主页面

进入主页面时需要携带所有用户信息，用于操作市场活动时选择的该市场活动所有者

在 UserMapper 接口中定义 Mapper 层查询所有用户的方法，返回一个 User 集合，在 UserMapper.xml 文件中查询所有在职用户

在 UserService 接口中定义 Service 层方法，在 UserServiceImpl 类中实现方法，自动装配一个 UserMapper 对象，调用 UserMapper 的查询方法，返回 User 集合

在 ActivityController 中定义控制器映射/workbench/activity/index.do 请求，自动装配一个 UserService 对象，将 userList 集合保存到 request 对象中，并转到市场活动首页

```java
@Controller
public class ActivityController {
    @Autowired
private UserService userService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        List<User> userList = userService.queryAllUsers();
        request.setAttribute("userList",userList);
        return "workbench/activity/index";
    }
```

## 9.2 创建市场活动

前台需要表单验证、将表单数据通过异步请求传给后台

```javascript
$("#saveCreateActivityBtn").click(function (){
   var owner=$("#create-marketActivityOwner").val();
   var name=$.trim($("#create-marketActivityName").val());
   var startDate=$("#create-startDate").val();
   var endDate=$("#create-endDate").val();
   var cost=$.trim($("#create-cost").val());
   var description=$.trim($("#create-description").val());
   if (owner==""){
      alert("请选择所有者");
      return
   }
   if (name==""){
      alert("请输入市场活动名称");
      return
   }
   if (startDate!=""&&endDate!=""){
      if (startDate<startDate){
         alert("请输入正确的结束日期与开始日期 , 结束日期需大于开始日期")
         return
      }
   }
   var regExp=/^(([1-9]\d*)|0)$/;
   if (!regExp.test(cost)){
      alert("请输入正确的成本");
      return;
   }
   //发送请求
   $.ajax({
      url : "workbench/activity/saveCreateActivity.do",
      data : {
         owner : owner,
         name : name,
         startDate : startDate,
         endDate : endDate,
         cost : cost,
         description : description
      },
      type : 'post',
      dataType : 'json',
      success : function(data){
         if (data.code=="1")
            $("#createActivityModal").modal("hide")
         else
            alert(data.message);
      }
   })
})
```

表单输入域涉及日期，所以使用 bootstrap-datetimepicker 日历插件

需要引入 bootstrap 文件

然后为日历插件设置初始化参数

```javascript
$(".mydate").datetimepicker({
   //设置语言
language : 'zh-CN',
   //设置日期格式
format : 'yyyy-mm-dd',
   //最小选择视图
minView : 'month',
   //初始化显示日期
initialDate : new Date(),
   //选择完日期后 ， 是否自动关闭日历
autoclose : true,
   //是否显示 ‘today’ 按钮 , 单击后会直接选中当日
todayBtn : true,
   //是否显示 ‘清除’ 按钮 , 单击后会清除当前选中日期
clearBtn : true
})
```

后台逻辑

controller 层调用 Service 层 save 方法，Service 层调用 Mapper 层 Insert 方法

并为其生成 id、createTime(创建时间)、cteateBy(创建者的 id)

```java
    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    public @ResponseBody Object saveCreateActivity(Activity activity, HttpSession session){
        User user = (User) session.getAttribute(Contacts.SESSION_USER);
//        为市场活动对象 activity 随机成功一个id , 取随机UUID , 此处做了封装
	activity.setId(UUIDUtils.getUUID());
//        为市场活动对象 activity 设置创建时间 获取当前时间 , 此处调用前面封装的格式化时间
	activity.setCreateTime(DateUtils.formateDateTime(new Date()));
        activity.setCreateBy(user.getId());
        ReturnObject returnObject = new ReturnObject();
        try {
            int saveCreateActivityRes = activityService.saveCreateActivity(activity);
            if (saveCreateActivityRes>0)
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("保存失败 , 请重试!");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("保存失败 , 请重试!");
        }
        return returnObject;
    }
```


## 9.3 查询市场活动功能

前台异步请求

向后台发送参数、在成功的回调函数中渲染后台返回数据

查询数据函数 :

```js
function queryActivityByConditionForPage(pageNo,pageSize){
   var name=$("#query-name").val();
   var owner=$("#query-owner").val();
   var startDate=$("#query-startDate").val();
   var endDate=$("#query-endDate").val();
   // var pageNo=1;
   // var pageSize=10;
   $.ajax({
      url: 'workbench/activity/queryActivityByConditionForPage.do',
      data: {
         name : name,
         owner : owner,
         startDate : startDate,
         endDate : endDate,
         pageNo : pageNo,
         pageSize : pageSize
   },
      type: 'post',
      dataType: 'json',
      success : function (data){
         //渲染获取的数据
	 //数据总条数
	 $("#totalRows").text(data.totalRows);
         //拼接数据
	 var htmlStr="";
         $.each(data.activityList,function (index,obj){
            htmlStr+="<tr class=\"active\">";
            htmlStr+="<td><input type=\"checkbox\" value=\""+obj.id+"\"/></td>";
            htmlStr+="<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='detail.html';\">"+obj.name+"</a></td>";
            htmlStr+="<td>"+obj.owner+"</td>";
            htmlStr+="<td>"+obj.startDate+"</td>";
            htmlStr+="<td>"+obj.endDate+"</td>";
            htmlStr+="</tr>";
         })
         $("#tBody").html(htmlStr);

         //查询数据后 , 取消全选按钮
	 //$("#checkAll").prop("checked",false);

         //计算总页数
	 var totalPages=1;
         (data.totalRows%pageSize==0)?(totalPages=data.totalRows/pageSize):totalPages=parseInt(totalPages=data.totalRows/pageSize)+1

	 //调用分页插件
	 $("#demo_pag1").bs_pagination({
            //当前页号
	    currentPage:pageNo,
            //每页显示数据条数
   	    rowsPerPage:pageSize,
            //数据总数
	    totalRows:data.totalRows,
            //总页数
	    totalPages:totalPages,
            //最多显示的卡片数
	    visiblePageLinks: 5,
            //是否显示"跳转到"部分 , 默认为true showGoToPage:
            //是否显示"每页显示条数"部分 , 默认为true showRowsPerPage:
            //是否显示记录的信息 , 默认true showRowsInfo:,

            //当数据改变时 , 触发此函数
	    onChangePage : function (event,pageObj){
               queryActivityByConditionForPage(pageObj.currentPage,pageObj.rowsPerPage);
               $("#checkAll").prop("checked",false);
            }
         })
      }
   });

};
```

Mapper 层，分别需要查询具体数据和数据条数

接口

```java
    /**
     * 分页查询
     * @return
     */
     List<Activity> selectActivityByConditionForPage(Map<String,Object> map);

    /**
     * 查询市场活动数据总量
     * @param map
     * @return
     */
     int selectCountOfActivityByCondition(Map<String,Object> map);
     }
```

Mapper 实现

由于涉及条件查询，需要分别对四个条件的非空进行确认，确认非空后使用模糊查询

```xml
<select id="selectActivityByConditionForPage" parameterType="map" resultMap="BaseResultMap">
  select a.id,u1.name as owner,a.name,a.start_date,a.end_date,a.cost,a.description,a.create_time,
         u2.name as create_by,a.edit_time,u3.name as edit_by
  from tbl_activity a
  join tbl_user u1 on a.owner=u1.id
  join tbl_user u2 on a.create_by=u2.id
  left join tbl_user u3 on a.edit_by=u3.id
<where>
<if test="name!=null and name!=''">
      and a.name like '%' #{name} '%'
</if>
<if test="owner!=null and owner!=''">
      and u1.name like '%' #{owner} '%'
</if>
<if test="startDate!=null and startDate!=''">
      and a.start_date>=#{startDate}
</if>
<if test="endDate!=null and endDate!=''">
      and a.end_date<=#{endDate}
</if>
</where>
      order by a.create_time desc
      limit #{pageNo},#{pageSize}
</select>
<select id="selectCountOfActivityByCondition" parameterType="map" resultType="int">
  select count(*)
  from tbl_activity a
  join tbl_user u1 on a.owner=u1.id
  join tbl_user u2 on a.create_by=u2.id
  left join tbl_user u3 on a.edit_by=u3.id
<where>
<if test="name!=null and name!=''">
      and a.name like '%' #{name} '%'
</if>
<if test="owner!=null and owner!=''">
      and u1.name like '%' #{owner} '%'
</if>
<if test="startDate!=null and startDate!=''">
      and a.start_date>=#{startDate}
</if>
<if test="endDate!=null and endDate!=''">
      and a.end_date<=#{endDate}
</if>
</where>
</select>
```

Service 层调用 Mapper 层，代码略

Controller 层

将参数封装到 map 后调用 Service 方法查询后返回结果集合

```java
@RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
public @ResponseBody Object queryActivityByConditionForPage(String name,String owner,
                                                            String startDate,String endDate,
                                                            Integer pageNo,Integer pageSize){
    Map<String,Object> map=new HashMap<>();
    map.put("name",name);
    map.put("owner",owner);
    map.put("startDate",startDate);
    map.put("endDate",endDate);
    map.put("pageNo",(pageNo-1)*pageSize);
    map.put("pageSize",pageSize);
    List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
    int countOfActivityByCondition = activityService.queryCountOfActivityByCondition(map);
    Map<String,Object> resultMap=new HashMap<>();
    resultMap.put("activityList",activityList);
    resultMap.put("totalRows",countOfActivityByCondition);
    return resultMap;
}
```

### 9.3.1 进入市场活动页面显示所有数据

当用户初次进入此页面时，由于未选择查询方式，入口函数中默认调用查询函数，默认设置数据从头开始以及每页显示五条

```javascript
//当市场活动页面加载完成后 , 查询所有数据第一页和所有数据的总条数
queryActivityByConditionForPage(1,5);
```


### 9.3.2 条件查询

```javascript
//条件查询
$("#queryActivityBtn").click(function () {
   //点击查询后不会重置每页显示个数 , 而是读取上次的每页显示个数数据
queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination('getOption','rowsPerPage'));

});
```

### 9.3.2 分页显示功能

由于每次查询都是分页查询，所以此分页查询需要写在 queryActivityByConditionForPage(pageNo,pageSize)函数中

涉及根据数据总数(totalRows)和每页显示条数(pageSize)计算总页数(totalPages)


```java
				//计算总页数
				var totalPages=1;
				(data.totalRows%pageSize==0)?(totalPages=data.totalRows/pageSize):totalPages=parseInt(totalPages=data.totalRows/pageSize)+1

				//调用分页插件
				$("#demo_pag1").bs_pagination({
					//当前页号
					currentPage:pageNo,
					//每页显示数据条数
					rowsPerPage:pageSize,
					//数据总数
					totalRows:data.totalRows,
					//总页数
					totalPages:totalPages,
					//最多显示的卡片数
					visiblePageLinks: 5,
					//是否显示"跳转到"部分 , 默认为true showGoToPage:
					//是否显示"每页显示条数"部分 , 默认为true showRowsPerPage:
					//是否显示记录的信息 , 默认true showRowsInfo:,

					//当数据改变时 , 触发此函数
					onChangePage : function (event,pageObj){
						queryActivityByConditionForPage(pageObj.currentPage,pageObj.rowsPerPage);
						$("#checkAll").prop("checked",false);
					}
				})
```

## 9.4 删除市场活动

### 前台

涉及一条或多条同时删除，即涉及对全选按钮操作

```javascript
//给全选按钮添加单击事件
$("#checkAll").click(function (){
   //若"全选"按钮被选中 , 选中所有checkbox
   $("#tBody input[type='checkbox']").prop("checked",this.checked);
})
```

 当所有数据都被手动选中时 , 自动选中"全选"按钮 ; 否则只要有任何一个数据没被选中 , 则将全选按钮取消选中

```javascript
//当所有数据都被手动选中时 , 自动选中"全选"按钮 ; 否则只要有任何一个数据没被选中 , 则将全选按钮取消选中
$("#tBody").on("click","input[type='checkbox']",function (){
      if ($("#tBody input[type='checkbox']").size()==$("#tBody input[type='checkbox']:checked").size()){
         $("#checkAll").prop("checked",true);
      }else {
         $("#checkAll").prop("checked",false);
      }
});
```

将多个 id 拼接，发送请求


```js
		//删除按钮处理事件
		$("#deleteActivityBtn").click(function (){
			var checkedIds=$("#tBody input[type='checkbox']:checked");

			if (checkedIds.size()==0){
				alert("请选择要删除的市场活动");
				return
			}

			if (window.confirm("是否确认删除")){
				var ids="";
				//对checkedIds中的每个对象都执行此方法
				$.each(checkedIds,function (){
					//将数据封装为  id=xxxx&id=xxxx....的形式 , 但最终会多一个 &
					ids+="id="+this.value+"&";
				})
				//alert(ids);
				//对此字符串不取最后一个&
				ids=ids.substr(0,ids.length-1);
				$.ajax({
					url : 'workbench/activity/deleteActivityByIds.do' ,
					data : ids ,
					type : 'post',
					dataType : 'json',
					success : function (data){
						if (data.code=="1")
							queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination('getOption','rowsPerPage'));
						else
							alert(data.message);

					}
				})

			}
		})
```

### 后台

#### Mapper 层

mapper 接口

```java
/**
     * 根据一个或多个id删除
     * @param ids
     * @return
     */
    int deleteActivityByIds(String[] ids);
```

xml

```xml
  <delete id="deleteActivityByIds" parameterType="string">
    delete
    from tbl_activity
    where id in
          <foreach collection="array" item="id" separator="," open="(" close=")">
            #{id}
          </foreach>
  </delete>
```

#### Service 层

Service 接口

```java
    int deleteActivityByIds(String[] ids);
```

ServiceImpl

```java
    @Override
    public int deleteActivityByIds(String[] ids) {
        return activityMapper.deleteActivityByIds(ids);
    }
```

#### Controller 层

```java
    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    public @ResponseBody Object deleteActivityByids(String[] id){
        ReturnObject returnObject = new ReturnObject();
        try {
            if (activityService.deleteActivityByIds(id)>0){
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("删除失败 , 请重试!>0");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("删除失败 , 请重试!try");
        }
        return returnObject;
    }
```

## 9.5 修改市场活动

### 前台

选中一个市场活动，单击修改按钮后，查询该 id 的数据并渲染到模态窗口中

```javascript
		//给修改按钮添加单击事件
		$("#editActivityBtn").click(function (){
			var checkIds = $("tBody input[type='checkbox']:checked");
			if (checkIds.size()!=1){
				alert("请逐条修改");
				return
			}
			var id =checkIds[0].value;
			//alert(id);
			$.ajax({
				url : 'workbench/activity/queryActivityById.do',
				data : {
					id:id
				},
				type : 'post',
				dataType : 'json',
				success : function (data){
					$("#edit-id").val(data.id);
					$("#edit-marketActivityOwner").val(data.owner);
					$("#edit-marketActivityName").val(data.name);
					$("#edit-startData").val(data.startDate);
					$("#edit-endData").val(data.endDate);
					$("#edit-cost").val(data.cost);
					$("#edit-description").val(data.description);

					$("#editActivityModal").modal("show");
				}
			})
		})
```

用户修改完相关数据后，单击更新按钮，即更新数据

```javascript
		//给更新按钮添加单击事件
		$("#saveEditActivityBtn").click(function (){
			var id=$("#edit-id").val();
			var owner=$("#edit-marketActivityOwner").val();
			var name=$.trim($("#edit-marketActivityName").val());
			var startDate=$("#edit-startData").val();
			var endDate=$("#edit-endData").val();
			var cost=$.trim($("#edit-cost").val());
			var description=$.trim($("#edit-description").val());
			if (owner==""){
				alert("请选择所有者");
				return
			}
			if (name==""){
				alert("请输入市场活动名称");
				return
			}
			if (startDate!=""&&endDate!=""){
			    	if (startDate<startDate){
					alert("请输入正确的结束日期与开始日期 , 结束日期需大于开始日期")
					return
				}
			}
			var regExp=/^(([1-9]\d*)|0)$/;
			if (!regExp.test(cost)){
				alert("请输入正确的成本");
				return;
			}
			$.ajax({
				url : 'workbench/activity/saveEditActivity.do',
				data : {
					id : id,
					owner : owner,
					name : name,
					startDate : startDate,
					endDate : endDate,
					cost : cost,
					description : description
				},
				type : 'post',
				dataType : 'json',
				success : function (data){
					if (data.code==1){
						$("#editActivityModal").modal("hide");
						queryActivityByConditionForPage($("#demo_pag1").bs_pagination('getOption','currentPage'),$("#demo_pag1").bs_pagination('getOption','rowsPerPage'))
					}else
						alert(data.message);
				}
			})
		})
```

### 后台(涉及查询该条数据、修改该条数据)

**Mapper 层**

ActivityMapper(mapper interface)

```java
    /**
     * 根据id查询市场活动数据
     * @param id
     * @return
     */
    Activity selectActivityById(String id);

    /**
     * 保存修改市场活动
     * @param activity
     * @return
     */
    int updateActivity(Activity activity);
```

ActivityMapper.xml(mapper impl)

```xml
  <select id="selectActivityById" parameterType="string" resultMap="BaseResultMap">
    select <include refid="Base_Column_List"/>
    from tbl_activity
    where id = #{id}
  </select>
  <update id="updateActivity" parameterType="com.bjpowernode.crm.workbench.domain.Activity">
    update tbl_activity
    set owner=#{owner},name=#{name},
        start_date=#{startDate},end_date=#{endDate},
        cost=#{cost},description=#{description},
        edit_time=#{editTime},edit_by=#{editBy}
    where id=#{id}
</update>
```

**​Service 层**

ServiceInterface

```java
    Activity queryActivityById(String id);

    int saveEditActivity(Activity activity);
```

ServiceImpl

```java
    @Override
    public Activity queryActivityById(String id) {
        return activityMapper.selectActivityById(id);
    }

    @Override
    public int saveEditActivity(Activity activity) {
        return activityMapper.updateActivity(activity);
    }
```

**Controller 层**

```java
    @RequestMapping("/workbench/activity/queryActivityById.do")
    public @ResponseBody Object queryActivityById(String id){
        return activityService.queryActivityById(id);
    }

    @RequestMapping("/workbench/activity/saveEditActivity.do")
    private @ResponseBody Object saveEditActivity(Activity activity, HttpSession session){
        ReturnObject returnObject = new ReturnObject();
//        手动封装参数 : 修改时间 、修改人id
        activity.setEditTime(DateUtils.formateDateTime(new Date()));
        activity.setEditBy(((User)session.getAttribute(Contacts.SESSION_USER)).getId());
        try {
            if (activityService.saveEditActivity(activity)>0)
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("修改失败 , 请重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("修改失败 , 请重试");

        }
        return returnObject;
    }
```

## 9.6 导出市场活动

封装工具类HSSFUtils


```java
/**
 * excel工具类
 */
public class HSSFUtils {
    public static String getCellValueForStr(HSSFCell cell){
        String result="";
        if (cell.getCellType()== HSSFCell.CELL_TYPE_STRING) {
            result = cell.getStringCellValue();
        } else if (cell.getCellType()== HSSFCell.CELL_TYPE_NUMERIC) {
            result = cell.getNumericCellValue() + "";
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
            result=cell.getBooleanCellValue()+"";
        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
            result=cell.getCellFormula();
        } else {
            result="";
        }
        return result;
    }
}
```

### 批量导出

使用apache-poi插件

#### Mapper层

Interface

```javascript
    List<Activity> selectAllActivitys();
```

xml

```javascript
  <select id="selectAllActivitys" resultMap="BaseResultMap">
    select a.id,u1.name as owner,a.name,a.start_date,a.end_date,a.cost,a.description,a.create_time,
           u2.name as create_by,a.edit_time,u3.name as edit_by
    from tbl_activity a
    join tbl_user u1 on a.owner=u1.id
    join tbl_user u2 on a.create_by=u2.id
    left join tbl_user u3 on a.edit_by=u3.id
    order by a.create_time desc
  </select>
```

#### Service层

Interface

```javascript
    List<Activity> queryAllActivitys();
```

Impl

```javascript
    @Override
    public List<Activity> queryAllActivitys() {
        return activityMapper.selectAllActivitys();
    }
```

#### Controller层

```javascript
    @RequestMapping("/workbench/activity/exportAllActivitys.do")
    public void exportAllActivitys(HttpServletResponse response) throws Exception{
        List<Activity> activityList = activityService.queryAllActivitys();
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell =null;
        cell=row.createCell(0);cell.setCellValue("ID");
        cell=row.createCell(1);cell.setCellValue("所有者");
        cell=row.createCell(2);cell.setCellValue("名称");
        cell=row.createCell(3);cell.setCellValue("开始日期");
        cell=row.createCell(4);cell.setCellValue("结束日期");
        cell=row.createCell(5);cell.setCellValue("成本");
        cell=row.createCell(6);cell.setCellValue("描述");
        cell=row.createCell(7);cell.setCellValue("创建时间");
        cell=row.createCell(8);cell.setCellValue("创建者");
        cell=row.createCell(9);cell.setCellValue("修改时间");
        cell=row.createCell(10);cell.setCellValue("修改者");

//        遍历 activityList , 创建数据对象
        if (activityList!=null && activityList.size()>0){
            Activity activity = null;
            for (int i = 0; i <activityList.size(); i++){
                activity=activityList.get(i);
//            每个对象生成一行
                row=sheet.createRow(i+1);
//            每个属性生成一行
                cell=row.createCell(0);cell.setCellValue(activity.getId());
                cell=row.createCell(1);cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);cell.setCellValue(activity.getName());
                cell=row.createCell(3);cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);cell.setCellValue(activity.getCost());
                cell=row.createCell(6);cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);cell.setCellValue(activity.getEditBy());
            }
        }

//      将生成的 excel文件下载到客户端
        response.setContentType("application/octet-stream;charest=UTF-8");
        response.addHeader("Content-Disposition","attachment;filename=activityList.xls");
        OutputStream outputStream = response.getOutputStream();
//
        wb.write(outputStream);
        wb.close();
        outputStream.flush();
    }
```

### 前台

```java
		//给批量导出按钮添加单击事件
		$("#exportActivityAllBtn").click(function (){
			window.location.href="workbench/activity/exportAllActivitys.do";
		})
```

### 选择导出

#### Mapper

Interface

```java
    List<Activity> selectActivityByIds(String[] ids);
```

xml

```javascript
  <select id="selectActivityByIds" parameterType="string" resultMap="BaseResultMap">
    select a.id,u1.name as owner,a.name,a.start_date,a.end_date,a.cost,a.description,a.create_time,
           u2.name as create_by,a.edit_time,u3.name as edit_by
    from tbl_activity a
           join tbl_user u1 on a.owner=u1.id
           join tbl_user u2 on a.create_by=u2.id
           left join tbl_user u3 on a.edit_by=u3.id
    where a.id in
        <foreach collection="array" item="id" separator="," open="(" close=")">
          #{id}
        </foreach>
    order by a.create_time desc

  </select>
```

#### Service

Interface

```java
    List<Activity> queryActivityByIds(String[] ids);
```

Impl


```java
    @Override
    public List<Activity> queryActivityByIds(String[] ids) {
        return activityMapper.selectActivityByIds(ids);
    }
```

#### Controller


```java
    @RequestMapping("/workbench/activity/exportActivityByIds.do")
    public void exportActivityById(String[] id,HttpServletResponse response) throws Exception{
        List<Activity> activityList = activityService.queryActivityByIds(id);
        HSSFWorkbook wb=new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell =null;
        cell=row.createCell(0);cell.setCellValue("ID");
        cell=row.createCell(1);cell.setCellValue("所有者");
        cell=row.createCell(2);cell.setCellValue("名称");
        cell=row.createCell(3);cell.setCellValue("开始日期");
        cell=row.createCell(4);cell.setCellValue("结束日期");
        cell=row.createCell(5);cell.setCellValue("成本");
        cell=row.createCell(6);cell.setCellValue("描述");
        cell=row.createCell(7);cell.setCellValue("创建时间");
        cell=row.createCell(8);cell.setCellValue("创建者");
        cell=row.createCell(9);cell.setCellValue("修改时间");
        cell=row.createCell(10);cell.setCellValue("修改者");

//        遍历 activityList , 创建数据对象
        if (activityList!=null && activityList.size()>0){
            Activity activity = null;
            for (int i = 0; i <activityList.size(); i++){
                activity=activityList.get(i);
//            每个对象生成一行
                row=sheet.createRow(i+1);
//            每个属性生成一行
                cell=row.createCell(0);cell.setCellValue(activity.getId());
                cell=row.createCell(1);cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);cell.setCellValue(activity.getName());
                cell=row.createCell(3);cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);cell.setCellValue(activity.getCost());
                cell=row.createCell(6);cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);cell.setCellValue(activity.getEditBy());
            }
        }
        response.setContentType("application/octet-stream;charest=UTF-8");
        response.addHeader("Content-Disposition","attachment;filename=activityList.xls");
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        wb.close();
        outputStream.flush();
    }
```

### 前台

```java
		//给选择导出按钮添加单击事件
		$("#exportActivityXzBtn").click(function (){
			var checkedIds=$("#tBody input[type='checkbox']:checked");

			if (checkedIds.size()==0){
				alert("请选择要导出的市场活动");
				return
			}

			var ids="";
			//对checkedIds中的每个对象都执行此方法
			$.each(checkedIds,function (){
				//将数据封装为  id=xxxx&id=xxxx....的形式 , 但最终会多一个 &
				ids+="id="+this.value+"&";
			})
			//alert(ids);
			//对此字符串不取最后一个&
			ids=ids.substr(0,ids.length-1);

			window.location.href="workbench/activity/exportActivityByIds.do?"+ids;
		})
```

## 9.7 导入市场活动

### 前台

```java
		//给导入市场活动按钮添加单击事件
		$("#importActivityBtn").click(function (){
			var activityFileName=$("#activityFile").val();
			var suffix=activityFileName.substr(activityFileName.lastIndexOf(".")+1).toLocaleLowerCase();
			if (suffix!="xls"){
				alert("只支持excel文件 , 即后缀为 .xls 类型的文件!")
				return
			}
			var activityFile=$("#activityFile")[0].files[0];
			if (activityFile.size>5*1024*1024){
				alert("文件最大只能5MB");
				return;
			}
			var formData=new FormData();
			formData.append("activityFile",activityFile);
			$.ajax({
				url : "workbench/activity/importActivity.do",
				data : formData,
				processData : false,
				contentType : false,
				type : "post",
				dataType : "json",
				success : function (data) {
					if (data.code=="1"){
						alert(data.message);
						$("#importActivityModal").modal("hide");
						queryActivityByConditionForPage(1,$("#demo_pag1").bs_pagination('getOption','rowsPerPage'));
					} else {
						alert(data.message);
						$("#importActivityModal").modal("show");
					}
				}
			})
		})
```

### 后台

Mapper

```java
    /**
     * 批量保存市场活动 ( 导入功能 )
     * @param activityList
     * @return
     */
    int insertActivityByList(List<Activity> activityList);
```

xml

```xml
  <insert id="insertActivityByList" parameterType="com.bjpowernode.crm.workbench.domain.Activity">
    insert into
        tbl_activity(    id, owner, name,
                     start_date, end_date, cost,
                     description, create_time, create_by   )
    values
    <foreach collection="list" item="obj" separator=",">
        (#{obj.id},#{obj.owner},#{obj.name},
        #{obj.startDate},#{obj.endDate},#{obj.cost},
        #{obj.description},#{obj.createTime},#{obj.createBy})
    </foreach>
  </insert>
```

Service


```java
    int saveCreateActivityByList(List<Activity> activityList);
```

ServiceImpl


```java
    @Override
    public int saveCreateActivityByList(List<Activity> activityList) {
        return activityMapper.insertActivityByList(activityList);
    }
```

Controller


```java
    @RequestMapping("/workbench/activity/importActivity.do")
    public @ResponseBody Object importActivity(MultipartFile activityFile,HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        User user = (User) session.getAttribute(Contacts.SESSION_USER);
        try {
//            将excel文件写到磁盘上
//            String filename = activityFile.getOriginalFilename();
//            File file = new File("F:\\Java Web\\DLJD-crm\\md",filename);
//            activityFile.transferTo(file);

//            解析excel文件
//            InputStream inputStream = new FileInputStream("F:\\Java Web\\DLJD-crm\\md\\" + filename);
//            得到该文件的workbook对象
            InputStream inputStream=activityFile.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
//            得到页对象
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow row=null;
            HSSFCell cell=null;
            Activity activity=null;
            List<Activity> activityList = new ArrayList<>();
//            遍历该页 , getLastRowNum()函数获取最后一行的下标
            for (int i=1;i<=sheet.getLastRowNum();i++){
//                获得该行对象
                row=sheet.getRow(i);
//                遍历该行
                activity=new Activity();
//                id 、owner 、createTime、createBy自动设置
                activity.setId(UUIDUtils.getUUID());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formateDateTime(new Date()));
                activity.setCreateBy(user.getId());
                for (int j=0;j<row.getLastCellNum();j++){
//                    获取数据 , 存入activity对象
                    cell=row.getCell(j);
                    String cellValue = HSSFUtils.getCellValueForStr(cell);
                    switch (j){
                        case 0:activity.setName(cellValue);
                        case 1:activity.setStartDate(cellValue);
                        case 2:activity.setEndDate(cellValue);
                        case 3:activity.setCost(cellValue);
                        case 4:activity.setDescription(cellValue);
                    }
                }
                activityList.add(activity);
            }
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setMessage(" 成功上传 "+activityService.saveCreateActivityByList(activityList)+" 条数据 ");
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage(" 文件上传失败 , 请重试 ");
        }
        return returnObject;
    }
```

# 10. 市场活动明细、备注相关功能

## 查看市场活动明细功能

单击任一市场活动，超链接到detail.jsp页面，携带该市场活动id，并显示该市场活动相应明细、该市场活动备注

涉及查询市场活动与查询市场活动备注，涉及对另一表tbl_activity_remark操作，重新使用Mybtis逆向工程生成

实体类ActivityRemark、

持久层ActivityRemarkmapper接口与ActivityRemarkMapper.xml文件、

服务层ActivityRemarkService接口与ActivityRemarkServiceImpl实现类、

控制层ActivityRemarkController

### 后台

**mapper层**

```java
 /**
     * 根据市场活动 id 查询明细信息
     * @param id
     * @return
     */
    Activity selectActivityForDetailById(String id);
```

```xml
  <select id="selectActivityForDetailById" parameterType="string" resultMap="BaseResultMap">
    select a.id,u1.name as owner,a.name,a.start_date,
           a.end_date,a.cost,a.description,
           a.create_time,u2.name as create_by,
           a.edit_time,u3.name as edit_by
    from tbl_activity a
    join tbl_user u1 on a.owner=u1.id
    join tbl_user u2 on a.create_by=u2.id
    left join tbl_user u3 on a.edit_by=u3.id
    where a.id=#{id}
  </select>
```



```java
    /**
     * 根据市场活动id , 查询该市场活动对应的市场活动明细
     * @param activityId
     * @return
     */
    List<ActivityRemark> selectActivityRemarkForDetailByActivityId(String activityId);
```


```xml
  <select id="selectActivityRemarkForDetailByActivityId" parameterType="string" resultMap="BaseResultMap">
    select ar.id,ar.note_content,ar.create_time,u1.name as create_by,ar.edit_time,u2.name as edit_by,ar.edit_flag
    from tbl_activity_remark ar
           join tbl_user u1 on ar.create_by=u1.id
           left join tbl_user u2 on ar.edit_by=u2.id
    where ar.activity_id=#{activityId}
    order by ar.create_time
  </select>
```

**Service层略**

**Controller层**

```java
    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id, HttpServletRequest request){
        Activity activity = activityService.queryActivityForDetailById(id);
        List<ActivityRemark> remarkList = activityRemarkService.queryActivityRemarkForDetailByActivityId(id);
        request.setAttribute("activity",activity);
        request.setAttribute("remarkList", remarkList);
        return "workbench/activity/detail";
    }
```

### 前台

渲染市场活动备注

```xml
		<!--遍历remarkList，显示所有的备注-->
		<c:forEach items="${remarkList}" var="remark">
			<div id="div_${remark.id}" class="remarkDiv" style="height: 60px;">
				<img title="${remark.createBy}" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
				<div style="position: relative; top: -40px; left: 40px;" >
					<h5>${remark.noteContent}</h5>
					<font color="gray">市场活动</font> <font color="gray">-</font> <b>${activity.name}</b> <small style="color: gray;"> ${remark.editFlag=='1'?remark.editTime:remark.createTime} 由 ${remark.editFlag=='1'?remark.editBy:remark.createBy}${remark.editFlag=='1'?' 修改':' 创建'}</small>
					<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
						<a class="myHref" name="editA" remarkId="${remark.id}" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<a class="myHref" name="deleteA" remarkId="${remark.id}" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
					</div>
				</div>
			</div>
		</c:forEach>
```



## 保存市场活动明细功能

### 后台

**mapper**


```html
    /**
     * 插入市场活动明细
     * @param activityRemark
     * @return
     */
    int insertActivityRemark(ActivityRemark activityRemark);
```

**Service略​**

**Controller**

```java
    @RequestMapping("/workbench/activity/saveCreateActivityRemark.do")
    public @ResponseBody Object saveCreateActivityRemark(ActivityRemark activityRemark, HttpSession session){
        User user = (User) session.getAttribute(Contacts.SESSION_USER);
        //        封装参数
        activityRemark.setId(UUIDUtils.getUUID());
        activityRemark.setCreateTime(DateUtils.formateDateTime(new Date()));
        activityRemark.setCreateBy(user.getId());
        activityRemark.setEditFlag(Contacts.REMARK_EDIT_FLAG_NO_EDIT);

        ReturnObject returnObject = new ReturnObject();
        try {
            int saveCreateActivityRemarkResult = activityRemarkService.saveCreateActivityRemark(activityRemark);
            if (saveCreateActivityRemarkResult>0){
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMessage("保存成功");
                returnObject.setRetData(activityRemark);
            }else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("保存失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("保存失败");
        }
        return returnObject;
    }
```




### 前台


```html
		//给保存按钮添加单击事件
		$("#saveCreateActivityRemarkBtn").click(function (){
			var noteContent=$.trim($("#remark").val());
			var activityId='${activity.id}';
			if (noteContent==""){
				alert("请输入备注内容");
				return;
			}
			$.ajax({
				url : 'workbench/activity/saveCreateActivityRemark.do',
				data : {
					noteContent : noteContent,
					activityId : activityId
				},
				type : 'post',
				dataType : 'json',
				success : function (data){
					if (data.code=="1"){
						$("#remark").val("");
						var htmlStr="";
						htmlStr+="<div id=\"div_"+data.retData.id+"\" class=\"remarkDiv\" style=\"height: 60px;\">";
						htmlStr+="<img title=\"${sessionScope.sessionUser.name}\" src=\"image/user-thumbnail.png\" style=\"width: 30px; height:30px;\">";
						htmlStr+="<div style=\"position: relative; top: -40px; left: 40px;\" >";
						htmlStr+="<h5>" +data.retData.noteContent+ "</h5>";
						htmlStr+="<font color=\"gray\">市场活动</font> <font color=\"gray\">-</font> <b>${activity.name}</b> <small style=\"color: gray;\"> "+data.retData.createTime+" 由 ${sessionScope.sessionUser.name} 创建 </small>";
						htmlStr+="<div style=\"position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;\">";
						htmlStr+="<a class=\"myHref\" name=\"editA\" remarkId=\""+data.retData.id+"\" href=\"javascript:void(0);\"><span class=\"glyphicon glyphicon-edit\" style=\"font-size: 20px; color: #E6E6E6;\"></span></a>";
						htmlStr+="&nbsp;&nbsp;&nbsp;&nbsp;";
						htmlStr+="<a class=\"myHref\" name=\"deleteA\" remarkId=\""+data.retData.id+"\" href=\"javascript:void(0);\"><span class=\"glyphicon glyphicon-remove\" style=\"font-size: 20px; color: #E6E6E6;\"></span></a>";
						htmlStr+="</div>";
						htmlStr+="</div>";
						htmlStr+="</div>";
						$("#remarkDiv").before(htmlStr);
					}else {
						alert(data.message);
					}
				}
			})
		})
```

## 删除市场活动明细功能

### 后台

**Mapper**


```java
    /**
     * 根据 Id 删除市场活动备注
     * @param id
     * @return
     */
    int deleteActivityRemarkById(String id);
```


```xml
  <delete id="deleteActivityRemarkById" parameterType="string">
    delete
    from    tbl_activity_remark
    where id=#{id}
  </delete>
```

**Service**

**Controller**

```java
    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    public @ResponseBody Object deleteActivityRemarkById(String id){
        ReturnObject returnObject = new ReturnObject();
        try {
            int deleteActivityRemarkByIdResult = activityRemarkService.deleteActivityRemarkById(id);
            if (deleteActivityRemarkByIdResult>0){
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("删除失败 , 请重试!");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("删除失败 , 请重试!");
        }
        return returnObject;
    }

```

### 前台


```html
		//给删除图标添加单击事件
		$("#remarkDivList").on("click","a[name='deleteA']",function (){
			var id = $(this).attr("remarkId");
			$.ajax({
				url : 'workbench/activity/deleteActivityRemarkById.do',
				data: {
					id : id
				},
				type: 'post',
				dataType: 'json',
				success : function (data){
					if (data.code=="1"){
						$("#div_"+id).remove();
					}else {
						alert(data.message);
					}
				}
			})
		})
```

## 修改市场活动明细功能

### 后台

**Mapper**


```java
    /**
     * 修改市场活动备注
     * @param newActivityRemarkNoteContent
     * @return
     */
    int updateActivitRemark(ActivityRemark activityRemark);
```


```xml
  <update id="updateActivitRemark" parameterType="com.bjpowernode.crm.workbench.domain.ActivityRemark">
    update tbl_activity_remark
    set note_content=#{noteContent},edit_time=#{editTime},edit_By=#{editBy},edit_flag=#{editFlag}
    where id=#{id}
  </update>
```

**​Service**

**Controller**


```java
    @RequestMapping("/workbench/activity/saveEditActivityRemark.do")
    public @ResponseBody Object saveEditActivityRemark(ActivityRemark activityRemark,HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        User user =(User) session.getAttribute(Contacts.SESSION_USER);
//        手动封装实体类
        activityRemark.setEditBy(user.getId());
        activityRemark.setEditTime(DateUtils.formateDateTime(new Date()));
        activityRemark.setEditFlag(Contacts.REMARK_EDIT_FLAG_YES_EDIT);

        try {
            int saveEditActivityRemarkResult = activityRemarkService.saveEditActivityRemark(activityRemark);
            if (saveEditActivityRemarkResult>0){
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setRetData(activityRemark);
            }else {
                returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("更新失败 , 请重试!");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Contacts.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("更新失败 , 请重试!");
        }
        return returnObject;
    }
```


### 前台


```javascript
		//给修改图标添加单击事件
		$("#remarkDivList").on("click","a[name='editA']",function (){
			var id = $(this).attr("remarkId");
			var oldNoteContent=$("#div_"+id+" h5").text();
			//给修改的模态窗口添加数据
			$("#edit-HiddenId").val(id);
			$("#edit-noteContent").val(oldNoteContent);
			$("#editRemarkModal").modal("show");

		})

		//给更新按钮添加单击事件
		$("#updateRemarkBtn").click(function (){
			var id=$("#edit-HiddenId").val();
			var newNoteContent = $("#edit-noteContent").val();
			// alert(newNoteContent);
			$.ajax({
				url : 'workbench/activity/saveEditActivityRemark.do',
				data : {
					id:id,
					noteContent: newNoteContent
				},
				type :'post',
				dataType: 'json',
				success : function (data){
					if (data.code=="1"){
						$("#editRemarkModal").modal("hide");
						$("#div_"+id+" h5").text(data.retData.noteContent);
						$("#div_"+id+" small").text(" "+data.retData.editTime+" 由 ${sessionScope.sessionUser.name} 修改");
					}else {
						alert(data.message);
					}
				}
			})
		})
```
