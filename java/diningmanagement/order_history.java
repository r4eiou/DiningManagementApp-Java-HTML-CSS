package diningmanagement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class order_history {

 
    public int order_history_id;
    public float order_rating;
    public String order_satisfaction;
    public float totalPrice;
    public LocalDateTime date_ordered;
    public int order_transaction_id;
    public String monthName;
    public int year;
    public int salesPerMonth;
    public int salesPerYear;
    public int orderlist;

    public ArrayList<Integer> order_history_idlist = new ArrayList<>();
    public ArrayList<Float> order_ratinglist = new ArrayList<>();
    public ArrayList<String> order_satisfactionlist = new ArrayList<>();
    public ArrayList<Float> totalPricelist = new ArrayList<>();
    public ArrayList<LocalDateTime> date_orderedlist = new ArrayList<>();
    public ArrayList<Integer> order_transaction_idlist = new ArrayList<>();
    public ArrayList<String> monthNamelist = new ArrayList<>();
    public ArrayList<Integer> yearlist = new ArrayList<>();
    public ArrayList<Integer> salesPerMonthlist = new ArrayList<>();
    public ArrayList<Integer> salesPerYearlist = new ArrayList<>();
    public ArrayList<String> orderlistList = new ArrayList<>();
    

    public ArrayList<String>  monthlistMMOM = new ArrayList<>();
    public ArrayList<String>  meallistMMOM = new ArrayList<>();
    public ArrayList<Integer> ordercountMMOM = new ArrayList<>();
    

    public ArrayList<Integer>  yearlistYMOM = new ArrayList<>();
    public ArrayList<String>  meallistYMOM = new ArrayList<>();
    public ArrayList<Integer> ordercountYMOM = new ArrayList<>();

    public void clearList() {
        order_history_idlist.clear();
        order_ratinglist.clear();
        order_satisfactionlist.clear();
        totalPricelist.clear();
        date_orderedlist.clear();
        order_transaction_idlist.clear();
        monthNamelist.clear();
        yearlist.clear();
        salesPerMonthlist.clear();
        salesPerYearlist.clear();
        orderlistList.clear();
        monthlistMMOM.clear();
        meallistMMOM.clear();
        ordercountMMOM.clear();
        yearlistYMOM.clear();
        meallistYMOM.clear();
        ordercountYMOM.clear();
    }

    public boolean add_orderhistory() {
        clearList();
        try {
            date_ordered = LocalDateTime.now();

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn;
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dining_db?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "password");
            System.out.println("Connection Successful");

            // Prepare SQL statement
            PreparedStatement pstmt = conn.prepareStatement("SELECT MAX(order_history_id) + 1 AS new_id FROM order_history"); // Get a new transaction_id
            ResultSet rst = pstmt.executeQuery();

            while (rst.next()) {
                order_history_id = rst.getInt("new_id");
            }

            pstmt = conn.prepareStatement("INSERT INTO order_history (order_history_id, order_rating, order_satisfaction, totalPrice, date_ordered, order_transaction_id) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, order_history_id);
            pstmt.setFloat(2, order_rating);
            pstmt.setString(3, order_satisfaction);
            pstmt.setFloat(4, totalPrice);
            Timestamp timestamp = Timestamp.valueOf(date_ordered);
            pstmt.setTimestamp(5, timestamp);
            pstmt.setInt(6, order_transaction_id);
            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
            return true;

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int view_order_history() {
        clearList();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn;
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dining_db?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "password");
            System.out.println("Connection Successfull");

            PreparedStatement pstmt = conn.prepareStatement("SELECT oh.order_history_id, oh.order_rating, oh.order_satisfaction, oh.totalPrice, oh.date_ordered,  GROUP_CONCAT(CONCAT(m.meal_name, ' (', tm.quantity, ')') ORDER BY m.meal_name ASC) AS 'Orderlist' FROM order_history oh JOIN order_transaction ot ON ot.transaction_id = oh.order_transaction_id  JOIN transaction_meal tm ON ot.transaction_id = tm.transaction_id  JOIN meal m ON tm.meal_id = m.meal_id GROUP BY oh.order_history_id ORDER BY oh.order_history_id ASC;");
            ResultSet rst = pstmt.executeQuery();

            while (rst.next()) {
                order_history_idlist.add(rst.getInt("order_history_id"));
                order_ratinglist.add(rst.getFloat("order_rating"));
                order_satisfactionlist.add(rst.getString("order_satisfaction"));
                totalPricelist.add(rst.getFloat("totalPrice"));
                Timestamp timestamp = rst.getTimestamp("date_ordered");
                LocalDateTime localDateTime = timestamp.toLocalDateTime();
                date_orderedlist.add(localDateTime);
                String orderlistValue = rst.getString("Orderlist");
                orderlistList.add(orderlistValue);
            }

            pstmt.close();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int generate_monthly_sales_report() {
        clearList();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn;
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dining_db?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "password");
            System.out.println("Connection Successfull");

            PreparedStatement pstmt = conn.prepareStatement("SELECT DATE_FORMAT(date_ordered, '%M') AS Month_Name, SUM(totalPrice) AS Monthly_Sales FROM  order_history GROUP BY DATE_FORMAT(date_ordered, '%M') ORDER BY DATE_FORMAT(date_ordered, '%M')");
            ResultSet rst = pstmt.executeQuery();

            while (rst.next()) {
                monthNamelist.add(rst.getString("Month_Name"));
                salesPerMonthlist.add(rst.getInt("Monthly_Sales"));

                System.out.println("Month_Name: " + rst.getString("Month_Name"));
                System.out.println("Monthly_Sales: " + rst.getInt("Monthly_Sales"));
            }
            pstmt.close();
            conn.close();

            return 1;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int generate_yearly_sales_report() {
        clearList();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn;
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dining_db?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "password");
            System.out.println("Connection Successfull");

            PreparedStatement pstmt = conn.prepareStatement("SELECT YEAR(date_ordered) AS Year, SUM(totalPrice) AS Yearly_Sales FROM order_history GROUP BY YEAR(date_ordered) ORDER BY YEAR(date_ordered);");
            ResultSet rst = pstmt.executeQuery();

            while (rst.next()) {
                yearlist.add(rst.getInt("Year"));
                salesPerYearlist.add(rst.getInt("Yearly_Sales"));

                System.out.println("Month_Name: " + rst.getInt("Year"));
                System.out.println("Monthly_Sales: " + rst.getInt("Yearly_Sales"));
            }
            pstmt.close();
            conn.close();

            return 1;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int getRecentOrderHistory() {
        int recentOrderHistoryId = -1;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn;
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dining_db?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "password");
            System.out.println("Connection Successfull");

            PreparedStatement pstmt = conn.prepareStatement("SELECT MAX(order_history_id) AS new_id FROM order_history");
            ResultSet rst = pstmt.executeQuery();

            while (rst.next()) {
                recentOrderHistoryId = rst.getInt("new_id");
            }

            pstmt.close();
            conn.close();
            return recentOrderHistoryId;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public int getMonthlyMostOrderedMeals() {
    clearList();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dining_db?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "password")) {
            String sql = "WITH RankedMeals AS ( " +
                    "    SELECT " +
                    "        DATE_FORMAT(oh.date_ordered, '%M') AS Month_Name, " +
                    "        m.meal_name AS Most_Ordered_Meal, " +
                    "        SUM(tm.quantity) AS Order_Count, " +
                    "        ROW_NUMBER() OVER (PARTITION BY DATE_FORMAT(oh.date_ordered, '%M') ORDER BY SUM(tm.quantity) DESC) AS RowNum " +
                    "    FROM " +
                    "        order_history oh " +
                    "    JOIN " +
                    "        order_transaction ot ON oh.order_transaction_id = ot.transaction_id " +
                    "    JOIN " +
                    "        transaction_meal tm ON ot.transaction_id = tm.transaction_id " +
                    "    JOIN " +
                    "        meal m ON tm.meal_id = m.meal_id " +
                    "    GROUP BY " +
                    "        Month_Name, m.meal_id " +
                    ") " +
                    "SELECT " +
                    "    Month_Name, " +
                    "    Most_Ordered_Meal, " +
                    "    Order_Count " +
                    "FROM " +
                    "    RankedMeals " +
                    "WHERE " +
                    "    RowNum = 1;";

            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    monthlistMMOM.add(rs.getString("Month_Name"));
                    meallistMMOM.add(rs.getString("Most_Ordered_Meal"));
                    ordercountMMOM.add(rs.getInt("Order_Count"));
                }
            }
            return 1;
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
            return 0;
        }
    }
    
    public int getYearlyMostOrderedMeals() {
    clearList();
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dining_db?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "password")) {
        String sql = "WITH RankedMeals AS ( " +
                "    SELECT " +
                "        YEAR(oh.date_ordered) AS Year, " +
                "        m.meal_name AS Most_Ordered_Meal, " +
                "        SUM(tm.quantity) AS Order_Count, " +
                "        ROW_NUMBER() OVER (PARTITION BY YEAR(oh.date_ordered) ORDER BY SUM(tm.quantity) DESC) AS RowNum " +
                "    FROM " +
                "        order_history oh " +
                "    JOIN " +
                "        order_transaction ot ON oh.order_transaction_id = ot.transaction_id " +
                "    JOIN " +
                "        transaction_meal tm ON ot.transaction_id = tm.transaction_id " +
                "    JOIN " +
                "        meal m ON tm.meal_id = m.meal_id " +
                "    GROUP BY " +
                "        Year, m.meal_id " +
                ") " +
                "SELECT " +
                "    Year, " +
                "    Most_Ordered_Meal, " +
                "    Order_Count " +
                "FROM " +
                "    RankedMeals " +
                "WHERE " +
                "    RowNum = 1;";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                yearlistYMOM.add(rs.getInt("Year"));
                meallistYMOM.add(rs.getString("Most_Ordered_Meal"));
                ordercountYMOM.add(rs.getInt("Order_Count"));
            }
        }
        return 1;
    } catch (SQLException e) {
        e.printStackTrace(); 
        return 0;
    }
}
    public order_history() {
    }

    public static void main(String[] args) {
        order_history oh = new order_history();
        oh.getMonthlyMostOrderedMeals();
        oh.getYearlyMostOrderedMeals();
    }
}
