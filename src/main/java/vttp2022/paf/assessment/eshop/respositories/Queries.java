package vttp2022.paf.assessment.eshop.respositories;

public class Queries {
    
    public static final String SQL_FIND_USER_BY_NAME = 
        "select * from customers where name = ?";

    public static final String SQL_INSERT_ORDER =
        "insert into orders(orderId, name, address, email) values(?, ?, ?, ?)";

    public static final String SQL_INSERT_ORDER_ITEMS =
        "insert into orderItems(item, quantity, orderId) values(?,?,?)";

    public static final String SQL_SAVE_ORDER_STATUS_SUCCESS =
        "insert into order_status values(?,?,?,?)";

    public static final String SQL_SAVE_ORDER_STATUS_PENDING = 
        "insert into order_status(order_id, status, status_update)";

    public static final String SQL_INSERT_ORDER_STATUS =
        "insert into order_status values(?,?,?,?)";

    public static final String SQL_GET_ORDER_STATUS =
        "select count(status) as count from customers join orders on customers.name = orders.name join order_status on orders.orderId = order_status.orderId where status=? and customers.name = ?;";
}
