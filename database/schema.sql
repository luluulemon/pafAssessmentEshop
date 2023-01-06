create table customers(

    name    varchar(32)     not null,
    address varchar(128)    not null,
    email   varchar(128)    not null,

    primary key(name),
);

insert into customers values("fred", "201 Cobblestone Lane", "fredflintstone@bedrock.com");
insert into customers values("sherlock", "221B Baker Street London", "sherlock@consultingdetective.org");
insert into customers values("spongebob", "124 Conch Street Bikini Bottom", "spongebob@yahoo.com");
insert into customers values("jessica", "698 Candlewood Land Cabot Cove", "fletcher@gmail.com");
insert into customers values("dursley", "4 Privet Drive Little Whinging Surrey", "dursley@gmail.com");

create table orders (

    orderId char(8) not null,
    name    varchar(32)     not null,
    address varchar(128)    not null,
    email   varchar(128)    not null,

    primary key(orderId),
    constraint fk_name foreign key(name) references customers(name) 
)


create table orderItems(

    item    varchar(64)     not null,
    quantity    int         not null,
    orderId char(8)         not null,

    constraint fk_orderId foreign key(orderId) references orders(orderId)    
)


create table order_status(

    orderId char(8) not null,
    deliveryId varchar(128),
    status  enum("dispatched", "pending") not null,
    status_update varchar(64)

)


